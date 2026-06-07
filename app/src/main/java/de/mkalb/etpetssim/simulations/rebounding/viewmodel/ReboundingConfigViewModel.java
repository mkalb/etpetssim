package de.mkalb.etpetssim.simulations.rebounding.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConstraints.*;

public final class ReboundingConfigViewModel
        extends AbstractConfigViewModel<ReboundingConfig> {

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

    // Initialization
    private final InputIntegerProperty verticalWalls = InputIntegerProperty.of(
            VERTICAL_WALLS_DEFAULT,
            VERTICAL_WALLS_MIN,
            VERTICAL_WALLS_MAX,
            VERTICAL_WALLS_STEP);
    private final InputDoubleProperty movingEntityPercent = InputDoubleProperty.of(
            MOVING_ENTITY_PERCENT_DEFAULT,
            MOVING_ENTITY_PERCENT_MIN,
            MOVING_ENTITY_PERCENT_MAX);

    // Rules - NeighborhoodMode
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(
            NEIGHBORHOOD_MODE_DEFAULT,
            NEIGHBORHOOD_MODE_VALUES,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

    public ReboundingConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public ReboundingConfig getConfig() {
        return new ReboundingConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                verticalWalls.getValue(),
                movingEntityPercent.getValue(),
                neighborhoodMode.getValue()
        );
    }

    public InputIntegerProperty verticalWallsProperty() {
        return verticalWalls;
    }

    public InputDoubleProperty movingEntityPercentProperty() {
        return movingEntityPercent;
    }

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

}
