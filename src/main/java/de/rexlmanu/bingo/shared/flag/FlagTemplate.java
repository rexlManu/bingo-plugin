package de.rexlmanu.bingo.shared.flag;

import de.rexlmanu.bingo.game.flags.Flag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
public class FlagTemplate {

    private String name;
    private FlagType type;
    private List<Flag> flags;

    public Flag random() {
        return this.flags.get(ThreadLocalRandom.current().nextInt(this.flags.size()));
    }
}
