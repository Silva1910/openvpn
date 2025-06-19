-- Atualizar senha do admin para 'admin123' usando MD5
UPDATE users 
SET password = '0192023a7bbd73250516f069df18b500',
    active = true,
    first_login = false,
    failed_login_attempts = 0
WHERE username = 'admin'; 