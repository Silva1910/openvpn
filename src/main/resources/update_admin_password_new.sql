-- Atualizar senha do admin para 'admin123' com novo hash BCrypt
UPDATE users 
SET password = '$2a$10$n9JxYXMm3ZHmhHO.gFJZwu/h3riNF9.WlsZtxvzG.YD1LGbmWufZi',
    active = true,
    first_login = false,
    failed_login_attempts = 0
WHERE username = 'admin'; 