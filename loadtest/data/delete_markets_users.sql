-- 테스트 데이터 삭제 쿼리

DELETE FROM markets
WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%@email.com');

DELETE FROM users
WHERE email LIKE '%@email.com';