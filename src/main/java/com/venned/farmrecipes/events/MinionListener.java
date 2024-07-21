package com.venned.farmrecipes.events;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import com.venned.farmrecipes.util.ChatUtil;
import me.jet315.minions.events.FarmerFarmEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class MinionListener implements Listener
{
    private final FarmRecipes plugin;

    public MinionListener(final FarmRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMinionBlockBreak(final FarmerFarmEvent event) {
        final Location location = event.getMinion().getMinion().getLoc();
        if (location == null) {
            this.plugin.getLogger().severe("### Minion location is null! ###");
            return;
        }
        final Island islandAt = SuperiorSkyblockAPI.getIslandAt(location);
        if (islandAt == null) {
            return;
        }
        for (final ItemStack item : event.getItems()) {
            final String materialString = this.plugin.getConfig().getString("farming-crop-seeds-map-2." + item.getType().name().toLowerCase(), (String)null);
            if (materialString == null) {
                continue;
            }
            final Material farmingCorpId = Material.valueOf(materialString.toUpperCase());
            if (!this.plugin.getFarmingUtil().getFarmingBlocks().contains(farmingCorpId)) {
                return;
            }
            final Optional<IFarmingCrop> optionalIFarmingCrop = this.plugin.getFarmingUtil().getNextUnlockableFarmingCrop(islandAt.getUniqueId());
            if (!optionalIFarmingCrop.isPresent()) {
                return;
            }
            final IFarmingCrop ifarmingCrop = optionalIFarmingCrop.get();
            if (ifarmingCrop.isDefault()) {
                return;
            }
            final boolean doesNextCropEqualUnlockedCrop = ifarmingCrop.getHarvestType().equals((Object)farmingCorpId);
            if (!doesNextCropEqualUnlockedCrop) {
                continue;
            }
            final int newAmount = 1;
            this.plugin.getDataFile().set(ifarmingCrop.getDataPath(islandAt.getUniqueId()), (Object)(ifarmingCrop.getIslandCropsHarvested(this.plugin, islandAt.getUniqueId()) + newAmount));
            this.plugin.saveDataFile();
            final boolean unlockedCrop = this.plugin.getFarmingUtil().hasIslandUnlockedCrop(ifarmingCrop, islandAt.getUniqueId());
            if (!unlockedCrop) {
                continue;
            }
            final Optional<IFarmingCrop> nextCropOptional = this.plugin.getFarmingUtil().getNextUnlockableFarmingCrop(islandAt.getUniqueId());
            final String nextCrop = (nextCropOptional.isPresent() ? nextCropOptional.get().getName() : ifarmingCrop.getHarvestType().name()).toLowerCase();
            final String message = ChatUtil.parseString(this.plugin.getConfig().getString("messages.crop-unlocked").replace("<crop>", ChatUtil.parseString(nextCrop)));
            islandAt.sendMessage(message, new UUID[0]);
        }
    }
}

