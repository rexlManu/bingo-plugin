package de.rexlmanu.bingo;

import de.rexlmanu.bingo.bootstrap.PluginImplementation;
import de.rexlmanu.bingo.command.BingoCommand;
import de.rexlmanu.bingo.command.CommandResolvers;
import de.rexlmanu.bingo.game.GameManager;
import io.github.revxrsal.cub.bukkit.BukkitCommandHandler;
import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

public final class BingoPlugin extends JavaPlugin implements PluginImplementation {

    private BukkitCommandHandler commandHandler;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        this.commandHandler = BukkitCommandHandler.create(this);
        this.gameManager = new GameManager(this);

        this.commandHandler.registerDependency(GameManager.class, this.gameManager);
        this.commandHandler.registerResolvers(CommandResolvers.class);
        this.commandHandler.registerResponseHandler(Component.class, (response, subject, command, context) ->
                ((BukkitCommandSubject) subject).requirePlayer().sendMessage(response));
        this.commandHandler.registerCommand(new BingoCommand());
    }

    @Override
    public void onDisable() {
    }
}
