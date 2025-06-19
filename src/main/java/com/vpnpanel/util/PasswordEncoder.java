package com.vpnpanel.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        String password = "admin123";
        String hash = encode(password);
        
        System.out.println("Senha original: " + password);
        System.out.println("Hash gerado: " + hash);
        System.out.println("Verificação: " + matches(password, hash));
    }
} 