-- Atualizar senha do admin para 'admin123' usando Spring Security BCrypt
UPDATE users 
SET password = '$2a$10$6hg/QTw8Th1g/8RHxfl1HO8xPyDNVr3L4GM2XsUqNYWMvXuxbuGqm',
    active = true,
    first_login = false,
    failed_login_attempts = 0
WHERE username = 'admin'; 