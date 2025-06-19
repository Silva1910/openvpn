-- Adiciona valor padrão para a coluna failedLoginAttempts
ALTER TABLE users MODIFY COLUMN failed_login_attempts INT NOT NULL DEFAULT 0; 