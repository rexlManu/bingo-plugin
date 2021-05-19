package de.rexlmanu.bingo.game.users.scoreboard;

import com.google.common.collect.Lists;
import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.shared.scoreboard.FastBoardUserScoreboard;
import de.rexlmanu.bingo.shared.scoreboard.UserScoreboard;
import de.rexlmanu.bingo.utility.fastboard.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IngameUserScoreboard extends FastBoardUserScoreboard {

    public static UserScoreboard create(GameManager gameManager) {
        return new IngameUserScoreboard(gameManager);
    }

    IngameUserScoreboard(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void update(GameUser user) {
        List<Component> flags = this.gameManager()
                .flagManager()
                .flags()
                .stream()
                .map(flag -> Component
                        .text((user.selectedTeam().hasCollected(flag) ? "§8  - " : "§8  - "))
                        .append(flag.name().color(user.selectedTeam().hasCollected(flag) ? NamedTextColor.GREEN : NamedTextColor.GRAY)))
                .collect(Collectors.toList());

        user.fastBoard().ifPresent(fastBoard -> {
            ArrayList<Component> objects = Lists.newArrayList();
            objects.add(Component.empty());
            objects.add(Component.text("§7Dein Team"));
            objects.add(Component.text("§8» §f" + (Objects.isNull(user.selectedTeam()) ? "§7§oNoch keine Auswahl§r" : user.selectedTeam().name())));
            objects.add(Component.empty());
            objects.add(Component.text("§7Items:"));
            objects.add(Component.empty());
            objects.addAll(flags);
            objects.add(Component.empty());
            fastBoard.updateLines(objects);
        });
    }
}
