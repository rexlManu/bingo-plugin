package de.rexlmanu.bingo.game.users.scoreboard;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.shared.scoreboard.FastBoardUserScoreboard;
import de.rexlmanu.bingo.shared.scoreboard.UserScoreboard;
import de.rexlmanu.bingo.utility.fastboard.FastBoard;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class LobbyUserScoreboard extends FastBoardUserScoreboard {

    public static UserScoreboard create(GameManager gameManager) {
        return new LobbyUserScoreboard(gameManager);
    }

    LobbyUserScoreboard(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void update(GameUser user) {
        user.fastBoard().ifPresent(fastBoard -> {
            fastBoard.updateLines(
                    null,
                    Component.text("§7Dein Team"),
                    Component.text("§8» §f" + (Objects.isNull(user.selectedTeam()) ? "§7§oNoch keine Auswahl§r" : user.selectedTeam().name())),
                    null,
                    Component.text("§7Online Spieler"),
                    Component.text("§8» §f" + this.gameManager().getPlayingUsers().size()),
                    null
            );
        });
    }
}
