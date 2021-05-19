package de.rexlmanu.bingo.shared.itemstack;

import org.bukkit.Material;

public interface Item {

    static ItemStackBuilder builder(Material material) {
        return new ItemStackBuilder(material);
    }

}
