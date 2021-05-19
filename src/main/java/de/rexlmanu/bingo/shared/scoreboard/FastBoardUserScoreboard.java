package de.rexlmanu.bingo.shared.scoreboard;

import de.rexlmanu.bingo.game.GameManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor()
abstract public class FastBoardUserScoreboard implements UserScoreboard {

    private GameManager gameManager;
}
