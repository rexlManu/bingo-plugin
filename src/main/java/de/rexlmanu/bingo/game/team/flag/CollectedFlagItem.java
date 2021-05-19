package de.rexlmanu.bingo.game.team.flag;

import de.rexlmanu.bingo.game.flags.Flag;
import de.rexlmanu.bingo.game.users.GameUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class CollectedFlagItem {

    private Flag flag;
    private GameUser collector;
    private long collectedAt;

    public CollectedFlagItem(Flag flag, GameUser collector) {
        this.flag = flag;
        this.collector = collector;
        this.collectedAt = System.currentTimeMillis();
    }
}
