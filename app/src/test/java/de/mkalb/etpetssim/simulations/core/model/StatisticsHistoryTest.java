package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class StatisticsHistoryTest {

    private static StatisticsSample sample(int stepCount) {
        return new StatisticsSample(stepCount, StepTimingStatistics.empty(), Map.of("a", (double) stepCount));
    }

    @Test
    void testConstructor() {
        StatisticsHistory history = new StatisticsHistory();

        assertTrue(history.toList().isEmpty());
    }

    @Test
    void testConstructorWithCapacity() {
        StatisticsHistory history = new StatisticsHistory(2);

        assertTrue(history.toList().isEmpty());
    }

    @Test
    void testConstructorWithCapacityRejectsNonPositiveCapacity() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsHistory(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsHistory(-1))
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

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(sample(1)));
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
                () -> assertEquals(2, history.toList().size())
        );
    }

}
