package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationObservationViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
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
public abstract class AbstractObservationView<STA extends SimulationStatistics, VM extends SimulationObservationViewModel<STA>>
        implements SimulationObservationView {

    protected final VM viewModel;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private @Nullable NumberFormat integerFormat;

    protected AbstractObservationView(VM viewModel, GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;
    }

    @Override
    public abstract Region buildObservationRegion();

    protected final ScrollPane createObservationScrollPane(Region region) {
        ScrollPane observationScrollPane = new ScrollPane(region);
        observationScrollPane.getStyleClass().add(FXStyleClasses.OBSERVATION_SCROLLPANE);

        return observationScrollPane;
    }

    protected final ScrollPane createObservationScrollPane(Region... regions) {
        return createObservationScrollPane(createObservationContent(regions));
    }

    protected final VBox createObservationContent(Region... regions) {
        VBox content = new VBox();
        content.getStyleClass().add(FXStyleClasses.OBSERVATION_CONTENT_VBOX);

        for (Region region : regions) {
            content.getChildren().add(region);
        }

        return content;
    }

    /**
     * Creates a standard grid section displaying the total cell count.
     * This section displays the total number of cells in the simulation grid.
     * The provided label will be managed by the caller and updated in {@link #updateObservationLabels()}.
     *
     * @param totalCellsLabel the label that will display the total cell count
     * @return a VBox region containing the grid section with the provided label
     */
    protected final VBox createGridSection(Label totalCellsLabel) {
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
     * Updates the total cells label with the current statistics value.
     * Call this from {@link #updateObservationLabels()} to update the grid section.
     *
     * @param totalCellsLabel the label to update
     */
    protected final void updateGridSectionLabel(Label totalCellsLabel) {
        Optional<STA> statistics = viewModel.getStatistics();
        if (statistics.isPresent()) {
            setFormattedIntegerValue(totalCellsLabel, statistics.get().getTotalCells());
        } else {
            setUnknownValues(totalCellsLabel);
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

    protected abstract void updateObservationLabels();

    /**
     * Creates a standard selected cell section displaying coordinate and cell type.
     * The provided labels will be managed by the caller and updated via
     * {@link #updateSelectedCellBasicLabels(Label, Label, de.mkalb.etpetssim.engine.model.GridCell)}.
     *
     * @param coordinateLabel the label that will display the cell coordinate
     * @param cellTypeLabel the label that will display the cell type
     * @return a VBox region containing the selected cell section with the provided labels
     */
    protected final VBox createSelectedCellSection(Label coordinateLabel, Label cellTypeLabel) {
        return createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_COORDINATE,
                        AppLocalizationKeys.OBSERVATION_CELL_TYPE
                },
                new Label[]{
                        coordinateLabel,
                        cellTypeLabel
                }
        );
    }

    /**
     * Updates the visibility of a selected cell section based on whether a cell is selected.
     * Call this from a grid cell change listener to show/hide the section.
     *
     * @param selectedCellSection the section VBox to control visibility for
     * @param isVisible whether the section should be visible
     */
    protected final void updateSelectedCellSectionVisibility(@Nullable VBox selectedCellSection, boolean isVisible) {
        if (selectedCellSection != null) {
            selectedCellSection.setManaged(isVisible);
            selectedCellSection.setVisible(isVisible);
        }
    }

    /**
     * Updates the standard coordinate and cell type labels in a selected cell section.
     * Call this from a grid cell change listener to populate the basic selected cell info.
     *
     * @param coordinateLabel the label to update with coordinate
     * @param cellTypeLabel the label to update with cell type
     * @param gridCell the currently selected grid cell, or null if no cell is selected
     */
    protected final void updateSelectedCellBasicLabels(Label coordinateLabel, Label cellTypeLabel,
                                                       @Nullable GridCell<?> gridCell) {
        if (gridCell != null) {
            coordinateLabel.setText(gridCell.coordinate().toDisplayString());
            cellTypeLabel.setText(localizedShortCellTypeName(gridCell.entity()));
        } else {
            clearValues(coordinateLabel, cellTypeLabel);
        }
    }

}
