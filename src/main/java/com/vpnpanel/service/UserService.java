package com.vpnpanel.service;

import com.vpnpanel.dao.UserDAO;
import com.vpnpanel.model.User;
import com.vpnpanel.util.HibernateUtil;
import com.vpnpanel.util.SimpleHash;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.List;

public class UserService {
    private static UserService instance;
    private final UserDAO userDAO;

    private static final long RESET_TOKEN_EXPIRATION = 24 * 60 * 60 * 1000; // 24 horas em milissegundos

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserService getInstance(UserDAO userDAO) {
        if (instance == null) {
            instance = new UserService(userDAO);
        }
        return instance;
    }

    public User getUserByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<User> getUsersByFilter(String filter) {
        return userDAO.getUsersByFilter(filter);
    }

    public void saveUser(User user) {
        if (user.getPassword() != null && user.getPassword().length() != 32) {
            // Só gera novo hash se a senha não estiver no formato MD5 (32 caracteres)
            user.setPassword(SimpleHash.md5(user.getPassword()));
        }
        userDAO.saveUser(user);
    }

    public User getUserById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        }
    }

    public void deleteUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // Primeiro, deletar todas as VPNs associadas ao usuário
                session.createQuery("DELETE FROM VPNAccess WHERE user = :user")
                    .setParameter("user", user)
                    .executeUpdate();
                
                // Depois, deletar o usuário
                session.delete(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public boolean authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null) {
            try {
                // Verifica se o usuário está ativo
                if (!user.isActive()) {
                    System.out.println("Authentication failed: User is not active");
                    return false;
                }
                
                String hashedPassword = SimpleHash.md5(password);
                boolean matches = hashedPassword.equals(user.getPassword());
                System.out.println("Authentication result for " + username + ": " + matches);
                return matches;
            } catch (Exception e) {
                System.err.println("Error during password verification: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void saveResetToken(User user, String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                user.setResetToken(token);
                user.setResetTokenExpiry(new Date(System.currentTimeMillis() + RESET_TOKEN_EXPIRATION));
                session.update(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public User getUserByResetToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE resetToken = :token", User.class)
                    .setParameter("token", token)
                    .uniqueResult();
        }
    }

    public boolean isResetTokenValid(User user, String token) {
        if (user == null || token == null || !token.equals(user.getResetToken())) {
            return false;
        }
        
        Date expiry = user.getResetTokenExpiry();
        return expiry != null && expiry.after(new Date());
    }

    public void clearResetToken(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                user.setResetToken(null);
                user.setResetTokenExpiry(null);
                session.update(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public void updatePassword(User user, String newPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                user.setPassword(SimpleHash.md5(newPassword));
                user.setFirstLogin(false);
                session.update(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public void updateUser(User user) {
        if (user.getPassword() != null && user.getPassword().length() != 32) {
            // Só gera novo hash se a senha não estiver no formato MD5 (32 caracteres)
            user.setPassword(SimpleHash.md5(user.getPassword()));
        }
        userDAO.update(user);
    }
}