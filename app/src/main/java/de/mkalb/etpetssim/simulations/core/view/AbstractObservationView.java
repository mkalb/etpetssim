package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationObservationViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

/**
 * Base class for simulation observation views.
 */
public abstract class AbstractObservationView<
        ENT extends GridEntity,
        STA extends SimulationStatistics,
        VM extends SimulationObservationViewModel<STA>>
        implements SimulationObservationView {

    protected final VM viewModel;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label selectedCellCoordinateLabel = new Label();
    private final Label selectedCellTypeLabel = new Label();
    private @Nullable VBox selectedCellSection;
    private @Nullable NumberFormat integerFormat;

    protected AbstractObservationView(VM viewModel, GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;
    }

    @Override
    public abstract Region buildObservationRegion();

    protected final ScrollPane createObservationScrollPane(Region... regions) {
        VBox content = new VBox();
        content.getStyleClass().add(FXStyleClasses.OBSERVATION_CONTENT_VBOX);

        for (Region region : regions) {
            content.getChildren().add(region);
        }

        ScrollPane observationScrollPane = new ScrollPane(content);
        observationScrollPane.getStyleClass().add(FXStyleClasses.OBSERVATION_SCROLLPANE);

        return observationScrollPane;
    }

    /**
     * Creates a standard grid section displaying the total cell count.
     * This section displays the total number of cells in the simulation grid.
     * The provided label is initialized with the unknown placeholder and populated during simulation initialization.
     *
     * @return a VBox region containing the grid section with the provided label
     */
    protected final VBox createGridSection() {
        setUnknownValues(totalCellsLabel);

        return createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_GRID,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_GRID_TOTAL_CELLS
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
                AppLocalizationKeys.OBSERVATION_SECTION_STATUS,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_STEP
                },
                new Label[]{
                        stepCountLabel
                }
        );
    }

    private void updateGridSectionLabel() {
        Optional<STA> statistics = viewModel.getStatistics();
        if (statistics.isPresent()) {
            setFormattedIntegerValue(totalCellsLabel, statistics.get().getTotalCells());
        } else {
            setUnknownValues(totalCellsLabel);
        }
    }

    /**
     * Initializes observation labels when the simulation has been created.
     */
    protected final void initializeObservationLabels() {
        updateGridSectionLabel();
        updateObservationLabels();
    }

    /**
     * Updates the standard status section label with the current step count.
     *
     * @param statistics current statistics snapshot
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final void updateStatusSectionLabel(Optional<STA> statistics) {
        if (statistics.isPresent()) {
            setFormattedIntegerValue(stepCountLabel, statistics.get().getStepCount());
        } else {
            setUnknownValues(stepCountLabel);
        }
    }

    protected final VBox createObservationSection(String titleKey, String[] nameKeys, Label[] valueLabels) {
        return createObservationSection(titleKey, createObservationGrid(nameKeys, valueLabels));
    }

    protected final VBox createObservationSection(@Nullable String titleKey, Region region) {
        VBox section = new VBox();
        section.getStyleClass().add(FXStyleClasses.OBSERVATION_SECTION_VBOX);

        if (titleKey != null) {
            Label titleLabel = new Label(AppLocalization.getText(titleKey));
            titleLabel.getStyleClass().add(FXStyleClasses.OBSERVATION_SECTION_TITLE_LABEL);
            section.getChildren().add(titleLabel);
        }

        section.getChildren().add(region);

        return section;
    }

    protected final GridPane createObservationGrid(String[] nameKeys, Label[] valueLabels) {
        if (nameKeys.length != valueLabels.length) {
            throw new IllegalArgumentException("nameKeys and valueLabels must have the same length.");
        }

        GridPane grid = new GridPane();
        grid.getStyleClass().add(FXStyleClasses.OBSERVATION_GRID);

        ColumnConstraints nameColumn = new ColumnConstraints();
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(nameColumn, valueColumn);

        for (int i = 0; i < nameKeys.length; i++) {
            Label nameLabel = new Label(AppLocalization.getText(nameKeys[i]));
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

        return grid;
    }

    protected final String localizedShortCellTypeName(GridEntity entity) {
        return entityDescriptorRegistry.requireByDescriptorId(entity.descriptorId()).shortName();
    }

    protected final NumberFormat integerFormat() {
        if (integerFormat == null) {
            integerFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());
        }
        return integerFormat;
    }

    protected final void setFormattedIntegerValue(Label valueLabel, Number value) {
        valueLabel.setText(integerFormat().format(value));
    }

    protected final void clearValues(Label... valueLabels) {
        for (Label valueLabel : valueLabels) {
            valueLabel.setText("");
        }
    }

    protected final void setUnknownValues(Label... valueLabels) {
        String unknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
        for (Label valueLabel : valueLabels) {
            valueLabel.setText(unknown);
        }
    }

    /**
     * Registers a listener on the provided selected-cell property that calls
     * {@link #onSelectedCellChanged(GridCell)} whenever the selection changes.
     * Call this in the subclass constructor to wire up the standard cell-selection behavior.
     *
     * @param property selected-cell property to listen on
     */
    protected final void registerSelectedCellListener(ReadOnlyObjectProperty<@Nullable GridCell<ENT>> property) {
        property.addListener((_, _, newCell) -> onSelectedCellChanged(newCell));
    }

    /**
     * Called whenever the selected grid cell changes.
     * <p>
     * The default implementation updates the selected cell section visibility
     * and the standard coordinate and cell type labels.
     * Override to add simulation-specific selected cell behavior.
     *
     * @param gridCell the newly selected grid cell, or {@code null} if no cell is selected
     */
    protected void onSelectedCellChanged(@Nullable GridCell<ENT> gridCell) {
        updateSelectedCellSectionVisibility(gridCell != null);
        updateSelectedCellBasicLabels(gridCell);
    }

    protected abstract void updateObservationLabels();

    /**
     * Creates a standard selected cell section displaying coordinate and cell type.
     * The standard coordinate and cell type labels are managed by this base class and updated via
     * {@link #updateSelectedCellBasicLabels(de.mkalb.etpetssim.engine.model.GridCell)}.
     * The returned section is stored internally for use by {@link #updateSelectedCellSectionVisibility(boolean)}.
     *
     * @return a VBox region containing the selected cell section with the standard labels
     */
    protected final VBox createSelectedCellSection() {
        clearValues(selectedCellCoordinateLabel, selectedCellTypeLabel);

        selectedCellSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_COORDINATE,
                        AppLocalizationKeys.OBSERVATION_CELL_TYPE
                },
                new Label[]{
                        selectedCellCoordinateLabel,
                        selectedCellTypeLabel
                }
        );
        return selectedCellSection;
    }

    /**
     * Creates an extended selected cell section with coordinate and cell type as the first two rows,
     * followed by the provided simulation-specific extra rows.
     * The returned section is stored internally for use by {@link #updateSelectedCellSectionVisibility(boolean)}.
     *
     * @param extraNameKeys   additional localization keys for the extra rows
     * @param extraValueLabels additional labels for the extra rows
     * @return a VBox region containing the extended selected cell section
     */
    protected final VBox createExtendedSelectedCellSection(String[] extraNameKeys, Label[] extraValueLabels) {
        clearValues(selectedCellCoordinateLabel, selectedCellTypeLabel);

        String[] allNameKeys = new String[2 + extraNameKeys.length];
        allNameKeys[0] = AppLocalizationKeys.OBSERVATION_COORDINATE;
        allNameKeys[1] = AppLocalizationKeys.OBSERVATION_CELL_TYPE;
        System.arraycopy(extraNameKeys, 0, allNameKeys, 2, extraNameKeys.length);

        Label[] allValueLabels = new Label[2 + extraValueLabels.length];
        allValueLabels[0] = selectedCellCoordinateLabel;
        allValueLabels[1] = selectedCellTypeLabel;
        System.arraycopy(extraValueLabels, 0, allValueLabels, 2, extraValueLabels.length);

        selectedCellSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                allNameKeys,
                allValueLabels
        );
        return selectedCellSection;
    }

    /**
     * Updates the visibility of the selected cell section.
     * Call this from a grid cell change listener to show/hide the section.
     *
     * @param isVisible whether the section should be visible
     */
    protected final void updateSelectedCellSectionVisibility(boolean isVisible) {
        if (selectedCellSection != null) {
            selectedCellSection.setManaged(isVisible);
            selectedCellSection.setVisible(isVisible);
        }
    }

    /**
     * Updates the standard coordinate and cell type labels in a selected cell section.
     * Call this from a grid cell change listener to populate the basic selected cell info.
     *
     * @param gridCell the currently selected grid cell, or null if no cell is selected
     */
    protected final void updateSelectedCellBasicLabels(@Nullable GridCell<ENT> gridCell) {
        if (gridCell != null) {
            selectedCellCoordinateLabel.setText(gridCell.coordinate().toDisplayString());
            selectedCellTypeLabel.setText(localizedShortCellTypeName(gridCell.entity()));
        } else {
            clearValues(selectedCellCoordinateLabel, selectedCellTypeLabel);
        }
    }

}
