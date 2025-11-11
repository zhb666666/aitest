-- 更新admin用户密码为 admin123
-- 如果登录失败，请执行此脚本更新密码

-- 方法1: 使用BCrypt哈希 (推荐)
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pQ5O' 
WHERE username = 'admin';

-- 如果上面的哈希值不工作，可以运行后端项目中的 PasswordGenerator 工具类生成新的哈希值
-- 或者使用以下方法2临时重置密码

-- 方法2: 如果数据库中没有数据，删除后重新插入
-- DELETE FROM user_roles WHERE user_id = 1;
-- DELETE FROM users WHERE username = 'admin';
-- INSERT INTO users (username, password, email, status) VALUES
-- ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pQ5O', 'admin@example.com', 1);
-- INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

