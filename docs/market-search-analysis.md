# 가게 목록 조회 성능 분석

## 배경

캐싱 레이어를 제거한 뒤, 데이터베이스 자체에서 병목을 찾아 개선하는 것을 목표로 삼았다.
추측으로 인덱스를 추가하는 대신, `EXPLAIN ANALYZE`로 실행 계획을 먼저 측정했다.

더미 데이터: 가게 10,000개, 상품 60,000개 (가게당 6개)

## 베이스라인 측정

### 전체 조회 (키워드 없음)

```sql
SELECT * FROM markets ORDER BY id LIMIT 10 OFFSET 0;
```

```
Index Scan using markets_pkey on markets
Execution Time: 0.079 ms
```

PK 인덱스를 타고 LIMIT만큼만 읽는다. **개선 필요 없음.**

---

### 키워드 검색 (JOIN + DISTINCT 구조)

```sql
SELECT DISTINCT m.* FROM markets m
LEFT JOIN products p ON p.market_id = m.id
WHERE m.name LIKE '%치킨%'
   OR m.address LIKE '%치킨%'
   OR p.name LIKE '%치킨%'
LIMIT 10;
```

```
Limit  (actual time=53.491..53.504 rows=10)
  Unique
    Sort  Sort Method: quicksort  Memory: 1650kB
      Hash Left Join  (actual time=25.731..49.381 rows=9000)
        Rows Removed by Filter: 51000
        Seq Scan on products   rows=60000  Memory: 3592kB
        Seq Scan on markets    rows=10000
Execution Time: 53.790 ms
```

데이터 규모가 커지자 HashAggregate 대신 Sort+Unique 전략을 선택했다. 정렬에 1,650kB, 해시에 3,592kB를 사용하며 **전체 조회 대비 680배 느림.** 데이터가 늘수록 선형으로 비용이 증가하는 구조다.

---

## 문제 분석

### 1. 불필요한 중간 데이터 (JOIN → Filter → DISTINCT)

markets × products JOIN으로 3,000행 중간 결과를 만든 뒤, LIKE 필터링 후 DISTINCT로 다시 줄인다.

- JOIN 결과: 3,000행
- 필터 통과: 300행
- DISTINCT 후: 최종 결과

### 2. 풀 스캔 — B-tree 인덱스 무효

`%keyword%` 앞 와일드카드 검색은 B-tree 인덱스를 탈 수 없다. 시작점이 어디인지 알 수 없어 트리를 내려갈 수가 없기 때문이다. 별도의 확장이 필요하다.

---

## 개선 1단계 — EXISTS 서브쿼리 재작성

```sql
SELECT m.* FROM markets m
WHERE m.name LIKE '%치킨%'
   OR m.address LIKE '%치킨%'
   OR EXISTS (
       SELECT 1 FROM products p
       WHERE p.market_id = m.id
       AND p.name LIKE '%치킨%'
   )
LIMIT 10;
```

```
Limit  (actual time=0.021..19.297 rows=10)
  Seq Scan on markets m
    hashed SubPlan 2 (loops=1)
      Seq Scan on products p  rows=9000 / 60000
Execution Time: 19.347 ms
```

| 항목 | 이전 (JOIN + DISTINCT) | 이후 (EXISTS) |
|------|----------------------|---------------|
| 실행 시간 | 53.790ms | **19.347ms** |
| 중간 처리 행 | 60,000행 (JOIN 결과) | 없음 |
| DISTINCT 비용 | Sort+Unique 1,650kB | **없음** |
| products 스캔 | 60,000행 해시 후 필터 | hashed SubPlan **1회** |

Sort+Unique가 제거되고, PostgreSQL이 EXISTS를 hashed SubPlan으로 최적화해 products를 1회만 스캔한다. markets는 조기 종료로 28행만 스캔 후 LIMIT 10 도달.

---

## 개선 2단계 — GIN 인덱스 도입

### 왜 pg_trgm이 아닌 pg_bigm인가

`%keyword%` LIKE 검색에는 trigram/bigram 기반 GIN 인덱스가 필요하다.
처음엔 PostgreSQL 내장 `pg_trgm`을 시도했으나, 데이터베이스의 `lc_ctype=C` 설정으로 인해 한국어 문자에서 trigram 추출 자체가 불가능했다.
`pg_bigm`은 로케일과 무관하게 동작하며 한국어를 포함한 멀티바이트 문자를 올바르게 처리한다.

### 인덱스 구성

```sql
CREATE EXTENSION pg_bigm;
CREATE INDEX idx_markets_name_bigm    ON markets  USING GIN (name    gin_bigm_ops);
CREATE INDEX idx_markets_address_bigm ON markets  USING GIN (address gin_bigm_ops);
CREATE INDEX idx_products_name_bigm   ON products USING GIN (name    gin_bigm_ops);
```

### 측정 결과 — products 단독

```
Bitmap Index Scan on idx_products_name_bigm → rows=1,000 (정확, false positive 없음)
Execution Time: 1.630 ms
```

### 측정 결과 — 전체 검색 쿼리

```
Limit  (actual time=3.016..3.047 rows=10)
  Seq Scan on markets m
    hashed SubPlan 2
      Bitmap Index Scan on idx_products_name_bigm → rows=1,000
Execution Time: 3.207 ms
```

products SubPlan이 60,000행 전체 스캔에서 **1,000행 정확 반환**으로 개선됐다.

---

## UNION 구조는 어떨까?

markets name/address도 인덱스를 활용하게 하려면 OR를 UNION으로 분리하는 방법을 고려할 수 있다.
OR 조건에 인덱스를 탈 수 없는 서브쿼리가 섞이면 PostgreSQL이 markets 전체에 Seq Scan을 선택하기 때문이다.

```sql
SELECT * FROM markets WHERE name LIKE '%마르게리타%'
UNION
SELECT * FROM markets WHERE address LIKE '%마르게리타%'
UNION
SELECT m.* FROM markets m
WHERE EXISTS (
    SELECT 1 FROM products p
    WHERE p.market_id = m.id AND p.name LIKE '%마르게리타%'
);
```

```
HashAggregate (DISTINCT)  Memory Usage: 329kB
  Append
    Bitmap Index Scan on idx_markets_name_bigm    rows=0   (0.299ms)
    Bitmap Index Scan on idx_markets_address_bigm rows=0   (0.297ms)
    Hash Semi Join — Seq Scan on markets (10,000행) + Bitmap Index Scan on idx_products_name_bigm
Execution Time: 25.666 ms
```

세 인덱스를 모두 타지만 **25ms로 EXISTS(3~6ms)보다 오히려 느리다.**

UNION은 세 브랜치의 결과를 전부 모아 HashAggregate로 중복 제거한 뒤 LIMIT을 적용하므로, 조건을 만족하는 markets 1,000개를 전부 처리해야 한다.
반면 EXISTS는 products 해시셋을 만든 뒤 markets를 순서대로 스캔하다 10개를 찾으면 즉시 멈춘다 (92행만 스캔).
**LIMIT이 있는 페이지네이션 구조에서는 EXISTS의 조기 종료가 훨씬 유리하다.**

---

## 최종 결론

측정 기준: 가게 10,000개 / 상품 60,000개

| 버전 | 실행 시간 | 개선율 |
|------|----------|--------|
| JOIN + DISTINCT (최초) | 53.790ms | — |
| EXISTS 재작성 | 19.347ms | 2.8x |
| EXISTS + pg_bigm | **3~6ms** | **9~18x** |
| UNION + pg_bigm | 25ms | — |

**채택: EXISTS 서브쿼리 + pg_bigm GIN 인덱스**

- JOIN 제거로 불필요한 중간 행 생성 차단
- pg_bigm GIN 인덱스로 products 검색을 60,000 → 1,000행으로 압축
- 페이지네이션 구조에서 LIMIT 조기 종료 효과 극대화

한국어 LIKE 검색의 근본적인 성능 개선은 DB 수준에서 도달 가능한 한계가 있다. 더 나아가려면 Elasticsearch 같은 전문 검색 엔진 도입을 고려해야 한다.
