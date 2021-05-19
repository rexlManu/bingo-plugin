package de.rexlmanu.bingo.game.tasks;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.shared.task.Task;

import java.util.Objects;

public class BossbarTask extends Task {
    public BossbarTask(GameManager gameManager) {
        super(gameManager, true, 1);
    }

    @Override
    public void run() {
        if (!this.gameManager().state().isIngame()) {
            return;
        }

        this.gameManager().getPlayingUsers().forEach(user -> {
            if (Objects.isNull(user.bossBar())) return;
            this.gameManager().updateBossbar(user);
        });
    }
}
