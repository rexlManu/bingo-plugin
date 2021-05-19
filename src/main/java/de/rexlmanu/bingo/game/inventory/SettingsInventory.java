package de.rexlmanu.bingo.game.inventory;

import com.google.common.collect.Maps;
import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.shared.inventory.IntractableInventory;
import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.settings.SettingElement;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SettingsInventory implements Listener, IntractableInventory {

    private static final ItemStack FILLER_ITEM = Item.builder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.text("§r")).build();

    private Inventory inventory;
    private int page;
    private Map<Integer, SettingElement> slotSettingElement;

    private GameManager gameManager;

    public SettingsInventory(GameManager gameManager) {
        this.gameManager = gameManager;

        this.inventory = Bukkit.createInventory(null, 3 * 9, Component.text("Einstellungen")
                .decoration(TextDecoration.ITALIC, false)
                .color(TextColor.fromCSSHexString("#E11D48")));

        this.page = 0;
        this.slotSettingElement = Maps.newHashMap();
        this.drawItems();

        Bukkit.getPluginManager().registerEvents(this, this.gameManager.implementation().context());
    }

    public void drawItems() {
        List<SettingElement> list = this.gameManager.gameSettings().settings();
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            this.inventory.setItem(i, FILLER_ITEM);
            this.inventory.setItem(i + 18, FILLER_ITEM);
        }
        this.slotSettingElement.clear();
        for (int i = this.page; i < (this.page + 9); i++) {
            if (list.size() <= i) {
                this.inventory.setItem(slot + 9, null);
                continue;
            }
            SettingElement element = list.get(i);
            this.inventory.setItem(slot + 9, element.interpreter().transform(element));
            this.slotSettingElement.put(slot + 9, element);
            slot++;
        }
        if (list.size() > 9) {
            this.inventory.setItem(18, Item.builder(Material.ARROW)
                    .displayName(Component.text("Zurück").decoration(TextDecoration.ITALIC, false)).build());
            this.inventory.setItem(26, Item.builder(Material.ARROW)
                    .displayName(Component.text("Weiter").decoration(TextDecoration.ITALIC, false)).build());
        }
    }

    public void open(Player player) {
        player.playSound(Sound.sound(Key.key("block.chest.open"), Sound.Source.PLAYER, 1, 2));
        player.openInventory(this.inventory);
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (Objects.isNull(event.getClickedInventory())
                || !event.getClickedInventory().equals(this.inventory)
                || !(event.getWhoClicked() instanceof Player)) return;

        if (Objects.nonNull(event.getCurrentItem())) {
            if (event.getSlot() == 26 && this.page < (this.gameManager.gameSettings().settings().size() - 1)) {
                this.page++;
                event.getWhoClicked().playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.PLAYER, 1, 2));
            }
            if (event.getSlot() == 18 && this.page > 0) {
                this.page--;
                event.getWhoClicked().playSound(Sound.sound(Key.key("item.book.page_turn"), Sound.Source.PLAYER, 1, 2));
            }
            if (this.slotSettingElement.containsKey(event.getSlot())) {
                SettingElement element = this.slotSettingElement.get(event.getSlot());
                if (element.interpreter().modify(element, event)) {
                    event.getWhoClicked().playSound(Sound.sound(Key.key("entity.chicken.egg"), Sound.Source.PLAYER, 1, 2));
                }
            }
        }

        this.drawItems();
        event.setCancelled(true);
    }
}
