-- @email은 테스트 데이터용 이메일 포맷
-- generate_series 이용, 데이터 개수가 100개일 때, 1,000개일 때, 100,000개일 때

WITH new_users AS (
    INSERT INTO users (name, email, password, role, created_at)
    SELECT
        LEFT('user' || gs, 10) AS name,
        ('u' || gs || '@example.com') AS email,
        '{noop}1234',
        'MERCHANT',
        NOW() - (random() * 365 || ' days')::interval
    FROM generate_series(1, 100000) gs
    RETURNING id
)
INSERT INTO markets (user_id, name, address, created_at)
SELECT
    id,
    '가게 ' || id,
    '서울특별시 서울로 ' || id || '번길',
    NOW() - (random() * 365 || ' days')::interval
FROM new_users;

INSERT INTO users (name, email, password, role, created_at)
VALUES ('테스트유저', 'test@example.com', '{noop}1234', 'CUSTOMER', NOW());