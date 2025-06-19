-- Atualizar senha do admin para 'admin123' com hash BCrypt válido
UPDATE users 
SET password = '$2a$10$vG7UJXVCGrQrur0OH0FKxOYTqn5PoFK4H6AV8Hd6gYxR4qYGJZNWi',
    active = true,
    first_login = false,
    failed_login_attempts = 0
WHERE username = 'admin'; 