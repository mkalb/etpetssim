package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Bounded in-memory history of statistic samples.
 *
 * <p>Thread-safe: {@code size()}, {@code clear()}, {@code add()}, and {@code asList()} are {@code synchronized}
 * so that a JavaFX-thread consumer reading {@code asList()} does not race with a background
 * thread calling {@code add()}.
 */
public final class StatisticHistory {

    /**
     * Default maximum number of retained samples.
     */
    public static final int DEFAULT_CAPACITY = 1000;

    private final int capacity;
    private final ArrayDeque<StatisticSample> samples;

    /**
     * Creates a history with {@link #DEFAULT_CAPACITY}.
     */
    public StatisticHistory() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates a history with the specified maximum number of retained samples.
     *
     * @param capacity maximum number of retained samples; must be positive
     * @throws IllegalArgumentException if {@code capacity} is not positive
     */
    public StatisticHistory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
        samples = new ArrayDeque<>(capacity);
    }

    /**
     * Returns the maximum number of retained samples.
     *
     * @return the history capacity
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns the current number of retained samples.
     *
     * @return the current history size
     */
    public synchronized int size() {
        return samples.size();
    }

    /**
     * Removes all retained samples.
     */
    public synchronized void clear() {
        samples.clear();
    }

    /**
     * Appends a sample, evicting the oldest sample when the history is full.
     *
     * @param sample sample to append
     */
    public synchronized void add(StatisticSample sample) {
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
    public synchronized List<StatisticSample> asList() {
        return List.copyOf(samples);
    }

}
