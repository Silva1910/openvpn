#!/usr/bin/python3
# -*- coding: utf-8 -*-

import sys
import os
import subprocess

if len(sys.argv) < 2:
    print("Uso: sudo python3 revogar_certificado.py <nome_zip>")
    print("Exemplo: sudo python3 revogar_certificado.py 1asgdxg.zip")
    exit(1)

zip_filename = sys.argv[1]

if not zip_filename.endswith(".zip"):
    print("Erro: forneca um arquivo .zip valido")
    exit(1)

# Extrai o CN do nome do zip (sem .zip)
cn = os.path.splitext(os.path.basename(zip_filename))[0]

# Caminho da Easy-RSA
easy_rsa_path = "/usr/share/easy-rsa"

# Senha da CA
ca_pass = "123456"  # Mude aqui se necessario

# Ambiente com senha embutida para revogacao
env = os.environ.copy()
env["EASYRSA_PASSIN"] = f"pass:{ca_pass}"

# Revoke
try:
    subprocess.run(
        ["./easyrsa", "--batch", "revoke", cn],
        cwd=easy_rsa_path,
        env=env,
        check=True
    )
    print(f"[OK] Certificado '{cn}' revogado.")
except subprocess.CalledProcessError:
    print(f"[ERRO] Falha ao revogar '{cn}'")
    exit(1)

# Gera nova CRL
try:
    subprocess.run(
        ["./easyrsa", "--batch", "gen-crl"],
        cwd=easy_rsa_path,
        env=env,
        check=True
    )
    print("[OK] Lista CRL gerada.")
except subprocess.CalledProcessError:
    print("[ERRO] Falha ao gerar CRL")
    exit(1)

# Copia CRL para o servidor
try:
    subprocess.run(
        ["cp", os.path.join(easy_rsa_path, "pki/crl.pem"), "/etc/openvpn/server/crl.pem"],
        check=True
    )
    print("[OK] crl.pem atualizado no servidor OpenVPN.")
except subprocess.CalledProcessError:
    print("[ERRO] Falha ao copiar crl.pem")
    exit(1)

# Remove o arquivo zip apos a revogacao
try:
    os.remove(zip_filename)
    print(f"[OK] Arquivo {zip_filename} removido com sucesso.")
except OSError as e:
    print(f"[ERRO] Nao foi possivel remover o arquivo {zip_filename}: {e.strerror}")

print("\n✅ Revogacao concluida com sucesso.") 