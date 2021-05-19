package de.rexlmanu.bingo.shared.settings.bukkit;

import de.rexlmanu.bingo.shared.settings.bukkit.interpreters.BooleanItemInterpreter;
import de.rexlmanu.bingo.shared.settings.bukkit.interpreters.EnumItemInterpreter;
import de.rexlmanu.bingo.shared.settings.bukkit.interpreters.IntegerItemInterpreter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface ItemInterpreter<S> {

    public static final ItemInterpreter INTEGER_INTERPRETER = new IntegerItemInterpreter();
    public static final ItemInterpreter BOOLEAN_INTERPRETER = new BooleanItemInterpreter();
    public static final ItemInterpreter ENUM_INTERPRETER = new EnumItemInterpreter();

    ItemStack transform(S s);

    boolean modify(S s, InventoryClickEvent event);

}
