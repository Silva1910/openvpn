package com.vpnpanel.service;

import com.vpnpanel.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VPNConfigService {
    
    public String generateVPNConfig(User user) throws Exception {
        try {
            // Comando exato para executar na máquina virtual Linux
            String[] command = {
                "sudo",
                "python3",
                "scriptVPN.py",  // Sem caminho completo, pois será executado no diretório atual
                user.getUsername(),
                user.getPassword(),
                String.valueOf(user.getId())
            };
            
            // Debug para ver o comando que será executado
            System.out.println("Executando comando: " + String.join(" ", command));
            
            // Executar o comando
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // Combinar stderr com stdout
            Process process = processBuilder.start();
            
            // Ler a saída do script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("Script output: " + line);
            }
            
            // Aguardar o término do processo
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                throw new Exception("Script retornou erro (código " + exitCode + "): " + output.toString());
            }
            
            return output.toString();
            
        } catch (Exception e) {
            System.err.println("Erro detalhado: " + e.toString());
            e.printStackTrace();
            throw new Exception("Failed to generate VPN configuration: " + e.getMessage());
        }
    }
} 