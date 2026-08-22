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

final class StatisticMetricRowTest {

    private static void assertMetricLayout(
            List<? extends StatisticMetric<?>> metrics,
            ExpectedMetric... expectedMetrics) {
        List<ExpectedMetric> actualMetrics = metrics.stream()
                                                    .map(metric -> new ExpectedMetric(
                                                            metric.key(), metric.extremaMode(), metric.chartGroup()))
                                                    .toList();
        assertEquals(List.of(expectedMetrics), actualMetrics);
    }

    @Test
    void testConwayMetricRowLayout() {
        assertMetricLayout(
                ConwayStatistics.metrics(),
                new ExpectedMetric(ConwayStatistics.KEY_ALIVE_CELLS,
                        StatisticExtremaMode.MIN_AND_MAX, StatisticChartGroup.PRIMARY),
                new ExpectedMetric(ConwayStatistics.KEY_DEAD_CELLS,
                        StatisticExtremaMode.MIN_AND_MAX, StatisticChartGroup.NONE),
                new ExpectedMetric(ConwayStatistics.KEY_CHANGED_CELLS,
                        StatisticExtremaMode.MAX, StatisticChartGroup.NONE)
        );
    }

    @Test
    void testForestMetricRowLayout() {
        assertMetricLayout(
                ForestStatistics.metrics(),
                new ExpectedMetric(ForestStatistics.KEY_EMPTY_CELLS,
                        StatisticExtremaMode.MAX, StatisticChartGroup.NONE),
                new ExpectedMetric(ForestStatistics.KEY_TREE_CELLS,
                        StatisticExtremaMode.MIN_AND_MAX, StatisticChartGroup.PRIMARY),
                new ExpectedMetric(ForestStatistics.KEY_BURNING_CELLS,
                        StatisticExtremaMode.MAX, StatisticChartGroup.SECONDARY)
        );
    }

    @Test
    void testWatorMetricRowLayout() {
        assertMetricLayout(
                WatorStatistics.metrics(),
                new ExpectedMetric(WatorStatistics.KEY_FISH_CELLS,
                        StatisticExtremaMode.MIN_AND_MAX, StatisticChartGroup.PRIMARY),
                new ExpectedMetric(WatorStatistics.KEY_SHARK_CELLS,
                        StatisticExtremaMode.MIN_AND_MAX, StatisticChartGroup.PRIMARY)
        );
    }

    @Test
    void testEtpetsMetricRowLayout() {
        assertMetricLayout(
                EtpetsStatistics.metrics(),
                new ExpectedMetric(EtpetsStatistics.KEY_ACTIVE_PET_CELLS,
                        StatisticExtremaMode.MIN_AND_MAX, StatisticChartGroup.PRIMARY),
                new ExpectedMetric(EtpetsStatistics.KEY_EGG_CELLS,
                        StatisticExtremaMode.MAX, StatisticChartGroup.NONE),
                new ExpectedMetric(EtpetsStatistics.KEY_CUMULATIVE_PET_DEATH_COUNT,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE)
        );
    }

    @Test
    void testSnakeMetricRowLayout() {
        assertMetricLayout(
                SnakeStatistics.metrics(),
                new ExpectedMetric(SnakeStatistics.KEY_SNAKE_HEAD_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_LIVING_SNAKE_HEAD_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_WALL_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_FOOD_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE),
                new ExpectedMetric(SnakeStatistics.KEY_CUMULATIVE_SNAKE_DEATH_COUNT,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE)
        );
    }

    @Test
    void testSugarMetricRowLayout() {
        assertMetricLayout(
                SugarStatistics.metrics(),
                new ExpectedMetric(SugarStatistics.KEY_RESOURCE_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE),
                new ExpectedMetric(SugarStatistics.KEY_AGENT_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE)
        );
    }

    @Test
    void testReboundingMetricRowLayout() {
        assertMetricLayout(
                ReboundingStatistics.metrics(),
                new ExpectedMetric(ReboundingStatistics.KEY_WALL_CELLS,
                        StatisticExtremaMode.MAX, StatisticChartGroup.NONE),
                new ExpectedMetric(ReboundingStatistics.KEY_MOVING_ENTITY_CELLS,
                        StatisticExtremaMode.MAX, StatisticChartGroup.NONE)
        );
    }

    @Test
    void testLangtonMetricRowLayout() {
        assertMetricLayout(
                LangtonStatistics.metrics(),
                new ExpectedMetric(LangtonStatistics.KEY_ANT_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE),
                new ExpectedMetric(LangtonStatistics.KEY_VISITED_CELLS,
                        StatisticExtremaMode.NONE, StatisticChartGroup.NONE)
        );
    }

    @Test
    void testChartedMetricsShareWindowSizeWithinGroup() {
        // StatisticHistoryChartView is built per simulation, so the shared-window-size invariant
        // is scoped to one simulation's metrics() list, not across simulations reusing the same group.
        Stream.<List<? extends StatisticMetric<?>>>of(
                ConwayStatistics.metrics(),
                EtpetsStatistics.metrics(),
                ForestStatistics.metrics(),
                LangtonStatistics.metrics(),
                ReboundingStatistics.metrics(),
                SnakeStatistics.metrics(),
                SugarStatistics.metrics(),
                WatorStatistics.metrics()
        ).forEach(metrics -> {
            Map<StatisticChartGroup, Set<Integer>> windowSizesByGroup = metrics.stream()
                                                                               .filter(m -> m.chartGroup() != StatisticChartGroup.NONE)
                                                                               .collect(Collectors.groupingBy(
                                                                                       StatisticMetric::chartGroup,
                                                                                       Collectors.mapping(StatisticMetric::chartWindowSize, Collectors.toSet())));
            windowSizesByGroup.forEach((group, windowSizes) -> assertEquals(1, windowSizes.size(),
                    "Metrics in chartGroup " + group + " must share the same chartWindowSize, but found " + windowSizes));
        });
    }

    private record ExpectedMetric(
            String key,
            StatisticExtremaMode extremaMode,
            StatisticChartGroup chartGroup) {}

}
