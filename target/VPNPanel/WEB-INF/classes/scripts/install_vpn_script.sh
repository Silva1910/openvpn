#!/bin/bash

# Copiar o script para /usr/local/bin
sudo cp scriptVPN.py /usr/local/bin/
sudo chmod +x /usr/local/bin/scriptVPN.py

# Criar diretório para configurações OpenVPN se não existir
sudo mkdir -p /etc/openvpn/client-configs

# Definir permissões corretas
sudo chown -R root:root /usr/local/bin/scriptVPN.py
sudo chmod 755 /usr/local/bin/scriptVPN.py
sudo chown -R root:root /etc/openvpn/client-configs
sudo chmod 755 /etc/openvpn/client-configs

echo "Script VPN instalado com sucesso!" 