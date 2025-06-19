package com.vpnpanel.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;
    
    @Column(nullable = false)
    private boolean used = false;
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}