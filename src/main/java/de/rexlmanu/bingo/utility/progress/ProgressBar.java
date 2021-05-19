package de.rexlmanu.bingo.utility.progress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;

public class ProgressBar {

    @Data
    @Accessors(fluent = true)
    @AllArgsConstructor
    public static class ProgressBarConfig {
        private char icon;
        private ChatColor activeColor, color;
        private String prefix, suffix;
    }

    private static final ProgressBarConfig DEFAULT_CONFIG = new ProgressBarConfig(
            '|',
            ChatColor.GREEN,
            ChatColor.GRAY,
            ChatColor.DARK_GRAY + "[",
            ChatColor.DARK_GRAY + "]"
    );

    public static final char CHAR = '|';

    public static String create(ProgressBarConfig config, int amount, float percentage) {
        int activeCells = (int) (percentage * amount);
        StringBuilder builder = new StringBuilder().append(config.prefix());
        for (int i = 0; i < amount; i++) {
            builder.append(activeCells > i ? config.activeColor() : config.color()).append(config.icon());
        }
        return builder.append(config.suffix()).toString();
    }

    public static String create(int amount, float percentage) {
        return ProgressBar.create(DEFAULT_CONFIG, amount, percentage);
    }

}
