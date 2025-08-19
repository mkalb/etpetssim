package de.mkalb.etpetssim.simulations.view;

import java.util.*;

final class DrawCallThrottler {

    private final int historySize;
    private final int maxSkips;
    private final Queue<Long> durations = new ArrayDeque<>();
    private int skipCounter = 0;
    private long averageDuration = 0;

    DrawCallThrottler(int historySize, int maxSkips) {
        this.historySize = historySize;
        this.maxSkips = maxSkips;
    }

    boolean shouldSkip(long thresholdMillis) {
        if (durations.isEmpty()) {
            skipCounter = 0;
            return false;
        }
        if (averageDuration > thresholdMillis) {
            skipCounter++;
            if (skipCounter <= maxSkips) {
                return true;
            } else {
                skipCounter = 0;
                return false;
            }
        } else {
            skipCounter = 0;
            return false;
        }
    }

    void recordDuration(long durationMillis) {
        if (durations.size() >= historySize) {
            durations.poll();
        }
        durations.offer(durationMillis);
        averageDuration = durations.stream().mapToLong(Long::longValue).sum() / durations.size();
    }

    long getAverageDuration() {
        return averageDuration;
    }

}
