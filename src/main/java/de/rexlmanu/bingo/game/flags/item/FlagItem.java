package de.rexlmanu.bingo.game.flags.item;

import de.rexlmanu.bingo.game.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
public class FlagItem extends Flag {

    private Material material;

    @Override
    public Component name() {
        return Component.translatable(String.format(
                "%s.minecraft.%s",
                this.material.isBlock() ? "block" : "item",
                material.name().toLowerCase())
        );
    }
}
