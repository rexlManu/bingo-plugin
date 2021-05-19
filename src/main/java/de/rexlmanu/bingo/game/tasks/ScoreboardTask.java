package de.rexlmanu.bingo.game.tasks;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.GameState;
import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.shared.task.Task;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardTask extends Task {

    private static final String[] COLORS = new String[]{
            "#10B981", "#059669", "#047857", "#065F46", "#047857", "#059669", "#10B981",
            "#34D399", "#6EE7B7", "#A7F3D0", "#6EE7B7", "#34D399"};

    private int colorTick;

    public ScoreboardTask(GameManager gameManager) {
        super(gameManager, true, 1);

        this.colorTick = 0;
    }

    @Override
    public void run() {
        if (this.colorTick >= COLORS.length) {
            this.colorTick = 0;
        }
        this.gameManager().users().stream().filter(GameUser::joined).forEach(user -> {
            if (this.gameManager().state().equals(GameState.LOBBY)) {
                this.updateLobbyScoreboard(user);
            }
        });

        this.colorTick++;
    }

    private void updateLobbyScoreboard(GameUser user) {
        /*user.fastBoard().ifPresent(fastBoard -> {
            if (fastBoard.isDeleted()) return;
            fastBoard.updateTitle(LegacyComponentSerializer.legacySection().serialize(Component.text("Bingo").color(TextColor.color(16, 185, 129))));

            fastBoard.updateLines("Some", "text", "could", "be", "here :)");
            // Component.text("dasdsa").style(Style.style().build().)
        });*/
    }

}
