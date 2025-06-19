package com.vpnpanel.util;

import org.mindrot.jbcrypt.BCrypt;

public class GenerateHash {
    public static void main(String[] args) {
        String password = "admin123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("Senha original: " + password);
        System.out.println("Hash gerado: " + hashedPassword);
        
        // Testar verificação
        boolean verificacao = BCrypt.checkpw(password, hashedPassword);
        System.out.println("Verificação com senha correta: " + verificacao);
        
        // Testar com o hash atual do banco
        String hashAtual = "$2a$10$vG7UJXVCGrQrur0OH0FKxOYTqn5PoFK4H6AV8Hd6gYxR4qYGJZNWi";
        boolean verificacaoAtual = BCrypt.checkpw(password, hashAtual);
        System.out.println("Verificação com hash atual: " + verificacaoAtual);
    }
} 