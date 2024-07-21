package com.venned.farmrecipes.inventory;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import com.venned.farmrecipes.util.ChatUtil;
import com.venned.farmrecipes.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Objects;

public class ISFarmInventory {
    public ISFarmInventory() {
    }

    public static void openInventory(FarmRecipes plugin, Player player) {
        Island islandAt = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
        if (islandAt != null && islandAt.isMember(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()))) {
            int size = plugin.getConfig().getInt("inventory.size");
            String invName = ChatUtil.parseString(plugin.getConfig().getString("inventory.name"));
            boolean fillInventory = plugin.getConfig().getBoolean("inventory.fill-inventory");
            Inventory inventory = Bukkit.createInventory((InventoryHolder)null, size, invName);
            if (fillInventory) {
                Material material;
                try {
                    material = Material.valueOf(Objects.requireNonNull(plugin.getConfig().getString("inventory.filler-item.material")).toUpperCase());
                } catch (IllegalArgumentException var11) {
                    plugin.getLogger().severe("### Filler item is not configured properly!! ###");
                    material = Material.BLACK_STAINED_GLASS;
                }

                int data = plugin.getConfig().getInt("inventory.filler-item.data");
                ItemStack fillerItem = (new ItemBuilder()).setMaterial(material).setData(data).build(false);

                for(int slot = 0; slot < inventory.getSize(); ++slot) {
                    inventory.setItem(slot, fillerItem);
                }
            }

            for (IFarmingCrop iFarmingCrop : plugin.getFarmingUtil().getFarmingCropMap().values()) {
                inventory.setItem(iFarmingCrop.getInventorySlot(plugin), iFarmingCrop.getFarmingCropItem(plugin, islandAt.getUniqueId(), plugin.getNumberFormat()));
            }

            player.getOpenInventory().close();
            player.openInventory(inventory);
        } else {
            player.sendMessage(ChatUtil.parseString(plugin.getConfig().getString("messages.not-on-island")));
        }
    }
}