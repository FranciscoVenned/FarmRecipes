package com.venned.farmrecipes.file;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class AbstractFile {
    private final JavaPlugin plugin;
    private final String fileName;
    private FileConfiguration file = null;
    private File configFile = null;

    public AbstractFile(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.saveDefaultConfig();
        this.reloadConfig();
    }

    public void saveDefaultConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
        }

        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.fileName, false);
        }

    }

    public void reloadConfig() {
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), this.fileName);
        }

        this.file = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public FileConfiguration getFile() {
        if (this.file == null) {
            this.reloadConfig();
        }

        return this.file;
    }

    public void saveFile() {
        if (this.file != null && this.configFile != null) {
            try {
                this.getFile().save(this.configFile);
            } catch (IOException var2) {
                this.plugin.getLogger().severe("Could not save config to " + this.configFile);
            }

        }
    }
}
