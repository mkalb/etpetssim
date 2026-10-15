package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticsHistoryTest {

    private static StatisticsSample sample(int stepCount) {
        return new StatisticsSample(stepCount, StepTimingStatistics.empty(), Map.of("a", (double) stepCount));
    }

    @Test
    void testDefaultConstructorUsesDefaultCapacity() {
        StatisticsHistory history = new StatisticsHistory();

        assertEquals(StatisticsHistory.DEFAULT_CAPACITY, history.capacity());
    }

    @Test
    void testConstructorUsesCapacity() {
        StatisticsHistory history = new StatisticsHistory(2);

        assertEquals(2, history.capacity());
    }

    @Test
    void testConstructorRejectsNonPositiveCapacity() {
        assertAll(
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticsHistory(0))),
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticsHistory(-1)))
        );
    }

    @Test
    void testInitialStateIsEmpty() {
        StatisticsHistory history = new StatisticsHistory(2);

        assertAll(
                () -> assertEquals(0, history.size()),
                () -> assertTrue(history.toList().isEmpty())
        );
    }

    @Test
    void testAddEvictsOldestSampleAtCapacity() {
        StatisticsHistory history = new StatisticsHistory(2);

        history.add(sample(0));
        history.add(sample(1));
        history.add(sample(2));

        var samples = history.toList();
        assertAll(
                () -> assertEquals(2, history.size()),
                () -> assertEquals(2, samples.size()),
                () -> assertEquals(1, samples.getFirst().stepCount()),
                () -> assertEquals(2, samples.getLast().stepCount())
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testToListReturnsImmutableSnapshot() {
        StatisticsHistory history = new StatisticsHistory(2);
        history.add(sample(0));

        var snapshot = history.toList();

        assertNotNull(assertThrows(UnsupportedOperationException.class, () -> snapshot.add(sample(1))));
    }

    @Test
    void testToListReturnsIndependentSnapshot() {
        StatisticsHistory history = new StatisticsHistory(2);
        history.add(sample(0));
        var snapshot = history.toList();

        history.add(sample(1));

        assertAll(
                () -> assertEquals(1, snapshot.size()),
                () -> assertEquals(0, snapshot.getFirst().stepCount()),
                () -> assertEquals(2, history.size())
        );
    }

    @Test
    void testClearRemovesSamples() {
        StatisticsHistory history = new StatisticsHistory(2);
        history.add(sample(0));

        history.clear();

        assertAll(
                () -> assertEquals(0, history.size()),
                () -> assertTrue(history.toList().isEmpty())
        );
    }

}

