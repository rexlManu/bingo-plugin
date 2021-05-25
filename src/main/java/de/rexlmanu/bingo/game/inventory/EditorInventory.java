package de.rexlmanu.bingo.game.inventory;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.flags.item.FlagItem;
import de.rexlmanu.bingo.shared.flag.FlagTemplate;
import de.rexlmanu.bingo.shared.flag.FlagType;
import de.rexlmanu.bingo.shared.inventory.IntractableInventory;
import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.message.Message;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;

public class EditorInventory implements Listener, IntractableInventory {

    private GameManager gameManager;
    private FlagTemplate flagTemplate;
    private Inventory inventory;

    public EditorInventory(GameManager gameManager, FlagTemplate flagTemplate) {
        this.gameManager = gameManager;
        this.flagTemplate = flagTemplate;
        this.inventory = Bukkit.createInventory(null, 6 * 9, Component.text("Editor -> " + this.flagTemplate.name()));

        this.flagTemplate.flags().forEach(flag -> {
            var material = Material.PAPER;
            if (this.flagTemplate.type().equals(FlagType.ITEM)) {
                material = ((FlagItem) flag).material();
            }
            this.inventory.addItem(Item.builder(material).displayName(flag.name()).build());
        });

        Bukkit.getPluginManager().registerEvents(this, this.gameManager.implementation().context());
    }

    @Override
    public void open(Player player) {
        player.openInventory(this.inventory);
        player.playSound(Sound.sound(Key.key("block.chest.open"), Sound.Source.PLAYER, 1, 2));
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (!event.getClickedInventory().equals(this.inventory)) return;
    }

    @EventHandler
    public void handle(InventoryCloseEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;

        HandlerList.unregisterAll(this);

        if (this.flagTemplate.type().equals(FlagType.ITEM)) {
            this.flagTemplate.flags().clear();
            Arrays.stream(this.inventory.getContents()).filter(Objects::nonNull).forEach(itemStack -> {
                this.flagTemplate.flags().add(new FlagItem(itemStack.getType()));
            });
        }
        this.gameManager.flagTemplateProvider().saveTemplate(this.flagTemplate);

        event.getPlayer().sendMessage(Message.PREFIX.append(Component.text("Das Template wurde gespeichert.")));
    }
}
