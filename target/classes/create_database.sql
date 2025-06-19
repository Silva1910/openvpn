-- Criar o banco de dados
CREATE DATABASE IF NOT EXISTS vpnpanel;
USE vpnpanel;

-- Criar tabela de usuários
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    active TINYINT(1) NOT NULL DEFAULT 1,
    admin TINYINT(1) NOT NULL DEFAULT 0,
    first_login TINYINT(1) NOT NULL DEFAULT 1,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME
);

-- Criar tabela de acessos VPN
CREATE TABLE vpn_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP NOT NULL,
    config_file_path VARCHAR(255) NOT NULL,
    config_path VARCHAR(255),
    created_at DATETIME NOT NULL,
    identifier VARCHAR(255) UNIQUE,
    common_name VARCHAR(255) NOT NULL UNIQUE,
    active TINYINT(1) NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Criar tabela de tokens de redefinição de senha
CREATE TABLE password_reset_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Criar tabela de configurações
CREATE TABLE settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vpn_server_address VARCHAR(255) NOT NULL,
    vpn_port INT NOT NULL,
    vpn_protocol VARCHAR(10) NOT NULL,
    smtp_server VARCHAR(255),
    smtp_port INT,
    smtp_username VARCHAR(255),
    smtp_password VARCHAR(255),
    smtp_use_tls TINYINT(1) DEFAULT 0,
    cert_path VARCHAR(255),
    key_path VARCHAR(255),
    ca_path VARCHAR(255),
    ssh_host VARCHAR(255),
    ssh_port INT,
    ssh_username VARCHAR(255),
    ssh_password VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inserir usuário administrador padrão
-- Username: admin
-- Senha: admin123 (MD5 hash)
INSERT INTO users (username, password, full_name, email, admin, first_login) 
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'Administrador', 'admin@example.com', 1, 0);

-- Inserir configurações padrão
INSERT INTO settings (
    vpn_server_address, 
    vpn_port, 
    vpn_protocol
) VALUES (
    'localhost',
    1194,
    'UDP'
); 