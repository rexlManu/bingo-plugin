package de.rexlmanu.bingo.command;

import de.rexlmanu.bingo.command.validator.AdvancementFlagType;
import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.flags.advancement.FlagAdvancement;
import de.rexlmanu.bingo.game.inventory.EditorInventory;
import de.rexlmanu.bingo.shared.flag.FlagTemplate;
import de.rexlmanu.bingo.shared.flag.FlagType;
import de.rexlmanu.bingo.shared.message.Message;
import io.github.revxrsal.cub.annotation.Command;
import io.github.revxrsal.cub.annotation.Dependency;
import io.github.revxrsal.cub.annotation.Description;
import io.github.revxrsal.cub.annotation.Subcommand;
import io.github.revxrsal.cub.bukkit.annotation.CommandPermission;
import net.kyori.adventure.text.Component;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Command(value = "bingo", aliases = { "b" })
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

    @Subcommand("editor")
    @Description("Edit a template")
    public Component editor(Player player, FlagTemplate template) {
        EditorInventory inventory = new EditorInventory(this.gameManager, template);
        inventory.open(player);
        return Message.PREFIX.append(Component.text("Du kannst das Template nun bearbeiten."));
    }

    @Subcommand("add-advancement")
    @Description("Addes advancement to template")
    public Component addAdvancement(Player player, @AdvancementFlagType FlagTemplate flagTemplate, Advancement advancement) {
        flagTemplate.flags().add(new FlagAdvancement(advancement));
        this.gameManager.flagTemplateProvider().saveTemplate(flagTemplate);
        return Message.PREFIX.append(Component.text("Das Advancement wurde hinzugefügt."));
    }

    @Subcommand("remove-advancement")
    @Description("Removes advancement to template")
    public Component removeAdvancement(Player player, @AdvancementFlagType @NotNull FlagTemplate flagTemplate, Advancement advancement) {
        flagTemplate.flags().stream().filter(flag -> ((FlagAdvancement) flag).advancement().equals(advancement)).forEach(flag -> flagTemplate.flags().remove(flag));
        this.gameManager.flagTemplateProvider().saveTemplate(flagTemplate);
        return Message.PREFIX.append(Component.text("Das Advancement wurde entfernt."));
    }

    @Subcommand("create-advancement-template")
    @Description("Creates advancement template")
    public Component createAdvancementTemplate(Player player, String name) {
        var template = new FlagTemplate(name + ".json", name, FlagType.ADVANCEMENT, new ArrayList<>());
        this.gameManager.flagTemplateProvider().saveTemplate(template);
        return Message.PREFIX.append(Component.text("Das Template wurde erstellt."));
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
