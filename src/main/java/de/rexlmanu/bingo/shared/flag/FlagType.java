package de.rexlmanu.bingo.shared.flag;

import de.rexlmanu.bingo.game.flags.advancement.FlagAdvancement;
import de.rexlmanu.bingo.game.flags.item.FlagItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

import java.util.Collections;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum FlagType {
    ITEM,
    ADVANCEMENT;

    public String directory() {
        return this.name().toLowerCase();
    }
}
