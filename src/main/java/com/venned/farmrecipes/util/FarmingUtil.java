package com.venned.farmrecipes.util;

import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.FarmingCrop;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import org.bukkit.Material;

import java.util.*;
import java.util.logging.Level;

public class FarmingUtil
{
    private final FarmRecipes plugin;
    private final Map<Material, IFarmingCrop> farmingCropMap;
    private final List<Material> farmingBlocks;

    public Map<Material, IFarmingCrop> getFarmingCropMap() {
        return this.farmingCropMap;
    }

    public FarmingUtil(final FarmRecipes plugin) {
        this.plugin = plugin;
        this.farmingCropMap = new LinkedHashMap<Material, IFarmingCrop>();
        this.farmingBlocks = new ArrayList<Material>();
    }

    public void initFarmingCrops() {
        if (!this.farmingCropMap.isEmpty()) {
            this.farmingCropMap.clear();
        }
        if (!this.farmingBlocks.isEmpty()) {
            this.farmingBlocks.clear();
        }
        final boolean[] def = new boolean[1];
        final IFarmingCrop[] farmingCrop = new IFarmingCrop[1];
        this.plugin.getConfig().getConfigurationSection("farming-crops").getKeys(false).forEach(key -> {
            def[0] = this.plugin.getConfig().getBoolean("farming-crops." + key + ".default", false);
            farmingCrop[0] = new FarmingCrop(this.plugin, key, def[0]);
            try {
                Material material = Material.valueOf(farmingCrop[0].getId().toUpperCase());
                this.farmingCropMap.put(material, farmingCrop[0]);
            } catch (IllegalArgumentException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Invalid material: " + farmingCrop[0].getId().toUpperCase());
            }
        });
        for (final IFarmingCrop iFarmingCrop : this.farmingCropMap.values()) {
            this.plugin.getLogger().info(iFarmingCrop.toString());
        }
        this.farmingBlocks.addAll(this.farmingCropMap.keySet());
        this.plugin.getLogger().info("### Loaded in " + this.farmingCropMap.keySet().size() + " farming crops settings ###");
    }

    public Optional<IFarmingCrop> getIFarmingCropFromMaterial(final Material material) {
        return Optional.ofNullable(this.farmingCropMap.get(material));
    }

    public boolean hasIslandUnlockedCrop(final IFarmingCrop iFarmingCrop, final UUID islandUUID) {
        if (iFarmingCrop.isDefault()) {
            return true;
        }
        if (islandUUID == null) {
            this.plugin.getLogger().severe("### IslandUUID parameter was null ###");
            return false;
        }
        final int islandCropHarvested = this.plugin.getDataFile().getInt(iFarmingCrop.getDataPath(islandUUID));
        return islandCropHarvested >= iFarmingCrop.getIslandCropsHarvestedRequired(this.plugin);
    }

    public boolean hasIslandUnlockedCrop(final Material material, final UUID islandUUID) {
        final IFarmingCrop farmingCrop = this.farmingCropMap.get(material);
        if (farmingCrop == null) {
            throw new NullPointerException("### Couldn't get farmingCrop from " + material + " ###");
        }
        if (farmingCrop.isDefault()) {
            return true;
        }
        if (islandUUID == null) {
            this.plugin.getLogger().severe("### IslandUUID parameter was null ###");
            return false;
        }
        final int islandCropHarvested = this.plugin.getDataFile().getInt(farmingCrop.getDataPath(islandUUID));
        return islandCropHarvested >= farmingCrop.getIslandCropsHarvestedRequired(this.plugin);
    }

    public Optional<IFarmingCrop> getNextUnlockableFarmingCrop(final UUID islandUUID) {
        return this.farmingCropMap.values().stream()
                .sorted(Comparator.comparing(IFarmingCrop::getPriority))
                .filter(iFarmingCrop -> !this.hasIslandUnlockedCrop(iFarmingCrop, islandUUID))
                .findFirst();
    }

    public List<Material> getFarmingBlocks() {
        return this.farmingBlocks;
    }
}