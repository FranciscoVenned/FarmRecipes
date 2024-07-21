package com.venned.farmrecipes.events;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import com.venned.farmrecipes.util.ChatUtil;
import me.fullpage.mantichoes.api.events.HoePreUseEvent;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.material.Crops;

import java.util.Optional;

public class HoeListener implements Listener
{
    private final FarmRecipes plugin;

    public HoeListener(final FarmRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHoeUse(final HoePreUseEvent event) {


        final Block block = event.getInitialBlock();
        final int blockData = block.getState().getData().getData();
        final Player player = event.getPlayer();
        final Island islandAt = SuperiorSkyblockAPI.getIslandAt(block.getLocation());
        if (islandAt == null || !islandAt.isMember(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()))) {
            return;
        }
        final Material farmingCorpId = block.getState().getType();
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
        final boolean isCropsInstance = block.getState().getData() instanceof Crops;
        if (isCropsInstance) {
            final Crops crops = new Crops(block.getState().getType(), block.getState().getData().getData());
            if (!crops.getState().equals((Object) CropState.RIPE)) {
                return;
            }
        }
        final boolean doesNextCropEqualUnlockedCrop = ifarmingCrop.getHarvestType().equals((Object)farmingCorpId);
        if (doesNextCropEqualUnlockedCrop) {
            final int newAmount = 1;
            this.plugin.getDataFile().set(ifarmingCrop.getDataPath(islandAt.getUniqueId()), (Object)(ifarmingCrop.getIslandCropsHarvested(this.plugin, islandAt.getUniqueId()) + newAmount));
            this.plugin.saveDataFile();
            final boolean unlockedCrop = this.plugin.getFarmingUtil().hasIslandUnlockedCrop(ifarmingCrop, islandAt.getUniqueId());
            if (unlockedCrop) {
                final Optional<IFarmingCrop> nextCropOptional = this.plugin.getFarmingUtil().getNextUnlockableFarmingCrop(islandAt.getUniqueId());
                final String nextCrop = (nextCropOptional.isPresent() ? nextCropOptional.get().getName() : ifarmingCrop.getHarvestType().name()).toLowerCase();
                player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.crop-unlocked").replace("<crop>", ChatUtil.parseString(nextCrop))));
            }
        }
        else if (!this.plugin.getFarmingUtil().hasIslandUnlockedCrop(farmingCorpId, islandAt.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.crop-locked").replace("<crop>", ChatUtil.parseEnumString(ifarmingCrop.getName()))));
        }
    }
}