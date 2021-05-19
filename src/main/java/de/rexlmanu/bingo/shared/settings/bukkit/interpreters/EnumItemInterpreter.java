package de.rexlmanu.bingo.shared.settings.bukkit.interpreters;

import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.settings.bukkit.ItemInterpreter;
import de.rexlmanu.bingo.shared.settings.elements.EnumSettingElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class EnumItemInterpreter implements ItemInterpreter<EnumSettingElement> {
    @Override
    public ItemStack transform(EnumSettingElement element) {
        return Item
                .builder(element.material())
                .displayName(Component.text(element.name()).decoration(TextDecoration.ITALIC, false))
                .lore(Arrays.asList(
                        "",
                        "§7Auswahl: §b" + element.selected(),
                        "§7Optionen: §b" + String.join(", ", element.values()),
                        "",
                        "§7§oLinksklick = Nächste Option",
                        "§7§oRechtsklick = Vorherige Option "
                ))
                .build();
    }

    @Override
    public boolean modify(EnumSettingElement enumSettingElement, InventoryClickEvent event) {
        int index = enumSettingElement.values().indexOf(enumSettingElement.selected());
        if (event.isLeftClick() && index < (enumSettingElement.values().size() - 1)) {
            enumSettingElement.selected(enumSettingElement.values().get(index + 1));
            return true;
        }
        if (event.isRightClick() && index > 0) {
            enumSettingElement.selected(enumSettingElement.values().get(index - 1));
            return true;
        }
        return false;
    }
}
