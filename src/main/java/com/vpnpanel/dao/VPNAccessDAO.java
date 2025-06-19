package com.vpnpanel.dao;

import com.vpnpanel.model.User;
import com.vpnpanel.model.VPNAccess;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class VPNAccessDAO {
    private final SessionFactory sessionFactory;

    public VPNAccessDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public VPNAccess getAccessById(String id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(VPNAccess.class, id);
        }
    }

    public List<VPNAccess> getAccessesByUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM VPNAccess WHERE user = :user ORDER BY creationDate DESC", VPNAccess.class)
                    .setParameter("user", user)
                    .list();
        }
    }

    
    
    public byte[] getConfigFileBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public List<VPNAccess> getAccessesByUserAndFilter(User user, String filter) {
        try (Session session = sessionFactory.openSession()) {
            Query<VPNAccess> query = session.createQuery(
                    "FROM VPNAccess WHERE user = :user AND identifier LIKE :filter ORDER BY createdAt DESC", 
                    VPNAccess.class);
            query.setParameter("user", user);
            query.setParameter("filter", "%" + filter + "%");
            return query.list();
        }
    }

    public void saveAccess(VPNAccess access) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(access);
            session.getTransaction().commit();
        }
    }

    public void deleteAccess(VPNAccess access) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(access);
            session.getTransaction().commit();
        }
    }
}