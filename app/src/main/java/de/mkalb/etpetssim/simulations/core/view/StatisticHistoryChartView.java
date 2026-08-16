package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.*;
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

    private final List<StatisticChartGroup> distinctGroups;
    private final List<GroupChart> groupCharts;
    private final Map<String, XYChart.Series<Number, Number>> seriesByKey;
    private final Map<StatisticChartGroup, List<String>> keysByGroup;
    private final TitledPane titledPane;

    StatisticHistoryChartView(
            List<? extends StatisticMetric<?>> metrics,
            ReadOnlyObjectProperty<List<StatisticSample>> historyProperty,
            ReadOnlyObjectProperty<StatisticExtrema> extremaProperty) {

        distinctGroups = Arrays.stream(StatisticChartGroup.values())
                               .filter(g -> g != StatisticChartGroup.NONE)
                               .filter(g -> metrics.stream().anyMatch(m -> m.chartGroup() == g))
                               .collect(Collectors.toList());

        keysByGroup = new LinkedHashMap<>();
        for (StatisticChartGroup group : distinctGroups) {
            List<String> keys = metrics.stream()
                                       .filter(m -> m.chartGroup() == group)
                                       .map(StatisticMetric::key)
                                       .collect(Collectors.toList());
            keysByGroup.put(group, keys);
        }

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

        historyProperty.addListener((_, _, history) -> updateCharts(history, extremaProperty.get()));
        updateCharts(historyProperty.get(), extremaProperty.get());
    }

    /**
     * Computes an axis tick unit that yields roughly {@link #TARGET_TICK_COUNT} major ticks
     * for the given span, without affecting the axis bounds.
     */
    private static double tickUnit(double span) {
        return Math.max(1.0, Math.ceil(span / TARGET_TICK_COUNT));
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

    private void updateCharts(List<StatisticSample> history, StatisticExtrema extrema) {
        boolean anyVisible = false;

        for (int i = 0; i < distinctGroups.size(); i++) {
            StatisticChartGroup group = distinctGroups.get(i);
            GroupChart gc = groupCharts.get(i);
            List<String> keys = keysByGroup.get(group);

            double groupMax = keys.stream()
                                  .map(k -> extrema.maximumValues().get(k))
                                  .filter(Objects::nonNull)
                                  .mapToDouble(StatisticExtremum::value)
                                  .max()
                                  .orElse(0.0);
            groupMax = Math.ceil(groupMax);

            boolean chartVisible = (groupMax > 0) && !history.isEmpty();
            gc.chart().setManaged(chartVisible);
            gc.chart().setVisible(chartVisible);

            if (chartVisible) {
                anyVisible = true;

                gc.yAxis().setUpperBound(groupMax);
                gc.yAxis().setTickUnit(tickUnit(groupMax));

                int xMin = history.getFirst().stepCount();
                int xMax = history.getLast().stepCount();
                if (xMin == xMax) {
                    xMax = xMin + 1;
                }
                gc.xAxis().setLowerBound(xMin);
                gc.xAxis().setUpperBound(xMax);
                gc.xAxis().setTickUnit(tickUnit(xMax - xMin));

                for (String key : keys) {
                    XYChart.Series<Number, Number> series = seriesByKey.get(key);
                    ObservableList<XYChart.Data<Number, Number>> data = FXCollections.observableArrayList();
                    for (StatisticSample sample : history) {
                        Double value = sample.values().get(key);
                        if ((value != null) && Double.isFinite(value)) {
                            data.add(new XYChart.Data<>(sample.stepCount(), value));
                        }
                    }
                    series.setData(data);
                }
            }
        }

        titledPane.setManaged(anyVisible);
        titledPane.setVisible(anyVisible);
    }

    private record GroupChart(LineChart<Number, Number> chart, NumberAxis xAxis, NumberAxis yAxis) {}

}
