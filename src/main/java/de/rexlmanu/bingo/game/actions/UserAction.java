package de.rexlmanu.bingo.game.actions;

import de.rexlmanu.bingo.game.users.GameUser;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class UserAction<E> extends Action<E> {

    private GameUser user;

    public UserAction(E event, GameUser user) {
        super(event);

        this.user = user;
    }
}
