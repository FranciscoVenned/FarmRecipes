package com.venned.farmrecipes.events;

import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.util.RecipeUtil;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class PlayerRecipeListener implements Listener {

    private final Map<Player, BukkitTask> clickTasks = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand != null && itemInHand.getType() != Material.AIR) {
            if (itemInHand.hasItemMeta()) {
                ItemMeta meta = itemInHand.getItemMeta();
                if (meta != null && meta.hasDisplayName() && meta.hasLore()) {
                    // Check if the item has the farmingrecipe_piece NBT tag
                    NBTItem nbtItem = new NBTItem(itemInHand);
                    if (nbtItem.hasKey("farmingrecipe_piece")) {
                        // Get the type of crop from the NBT
                        String cropType = nbtItem.getString("farmingrecipe_piece");

                        // Check if the player already has a task for this interaction
                        if (clickTasks.containsKey(player)) {
                            player.sendMessage("§cPlease wait before combining another recipe piece.");
                            return;
                        }

                        clickTasks.put(player, new BukkitRunnable() {
                            @Override
                            public void run() {
                                int totalPieces = 0;
                                for (ItemStack item : player.getInventory().getContents()) {
                                    if (item != null && item.hasItemMeta()) {
                                        NBTItem nbt = new NBTItem(item);
                                        if (nbt.hasKey("farmingrecipe_piece") && nbt.getString("farmingrecipe_piece").equals(cropType)) {
                                            totalPieces += item.getAmount();
                                        }
                                    }
                                }

                                if (totalPieces >= 7) {
                                    // Remove 7 pieces and give the recipe item
                                    RecipeUtil.removeRecipePieces(player, cropType, 7);
                                    RecipeUtil.createRecipeItem(cropType, player);
                                    player.sendMessage("§aYou have combined 7 pieces into a recipe for " + cropType + ".");
                                } else {
                                    player.sendMessage("§cYou need at least 7 pieces of " + cropType + " to combine into a recipe.");
                                }

                                clickTasks.remove(player);
                            }
                        }.runTaskLater(FarmRecipes.getInstance(), 20 * 3)); // Delay of 3 seconds (adjust as needed)
                    }
                }
            }
        }
    }
}
