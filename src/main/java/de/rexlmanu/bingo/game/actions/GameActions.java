package de.rexlmanu.bingo.game.actions;

import de.rexlmanu.bingo.game.flags.Flag;
import de.rexlmanu.bingo.game.users.GameUser;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface GameActions {

    void onUserEnter(UserAction<PlayerInteractEvent> action);

    void onUserLeave(UserAction<PlayerInteractEvent> action);

    void onUserItemInteraction(UserAction<PlayerInteractEvent> action);

    void onUserAdvancementCompletion(UserAction<PlayerAdvancementDoneEvent> action);

    void onTeamFlagCompletion(GameUser gameUser, Flag flag);
}
