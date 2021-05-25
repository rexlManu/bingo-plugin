package de.rexlmanu.bingo.game.tablist.impl;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.tablist.TablistScoreboard;
import de.rexlmanu.bingo.game.users.GameUser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class LobbyTablistScoreboard implements TablistScoreboard {
    @Override
    public void update(GameManager manager, GameUser user) {
        Player player = user.asPlayer().orElse(null);
        if (Objects.isNull(player)) return;

        Scoreboard scoreboard = Objects.isNull(player.getScoreboard()) ?
                Bukkit.getScoreboardManager().getNewScoreboard() :
                player.getScoreboard();

        scoreboard.getTeams().forEach(Team::unregister);

        Team team = scoreboard.registerNewTeam("lobby");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.color(NamedTextColor.GRAY);

        manager.teams().forEach(gameTeam -> {
            Team scoreboardTeam = scoreboard.registerNewTeam(gameTeam.name());
            scoreboardTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            scoreboardTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            scoreboardTeam.color(NamedTextColor.GRAY);
            scoreboardTeam.prefix(Component.text(gameTeam.name() + "§8∙ §7"));
            manager.getPlayingUsers().stream().filter(user1 ->
                    gameTeam.equals(user1.selectedTeam())).forEach(user1 ->
                    user1.asPlayer().ifPresent(player1 -> scoreboardTeam.addEntry(player1.getName())));
        });

        manager.getPlayingUsers().stream().filter(user1 -> user1.selectedTeam() == null).forEach(u -> u.asPlayer().ifPresent(p -> team.addEntry(p.getName())));

        player.setScoreboard(scoreboard);
    }
}
