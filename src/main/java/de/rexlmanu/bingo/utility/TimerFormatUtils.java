package de.rexlmanu.bingo.utility;

import net.kyori.adventure.text.Component;

import java.util.concurrent.TimeUnit;

public class TimerFormatUtils {

    public static Component formatMillis(long remainingMillis) {

        long days = TimeUnit.MILLISECONDS.toDays(remainingMillis);
        long daysMillis = TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(remainingMillis - daysMillis);
        long hoursMillis = TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis - daysMillis - hoursMillis);
        long minutesMillis = TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis - daysMillis - hoursMillis - minutesMillis);

        return Component.text(days > 0 ? days + " Tag" + (days == 1 ? "" : "e") + " " : "").append(Component.text(String.format("%02d:%02d:%02d", hours, minutes, seconds)));
    }

}
