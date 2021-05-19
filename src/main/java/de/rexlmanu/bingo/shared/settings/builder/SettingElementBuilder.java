package de.rexlmanu.bingo.shared.settings.builder;

import de.rexlmanu.bingo.shared.settings.SettingElement;
import de.rexlmanu.bingo.shared.settings.elements.BooleanSettingElement;
import de.rexlmanu.bingo.shared.settings.elements.EnumSettingElement;
import de.rexlmanu.bingo.shared.settings.elements.IntegerSettingElement;
import de.rexlmanu.bingo.utility.builder.Buildable;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

@Setter
@Accessors(fluent = true)
public class SettingElementBuilder implements Buildable<SettingElement> {

    private String name;
    private Material material;
    private SettingElement element;

    public SettingElementBuilder() {
    }

    public IntegerSettingElement asInteger(int defaultValue, int minimum, int maximum) {
        Objects.requireNonNull(this.name);
        Objects.requireNonNull(this.material);

        return new IntegerSettingElement(this.name, this.material, defaultValue, minimum, maximum);
    }

    public BooleanSettingElement asBoolean(boolean defaultValue) {
        Objects.requireNonNull(this.name);
        Objects.requireNonNull(this.material);

        return new BooleanSettingElement(this.name, this.material, defaultValue);
    }

    public EnumSettingElement asEnum(String defaultValue, List<String> values) {
        Objects.requireNonNull(this.name);
        Objects.requireNonNull(this.material);

        return new EnumSettingElement(this.name, this.material, defaultValue, values);
    }


    @Override
    public SettingElement build() {
        Objects.requireNonNull(this.element);
        return this.element.material(this.material).name(this.name);
    }
}
