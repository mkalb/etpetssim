package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class StatisticHistoryChartViewTest {

    private static final String TEST_KEY = "testKey";

    private static StatisticSample sample(int step, Map<String, Double> values) {
        return new StatisticSample(step, StepTimingStatistics.empty(), values);
    }

    /**
     * A single-metric list with a configurable chart window size, for tests that need
     * precise control over which samples are still inside the trailing window.
     */
    private static List<StatisticMetric<WatorStatistics>> singleMetric(int windowSize) {
        return List.of(new StatisticMetric<>(TEST_KEY, "wator.observation.cells.fish",
                s -> 0.0, StatisticExtremaMode.NONE, StatisticChartGroup.PRIMARY, windowSize));
    }

    @BeforeAll
    void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FxTestSupport.ensureStarted();
    }

    // --- Structure: number of sub-charts and series ---

    @Test
    void testWatorMetricsProduceOneChartWithTwoSeries() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        List<LineChart<Number, Number>> charts = FxTestSupport.supplyAndWait(() ->
                new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp)
                        .chartsForTest());

        assertNotNull(charts, "charts must not be null");
        assertEquals(1, charts.size(), "Wator PRIMARY group → one sub-chart");
        assertEquals(2, charts.getFirst().getData().size(), "fishCells + sharkCells = 2 series");
    }

    @Test
    void testForestMetricsProducesTwoChartsWithOneSeriesEach() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        List<LineChart<Number, Number>> charts = FxTestSupport.supplyAndWait(() ->
                new StatisticHistoryChartView(ForestStatistics.metrics(), historyProp)
                        .chartsForTest());

        assertNotNull(charts, "charts must not be null");
        assertEquals(2, charts.size(), "Forest PRIMARY+SECONDARY → two sub-charts");
        assertEquals(1, charts.getFirst().getData().size(), "PRIMARY: treeCells only");
        assertEquals(1, charts.get(1).getData().size(), "SECONDARY: burningCells only");
    }

    // --- Visibility toggling ---

    @Test
    void testSectionHiddenWhenNoData() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Boolean managed = FxTestSupport.supplyAndWait(() ->
                new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp)
                        .titledPane().isManaged());

        assertNotNull(managed, "TitledPane must not be null");
        assertFalse(managed, "TitledPane must be hidden before any data exists");
    }

    @Test
    void testSectionAndChartVisibleAfterDataArrives() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Boolean[] results = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp);

            historyProp.set(List.of(sample(1, Map.of(
                    WatorStatistics.KEY_FISH_CELLS, 200.0,
                    WatorStatistics.KEY_SHARK_CELLS, 50.0))));

            return new Boolean[]{
                    view.titledPane().isManaged(),
                    view.chartsForTest().getFirst().isManaged()
            };
        });

        assertNotNull(results, "Results must not be null");
        assertAll(
                () -> assertTrue(results[0], "TitledPane must be managed after data arrives"),
                () -> assertTrue(results[1], "Sub-chart must be managed after data arrives")
        );
    }

    @Test
    void testChartVisibleImmediatelyEvenWhenAllValuesAreZero() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Boolean managed = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(ForestStatistics.metrics(), historyProp);

            historyProp.set(List.of(sample(0, Map.of(
                    ForestStatistics.KEY_TREE_CELLS, 500.0,
                    ForestStatistics.KEY_BURNING_CELLS, 0.0))));

            // SECONDARY chart (burningCells) is the second sub-chart.
            return view.chartsForTest().get(1).isManaged();
        });

        assertNotNull(managed, "Managed flag must not be null");
        assertTrue(managed, "A group with all-zero values must be visible from the first sample, not hidden until nonzero");
    }

    @Test
    void testSectionHiddenAgainAfterReset() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Boolean managed = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp);

            historyProp.set(List.of(sample(1, Map.of(
                    WatorStatistics.KEY_FISH_CELLS, 200.0,
                    WatorStatistics.KEY_SHARK_CELLS, 50.0))));

            // Simulate shutdown reset (Step 8 contract)
            historyProp.set(List.of());

            return view.titledPane().isManaged();
        });

        assertNotNull(managed, "TitledPane must not be null after reset");
        assertFalse(managed, "TitledPane must be hidden again after shutdown reset");
    }

    // --- Data-point counts ---

    @Test
    void testDataPointCountMatchesHistorySize() {
        var history = List.of(
                sample(0, Map.of(WatorStatistics.KEY_FISH_CELLS, 100.0, WatorStatistics.KEY_SHARK_CELLS, 10.0)),
                sample(1, Map.of(WatorStatistics.KEY_FISH_CELLS, 120.0, WatorStatistics.KEY_SHARK_CELLS, 12.0)),
                sample(2, Map.of(WatorStatistics.KEY_FISH_CELLS, 110.0, WatorStatistics.KEY_SHARK_CELLS, 11.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        Integer dataCount = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp);
            // fishCells is the first series in the PRIMARY chart
            return view.chartsForTest().getFirst().getData().getFirst().getData().size();
        });

        assertEquals(3, dataCount, "All 3 history samples must appear as data points");
    }

    @Test
    void testNaNPointsAreSkipped() {
        var history = List.of(
                sample(0, Map.of(WatorStatistics.KEY_FISH_CELLS, 100.0, WatorStatistics.KEY_SHARK_CELLS, 10.0)),
                sample(1, Map.of(WatorStatistics.KEY_FISH_CELLS, Double.NaN, WatorStatistics.KEY_SHARK_CELLS, 12.0)),
                sample(2, Map.of(WatorStatistics.KEY_FISH_CELLS, 110.0, WatorStatistics.KEY_SHARK_CELLS, 11.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        Integer dataCount = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp);
            return view.chartsForTest().getFirst().getData().getFirst().getData().size();
        });

        assertEquals(2, dataCount, "NaN step must be skipped; only 2 finite data points expected");
    }

    @Test
    void testSeriesUsesTrailingChartWindow() {
        var history = List.of(
                sample(0, Map.of(TEST_KEY, 10.0)),
                sample(1, Map.of(TEST_KEY, 20.0)),
                sample(2, Map.of(TEST_KEY, 30.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        List<Number> xValues = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(singleMetric(2), historyProp);
            return view.chartsForTest().getFirst().getData().getFirst().getData().stream()
                       .map(XYChart.Data::getXValue)
                       .toList();
        });

        assertEquals(List.of(1, 2), xValues, "Only the two trailing samples must appear in the series");
    }

    @Test
    void testXAxisUsesTrailingChartWindow() {
        var history = List.of(
                sample(0, Map.of(TEST_KEY, 10.0)),
                sample(1, Map.of(TEST_KEY, 20.0)),
                sample(2, Map.of(TEST_KEY, 30.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        Double[] xBounds = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(singleMetric(2), historyProp);
            var xAxis = (ValueAxis<Number>) view.chartsForTest().getFirst().getXAxis();
            return new Double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
        });

        assertArrayEquals(new Double[]{1.0, 2.0}, xBounds,
                "X-axis bounds must span only the two trailing samples");
    }

    // --- Y-axis bounds: nice ceiling, minimum, growth, shrink, reset ---

    @Test
    void testYAxisUpperBoundIsNiceCeilingOfWindowedMax() {
        var history = List.of(sample(1, Map.of(
                WatorStatistics.KEY_FISH_CELLS, 100.3,
                WatorStatistics.KEY_SHARK_CELLS, 50.0)));
        var historyProp = new SimpleObjectProperty<>(history);

        Double yUpper = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp);
            return ((ValueAxis<Number>) view.chartsForTest().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(200.0, yUpper, "Y upper bound must be the 1-2-5 nice ceiling of 100.3, i.e. 200");
    }

    @Test
    void testYAxisMinimumCeilingIsOneWhenAllValuesAreZero() {
        var history = List.of(sample(0, Map.of(TEST_KEY, 0.0)));
        var historyProp = new SimpleObjectProperty<>(history);

        Double yUpper = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(singleMetric(100), historyProp);
            return ((ValueAxis<Number>) view.chartsForTest().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(1.0, yUpper, "Y upper bound must never be 0, so an all-zero group still renders a [0, 1] axis");
    }

    @Test
    void testCeilingGrowsImmediatelyWhenWindowedMaxIncreases() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Double yUpper = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(singleMetric(100), historyProp);

            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 50.0))));
            historyProp.set(List.of(
                    sample(0, Map.of(TEST_KEY, 50.0)),
                    sample(1, Map.of(TEST_KEY, 80.0))));

            return ((ValueAxis<Number>) view.chartsForTest().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(100.0, yUpper, "Ceiling must grow immediately from niceCeiling(50)=50 to niceCeiling(80)=100");
    }

    @Test
    void testCeilingDoesNotShrinkWhenLatestValueAboveThreshold() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Double yUpper = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(singleMetric(100), historyProp);

            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 500.0))));
            // 150 is 30% of the 500 ceiling, above the 20% shrink-gate threshold.
            historyProp.set(List.of(
                    sample(0, Map.of(TEST_KEY, 500.0)),
                    sample(1, Map.of(TEST_KEY, 150.0))));

            return ((ValueAxis<Number>) view.chartsForTest().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(500.0, yUpper, "Ceiling must stay at 500 since the latest value did not drop below the 20% threshold");
    }

    @Test
    void testCeilingShrinksAfterLatestValueBelowThresholdAndPeakLeavesWindow() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Double yUpper = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(singleMetric(2), historyProp);

            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 500.0))));
            historyProp.set(List.of(
                    sample(0, Map.of(TEST_KEY, 500.0)),
                    sample(1, Map.of(TEST_KEY, 500.0))));
            historyProp.set(List.of(
                    sample(0, Map.of(TEST_KEY, 500.0)),
                    sample(1, Map.of(TEST_KEY, 500.0)),
                    sample(2, Map.of(TEST_KEY, 50.0))));
            // Window size 2: the 500-peak at step 1 has now left the trailing window [step2, step3].
            historyProp.set(List.of(
                    sample(0, Map.of(TEST_KEY, 500.0)),
                    sample(1, Map.of(TEST_KEY, 500.0)),
                    sample(2, Map.of(TEST_KEY, 50.0)),
                    sample(3, Map.of(TEST_KEY, 40.0))));

            return ((ValueAxis<Number>) view.chartsForTest().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(50.0, yUpper, "Once the peak leaves the window and the latest value stays low, the ceiling must shrink to niceCeiling(50)=50");
    }

    @Test
    void testCeilingResetsToFreshValueAfterHistoryEmptied() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());

        Double yUpper = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(singleMetric(100), historyProp);

            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 500.0))));
            // Simulate shutdown reset, then a fresh run whose first value (150) would not
            // pass the 20% shrink gate against the stale 500 ceiling if it were not reset.
            historyProp.set(List.of());
            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 150.0))));

            return ((ValueAxis<Number>) view.chartsForTest().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(200.0, yUpper, "After a reset, the ceiling must be computed fresh (niceCeiling(150)=200), not inherited from the previous run");
    }

    // --- X-axis single-sample guard ---

    @Test
    void testSingleSampleXAxisUpperIsLowerPlusOne() {
        var history = List.of(sample(5, Map.of(
                WatorStatistics.KEY_FISH_CELLS, 100.0,
                WatorStatistics.KEY_SHARK_CELLS, 50.0)));
        var historyProp = new SimpleObjectProperty<>(history);

        Double[] xBounds = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp);
            var xAxis = (ValueAxis<Number>) view.chartsForTest().getFirst().getXAxis();
            return new Double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
        });

        assertNotNull(xBounds, "X bounds must not be null");
        assertEquals(5.0, xBounds[0], "X lower bound = step 5");
        assertEquals(6.0, xBounds[1], "X upper bound = lower + 1 for single sample");
    }

}
