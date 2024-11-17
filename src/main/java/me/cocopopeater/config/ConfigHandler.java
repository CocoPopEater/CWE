package me.cocopopeater.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config", "cwe_config.json");

    // Example config fields
    private boolean isEnabled = true;
    private int commandDelay = 1000;

    private static ConfigHandler instance;

    public static ConfigHandler getInstance() {
        if (instance == null) {
            instance = new ConfigHandler();
            instance.loadConfig();
        }
        return instance;
    }

    // Load the configuration file
    public void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, ConfigHandler.class);
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
            }
        } else {
            saveConfig(); // Save default config if the file doesn't exist
        }
    }

    // Save the configuration file
    public void saveConfig() {
        try {
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    // Example getters and setters for config fields
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        saveConfig();
    }

    public int getCommandDelay() {
        return commandDelay;
    }

    public void setCommandDelay(int commandDelay) {
        Integer value = commandDelay;
        if(value != null){
            this.commandDelay = Math.max(commandDelay, 1); // ensure no 0ms command delay
        }
        saveConfig();
    }
}
