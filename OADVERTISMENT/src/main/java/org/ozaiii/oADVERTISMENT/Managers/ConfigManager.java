package org.ozaiii.oADVERTISMENT.Managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
    private Plugin plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setupConfig() {
        this.plugin.saveDefaultConfig();
        if (!this.plugin.getDataFolder().exists())
            this.plugin.getDataFolder().mkdir();
        this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        if (!this.configFile.exists())
            try {
                this.configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void saveConfig() {
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void set(String path, Object value) {
        this.config.set(path, value);
        saveConfig();
    }

    public World getWorld(String worldName) {
        return Bukkit.getWorld(worldName);
    }

    public Object get(String path) {
        return this.config.get(path);
    }

    public String getString(String path) {
        return this.config.getString(path);
    }

    public String getString(String path, String defaultValue) {
        return this.config.getString(path, defaultValue);
    }

    public int getInt(String path) {
        return this.config.getInt(path);
    }

    public int getInt(String path, int defaultValue) {
        return this.config.getInt(path, defaultValue);
    }

    public boolean getBoolean(String path) {
        return this.config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean defaultValue) {
        return this.config.getBoolean(path, defaultValue);
    }

    public long getLong(String path) {
        return this.config.getLong(path);
    }

    public long getLong(String path, long defaultValue) {
        return this.config.getLong(path, defaultValue);
    }

    public double getDouble(String path) {
        return this.config.getDouble(path);
    }

    public double getDouble(String path, double defaultValue) {
        return this.config.getDouble(path, defaultValue);
    }
}
