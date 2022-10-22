package de.rexlmanu.bingo.shared.scoreboard;

import de.rexlmanu.bingo.game.users.GameUser;
import de.rexlmanu.bingo.utility.fastboard.FastBoard;
import net.kyori.adventure.text.Component;

public interface UserScoreboard {

  default void create(GameUser user) {
    user.fastBoard().ifPresent(fastBoard -> {
      if (!fastBoard.isDeleted()) {
        fastBoard.delete();
      }
    });
    user.asPlayer().ifPresent(player -> {
      FastBoard board = new FastBoard(player);
      user.fastBoard(board);

      board.updateTitle(Component.text("§8» §3Bingo"));
      this.update(user);
    });
  }

  default void destroy(GameUser user) {
    user.fastBoard().ifPresent(FastBoard::delete);
  }

  void update(GameUser user);

}
