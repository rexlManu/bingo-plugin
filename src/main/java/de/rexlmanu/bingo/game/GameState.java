package de.rexlmanu.bingo.game;

public enum GameState {
    LOBBY, INGAME, END;

    public boolean isIngame() {
        return this.equals(INGAME);
    }

    public boolean isLobby() {
        return this.equals(LOBBY);
    }

    public boolean isEnding() {
        return this.equals(END);
    }

}
