package de.rexlmanu.bingo;

import de.rexlmanu.bingo.bootstrap.PluginImplementation;
import de.rexlmanu.bingo.command.BingoCommand;
import de.rexlmanu.bingo.command.CommandResolvers;
import de.rexlmanu.bingo.command.validator.AdvancementFlagType;
import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.shared.flag.FlagTemplate;
import de.rexlmanu.bingo.shared.flag.FlagType;
import io.github.revxrsal.cub.bukkit.BukkitCommandHandler;
import io.github.revxrsal.cub.bukkit.BukkitCommandSubject;
import io.github.revxrsal.cub.exception.SimpleCommandException;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.commandHandler.registerTypeResolver(FlagTemplate.class, (args, subject, parameter) ->
        {
            String name = args.pop();
            return this.gameManager.flagTemplateProvider().findByName(name).orElse(new FlagTemplate(name + ".json", name, FlagType.ITEM, new ArrayList<>()));
        });
        this.commandHandler.registerTypeResolver(Advancement.class, (args, subject, parameter) -> {
            Advancement advancement = Bukkit.getAdvancement(NamespacedKey.minecraft(args.pop()));
            if (advancement != null) return advancement;
            throw new SimpleCommandException("Advancement not found.");
        });
        this.commandHandler.registerParameterValidator(FlagTemplate.class, (value, parameter, subject) -> {
            if (parameter.getAnnotation(AdvancementFlagType.class) == null) return;
            if (parameter.getAnnotation(NotNull.class) != null && value == null) {
                throw new SimpleCommandException("Template cant be null.");
            }
            if (value == null) {
                return;
            }
            if (value.type().equals(FlagType.ITEM))
                throw new SimpleCommandException("Template need to be advancements");
        });
        this.commandHandler.registerParameterTab(FlagTemplate.class, (args, sender, command, bukkitCommand) -> {
            return this.gameManager.flagTemplateProvider().loadedTemplates().stream().map(FlagTemplate::name).collect(Collectors.toList());
        });
        List<Advancement> advancements = new ArrayList<>();
        Bukkit.advancementIterator().forEachRemaining(advancements::add);
        var advancementNames = advancements.stream().map(advancement -> advancement.getKey().getKey()).collect(Collectors.toList());
        this.commandHandler.registerParameterTab(Advancement.class, (args, sender, command, bukkitCommand) -> advancementNames);
        this.commandHandler.registerCommand(new BingoCommand());
    }

    @Override
    public void onDisable() {
    }
}
