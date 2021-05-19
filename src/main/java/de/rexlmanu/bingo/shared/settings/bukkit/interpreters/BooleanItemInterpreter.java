package de.rexlmanu.bingo.shared.settings.bukkit.interpreters;

import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.settings.bukkit.ItemInterpreter;
import de.rexlmanu.bingo.shared.settings.elements.BooleanSettingElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BooleanItemInterpreter implements ItemInterpreter<BooleanSettingElement> {
    @Override
    public ItemStack transform(BooleanSettingElement element) {
        return Item
                .builder(element.material())
                .displayName(Component.text(element.name()).decoration(TextDecoration.ITALIC, false))
                .lore(Arrays.asList(
                        "",
                        element.value() ? "§aAktiviert" : "§cDeaktiviert",
                        "",
                        "§7§oLinksklick = Umstellen"
                ))
                .build();
    }

    @Override
    public boolean modify(BooleanSettingElement element, InventoryClickEvent event) {
        if (event.isLeftClick()) {
            element.value(!element.value());
            return true;
        }
        return false;
    }
}
