package de.mkalb.etpetssim.simulations.core.view;

import java.util.*;

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

    long getAverageDurationMillis() {
        return averageDurationMillis;
    }

}
