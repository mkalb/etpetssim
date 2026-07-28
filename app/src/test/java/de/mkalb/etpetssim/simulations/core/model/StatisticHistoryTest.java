package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

final class StatisticHistoryTest {

    @Test
    void testAddEvictsOldestSampleAtCapacity() {
        StatisticHistory history = new StatisticHistory(2);

        history.add(new StatisticSample(0, StepTimingStatistics.empty(), Map.of("a", 1.0d)));
        history.add(new StatisticSample(1, StepTimingStatistics.empty(), Map.of("a", 2.0d)));
        history.add(new StatisticSample(2, StepTimingStatistics.empty(), Map.of("a", 3.0d)));

        var samples = history.asList();
        assertAll(
                () -> assertEquals(2, samples.size()),
                () -> assertEquals(1, samples.getFirst().stepCount()),
                () -> assertEquals(2, samples.getLast().stepCount())
        );
    }

    @Test
    void testAsListReturnsImmutableSnapshot() {
        StatisticHistory history = new StatisticHistory(2);
        history.add(new StatisticSample(0, StepTimingStatistics.empty(), Map.of("a", 1.0d)));

        var snapshot = history.asList();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new StatisticSample(1, StepTimingStatistics.empty(), Map.of())));
    }

}

