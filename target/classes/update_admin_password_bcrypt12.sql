-- Atualizar senha do admin para 'admin123' usando BCrypt com força 12
UPDATE users 
SET password = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKXL3/C8QDr/Zaq',
    active = true,
    first_login = false,
    failed_login_attempts = 0
WHERE username = 'admin'; 