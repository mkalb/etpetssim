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

    private static StatisticSample sample(int step, Map<String, Double> values) {
        return new StatisticSample(step, StepTimingStatistics.empty(), values);
    }

    @SuppressWarnings("SameParameterValue")
    private static StatisticExtrema maxExtrema(String key, double value) {
        return new StatisticExtrema(Map.of(), Map.of(key, new StatisticExtremum(value, 1L)));
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
    void testWatorMetrics_oneChartWithTwoSeries() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());
        var extremaProp = new SimpleObjectProperty<>(StatisticExtrema.empty());

        List<LineChart<Number, Number>> charts = FxTestSupport.supplyAndWait(() ->
                new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp)
                        .chartsForTest());

        assertNotNull(charts, "charts must not be null");
        assertEquals(1, charts.size(), "Wator PRIMARY group → one sub-chart");
        assertEquals(2, charts.getFirst().getData().size(), "fishCells + sharkCells = 2 series");
    }

    @Test
    void testForestMetricsProducesTwoChartsWithOneSeriesEach() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());
        var extremaProp = new SimpleObjectProperty<>(StatisticExtrema.empty());

        List<LineChart<Number, Number>> charts = FxTestSupport.supplyAndWait(() ->
                new StatisticHistoryChartView(ForestStatistics.metrics(), historyProp, extremaProp)
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
        var extremaProp = new SimpleObjectProperty<>(StatisticExtrema.empty());

        Boolean managed = FxTestSupport.supplyAndWait(() ->
                new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp)
                        .titledPane().isManaged());

        assertNotNull(managed, "TitledPane must not be null");
        assertFalse(managed, "TitledPane must be hidden before any data exists");
    }

    @Test
    void testSectionAndChartVisibleAfterDataArrives() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());
        var extremaProp = new SimpleObjectProperty<>(StatisticExtrema.empty());

        Boolean[] results = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp);

            extremaProp.set(maxExtrema(WatorStatistics.KEY_FISH_CELLS, 200.0));
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
    void testSectionHiddenAgainAfterReset() {
        var historyProp = new SimpleObjectProperty<>(List.<StatisticSample>of());
        var extremaProp = new SimpleObjectProperty<>(StatisticExtrema.empty());

        Boolean managed = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp);

            extremaProp.set(maxExtrema(WatorStatistics.KEY_FISH_CELLS, 200.0));
            historyProp.set(List.of(sample(1, Map.of(
                    WatorStatistics.KEY_FISH_CELLS, 200.0,
                    WatorStatistics.KEY_SHARK_CELLS, 50.0))));

            // Simulate shutdown reset (Step 8 contract)
            extremaProp.set(StatisticExtrema.empty());
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
        var extrema = maxExtrema(WatorStatistics.KEY_FISH_CELLS, 120.0);
        var historyProp = new SimpleObjectProperty<>(history);
        var extremaProp = new SimpleObjectProperty<>(extrema);

        Integer dataCount = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp);
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
        var extrema = maxExtrema(WatorStatistics.KEY_FISH_CELLS, 110.0);
        var historyProp = new SimpleObjectProperty<>(history);
        var extremaProp = new SimpleObjectProperty<>(extrema);

        Integer dataCount = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp);
            return view.chartsForTest().getFirst().getData().getFirst().getData().size();
        });

        assertEquals(2, dataCount, "NaN step must be skipped; only 2 finite data points expected");
    }

    // --- Y-axis bounds ---

    @Test
    void testYAxisUpperBoundIsCeilOfGroupMax() {
        var history = List.of(sample(1, Map.of(
                WatorStatistics.KEY_FISH_CELLS, 100.3,
                WatorStatistics.KEY_SHARK_CELLS, 50.0)));
        var extrema = maxExtrema(WatorStatistics.KEY_FISH_CELLS, 100.3);
        var historyProp = new SimpleObjectProperty<>(history);
        var extremaProp = new SimpleObjectProperty<>(extrema);

        Double yUpper = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp);
            return ((ValueAxis<Number>) view.chartsForTest().getFirst().getYAxis()).getUpperBound();
        });

        assertEquals(101.0, yUpper, "Y upper bound must be ceil(100.3) = 101");
    }

    // --- X-axis single-sample guard ---

    @Test
    void testSingleSampleXAxisUpperIsLowerPlusOne() {
        var history = List.of(sample(5, Map.of(
                WatorStatistics.KEY_FISH_CELLS, 100.0,
                WatorStatistics.KEY_SHARK_CELLS, 50.0)));
        var extrema = maxExtrema(WatorStatistics.KEY_FISH_CELLS, 100.0);
        var historyProp = new SimpleObjectProperty<>(history);
        var extremaProp = new SimpleObjectProperty<>(extrema);

        Double[] xBounds = FxTestSupport.supplyAndWait(() -> {
            var view = new StatisticHistoryChartView(WatorStatistics.metrics(), historyProp, extremaProp);
            var xAxis = (ValueAxis<Number>) view.chartsForTest().getFirst().getXAxis();
            return new Double[]{xAxis.getLowerBound(), xAxis.getUpperBound()};
        });

        assertNotNull(xBounds, "X bounds must not be null");
        assertEquals(5.0, xBounds[0], "X lower bound = step 5");
        assertEquals(6.0, xBounds[1], "X upper bound = lower + 1 for single sample");
    }

}
