package fr.red_spash.crazygames;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemStackBuilder {

    private final ItemStack itemStack;

    public ItemStackBuilder(Material m) {
        this(m, 1);
    }

    public ItemStackBuilder(ItemStack is) {
        this.itemStack = is;
    }

    public ItemStackBuilder(Material m, int amount) {
        this.itemStack = new ItemStack(m, amount);
    }

    public ItemStackBuilder setDurability(short dur) {
        ItemMeta im = this.getItemMeta();
        if(im instanceof Damageable damageable){
            damageable.setDamage(dur);
            this.itemStack.setItemMeta(damageable);
        }
        return this;
    }


    public ItemStackBuilder setLore(String... lore) {
        ItemMeta im = this.getItemMeta();
        im.setLore(Arrays.asList(lore));
        this.itemStack.setItemMeta(im);
        return this;
    }


    public ItemStackBuilder setLore(List<String> lore) {
        ItemMeta im = this.getItemMeta();
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder addLoreLine(String line) {
        ItemMeta im = this.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder setName(String name) {
        ItemMeta im = this.getItemMeta();
        im.setDisplayName(name);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStackBuilder hideAttributes() {
        ItemMeta im = this.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
        this.itemStack.setItemMeta(im);
        return this;
    }

    private ItemMeta getItemMeta() {
        if(itemStack.hasItemMeta()){
            return itemStack.getItemMeta();
        }
        this.itemStack.setItemMeta(Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
        return this.itemStack.getItemMeta();
    }

    public ItemStackBuilder addEnchant(Enchantment ench, int level) {
        ItemMeta im = this.getItemMeta();
        im.addEnchant(ench, level, true);
        this.itemStack.setItemMeta(im);
        return this;
    }

    public ItemStack toItemStack() {
        return itemStack;
    }


    public ItemStackBuilder setUnbreakable(boolean b) {
        ItemMeta im = this.getItemMeta();
        im.setUnbreakable(b);
        this.itemStack.setItemMeta(im);
        return this;
    }
}
