package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremum;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Base class for simulation observation views.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GC>  grid cell type used for cell selection
 * @param <STA> statistics type shown by the view
 */
public abstract class AbstractObservationView<
        ENT extends GridEntity,
        GC extends GridCellView<ENT>,
        STA extends SimulationStatistics> {

    private static final String LABEL_SEPARATOR = ":";

    protected final DefaultObservationViewModel<ENT, GC, STA> viewModel;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label selectedCellCoordinateLabel = new Label();
    private final Label selectedCellTypeLabel = new Label();
    private final List<StatisticsHistoryChartView> statisticsHistoryChartViews = new ArrayList<>();
    private @Nullable GC selectedGridCell;
    private @Nullable VBox selectedCellSection;
    private @Nullable NumberFormat integerFormat;
    private @Nullable List<StatisticsMetric<STA>> genericMetrics;
    private @Nullable List<MetricLabels> genericMetricLabels;
    private @Nullable ReadOnlyObjectProperty<@Nullable GC> selectedGridCellProperty;
    private @Nullable ChangeListener<@Nullable GC> selectedGridCellListener;

    protected AbstractObservationView(DefaultObservationViewModel<ENT, GC, STA> viewModel,
                                      GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;
    }

    /**
     * Builds the observation UI region.
     *
     * @return root region for simulation observation
     */
    public abstract Region buildObservationRegion();

    /**
     * Initializes observation labels when the simulation has been created.
     */
    public final void initializeObservationLabels() {
        updateGridSectionLabel();
        updateObservationLabels();
    }

    /**
     * Releases listeners and bindings owned by the observation view.
     */
    public final void shutdownObservation() {
        if ((selectedGridCellProperty != null) && (selectedGridCellListener != null)) {
            selectedGridCellProperty.removeListener(selectedGridCellListener);
        }
        selectedGridCellProperty = null;
        selectedGridCellListener = null;

        for (StatisticsHistoryChartView chartView : statisticsHistoryChartViews) {
            chartView.shutdown();
        }
        statisticsHistoryChartViews.clear();
    }

    /**
     * Wraps the given regions in a vertically stacked {@link VBox} inside a {@link ScrollPane}.
     * The scroll pane and its content are styled with the standard observation CSS classes.
     *
     * @param regions one or more regions to stack vertically inside the scroll pane
     * @return a styled {@link ScrollPane} containing all provided regions
     */
    protected final ScrollPane createObservationScrollPane(@Nullable Region... regions) {
        VBox content = new VBox();
        content.getStyleClass().add(FXStyleClasses.OBSERVATION_CONTENT_VBOX);

        for (Region region : regions) {
            if (region != null) {
                content.getChildren().add(region);
            }
        }

        ScrollPane observationScrollPane = new ScrollPane(content);
        observationScrollPane.getStyleClass().add(FXStyleClasses.OBSERVATION_SCROLLPANE);
        observationScrollPane.setFitToWidth(true);

        return observationScrollPane;
    }

    /**
     * Creates the standard grid section displaying the total cell count.
     * The value shows the unknown placeholder until the simulation is initialized.
     *
     * @return a VBox region containing the grid section
     */
    protected final VBox createGridSection() {
        setUnknownValues(totalCellsLabel);

        return createObservationSection(
                SimulationLocalizationKeys.OBSERVATION_SECTION_GRID,
                new String[]{
                        SimulationLocalizationKeys.OBSERVATION_GRID_TOTAL_CELLS
                },
                new Label[]{
                        totalCellsLabel
                }
        );
    }

    /**
     * Creates the standard status section displaying the current step count.
     *
     * @return a VBox region containing the status section
     */
    protected final VBox createStatusSection() {
        setUnknownValues(stepCountLabel);

        return createObservationSection(
                SimulationLocalizationKeys.OBSERVATION_SECTION_STATUS,
                new String[]{
                        SimulationLocalizationKeys.OBSERVATION_STEP
                },
                new Label[]{
                        stepCountLabel
                }
        );
    }

    /**
     * Sets the text of {@code valueLabel} to the locale-formatted integer representation of {@code value}.
     *
     * @param valueLabel the label whose text is updated
     * @param value      the numeric value to format and display
     */
    protected final void setFormattedIntegerValue(Label valueLabel, Number value) {
        valueLabel.setText(integerFormat().format(value));
    }

    /**
     * Sets the text of each provided label to the localized unknown-value placeholder.
     * Use this to reset labels to their initial indeterminate state.
     *
     * @param valueLabels one or more labels to reset
     */
    protected final void setUnknownValues(Label... valueLabels) {
        String unknown = AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
        for (Label valueLabel : valueLabels) {
            valueLabel.setText(unknown);
        }
    }

    /**
     * Registers a listener on the provided selected-cell property that calls
     * {@link #handleSelectedGridCellChanged(GridCellView)} whenever the selection changes.
     * Call this in the subclass constructor to wire up the standard cell-selection behavior.
     *
     * @param property selected-cell property to listen on
     */
    protected final void registerSelectedCellListener(ReadOnlyObjectProperty<@Nullable GC> property) {
        selectedGridCell = property.get();
        selectedGridCellProperty = property;
        selectedGridCellListener = (_, _, newCell) -> {
            selectedGridCell = newCell;
            handleSelectedGridCellChanged(newCell);
        };
        property.addListener(selectedGridCellListener);
    }

    /**
     * Handles a change of the selected grid cell.
     *
     * <p>The default implementation updates the selected cell section visibility
     * and the standard coordinate and cell type labels.
     * Override to add simulation-specific selected cell behavior.
     *
     * @param gridCell the newly selected grid cell, or {@code null} if no cell is selected
     */
    protected void handleSelectedGridCellChanged(@Nullable GC gridCell) {
        if (selectedCellSection != null) {
            boolean hasSelectedCell = (gridCell != null);
            selectedCellSection.setManaged(hasSelectedCell);
            selectedCellSection.setVisible(hasSelectedCell);
            setUnknownValues(selectedCellCoordinateLabel, selectedCellTypeLabel);

            if (hasSelectedCell) {
                selectedCellCoordinateLabel.setText(gridCell.coordinate().toDisplayString());
                entityDescriptorRegistry.findByDescriptorId(gridCell.entity().descriptorId())
                        .ifPresent(descriptor -> selectedCellTypeLabel.setText(descriptor.shortName()));
            }
        }
    }

    /**
     * Creates a standard selected cell section displaying coordinate and cell type.
     * The standard coordinate and cell type labels are managed by this base class; visibility and
     * label values are updated by {@link #handleSelectedGridCellChanged(GridCellView)}.
     *
     * @return a VBox region containing the selected cell section with the standard labels
     */
    protected final VBox createSelectedCellSection() {
        return createExtendedSelectedCellSection(new String[0], new Label[0]);
    }

    /**
     * Creates an extended selected cell section with coordinate and cell type as the first two rows,
     * followed by the provided simulation-specific extra rows.
     * The section is stored internally; its visibility and the standard label values are managed by
     * {@link #handleSelectedGridCellChanged(GridCellView)}.
     *
     * @param extraNameKeys    additional localization keys for the extra rows
     * @param extraValueLabels additional labels for the extra rows
     * @return a VBox region containing the extended selected cell section
     */
    protected final VBox createExtendedSelectedCellSection(String[] extraNameKeys, Label[] extraValueLabels) {
        String[] allNameKeys = new String[2 + extraNameKeys.length];
        allNameKeys[0] = SimulationLocalizationKeys.OBSERVATION_COORDINATE;
        allNameKeys[1] = SimulationLocalizationKeys.OBSERVATION_CELL_TYPE;
        System.arraycopy(extraNameKeys, 0, allNameKeys, 2, extraNameKeys.length);

        Label[] allValueLabels = new Label[2 + extraValueLabels.length];
        allValueLabels[0] = selectedCellCoordinateLabel;
        allValueLabels[1] = selectedCellTypeLabel;
        System.arraycopy(extraValueLabels, 0, allValueLabels, 2, extraValueLabels.length);

        selectedCellSection = createObservationSection(
                SimulationLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                allNameKeys,
                allValueLabels
        );
        handleSelectedGridCellChanged(selectedGridCell);
        return selectedCellSection;
    }

    /**
     * Creates a titled metrics section displaying current values and running extrema for each metric.
     *
     * <p>When any metric tracks extrema, a "min" / "max" header row is prepended; step-count context
     * appears in a tooltip on each extremum value label on hover.
     * {@code updateObservationLabels()} refreshes all values.
     *
     * @param metrics ordered metric descriptors defining the row layout
     * @return a styled {@link VBox} containing the title and the metric grid
     */
    protected final VBox createGenericMetricSection(List<StatisticsMetric<STA>> metrics) {
        genericMetrics = List.copyOf(metrics);
        List<MetricLabels> labels = new ArrayList<>(metrics.size());
        genericMetricLabels = labels;

        GridPane grid = new GridPane();
        grid.getStyleClass().add(FXStyleClasses.OBSERVATION_GRID);

        ColumnConstraints nameColumn = new ColumnConstraints();
        nameColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(nameColumn);

        boolean hasMin = metrics.stream().anyMatch(m -> m.extremaMode().includesMinimum());
        boolean hasMax = metrics.stream().anyMatch(m -> m.extremaMode().includesMaximum());
        int minCol = 2;
        int maxCol = hasMin ? 3 : 2;
        if (hasMin) {
            addExtremumHeader(grid, SimulationLocalizationKeys.OBSERVATION_EXTREMUM_MIN, minCol);
        }
        if (hasMax) {
            addExtremumHeader(grid, SimulationLocalizationKeys.OBSERVATION_EXTREMUM_MAX, maxCol);
        }
        int rowOffset = (hasMin || hasMax) ? 1 : 0;

        for (int i = 0; i < metrics.size(); i++) {
            StatisticsMetric<STA> metric = metrics.get(i);
            int row = i + rowOffset;

            Label nameLabel = new Label(AppLocalization.getText(metric.labelKey()) + LABEL_SEPARATOR);
            nameLabel.getStyleClass().add(FXStyleClasses.OBSERVATION_NAME_LABEL);
            nameLabel.setMaxWidth(Double.MAX_VALUE);

            Label currentLabel = createValueLabel();

            grid.add(nameLabel, 0, row);
            grid.add(currentLabel, 1, row);
            GridPane.setHalignment(nameLabel, HPos.LEFT);
            GridPane.setHgrow(nameLabel, Priority.ALWAYS);
            GridPane.setHalignment(currentLabel, HPos.RIGHT);

            var mode = metric.extremaMode();
            ExtremumLabel minimum = mode.includesMinimum() ? createExtremumLabel(grid, minCol, row) : null;
            ExtremumLabel maximum = mode.includesMaximum() ? createExtremumLabel(grid, maxCol, row) : null;
            labels.add(new MetricLabels(currentLabel, minimum, maximum));
        }

        return createTitledSection(SimulationLocalizationKeys.OBSERVATION_SECTION_METRICS, grid);
    }

    /**
     * Creates the chart section for simulations that have at least one charted metric.
     * Must be called after {@link #createGenericMetricSection}, whose metrics define the charts.
     *
     * @return a {@link TitledPane} wrapping the history line charts, or {@code null} if no generic metric section
     * was created or none of its metrics is charted
     */
    protected final @Nullable TitledPane createChartSection() {
        if ((genericMetrics == null) ||
                genericMetrics.stream().noneMatch(m -> m.chartGroup() != StatisticsChartGroup.NONE)) {
            return null;
        }
        StatisticsHistoryChartView chartView = new StatisticsHistoryChartView(
                genericMetrics,
                viewModel.statisticsHistoryProperty());
        statisticsHistoryChartViews.add(chartView);
        return chartView.titledPane();
    }

    /**
     * Updates the step count and the generic metrics section with the latest statistics and extrema.
     * Called by {@link #initializeObservationLabels()} on simulation creation and by the main view
     * whenever a step has been executed or a user action has changed the simulation state.
     */
    final void updateObservationLabels() {
        updateStatusSectionLabel();
        updateGenericMetricSection();
    }

    final boolean hasSelectedGridCellListener() {
        return selectedGridCellListener != null;
    }

    private void updateGridSectionLabel() {
        Optional<STA> statistics = viewModel.getStatistics();
        if (statistics.isPresent()) {
            setFormattedIntegerValue(totalCellsLabel, statistics.get().totalCells());
        } else {
            setUnknownValues(totalCellsLabel);
        }
    }

    private void updateStatusSectionLabel() {
        Optional<STA> statistics = viewModel.getStatistics();
        if (statistics.isPresent()) {
            setFormattedIntegerValue(stepCountLabel, statistics.get().stepCount());
        } else {
            setUnknownValues(stepCountLabel);
        }
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void updateGenericMetricSection() {
        if ((genericMetrics == null) || (genericMetricLabels == null)) {
            return;
        }
        Optional<STA> statistics = viewModel.getStatistics();
        StatisticsExtrema extrema = viewModel.getStatisticsExtrema();
        String unknown = AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
        String atStep = AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_EXTREMUM_AT_STEP);
        for (int i = 0; i < genericMetrics.size(); i++) {
            StatisticsMetric<STA> metric = genericMetrics.get(i);
            MetricLabels rowLabels = genericMetricLabels.get(i);
            if (statistics.isPresent()) {
                double currentValue = metric.extractor().applyAsDouble(statistics.get());
                if (Double.isFinite(currentValue)) {
                    setFormattedIntegerValue(rowLabels.currentLabel(), (int) currentValue);
                } else {
                    rowLabels.currentLabel().setText(unknown);
                }
            } else {
                rowLabels.currentLabel().setText(unknown);
            }
            ExtremumLabel minimum = rowLabels.minimum();
            if (minimum != null) {
                updateExtremumLabel(minimum, extrema.minimumValues().get(metric.key()), unknown, atStep);
            }
            ExtremumLabel maximum = rowLabels.maximum();
            if (maximum != null) {
                updateExtremumLabel(maximum, extrema.maximumValues().get(metric.key()), unknown, atStep);
            }
        }
    }

    private void updateExtremumLabel(ExtremumLabel extremumLabel, @Nullable StatisticsExtremum extremum,
                                     String unknown, String atStep) {
        Label label = extremumLabel.label();
        if (extremum != null) {
            setFormattedIntegerValue(label, extremum.intValue());
            extremumLabel.tooltip().setText(atStep + " " + integerFormat().format(extremum.stepCount()));
            label.setTooltip(extremumLabel.tooltip());
        } else {
            label.setText(unknown);
            label.setTooltip(null);
        }
    }

    /**
     * Creates a titled observation section as a {@link VBox} containing a two-column {@link GridPane}.
     * Each row pairs a localized name label (left-aligned, followed by a colon) with a value label (right-aligned,
     * expanding).
     * {@code nameKeys} and {@code valueLabels} must have the same length.
     *
     * @param titleKey    localization key for the section title
     * @param nameKeys    localization keys for the row name labels, one per row
     * @param valueLabels pre-created value labels to place in the right column, one per row
     * @return a styled {@link VBox} containing the title and the name/value grid
     * @throws IllegalArgumentException if {@code nameKeys} and {@code valueLabels} differ in length
     */
    private VBox createObservationSection(String titleKey, String[] nameKeys, Label[] valueLabels) {
        if (nameKeys.length != valueLabels.length) {
            throw new IllegalArgumentException("nameKeys and valueLabels must have the same length, but was: "
                    + "nameKeys.length=" + nameKeys.length + ", valueLabels.length=" + valueLabels.length);
        }

        GridPane grid = new GridPane();
        grid.getStyleClass().add(FXStyleClasses.OBSERVATION_GRID);

        ColumnConstraints nameColumn = new ColumnConstraints();
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(nameColumn, valueColumn);

        for (int i = 0; i < nameKeys.length; i++) {
            Label nameLabel = new Label(AppLocalization.getText(nameKeys[i]) + LABEL_SEPARATOR);
            nameLabel.getStyleClass().add(FXStyleClasses.OBSERVATION_NAME_LABEL);
            valueLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_VALUE_LABEL);
            valueLabels[i].setMaxWidth(Double.MAX_VALUE);
            valueLabels[i].setAlignment(Pos.CENTER_RIGHT);

            grid.add(nameLabel, 0, i);
            grid.add(valueLabels[i], 1, i);

            GridPane.setHalignment(nameLabel, HPos.LEFT);
            GridPane.setHalignment(valueLabels[i], HPos.RIGHT);
            GridPane.setHgrow(valueLabels[i], Priority.ALWAYS);
        }

        return createTitledSection(titleKey, grid);
    }

    private VBox createTitledSection(String titleKey, GridPane grid) {
        VBox section = new VBox();
        section.getStyleClass().add(FXStyleClasses.OBSERVATION_SECTION_VBOX);

        Label titleLabel = new Label(AppLocalization.getText(titleKey));
        titleLabel.getStyleClass().add(FXStyleClasses.OBSERVATION_SECTION_TITLE_LABEL);
        section.getChildren().addAll(titleLabel, grid);

        return section;
    }

    private void addExtremumHeader(GridPane grid, String titleKey, int column) {
        Label header = new Label(AppLocalization.getText(titleKey));
        header.getStyleClass().add(FXStyleClasses.OBSERVATION_EXTREMUM_HEADER_LABEL);
        grid.add(header, column, 0);
        GridPane.setHalignment(header, HPos.RIGHT);
    }

    private Label createValueLabel() {
        Label label = new Label();
        label.getStyleClass().add(FXStyleClasses.OBSERVATION_VALUE_LABEL);
        label.setAlignment(Pos.CENTER_RIGHT);
        setUnknownValues(label);
        return label;
    }

    private ExtremumLabel createExtremumLabel(GridPane grid, int column, int row) {
        Label label = createValueLabel();
        grid.add(label, column, row);
        GridPane.setHalignment(label, HPos.RIGHT);
        return new ExtremumLabel(label, new Tooltip());
    }

    private NumberFormat integerFormat() {
        if (integerFormat == null) {
            integerFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());
        }
        return integerFormat;
    }

    /**
     * Labels of one metric row.
     *
     * @param currentLabel the label of the current value
     * @param minimum      the label of the minimum, or {@code null} if the metric tracks no minimum
     * @param maximum      the label of the maximum, or {@code null} if the metric tracks no maximum
     */
    private record MetricLabels(
            Label currentLabel,
            @Nullable ExtremumLabel minimum,
            @Nullable ExtremumLabel maximum) {
    }

    private record ExtremumLabel(Label label, Tooltip tooltip) {
    }

}
