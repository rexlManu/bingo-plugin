package de.rexlmanu.bingo.game.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.flags.item.FlagItem;
import de.rexlmanu.bingo.game.team.flag.CollectedFlagItem;
import de.rexlmanu.bingo.shared.inventory.IntractableInventory;
import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.itemstack.ItemStackBuilder;
import de.rexlmanu.bingo.utility.TimerFormatUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FlagsInventory implements Listener, IntractableInventory {

    private static final ItemStack FILLER_ITEM = Item.builder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.text("§r")).build();

    private GameManager gameManager;
    private Map<HumanEntity, Inventory> inventoryMap;

    public FlagsInventory(GameManager gameManager) {
        this.gameManager = gameManager;
        this.inventoryMap = Maps.newHashMap();

        Bukkit.getPluginManager().registerEvents(this, this.gameManager.implementation().context());
    }


    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (Objects.isNull(event.getClickedInventory())
                || !this.inventoryMap.containsValue(event.getClickedInventory())
                || !(event.getWhoClicked() instanceof Player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void handle(InventoryMoveItemEvent event) {
        if (this.inventoryMap.containsValue(event.getDestination())) {
            event.setCancelled(true);
        }
    }

    public void open(Player player) {
        player.playSound(Sound.sound(Key.key("block.chest.open"), Sound.Source.PLAYER, 1, 2));

        Inventory inventory = Bukkit.createInventory(null, 3 * 9, Component.text("Flags")
                .style(Style.style().decoration(TextDecoration.ITALIC, false).build()));

        this.gameManager.findUserByUuid(player.getUniqueId()).ifPresent(user -> {
            List<CollectedFlagItem> collectedFlagItems = user.selectedTeam().items();
            this.gameManager.flagManager().flags().forEach(flag -> {
                Material material = Material.PAPER;
                if (flag instanceof FlagItem) {
                    material = ((FlagItem) flag).material();
                }
                ItemStackBuilder builder = Item.builder(material).displayName(flag.name().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                collectedFlagItems.stream().filter(collectedFlagItem -> collectedFlagItem.flag().equals(flag)).findFirst().ifPresent(collectedFlagItem -> {
                    ArrayList<String> lore = Lists.newArrayList("");
                    lore.add("§7§oGesammelt von " + collectedFlagItem.collector().asPlayer().get().getName());
                    lore.add("§7§oZeitpunkt: " + LegacyComponentSerializer
                            .legacySection()
                            .serialize(TimerFormatUtils.formatMillis(
                                    collectedFlagItem.collectedAt() - this.gameManager.startedAt()
                            )));
                    builder.lore(lore).glow(true);
                });
                inventory.addItem(builder.build());
            });
        });

        player.openInventory(inventory);

        this.inventoryMap.put(player, inventory);
    }
}
