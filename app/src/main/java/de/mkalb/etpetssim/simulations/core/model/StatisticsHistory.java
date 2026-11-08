package de.mkalb.etpetssim.simulations.core.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Bounded in-memory history of statistic samples.
 *
 * <p>Thread-safe: {@code add(..)} and {@code toList()} are {@code synchronized} so that a JavaFX-thread consumer
 * reading {@code toList()} does not race with a background thread calling {@code add(..)}.
 */
final class StatisticsHistory {

    private static final int DEFAULT_CAPACITY = 1000;

    private final int capacity;
    private final Deque<StatisticsSample> samples;

    StatisticsHistory() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates a history with the specified maximum number of retained samples.
     *
     * @param capacity maximum number of retained samples; must be positive
     * @throws IllegalArgumentException if {@code capacity} is not positive
     */
    StatisticsHistory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0, but was: " + capacity);
        }
        this.capacity = capacity;
        samples = new ArrayDeque<>(capacity);
    }

    /**
     * Appends a sample, evicting the oldest sample when the history is full.
     *
     * @param sample sample to append
     */
    synchronized void add(StatisticsSample sample) {
        if (samples.size() == capacity) {
            samples.removeFirst();
        }
        samples.addLast(sample);
    }

    /**
     * Returns an immutable snapshot in oldest-to-newest order.
     *
     * @return the retained samples
     */
    synchronized List<StatisticsSample> toList() {
        return List.copyOf(samples);
    }

}
