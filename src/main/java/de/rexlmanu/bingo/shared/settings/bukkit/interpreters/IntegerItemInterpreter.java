package de.rexlmanu.bingo.shared.settings.bukkit.interpreters;

import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.settings.bukkit.ItemInterpreter;
import de.rexlmanu.bingo.shared.settings.elements.IntegerSettingElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class IntegerItemInterpreter implements ItemInterpreter<IntegerSettingElement> {
    @Override
    public ItemStack transform(IntegerSettingElement integerSettingElement) {
        return Item
                .builder(integerSettingElement.material())
                .displayName(Component.text(integerSettingElement.name()).decoration(TextDecoration.ITALIC, false))
                .lore(Arrays.asList(
                        "",
                        "§7Current Value: §b" + integerSettingElement.value(),
                        "§7Minimum: §b" + integerSettingElement.minimum(),
                        "§7Maximum: §b" + integerSettingElement.maximum(),
                        "",
                        "§7§oLinksklick = §a§o+§7§o1",
                        "§7§oRechtsklick = §c§o-§7§o1"
                ))
                .build();
    }

    @Override
    public boolean modify(IntegerSettingElement element, InventoryClickEvent event) {
        int value = element.value();
        if (event.isLeftClick() && value < element.maximum()) {
            element.value(value + 1);
            return true;
        } else if (event.isRightClick() && value > element.minimum()) {
            element.value(value - 1);
            return true;
        }
        return false;
    }
}
