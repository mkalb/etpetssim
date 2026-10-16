package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("MagicNumber")
final class StatisticsMetricLayoutTest {

    private static void assertMetricLayout(
            List<? extends StatisticsMetric<?>> metrics,
            ExpectedMetric... expectedMetrics) {
        List<ExpectedMetric> actualMetrics = metrics.stream()
                                                    .map(metric -> new ExpectedMetric(
                                                            metric.key(), metric.extremaMode(), metric.chartGroup()))
                                                    .toList();
        assertEquals(List.of(expectedMetrics), actualMetrics);
    }

    private static void assertChartWindowSizes(
            List<? extends StatisticsMetric<?>> metrics,
            int... expectedWindowSizes) {
        List<Integer> actualWindowSizes = metrics.stream()
                                                 .map(StatisticsMetric::chartWindowSize)
                                                 .toList();
        assertEquals(Arrays.stream(expectedWindowSizes).boxed().toList(), actualWindowSizes);
    }

    @Test
    void testConwayLayout() {
        assertMetricLayout(
                ConwayStatistics.metrics(),
                new ExpectedMetric(ConwayStatistics.KEY_ALIVE_CELLS,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY),
                new ExpectedMetric(ConwayStatistics.KEY_DEAD_CELLS,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.NONE),
                new ExpectedMetric(ConwayStatistics.KEY_CHANGED_CELLS,
                        StatisticsExtremaMode.MAX, StatisticsChartGroup.NONE)
        );
    }

    @Test
    void testForestLayout() {
        assertMetricLayout(
                ForestStatistics.metrics(),
                new ExpectedMetric(ForestStatistics.KEY_EMPTY_CELLS,
                        StatisticsExtremaMode.MAX, StatisticsChartGroup.NONE),
                new ExpectedMetric(ForestStatistics.KEY_TREE_CELLS,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY),
                new ExpectedMetric(ForestStatistics.KEY_BURNING_CELLS,
                        StatisticsExtremaMode.MAX, StatisticsChartGroup.SECONDARY)
        );
    }

    @Test
    void testWatorLayout() {
        assertMetricLayout(
                WatorStatistics.metrics(),
                new ExpectedMetric(WatorStatistics.KEY_FISH_CELLS,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY),
                new ExpectedMetric(WatorStatistics.KEY_SHARK_CELLS,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY)
        );
    }

    @Test
    void testEtpetsLayout() {
        assertMetricLayout(
                EtpetsStatistics.metrics(),
                new ExpectedMetric(EtpetsStatistics.KEY_ACTIVE_PET_CELLS,
                        StatisticsExtremaMode.MIN_AND_MAX, StatisticsChartGroup.PRIMARY),
                new ExpectedMetric(EtpetsStatistics.KEY_EGG_CELLS,
                        StatisticsExtremaMode.MAX, StatisticsChartGroup.NONE),
                new ExpectedMetric(EtpetsStatistics.KEY_CUMULATIVE_PET_DEATH_COUNT,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE)
        );
    }

    @Test
    void testSnakeLayout() {
        assertMetricLayout(
                SnakeStatistics.metrics(),
                new ExpectedMetric(SnakeStatistics.KEY_SNAKE_HEAD_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_LIVING_SNAKE_HEAD_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_WALL_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_FOOD_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_CUMULATIVE_SNAKE_DEATH_COUNT,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE)
        );
    }

    @Test
    void testSugarLayout() {
        assertMetricLayout(
                SugarStatistics.metrics(),
                new ExpectedMetric(SugarStatistics.KEY_RESOURCE_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE),
                new ExpectedMetric(SugarStatistics.KEY_AGENT_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE)
        );
    }

    @Test
    void testReboundingLayout() {
        assertMetricLayout(
                ReboundingStatistics.metrics(),
                new ExpectedMetric(ReboundingStatistics.KEY_WALL_CELLS,
                        StatisticsExtremaMode.MAX, StatisticsChartGroup.NONE),
                new ExpectedMetric(ReboundingStatistics.KEY_MOVING_ENTITY_CELLS,
                        StatisticsExtremaMode.MAX, StatisticsChartGroup.NONE)
        );
    }

    @Test
    void testLangtonLayout() {
        assertMetricLayout(
                LangtonStatistics.metrics(),
                new ExpectedMetric(LangtonStatistics.KEY_ANT_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE),
                new ExpectedMetric(LangtonStatistics.KEY_VISITED_CELLS,
                        StatisticsExtremaMode.NONE, StatisticsChartGroup.NONE)
        );
    }

    @Test
    void testChartWindowSizes() {
        assertChartWindowSizes(ConwayStatistics.metrics(), 50, 0, 0);
        assertChartWindowSizes(EtpetsStatistics.metrics(), 1_000, 0, 0);
        assertChartWindowSizes(ForestStatistics.metrics(), 0, StatisticsMetric.DEFAULT_CHART_WINDOW_SIZE,
                StatisticsMetric.DEFAULT_CHART_WINDOW_SIZE);
        assertChartWindowSizes(LangtonStatistics.metrics(), 0, 0);
        assertChartWindowSizes(ReboundingStatistics.metrics(), 0, 0);
        assertChartWindowSizes(SnakeStatistics.metrics(), 0, 0, 0, 0, 0);
        assertChartWindowSizes(SugarStatistics.metrics(), 0, 0);
        assertChartWindowSizes(WatorStatistics.metrics(), 500, 500);
    }

    @Test
    void testChartedMetricsShareWindowSizeWithinGroup() {
        // StatisticsHistoryChartView is built per simulation, so the shared-window-size invariant
        // is scoped to one simulation's metrics() list, not across simulations reusing the same group.
        Stream.<List<? extends StatisticsMetric<?>>>of(
                ConwayStatistics.metrics(),
                EtpetsStatistics.metrics(),
                ForestStatistics.metrics(),
                LangtonStatistics.metrics(),
                ReboundingStatistics.metrics(),
                SnakeStatistics.metrics(),
                SugarStatistics.metrics(),
                WatorStatistics.metrics()
        ).forEach(metrics -> {
            Map<StatisticsChartGroup, Set<Integer>> windowSizesByGroup = metrics.stream()
                                                                                .filter(m -> m.chartGroup() != StatisticsChartGroup.NONE)
                                                                                .collect(Collectors.groupingBy(
                                                                                        StatisticsMetric::chartGroup,
                                                                                        Collectors.mapping(StatisticsMetric::chartWindowSize, Collectors.toSet())));
            windowSizesByGroup.forEach((group, windowSizes) -> assertEquals(1, windowSizes.size(),
                    "Metrics in chartGroup " + group + " must share the same chartWindowSize, but found " + windowSizes));
        });
    }

    private record ExpectedMetric(
            String key,
            StatisticsExtremaMode extremaMode,
            StatisticsChartGroup chartGroup) {}

}
