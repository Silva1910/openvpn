package com.vpnpanel.dao;

import com.vpnpanel.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;
import java.util.logging.Logger;

public class UserDAO {
    private final SessionFactory sessionFactory;
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User getUserByUsername(String username) {
        System.out.println("\n=== Buscando usuário por username ===");
        System.out.println("Username: " + username);
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            System.out.println("Usuário encontrado: " + user);
            if (user != null) {
                System.out.println("Senha do usuário: [" + user.getPassword() + "]");
            }
            return user;
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    public User getUserById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        }
    }
    
    public void saveUser(User user) {
        System.out.println("\n=== Salvando usuário no banco ===");
        System.out.println("Dados do usuário: " + user);
        System.out.println("Senha antes de salvar: [" + user.getPassword() + "]");
        
        // Garantir que campos obrigatórios estejam inicializados
        user.setFailedLoginAttempts(user.getFailedLoginAttempts()); // Isso manterá o valor atual ou o padrão (0)
        
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            
            session.saveOrUpdate(user);
            transaction.commit();
            
            System.out.println("Usuário salvo com sucesso!");
            System.out.println("Senha após salvar: [" + user.getPassword() + "]");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    public List<User> getUsersByFilter(String filter) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery(
                "FROM User WHERE username LIKE :filter OR fullName LIKE :filter OR email LIKE :filter", 
                User.class
            );
            query.setParameter("filter", "%" + filter + "%");
            return query.list();
        }
    }

    public void deleteUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.delete(user);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public void update(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.severe("Erro ao atualizar usuário: " + e.getMessage());
            throw e;
        }
    }
}