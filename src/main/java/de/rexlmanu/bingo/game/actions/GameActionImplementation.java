package de.rexlmanu.bingo.game.actions;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.flags.Flag;
import de.rexlmanu.bingo.game.flags.advancement.FlagAdvancement;
import de.rexlmanu.bingo.game.inventory.SettingsInventory;
import de.rexlmanu.bingo.game.inventory.TeamSelectorInventory;
import de.rexlmanu.bingo.game.team.flag.CollectedFlagItem;
import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.shared.inventory.IntractableInventory;
import de.rexlmanu.bingo.shared.message.Message;
import de.rexlmanu.bingo.utility.fastboard.FastBoard;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameActionImplementation implements GameActions {

    private GameManager gameManager;

    private IntractableInventory teamSelectorInventory, settingsInventory;

    public GameActionImplementation(GameManager gameManager) {
        this.gameManager = gameManager;

        this.teamSelectorInventory = new TeamSelectorInventory(this.gameManager);
        this.settingsInventory = new SettingsInventory(this.gameManager);
    }

    @Override
    public void onUserEnter(UserAction<PlayerInteractEvent> action) {
        action.user().joined(true);

        action.user().asPlayer().ifPresent(player -> {
            player.playSound(Sound.sound(Key.key("block.piston.extend"), Sound.Source.PLAYER, 1f, 2f));
            player.sendMessage(Message.PREFIX.append(Component.text("Du hast das Spiel betreten.").color(Message.COLOR)));
            this.gameManager.scoreboard().create(action.user());
            player.getInventory().setContents(new ItemStack[]{});
            player.setFoodLevel(20);
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setGameMode(GameMode.ADVENTURE);
            this.gameManager.giveLobbyItems(player);
            this.gameManager.getPlayingUsers().forEach(user -> this.gameManager.scoreboard().update(user));
            this.gameManager.tablistHandler().update();
            this.gameManager.updateTabFooter();
        });
    }

    @Override
    public void onUserLeave(UserAction<PlayerInteractEvent> action) {
        action.user().joined(false);

        action.user().asPlayer().ifPresent(player -> {
            player.sendMessage(Message.PREFIX.append(Component.text("Du hast das Spiel verlassen.").color(Message.COLOR)));
            player.playSound(Sound.sound(Key.key("block.piston.contract"), Sound.Source.PLAYER, 1f, 2f));
            player.getInventory().setContents(new ItemStack[]{});

            this.gameManager.getPlayingUsers().forEach(user -> this.gameManager.scoreboard().update(user));

            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        });
        action.user().fastBoard().ifPresent(FastBoard::delete);
    }

    @Override
    public void onUserItemInteraction(UserAction<PlayerInteractEvent> action) {
        if (action.event().getItem().isSimilar(GameManager.TEAM_SELECTOR_ITEM)) {
            this.teamSelectorInventory.open(action.event().getPlayer());
            return;
        }

        if (action.event().getItem().isSimilar(GameManager.SETTINGS_ITEM)) {
            this.settingsInventory.open(action.event().getPlayer());
            return;
        }

        if (action.event().getItem().isSimilar(GameManager.START_ITEM)) {
            if (this.gameManager.starting()) {
                return;
            }
            this.gameManager.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> {
                player.closeInventory();
                player.getInventory().clear();
            }));
            action.event().getPlayer().sendMessage(Message.PREFIX.append(Component.text("Das Spiel wird nun gestartet.").color(Message.COLOR)));
            this.gameManager.start();
            return;
        }
    }

    @Override
    public void onUserAdvancementCompletion(UserAction<PlayerAdvancementDoneEvent> action) {
        List<FlagAdvancement> flagAdvancements = this.gameManager.flagManager().flags().stream().filter(flag ->
                flag instanceof FlagAdvancement).map(flag -> (FlagAdvancement) flag).collect(Collectors.toList());

        Optional<FlagAdvancement> optional = flagAdvancements.stream().filter(flagAdvancement ->
                flagAdvancement.advancement().equals(action.event().getAdvancement())).findFirst();

        optional.ifPresent(flagAdvancement -> {
            if (action.user().selectedTeam().hasCollected(flagAdvancement)) {
                return;
            }

            this.gameManager.gameActionImplementation().onTeamFlagCompletion(action.user(), flagAdvancement);
        });
    }

    @Override
    public void onTeamFlagCompletion(GameUser user, Flag flag) {
        user.selectedTeam().items().add(new CollectedFlagItem(flag, user));

        this.gameManager.broadcast(
                Message.PREFIX.append(
                        Component
                                .text("Das Team " + user.selectedTeam().name() + " hat ")
                                .color(Message.COLOR)
                                .append(flag.name().color(NamedTextColor.GREEN))
                                .append(Component.text("."))
                )
        );

        this.gameManager.getUsersByTeam(user.selectedTeam()).forEach(teamPlayer -> teamPlayer.asPlayer().ifPresent(player -> {
            this.gameManager.scoreboard().update(teamPlayer);
            this.gameManager.updateBossbar(teamPlayer);
            player.playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.PLAYER, 1, 1.4f));
        }));
        this.gameManager.updateTabFooter();

        // Check for win
        if (user.selectedTeam().items().size() < this.gameManager.flagManager().flags().size()) {
            return;
        }

        this.gameManager.winner(user.selectedTeam());
        this.gameManager.end();
    }
}
