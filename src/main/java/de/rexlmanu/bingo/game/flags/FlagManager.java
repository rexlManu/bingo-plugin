package de.rexlmanu.bingo.game.flags;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
public class FlagManager {

    private List<Flag> flags;

    public FlagManager() {
        this.flags = Lists.newArrayList();
    }
}
