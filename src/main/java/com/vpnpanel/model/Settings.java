package com.vpnpanel.model;

import javax.persistence.*;

@Entity
@Table(name = "settings")
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vpn_server_address")
    private String vpnServerAddress;

    @Column(name = "vpn_port")
    private Integer vpnPort;

    @Column(name = "vpn_protocol")
    private String vpnProtocol;

    @Column(name = "smtp_server")
    private String smtpServer;

    @Column(name = "smtp_port")
    private Integer smtpPort;

    @Column(name = "smtp_username")
    private String smtpUsername;

    @Column(name = "smtp_password")
    private String smtpPassword;

    @Column(name = "smtp_use_tls")
    private Boolean smtpUseTls;

    @Column(name = "cert_path")
    private String certPath;

    @Column(name = "key_path")
    private String keyPath;

    @Column(name = "ca_path")
    private String caPath;

    @Column(name = "ssh_host")
    private String sshHost;

    @Column(name = "ssh_port")
    private Integer sshPort;

    @Column(name = "ssh_username")
    private String sshUsername;

    @Column(name = "ssh_password")
    private String sshPassword;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVpnServerAddress() { return vpnServerAddress; }
    public void setVpnServerAddress(String vpnServerAddress) { this.vpnServerAddress = vpnServerAddress; }

    public Integer getVpnPort() { return vpnPort; }
    public void setVpnPort(Integer vpnPort) { this.vpnPort = vpnPort; }

    public String getVpnProtocol() { return vpnProtocol; }
    public void setVpnProtocol(String vpnProtocol) { this.vpnProtocol = vpnProtocol; }

    public String getSmtpServer() { return smtpServer; }
    public void setSmtpServer(String smtpServer) { this.smtpServer = smtpServer; }

    public Integer getSmtpPort() { return smtpPort; }
    public void setSmtpPort(Integer smtpPort) { this.smtpPort = smtpPort; }

    public String getSmtpUsername() { return smtpUsername; }
    public void setSmtpUsername(String smtpUsername) { this.smtpUsername = smtpUsername; }

    public String getSmtpPassword() { return smtpPassword; }
    public void setSmtpPassword(String smtpPassword) { this.smtpPassword = smtpPassword; }

    public Boolean getSmtpUseTls() { return smtpUseTls; }
    public void setSmtpUseTls(Boolean smtpUseTls) { this.smtpUseTls = smtpUseTls; }

    public String getCertPath() { return certPath; }
    public void setCertPath(String certPath) { this.certPath = certPath; }

    public String getKeyPath() { return keyPath; }
    public void setKeyPath(String keyPath) { this.keyPath = keyPath; }

    public String getCaPath() { return caPath; }
    public void setCaPath(String caPath) { this.caPath = caPath; }

    public String getSshHost() { return sshHost; }
    public void setSshHost(String sshHost) { this.sshHost = sshHost; }

    public Integer getSshPort() { return sshPort; }
    public void setSshPort(Integer sshPort) { this.sshPort = sshPort; }

    public String getSshUsername() { return sshUsername; }
    public void setSshUsername(String sshUsername) { this.sshUsername = sshUsername; }

    public String getSshPassword() { return sshPassword; }
    public void setSshPassword(String sshPassword) { this.sshPassword = sshPassword; }
} 