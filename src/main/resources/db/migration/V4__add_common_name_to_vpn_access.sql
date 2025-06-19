-- 1. Adicionar a coluna como nullable primeiro
ALTER TABLE vpn_access ADD COLUMN common_name VARCHAR(255);

-- 2. Popular todos os registros com o mesmo common_name
UPDATE vpn_access SET common_name = 'asouza-vpn';

-- 3. Tornar a coluna NOT NULL depois que os dados estiverem populados
ALTER TABLE vpn_access MODIFY COLUMN common_name VARCHAR(255) NOT NULL;

-- 4. Adicionar a constraint de unicidade
ALTER TABLE vpn_access ADD CONSTRAINT uk_vpn_access_common_name UNIQUE (common_name); 