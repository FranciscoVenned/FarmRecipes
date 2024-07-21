package com.venned.farmrecipes.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder
{
    private String name;
    private List<String> lore;
    private Material material;
    private int data;
    private int amount;
    private ItemStack itemStack;

    public ItemBuilder() {
        this.name = null;
        this.lore = null;
        this.material = Material.APPLE;
        this.data = 0;
        this.amount = 1;
        this.itemStack = null;
    }

    public ItemBuilder(final String name) {
        this.name = null;
        this.lore = null;
        this.material = Material.APPLE;
        this.data = 0;
        this.amount = 1;
        this.itemStack = null;
        this.name = name;
    }

    public ItemBuilder(final ItemStack itemStack) {
        this.name = null;
        this.lore = null;
        this.material = Material.APPLE;
        this.data = 0;
        this.amount = 1;
        this.itemStack = null;
        this.data = itemStack.getData().getData();
        this.amount = itemStack.getAmount();
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setLore(final String... strings) {
        this.lore = Arrays.asList(strings);
        return this;
    }

    public ItemBuilder setLore(final List<String> lore) {
        if (this.lore == null) {
            this.lore = lore;
        }
        else {
            final List<String> newLore = new ArrayList<String>(this.lore);
            newLore.addAll(lore);
            this.lore = newLore;
        }
        return this;
    }

    public ItemBuilder setMaterial(final Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder setData(final int data) {
        this.data = data;
        return this;
    }

    public ItemBuilder setAmount(final int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build(final boolean glow) {
        final ItemStack itemStack = new ItemStack((this.itemStack == null) ? this.material : this.itemStack.getType(), this.amount, (short)this.data);
        final ItemMeta itemMeta = (this.itemStack == null) ? itemStack.getItemMeta() : this.itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        final String name = (this.name == null) ? itemStack.getItemMeta().getDisplayName() : this.name;
        final List<String> lore = (this.lore == null) ? itemStack.getItemMeta().getLore() : this.lore;
        if (name != null) {
            itemMeta.setDisplayName(ChatUtil.parseString(name));
        }
        if (lore != null) {
            itemMeta.setLore(ChatUtil.parseList((List)lore));
        }
        if (glow) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        else {
            itemMeta.removeEnchant(Enchantment.DURABILITY);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
