package com.vpnpanel.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.vpnpanel.dao.*;
import com.vpnpanel.service.*;
import com.vpnpanel.model.Settings;

public class DependencyFactory {
    private static SessionFactory sessionFactory;
    private static UserDAO userDAO;
    private static VPNAccessDAO vpnAccessDAO;
    private static PasswordResetTokenDAO passwordResetTokenDAO;
    private static SettingsDAO settingsDAO;
    
    private static UserService userService;
    private static VPNAccessService vpnAccessService;
    private static PasswordResetService passwordResetService;
    private static SettingsService settingsService;
    
    private static EmailService emailService;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                System.out.println("Initializing DependencyFactory...");
                Configuration configuration = new Configuration().configure();
                System.out.println("Hibernate Configuration loaded");
                sessionFactory = configuration.buildSessionFactory();
                System.out.println("SessionFactory built successfully");
            } catch (Exception e) {
                System.err.println("Error building SessionFactory:");
                e.printStackTrace();
                throw e;
            }
        }
        return sessionFactory;
    }
    
    public static UserDAO getUserDAO() {
        if (userDAO == null) {
            userDAO = new UserDAO(getSessionFactory());
        }
        return userDAO;
    }
    
    public static VPNAccessDAO getVPNAccessDAO() {
        if (vpnAccessDAO == null) {
            vpnAccessDAO = new VPNAccessDAO(getSessionFactory());
        }
        return vpnAccessDAO;
    }
    
    public static PasswordResetTokenDAO getPasswordResetTokenDAO() {
        if (passwordResetTokenDAO == null) {
            passwordResetTokenDAO = new PasswordResetTokenDAO();
        }
        return passwordResetTokenDAO;
    }
    
    public static SettingsDAO getSettingsDAO() {
        if (settingsDAO == null) {
            settingsDAO = new SettingsDAO(getSessionFactory());
        }
        return settingsDAO;
    }
    
    public static UserService getUserService() {
        if (userService == null) {
            userService = UserService.getInstance(getUserDAO());
        }
        return userService;
    }
    
    public static VPNAccessService getVPNAccessService() {
        if (vpnAccessService == null) {
            vpnAccessService = VPNAccessService.getInstance(
                getVPNAccessDAO(),
                getSettingsService()
            );
        }
        return vpnAccessService;
    }
    
    public static PasswordResetService getPasswordResetService() {
        if (passwordResetService == null) {
            passwordResetService = new PasswordResetService(
                    getPasswordResetTokenDAO(), 
                    getUserDAO(), 
                    getEmailService());
        }
        return passwordResetService;
    }
    
    public static SettingsService getSettingsService() {
        if (settingsService == null) {
            settingsService = SettingsService.getInstance(getSettingsDAO());
        }
        return settingsService;
    }
    
    public static EmailService getEmailService() {
        return EmailService.getInstance();
    }
}