package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticSampleTest {

    @Test
    void testRecordComponents() {
        StepTimingStatistics timing = StepTimingStatistics.empty();
        var sample = new StatisticSample(7, timing, Map.of("count", 42.0d));

        assertAll(
                () -> assertEquals(7, sample.stepCount()),
                () -> assertSame(timing, sample.stepTimingStatistics()),
                () -> assertEquals(42.0d, sample.values().get("count"))
        );
    }

    @Test
    void testConstructorAcceptsZeroStepCount() {
        var sample = new StatisticSample(0, StepTimingStatistics.empty(), Map.of());

        assertEquals(0, sample.stepCount());
    }

    @Test
    void testConstructorRejectsNegativeStepCount() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new StatisticSample(-1, StepTimingStatistics.empty(), Map.of())
        );

        assertTrue(exception.getMessage().contains("stepCount"));
    }

    @Test
    void testConstructorDefensivelyCopiesValuesAndPreservesOrder() {
        var values = new LinkedHashMap<String, Double>();
        values.put("first", 1.0d);
        values.put("second", 2.0d);

        var sample = new StatisticSample(0, StepTimingStatistics.empty(), values);
        values.clear();

        assertAll(
                () -> assertEquals(List.of("first", "second"), List.copyOf(sample.values().keySet())),
                () -> assertEquals(1.0d, sample.values().get("first")),
                () -> assertEquals(2.0d, sample.values().get("second"))
        );
    }

    @Test
    void testValuesIsUnmodifiable() {
        var sample = new StatisticSample(0, StepTimingStatistics.empty(), Map.of("count", 1.0d));

        assertNotNull(assertThrows(
                UnsupportedOperationException.class,
                () -> sample.values().put("other", 2.0d)
        ));
    }

    @Test
    void testConstructorPreservesNaNSentinel() {
        var sample = new StatisticSample(0, StepTimingStatistics.empty(), Map.of("count", Double.NaN));

        assertTrue(Double.isNaN(sample.values().get("count")));
    }

}
