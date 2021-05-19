package de.rexlmanu.bingo.shared.itemstack;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Data
@Accessors(fluent = true)
public class ItemStackBuilder {

    private Material material;
    private int amount;
    private List<String> lore;
    private Component displayName;
    private List<ItemFlag> itemFlags;
    private boolean unbreakable;
    private boolean glow;

    ItemStackBuilder(Material material) {
        this.material = material;
        this.amount = 1;
        this.lore = Lists.newArrayList();
        this.itemFlags = Lists.newArrayList();
        this.unbreakable = false;
        this.glow = false;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.material, amount);
        itemStack.setLore(lore);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(this.displayName);
        itemFlags.forEach(itemMeta::addItemFlags);
        itemMeta.setUnbreakable(this.unbreakable);
        if (this.glow) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
