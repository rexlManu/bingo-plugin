package de.rexlmanu.bingo.command;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.shared.message.Message;
import io.github.revxrsal.cub.annotation.Command;
import io.github.revxrsal.cub.annotation.Dependency;
import io.github.revxrsal.cub.annotation.Description;
import io.github.revxrsal.cub.annotation.Subcommand;
import io.github.revxrsal.cub.bukkit.annotation.CommandPermission;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Command(value = "bingo", aliases = {"b"})
@Description("Main command for bingo")
public class BingoCommand {
    @Dependency
    private GameManager gameManager;

    @Subcommand("reload")
    @CommandPermission("bingo.command.reload")
    @Description("Reloads the plugin resources")
    public Component reload() {
        this.gameManager.reload();
        return Message.PREFIX.append(Component.text("Alle Plugin Resourcen wurden geladen.").color(Message.COLOR));
    }

    @Subcommand("flags")
    @Description("Shows all flags")
    public Component flags(Player player) {
        if (this.gameManager.state().isLobby()) {
            return Message.PREFIX.append(Component.text("Das ist nur während dem Spiel möglich.").color(Message.COLOR));
        }
        this.gameManager.flagsInventory().open(player);
        return Message.PREFIX.append(Component.text("Dir werden alle Ziele angezeigt.").color(Message.COLOR));
    }

    @Subcommand("reset")
    @CommandPermission("bingo.command.reset")
    public static class Reset {
        @Dependency
        private GameManager gameManager;

        @Subcommand("world")
        @CommandPermission("bingo.command.reset.world")
        @Description("Resets the world")
        public Component world() {
            if (!this.gameManager.state().isIngame()) {
                return Message.PREFIX.append(Component.text("Die Welt kann nur während der Spielphase neu erstellt werden.").color(Message.COLOR));
            }
            this.gameManager.createWorld();
            return Message.PREFIX.append(Component.text("Die Welt wurde neu erstellt.").color(Message.COLOR));
        }

        @Subcommand("game")
        @CommandPermission("bingo.command.reset.game")
        @Description("Resets the whole game")
        public Component game() {
            if (this.gameManager.state().isLobby()) {
                return Message.PREFIX.append(Component.text("Das Spiel kann nicht zurückgesetzt werden.").color(Message.COLOR));
            }
            this.gameManager.resetGame();
            return Message.PREFIX.append(Component.text("Das Spiel wurde zurückgesetzt.").color(Message.COLOR));
        }
    }
}
