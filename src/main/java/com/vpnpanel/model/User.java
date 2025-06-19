package com.vpnpanel.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "username", unique = true, nullable = false, length = 30)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    
    @Column(name = "active", nullable = false)
    private boolean active = true;
    
    @Column(name = "admin", nullable = false)
    private boolean admin = false;
    
    @Column(name = "first_login", nullable = false)
    private boolean firstLogin = true;
    
    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;
    
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @Column(name = "reset_token")
    private String resetToken;
    
    @Column(name = "reset_token_expiry")
    @Temporal(TemporalType.TIMESTAMP)
    private Date resetTokenExpiry;
    
    public User() {
        this.active = true;
        this.admin = false;
        this.firstLogin = true;
        this.failedLoginAttempts = 0;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VPNAccess> vpnAccesses;
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public boolean isFirstLogin() { return firstLogin; }
    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public List<VPNAccess> getVpnAccesses() { return vpnAccesses; }
    public void setVpnAccesses(List<VPNAccess> vpnAccesses) { this.vpnAccesses = vpnAccesses; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public String getResetToken() {
        return resetToken;
    }
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    public Date getResetTokenExpiry() {
        return resetTokenExpiry;
    }
    public void setResetTokenExpiry(Date resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", active=" + active +
            ", admin=" + admin +
            ", failedLoginAttempts=" + failedLoginAttempts +
            '}';
    }
}