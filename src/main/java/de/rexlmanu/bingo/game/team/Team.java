package de.rexlmanu.bingo.game.team;

import com.google.common.collect.Lists;
import de.rexlmanu.bingo.game.flags.Flag;
import de.rexlmanu.bingo.game.team.flag.CollectedFlagItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Material;

import java.util.List;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class Team {

    private String name;
    private Material material;
    private List<CollectedFlagItem> items;

    public Team(String name, Material material) {
        this.name = name;
        this.material = material;
        this.items = Lists.newArrayList();
    }

    public boolean hasCollected(Flag flag) {
        return this.items.stream().anyMatch(collectedFlagItem -> collectedFlagItem.flag().equals(flag));
    }
}
