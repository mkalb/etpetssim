package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticsMetricTest {

    @Test
    void testRecord() {
        var metric = new StatisticsMetric<>(
                "count",
                "count.label",
                TestStatistics::value,
                StatisticsExtremaMode.MIN_AND_MAX,
                StatisticsChartGroup.PRIMARY,
                25
        );

        assertAll(
                () -> assertEquals("count", metric.key()),
                () -> assertEquals("count.label", metric.labelKey()),
                () -> assertEquals(7.0d, metric.extractor().applyAsDouble(new TestStatistics(7))),
                () -> assertEquals(11.0d, metric.extractor().applyAsDouble(new TestStatistics(11))),
                () -> assertEquals(StatisticsExtremaMode.MIN_AND_MAX, metric.extremaMode()),
                () -> assertEquals(StatisticsChartGroup.PRIMARY, metric.chartGroup()),
                () -> assertEquals(25, metric.chartWindowSize())
        );
    }

    @Test
    void testConstructorWithoutChartGroupCreatesUnchartedMetric() {
        var metric = new StatisticsMetric<>(
                "count", "count.label", TestStatistics::value, StatisticsExtremaMode.NONE
        );

        assertAll(
                () -> assertEquals(StatisticsChartGroup.NONE, metric.chartGroup()),
                () -> assertEquals(0, metric.chartWindowSize())
        );
    }

    @Test
    void testConstructorWithChartGroupUsesDefaultWindowForChartedMetric() {
        var metric = new StatisticsMetric<>(
                "count",
                "count.label",
                TestStatistics::value,
                StatisticsExtremaMode.MAX,
                StatisticsChartGroup.SECONDARY
        );

        assertEquals(StatisticsMetric.DEFAULT_CHART_WINDOW_SIZE, metric.chartWindowSize());
    }

    @Test
    void testConstructorWithChartGroupUsesZeroWindowForUnchartedMetric() {
        var metric = new StatisticsMetric<>(
                "count",
                "count.label",
                TestStatistics::value,
                StatisticsExtremaMode.NONE,
                StatisticsChartGroup.NONE
        );

        assertEquals(0, metric.chartWindowSize());
    }

    @Test
    void testConstructorRejectsBlankKey() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "", "count.label", TestStatistics::value, StatisticsExtremaMode.NONE
                )),
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "  ", "count.label", TestStatistics::value, StatisticsExtremaMode.NONE
                ))
        );
    }

    @Test
    void testConstructorRejectsBlankLabelKey() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "count", "", TestStatistics::value, StatisticsExtremaMode.NONE
                )),
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "count", "  ", TestStatistics::value, StatisticsExtremaMode.NONE
                ))
        );
    }

    @Test
    void testConstructorRejectsNonZeroWindowForUnchartedMetric() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticsExtremaMode.NONE,
                        StatisticsChartGroup.NONE, -1
                )),
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticsExtremaMode.NONE,
                        StatisticsChartGroup.NONE, 1
                ))
        );
    }

    @Test
    void testConstructorRejectsNonPositiveWindowForChartedMetric() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticsExtremaMode.MAX,
                        StatisticsChartGroup.PRIMARY, -1
                )),
                () -> assertThrows(IllegalArgumentException.class, () -> new StatisticsMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticsExtremaMode.MAX,
                        StatisticsChartGroup.PRIMARY, 0
                ))
        );
    }

    private record TestStatistics(int value) implements SimulationStatistics {

        @Override
        public int stepCount() {
            return 0;
        }

        @NonNull
        @Override
        public GridStructure gridStructure() {
            throw new UnsupportedOperationException();
        }

    }

}
