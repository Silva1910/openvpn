package com.vpnpanel.service;

import com.vpnpanel.dao.PasswordResetTokenDAO;
import com.vpnpanel.dao.UserDAO;
import com.vpnpanel.model.PasswordResetToken;
import com.vpnpanel.model.User;
import com.vpnpanel.util.EmailService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class PasswordResetService {
    private static final Logger logger = Logger.getLogger(PasswordResetService.class.getName());
    private static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000; // 24 horas
    
    private final PasswordResetTokenDAO tokenDAO;
    private final UserDAO userDAO;
    private final EmailService emailService;

    public PasswordResetService(PasswordResetTokenDAO tokenDAO, UserDAO userDAO, EmailService emailService) {
        this.tokenDAO = tokenDAO;
        this.userDAO = userDAO;
        this.emailService = emailService;
    }

    public boolean requestPasswordReset(String username, HttpServletRequest request) {
        User user = userDAO.getUserByUsername(username);
        if (user == null || !user.isActive()) {
            // As per spec, we don't reveal if user exists or not
            return true;
        }

        // Invalidate any existing tokens for this user
        tokenDAO.invalidateUserTokens(user);

        // Create new token
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION));
        token.setUsed(false);
        tokenDAO.saveToken(token);

        // Send email
        String resetLink = request.getContextPath() + "/reset-password?token=" + token.getToken();
        String emailContent = "Para redefinir sua senha, clique no link abaixo (válido por 24 horas):\n\n" + resetLink;
        
        return emailService.sendEmail(user.getEmail(), "Redefinição de Senha - VPN Panel", emailContent);
    }

    public String createResetToken(User user) {
        // Invalidar tokens existentes
        tokenDAO.invalidateUserTokens(user);

        // Criar novo token
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION));
        token.setUsed(false);

        tokenDAO.saveToken(token);
        return token.getToken();
    }

    public boolean validateToken(String token) {
        PasswordResetToken resetToken = tokenDAO.findByToken(token);
        if (resetToken == null) {
            return false;
        }

        // Verificar se o token não expirou e não foi usado
        boolean valid = !resetToken.isUsed() && 
                       resetToken.getExpiryDate().after(new Date()) &&
                       resetToken.getUser().isActive();

        logger.info("Token validation result: " + valid);
        return valid;
    }

    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenDAO.findByToken(token);
        if (!validateToken(token)) {
            return false;
        }

        User user = resetToken.getUser();
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        
        // Marcar token como usado
        resetToken.setUsed(true);
        
        try {
            userDAO.update(user);
            tokenDAO.update(resetToken);
            return true;
        } catch (Exception e) {
            logger.severe("Erro ao resetar senha: " + e.getMessage());
            return false;
        }
    }
}