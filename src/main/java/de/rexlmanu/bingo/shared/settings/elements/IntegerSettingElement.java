package de.rexlmanu.bingo.shared.settings.elements;

import de.rexlmanu.bingo.shared.settings.SettingElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

@Getter
@Setter
@Accessors(fluent = true)
public class IntegerSettingElement extends SettingElement {

    private int value, minimum, maximum;

    public IntegerSettingElement(String name, Material material, int value, int minimum, int maximum) {
        super(name, material);
        this.value = value;
        this.minimum = minimum;
        this.maximum = maximum;
    }
}
