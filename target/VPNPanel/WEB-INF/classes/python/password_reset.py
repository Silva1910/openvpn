import pymysql
import smtplib
import string
import random
import sys
import logging
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Configurações do banco de dados
DB_CONFIG = {
    'host': '192.168.0.155',
    'user': 'vpnpanel_user',
    'password': 'securepassword123',
    'database': 'vpnpanel',
    'cursorclass': pymysql.cursors.DictCursor
}

# Configurações do email do sistema
EMAIL_CONFIG = {
    'smtp_server': 'smtp.gmail.com',
    'smtp_port': 587,
    'username': 'vpnpanel.sistema@gmail.com',
    'password': 'qikz zxdd ecjz rufs'
}

def generate_temp_password(length=8):
    """Gera uma senha temporária aleatória."""
    chars = string.ascii_letters + string.digits
    return ''.join(random.choice(chars) for _ in range(length))

def get_user_email(username):
    """Busca o email do usuário no banco de dados."""
    try:
        logger.info(f"Buscando informações do usuário: {username}")
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        cursor.execute("SELECT id, email, username FROM users WHERE username = %s AND active = 1", (username,))
        user = cursor.fetchone()
        
        cursor.close()
        conn.close()
        
        if user:
            logger.info(f"Usuário encontrado: {user['email']}")
        else:
            logger.warning(f"Usuário não encontrado ou inativo: {username}")
        
        return user
    except Exception as e:
        logger.error(f"Erro ao buscar usuário: {e}")
        return None

def send_temp_password_email(email, username, temp_password):
    """Envia o email com a senha temporária."""
    try:
        logger.info(f"Iniciando envio de email para: {email}")
        
        msg = MIMEMultipart()
        msg['From'] = f"VPN Panel <{EMAIL_CONFIG['username']}>"
        msg['To'] = email
        msg['Subject'] = 'Redefinição de Senha - VPN Panel'
        
        body = f"""Olá {username},

Uma nova senha temporária foi gerada para sua conta no VPN Panel.

Sua senha temporária é: {temp_password}

Use esta senha para fazer login. Você será solicitado a criar uma nova senha.

Se você não solicitou esta redefinição de senha, por favor, entre em contato com o administrador.

Atenciosamente,
Equipe VPN Panel"""
        
        msg.attach(MIMEText(body, 'plain'))
        
        logger.info("Conectando ao servidor SMTP...")
        server = smtplib.SMTP(EMAIL_CONFIG['smtp_server'], EMAIL_CONFIG['smtp_port'])
        server.set_debuglevel(1)  # Ativa debug
        
        logger.info("Iniciando TLS...")
        server.starttls()
        
        logger.info("Realizando login SMTP...")
        server.login(EMAIL_CONFIG['username'], EMAIL_CONFIG['password'])
        
        logger.info("Enviando email...")
        server.send_message(msg)
        
        logger.info("Fechando conexão SMTP...")
        server.quit()
        
        logger.info("Email enviado com sucesso!")
        return True
    except Exception as e:
        logger.error(f"Erro ao enviar email: {str(e)}")
        return False

def update_user_password(user_id, temp_password):
    """Atualiza a senha do usuário no banco de dados."""
    try:
        logger.info(f"Atualizando senha para usuário ID: {user_id}")
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # Hash MD5 da senha temporária
        import hashlib
        hashed_password = hashlib.md5(temp_password.encode()).hexdigest()
        
        cursor.execute("UPDATE users SET password = %s WHERE id = %s", (hashed_password, user_id))
        conn.commit()
        
        cursor.close()
        conn.close()
        
        logger.info("Senha atualizada com sucesso!")
        return True
    except Exception as e:
        logger.error(f"Erro ao atualizar senha: {e}")
        return False

def reset_password(username):
    """Processo completo de redefinição de senha."""
    try:
        user = get_user_email(username)
        if not user:
            logger.warning("Usuário não encontrado ou inativo")
            return {"success": False, "message": "Usuário não encontrado ou inativo"}
        
        temp_password = generate_temp_password()
        logger.info("Senha temporária gerada")
        
        if not update_user_password(user['id'], temp_password):
            logger.error("Falha ao atualizar senha no banco")
            return {"success": False, "message": "Erro ao atualizar senha"}
        
        if not send_temp_password_email(user['email'], user['username'], temp_password):
            logger.error("Falha ao enviar email")
            return {"success": False, "message": "Erro ao enviar email"}
        
        logger.info("Processo de reset de senha concluído com sucesso")
        return {"success": True, "message": "Email enviado com sucesso"}
    except Exception as e:
        logger.error(f"Erro no processo de reset de senha: {e}")
        return {"success": False, "message": str(e)}

if __name__ == "__main__":
    if len(sys.argv) != 2:
        logger.error("Uso: python password_reset.py <username>")
        sys.exit(1)
    
    username = sys.argv[1]
    result = reset_password(username)
    
    if result["success"]:
        logger.info(result["message"])
        sys.exit(0)
    else:
        logger.error(result["message"])
        sys.exit(1)
