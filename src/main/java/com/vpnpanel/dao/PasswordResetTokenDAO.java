package com.vpnpanel.dao;

import com.vpnpanel.model.PasswordResetToken;
import com.vpnpanel.model.User;
import com.vpnpanel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.logging.Logger;

public class PasswordResetTokenDAO {
    private static final Logger logger = Logger.getLogger(PasswordResetTokenDAO.class.getName());

    public void saveToken(PasswordResetToken token) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(token);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Erro ao salvar token: " + e.getMessage());
            throw e;
        }
    }

    public void update(PasswordResetToken token) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(token);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Erro ao atualizar token: " + e.getMessage());
            throw e;
        }
    }

    public PasswordResetToken findByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PasswordResetToken> query = session.createQuery(
                "FROM PasswordResetToken WHERE token = :token", 
                PasswordResetToken.class
            );
            query.setParameter("token", token);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.severe("Erro ao buscar token: " + e.getMessage());
            return null;
        }
    }

    public void invalidateUserTokens(User user) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<?> query = session.createQuery(
                "UPDATE PasswordResetToken SET used = true WHERE user = :user"
            );
            query.setParameter("user", user);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Erro ao invalidar tokens do usuário: " + e.getMessage());
            throw e;
        }
    }
}