-- Primeiro, deletar o usuário duplicado
DELETE FROM users WHERE id = 4;

-- Agora atualizar o usuário principal
UPDATE users 
SET password = '123',
    active = true,
    failed_login_attempts = 0,
    first_login = false
WHERE id = 1; 