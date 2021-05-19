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

import java.util.List;
import java.util.Objects;

public class IngameTablistScoreboard implements TablistScoreboard {

    @Override
    public void update(GameManager manager, GameUser user) {
        Player player = user.asPlayer().orElse(null);
        if (Objects.isNull(player)) return;

        Scoreboard scoreboard = Objects.isNull(player.getScoreboard()) ?
                Bukkit.getScoreboardManager().getNewScoreboard() :
                player.getScoreboard();

        scoreboard.getTeams().forEach(Team::unregister);

        manager.teams().forEach(team -> {
            List<GameUser> users = manager.getUsersByTeam(team);
            org.bukkit.scoreboard.Team scoreboardTeam = scoreboard.registerNewTeam(team.name());
            scoreboardTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.FOR_OWN_TEAM);
            scoreboardTeam.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.FOR_OWN_TEAM);
            scoreboardTeam.color(NamedTextColor.GRAY);
            scoreboardTeam.prefix(Component.text(team.name() + "§8∙ §7"));
            users.forEach(u -> u.asPlayer().ifPresent(p -> scoreboardTeam.addEntry(p.getName())));
        });

        player.setScoreboard(scoreboard);
    }

}
