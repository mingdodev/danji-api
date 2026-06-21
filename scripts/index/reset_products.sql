-- 더 현실적인 상품명으로 products 재삽입
-- 가게 유형별 메뉴로 구성해 키워드 검색 테스트가 가능하도록 함
-- 실행: psql -d <DB명> -U <유저명> -f scripts/reset_products.sql

TRUNCATE TABLE order_item, products;

DO $$
DECLARE
    chicken_menus  TEXT[] := ARRAY['후라이드치킨', '양념치킨', '치킨버거', '치킨샐러드', '치킨피자', '치킨덮밥'];
    pizza_menus    TEXT[] := ARRAY['마르게리타피자', '페퍼로니피자', '불고기피자', '치즈피자', '콤비피자', '포테이토피자'];
    bunsik_menus   TEXT[] := ARRAY['떡볶이', '순대볶음', '김밥', '라면', '튀김', '어묵탕'];
    hansik_menus   TEXT[] := ARRAY['된장찌개', '김치찌개', '삼겹살', '제육볶음', '갈비탕', '비빔밥'];
    chinese_menus  TEXT[] := ARRAY['짜장면', '짬뽕', '탕수육', '볶음밥', '마파두부', '깐풍기'];
    japanese_menus TEXT[] := ARRAY['초밥', '라멘', '돈카츠', '우동', '텐동', '규동'];
    cafe_menus     TEXT[] := ARRAY['아메리카노', '카페라떼', '케이크', '마카롱', '샌드위치', '와플'];
    burger_menus   TEXT[] := ARRAY['치킨버거', '새우버거', '불고기버거', '치즈버거', '더블버거', '베이컨버거'];
    salad_menus    TEXT[] := ARRAY['치킨샐러드', '연어샐러드', '그릭샐러드', '시저샐러드', '과일샐러드', '두부샐러드'];
    tonkatsu_menus TEXT[] := ARRAY['등심돈까스', '치즈돈까스', '치킨까스', '새우까스', '왕돈까스', '카레돈까스'];

    m           RECORD;
    market_type INT;
    menu_name   TEXT;
BEGIN
    FOR m IN SELECT id FROM markets ORDER BY id LOOP
        market_type := (m.id - 1) % 10;

        FOR j IN 1..6 LOOP
            menu_name := CASE market_type
                WHEN 0 THEN chicken_menus[j]
                WHEN 1 THEN pizza_menus[j]
                WHEN 2 THEN bunsik_menus[j]
                WHEN 3 THEN hansik_menus[j]
                WHEN 4 THEN chinese_menus[j]
                WHEN 5 THEN japanese_menus[j]
                WHEN 6 THEN cafe_menus[j]
                WHEN 7 THEN burger_menus[j]
                WHEN 8 THEN salad_menus[j]
                WHEN 9 THEN tonkatsu_menus[j]
            END;

            INSERT INTO products (name, price, min_quantity, max_quantity, created_at, market_id)
            VALUES (
                menu_name,
                (FLOOR(random() * 20 + 1) * 1000)::NUMERIC,
                1,
                10,
                NOW() - (random() * INTERVAL '365 days'),
                m.id
            );
        END LOOP;
    END LOOP;
END $$;

ANALYZE products;

SELECT COUNT(*) AS products FROM products;
