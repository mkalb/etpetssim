package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import static de.mkalb.etpetssim.simulations.lab.model.LabConstraints.*;

public final class LabConfigViewModel
        extends AbstractConfigViewModel<LabConfig> {

    private static final String LAB_CONFIG_VALIDATION_GRID_STRUCTURE = "lab.config.validation.gridstructure";

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CELL_SHAPE_DEFAULT,
            CELL_SHAPE_VALUES,
            GRID_EDGE_BEHAVIOR_DEFAULT,
            GRID_EDGE_BEHAVIOR_VALUES,
            GRID_WIDTH_DEFAULT,
            GRID_WIDTH_MIN,
            GRID_WIDTH_MAX,
            GRID_WIDTH_STEP,
            GRID_HEIGHT_DEFAULT,
            GRID_HEIGHT_MIN,
            GRID_HEIGHT_MAX,
            GRID_HEIGHT_STEP,
            CELL_EDGE_LENGTH_DEFAULT,
            CELL_EDGE_LENGTH_MIN,
            CELL_EDGE_LENGTH_MAX,
            CELL_DISPLAY_MODE_DEFAULT,
            CELL_DISPLAY_MODE_VALUES,
            SEED_INITIAL
    );

    // Layout
    private final InputEnumProperty<LabColorMode> colorMode = InputEnumProperty.of(COLOR_MODE_DEFAULT, COLOR_MODE_VALUES, Enum::toString);

    // Rules - NeighborhoodMode
    private final InputChoiceProperty<NeighborhoodMode> neighborhoodMode = InputChoiceProperty.ofEnum(
            NEIGHBORHOOD_MODE_DEFAULT,
            NeighborhoodMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

    // Internal state
    private final BooleanProperty configChangedRequested = new SimpleBooleanProperty(false);

    public LabConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
        addConfigValidationRule(
                Bindings.createBooleanBinding(
                        () -> !GridStructure.isValid(
                                new GridTopology(cellShapeProperty().getValue(), gridEdgeBehaviorProperty().getValue()),
                                new GridSize(gridWidthProperty().getValue(), gridHeightProperty().getValue())
                        ),
                        cellShapeProperty().property(),
                        gridEdgeBehaviorProperty().property(),
                        gridWidthProperty().property(),
                        gridHeightProperty().property()
                ),
                () -> AppLocalization.getText(LAB_CONFIG_VALIDATION_GRID_STRUCTURE)
        );
        initializeConfigListeners();
    }

    private void initializeConfigListeners() {
        cellShapeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridEdgeBehaviorProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridWidthProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridHeightProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        cellEdgeLengthProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        cellDisplayModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        colorModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        seedProperty().stringProperty().addListener((_, _, _) -> configChangedRequested.set(true));
        neighborhoodModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
    }

    @Override
    public LabConfig getConfig() {
        return new LabConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                colorMode.getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                neighborhoodMode.getValue()
        );
    }

    public InputEnumProperty<LabColorMode> colorModeProperty() {
        return colorMode;
    }

    public InputChoiceProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

    public BooleanProperty configChangedRequestedProperty() {
        return configChangedRequested;
    }

}
