-- GIN 인덱스 임계점 확인용 대용량 더미 데이터 삽입 스크립트
-- 기존 500개에 추가로 단계별 삽입 (총 누적 규모로 측정)
-- 실행: psql -d <DB명> -U <유저명> -f scripts/dummy_data_large.sql
--
-- 측정 순서:
--   1. 이 스크립트 실행 후 EXPLAIN ANALYZE로 플랜 확인
--   2. Seq Scan → Bitmap Index Scan 또는 Index Scan으로 바뀌는 시점이 임계점

DO $$
DECLARE
    hashed_password TEXT := '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';
    market_keywords TEXT[] := ARRAY['치킨', '피자', '분식', '한식', '중식', '일식', '카페', '버거', '샐러드', '돈까스'];
    addr_districts  TEXT[] := ARRAY['강남구', '마포구', '송파구', '영등포구', '성동구', '용산구', '은평구', '노원구', '강서구', '관악구'];
    product_keywords TEXT[] := ARRAY['후라이드', '양념', '마르게리타', '떡볶이', '순대', '김밥', '냉면', '된장찌개', '삼겹살', '제육볶음'];
    start_idx INT;
    end_idx   INT;
BEGIN
    start_idx := 501;
    end_idx   := 10000; -- 기존 500 + 9,500 = 총 10,000

    -- 이메일 형식: u{i}@test.com (최대 14자, varchar(20) 이내)
    INSERT INTO users (email, password, name, role, created_at)
    SELECT
        'u' || i || '@test.com',
        hashed_password,
        '사장' || i,
        'MERCHANT',
        NOW() - (random() * INTERVAL '365 days')
    FROM generate_series(start_idx, end_idx) AS i
    ON CONFLICT (email) DO NOTHING;

    INSERT INTO markets (name, address, image_url, created_at, user_id)
    SELECT
        market_keywords[((i - 1) % 10) + 1] || ' ' || i || '호점',
        '서울 ' || addr_districts[((i - 1) % 10) + 1] || ' ' || i || '번길',
        NULL,
        NOW() - (random() * INTERVAL '365 days'),
        u.id
    FROM generate_series(start_idx, end_idx) AS i
    JOIN users u ON u.email = 'u' || i || '@test.com'
    ON CONFLICT DO NOTHING;

    INSERT INTO products (name, price, min_quantity, max_quantity, created_at, market_id)
    SELECT
        product_keywords[((j - 1) % 10) + 1] || ' ' || m.id,
        (FLOOR(random() * 20 + 1) * 1000)::NUMERIC,
        1,
        10,
        NOW() - (random() * INTERVAL '365 days'),
        m.id
    FROM markets m
    CROSS JOIN generate_series(1, 6) AS j
    WHERE m.id IN (
        SELECT id FROM markets ORDER BY id DESC LIMIT 9500
    );

END $$;

ANALYZE markets;
ANALYZE products;

SELECT
    (SELECT COUNT(*) FROM users   WHERE role = 'MERCHANT') AS merchants,
    (SELECT COUNT(*) FROM markets)                          AS markets,
    (SELECT COUNT(*) FROM products)                         AS products;
