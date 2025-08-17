package de.mkalb.etpetssim.simulations.wator.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class WatorConfigViewModel
        extends AbstractConfigViewModel<WatorConfig> {

    private static final CellShape CELL_SHAPE_INITIAL = CellShape.SQUARE;
    private static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_INITIAL = GridEdgeBehavior.WRAP_XY;
    private static final int GRID_WIDTH_INITIAL = 64;
    private static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    private static final int GRID_WIDTH_MAX = 1_024;
    private static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    private static final int GRID_HEIGHT_INITIAL = 32;
    private static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    private static final int GRID_HEIGHT_MAX = 1_024;
    private static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;
    private static final int CELL_EDGE_LENGTH_INITIAL = 8;
    private static final int CELL_EDGE_LENGTH_MIN = 2;
    private static final int CELL_EDGE_LENGTH_MAX = 32;

    private static final double FISH_PERCENT_INITIAL = 0.25d;
    private static final double FISH_PERCENT_MAX = 1.0d;
    private static final double FISH_PERCENT_MIN = 0.0d;
    private static final double SHARK_PERCENT_INITIAL = 0.10d;
    private static final double SHARK_PERCENT_MAX = 1.0d;
    private static final double SHARK_PERCENT_MIN = 0.0d;
    private static final int FISH_MAX_AGE_INITIAL = 20;
    private static final int FISH_MAX_AGE_MAX = 100;
    private static final int FISH_MAX_AGE_MIN = 10;
    private static final int FISH_MAX_AGE_STEP = 1;
    private static final int SHARK_MAX_AGE_INITIAL = 40;
    private static final int SHARK_MAX_AGE_MAX = 100;
    private static final int SHARK_MAX_AGE_MIN = 10;
    private static final int SHARK_MAX_AGE_STEP = 1;
    private static final int SHARK_BIRTH_ENERGY_INITIAL = 8;
    private static final int SHARK_BIRTH_ENERGY_MAX = 100;
    private static final int SHARK_BIRTH_ENERGY_MIN = 1;
    private static final int SHARK_BIRTH_ENERGY_STEP = 1;
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_ONLY;

    private final InputDoubleProperty fishPercent = InputDoubleProperty.of(
            FISH_PERCENT_INITIAL,
            FISH_PERCENT_MIN,
            FISH_PERCENT_MAX);
    private final InputDoubleProperty sharkPercent = InputDoubleProperty.of(
            SHARK_PERCENT_INITIAL,
            SHARK_PERCENT_MIN,
            SHARK_PERCENT_MAX);
    private final InputIntegerProperty fishMaxAge = InputIntegerProperty.of(
            FISH_MAX_AGE_INITIAL,
            FISH_MAX_AGE_MIN,
            FISH_MAX_AGE_MAX,
            FISH_MAX_AGE_STEP);
    private final InputIntegerProperty sharkMaxAge = InputIntegerProperty.of(
            SHARK_MAX_AGE_INITIAL,
            SHARK_MAX_AGE_MIN,
            SHARK_MAX_AGE_MAX,
            SHARK_MAX_AGE_STEP);
    private final InputIntegerProperty sharkBirthEnergy = InputIntegerProperty.of(
            SHARK_BIRTH_ENERGY_INITIAL,
            SHARK_BIRTH_ENERGY_MIN,
            SHARK_BIRTH_ENERGY_MAX,
            SHARK_BIRTH_ENERGY_STEP);
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode =
            InputEnumProperty.of(NEIGHBORHOOD_MODE_INITIAL, NeighborhoodMode.class,
                    e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

    public WatorConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, new GridStructureSettings(
                CELL_SHAPE_INITIAL,
                GRID_EDGE_BEHAVIOR_INITIAL,
                List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY),
                GRID_WIDTH_INITIAL,
                GRID_WIDTH_MIN,
                GRID_WIDTH_MAX,
                GRID_WIDTH_STEP,
                GRID_HEIGHT_INITIAL,
                GRID_HEIGHT_MIN,
                GRID_HEIGHT_MAX,
                GRID_HEIGHT_STEP,
                CELL_EDGE_LENGTH_INITIAL,
                CELL_EDGE_LENGTH_MIN,
                CELL_EDGE_LENGTH_MAX));
    }

    @Override
    public WatorConfig getConfig() {
        return new WatorConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                fishPercent.getValue(),
                sharkPercent.getValue(),
                fishMaxAge.getValue(),
                sharkMaxAge.getValue(),
                sharkBirthEnergy.getValue(),
                neighborhoodMode.getValue()
        );
    }

    public InputDoubleProperty fishPercentProperty() {
        return fishPercent;
    }

    public InputDoubleProperty sharkPercentProperty() {
        return sharkPercent;
    }

    public InputIntegerProperty fishMaxAgeProperty() {
        return fishMaxAge;
    }

    public InputIntegerProperty sharkMaxAgeProperty() {
        return sharkMaxAge;
    }

    public InputIntegerProperty sharkBirthEnergyProperty() {
        return sharkBirthEnergy;
    }

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

}
