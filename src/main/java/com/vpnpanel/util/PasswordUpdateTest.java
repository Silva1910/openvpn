package com.vpnpanel.util;

import com.vpnpanel.model.User;
import com.vpnpanel.service.UserService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class PasswordUpdateTest {
    public static void main(String[] args) {
        try {
            // Obter a SessionFactory
            SessionFactory sessionFactory = DependencyFactory.getSessionFactory();
            
            // Abrir uma sessão
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = session.beginTransaction();
                
                // Buscar o usuário admin
                User admin = session.createQuery("FROM User WHERE username = :username", User.class)
                        .setParameter("username", "admin")
                        .uniqueResult();
                
                if (admin != null) {
                    System.out.println("Senha atual: " + admin.getPassword());
                    
                    // Atualizar a senha
                    admin.setPassword("123");
                    session.update(admin);
                    
                    // Commit da transação
                    tx.commit();
                    
                    System.out.println("Senha atualizada para: " + admin.getPassword());
                    
                    // Verificar se a atualização foi persistida
                    session.clear(); // Limpa o cache da sessão
                    
                    User adminAfterUpdate = session.createQuery("FROM User WHERE username = :username", User.class)
                            .setParameter("username", "admin")
                            .uniqueResult();
                            
                    System.out.println("Senha após buscar novamente: " + adminAfterUpdate.getPassword());
                } else {
                    System.out.println("Usuário admin não encontrado");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar senha:");
            e.printStackTrace();
        }
    }
} 