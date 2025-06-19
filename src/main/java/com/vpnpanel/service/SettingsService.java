package com.vpnpanel.service;

import com.vpnpanel.dao.SettingsDAO;
import com.vpnpanel.model.Settings;

public class SettingsService {
    private static SettingsService instance;
    private final SettingsDAO settingsDAO;

    private SettingsService(SettingsDAO settingsDAO) {
        this.settingsDAO = settingsDAO;
    }

    public static SettingsService getInstance(SettingsDAO settingsDAO) {
        if (instance == null) {
            instance = new SettingsService(settingsDAO);
        }
        return instance;
    }

    public Settings getSettings() {
        Settings settings = settingsDAO.getSettings();
        if (settings == null) {
            settings = createDefaultSettings();
            settingsDAO.saveSettings(settings);
        }
        return settings;
    }

    public void saveSettings(Settings settings) {
        settingsDAO.saveSettings(settings);
    }

    private Settings createDefaultSettings() {
        Settings settings = new Settings();
        // Configurações SSH padrão
        settings.setSshHost("192.168.0.29");  // IP padrão da máquina virtual
        settings.setSshPort(22);              // Porta SSH padrão
        settings.setSshUsername("usuario");    // Usuário padrão
        settings.setSshPassword("123456");     // Senha padrão
        return settings;
    }
} 