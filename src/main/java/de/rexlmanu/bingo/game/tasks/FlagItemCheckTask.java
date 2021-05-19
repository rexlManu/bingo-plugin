package de.rexlmanu.bingo.game.tasks;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.flags.item.FlagItem;
import de.rexlmanu.bingo.game.team.flag.CollectedFlagItem;
import de.rexlmanu.bingo.shared.message.Message;
import de.rexlmanu.bingo.shared.task.Task;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FlagItemCheckTask extends Task {
    public FlagItemCheckTask(GameManager gameManager) {
        super(gameManager, true, 1);
    }

    @Override
    public void run() {
        if (!this.gameManager().state().isIngame()) {
            return;
        }
        List<FlagItem> flagItems = this.gameManager().flagManager().flags().stream().filter(flag ->
                flag instanceof FlagItem).map(flag -> (FlagItem) flag).collect(Collectors.toList());

        this.gameManager().getPlayingUsers().forEach(user -> user.asPlayer().ifPresent(player -> {
            Arrays.stream(player.getInventory().getContents()).filter(Objects::nonNull).filter(itemStack ->
                    flagItems.stream().anyMatch(flagItem ->
                            flagItem.material().equals(itemStack.getType())
                                    && !user.selectedTeam().hasCollected(flagItem))).findFirst().ifPresent(itemStack -> {
                flagItems.stream().filter(flagItem -> flagItem.material().equals(itemStack.getType())).findFirst().ifPresent(flagItem -> {
                    this.gameManager().gameActionImplementation().onTeamFlagCompletion(user, flagItem);
                });
            });
        }));
    }
}
