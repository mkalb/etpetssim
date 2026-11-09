package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;
import de.mkalb.etpetssim.simulations.core.model.StatisticsSample;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Renders one {@link LineChart} per distinct non-{@code NONE} {@link StatisticsChartGroup},
 * driven entirely by the metric descriptors. Updated whenever the history property changes.
 */
final class StatisticsHistoryChartView {

    private static final double TARGET_TICK_COUNT = 5.0;
    private static final double CHART_SPACING = 8.0;
    private static final double[] NICE_CEILING_FACTORS = {1.0, 2.0, 5.0, 10.0};
    private static final double SHRINK_THRESHOLD_RATIO = 0.2;

    private final List<GroupChart> groupCharts;
    private final Map<String, XYChart.Series<Number, Number>> seriesByKey;
    private final Map<StatisticsChartGroup, Double> ceilingByGroup;
    private final TitledPane titledPane;
    private final ReadOnlyObjectProperty<List<StatisticsSample>> historyProperty;
    private final ChangeListener<List<StatisticsSample>> historyListener;

    StatisticsHistoryChartView(
            List<? extends StatisticsMetric<?>> metrics,
            ReadOnlyObjectProperty<List<StatisticsSample>> historyProperty) {

        this.historyProperty = historyProperty;

        ceilingByGroup = new EnumMap<>(StatisticsChartGroup.class);
        seriesByKey = new HashMap<>();
        groupCharts = new ArrayList<>();
        VBox chartsBox = new VBox();
        chartsBox.setSpacing(CHART_SPACING);

        for (StatisticsChartGroup group : StatisticsChartGroup.values()) {
            if (group == StatisticsChartGroup.NONE) {
                continue;
            }
            List<? extends StatisticsMetric<?>> groupMetrics = metrics.stream()
                    .filter(m -> m.chartGroup() == group)
                    .toList();
            if (groupMetrics.isEmpty()) {
                continue;
            }

            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_CHART_AXIS_STEP));
            xAxis.setAutoRanging(false);

            NumberAxis yAxis = new NumberAxis();
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);

            LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setCreateSymbols(false);
            chart.setAnimated(false);
            chart.setLegendVisible(true);
            chart.setLegendSide(Side.TOP);
            chart.getStyleClass().add(FXStyleClasses.OBSERVATION_CHART);
            chart.setMinWidth(0);
            chart.setManaged(false);
            chart.setVisible(false);

            for (StatisticsMetric<?> metric : groupMetrics) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(AppLocalization.getText(metric.labelKey()));
                seriesByKey.put(metric.key(), series);
                chart.getData().add(series);
            }

            List<String> keys = groupMetrics.stream()
                    .map(StatisticsMetric::key)
                    .toList();
            // Metrics of one chart group share their window size (see StatisticsMetric).
            int windowSize = groupMetrics.getFirst().chartWindowSize();
            groupCharts.add(new GroupChart(group, keys, windowSize, chart, xAxis, yAxis));
            chartsBox.getChildren().add(chart);
        }

        titledPane = new TitledPane(
                AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_SECTION_CHARTS),
                chartsBox);
        titledPane.getStyleClass().add(FXStyleClasses.OBSERVATION_CHART_SECTION);
        titledPane.setExpanded(false);
        titledPane.setManaged(false);
        titledPane.setVisible(false);

        historyListener = (_, _, history) -> updateCharts(history);
        historyProperty.addListener(historyListener);
        updateCharts(historyProperty.get());
    }

    /**
     * Computes an axis tick unit that yields roughly {@link #TARGET_TICK_COUNT} major ticks
     * for the given span, without affecting the axis bounds.
     *
     * @param span the axis span
     * @return the tick unit; at least {@code 1}
     */
    private static double tickUnit(double span) {
        return Math.max(1.0, Math.ceil(span / TARGET_TICK_COUNT));
    }

    /**
     * Rounds up to the smallest {@code {1, 2, 5} x 10^n} value that is greater than or equal
     * to the given value, so the Y-axis ceiling stays stable and human-readable as the group
     * maximum grows (e.g. {@code 12 -> 20}, {@code 2495 -> 5000}). Never returns less than
     * {@code 1}, so an all-zero group still renders a usable {@code [0, 1]} axis.
     *
     * @param value the group maximum
     * @return the rounded-up ceiling
     */
    private static double computeNiceCeiling(double value) {
        if (value <= 1.0) {
            return 1.0;
        }
        double magnitude = Math.pow(10.0, Math.floor(Math.log10(value) + 1.0e-9));
        for (double factor : NICE_CEILING_FACTORS) {
            double candidate = factor * magnitude;
            if (candidate >= (value - 1.0e-9)) {
                return candidate;
            }
        }
        return 10.0 * magnitude;
    }

    /**
     * Extracts the maximum finite value across the given metric keys for a single sample.
     *
     * @param sample the sample to read
     * @param keys   the metric keys of one chart group
     * @return the maximum finite value, or {@code 0.0} if none of the keys have a finite value
     */
    private static double maxValueAcrossKeys(StatisticsSample sample, List<String> keys) {
        return keys.stream()
                .map(k -> sample.values().get(k))
                .filter(Objects::nonNull)
                .filter(Double::isFinite)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    void shutdown() {
        historyProperty.removeListener(historyListener);
    }

    TitledPane titledPane() {
        return titledPane;
    }

    List<LineChart<Number, Number>> charts() {
        return groupCharts.stream().map(GroupChart::chart).toList();
    }

    private void updateCharts(List<StatisticsSample> history) {
        boolean hasHistory = !history.isEmpty();
        if (!hasHistory) {
            ceilingByGroup.clear();
        }

        for (GroupChart groupChart : groupCharts) {
            groupChart.chart().setManaged(hasHistory);
            groupChart.chart().setVisible(hasHistory);
            if (hasHistory) {
                updateGroupChart(groupChart, history);
            }
        }

        boolean sectionVisible = hasHistory && !groupCharts.isEmpty();
        titledPane.setManaged(sectionVisible);
        titledPane.setVisible(sectionVisible);
    }

    private void updateGroupChart(GroupChart groupChart, List<StatisticsSample> history) {
        int windowSize = groupChart.windowSize();
        List<StatisticsSample> windowedHistory = (history.size() <= windowSize)
                ? history
                : history.subList(history.size() - windowSize, history.size());

        double windowedMax = windowedHistory.stream()
                .mapToDouble(s -> maxValueAcrossKeys(s, groupChart.keys()))
                .max()
                .orElse(0.0);
        double latestMax = maxValueAcrossKeys(history.getLast(), groupChart.keys());
        double currentCeiling = ceilingByGroup.getOrDefault(groupChart.group(), 0.0);
        // Grow at once; shrink only when the latest value falls below the threshold.
        double newCeiling = ((currentCeiling <= 0.0)
                || (windowedMax > currentCeiling)
                || (latestMax < (SHRINK_THRESHOLD_RATIO * currentCeiling)))
                ? computeNiceCeiling(windowedMax)
                : currentCeiling;
        ceilingByGroup.put(groupChart.group(), newCeiling);

        groupChart.yAxis().setUpperBound(newCeiling);
        groupChart.yAxis().setTickUnit(tickUnit(newCeiling));

        int xMin = windowedHistory.getFirst().stepCount();
        int xMax = windowedHistory.getLast().stepCount();
        if (xMin == xMax) {
            xMax = xMin + 1;
        }
        groupChart.xAxis().setLowerBound(xMin);
        groupChart.xAxis().setUpperBound(xMax);
        groupChart.xAxis().setTickUnit(tickUnit(xMax - xMin));

        for (String key : groupChart.keys()) {
            XYChart.Series<Number, Number> series = Objects.requireNonNull(seriesByKey.get(key));
            List<XYChart.Data<Number, Number>> data = new ArrayList<>(windowedHistory.size());
            for (StatisticsSample sample : windowedHistory) {
                Double value = sample.values().get(key);
                if ((value != null) && Double.isFinite(value)) {
                    data.add(new XYChart.Data<>(sample.stepCount(), value));
                }
            }
            series.getData().setAll(data);
        }
    }

    private record GroupChart(
            StatisticsChartGroup group,
            List<String> keys,
            int windowSize,
            LineChart<Number, Number> chart,
            NumberAxis xAxis,
            NumberAxis yAxis) {
    }

}
