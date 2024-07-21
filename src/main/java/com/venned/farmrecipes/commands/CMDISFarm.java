package com.venned.farmrecipes.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.venned.farmrecipes.FarmRecipes;
import com.venned.farmrecipes.farmingcrops.IFarmingCrop;
import com.venned.farmrecipes.inventory.ISFarmInventory;
import com.venned.farmrecipes.util.ChatUtil;
import com.venned.farmrecipes.util.ItemBuilder;
import com.venned.farmrecipes.util.RecipeUtil;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CMDISFarm implements CommandExecutor {
    private final FarmRecipes plugin;

    public CMDISFarm(FarmRecipes plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("isfarm.help")) {
                sender.sendMessage("§6§lISFARM HELP v" + this.plugin.getDescription().getVersion());
                sender.sendMessage("§6§l/isfarm help - Open help");
                sender.sendMessage("§6§l/isfarm reload - Reload configs");
                sender.sendMessage("§6§l/isfarm open - Open slayer gui");
                sender.sendMessage("§6§l/isfarm increaser <player> <amount>");
                sender.sendMessage("§6§l/isfarm resetDay");
                sender.sendMessage("§6§l/isfarm piecerecipe <player> <recipe> <amount>");
                sender.sendMessage("§6§l/isfarm give <player> <recipe> - Give player an farming recipe");
                sender.sendMessage("§6§l/isfarm set <player> <material> <amount> - Change someones crops harvested");
                return true;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                if (sender.hasPermission("isfarm.help")) {
                    sender.sendMessage("§6§lISFARM HELP v" + this.plugin.getDescription().getVersion());
                    sender.sendMessage("§6§l/isfarm help - Open help");
                    sender.sendMessage("§6§l/isfarm reload - Reload configs");
                    sender.sendMessage("§6§l/isfarm open - Open slayer gui");
                    sender.sendMessage("§6§l/isfarm give <player> <recipe> - Give player an farming recipe");
                    sender.sendMessage("§6§l/isfarm set <player> <key> <amount> - Change someones crops harvested");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("isfarm.reload")) {
                    this.plugin.reloadConfig();
                    this.plugin.reloadDataFile();
                    this.plugin.getFarmingUtil().initFarmingCrops();
                    sender.sendMessage("§a§l(!) §aYou've successfully reloaded all of the configs");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("open") && sender instanceof Player) {
                Player player = (Player)sender;
                ISFarmInventory.openInventory(this.plugin, player);
                return true;
            }
        } else if (args[0].equalsIgnoreCase("piecerecipe")) {
            if (args.length < 4) {
                sender.sendMessage("§6§l/isfarm piecerecipe <player> <crop> <amount>");
                return true;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage("§c§l(!) §cTarget '" + args[1] + "' not online...");
                return true;
            }

            Material cropType = Material.valueOf(args[2].toUpperCase());// El tipo de cultivo, por ejemplo: wheat, potatoes, carrots, etc.
            int amount;
            try {
                amount = Integer.parseInt(args[3]); // La cantidad de piezas de receta a dar al jugador
            } catch (NumberFormatException e) {
                sender.sendMessage("§c§l(!) §cCouldn't parse '" + args[3] + "' as an integer...");
                return true;
            }

            Optional<IFarmingCrop> farmingCrop = this.plugin.getFarmingUtil().getIFarmingCropFromMaterial(cropType);

            if (!farmingCrop.isPresent()) {
                sender.sendMessage("§c§l(!) §cFarming crop '" + cropType + "' was not found!");
                return true;
            }
            ItemStack pieceItem = RecipeUtil.createRecipePieceItem(cropType, amount);
            player.getInventory().addItem(pieceItem);
            sender.sendMessage("§a§l✔ §aYou gave " + player.getName() + " " + amount + " piece(s) of " + farmingCrop.get().getName() + " recipe.");
            return true;
        }

        else if (args[0].equalsIgnoreCase("resetday")){

            if(!sender.hasPermission("farmrecipes.admin")){
                sender.sendMessage("§c§l(!) §cNo Permissions");
                return true;
            }

            this.plugin.getDayLimitManager().resetDayAndTime();
            sender.sendMessage("§aDay and time have been reset, and all bonuses have been cleared.");
            return true;

        }
        else if (args[0].equalsIgnoreCase("increaser")) {

            if(!sender.hasPermission("farmrecipes.admin")){
                sender.sendMessage("§c§l(!) §cNo Permissions");
                return true;
            }

            if(args.length < 3){
                sender.sendMessage("§c§l(!) §cUse Correc format /is increaser Player Amount");
                return true;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage("§c§l(!) §cTarget '" + args[1] + "' not online...");
                return true;
            }

            int value;
            try {
                value = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c§l(!) §cCouldn't parse '" + args[2] + "' as an integer...");
                return true;
            }

            List<String> lore = new ArrayList<>();
            lore.add("&7Right-click to increase your bonus by " + value);

            ItemStack increaserItem = (new ItemBuilder())
                    .setName(ChatUtil.parseString("&aBonus Increaser"))
                    .setLore(ChatUtil.parseList(lore))
                    .setMaterial(Material.PAPER)
                    .build(this.plugin.getConfig().getBoolean("recipe-item.glow"));

            NBTItem nbtItem = new NBTItem(increaserItem);
            nbtItem.setInteger("bonusIncreaser", value);
            increaserItem = nbtItem.getItem();

            player.getInventory().addItem(increaserItem);
            sender.sendMessage("§aYou sent " + player.getName() + " a bonus increaser worth " + value);
            player.sendMessage("§aYou received a bonus increaser worth " + value);
            return true;

        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("§c§l(!) §c Target '" + args[1] + "' not online...");
                    return true;
                }

                Material material = Material.valueOf(args[2].toUpperCase());
                Optional<IFarmingCrop> farmingCrop = this.plugin.getFarmingUtil().getIFarmingCropFromMaterial(material);
                if (!farmingCrop.isPresent()) {
                    sender.sendMessage("§c§l(!) §cFarming crop " + material + " was not found!");
                    return true;
                }

                ItemStack itemStack = (new ItemBuilder())
                        .setName(this.plugin.getConfig().getString("recipe-item.name").replace("<crop>", ((IFarmingCrop)farmingCrop.get()).getName()))
                        .setLore(ChatUtil.parseList(this.plugin.getConfig().getStringList("recipe-item.lore"), "<crop>", ((IFarmingCrop)farmingCrop.get()).getName()))
                        .setMaterial(Material.valueOf(this.plugin.getConfig().getString("recipe-item.material").toUpperCase()))
                        .setData((short) this.plugin.getConfig().getInt("recipe-item.data"))
                        .build(this.plugin.getConfig().getBoolean("recipe-item.glow"));

                NBTItem nbtItem = new NBTItem(itemStack);
                nbtItem.setString("farmingrecipe", ((IFarmingCrop)farmingCrop.get()).getId());
                itemStack = nbtItem.getItem();
                player.getInventory().addItem(new ItemStack[]{itemStack});
                sender.sendMessage("You sent " + player.getName() + " a " + ((IFarmingCrop)farmingCrop.get()).getName() + " recipe");
                player.sendMessage(ChatUtil.parseString(this.plugin.getConfig().getString("messages.recipe-received").replace("<crop>", ((IFarmingCrop)farmingCrop.get()).getName())));
                return true;
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("set")) {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage("§c§l(!) §c Target '" + args[1] + "' not online...");
                return true;
            }

            Island playerIsland = SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland();
            if (playerIsland != null && playerIsland.getUniqueId() != null) {
                String key = args[2].toLowerCase();
                String path = playerIsland.getUniqueId().toString() + "." + key;
                if (!this.plugin.getDataFile().isSet(path)) {
                    sender.sendMessage("§c§l(!) §cThe path '" + path + "' is not set for the island. Wait until it is, then update!");
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException var11) {
                    sender.sendMessage("§c§l(!) §cCouldn't parse '" + args[3] + "' an integer...");
                    return true;
                }

                this.plugin.getDataFile().set(path, amount);
                this.plugin.saveDataFile();
                sender.sendMessage("§a§l §aYou set set " + player.getName() + "'s island '" + key + "' §avalue to §2" + amount);
                return true;
            }

            sender.sendMessage("§c§l(!) §c" + player.getName() + " §cdoesn't have an island...");
            return true;
        }

        return false;
    }
}
