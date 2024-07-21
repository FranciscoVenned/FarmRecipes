package com.venned.farmrecipes.farmingcrops;

import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.util.ChatUtil;
import com.venned.farmrecipes.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface IFarmingCrop
{
    String getId();

    String getName();

    Material getHarvestType();

    Material getItemStackType();

    byte getData();

    int getPriority();

    boolean isDefault();

    default int getInventorySlot(final FarmRecipes plugin) {
        return plugin.getConfig().getInt("farming-crops." + this.getId().toLowerCase() + ".slot");
    }

    default ItemStack getFarmingCropItem(final FarmRecipes plugin, final UUID islandUUID, final NumberFormat numberFormat) {
        final String name = plugin.getConfig().getString("inventory.farming-crop-item.name").replace("<crop>", this.getName());
        final String toHarvestId = plugin.getConfig().getString("farming-crops." + this.getId().toLowerCase() + ".to-harvest");
        final String toHarvestName = plugin.getConfig().getString("farming-crops." + toHarvestId + ".name");
        final String status = this.isUnlocked(plugin, islandUUID) ? "&a&lUNLOCKED" : "&c&lLOCKED";
        final List<String> lore = this.isDefault() ? Arrays.asList("", status) : ChatUtil.parseList(plugin.getConfig().getStringList("inventory.farming-crop-item.lore"), "<crop>", ChatUtil.fixPluralName(toHarvestName), "<crops-harvested>", numberFormat.format(this.getIslandCropsHarvested(plugin, islandUUID)), "<crops-harvested-required>", numberFormat.format(this.getIslandCropsHarvestedRequired(plugin)), "<status>", status);
        return new ItemBuilder().setName(name).setLore(lore).setMaterial(this.getItemStackType()).setData(this.getData()).build(false);
    }

    default boolean isUnlocked(final FarmRecipes plugin, final UUID islandUUID) {
        return this.isDefault() || plugin.getFarmingUtil().hasIslandUnlockedCrop(this, islandUUID);
    }

    default String getDataPath(final UUID islandUUID) {
        return islandUUID.toString() + "." + this.getHarvestType().name().toLowerCase() + ":" + this.getData();
    }

    default int getIslandCropsHarvested(final FarmRecipes plugin, final UUID islandUUID) {
        return plugin.getDataFile().getInt(this.getDataPath(islandUUID));
    }

    default int getIslandCropsHarvestedRequired(final FarmRecipes plugin) {
        return plugin.getConfig().getInt("farming-crops." + this.getId().toLowerCase() + ".harvests-needed");
    }
}
