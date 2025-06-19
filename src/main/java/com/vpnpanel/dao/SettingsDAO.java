package com.vpnpanel.dao;

import com.vpnpanel.model.Settings;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class SettingsDAO {
    private final SessionFactory sessionFactory;

    public SettingsDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Settings getSettings() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Settings", Settings.class)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    public void saveSettings(Settings settings) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.saveOrUpdate(settings);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
        }
    }
} 