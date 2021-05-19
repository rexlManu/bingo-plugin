package de.rexlmanu.bingo.game.inventory;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.team.Team;
import de.rexlmanu.bingo.shared.inventory.IntractableInventory;
import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.message.Message;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeamSelectorInventory implements Listener, IntractableInventory {

    private static final ItemStack FILLER_ITEM = Item.builder(Material.BLACK_STAINED_GLASS_PANE).displayName(Component.text("§r")).build();

    private Inventory inventory;

    private GameManager gameManager;

    public TeamSelectorInventory(GameManager gameManager) {
        this.gameManager = gameManager;

        this.inventory = Bukkit.createInventory(null, 3 * 9, Component.text("Teamauswahl")
                .style(Style.style().decoration(TextDecoration.ITALIC, false)
                        .color(TextColor.fromCSSHexString("#0EA5E9")).build()));

        for (int i = 0; i < 9; i++) {
            this.inventory.setItem(i, FILLER_ITEM);
            this.inventory.setItem(i + 18, FILLER_ITEM);
        }

        this.updateTeamItems();

        Bukkit.getPluginManager().registerEvents(this, this.gameManager.implementation().context());
    }

    private void updateTeamItems() {
        for (int i = 0; i < this.gameManager.teams().size(); i++) {
            this.inventory.setItem(i + 9, this.buildItemForTeam(this.gameManager.teams().get(i)));
        }
    }

    private ItemStack buildItemForTeam(Team team) {
        return Item
                .builder(team.material())
                .displayName(Component.text(team.name()).decoration(TextDecoration.ITALIC, false))
                .lore(this.gameManager.getUsersByTeam(team).stream().filter(gameUser ->
                        gameUser.asPlayer().isPresent()).map(gameUser ->
                        "§8- §7" + gameUser.asPlayer().get().getName()).collect(Collectors.toList()))
                .build();
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (Objects.isNull(event.getClickedInventory())
                || !event.getClickedInventory().equals(this.inventory)
                || !(event.getWhoClicked() instanceof Player)) return;

        if (Objects.nonNull(event.getCurrentItem())
                && event.getCurrentItem().getType().name().contains("CONCRETE")) {
            Optional<Team> optionalTeam = this.gameManager.teams().stream().filter(team ->
                    team.name().equals(PlainComponentSerializer.plain()
                            .serialize(Objects.requireNonNull(event.getCurrentItem().getItemMeta().displayName()))))
                    .findFirst();
            optionalTeam.ifPresent(team -> this.gameManager.findUserByUuid(event.getWhoClicked().getUniqueId())
                    .ifPresent(gameUser -> {
                        if (Objects.nonNull(gameUser.selectedTeam()) && gameUser.selectedTeam().equals(team)) {
                            event.getWhoClicked().sendMessage(Message.PREFIX
                                    .append(Component.text("Du bist bereits im Team " + team.name() + ".")
                                            .color(Message.COLOR)));
                            return;
                        }
                        if (this.gameManager.gameSettings().teamSize().value() <= this.gameManager.getUsersByTeam(team).size()) {
                            event.getWhoClicked().sendMessage(Message.PREFIX
                                    .append(Component.text("Das Team " + team.name() + " ist bereits voll.")
                                            .color(Message.COLOR)));
                            return;
                        }
                        gameUser.selectedTeam(team);
                        event.getWhoClicked().sendMessage(Message.PREFIX
                                .append(Component.text("Du hast das Team " + team.name() + " ausgewählt.")
                                        .color(Message.COLOR)));
                        event.getWhoClicked().playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.PLAYER, 1, 2));
                        this.gameManager.scoreboard().update(gameUser);
                    }));
        }

        this.updateTeamItems();

        event.setCancelled(true);
    }

    public void open(Player player) {
        player.playSound(Sound.sound(Key.key("block.chest.open"), Sound.Source.PLAYER, 1, 2));
        player.openInventory(this.inventory);
    }
}
