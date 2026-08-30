package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticHistoryTest {

    private static StatisticSample sample(int stepCount) {
        return new StatisticSample(stepCount, StepTimingStatistics.empty(), Map.of("a", (double) stepCount));
    }

    @Test
    void testDefaultConstructorUsesDefaultCapacity() {
        StatisticHistory history = new StatisticHistory();

        assertEquals(StatisticHistory.DEFAULT_CAPACITY, history.capacity());
    }

    @Test
    void testConstructorUsesCapacity() {
        StatisticHistory history = new StatisticHistory(2);

        assertEquals(2, history.capacity());
    }

    @Test
    void testConstructorRejectsNonPositiveCapacity() {
        assertAll(
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticHistory(0))),
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticHistory(-1)))
        );
    }

    @Test
    void testInitialStateIsEmpty() {
        StatisticHistory history = new StatisticHistory(2);

        assertAll(
                () -> assertEquals(0, history.size()),
                () -> assertTrue(history.asList().isEmpty())
        );
    }

    @Test
    void testAddEvictsOldestSampleAtCapacity() {
        StatisticHistory history = new StatisticHistory(2);

        history.add(sample(0));
        history.add(sample(1));
        history.add(sample(2));

        var samples = history.asList();
        assertAll(
                () -> assertEquals(2, history.size()),
                () -> assertEquals(2, samples.size()),
                () -> assertEquals(1, samples.getFirst().stepCount()),
                () -> assertEquals(2, samples.getLast().stepCount())
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testAsListReturnsImmutableSnapshot() {
        StatisticHistory history = new StatisticHistory(2);
        history.add(sample(0));

        var snapshot = history.asList();

        assertNotNull(assertThrows(UnsupportedOperationException.class, () -> snapshot.add(sample(1))));
    }

    @Test
    void testAsListReturnsIndependentSnapshot() {
        StatisticHistory history = new StatisticHistory(2);
        history.add(sample(0));
        var snapshot = history.asList();

        history.add(sample(1));

        assertAll(
                () -> assertEquals(1, snapshot.size()),
                () -> assertEquals(0, snapshot.getFirst().stepCount()),
                () -> assertEquals(2, history.size())
        );
    }

    @Test
    void testClearRemovesSamples() {
        StatisticHistory history = new StatisticHistory(2);
        history.add(sample(0));

        history.clear();

        assertAll(
                () -> assertEquals(0, history.size()),
                () -> assertTrue(history.asList().isEmpty())
        );
    }

}

