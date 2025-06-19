#!/usr/bin/python3
# -*- coding: utf-8 -*-

import sys
import os
import subprocess
import zipfile
import random
import string
from pathlib import Path

if len(sys.argv) < 4:
    print("Uso: python3 scriptVPN.py <nomeCliente> <senha> <id>")
    exit(1)

nomeCliente = sys.argv[1]
password = sys.argv[2]
id_prefixo = sys.argv[3]

if not id_prefixo.isalnum():
    print("ID precisa ser alfanumerico.")
    exit(1)

def gerar_nome_zip(id_base):
    restante = 7 - len(id_base)
    sufixo = ''.join(random.choices(string.ascii_lowercase + string.digits, k=restante))
    return id_base + sufixo

nome_zip = gerar_nome_zip(id_prefixo)

# Usar diretórios no home do usuário em vez de /etc/openvpn
HOME = str(Path.home())
CONFIG_DIR = os.path.join(HOME, '.vpnconfigs')
CLIENT_DIR = os.path.join(CONFIG_DIR, nomeCliente)
OUTPUT_DIR = os.path.join(HOME, 'vpn_output')

# Criar diretórios necessários
os.makedirs(CONFIG_DIR, exist_ok=True)
os.makedirs(CLIENT_DIR, exist_ok=True)
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Gerar IP dinâmico (exemplo: 10.8.x.x)
ip_part3 = random.randint(0, 255)
ip_part4 = random.randint(2, 254)
ip_address = f"10.8.{ip_part3}.{ip_part4}"

# Criar arquivo de configuração OpenVPN
ovpn_path = os.path.join(CLIENT_DIR, f"{nomeCliente}.ovpn")
ovpn_conteudo = f"""client
dev tun
proto udp
remote {ip_address} 1194
resolv-retry infinite
nobind
persist-key
persist-tun
remote-cert-tls server
cipher AES-256-CBC
verb 3
auth-user-pass

# Configurações do cliente
ifconfig-push {ip_address} 255.255.255.0
"""

try:
    with open(ovpn_path, "w") as f:
        f.write(ovpn_conteudo)
except Exception as e:
    print(f"Erro ao criar arquivo .ovpn: {e}")
    exit(1)

# Criar arquivos de exemplo para certificados e chaves
# Em produção, você deve ter um processo seguro para gerar estes arquivos
exemplo_arquivos = {
    'ca.crt': '# Exemplo de CA certificate\n',
    f'{nomeCliente}.crt': f'# Certificado para {nomeCliente}\n',
    f'{nomeCliente}.key': f'# Chave privada para {nomeCliente}\n',
}

for nome_arquivo, conteudo in exemplo_arquivos.items():
    caminho = os.path.join(CLIENT_DIR, nome_arquivo)
    try:
        with open(caminho, 'w') as f:
            f.write(conteudo)
    except Exception as e:
        print(f"Erro ao criar {nome_arquivo}: {e}")
        exit(1)

# Criar arquivo ZIP
zip_path = os.path.join(OUTPUT_DIR, f"{nome_zip}.zip")
try:
    with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(CLIENT_DIR):
            for file in files:
                caminho_completo = os.path.join(root, file)
                caminho_relativo = os.path.relpath(caminho_completo, CLIENT_DIR)
                zipf.write(caminho_completo, caminho_relativo)
except Exception as e:
    print(f"Erro ao criar arquivo ZIP: {e}")
    exit(1)

print(f"\nCliente VPN '{nomeCliente}' criado com sucesso!")
print(f"Arquivo ZIP final: {zip_path}") 