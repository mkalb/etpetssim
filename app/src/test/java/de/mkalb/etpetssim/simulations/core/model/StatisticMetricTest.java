package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class StatisticMetricTest {

    @Test
    void testCanonicalConstructorStoresComponentsAndExtractsValue() {
        var metric = new StatisticMetric<>(
                "count",
                "count.label",
                TestStatistics::value,
                StatisticExtremaMode.MIN_AND_MAX,
                StatisticChartGroup.PRIMARY,
                25
        );

        assertAll(
                () -> assertEquals("count", metric.key()),
                () -> assertEquals("count.label", metric.labelKey()),
                () -> assertEquals(7.0d, metric.extractor().applyAsDouble(new TestStatistics(7))),
                () -> assertEquals(11.0d, metric.extractor().applyAsDouble(new TestStatistics(11))),
                () -> assertEquals(StatisticExtremaMode.MIN_AND_MAX, metric.extremaMode()),
                () -> assertEquals(StatisticChartGroup.PRIMARY, metric.chartGroup()),
                () -> assertEquals(25, metric.chartWindowSize())
        );
    }

    @Test
    void testFourArgumentConstructorCreatesUnchartedMetric() {
        var metric = new StatisticMetric<>(
                "count", "count.label", TestStatistics::value, StatisticExtremaMode.NONE
        );

        assertAll(
                () -> assertEquals(StatisticChartGroup.NONE, metric.chartGroup()),
                () -> assertEquals(0, metric.chartWindowSize())
        );
    }

    @Test
    void testFiveArgumentConstructorUsesDefaultWindowForChartedMetric() {
        var metric = new StatisticMetric<>(
                "count",
                "count.label",
                TestStatistics::value,
                StatisticExtremaMode.MAX,
                StatisticChartGroup.SECONDARY
        );

        assertEquals(StatisticMetric.DEFAULT_CHART_WINDOW_SIZE, metric.chartWindowSize());
    }

    @Test
    void testFiveArgumentConstructorUsesZeroWindowForUnchartedMetric() {
        var metric = new StatisticMetric<>(
                "count",
                "count.label",
                TestStatistics::value,
                StatisticExtremaMode.NONE,
                StatisticChartGroup.NONE
        );

        assertEquals(0, metric.chartWindowSize());
    }

    @Test
    void testConstructorRejectsBlankKey() {
        assertAll(
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "", "count.label", TestStatistics::value, StatisticExtremaMode.NONE
                ))),
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "  ", "count.label", TestStatistics::value, StatisticExtremaMode.NONE
                )))
        );
    }

    @Test
    void testConstructorRejectsBlankLabelKey() {
        assertAll(
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "count", "", TestStatistics::value, StatisticExtremaMode.NONE
                ))),
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "count", "  ", TestStatistics::value, StatisticExtremaMode.NONE
                )))
        );
    }

    @Test
    void testConstructorRejectsNonZeroWindowForUnchartedMetric() {
        assertAll(
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticExtremaMode.NONE,
                        StatisticChartGroup.NONE, -1
                ))),
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticExtremaMode.NONE,
                        StatisticChartGroup.NONE, 1
                )))
        );
    }

    @Test
    void testConstructorRejectsNonPositiveWindowForChartedMetric() {
        assertAll(
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticExtremaMode.MAX,
                        StatisticChartGroup.PRIMARY, -1
                ))),
                () -> assertNotNull(assertThrows(IllegalArgumentException.class, () -> new StatisticMetric<>(
                        "count", "count.label", TestStatistics::value, StatisticExtremaMode.MAX,
                        StatisticChartGroup.PRIMARY, 0
                )))
        );
    }

    private record TestStatistics(int value) implements SimulationStatistics {

        @Override
        public int getStepCount() {
            return 0;
        }

        @Override
        public GridStructure getGridStructure() {
            throw new UnsupportedOperationException();
        }

    }

}
