package de.rexlmanu.bingo.shared.settings.elements;

import de.rexlmanu.bingo.shared.settings.SettingElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
@Accessors(fluent = true)
public class EnumSettingElement extends SettingElement {

    private String selected;
    private List<String> values;

    public EnumSettingElement(String name, Material material, String selected, List<String> values) {
        super(name, material);
        this.selected = Objects.isNull(selected) ? (values.size() > 0 ? values.get(0) : "") : selected;
        this.values = values;
    }
}
