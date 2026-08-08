package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Bounded in-memory history of statistic samples.
 *
 * <p>Thread-safe: {@code add()}, {@code asList()}, and {@code clear()} are {@code synchronized}
 * so that a JavaFX-thread consumer reading {@code asList()} does not race with a background
 * thread calling {@code add()}.
 */
public final class StatisticHistory {

    public static final int DEFAULT_CAPACITY = 100;

    private final int capacity;
    private final ArrayDeque<StatisticSample> samples;

    public StatisticHistory() {
        this(DEFAULT_CAPACITY);
    }

    public StatisticHistory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
        samples = new ArrayDeque<>(capacity);
    }

    public int capacity() {
        return capacity;
    }

    public int size() {
        return samples.size();
    }

    public synchronized void clear() {
        samples.clear();
    }

    public synchronized void add(StatisticSample sample) {
        if (samples.size() == capacity) {
            samples.removeFirst();
        }
        samples.addLast(sample);
    }

    public synchronized List<StatisticSample> asList() {
        return List.copyOf(samples);
    }

}
