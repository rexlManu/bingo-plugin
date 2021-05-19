package de.rexlmanu.bingo.game.tablist;

import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.users.GameUser;
import org.bukkit.entity.Player;

public interface TablistScoreboard {

    void update(GameManager manager, GameUser user);

}
