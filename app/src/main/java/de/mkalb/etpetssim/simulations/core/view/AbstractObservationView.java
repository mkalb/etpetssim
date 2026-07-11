package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.entity.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationObservationViewModel;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

/**
 * Base class for simulation observation views.
 */
public abstract class AbstractObservationView<
        ENT extends GridEntity,
        GC extends GridCellView<ENT>,
        STA extends SimulationStatistics,
        VM extends SimulationObservationViewModel<STA>>
        implements SimulationObservationView {

    protected final VM viewModel;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label selectedCellCoordinateLabel = new Label();
    private final Label selectedCellTypeLabel = new Label();
    private @Nullable GC selectedGridCell;
    private @Nullable VBox selectedCellSection;
    private @Nullable NumberFormat integerFormat;

    /**
     * Constructs a new observation view bound to the given view model and entity descriptor registry.
     *
     * @param viewModel                the simulation observation view model providing statistics and state
     * @param entityDescriptorRegistry the registry used to resolve entity descriptors for display
     */
    protected AbstractObservationView(VM viewModel,
                                      GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;
    }

    @Override
    public abstract Region buildObservationRegion();

    /**
     * Wraps the given regions in a vertically stacked {@link VBox} inside a {@link ScrollPane}.
     * The scroll pane and its content are styled with the standard observation CSS classes.
     *
     * @param regions one or more regions to stack vertically inside the scroll pane
     * @return a styled {@link ScrollPane} containing all provided regions
     */
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

    @Override
    public final void initializeObservationLabels() {
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

    /**
     * Creates a titled observation section as a {@link VBox} containing a two-column {@link GridPane}.
     * Each row pairs a localized name label (left-aligned) with a value label (right-aligned, expanding).
     * {@code nameKeys} and {@code valueLabels} must have the same length.
     *
     * @param titleKey    localization key for the section title
     * @param nameKeys    localization keys for the row name labels, one per row
     * @param valueLabels pre-created value labels to place in the right column, one per row
     * @return a styled {@link VBox} containing the title and the name/value grid
     * @throws IllegalArgumentException if {@code nameKeys} and {@code valueLabels} differ in length
     */
    protected final VBox createObservationSection(String titleKey, String[] nameKeys, Label[] valueLabels) {
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

        VBox section = new VBox();
        section.getStyleClass().add(FXStyleClasses.OBSERVATION_SECTION_VBOX);

        Label titleLabel = new Label(AppLocalization.getText(titleKey));
        titleLabel.getStyleClass().add(FXStyleClasses.OBSERVATION_SECTION_TITLE_LABEL);
        section.getChildren().add(titleLabel);

        section.getChildren().add(grid);

        return section;
    }

    private NumberFormat integerFormat() {
        if (integerFormat == null) {
            integerFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());
        }
        return integerFormat;
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
        String unknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
        for (Label valueLabel : valueLabels) {
            valueLabel.setText(unknown);
        }
    }

    /**
     * Registers a listener on the provided selected-cell property that calls
     * {@link #onSelectedCellChanged(GridCellView)} whenever the selection changes.
     * Call this in the subclass constructor to wire up the standard cell-selection behavior.
     *
     * @param property selected-cell property to listen on
     */
    protected final void registerSelectedCellListener(ReadOnlyObjectProperty<@Nullable GC> property) {
        selectedGridCell = property.get();
        property.addListener((_, _, newCell) -> {
            selectedGridCell = newCell;
            onSelectedCellChanged(newCell);
        });
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
    protected void onSelectedCellChanged(@Nullable GC gridCell) {
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
     * Updates all simulation-specific observation labels with the latest state.
     * Called by {@link #initializeObservationLabels()} on simulation creation and by subclasses
     * whenever a step has been executed and labels need to reflect the new statistics.
     */
    protected abstract void updateObservationLabels();

    /**
     * Creates a standard selected cell section displaying coordinate and cell type.
     * The standard coordinate and cell type labels are managed by this base class; visibility and
     * label values are updated by {@link #onSelectedCellChanged(GridCellView)}.
     *
     * @return a VBox region containing the selected cell section with the standard labels
     */
    protected final VBox createSelectedCellSection() {
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
        onSelectedCellChanged(selectedGridCell);
        return selectedCellSection;
    }

    /**
     * Creates an extended selected cell section with coordinate and cell type as the first two rows,
     * followed by the provided simulation-specific extra rows.
     * The section is stored internally; its visibility and the standard label values are managed by
     * {@link #onSelectedCellChanged(GridCellView)}.
     *
     * @param extraNameKeys    additional localization keys for the extra rows
     * @param extraValueLabels additional labels for the extra rows
     * @return a VBox region containing the extended selected cell section
     */
    protected final VBox createExtendedSelectedCellSection(String[] extraNameKeys, Label[] extraValueLabels) {
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
        onSelectedCellChanged(selectedGridCell);
        return selectedCellSection;
    }

}
