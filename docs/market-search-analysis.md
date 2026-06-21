# 가게 목록 조회 성능 분석

## 배경

캐싱 레이어를 제거한 뒤, 데이터베이스 자체에서 병목을 찾아 개선하는 것을 목표로 삼았다.
추측으로 인덱스를 추가하는 대신, `EXPLAIN ANALYZE`로 실행 계획을 먼저 측정했다.

더미 데이터: 가게 500개, 상품 3,000개 (가게당 6개)

---

## EXPLAIN ANALYZE 결과

### 전체 조회 (키워드 없음)

```sql
SELECT * FROM markets ORDER BY id LIMIT 10 OFFSET 0;
```

```
Limit  (actual time=0.025..0.029 rows=10)
  ->  Index Scan using markets_pkey on markets  (actual time=0.023..0.026 rows=10)
Execution Time: 0.079 ms
```

PK 인덱스를 타고 LIMIT만큼만 읽는다. **개선 필요 없음.**

---

### 키워드 검색

```sql
SELECT DISTINCT m.* FROM markets m
LEFT JOIN products p ON p.market_id = m.id
WHERE m.name LIKE '%치킨%'
   OR m.address LIKE '%치킨%'
   OR p.name LIKE '%치킨%'
LIMIT 10;
```

```
Limit  (actual time=2.492..2.501 rows=10)
  ->  HashAggregate [DISTINCT]  (actual time=2.490..2.497 rows=10)
        ->  Hash Right Join  (actual time=0.272..2.323 rows=300)
              Filter: LIKE 조건 3개
              Rows Removed by Filter: 2700
              ->  Seq Scan on products  (actual time=0.007..0.284 rows=3000)
              ->  Seq Scan on markets   (actual time=0.016..0.076 rows=500)
Execution Time: 2.611 ms
```

전체 조회 대비 **33배 느림.** 데이터가 늘어날수록 선형으로 비용이 증가하는 구조다.

---

## 문제 분석

### 1. 불필요한 중간 데이터 (JOIN → Filter → DISTINCT)

현재 쿼리는 markets와 products를 먼저 JOIN해서 3,000행짜리 중간 결과를 만든 뒤, LIKE 필터링 후 DISTINCT로 다시 줄인다.

- JOIN 결과: 3,000행
- 필터 후: 300행 (2,700행 제거)
- DISTINCT 후: 최종 결과

`EXISTS` 서브쿼리로 재작성하면 "상품명에 키워드가 있는 가게가 존재하는가?"를 가게 단위로 확인하므로, JOIN 중간 결과 자체가 생기지 않는다.

### 2. 풀 스캔 — 인덱스가 있어도 B-tree는 무효

`%키워드%` 형태의 앞 와일드카드 검색은 B-tree 인덱스를 탈 수 없다.

B-tree는 정렬된 트리 구조로, `name LIKE '치킨%'`처럼 시작점이 정해진 경우엔 해당 범위만 읽을 수 있다. 그런데 `LIKE '%치킨%'`는 문자열 어디서든 나타날 수 있어 시작점을 특정할 수 없다. 인덱스가 있어도 어디서부터 읽어야 할지 알 수 없으니 결국 전체를 스캔하게 된다.

---

## 개선 방향

### pg_trgm GIN 인덱스

`pg_trgm`은 문자열을 3글자(trigram) 단위로 쪼개 역인덱스(inverted index)를 구성한다.

```
"치킨집" → {" 치", "치킨", "킨집", "집 "}
```

GIN 내부에서 trigram 사전(dictionary) 자체는 B-tree로 관리한다. 검색 시 흐름은 다음과 같다.

1. `LIKE '%치킨%'` → 패턴에서 trigram `"치킨"` 추출
2. trigram 사전에서 `"치킨"` 찾기 (B-tree, O(log unique_trigrams))
3. `"치킨"`을 포함하는 row_id 목록 조회 (posting list)
4. 해당 행에만 실제 LIKE 조건 재검증 (recheck)

row 수 N에 비해 unique trigram 수는 훨씬 작아 탐색 범위 자체가 압도적으로 줄어든다.

#### 왜 Full-text search가 아닌가

| 방식 | `%keyword%` 지원 | 특징 |
|------|-----------------|------|
| B-tree | X | 전방 일치만 가능 |
| pg_trgm + GIN | O | 언어 무관, 부분 문자열 검색에 최적 |
| tsvector (Full-text search) | 부분적 | 형태소 분석 기반, 한국어는 별도 파서 필요 |

Full-text search는 한국어 형태소 분석기(`pg_korean` 등)를 별도로 설치해야 하고 설정이 복잡하다. `pg_trgm`은 언어 무관하게 글자 단위로 동작하므로 한국어 부분 문자열 검색에 바로 쓸 수 있다.

#### 트레이드오프

**저장 공간**: GIN은 한 행이 여러 trigram을 생성하므로 B-tree보다 인덱스가 크다. 다만 대상 컬럼이 짧고(`name` 30자, `address` 30자, `products.name` 20자) trigram 수가 제한적이라 수만 건 규모에서도 수십 MB 수준으로 감당 가능하다.

**쓰기 성능**: 여러 trigram posting list를 업데이트해야 해서 B-tree보다 쓰기가 느리다. 단 PostgreSQL의 `fastupdate` 모드(기본 ON)가 변경사항을 pending list에 쌓고 일괄 병합해 개별 쓰기 부담을 완화한다. 가게·상품 등록은 조회에 비해 빈도가 매우 낮은 도메인이라 실질적 영향은 작다.

---

## 개선 계획

1. 검색 쿼리를 `EXISTS` 서브쿼리 방식으로 재작성 (불필요한 중간 데이터 제거)
2. `pg_trgm` 확장 활성화 및 GIN 인덱스 추가
   - `markets.name`
   - `markets.address`
   - `products.name`
3. `EXPLAIN ANALYZE` 재측정 및 전후 비교
