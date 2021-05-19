package de.rexlmanu.bingo.shared.task;

import de.rexlmanu.bingo.game.GameManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
abstract public class Task implements Runnable {
    private GameManager gameManager;
    private boolean async;
    private long period;
}
