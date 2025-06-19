-- Gerar um novo hash para a senha 'admin123'
UPDATE users 
SET password = '$2a$10$xVfq7.yzX8Ot3V3ywCAKxOYKJPH.ZB7qYoF8.WoXWAzHLJLUYt.Hy',
    active = true,
    first_login = false,
    failed_login_attempts = 0
WHERE username = 'admin'; 