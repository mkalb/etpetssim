package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class StatisticsHistoryChartViewTest {

    private static final String TEST_KEY = "testKey";

    private static StatisticsSample sample(int step, Map<String, Double> values) {
        return new StatisticsSample(step, StepTimingStatistics.empty(), values);
    }

    private static List<StatisticsMetric<WatorStatistics>> singleMetric(int windowSize) {
        return List.of(new StatisticsMetric<>(TEST_KEY, "wator.observation.cells.fish",
                s -> 0.0, StatisticsExtremaMode.NONE, StatisticsChartGroup.PRIMARY, windowSize));
    }

    @BeforeAll
    void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    @Test
    void testConstructorCreatesOneChartWithTwoSeriesForWator() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        List<LineChart<Number, Number>> charts = FXTestSupport.supplyAndWaitNonNull(() ->
                new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp)
                        .chartsForTesting());

        assertNotNull(charts, "charts must not be null");
        assertEquals(1, charts.size(), "Wator PRIMARY group → one sub-chart");
        assertEquals(2, charts.getFirst().getData().size(), "fishCells + sharkCells = 2 series");
    }

    @Test
    void testConstructorCreatesTwoChartsWithOneSeriesEachForForest() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        List<LineChart<Number, Number>> charts = FXTestSupport.supplyAndWaitNonNull(() ->
                new StatisticsHistoryChartView(ForestStatistics.metrics(), historyProp)
                        .chartsForTesting());

        assertNotNull(charts, "charts must not be null");
        assertEquals(2, charts.size(), "Forest PRIMARY+SECONDARY → two sub-charts");
        assertEquals(1, charts.getFirst().getData().size(), "PRIMARY: treeCells only");
        assertEquals(1, charts.get(1).getData().size(), "SECONDARY: burningCells only");
    }

    @Test
    void testUpdateChartsHidesTitledPaneWithoutData() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Boolean managed = FXTestSupport.supplyAndWaitNonNull(() ->
                new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp)
                        .titledPane().isManaged());

        assertNotNull(managed, "TitledPane must not be null");
        assertFalse(managed, "TitledPane must be hidden before any data exists");
    }

    @Test
    void testUpdateChartsShowsTitledPaneAndChartWithData() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Boolean[] results = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp);

            historyProp.set(List.of(sample(1, Map.of(
                    WatorStatistics.KEY_FISH_CELLS, 200.0,
                    WatorStatistics.KEY_SHARK_CELLS, 50.0))));

            return new Boolean[]{
                    view.titledPane().isManaged(),
                    view.chartsForTesting().getFirst().isManaged()
            };
        });

        assertNotNull(results, "Results must not be null");
        assertAll(
                () -> assertTrue(results[0], "TitledPane must be managed after data arrives"),
                () -> assertTrue(results[1], "Sub-chart must be managed after data arrives")
        );
    }

    @Test
    void testUpdateChartsShowsChartWithAllZeroValues() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Boolean managed = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(ForestStatistics.metrics(), historyProp);

            historyProp.set(List.of(sample(0, Map.of(
                    ForestStatistics.KEY_TREE_CELLS, 500.0,
                    ForestStatistics.KEY_BURNING_CELLS, 0.0))));

            return view.chartsForTesting().get(1).isManaged();
        });

        assertNotNull(managed, "Managed flag must not be null");
        assertTrue(managed, "A group with all-zero values must be visible from the first sample, not hidden until nonzero");
    }

    @Test
    void testUpdateChartsHidesTitledPaneAfterReset() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Boolean managed = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp);

            historyProp.set(List.of(sample(1, Map.of(
                    WatorStatistics.KEY_FISH_CELLS, 200.0,
                    WatorStatistics.KEY_SHARK_CELLS, 50.0))));

            historyProp.set(List.of());

            return view.titledPane().isManaged();
        });

        assertNotNull(managed, "TitledPane must not be null after reset");
        assertFalse(managed, "TitledPane must be hidden again after shutdown reset");
    }

    @Test
    void testShutdownUnregistersHistoryListener() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Boolean managed = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp);
            view.shutdown();
            historyProp.set(List.of(sample(1, Map.of(
                    WatorStatistics.KEY_FISH_CELLS, 200.0,
                    WatorStatistics.KEY_SHARK_CELLS, 50.0))));

            return view.titledPane().isManaged();
        });

        assertFalse(managed, "Chart section must not react to history changes after shutdown");
    }

    @Test
    void testUpdateChartsAddsDataPointPerSample() {
        var history = List.of(
                sample(0, Map.of(WatorStatistics.KEY_FISH_CELLS, 100.0, WatorStatistics.KEY_SHARK_CELLS, 10.0)),
                sample(1, Map.of(WatorStatistics.KEY_FISH_CELLS, 120.0, WatorStatistics.KEY_SHARK_CELLS, 12.0)),
                sample(2, Map.of(WatorStatistics.KEY_FISH_CELLS, 110.0, WatorStatistics.KEY_SHARK_CELLS, 11.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        Integer dataCount = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp);
            return view.chartsForTesting().getFirst().getData().getFirst().getData().size();
        });

        assertEquals(3, dataCount, "All 3 history samples must appear as data points");
    }

    @Test
    void testUpdateChartsSkipsNaNValues() {
        var history = List.of(
                sample(0, Map.of(WatorStatistics.KEY_FISH_CELLS, 100.0, WatorStatistics.KEY_SHARK_CELLS, 10.0)),
                sample(1, Map.of(WatorStatistics.KEY_FISH_CELLS, Double.NaN, WatorStatistics.KEY_SHARK_CELLS, 12.0)),
                sample(2, Map.of(WatorStatistics.KEY_FISH_CELLS, 110.0, WatorStatistics.KEY_SHARK_CELLS, 11.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        Integer dataCount = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp);
            return view.chartsForTesting().getFirst().getData().getFirst().getData().size();
        });

        assertEquals(2, dataCount, "NaN step must be skipped; only 2 finite data points expected");
    }

    @Test
    void testUpdateChartsLimitsSeriesToChartWindow() {
        var history = List.of(
                sample(0, Map.of(TEST_KEY, 10.0)),
                sample(1, Map.of(TEST_KEY, 20.0)),
                sample(2, Map.of(TEST_KEY, 30.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        List<Number> xValues = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(singleMetric(2), historyProp);
            return view.chartsForTesting().getFirst().getData().getFirst().getData().stream()
                       .map(XYChart.Data::getXValue)
                       .toList();
        });

        assertEquals(List.of(1, 2), xValues, "Only the two trailing samples must appear in the series");
    }

    @Test
    void testUpdateChartsLimitsXAxisToChartWindow() {
        var history = List.of(
                sample(0, Map.of(TEST_KEY, 10.0)),
                sample(1, Map.of(TEST_KEY, 20.0)),
                sample(2, Map.of(TEST_KEY, 30.0))
        );
        var historyProp = new SimpleObjectProperty<>(history);

        Double[] xBounds = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(singleMetric(2), historyProp);
            var xAxis = (ValueAxis<Number>) view.chartsForTesting().getFirst().getXAxis();
            return new Double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
        });

        assertArrayEquals(new Double[]{1.0, 2.0}, xBounds,
                "X-axis bounds must span only the two trailing samples");
    }

    @Test
    void testUpdateChartsSetsYAxisToNiceCeiling() {
        var history = List.of(sample(1, Map.of(
                WatorStatistics.KEY_FISH_CELLS, 100.3,
                WatorStatistics.KEY_SHARK_CELLS, 50.0)));
        var historyProp = new SimpleObjectProperty<>(history);

        Double yUpper = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp);
            return ((ValueAxis<Number>) view.chartsForTesting().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(200.0, yUpper, "Y upper bound must be the 1-2-5 nice ceiling of 100.3, i.e. 200");
    }

    @Test
    void testUpdateChartsSetsMinimumYAxisCeilingToOne() {
        var history = List.of(sample(0, Map.of(TEST_KEY, 0.0)));
        var historyProp = new SimpleObjectProperty<>(history);

        Double yUpper = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(singleMetric(100), historyProp);
            return ((ValueAxis<Number>) view.chartsForTesting().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(1.0, yUpper, "Y upper bound must never be 0, so an all-zero group still renders a [0, 1] axis");
    }

    @Test
    void testUpdateChartsGrowsCeilingImmediately() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Double yUpper = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(singleMetric(100), historyProp);

            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 50.0))));
            historyProp.set(List.of(
                    sample(0, Map.of(TEST_KEY, 50.0)),
                    sample(1, Map.of(TEST_KEY, 80.0))));

            return ((ValueAxis<Number>) view.chartsForTesting().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(100.0, yUpper, "Ceiling must grow immediately from computeNiceCeiling(50)=50 to computeNiceCeiling(80)=100");
    }

    @Test
    void testUpdateChartsKeepsCeilingAboveShrinkThreshold() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Double yUpper = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(singleMetric(100), historyProp);

            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 500.0))));
            // 150 is 30% of the 500 ceiling, above the 20% shrink-gate threshold.
            historyProp.set(List.of(
                    sample(0, Map.of(TEST_KEY, 500.0)),
                    sample(1, Map.of(TEST_KEY, 150.0))));

            return ((ValueAxis<Number>) view.chartsForTesting().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(500.0, yUpper, "Ceiling must stay at 500 since the latest value did not drop below the 20% threshold");
    }

    @Test
    void testUpdateChartsShrinksCeilingAfterPeakLeavesWindow() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Double yUpper = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(singleMetric(2), historyProp);

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

            return ((ValueAxis<Number>) view.chartsForTesting().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(50.0, yUpper, "Once the peak leaves the window and the latest value stays low, the ceiling must shrink to computeNiceCeiling(50)=50");
    }

    @Test
    void testUpdateChartsResetsCeilingAfterEmptyHistory() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticsSample>of());

        Double yUpper = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(singleMetric(100), historyProp);

            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 500.0))));
            // Simulate shutdown reset, then a fresh run whose first value (150) would not
            // pass the 20% shrink gate against the stale 500 ceiling if it were not reset.
            historyProp.set(List.of());
            historyProp.set(List.of(sample(0, Map.of(TEST_KEY, 150.0))));

            return ((ValueAxis<Number>) view.chartsForTesting().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(200.0, yUpper, "After a reset, the ceiling must be computed fresh (computeNiceCeiling(150)=200), not inherited from the previous run");
    }

    @Test
    void testUpdateChartsWidensXAxisForSingleSample() {
        var history = List.of(sample(5, Map.of(
                WatorStatistics.KEY_FISH_CELLS, 100.0,
                WatorStatistics.KEY_SHARK_CELLS, 50.0)));
        var historyProp = new SimpleObjectProperty<>(history);

        Double[] xBounds = FXTestSupport.supplyAndWaitNonNull(() -> {
            var view = new StatisticsHistoryChartView(WatorStatistics.metrics(), historyProp);
            var xAxis = (ValueAxis<Number>) view.chartsForTesting().getFirst().getXAxis();
            return new Double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
        });

        assertNotNull(xBounds, "X bounds must not be null");
        assertEquals(5.0, xBounds[0], "X lower bound = step 5");
        assertEquals(6.0, xBounds[1], "X upper bound = lower + 1 for single sample");
    }

}
