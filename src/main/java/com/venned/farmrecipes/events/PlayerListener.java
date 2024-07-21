package com.venned.farmrecipes.events;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import com.venned.farmrecipes.util.ChatUtil;
import de.tr7zw.nbtapi.NBTItem;
//import me.fullpage.mantichoes.wrappers.HarvesterHoe;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import java.util.Optional;

public class PlayerListener implements Listener
{
    private final FarmRecipes plugin;

    public PlayerListener(final FarmRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandProcess(final PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/is farming") || event.getMessage().equalsIgnoreCase("/is farm")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("isfarm open");
        }
    }

    @EventHandler
    public void onInventoryClickEvent(final InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) {
            return;
        }
        final String invName = ChatUtil.parseString(this.plugin.getConfig().getString("inventory.name"));
        if (event.getView().getTitle().equalsIgnoreCase(invName)) {
            event.setCancelled(true);
        }
    }



    @EventHandler
    public void onRedeemCropItem(final PlayerInteractEvent event) {
        final ItemStack item = event.getItem();
        final Player player = event.getPlayer();
        if (item == null || event.getAction().name().startsWith("LEFT_")) {
            return;
        }
        final NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey("farmingrecipe")) {
            return;
        }
        final Island islandAt = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
        if (islandAt == null || !islandAt.isMember(SuperiorSkyblockAPI.getPlayer(player))) {
            player.sendMessage("�cRedeem on your island");
            return;
        }
        final Optional<IFarmingCrop> optionalIFarmingCrop = this.plugin.getFarmingUtil().getIFarmingCropFromMaterial(Material.valueOf(nbtItem.getString("farmingrecipe").toUpperCase()));
        if (!optionalIFarmingCrop.isPresent()) {
            return;
        }
        final Optional<IFarmingCrop> nextCrop = this.plugin.getFarmingUtil().getNextUnlockableFarmingCrop(islandAt.getUniqueId());
        if (!nextCrop.isPresent()) {
            return;
        }
        final IFarmingCrop farmingCrop = optionalIFarmingCrop.get();
        final boolean unlocked = farmingCrop.isUnlocked(this.plugin, islandAt.getUniqueId());
        final boolean isNextAndRecipeEqual = nextCrop.get().getId().equals(farmingCrop.getId());
        if (unlocked) {
            player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.already-unlocked")));
        }
        else if (!isNextAndRecipeEqual) {
            player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.crop-locked").replace("<crop>", nextCrop.get().getName())));
        }
        else {
            this.removeItem(player);
            this.plugin.getDataFile().set(farmingCrop.getDataPath(islandAt.getUniqueId()), (Object)farmingCrop.getIslandCropsHarvestedRequired(this.plugin));
            this.plugin.saveDataFile();
            player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.used-recipe").replace("<crop>", farmingCrop.getName())));
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        final ItemStack itemInHand = event.getItem();
        if (itemInHand == null || !event.getAction().equals((Object) Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        final Island islandAt = SuperiorSkyblockAPI.getIslandAt(clickedBlock.getLocation());
        if (islandAt == null || !islandAt.isMember(SuperiorSkyblockAPI.getPlayer(event.getPlayer()))) {
            return;
        }
        
        //final int itemData = itemInHand.getData().getData();
        //System.out.println("Item Data " + itemData);
       // final String farmingCorpPath = itemInHand.getType().name().toLowerCase() + ((itemData > 0) ? (":" + itemData) : "");
        final String farmingCorpPath = itemInHand.getType().name().toLowerCase();
       //System.out.println("farmingCorp Path " + farmingCorpPath);
        final String farmingCorpKey = this.plugin.getConfig().getString("farming-crop-seeds-map." + farmingCorpPath, (String)null);

       // System.out.println("farmingCorpKey " + farmingCorpKey);

        if (farmingCorpKey == null) {
            return;
        }
        Material m;
        try {
            m = Material.valueOf(farmingCorpKey.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            this.plugin.getLogger().severe("### " + farmingCorpKey.toUpperCase() + " could not be parsed to a material!! ###");
            return;
        }

       // System.out.println("Material " + m.toString());

        final Optional<IFarmingCrop> iFarmingCrop = this.plugin.getFarmingUtil().getIFarmingCropFromMaterial(m);
        if (!iFarmingCrop.isPresent()) {
            return;
        }

      //  System.out.println("IsFarming Crop " + iFarmingCrop.toString());

        final boolean isFarmable = this.plugin.getFarmingUtil().hasIslandUnlockedCrop(iFarmingCrop.get(), islandAt.getUniqueId());

      //  System.out.println("IsFarmable " + isFarmable);

        if (!isFarmable) {
            event.setCancelled(true);
            final Player player = event.getPlayer();
            final Optional<IFarmingCrop> farmingCropOptional = this.plugin.getFarmingUtil().getNextUnlockableFarmingCrop(islandAt.getUniqueId());
            String crop = "???";
            if (farmingCropOptional.isPresent()) {
                crop = farmingCropOptional.get().getName();
            }
            player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.crop-locked").replace("<crop>", crop)));
        } else {

            String path = islandAt.getUniqueId().toString() + ".amount";
            String pathBonus = islandAt.getUniqueId().toString() + ".bonus";
            int currentAmount = this.plugin.getDataFile().getInt(path, 0);
            int bonus = this.plugin.getDataFile().getInt(pathBonus, 0);
            int limit = this.plugin.getDayLimitManager().getCurrentLimit() + bonus;
            if (currentAmount >= limit) {
                event.getPlayer().sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.limit-reached")));
                event.setCancelled(true);
            } else {
                this.plugin.getDataFile().set(path, currentAmount + 1);
                this.plugin.saveDataFile();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        final ItemStack itemInHand = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
        final Island islandAt = SuperiorSkyblockAPI.getIslandAt(block.getLocation());
        if (islandAt == null || !islandAt.isMember(SuperiorSkyblockAPI.getPlayer(player.getUniqueId()))) {
            return;
        }
        final Material farmingCorpId = block.getType();
      //  System.out.println("farmingcorpId " + farmingCorpId.toString());
      //  System.out.println("contains " + this.plugin.getFarmingUtil().getFarmingBlocks().toString());
        if (!this.plugin.getFarmingUtil().getFarmingBlocks().contains(farmingCorpId)) {
            return;
        }
      //  System.out.println("Si contenida");
        final Optional<IFarmingCrop> optionalIFarmingCrop = this.plugin.getFarmingUtil().getNextUnlockableFarmingCrop(islandAt.getUniqueId());
        if (!optionalIFarmingCrop.isPresent()) {
            return;
        }
        final IFarmingCrop ifarmingCrop = optionalIFarmingCrop.get();
        if (ifarmingCrop.isDefault()) {
            return;
        }
      //  System.out.println("Si contenida2");
        boolean isCrop = false;
        boolean isCropFullyGrown = false;
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Ageable) {
            Ageable ageable = (Ageable) blockData;
            isCrop = true;
            isCropFullyGrown = ageable.getAge() == ageable.getMaximumAge();
        }
      //  System.out.println("Si contenida3");
        final boolean doesNextCropEqualUnlockedCrop = ifarmingCrop.getHarvestType().equals(farmingCorpId);
      //  System.out.println("HarvestType " + ifarmingCrop.getHarvestType().toString());
      //  System.out.println("farmingcorpId " + farmingCorpId.toString());
      //  System.out.println("isCrop " + isCrop);
      //  System.out.println("isCropFullyGown " + isCropFullyGrown);
        if (doesNextCropEqualUnlockedCrop) {
            if (isCrop && !isCropFullyGrown) {
                return;
            }
        //    System.out.println("Si contenida4");
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
    }

    private void removeItem(final Player player) {
        final ItemStack itemStack = player.getItemInHand();
        itemStack.setAmount(itemStack.getAmount() - 1);
        if (itemStack.getAmount() < 0) {
            player.getInventory().clear(player.getInventory().getHeldItemSlot());
        }
    }

   /* private boolean isUsingAHarvesterHoe(final ItemStack itemInHand, final Player player) {
        final HarvesterHoe hoe = new HarvesterHoe(itemInHand, player);
        return hoe.isValid();
    }

    */

}