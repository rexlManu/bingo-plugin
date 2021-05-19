package de.rexlmanu.bingo.game.events;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.actions.GameActions;
import de.rexlmanu.bingo.game.actions.UserAction;
import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.shared.message.Message;
import de.rexlmanu.bingo.utility.fastboard.FastBoard;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class GameListener implements Listener {

    private static final int SIGN_LINE_INDEX = 1;
    private static final Component SIGN_LINE = Component.text("BINGO").color(TextColor.fromCSSHexString("#10B981"));

    private final GameManager gameManager;
    private final GameActions actions;

    public GameListener(GameManager gameManager, GameActions actions) {
        this.gameManager = gameManager;
        this.actions = actions;
    }

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.gameManager.state().isIngame()) {
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
        if (this.gameManager.state().isIngame()) {
            this.gameManager.findUserByUuid(player.getUniqueId()).ifPresentOrElse(user -> {
                event.joinMessage(Message.PREFIX.append(Component.text(player.getName() + " hat das Spiel betreten.").color(Message.COLOR)));
            }, () -> {
                event.joinMessage(null);
                player.teleport(Bukkit.getWorld(GameManager.WORLD_NAME).getSpawnLocation());
                player.getInventory().clear();
                player.setGameMode(GameMode.SPECTATOR);
            });
        }
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (this.gameManager.state().isLobby()) {
            event.quitMessage(Message.PREFIX.append(Component.text(player.getName() + " hat das Spiel verlassen.").color(Message.COLOR)));
            this.gameManager.findUserByUuid(player.getUniqueId()).ifPresent(user -> {
                user.selectedTeam(null);
                user.joined(false);
                user.fastBoard().ifPresent(FastBoard::delete);
            });
        }

        if (this.gameManager.state().isIngame()) {
            this.gameManager.findUserByUuid(player.getUniqueId()).ifPresentOrElse(user -> {
                event.quitMessage(Message.PREFIX.append(Component.text(player.getName() + " hat das Spiel verlassen.").color(Message.COLOR)));
            }, () -> {
                event.quitMessage(null);
            });
        }
    }

    @EventHandler
    public void handleSignInteraction(PlayerInteractEvent event) {
        if (this.gameManager.state().isIngame()
                || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || Objects.isNull(event.getClickedBlock())
                || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }
        Sign sign = (Sign) event.getClickedBlock().getState();
        if (!PlainComponentSerializer.plain().serialize(sign.line(SIGN_LINE_INDEX)).toLowerCase().equals("bingo")) {
            return;
        }
        Player player = event.getPlayer();
        GameUser user = this.gameManager.findUserByUuid(player.getUniqueId()).orElseGet(() -> {
            GameUser gameUser = GameUser.createFrom(player);
            gameManager.users().add(gameUser);
            return gameUser;
        });

        if (user.joined()) {
            this.actions.onUserLeave(new UserAction<>(event, user));
            return;
        }

        this.actions.onUserEnter(new UserAction<>(event, user));
    }

    @EventHandler
    public void handleSignPlacement(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (this.gameManager.state().isIngame()
                || !player.isOp()
                || event.lines().isEmpty()
                || !PlainComponentSerializer.plain().serialize(Objects.requireNonNull(event.line(0))).toLowerCase().equals("bingo"))
            return;

        event.line(0, Component.empty());
        event.line(SIGN_LINE_INDEX, SIGN_LINE);
        event.line(2, Component.empty());
        event.line(3, Component.text("by rexlManu"));

        player.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.PLAYER, 1f, 2f));
    }

    @EventHandler
    public void handleItemInteraction(PlayerInteractEvent event) {
        if (this.gameManager.state().isIngame()
                || !event.getAction().name().contains("RIGHT_CLICK")
                || Objects.isNull(event.getItem()))
            return;
        this.gameManager.findUserByUuid(event.getPlayer().getUniqueId()).ifPresent(gameUser ->
                this.actions.onUserItemInteraction(new UserAction<>(event, gameUser)));
    }

    @EventHandler
    public void handleItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(this.gameManager.state().isLobby());
    }

    @EventHandler
    public void handleItemPickup(PlayerAttemptPickupItemEvent event) {
        event.setCancelled(this.gameManager.state().isLobby());
    }

    @EventHandler
    public void handleFood(FoodLevelChangeEvent event) {
        if (this.gameManager.state().isLobby()) {
            event.setCancelled(true);
        }

        if (this.gameManager.state().isIngame() && this.gameManager.gameSettings().food().value()) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!this.gameManager.state().isIngame()) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(!this.gameManager.gameSettings().damage().value());
    }

    @EventHandler
    public void handle(PlayerAdvancementDoneEvent event) {
        Optional<GameUser> optional = this.gameManager.findUserByUuid(event.getPlayer().getUniqueId());
        if (!this.gameManager.state().isIngame()
                || !event.getPlayer().getWorld().getName().startsWith("bingo")
                || !optional.isPresent())
            return;
        optional.ifPresent(user -> this.actions.onUserAdvancementCompletion(new UserAction<>(event, user)));
    }

    @EventHandler
    public void handle(PlayerDeathEvent event) {
        if (this.gameManager.state().isIngame()) {
            event.setKeepInventory(this.gameManager.gameSettings().keepInventory().value());
            event.setKeepLevel(this.gameManager.gameSettings().keepInventory().value());
            if (Objects.nonNull(event.deathMessage())) {
                event.deathMessage(Message.PREFIX.append(event.deathMessage().color(Message.COLOR)));
            }
        }
    }

    @EventHandler
    public void handle(PlayerRespawnEvent event) {
        if (!this.gameManager.state().isIngame()) return;
        event.setRespawnLocation(Bukkit.getWorld(GameManager.WORLD_NAME).getSpawnLocation());
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if (!this.gameManager.state().isIngame()) return;
        boolean nether = event.getFrom().getBlock().getType().equals(Material.NETHER_PORTAL);

        Location location = event.getTo();
        // Player is in overworld
        if (event.getFrom().getWorld().getName().equals(GameManager.WORLD_NAME)) {
            location.setWorld(Bukkit.getWorld(
                    GameManager.WORLD_NAME +
                            "_" +
                            (nether ? "nether" : "end")
            ));
        } else {
            // Player is on other world
            location.setWorld(Bukkit.getWorld(GameManager.WORLD_NAME));
        }

        event.setTo(location);
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (!this.gameManager.state().isIngame()) return;

        if (!Arrays.asList(
                PlayerTeleportEvent.TeleportCause.NETHER_PORTAL,
                PlayerTeleportEvent.TeleportCause.END_PORTAL,
                PlayerTeleportEvent.TeleportCause.END_GATEWAY
        ).contains(event.getCause())) {
            return;
        }

        Location location = event.getTo();
        // Player is in overworld
        if (event.getFrom().getWorld().getName().equals(GameManager.WORLD_NAME)) {
            location.setWorld(Bukkit.getWorld(
                    GameManager.WORLD_NAME +
                            "_" +
                            (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) ? "nether" : "end")
            ));
        } else {
            // Player is on other world
            location.setWorld(Bukkit.getWorld(GameManager.WORLD_NAME));
        }

        event.setTo(location);
    }
}
