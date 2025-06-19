package com.vpnpanel.model;

import javax.persistence.*;
import java.util.Date;
import java.util.Calendar;

@Entity
@Table(name = "vpn_access")
public class VPNAccess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "expiration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;
    
    @Column(name = "active", nullable = false)
    private boolean active;
    
    @Column(name = "config_path")
    private String configPath;
    
    @Column(name = "config_file_path")
    private String configFilePath;
    
    @Column(name = "identifier", unique = true)
    private String identifier;
    
    @Column(name = "common_name", unique = true, nullable = false)
    private String commonName;
    
    // Construtores
    public VPNAccess() {
        this.active = true;
        this.createdAt = new Date();
        // Define a data de expiração para 1 ano após a criação
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.createdAt);
        cal.add(Calendar.YEAR, 1);
        this.expirationDate = cal.getTime();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getConfigPath() {
        return configPath;
    }
    
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
    
    public String getConfigFilePath() {
        return configFilePath;
    }
    
    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getCommonName() {
        return commonName;
    }
    
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    @Override
    public String toString() {
        return "VPNAccess{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", createdAt=" + createdAt +
                ", active=" + active +
                ", identifier=" + identifier +
                ", commonName=" + commonName +
                '}';
    }
}