package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.*;

/**
 * Renders one {@link LineChart} per distinct non-{@code NONE} {@link StatisticChartGroup},
 * driven entirely by the metric descriptors. Updated whenever the history property changes.
 */
final class StatisticHistoryChartView {

    private static final double TARGET_TICK_COUNT = 5.0;
    private static final double CHART_SPACING = 8.0;
    private static final double[] NICE_CEILING_FACTORS = {1.0, 2.0, 5.0, 10.0};
    private static final double SHRINK_THRESHOLD_RATIO = 0.2;

    private final List<StatisticChartGroup> distinctGroups;
    private final List<GroupChart> groupCharts;
    private final Map<String, XYChart.Series<Number, Number>> seriesByKey;
    private final Map<StatisticChartGroup, List<String>> keysByGroup;
    private final Map<StatisticChartGroup, Integer> windowSizeByGroup;
    private final Map<StatisticChartGroup, Double> ceilingByGroup;
    private final TitledPane titledPane;

    StatisticHistoryChartView(
            List<? extends StatisticMetric<?>> metrics,
            ReadOnlyObjectProperty<List<StatisticSample>> historyProperty) {

        distinctGroups = Arrays.stream(StatisticChartGroup.values())
                               .filter(g -> g != StatisticChartGroup.NONE)
                               .filter(g -> metrics.stream().anyMatch(m -> m.chartGroup() == g))
                               .collect(Collectors.toList());

        keysByGroup = new LinkedHashMap<>();
        windowSizeByGroup = new LinkedHashMap<>();
        for (StatisticChartGroup group : distinctGroups) {
            List<String> keys = metrics.stream()
                                       .filter(m -> m.chartGroup() == group)
                                       .map(StatisticMetric::key)
                                       .collect(Collectors.toList());
            keysByGroup.put(group, keys);
            // Guarded by StatisticMetricRowTest: metrics sharing a chartGroup use the same chartWindowSize.
            int windowSize = metrics.stream()
                                    .filter(m -> m.chartGroup() == group)
                                    .mapToInt(StatisticMetric::chartWindowSize)
                                    .findFirst()
                                    .orElse(StatisticMetric.DEFAULT_CHART_WINDOW_SIZE);
            windowSizeByGroup.put(group, windowSize);
        }

        ceilingByGroup = new EnumMap<>(StatisticChartGroup.class);

        seriesByKey = new LinkedHashMap<>();
        for (StatisticMetric<?> metric : metrics) {
            if (metric.chartGroup() != StatisticChartGroup.NONE) {
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName(AppLocalization.getText(metric.labelKey()));
                seriesByKey.put(metric.key(), series);
            }
        }

        groupCharts = new ArrayList<>(distinctGroups.size());
        VBox chartsBox = new VBox();
        chartsBox.setSpacing(CHART_SPACING);

        for (StatisticChartGroup group : distinctGroups) {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel(AppLocalization.getText(AppLocalizationKeys.OBSERVATION_CHART_AXIS_STEP));
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

            for (String key : keysByGroup.get(group)) {
                chart.getData().add(seriesByKey.get(key));
            }

            groupCharts.add(new GroupChart(chart, xAxis, yAxis));
            chartsBox.getChildren().add(chart);
        }

        titledPane = new TitledPane(
                AppLocalization.getText(AppLocalizationKeys.OBSERVATION_SECTION_CHARTS),
                chartsBox);
        titledPane.getStyleClass().add(FXStyleClasses.OBSERVATION_CHART_SECTION);
        titledPane.setExpanded(false);
        titledPane.setManaged(false);
        titledPane.setVisible(false);

        historyProperty.addListener((_, _, history) -> updateCharts(history));
        updateCharts(historyProperty.get());
    }

    /**
     * Computes an axis tick unit that yields roughly {@link #TARGET_TICK_COUNT} major ticks
     * for the given span, without affecting the axis bounds.
     */
    private static double tickUnit(double span) {
        return Math.max(1.0, Math.ceil(span / TARGET_TICK_COUNT));
    }

    /**
     * Rounds up to the smallest {@code {1, 2, 5} x 10^n} value that is greater than or equal
     * to the given value, so the Y-axis ceiling stays stable and human-readable as the group
     * maximum grows (e.g. {@code 12 -> 20}, {@code 2495 -> 5000}). Never returns less than
     * {@code 1}, so an all-zero group still renders a usable {@code [0, 1]} axis.
     */
    @SuppressWarnings("MagicNumber")
    private static double niceCeiling(double value) {
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
     * Extracts the maximum finite value across the given metric keys for a single sample,
     * or {@code 0.0} if none of the keys have a finite value.
     */
    private static double maxValueAcrossKeys(StatisticSample sample, List<String> keys) {
        return keys.stream()
                   .map(k -> sample.values().get(k))
                   .filter(Objects::nonNull)
                   .filter(Double::isFinite)
                   .mapToDouble(Double::doubleValue)
                   .max()
                   .orElse(0.0);
    }

    TitledPane titledPane() {
        return titledPane;
    }

    /**
     * Package-private for testing.
     */
    List<LineChart<Number, Number>> chartsForTest() {
        return groupCharts.stream().map(GroupChart::chart).collect(Collectors.toList());
    }

    private void updateCharts(List<StatisticSample> history) {
        if (history.isEmpty()) {
            ceilingByGroup.clear();
        }

        boolean anyVisible = false;

        for (int i = 0; i < distinctGroups.size(); i++) {
            StatisticChartGroup group = distinctGroups.get(i);
            GroupChart gc = groupCharts.get(i);
            List<String> keys = keysByGroup.get(group);

            boolean chartVisible = !history.isEmpty();
            gc.chart().setManaged(chartVisible);
            gc.chart().setVisible(chartVisible);

            if (chartVisible) {
                anyVisible = true;

                int windowSize = windowSizeByGroup.get(group);
                List<StatisticSample> windowedHistory = (history.size() <= windowSize)
                        ? history
                        : history.subList(history.size() - windowSize, history.size());

                double windowedMax = windowedHistory.stream()
                                                    .mapToDouble(s -> maxValueAcrossKeys(s, keys))
                                                    .max()
                                                    .orElse(0.0);
                double latestMax = maxValueAcrossKeys(history.getLast(), keys);
                double currentCeiling = ceilingByGroup.getOrDefault(group, 0.0);
                double newCeiling;
                if ((currentCeiling <= 0.0) || (windowedMax > currentCeiling)) {
                    newCeiling = niceCeiling(windowedMax); // initial ceiling or immediate growth
                } else if (latestMax < (SHRINK_THRESHOLD_RATIO * currentCeiling)) {
                    newCeiling = niceCeiling(windowedMax); // gated shrink
                } else {
                    newCeiling = currentCeiling; // no change
                }
                ceilingByGroup.put(group, newCeiling);

                gc.yAxis().setUpperBound(newCeiling);
                gc.yAxis().setTickUnit(tickUnit(newCeiling));

                int xMin = windowedHistory.getFirst().stepCount();
                int xMax = windowedHistory.getLast().stepCount();
                if (xMin == xMax) {
                    xMax = xMin + 1;
                }
                gc.xAxis().setLowerBound(xMin);
                gc.xAxis().setUpperBound(xMax);
                gc.xAxis().setTickUnit(tickUnit(xMax - xMin));

                for (String key : keys) {
                    XYChart.Series<Number, Number> series = seriesByKey.get(key);
                    List<XYChart.Data<Number, Number>> data = new ArrayList<>(windowedHistory.size());
                    for (StatisticSample sample : windowedHistory) {
                        Double value = sample.values().get(key);
                        if ((value != null) && Double.isFinite(value)) {
                            data.add(new XYChart.Data<>(sample.stepCount(), value));
                        }
                    }
                    series.getData().setAll(data);
                }
            }
        }

        titledPane.setManaged(anyVisible);
        titledPane.setVisible(anyVisible);
    }

    private record GroupChart(LineChart<Number, Number> chart, NumberAxis xAxis, NumberAxis yAxis) {}

}
