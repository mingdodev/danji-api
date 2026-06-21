-- 테스트 데이터 삭제 쿼리

BEGIN;

DELETE FROM markets
WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%@example.com');

DELETE FROM users
WHERE email LIKE '%@example.com';

COMMIT;