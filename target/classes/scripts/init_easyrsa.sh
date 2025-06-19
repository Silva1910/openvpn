#!/bin/bash

# Ir para o diretório do Easy-RSA
cd /usr/share/easy-rsa

# Limpar qualquer configuração anterior
./easyrsa clean-all

# Inicializar o PKI
./easyrsa init-pki

# Copiar o arquivo vars para o diretório PKI
cp vars.example pki/vars

# Gerar o certificado CA (Certificate Authority)
# Usando a senha 123456 para o CA
echo "123456" | ./easyrsa build-ca nopass

# Gerar o certificado do servidor
./easyrsa build-server-full server nopass

# Gerar os parâmetros Diffie-Hellman
./easyrsa gen-dh

# Ajustar permissões
chown -R root:root pki/
chmod -R 755 pki/
chmod 700 pki/private
chmod 600 pki/private/ca.key

echo "Easy-RSA inicializado com sucesso!" 