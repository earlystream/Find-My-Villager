package com.earlystream.tradecompass.util;

import java.time.Duration;

public final class TimeTextUtil {
    private TimeTextUtil() {
    }

    public static String ago(long epochMillis, long nowMillis) {
        if (epochMillis <= 0L) {
            return "never";
        }
        Duration duration = Duration.ofMillis(Math.max(0L, nowMillis - epochMillis));
        long minutes = duration.toMinutes();
        if (minutes < 1L) {
            return "just now";
        }
        if (minutes < 60L) {
            return minutes + " minute" + plural(minutes) + " ago";
        }
        long hours = duration.toHours();
        if (hours < 24L) {
            return hours + " hour" + plural(hours) + " ago";
        }
        long days = duration.toDays();
        return days + " day" + plural(days) + " ago";
    }

    private static String plural(long value) {
        return value == 1L ? "" : "s";
    }
}
