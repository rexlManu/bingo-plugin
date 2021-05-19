package de.rexlmanu.bingo.game.settings;

import com.google.common.collect.Lists;
import de.rexlmanu.bingo.shared.settings.GameSettingProvider;
import de.rexlmanu.bingo.shared.settings.SettingElement;
import de.rexlmanu.bingo.shared.settings.elements.BooleanSettingElement;
import de.rexlmanu.bingo.shared.settings.elements.EnumSettingElement;
import de.rexlmanu.bingo.shared.settings.elements.IntegerSettingElement;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Difficulty;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(fluent = true)
@Getter
public class GameSettings implements GameSettingProvider {

    private List<SettingElement> settings;

    private IntegerSettingElement teamSize = SettingElement
            .builder()
            .material(Material.BLACK_BED)
            .name("Teamgröße")
            .asInteger(2, 1, 10);

    private BooleanSettingElement damage = SettingElement
            .builder()
            .material(Material.STONE_SWORD)
            .name("Schaden")
            .asBoolean(true);

    private EnumSettingElement flagType = SettingElement
            .builder()
            .material(Material.BLUE_BANNER)
            .name("Zielart")
            .asEnum("item", Arrays.asList("item", "advancement", "mix"));

    private IntegerSettingElement flagCount = SettingElement
            .builder()
            .material(Material.BUCKET)
            .name("Zielmenge")
            .asInteger(3, 1, 30);

    private BooleanSettingElement food = SettingElement
            .builder()
            .material(Material.BREAD)
            .name("Hunger")
            .asBoolean(true);

    private BooleanSettingElement keepInventory = SettingElement
            .builder()
            .material(Material.NETHER_STAR)
            .name("KeepInventory")
            .asBoolean(false);

    private EnumSettingElement difficulty = SettingElement
            .builder()
            .material(Material.ROTTEN_FLESH)
            .name("Schwerigkeit")
            .asEnum("HARD", Arrays.stream(Difficulty.values()).map(Enum::name).collect(Collectors.toList()));

    public GameSettings() {
        this.settings = Lists.newArrayList(teamSize,
                damage,
                flagType,
                flagCount,
                food,
                keepInventory,
                difficulty
        );
    }

    public SettingElement byName(String name) {
        return this.settings.stream().filter(element -> element.name().equals(name)).findFirst().orElse(null);
    }
}
