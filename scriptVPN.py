#!/usr/bin/python3

import sys
import os
import subprocess
import zipfile
import string
import random

def generate_unique_identifier(user_id):
    """
    Gera um identificador único de 7 caracteres baseado no ID do usuário.
    Formato: XXXXYYY onde X são dígitos do ID e Y são letras aleatórias
    """
    # Converte o ID para string e pega os primeiros 4 dígitos (preenche com 0 à esquerda)
    id_part = str(user_id).zfill(4)[:4]
    
    # Gera 3 letras aleatórias
    letters = string.ascii_uppercase
    suffix = ''.join(random.choices(letters, k=3))
    
    # Combina para formar o identificador único
    return id_part + suffix

def main():
    if len(sys.argv) < 4:
        print("Uso: sudo python3 scriptVPN.py <username> <password> <userId>")
        exit(1)

    username = sys.argv[1]
    password = sys.argv[2]
    user_id = sys.argv[3]
    
    # Gera identificador único baseado no ID do usuário
    identifier = generate_unique_identifier(user_id)
    
    # Usa o identificador como nome do cliente VPN
    nomeCliente = identifier
    
    destino = os.path.join("/etc/openvpn/client", nomeCliente)
    home_cliente = os.path.join("/home/usuario", nomeCliente)

    ipServidor = ""

    # Obter IP da interface enp0s3
    try:
        result = subprocess.run(
            ["ip", "-4", "addr", "show", "enp0s3"],
            capture_output=True,
            text=True,
            check=True
        )
        for line in result.stdout.splitlines():
            if "inet " in line:
                ipServidor = line.split()[1].split("/")[0]
                break
    except subprocess.CalledProcessError:
        print("Erro ao obter IP da interface enp0s3")
        exit(1)

    # Gerar certificado cliente
    try:
        subprocess.run(
            ["./easyrsa", "--batch", "gen-req", nomeCliente, "nopass"],
            cwd="/usr/share/easy-rsa",
            check=True
        )
    except subprocess.CalledProcessError:
        print("Erro ao gerar requisição de certificado (gen-req)")
        exit(1)

    # Assinar certificado
    env = os.environ.copy()
    env["EASYRSA_PASSIN"] = f"pass:{password}"

    try:
        subprocess.run(
            ["./easyrsa", "--batch", "sign-req", "client", nomeCliente],
            cwd="/usr/share/easy-rsa",
            env=env,
            check=True
        )
    except subprocess.CalledProcessError:
        print("Erro ao assinar o certificado (sign-req)")
        exit(1)

    # Criar diretório destino
    os.makedirs(destino, exist_ok=True)

    # Copiar arquivos necessários
    arquivos_para_copiar = {
        "/usr/share/easy-rsa/pki/ca.crt": "ca.crt",
        f"/usr/share/easy-rsa/pki/issued/{nomeCliente}.crt": f"{nomeCliente}.crt",
        f"/usr/share/easy-rsa/pki/private/{nomeCliente}.key": f"{nomeCliente}.key",
        "/usr/share/easy-rsa/pki/dh.pem": "dh.pem"
    }

    for origem, nome_arquivo in arquivos_para_copiar.items():
        try:
            subprocess.run(["cp", origem, os.path.join(destino, nome_arquivo)], check=True)
        except subprocess.CalledProcessError:
            print(f"Erro ao copiar {nome_arquivo}")
            exit(1)

    # Criar arquivo .ovpn
    ovpn_path = os.path.join(destino, f"{nomeCliente}.ovpn")
    ovpn_conteudo = f"""client
dev tun
proto udp
remote {ipServidor} 1194
ca ca.crt
cert {nomeCliente}.crt
key {nomeCliente}.key
tls-client
resolv-retry infinite
nobind
persist-key
persist-tun
"""

    try:
        with open(ovpn_path, "w") as f:
            f.write(ovpn_conteudo)
    except Exception as e:
        print("Erro ao criar o arquivo .ovpn:", e)
        exit(1)

    # Copiar pasta para home do usuário
    try:
        subprocess.run(["cp", "-r", destino, home_cliente], check=True)
    except subprocess.CalledProcessError:
        print("Erro ao copiar diretório para /home/usuario/")
        exit(1)

    # Copiar .ovpn também para home
    try:
        subprocess.run(["cp", ovpn_path, home_cliente], check=True)
    except subprocess.CalledProcessError:
        print("Erro ao copiar .ovpn para /home/usuario/")
        exit(1)

    # Ajustar permissões
    try:
        subprocess.run(["chown", "-R", "usuario:usuario", home_cliente], check=True)
    except subprocess.CalledProcessError:
        print("Erro ao aplicar chown")
        exit(1)

    # Compactar em ZIP
    zip_path = f"/home/usuario/{nomeCliente}.zip"
    with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(home_cliente):
            for file in files:
                caminho_completo = os.path.join(root, file)
                caminho_relativo = os.path.relpath(caminho_completo, home_cliente)
                zipf.write(caminho_completo, caminho_relativo)

    print(f"\n✅ Cliente VPN '{nomeCliente}' gerado com sucesso!")
    print(f"👤 Usuário: {username}")
    print(f"🔑 Identificador único: {identifier}")
    print(f"📦 Arquivo ZIP final: {zip_path}")

if __name__ == "__main__":
    main() 