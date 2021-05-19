package de.rexlmanu.bingo.game.users;

import de.rexlmanu.bingo.game.team.Team;
import de.rexlmanu.bingo.utility.fastboard.FastBoard;
import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Data
@Accessors(fluent = true)
public class GameUser {

    public static GameUser createFrom(Player player) {
        return new GameUser(player.getUniqueId());
    }

    private UUID uuid;
    private boolean joined;

    private FastBoard fastBoard;
    private Team selectedTeam;
    private BossBar bossBar;

    private GameUser(UUID uuid) {
        this.uuid = uuid;
        this.joined = false;
    }

    public Optional<Player> asPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(this.uuid));
    }

    public Optional<FastBoard> fastBoard() {
        return Optional.ofNullable(this.fastBoard);
    }
}
