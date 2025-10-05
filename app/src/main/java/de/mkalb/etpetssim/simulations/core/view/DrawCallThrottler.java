package de.mkalb.etpetssim.simulations.core.view;

import java.util.*;

final class DrawCallThrottler {

    private final int historySize;
    private final int maxSkips;
    private final Queue<Long> durations;
    private int skipCounter = 0;
    private long averageDuration = 0;

    DrawCallThrottler(int historySize, int maxSkips) {
        this.historySize = historySize;
        this.maxSkips = maxSkips;
        durations = new ArrayDeque<>(historySize);
    }

    boolean shouldSkip(long thresholdMillis) {
        if ((averageDuration > thresholdMillis)
                && (durations.size() >= historySize)) {
            skipCounter++;
            if (skipCounter <= maxSkips) {
                return true;
            }
        }

        skipCounter = 0;
        return false;
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
