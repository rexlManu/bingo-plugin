package de.rexlmanu.bingo.game;

import com.google.common.collect.Lists;
import de.rexlmanu.bingo.bootstrap.PluginImplementation;
import de.rexlmanu.bingo.game.actions.GameActionImplementation;
import de.rexlmanu.bingo.game.events.GameListener;
import de.rexlmanu.bingo.game.flags.Flag;
import de.rexlmanu.bingo.game.flags.FlagManager;
import de.rexlmanu.bingo.game.inventory.FlagsInventory;
import de.rexlmanu.bingo.game.settings.GameSettings;
import de.rexlmanu.bingo.game.tablist.TablistHandler;
import de.rexlmanu.bingo.game.tasks.BossbarTask;
import de.rexlmanu.bingo.game.tasks.FlagItemCheckTask;
import de.rexlmanu.bingo.game.tasks.TimerActionTask;
import de.rexlmanu.bingo.game.team.Team;
import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.game.users.scoreboard.EndUserScoreboard;
import de.rexlmanu.bingo.game.users.scoreboard.IngameUserScoreboard;
import de.rexlmanu.bingo.game.users.scoreboard.LobbyUserScoreboard;
import de.rexlmanu.bingo.shared.flag.FlagTemplate;
import de.rexlmanu.bingo.shared.flag.FlagTemplateProvider;
import de.rexlmanu.bingo.shared.flag.FlagType;
import de.rexlmanu.bingo.shared.itemstack.Item;
import de.rexlmanu.bingo.shared.message.Message;
import de.rexlmanu.bingo.shared.scoreboard.UserScoreboard;
import de.rexlmanu.bingo.shared.settings.SettingElement;
import de.rexlmanu.bingo.shared.settings.elements.EnumSettingElement;
import de.rexlmanu.bingo.utility.FileUtils;
import de.rexlmanu.bingo.utility.TimerFormatUtils;
import de.rexlmanu.bingo.utility.interfaces.Reloadable;
import de.rexlmanu.bingo.utility.progress.ProgressBar;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Getter
@Accessors(fluent = true)
public class GameManager implements Reloadable {

    public static final String WORLD_NAME = "bingo";


    public static final ItemStack TEAM_SELECTOR_ITEM = Item
            .builder(Material.NETHER_STAR)
            .displayName(Component.text("Teamauswahl")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.fromCSSHexString("#0EA5E9")))
            .build();
    public static final ItemStack SETTINGS_ITEM = Item
            .builder(Material.COMPARATOR)
            .displayName(Component.text("Einstellungen")
                    .decoration(TextDecoration.ITALIC, false)
                    .color(TextColor.fromCSSHexString("#E11D48")))
            .build();

    public static final ItemStack START_ITEM = Item
            .builder(Material.LIME_DYE)
            .displayName(Component.text("§aStarten")
                    .decoration(TextDecoration.ITALIC, false))
            .build();

    private GameState state;
    private GameSettings gameSettings;

    private List<GameUser> users;
    private List<Team> teams;

    private PluginImplementation implementation;

    private FlagTemplateProvider flagTemplateProvider;
    private FlagManager flagManager;
    private TablistHandler tablistHandler;

    private GameActionImplementation gameActionImplementation;
    private GameListener gameListener;
    private FlagsInventory flagsInventory;

    @Setter
    private Team winner;
    @Getter
    private long startedAt;

    private boolean starting;

    public GameManager(PluginImplementation implementation) {
        this.implementation = implementation;

        this.state = GameState.LOBBY;
        this.gameSettings = new GameSettings();

        this.teams = Lists.newArrayList();
        this.generateTeams();
        this.users = Lists.newArrayList();

        this.flagTemplateProvider = new FlagTemplateProvider(this.implementation.context().getDataFolder());
        this.flagManager = new FlagManager();
        this.tablistHandler = new TablistHandler(this);

        this.gameSettings.settings().add(SettingElement
                .builder()
                .material(Material.DIAMOND)
                .name("Item Template")
                .asEnum(null, this.flagTemplateProvider
                        .getTemplates(FlagType.ITEM)
                        .stream()
                        .map(FlagTemplate::name)
                        .collect(Collectors.toList())
                ));

        this.gameSettings.settings().add(SettingElement
                .builder()
                .material(Material.PAPER)
                .name("Advancement Template")
                .asEnum(null, this.flagTemplateProvider
                        .getTemplates(FlagType.ADVANCEMENT)
                        .stream()
                        .map(FlagTemplate::name)
                        .collect(Collectors.toList())
                ));

        this.gameActionImplementation = new GameActionImplementation(this);
        this.gameListener = new GameListener(this, this.gameActionImplementation);
        this.flagsInventory = new FlagsInventory(this);

        this.starting = false;

        Bukkit.getPluginManager().registerEvents(this.gameListener, this.implementation.context());

        this.implementation.runTask(new FlagItemCheckTask(this));
        this.implementation.runTask(new TimerActionTask(this));
        this.implementation.runTask(new BossbarTask(this));
    }

    private void generateTeams() {
        for (int i = 0; i < 9; i++) {
            String teamName = DyeColor.values()[i].name().toLowerCase();
            if (teamName.contains("_")) {
                String[] splitTeamName = teamName.split("_");
                teamName = StringUtils.capitalize(splitTeamName[0]) +
                        " " +
                        StringUtils.capitalize(splitTeamName[1]);
            } else {
                teamName = StringUtils.capitalize(teamName);
            }
            this.teams.add(new Team(teamName, Material.valueOf(DyeColor.values()[i].name() + "_CONCRETE")));
        }
    }

    public Optional<GameUser> findUserByUuid(UUID uuid) {
        return this.users.stream().filter(gameUser -> gameUser.uuid().equals(uuid)).findFirst();
    }

    public List<GameUser> getUsersByTeam(Team team) {
        return this.users.stream()
                .filter(user -> Objects.nonNull(user.selectedTeam()))
                .filter(gameUser -> gameUser.selectedTeam().equals(team))
                .collect(Collectors.toList());
    }

    public List<GameUser> getPlayingUsers() {
        return this.users.stream().filter(GameUser::joined).collect(Collectors.toList());
    }

    public UserScoreboard scoreboard() {
        switch (this.state) {
            case LOBBY:
                return LobbyUserScoreboard.create(this);
            case INGAME:
                return IngameUserScoreboard.create(this);
            case END:
                return EndUserScoreboard.create(this);
            default:
                return null;
        }
    }

    public long playingTime() {
        return System.currentTimeMillis() - this.startedAt;
    }

    private Team getEmptiestTeam() {
        return this.teams.stream().min(Comparator.comparingInt(o -> this.getUsersByTeam(o).size())).get();
    }

    public void broadcast(Component component) {
        this.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> player.sendMessage(component)));
    }

    public void start() {
        this.starting = true;

        // Check for invalid team sizes
        this.teams.forEach(team -> {
            List<GameUser> users = this.getUsersByTeam(team);
            if (this.gameSettings.teamSize().value() < users.size()) {
                for (int i = 0; i < (users.size() - this.gameSettings.teamSize().value()); i++) {
                    GameUser user = users.get(i);
                    user.selectedTeam(null);
                    user.asPlayer().ifPresent(player -> player.sendMessage(Message.PREFIX.append(
                            Component.text("Da dein Team die erlaubte Teamgröße überschreitet, wurdest du entfernt.").color(Message.COLOR)
                    )));
                }
            }
        });

        // Force team for player that hasnt choosen one!
        this.getPlayingUsers().stream().filter(user -> Objects.isNull(user.selectedTeam())).forEach(user -> {
            user.selectedTeam(this.getEmptiestTeam());
            this.scoreboard().update(user);
        });

        this.createWorld();

        this.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> {
            this.playerReset(player);
            this.scoreboard().destroy(user);
        }));

        boolean mixFlags = this.gameSettings.flagType().selected().equals("mix");
        int rest = this.gameSettings.flagCount().value() % FlagType.values().length;
        Arrays.stream(FlagType.values()).forEach(type -> {
            if (!this.gameSettings.flagType().selected().equals(type.name().toLowerCase()) && !mixFlags) return;

            FlagTemplate template = this.getFlagTemplateBySetting(type);
            int flagCount = (mixFlags ? (this.gameSettings.flagCount().value() / 2) : this.gameSettings.flagCount().value());


            if (flagCount > template.flags().size()) {
                this.flagManager.flags().addAll(template.flags());
            } else {
                for (int i = 0; i < flagCount; i++) {
                    Flag flag = null;
                    int timeout = 0;
                    while (timeout < 60) {
                        flag = template.random();
                        if (!this.flagManager.flags().contains(flag)) {
                            break;
                        }
                        timeout++;
                    }
                    if (Objects.nonNull(flag))
                        this.flagManager.flags().add(flag);
                }
            }
        });

        this.state = GameState.INGAME;
        this.startedAt = System.currentTimeMillis();
        this.tablistHandler.update();
        this.updateTabFooter();
        this.getPlayingUsers().forEach(user -> {
            this.scoreboard().create(user);

            float progress = (float) (((double) user.selectedTeam().items().size()) / ((double) this.flagManager().flags().size()));
            user.bossBar(BossBar.bossBar(Component.text(""), progress, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS));
            this.updateBossbar(user);
            user.asPlayer().ifPresent(player -> player.showBossBar(user.bossBar()));
        });

        this.broadcast(Message.PREFIX.append(Component.text("Das Spiel hat begonnen! GLHF!").color(Message.COLOR)));
    }

    public void updateBossbar(GameUser user) {
        float progress = (float) (((double) user.selectedTeam().items().size()) / ((double) this.flagManager().flags().size()));
        user.bossBar().progress(progress);
        user.bossBar().name(Component.text(String.format("%s von %s gesammelt (%.0f%%)",
                user.selectedTeam().items().size(),
                this.flagManager().flags().size(),
                progress * 100
        )));
    }

    private void playerReset(Player player) {
        player.getInventory().clear();
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        player.setExp(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.getEnderChest().clear();

        Lists.newArrayList(Bukkit.getServer().advancementIterator()).forEach(advancement -> {
            player.getAdvancementProgress(advancement).getAwardedCriteria().forEach(s -> {
                player.getAdvancementProgress(advancement).revokeCriteria(s);
            });
        });
    }

    public void createWorld() {
        // Check if worlds exists
        List<String> worldNames = Arrays.asList(WORLD_NAME, WORLD_NAME + "_nether", WORLD_NAME + "_end");
        worldNames.forEach(worldName -> {
            World world = Bukkit.getWorld(worldName);
            if (Objects.nonNull(world)) {
                // Remove everyone before deleting.
                world.getPlayers().forEach(player -> {
                    player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    this.playerReset(player);
                });
                File folder = world.getWorldFolder();
                Bukkit.unloadWorld(worldName, false);
                FileUtils.deleteDirectory(folder);
                return;
            }

            File worldFolder = new File(worldName);
            if (worldFolder.exists()) {
                FileUtils.deleteDirectory(worldFolder);
            }
        });

        // Create World
        long seed = new Random().nextLong();

        System.out.println("Generating worlds");
        this.broadcast(Message.PREFIX.append(Component.text("Welt wird erstellt.").color(Message.COLOR)));
        long start = System.currentTimeMillis();
        // Normal world
        World world = WorldCreator.name(WORLD_NAME).seed(seed).environment(World.Environment.NORMAL).createWorld();
        // Nether
        WorldCreator.name(WORLD_NAME + "_nether").seed(seed).environment(World.Environment.NETHER).createWorld();
        // End
        WorldCreator.name(WORLD_NAME + "_end").seed(seed).environment(World.Environment.THE_END).createWorld();
        long timeNeededForCreation = System.currentTimeMillis() - start;
        this.broadcast(Message.PREFIX.append(Component.text("Welt wurde in " + timeNeededForCreation + "ms erstellt.").color(Message.COLOR)));
        System.out.println("Finished in " + timeNeededForCreation + "ms");

        worldNames.forEach(s -> {
            World createdWorld = Bukkit.getWorld(s);
            createdWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            createdWorld.setDifficulty(Difficulty.valueOf(this.gameSettings.difficulty().selected()));
            createdWorld.setAutoSave(false);
            createdWorld.setFullTime(0);
        });

        this.startedAt = System.currentTimeMillis();

        this.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> player.teleport(world.getSpawnLocation())));
    }

    public void end() {
        this.getPlayingUsers().forEach(user -> this.scoreboard().destroy(user));
        this.state = GameState.END;
        this.broadcast(Component.empty());
        this.broadcast(Message.PREFIX.append(Component.text("Das Spiel ist zuende!").color(Message.COLOR)));
        this.tablistHandler.update();
        this.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> {
            this.scoreboard().create(user);
            player.playSound(Sound.sound(Key.key("entity.firework_rocket.twinkle"), Sound.Source.PLAYER, 1, 1.1f));
            player.showTitle(Title.title(
                    Component.text(this.winner.name()),
                    Component.text("hat alle Ziele gesammelt.").color(NamedTextColor.GRAY),
                    Title.Times.of(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(1))
            ));
            this.broadcast(Message.PREFIX.append(Component.text("Das Team " + this.winner.name() + " hat gewonnen!").color(NamedTextColor.GREEN)));
            this.broadcast(Component.empty());
            this.broadcast(Component.text("§7Reihenfolge:"));
            this.winner.items().forEach(collectedFlagItem -> {
                this.broadcast(
                        Component.text(" - ").color(NamedTextColor.GRAY)
                                .append(collectedFlagItem.flag().name().color(NamedTextColor.GREEN))
                                .append(Component.text(
                                        " ("
                                                + collectedFlagItem.collector().asPlayer().map(HumanEntity::getName).get()
                                                + " - "
                                )
                                        .append(TimerFormatUtils.formatMillis(collectedFlagItem.collectedAt() - this.startedAt))
                                        .append(Component.text(")"))
                                        .color(NamedTextColor.GRAY))
                );
            });
            this.broadcast(Component.empty());
            this.broadcast(Message.PREFIX.append(Component.text("Spiellänge: ").color(Message.COLOR).append(TimerFormatUtils.formatMillis(this.playingTime())
                    .color(NamedTextColor.GREEN))));
            this.broadcast(Message.PREFIX.append(Component.text("Seed: ").color(Message.COLOR).append(Component.text(Bukkit.getWorld(WORLD_NAME).getSeed())
                    .clickEvent(ClickEvent.copyToClipboard(String.valueOf(Bukkit.getWorld(WORLD_NAME).getSeed())))
                    .hoverEvent(HoverEvent.showText(Component.text("Klicken zum Kopieren!")))
                    .color(Message.COLOR)
            )));
            this.broadcast(Component.empty());
        }));
    }

    public FlagTemplate getFlagTemplateBySetting(FlagType flagType) {
        SettingElement element = this.gameSettings.byName(StringUtils.capitalize(flagType.name().toLowerCase()) + " Template");
        if (Objects.isNull(element) || !(element instanceof EnumSettingElement)) return null;
        return this.flagTemplateProvider.getTemplates(flagType).stream().filter(flagTemplate ->
                flagTemplate.name().equals(((EnumSettingElement) element).selected())).findFirst().orElse(null);
    }

    @Override
    public void reload() {
        this.flagTemplateProvider.reload();

        ((EnumSettingElement) this.gameSettings.byName("Advancement Template")).values(this.flagTemplateProvider
                .getTemplates(FlagType.ADVANCEMENT)
                .stream()
                .map(FlagTemplate::name)
                .collect(Collectors.toList()));

        ((EnumSettingElement) this.gameSettings.byName("Item Template")).values(this.flagTemplateProvider
                .getTemplates(FlagType.ITEM)
                .stream()
                .map(FlagTemplate::name)
                .collect(Collectors.toList()));
    }

    public void resetGame() {
        this.state = GameState.LOBBY;
        this.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> {
            user.selectedTeam(null);
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
            playerReset(player);
            player.setGameMode(GameMode.ADVENTURE);
            this.scoreboard().destroy(user);
            this.scoreboard().create(user);
            this.giveLobbyItems(player);

            player.hideBossBar(user.bossBar());
            user.bossBar(null);
        }));
        this.updateTabFooter();
        this.teams.forEach(team -> team.items().clear());
        this.flagManager.flags().clear();
        this.starting = false;
        this.winner = null;
    }

    public void giveLobbyItems(Player player) {
        player.getInventory().setItem(4, TEAM_SELECTOR_ITEM);

        if (player.isOp()) {
            player.getInventory().setItem(0, SETTINGS_ITEM);
            player.getInventory().setItem(8, START_ITEM);
        }
    }

    public void updateTabFooter() {
        if (this.state.isIngame()) {
            AtomicReference<Component> component = new AtomicReference<>(Component.empty());
            this.teams().stream().filter(team -> !this.getUsersByTeam(team).isEmpty()).forEach(team -> {
                float percentage = ((float) team.items().size()) / ((float) this.flagManager.flags().size());
                component.set(component.get()
                        .append(Component.text("\n"))
                        .append(Component.text(team.name()).color(NamedTextColor.GRAY))
                        .append(Component.text(" » ").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(ProgressBar.create(40, percentage)))
                        .append(Component.text(" (").color(NamedTextColor.DARK_GRAY))
                        .append(Component.text(String.format("%.0f%%", percentage * 100)).color(NamedTextColor.GREEN))
                        .append(Component.text(")").color(NamedTextColor.DARK_GRAY))
                );
            });
            component.set(component.get().append(Component.text("\n")));
            this.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> {
                player.sendPlayerListFooter(component.get());
            }));
        } else {
            this.getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player ->
                    player.sendPlayerListFooter(Component.empty())));
        }
    }
}
