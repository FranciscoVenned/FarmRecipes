package com.venned.farmrecipes.util;

import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public class RecipeUtil {

    public static ItemStack createRecipePieceItem(Material farmingCrop, int amount) {
        ItemStack itemStack = new ItemStack(Material.valueOf(FarmRecipes.getInstance().getConfig().getString("recipe-piece.material").toUpperCase()), amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatUtil.parseString(FarmRecipes.getInstance().getConfig().getString("recipe-piece.name").replace("<crop>", farmingCrop.toString())));
        List<String> lore = FarmRecipes.getInstance().getConfig().getStringList("recipe-piece.lore");
        lore.replaceAll(line -> ChatUtil.parseString(line.replace("<crop>", farmingCrop.toString())));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(itemStack);

        System.out.println("Guardando Name " +  farmingCrop.toString());

        nbtItem.setString("farmingrecipe_piece", farmingCrop.toString());
        itemStack = nbtItem.getItem();

        return itemStack;
    }

    public static void removeRecipePieces(Player player, String cropType, int amountToRemove) {
        int remainingAmount = amountToRemove;
        for (ItemStack item : player.getInventory().getContents()) {
            if (remainingAmount <= 0) break;

            if (item != null && item.hasItemMeta()) {
                NBTItem nbt = new NBTItem(item);
                if (nbt.hasKey("farmingrecipe_piece") && nbt.getString("farmingrecipe_piece").equals(cropType)) {
                    int currentAmount = item.getAmount();
                    if (currentAmount <= remainingAmount) {
                        player.getInventory().remove(item);
                        remainingAmount -= currentAmount;
                    } else {
                        item.setAmount(currentAmount - remainingAmount);
                        remainingAmount = 0;
                    }
                }
            }
        }
    }

    public static void createRecipeItem(String material, Player player){
        Material cropType = Material.valueOf(material.toUpperCase());
        Optional<IFarmingCrop> farmingCrop = FarmRecipes.getInstance().getFarmingUtil().getIFarmingCropFromMaterial(cropType);
        ItemStack itemStack = (new ItemBuilder())
                .setName(FarmRecipes.getInstance().getConfig().getString("recipe-item.name").replace("<crop>", ((IFarmingCrop)farmingCrop.get()).getName()))
                .setLore(ChatUtil.parseList(FarmRecipes.getInstance().getConfig().getStringList("recipe-item.lore"), "<crop>", ((IFarmingCrop)farmingCrop.get()).getName()))
                .setMaterial(Material.valueOf(FarmRecipes.getInstance().getConfig().getString("recipe-item.material").toUpperCase()))
                .setData((short) FarmRecipes.getInstance().getConfig().getInt("recipe-item.data"))
                .build(FarmRecipes.getInstance().getConfig().getBoolean("recipe-item.glow"));

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("farmingrecipe", ((IFarmingCrop)farmingCrop.get()).getId());
        itemStack = nbtItem.getItem();
        player.getInventory().addItem(new ItemStack[]{itemStack});
    }
}
