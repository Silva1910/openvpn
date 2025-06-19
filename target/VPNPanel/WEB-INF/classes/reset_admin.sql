UPDATE users 
SET password = '123',
    active = true,
    failed_login_attempts = 0
WHERE username = 'admin'; 