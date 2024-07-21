package com.venned.farmrecipes.events;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerIncreaserListener implements Listener {

    FarmRecipes plugin;

    public PlayerIncreaserListener(FarmRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.PAPER) {
            return;
        }

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey("bonusIncreaser")) {
            return;
        }

        int bonusValue = nbtItem.getInteger("bonusIncreaser");
        Player player = event.getPlayer();
        Island island = SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland();
        if (island == null) {
            player.sendMessage("§cYou don't have an island...");
            return;
        }

        String path = island.getUniqueId().toString() + ".bonus";
        int currentBonus = this.plugin.getDataFile().getInt(path, 0);
        this.plugin.getDataFile().set(path, currentBonus + bonusValue);
        this.plugin.saveDataFile();

        player.getInventory().remove(item);
        player.sendMessage("§aYour bonus has been increased by " + bonusValue);
    }
}
