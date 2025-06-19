package com.vpnpanel.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the Configuration object
            Configuration configuration = new Configuration();
            
            // Load the configuration file
            try {
                configuration.configure("hibernate.cfg.xml");
                System.out.println("Hibernate Configuration loaded");
            } catch (Exception e) {
                System.err.println("Error loading hibernate.cfg.xml:");
                e.printStackTrace();
                throw e;
            }
            
            // Add annotated classes
            try {
                configuration.addAnnotatedClass(com.vpnpanel.model.User.class);
                configuration.addAnnotatedClass(com.vpnpanel.model.VPNAccess.class);
                configuration.addAnnotatedClass(com.vpnpanel.model.PasswordResetToken.class);
                System.out.println("Annotated classes added successfully");
            } catch (Exception e) {
                System.err.println("Error adding annotated classes:");
                e.printStackTrace();
                throw e;
            }
            
            // Build ServiceRegistry
            ServiceRegistry serviceRegistry = null;
            try {
                serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();
                System.out.println("ServiceRegistry built successfully");
            } catch (Exception e) {
                System.err.println("Error building ServiceRegistry:");
                e.printStackTrace();
                throw e;
            }
            
            // Build SessionFactory
            try {
                SessionFactory factory = configuration.buildSessionFactory(serviceRegistry);
                System.out.println("SessionFactory built successfully");
                return factory;
            } catch (Exception e) {
                System.err.println("Error building SessionFactory:");
                e.printStackTrace();
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                throw e;
            }
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory has not been initialized!");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }
}