package de.rexlmanu.bingo.shared.settings;

import de.rexlmanu.bingo.shared.settings.builder.SettingElementBuilder;
import de.rexlmanu.bingo.shared.settings.bukkit.ItemInterpreter;
import de.rexlmanu.bingo.shared.settings.elements.BooleanSettingElement;
import de.rexlmanu.bingo.shared.settings.elements.EnumSettingElement;
import de.rexlmanu.bingo.shared.settings.elements.IntegerSettingElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.Material;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class SettingElement {

    public static SettingElementBuilder builder() {
        return new SettingElementBuilder();
    }

    private String name;
    private Material material;

    public ItemInterpreter interpreter() {
        if (this instanceof IntegerSettingElement) {
            return ItemInterpreter.INTEGER_INTERPRETER;
        }
        if (this instanceof BooleanSettingElement) {
            return ItemInterpreter.BOOLEAN_INTERPRETER;
        }
        if (this instanceof EnumSettingElement) {
            return ItemInterpreter.ENUM_INTERPRETER;
        }
        return null;
    }

}
