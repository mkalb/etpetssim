package de.mkalb.etpetssim.simulations.rebounding.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class ReboundingConfigViewModel
        extends AbstractConfigViewModel<ReboundingConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.HEXAGON,
            List.of(CellShape.SQUARE, CellShape.HEXAGON),
            GridEdgeBehavior.BLOCK_XY,
            List.of(GridEdgeBehavior.BLOCK_XY),
            70,
            GridSize.MIN_SIZE,
            500,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            30,
            GridSize.MIN_SIZE,
            500,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            8,
            1,
            50,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE),
            ""
    );

    // Initialization
    private static final int VERTICAL_WALLS_INITIAL = 2;
    private static final int VERTICAL_WALLS_MIN = 0;
    private static final int VERTICAL_WALLS_MAX = 100;
    private static final int VERTICAL_WALLS_STEP = 1;
    private static final double MOVING_ENTITY_PERCENT_INITIAL = 0.02d;
    private static final double MOVING_ENTITY_PERCENT_MIN = 0.0d;
    private static final double MOVING_ENTITY_PERCENT_MAX = 0.1d;

    // Rules
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_AND_VERTICES;

    // Initialization properties
    private final InputIntegerProperty verticalWalls = InputIntegerProperty.of(
            VERTICAL_WALLS_INITIAL,
            VERTICAL_WALLS_MIN,
            VERTICAL_WALLS_MAX,
            VERTICAL_WALLS_STEP);
    private final InputDoubleProperty movingEntityPercent = InputDoubleProperty.of(
            MOVING_ENTITY_PERCENT_INITIAL,
            MOVING_ENTITY_PERCENT_MIN,
            MOVING_ENTITY_PERCENT_MAX);

    // Rules properties
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(
            NEIGHBORHOOD_MODE_INITIAL,
            NeighborhoodMode.class,
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
