-- Adicionar colunas SSH na tabela settings
ALTER TABLE settings
ADD COLUMN ssh_host VARCHAR(255),
ADD COLUMN ssh_port INT,
ADD COLUMN ssh_username VARCHAR(255),
ADD COLUMN ssh_password VARCHAR(255);

-- Atualizar com as configurações SSH fornecidas
UPDATE settings
SET ssh_host = '192.168.0.29',
    ssh_port = 22,
    ssh_username = 'usuario',
    ssh_password = '123456'
WHERE id = 1; 