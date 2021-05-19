package de.rexlmanu.bingo.shared.settings.elements;

import de.rexlmanu.bingo.shared.settings.SettingElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

@Getter
@Setter
@Accessors(fluent = true)
public class BooleanSettingElement extends SettingElement {

    private boolean value;

    public BooleanSettingElement(String name, Material material, boolean value) {
        super(name, material);
        this.value = value;
    }
}
