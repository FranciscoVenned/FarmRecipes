package com.venned.farmrecipes.farmingcrops;

import com.venned.farmrecipes.FarmRecipes;
import org.bukkit.Material;

public class FarmingCrop implements IFarmingCrop
{
    private final String id;
    private final String name;
    private Material harvestType;
    private Material itemStackType;
    private final byte data;
    private final int priority;
    private final boolean def;

    public FarmingCrop(final FarmRecipes plugin, final String id, final boolean def) {
        this.id = id;
        this.name = plugin.getConfig().getString("farming-crops." + id + ".name");
        this.def = def;
        try {
            this.harvestType = Material.valueOf(plugin.getConfig().getString("farming-crops." + id + ".to-harvest").toUpperCase());
        }
        catch (IllegalArgumentException e) {
            this.harvestType = Material.BEDROCK;
            plugin.getLogger().warning("### Couldn't register EntityType for '" + this.getId() + "' ###");
        }
        try {
            final String itemType = plugin.getConfig().getString("farming-crops." + id + ".item");
            this.itemStackType = Material.valueOf(itemType.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            this.itemStackType = Material.BEDROCK;
            plugin.getLogger().warning("### Couldn't register EntityType for 2 '" + this.getId() + "' ###");
        }
        this.data = (byte)plugin.getConfig().getInt("farming-crops." + id + ".data");
        this.priority = plugin.getConfig().getInt("farming-crops." + id + ".priority");
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Material getHarvestType() {
        return this.harvestType;
    }

    @Override
    public Material getItemStackType() {
        return this.itemStackType;
    }

    @Override
    public byte getData() {
        return this.data;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean isDefault() {
        return this.def;
    }

    @Override
    public String toString() {
        return "FarmingCrop{id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", harvestType=" + this.harvestType + ", itemStackType=" + this.itemStackType + ", data=" + this.data + ", priority=" + this.priority + ", def=" + this.def + '}';
    }
}
