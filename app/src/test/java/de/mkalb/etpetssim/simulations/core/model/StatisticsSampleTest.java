package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticsSampleTest {

    @Test
    void testRecord() {
        StepTimingStatistics timing = StepTimingStatistics.empty();
        var values = new LinkedHashMap<String, Double>();
        values.put("first", 1.0d);
        values.put("second", 2.0d);
        values.put("gap", Double.NaN);

        var sample = new StatisticsSample(7, timing, values);
        values.clear();
        var sampleAtZero = new StatisticsSample(0, timing, Map.of());

        assertAll(
                () -> assertEquals(7, sample.stepCount()),
                () -> assertSame(timing, sample.stepTimingStatistics()),
                () -> assertEquals(List.of("first", "second", "gap"), List.copyOf(sample.values().keySet())),
                () -> assertEquals(1.0d, sample.values().get("first")),
                () -> assertEquals(2.0d, sample.values().get("second")),
                () -> assertTrue(Double.isNaN(sample.values().get("gap"))),
                () -> assertThrows(UnsupportedOperationException.class, () -> sample.values().put("other", 3.0d)),
                () -> assertEquals(0, sampleAtZero.stepCount())
        );
    }

    @Test
    void testConstructorRejectsNegativeStepCount() {
        assertThrows(IllegalArgumentException.class,
                () -> new StatisticsSample(-1, StepTimingStatistics.empty(), Map.of()));
    }

}
