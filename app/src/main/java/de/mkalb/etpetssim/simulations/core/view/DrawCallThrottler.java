package de.mkalb.etpetssim.simulations.core.view;

import java.util.*;

final class DrawCallThrottler {

    private final int historySize;
    private final int maxSkips;
    private final Queue<Long> durations;
    private int skipCounter = 0;
    private long durationSum = 0;
    private long averageDuration = 0;

    DrawCallThrottler(int historySize, int maxSkips) {
        if (historySize <= 0) {
            throw new IllegalArgumentException("historySize must be greater than zero.");
        }
        if (maxSkips < 0) {
            throw new IllegalArgumentException("maxSkips must be non-negative.");
        }
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
            Long removed = durations.poll();
            if (removed != null) {
                durationSum -= removed;
            }
        }
        durations.offer(durationMillis);
        durationSum += durationMillis;
        averageDuration = durationSum / durations.size();
    }

    long getAverageDuration() {
        return averageDuration;
    }

}
