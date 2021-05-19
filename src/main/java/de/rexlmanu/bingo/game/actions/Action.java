package de.rexlmanu.bingo.game.actions;

import de.rexlmanu.bingo.game.users.GameUser;

public class Action<E> {

    private E event;

    public Action(E event) {
        this.event = event;
    }

    public E event() {
        return this.event;
    }
}
