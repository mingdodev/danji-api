-- 가게 목록 조회 성능 측정용 더미 데이터 삽입 스크립트
-- 실행 전 스키마 확인: \dt
-- 실행: psql -d <DB명> -U <유저명> -f scripts/dummy_data.sql

DO $$
DECLARE
    hashed_password TEXT := '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'; -- 평문: password
    market_keywords TEXT[] := ARRAY['치킨', '피자', '분식', '한식', '중식', '일식', '카페', '버거', '샐러드', '돈까스'];
    addr_districts TEXT[] := ARRAY['강남구', '마포구', '송파구', '영등포구', '성동구', '용산구', '은평구', '노원구', '강서구', '관악구'];
    product_keywords TEXT[] := ARRAY['후라이드', '양념', '마르게리타', '떡볶이', '순대', '김밥', '냉면', '된장찌개', '삼겹살', '제육볶음'];
BEGIN

    -- 1. MERCHANT 유저 500명
    INSERT INTO users (email, password, name, role, created_at)
    SELECT
        'merchant' || i || '@test.com',
        hashed_password,
        '사장님' || i,
        'MERCHANT',
        NOW() - (random() * INTERVAL '365 days')
    FROM generate_series(1, 500) AS i
    ON CONFLICT (email) DO NOTHING;

    -- 2. 가게 500개 (유저당 1개)
    INSERT INTO markets (name, address, image_url, created_at, user_id)
    SELECT
        market_keywords[((i - 1) % 10) + 1] || ' ' || i || '호점',
        '서울 ' || addr_districts[((i - 1) % 10) + 1] || ' ' || i || '번길',
        NULL,
        NOW() - (random() * INTERVAL '365 days'),
        u.id
    FROM generate_series(1, 500) AS i
    JOIN users u ON u.email = 'merchant' || i || '@test.com'
    ON CONFLICT DO NOTHING;

    -- 3. 상품 삽입 (가게당 6개, 총 3000개)
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
    WHERE m.id IN (SELECT id FROM markets ORDER BY id DESC LIMIT 500);

END $$;

SELECT
    (SELECT COUNT(*) FROM users   WHERE role = 'MERCHANT') AS merchants,
    (SELECT COUNT(*) FROM markets)                          AS markets,
    (SELECT COUNT(*) FROM products)                         AS products;
