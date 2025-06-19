-- Atualiza o usuário admin usando o ID
UPDATE users 
SET password = '123',
    active = true,
    failed_login_attempts = 0
WHERE id = 4; 