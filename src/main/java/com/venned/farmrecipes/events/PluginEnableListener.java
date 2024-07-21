package com.venned.farmrecipes.events;

import com.venned.farmrecipes.FarmRecipes;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class PluginEnableListener implements Listener
{
    private final FarmRecipes plugin;

    public PluginEnableListener(final FarmRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginEnable(final PluginEnableEvent event) {
        final String pluginName = event.getPlugin().getName();
        if (pluginName.equalsIgnoreCase("ManticHoes")) {
            Bukkit.getServer().getPluginManager().registerEvents((Listener)new ManticHoeListener(this.plugin), (Plugin)this.plugin);
            Bukkit.getServer().getLogger().info("### Enabled ManticHoes support (2) ###");
        }
        else if (pluginName.equalsIgnoreCase("JetsMinions")) {
            Bukkit.getServer().getPluginManager().registerEvents((Listener)new MinionListener(this.plugin), (Plugin)this.plugin);
            Bukkit.getServer().getLogger().info("### Enabled JetsMinions support (2) ###");
        }
    }
}
