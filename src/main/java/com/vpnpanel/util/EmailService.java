package com.vpnpanel.util;

import com.vpnpanel.service.SettingsService;
import com.vpnpanel.model.Settings;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

public class EmailService {
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    private static EmailService instance;
    private final SettingsService settingsService;
    
    private EmailService() {
        this.settingsService = DependencyFactory.getSettingsService();
    }
    
    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    public boolean sendEmail(String to, String subject, String content) {
        Settings settings = settingsService.getSettings();
        if (settings == null || settings.getSmtpServer() == null || settings.getSmtpUsername() == null) {
            logger.severe("Configurações de SMTP não encontradas");
            return false;
        }
        
        Properties prop = new Properties();
        prop.put("mail.smtp.host", settings.getSmtpServer());
        prop.put("mail.smtp.port", settings.getSmtpPort().toString());
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", settings.getSmtpUseTls().toString());
        
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    settings.getSmtpUsername(),
                    settings.getSmtpPassword()
                );
            }
        });
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(settings.getSmtpUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);
            
            Transport.send(message);
            logger.info("Email enviado com sucesso para: " + to);
            return true;
        } catch (MessagingException e) {
            logger.severe("Erro ao enviar email: " + e.getMessage());
            return false;
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) throws MessagingException {
        logger.info("Enviando email de redefinição de senha para: " + toEmail);
        
        String htmlContent = String.format(
            "<html><body>" +
            "<h2>Redefinição de Senha</h2>" +
            "<p>Foi solicitada a redefinição de senha para sua conta no VPN Panel.</p>" +
            "<p>Para redefinir sua senha, clique no link abaixo:</p>" +
            "<p><a href='%s'>Redefinir Senha</a></p>" +
            "<p>Este link é válido por 24 horas.</p>" +
            "<p>Se você não solicitou esta redefinição, por favor ignore este email.</p>" +
            "<p>Atenciosamente,<br>Equipe VPN Panel</p>" +
            "</body></html>",
            resetLink
        );

        if (!sendEmail(toEmail, "Redefinição de Senha - VPN Panel", htmlContent)) {
            throw new MessagingException("Falha ao enviar email de redefinição de senha");
        }
    }
}