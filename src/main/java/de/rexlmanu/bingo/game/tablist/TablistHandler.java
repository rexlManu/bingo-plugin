package de.rexlmanu.bingo.game.tablist;

import com.google.common.collect.Maps;
import de.rexlmanu.bingo.game.GameManager;
import de.rexlmanu.bingo.game.GameState;
import de.rexlmanu.bingo.game.tablist.impl.IngameTablistScoreboard;
import de.rexlmanu.bingo.game.tablist.impl.LobbyTablistScoreboard;

import java.util.Map;

public class TablistHandler {

    private GameManager gameManager;
    private Map<GameState, TablistScoreboard> tablistScoreboardMap;

    public TablistHandler(GameManager gameManager) {
        this.gameManager = gameManager;
        this.tablistScoreboardMap = Maps.newHashMap();

        this.tablistScoreboardMap.put(GameState.LOBBY, new LobbyTablistScoreboard());
        this.tablistScoreboardMap.put(GameState.INGAME, new IngameTablistScoreboard());
        this.tablistScoreboardMap.put(GameState.END, new LobbyTablistScoreboard());
    }

    public void update() {
        this.gameManager.getPlayingUsers().forEach(user ->
                this.tablistScoreboardMap.get(this.gameManager.state()).update(this.gameManager, user));
    }

}
