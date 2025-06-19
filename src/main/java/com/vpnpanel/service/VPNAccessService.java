package com.vpnpanel.service;

import com.vpnpanel.dao.VPNAccessDAO;
import com.vpnpanel.model.User;
import com.vpnpanel.model.VPNAccess;
import com.vpnpanel.model.Settings;
import com.vpnpanel.util.SSHService;
import com.vpnpanel.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.jcraft.jsch.JSchException;

public class VPNAccessService {
    private static VPNAccessService instance;
    private final VPNAccessDAO vpnAccessDAO;
    private final SettingsService settingsService;

    private VPNAccessService(VPNAccessDAO vpnAccessDAO, SettingsService settingsService) {
        this.vpnAccessDAO = vpnAccessDAO;
        this.settingsService = settingsService;
    }

    public static VPNAccessService getInstance(VPNAccessDAO vpnAccessDAO, SettingsService settingsService) {
        if (instance == null) {
            instance = new VPNAccessService(vpnAccessDAO, settingsService);
        }
        return instance;
    }

    private String generateUniqueCommonName(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            int increment = 1;
            String commonName;
            Long count;
            do {
                commonName = username + "-" + increment;
                count = session.createQuery(
                    "SELECT COUNT(v) FROM VPNAccess v WHERE v.commonName = :commonName",
                    Long.class
                )
                .setParameter("commonName", commonName)
                .uniqueResult();
                increment++;
            } while (count != null && count > 0);
            
            return commonName;
        }
    }

    private boolean isCommonNameUnique(String commonName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(v) FROM VPNAccess v WHERE v.commonName = :commonName",
                Long.class
            );
            query.setParameter("commonName", commonName);
            return query.uniqueResult() == 0;
        }
    }

    public List<VPNAccess> getAllVPNAccess() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM VPNAccess ORDER BY createdAt DESC", 
                VPNAccess.class
            ).list();
        }
    }

    public List<VPNAccess> getAllVPNAccessWithFilter(String filter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM VPNAccess WHERE identifier LIKE :filter ORDER BY createdAt DESC", 
                VPNAccess.class
            )
            .setParameter("filter", "%" + filter + "%")
            .list();
        }
    }

    public List<VPNAccess> getUserAccesses(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM VPNAccess WHERE user = :user ORDER BY createdAt DESC", 
                VPNAccess.class
            )
            .setParameter("user", user)
            .list();
        }
    }

    public List<VPNAccess> getUserAccessesWithFilter(User user, String filter) {
        return vpnAccessDAO.getAccessesByUserAndFilter(user, filter);
    }

    public void toggleVPNStatus(String identifier, User user) throws IOException, JSchException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                VPNAccess vpnAccess = session.createQuery(
                    "FROM VPNAccess WHERE identifier = :identifier AND user = :user", 
                    VPNAccess.class
                )
                .setParameter("identifier", identifier)
                .setParameter("user", user)
                .uniqueResult();

                if (vpnAccess != null) {
                    boolean newStatus = !vpnAccess.isActive();
                    vpnAccess.setActive(newStatus);
                    
                    // Se estiver desativando, revogar o certificado
                    if (!newStatus) {
                        // Get settings for SSH connection
                        Settings settings = settingsService.getSettings();
                        
                        // Create SSH service instance
                        SSHService sshService = SSHService.getInstance(
                            settings.getSshHost(),
                            settings.getSshPort(),
                            settings.getSshUsername(),
                            settings.getSshPassword()
                        );

                        // Execute revoke script on remote server
                        String zipFilename = vpnAccess.getIdentifier() + ".zip";
                        // Fornecer a senha do sudo via echo e usar o caminho correto do script
                        SSHService.ExecutionResult result = sshService.executeCommand(
                            String.format("echo '123456' | sudo -S python3 /home/usuario/revogar_certificado.py '%s' 2>&1",
                                zipFilename)
                        );

                        if (!result.isSuccess()) {
                            System.out.println("Erro ao revogar certificado. Status: " + result.getExitStatus());
                            System.out.println("Saída: " + result.getOutput());
                            System.out.println("Erro: " + result.getError());
                            throw new IOException("Failed to revoke certificate: " + result.getError());
                        }
                    }
                    
                    session.update(vpnAccess);
                }
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public VPNAccess generateVPNConfig(User user, String password) throws IOException, JSchException {
        Settings settings = settingsService.getSettings();
        
        System.out.println("Iniciando geração de configuração VPN para usuário: " + user.getUsername());
        
        // Gerar commonName único
        String commonName = generateUniqueCommonName(user.getUsername());
        
        // Create SSH service instance
        SSHService sshService = SSHService.getInstance(
            settings.getSshHost(),
            settings.getSshPort(),
            settings.getSshUsername(),
            settings.getSshPassword()
        );
        
        System.out.println("Executando script no servidor remoto...");
        // Execute the script on the remote server with commonName
        SSHService.ExecutionResult result = sshService.executeScript(
            commonName, // Usar o commonName em vez do username
            password,
            user.getId().toString()
        );
        
        if (!result.isSuccess()) {
            System.out.println("Falha na execução do script.");
            System.out.println("Status de saída: " + result.getExitStatus());
            System.out.println("Saída do comando: " + result.getOutput());
            System.out.println("Erro do comando: " + result.getError());
            throw new IOException("Failed to generate VPN configuration: " + result.getError() + "\nOutput: " + result.getOutput());
        }
        
        System.out.println("Script executado com sucesso.");
        System.out.println("Saída do script: " + result.getOutput());
        
        String identifier = extractIdentifierFromOutput(result.getOutput());
        if (identifier == null) {
            System.out.println("ERRO: Não foi possível extrair o identificador da saída do script:");
            System.out.println("Saída completa do script:");
            System.out.println(result.getOutput());
            throw new IOException("Não foi possível extrair o identificador do script");
        }
        
        System.out.println("Identificador extraído: " + identifier);
        
        // Create VPN access record
        VPNAccess vpnAccess = new VPNAccess();
        vpnAccess.setUser(user);
        vpnAccess.setCreatedAt(new Date());
        vpnAccess.setActive(true);
        vpnAccess.setIdentifier(identifier);
        vpnAccess.setCommonName(commonName);
        
        try {
            // Save to database
            System.out.println("Salvando registro de acesso VPN no banco de dados...");
            vpnAccessDAO.saveAccess(vpnAccess);
            System.out.println("Registro salvo com sucesso!");
            return vpnAccess;
        } catch (Exception e) {
            System.out.println("ERRO ao salvar no banco de dados:");
            e.printStackTrace();
            System.out.println("Detalhes do registro que tentou salvar:");
            System.out.println("User ID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Identifier: " + identifier);
            System.out.println("CommonName: " + commonName);
            System.out.println("Created At: " + vpnAccess.getCreatedAt());
            throw e;
        }
    }
    
    private String extractIdentifierFromOutput(String output) {
        System.out.println("Tentando extrair identificador da saída:");
        System.out.println("---INÍCIO DA SAÍDA---");
        System.out.println(output);
        System.out.println("---FIM DA SAÍDA---");

        String[] lines = output.split("\n");
        for (String line : lines) {
            System.out.println("Analisando linha: " + line);
            
            // Primeiro tentar pegar o nome do arquivo ZIP
            if (line.contains("Arquivo ZIP final:")) {
                String zipPath = line.split(":")[1].trim();
                // Extrair o nome do arquivo sem a extensão .zip e o caminho
                String fileName = new java.io.File(zipPath).getName().replace(".zip", "");
                System.out.println("Identificador encontrado (do arquivo ZIP): " + fileName);
                return fileName;
            }
        }
        
        // Se não encontrar o arquivo ZIP, gerar um identificador único
        String timestamp = String.format("%d%03d", 
            System.currentTimeMillis() / 1000, 
            System.nanoTime() % 1000);
        String identifier = "vpn_" + timestamp;
        System.out.println("Usando identificador gerado: " + identifier);
        return identifier;
    }

    public void deleteVPNAccess(String identifier, User user) throws IOException, JSchException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                VPNAccess vpnAccess = session.createQuery(
                    "FROM VPNAccess WHERE identifier = :identifier AND user = :user", 
                    VPNAccess.class
                )
                .setParameter("identifier", identifier)
                .setParameter("user", user)
                .uniqueResult();

                if (vpnAccess == null) {
                    throw new IllegalArgumentException("VPN Access not found or unauthorized");
                }

                // Get settings for SSH connection
                Settings settings = settingsService.getSettings();
                
                // Create SSH service instance
                SSHService sshService = SSHService.getInstance(
                    settings.getSshHost(),
                    settings.getSshPort(),
                    settings.getSshUsername(),
                    settings.getSshPassword()
                );

                // Execute revoke script on remote server
                String zipFilename = vpnAccess.getIdentifier() + ".zip";
                SSHService.ExecutionResult result = sshService.executeCommand(
                    "sudo python3 /usr/local/bin/revogar_certificado.py " + zipFilename
                );

                if (!result.isSuccess()) {
                    throw new IOException("Failed to revoke certificate: " + result.getError());
                }

                // Delete from database
                session.delete(vpnAccess);
                tx.commit();
                
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    public VPNAccess getVPNAccessByIdentifier(String identifier) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM VPNAccess WHERE identifier = :identifier",
                VPNAccess.class
            )
            .setParameter("identifier", identifier)
            .uniqueResult();
        }
    }
}