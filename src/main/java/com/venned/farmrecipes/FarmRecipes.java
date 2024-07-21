package com.venned.farmrecipes;

import com.venned.farmrecipes.api.FarmRecipesAPI;
import com.venned.farmrecipes.commands.CMDISFarm;
import com.venned.farmrecipes.events.*;
import com.venned.farmrecipes.file.DataFile;
import com.venned.farmrecipes.manager.DayLimitManager;
import com.venned.farmrecipes.util.ChatUtil;
import com.venned.farmrecipes.util.FarmingUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public final class FarmRecipes extends JavaPlugin
{
    private FarmingUtil farmingUtil;
    private DataFile dataFile;
    private NumberFormat numberFormat;
    private DayLimitManager dayLimitManager;

    private static FarmRecipes instance;

    public void onEnable() {
        instance = this;
        this.farmingUtil = new FarmingUtil(this);
        this.numberFormat = ChatUtil.getNumberFormat(Locale.ENGLISH);
        this.initFiles();
        this.initListeners();
        this.initCommands();
        this.getFarmingUtil().initFarmingCrops();
        this.dayLimitManager = new DayLimitManager(this);

        FarmRecipesAPI.initialize(dayLimitManager);
    }

    public void onDisable() {
    }

    private void initCommands() {
        Objects.requireNonNull(this.getCommand("isfarm")).setExecutor((CommandExecutor)new CMDISFarm(this));
    }

    private void initListeners() {
        this.getServer().getPluginManager().registerEvents((Listener)new IslandListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PluginEnableListener(this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerIncreaserListener(this), this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerRecipeListener(), this);
        //   this.getServer().getPluginManager().registerEvents(new IdentifyItem(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("ManticHoes")) {
            this.getServer().getPluginManager().registerEvents((Listener)new ManticHoeListener(this), (Plugin)this);
            this.getServer().getLogger().info("### Enabled ManticHoes support (1) ###");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("JetsMinions")) {
            this.getServer().getPluginManager().registerEvents((Listener)new MinionListener(this), (Plugin)this);
            this.getServer().getLogger().info("### Enabled JetsMinions (1) support ###");
        }
    }

    private void initFiles() {
        this.saveDefaultConfig();
        this.dataFile = new DataFile(this);
    }

    public DayLimitManager getDayLimitManager() {
        return dayLimitManager;
    }

    public FarmingUtil getFarmingUtil() {
        return this.farmingUtil;
    }

    public FileConfiguration getDataFile() {
        return this.dataFile.getFile();
    }

    public void saveDataFile() {
        this.dataFile.saveFile();
    }

    public void reloadDataFile() {
        this.dataFile.reloadConfig();
    }

    public static FarmRecipes getInstance() {
        return instance;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }
}

