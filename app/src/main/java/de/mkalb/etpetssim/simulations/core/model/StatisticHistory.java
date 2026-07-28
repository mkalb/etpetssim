package de.mkalb.etpetssim.simulations.core.model;

import java.util.*;

/**
 * Bounded in-memory history of statistic samples.
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

    public synchronized int capacity() {
        return capacity;
    }

    public synchronized int size() {
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

