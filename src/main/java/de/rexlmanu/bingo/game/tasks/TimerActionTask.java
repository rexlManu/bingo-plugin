package de.rexlmanu.bingo.game.tasks;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.shared.task.Task;
import de.rexlmanu.bingo.utility.TimerFormatUtils;
import net.kyori.adventure.text.format.NamedTextColor;

public class TimerActionTask extends Task {

    public TimerActionTask(GameManager gameManager) {
        super(gameManager, true, 1);
    }

    @Override
    public void run() {
        if (!this.gameManager().state().isIngame()) return;
        this.gameManager().getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> {
            player.sendActionBar(TimerFormatUtils.formatMillis(this.gameManager().playingTime()).color(NamedTextColor.GRAY));
        }));
    }
}
