package com.vpnpanel.util;

import com.jcraft.jsch.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SSHService {
    private static SSHService instance;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    
    private SSHService(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    public static SSHService getInstance(String host, int port, String username, String password) {
        if (instance == null) {
            instance = new SSHService(host, port, username, password);
        }
        return instance;
    }
    
    public ExecutionResult executeScript(String username, String password, String userId) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        
        try {
            System.out.println("Iniciando conexão SSH...");
            // Create SSH session
            session = jsch.getSession(this.username, host, port);
            session.setPassword(this.password);
            
            // Skip host key checking (Note: In production, you should handle this properly)
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            // Connect to remote host
            System.out.println("Conectando ao servidor SSH...");
            session.connect();
            System.out.println("Conexão SSH estabelecida.");
            
            // Create channel for command execution
            channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec) channel;
            
            // Prepare command with proper escaping and provide the sudo password
            String command = String.format("echo '123456' | sudo -S python3 /home/usuario/scriptVPN.py '%s' '%s' '%s' 2>&1",
                    escapeString(username),
                    escapeString(password),
                    escapeString(userId));
            
            System.out.println("Executando comando: " + command);
            channelExec.setCommand(command);
            
            // Get command output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            channelExec.setOutputStream(outputStream);
            channelExec.setErrStream(errorStream);
            
            // Execute command
            System.out.println("Conectando canal de execução...");
            channelExec.connect();
            
            // Wait for command to complete
            System.out.println("Aguardando conclusão do comando...");
            while (channelExec.isConnected()) {
                Thread.sleep(100);
            }
            
            // Get exit status
            int exitStatus = channelExec.getExitStatus();
            String output = outputStream.toString();
            String error = errorStream.toString();
            
            System.out.println("Comando concluído com status: " + exitStatus);
            System.out.println("Saída do comando: " + output);
            if (!error.isEmpty()) {
                System.out.println("Erro do comando: " + error);
            }
            
            return new ExecutionResult(
                exitStatus,
                output,
                error
            );
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Execução interrompida: " + e.getMessage());
            throw new IOException("Command execution interrupted", e);
        } catch (Exception e) {
            System.out.println("Erro durante execução: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    private String escapeString(String str) {
        return str.replace("'", "'\\''");
    }
    
    public static class ExecutionResult {
        private final int exitStatus;
        private final String output;
        private final String error;
        
        public ExecutionResult(int exitStatus, String output, String error) {
            this.exitStatus = exitStatus;
            this.output = output;
            this.error = error;
        }
        
        public int getExitStatus() {
            return exitStatus;
        }
        
        public String getOutput() {
            return output;
        }
        
        public String getError() {
            return error;
        }
        
        public boolean isSuccess() {
            return exitStatus == 0;
        }
    }

    public byte[] downloadFileViaSFTP(String remoteFilePath) throws JSchException, SftpException, IOException {
        Session session = null;
        ChannelSftp channelSftp = null;
        
        try {
            // Create SSH session
            session = createSession();
            
            // Create SFTP channel
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            
            // Download file to byte array
            try (InputStream is = channelSftp.get(remoteFilePath)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > -1) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            }
            
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    private Session createSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(this.username, host, port);
        session.setPassword(this.password);
        
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        
        // Adicionar timeouts
        session.setTimeout(30000); // 30 segundos de timeout
        session.setConfig("PreferredAuthentications", "password");
        session.setConfig("MaxAuthTries", "1");
        
        session.setConfig(config);
        
        System.out.println("Tentando conectar ao servidor SSH: " + host + ":" + port);
        System.out.println("Usuário: " + username);
        
        try {
            session.connect();
            System.out.println("Conexão SSH estabelecida com sucesso!");
        } catch (JSchException e) {
            System.out.println("Falha ao conectar via SSH: " + e.getMessage());
            throw e;
        }
        
        return session;
    }

    public ExecutionResult executeCommand(String command) throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = null;
        Channel channel = null;
        
        try {
            session = createSession();
            
            // Create channel for command execution
            channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec) channel;
            
            System.out.println("Executando comando: " + command);
            channelExec.setCommand(command);
            
            // Get command output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            channelExec.setOutputStream(outputStream);
            channelExec.setErrStream(errorStream);
            
            // Execute command
            channelExec.connect();
            
            // Wait for command to complete with timeout
            int maxWaitTime = 30000; // 30 segundos
            long startTime = System.currentTimeMillis();
            
            while (channelExec.isConnected()) {
                try {
                    Thread.sleep(100);
                    if (System.currentTimeMillis() - startTime > maxWaitTime) {
                        throw new IOException("Command execution timed out after 30 seconds");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Command execution interrupted", e);
                }
            }
            
            // Get exit status
            int exitStatus = channelExec.getExitStatus();
            String output = outputStream.toString();
            String error = errorStream.toString();
            
            System.out.println("Comando concluído com status: " + exitStatus);
            System.out.println("Saída do comando: " + output);
            if (!error.isEmpty()) {
                System.out.println("Erro do comando: " + error);
            }
            
            // Se houver erro mas o status for 0, ainda consideramos sucesso
            // pois o sudo pode escrever mensagens no stderr
            if (exitStatus != 0) {
                return new ExecutionResult(exitStatus, output, error);
            }
            
            return new ExecutionResult(exitStatus, output, "");
            
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
} 