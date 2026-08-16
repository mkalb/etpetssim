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

import static org.junit.jupiter.api.Assertions.*;

final class StatisticMetricRowTest {

    private static <S extends SimulationStatistics> StatisticExtremaMode extremaModeOf(
            List<StatisticMetric<S>> metrics, String key) {
        return metrics.stream()
                      .filter(m -> m.key().equals(key))
                      .findFirst()
                      .orElseThrow(() -> new AssertionError("Metric not found: " + key))
                      .extremaMode();
    }

    @Test
    void testConwayMetricRowLayout() {
        var metrics = ConwayStatistics.metrics();
        assertAll(
                () -> assertEquals(3, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.MIN_AND_MAX, extremaModeOf(metrics, ConwayStatistics.KEY_ALIVE_CELLS)),
                () -> assertEquals(StatisticExtremaMode.MIN_AND_MAX, extremaModeOf(metrics, ConwayStatistics.KEY_DEAD_CELLS)),
                () -> assertEquals(StatisticExtremaMode.MAX, extremaModeOf(metrics, ConwayStatistics.KEY_CHANGED_CELLS))
        );
    }

    @Test
    void testForestMetricRowLayout() {
        var metrics = ForestStatistics.metrics();
        assertAll(
                () -> assertEquals(3, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.MAX, extremaModeOf(metrics, ForestStatistics.KEY_EMPTY_CELLS)),
                () -> assertEquals(StatisticExtremaMode.MIN_AND_MAX, extremaModeOf(metrics, ForestStatistics.KEY_TREE_CELLS)),
                () -> assertEquals(StatisticExtremaMode.MAX, extremaModeOf(metrics, ForestStatistics.KEY_BURNING_CELLS))
        );
    }

    @Test
    void testWatorMetricRowLayout() {
        var metrics = WatorStatistics.metrics();
        assertAll(
                () -> assertEquals(2, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.MIN_AND_MAX, extremaModeOf(metrics, WatorStatistics.KEY_FISH_CELLS)),
                () -> assertEquals(StatisticExtremaMode.MIN_AND_MAX, extremaModeOf(metrics, WatorStatistics.KEY_SHARK_CELLS))
        );
    }

    @Test
    void testEtpetsMetricRowLayout() {
        var metrics = EtpetsStatistics.metrics();
        assertAll(
                () -> assertEquals(3, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.MIN_AND_MAX, extremaModeOf(metrics, EtpetsStatistics.KEY_ACTIVE_PET_CELLS)),
                () -> assertEquals(StatisticExtremaMode.MAX, extremaModeOf(metrics, EtpetsStatistics.KEY_EGG_CELLS)),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, EtpetsStatistics.KEY_CUMULATIVE_PET_DEATH_COUNT))
        );
    }

    @Test
    void testSnakeMetricRowLayout() {
        var metrics = SnakeStatistics.metrics();
        assertAll(
                () -> assertEquals(5, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, SnakeStatistics.KEY_SNAKE_HEAD_CELLS)),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, SnakeStatistics.KEY_LIVING_SNAKE_HEAD_CELLS)),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, SnakeStatistics.KEY_WALL_CELLS)),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, SnakeStatistics.KEY_FOOD_CELLS)),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, SnakeStatistics.KEY_CUMULATIVE_SNAKE_DEATH_COUNT))
        );
    }

    @Test
    void testSugarMetricRowLayout() {
        var metrics = SugarStatistics.metrics();
        assertAll(
                () -> assertEquals(2, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, SugarStatistics.KEY_RESOURCE_CELLS)),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, SugarStatistics.KEY_AGENT_CELLS))
        );
    }

    @Test
    void testReboundingMetricRowLayout() {
        var metrics = ReboundingStatistics.metrics();
        assertAll(
                () -> assertEquals(2, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.MAX, extremaModeOf(metrics, ReboundingStatistics.KEY_WALL_CELLS)),
                () -> assertEquals(StatisticExtremaMode.MAX, extremaModeOf(metrics, ReboundingStatistics.KEY_MOVING_ENTITY_CELLS))
        );
    }

    @Test
    void testLangtonMetricRowLayout() {
        var metrics = LangtonStatistics.metrics();
        assertAll(
                () -> assertEquals(2, metrics.size()),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, LangtonStatistics.KEY_ANT_CELLS)),
                () -> assertEquals(StatisticExtremaMode.NONE, extremaModeOf(metrics, LangtonStatistics.KEY_VISITED_CELLS))
        );
    }

    @Test
    void testChartedMetricsHaveMaxExtremaMode() {
        Stream.<List<? extends StatisticMetric<?>>>of(
                      ConwayStatistics.metrics(),
                      EtpetsStatistics.metrics(),
                      ForestStatistics.metrics(),
                      LangtonStatistics.metrics(),
                      ReboundingStatistics.metrics(),
                      SnakeStatistics.metrics(),
                      SugarStatistics.metrics(),
                      WatorStatistics.metrics()
              ).flatMap(List::stream)
              .filter(m -> m.chartGroup() != StatisticChartGroup.NONE)
              .forEach(m -> assertTrue(
                      (m.extremaMode() == StatisticExtremaMode.MAX) ||
                              (m.extremaMode() == StatisticExtremaMode.MIN_AND_MAX),
                      "Charted metric '" + m.key() + "' (group=" + m.chartGroup() +
                              ") must have extremaMode MAX or MIN_AND_MAX, but has " + m.extremaMode()
              ));
    }

}
