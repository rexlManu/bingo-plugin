package de.rexlmanu.bingo.game.users.scoreboard;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.shared.scoreboard.FastBoardUserScoreboard;
import de.rexlmanu.bingo.shared.scoreboard.UserScoreboard;
import de.rexlmanu.bingo.utility.TimerFormatUtils;
import net.kyori.adventure.text.Component;

public class EndUserScoreboard extends FastBoardUserScoreboard {

    public static UserScoreboard create(GameManager gameManager) {
        return new EndUserScoreboard(gameManager);
    }

    EndUserScoreboard(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void update(GameUser user) {
        user.fastBoard().ifPresent(fastBoard -> {
            fastBoard.updateLines(
                    null,
                    Component.text("§7Gewinner"),
                    Component.text("§8» §f" + (this.gameManager().winner().name())),
                    null,
                    Component.text("§7Zeit"),
                    Component.text("§8» §f").append(TimerFormatUtils.formatMillis(this.gameManager().playingTime())),
                    null
            );
        });
    }
}
