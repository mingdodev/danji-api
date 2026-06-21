-- 가게 검색 성능 개선: pg_trgm GIN 인덱스 추가
-- 실행: psql -d <DB명> -U <유저명> -f scripts/add_search_indexes.sql

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_markets_name_trgm    ON markets  USING GIN (name    gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_markets_address_trgm ON markets  USING GIN (address gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_products_name_trgm   ON products USING GIN (name    gin_trgm_ops);
