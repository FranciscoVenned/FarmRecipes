package com.venned.farmrecipes.events;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import com.venned.farmrecipes.util.ChatUtil;
import me.fullpage.mantichoes.api.events.HoePreUseEvent;
import me.fullpage.mantichoes.api.events.HoeUseEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ManticHoeListener implements Listener
{
    private final FarmRecipes plugin;

    public ManticHoeListener(final FarmRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHoePreUse(final HoePreUseEvent event) {
        final Block block = event.getInitialBlock();
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
        final boolean b = this.plugin.getFarmingUtil().hasIslandUnlockedCrop(farmingCorpId, islandAt.getUniqueId());
        if (!b) {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.crop-locked").replace("<crop>", ChatUtil.parseEnumString(ifarmingCrop.getName()))));
        }
    }

    @EventHandler
    public void onHoeUse(final HoeUseEvent event) {
        final Map<Block, Material> blockBrokenMap = (Map<Block, Material>)event.getBlockMaterialMap();
        final Collection<Material> brokenMaterials = new ArrayList<Material>(blockBrokenMap.values());
        for (final Material material : brokenMaterials) {
            final Player player = event.getPlayer();
            final Island islandAt = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
            if (islandAt == null || !islandAt.isMember(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()))) {
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
            final boolean doesNextCropEqualUnlockedCrop = ifarmingCrop.getHarvestType().equals((Object)material);
            if (!doesNextCropEqualUnlockedCrop) {
                continue;
            }
            final int dropAmount = 1;
            this.plugin.getDataFile().set(ifarmingCrop.getDataPath(islandAt.getUniqueId()), (Object)(ifarmingCrop.getIslandCropsHarvested(this.plugin, islandAt.getUniqueId()) + dropAmount));
            this.plugin.saveDataFile();
            final boolean unlockedCrop = this.plugin.getFarmingUtil().hasIslandUnlockedCrop(ifarmingCrop, islandAt.getUniqueId());
            if (!unlockedCrop) {
                continue;
            }
            final Optional<IFarmingCrop> nextCropOptional = this.plugin.getFarmingUtil().getNextUnlockableFarmingCrop(islandAt.getUniqueId());
            final String nextCrop = (nextCropOptional.isPresent() ? nextCropOptional.get().getName() : ifarmingCrop.getHarvestType().name()).toLowerCase();
            player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.crop-unlocked").replace("<crop>", ChatUtil.parseString(nextCrop))));
        }
    }
}
