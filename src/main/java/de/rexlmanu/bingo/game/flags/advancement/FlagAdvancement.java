package de.rexlmanu.bingo.game.flags.advancement;

import de.rexlmanu.bingo.game.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

@Getter
@Setter
@Accessors(fluent = true)
@AllArgsConstructor
public class FlagAdvancement extends Flag {

    private Advancement advancement;

    public FlagAdvancement(String advancementName) {
        this(Bukkit.getAdvancement(NamespacedKey.minecraft(advancementName)));
    }

    @Override
    public Component name() {
        String[] split = advancement.getKey().getKey().split("/");
        return Component.translatable(String.format("advancements.%s.%s.title", split[0], split[1]))
                .hoverEvent(HoverEvent.showText(
                        Component.translatable(String.format("advancements.%s.%s.description", split[0], split[1]))
                ));
    }

    public Component description() {
        String[] split = advancement.getKey().getKey().split("/");
        return Component.translatable(String.format("advancements.%s.%s.description", split[0], split[1]));
    }
}
