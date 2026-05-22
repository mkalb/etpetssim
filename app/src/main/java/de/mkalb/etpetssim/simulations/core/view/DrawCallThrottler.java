package de.mkalb.etpetssim.simulations.core.view;

import java.util.*;

/**
 * Tracks recent draw durations and decides whether intermediate draws should be skipped.
 */
final class DrawCallThrottler {

    private final int historySize;
    private final int maxSkips;
    private final Queue<Long> durationsMillis;
    private int skipCounter = 0;
    private long durationSumMillis = 0;
    private long averageDurationMillis = 0;

    DrawCallThrottler(int historySize, int maxSkips) {
        if (historySize <= 0) {
            throw new IllegalArgumentException("historySize must be greater than zero.");
        }
        if (maxSkips < 0) {
            throw new IllegalArgumentException("maxSkips must be non-negative.");
        }
        this.historySize = historySize;
        this.maxSkips = maxSkips;
        durationsMillis = new ArrayDeque<>(historySize);
    }

    /**
     * Returns whether a draw call should be skipped for the current step.
     */
    boolean shouldSkip(long thresholdMillis) {
        if ((averageDurationMillis > thresholdMillis)
                && (durationsMillis.size() >= historySize)) {
            skipCounter++;
            if (skipCounter <= maxSkips) {
                return true;
            }
        }

        skipCounter = 0;
        return false;
    }

    /**
     * Records one draw duration and refreshes the moving average.
     */
    void recordDurationMillis(long durationMillis) {
        if (durationsMillis.size() >= historySize) {
            Long removedMillis = durationsMillis.poll();
            if (removedMillis != null) {
                durationSumMillis -= removedMillis;
            }
        }
        durationsMillis.offer(durationMillis);
        durationSumMillis += durationMillis;
        averageDurationMillis = durationSumMillis / durationsMillis.size();
    }

    /**
     * Returns the current moving average over the configured history window.
     */
    long getAverageDurationMillis() {
        return averageDurationMillis;
    }

}
