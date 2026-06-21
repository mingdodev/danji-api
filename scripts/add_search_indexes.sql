-- 가게 검색 성능 개선: pg_bigm GIN 인덱스 추가
-- lc_ctype=C 환경에서도 한국어 LIKE 검색을 지원하기 위해 pg_trgm 대신 pg_bigm 사용
-- 실행: psql -d <DB명> -U <유저명> -f scripts/add_search_indexes.sql

CREATE EXTENSION IF NOT EXISTS pg_bigm;

CREATE INDEX IF NOT EXISTS idx_markets_name_bigm    ON markets  USING GIN (name    gin_bigm_ops);
CREATE INDEX IF NOT EXISTS idx_markets_address_bigm ON markets  USING GIN (address gin_bigm_ops);
CREATE INDEX IF NOT EXISTS idx_products_name_bigm   ON products USING GIN (name    gin_bigm_ops);
