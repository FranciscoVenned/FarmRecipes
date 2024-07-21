package com.venned.farmrecipes.manager;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class DayLimitManager {
    private final FarmRecipes plugin;

    public DayLimitManager(FarmRecipes plugin) {
        this.plugin = plugin;
        startDayCheckRunnable();
    }

    private void startDayCheckRunnable() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            FileConfiguration config = plugin.getConfig();
            long timeDay = config.getLong("time-day");
            int day = config.getInt("day");
            long currentTime = System.currentTimeMillis();

            // 86400000 ms = 24 horas
            if (currentTime - timeDay >= 86400000L) {
                Bukkit.broadcastMessage("Siguien dia !");
                day++;
                config.set("day", day);
                config.set("time-day", currentTime);
                resetAllBonuses();

                plugin.saveConfig();
            }
        }, 0L, 1200L);
    }

    private void resetAllBonuses() {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        FileConfiguration dataConfig = FarmRecipes.getInstance().getDataFile();

        for (String key : dataConfig.getKeys(false)) {
            if (dataConfig.contains(key + ".bonus")) {
                dataConfig.set(key + ".bonus", 0);
            }
            dataConfig.set(key + ".amount", 0);

        }

        FarmRecipes.getInstance().saveDataFile();


    }

    public int getCurrentAmount(Player player){
        final Island islandAt = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
        if (islandAt == null || !islandAt.isMember(SuperiorSkyblockAPI.getPlayer(player))) {
            return 0;
        }
        String path = islandAt.getUniqueId().toString() + ".amount";
        return this.plugin.getDataFile().getInt(path, 0);

    }

    public int getCurrentAmountMax(Player player){
        final Island islandAt = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
        if (islandAt == null || !islandAt.isMember(SuperiorSkyblockAPI.getPlayer(player))) {
            return 0;
        }
        String pathBonus = islandAt.getUniqueId().toString() + ".bonus";
        int bonus = this.plugin.getDataFile().getInt(pathBonus, 0);
        int amount = getCurrentLimit();
        return amount + bonus;
    }



    public int getCurrentDay(){
        FileConfiguration config = plugin.getConfig();
        return config.getInt("day", 1);
    }

    public void resetDayAndTime(){
        FileConfiguration config = plugin.getConfig();
        config.set("day", 1);
        config.set("time-day", System.currentTimeMillis());
        plugin.saveConfig();
        resetAllBonuses();
    }

    public int getCurrentLimit() {
        FileConfiguration config = plugin.getConfig();
        int day = config.getInt("day");
        int limitBase = config.getInt("limit-base");
        return day * limitBase;
    }
}
