package de.mkalb.etpetssim.simulations.forest.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class ForestConfigViewModel
        extends AbstractConfigViewModel<ForestConfig> {

    public static final int TREE_GROWTH_DECIMALS = 3;
    public static final int LIGHTNING_IGNITION_DECIMALS = 4;

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.HEXAGON,
            Arrays.asList(CellShape.values()),
            GridEdgeBehavior.BLOCK_XY,
            List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY),
            200,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            100,
            GridSize.MIN_SIZE,
            1_024,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            4,
            1,
            48,
            CellDisplayMode.CIRCLE_BORDERED,
            List.of(CellDisplayMode.SHAPE,
                    CellDisplayMode.SHAPE_BORDERED,
                    CellDisplayMode.CIRCLE,
                    CellDisplayMode.CIRCLE_BORDERED),
            ""
    );

    // Initialization
    private static final double TREE_DENSITY_INITIAL = 0.2d;
    private static final double TREE_DENSITY_MIN = 0.0d;
    private static final double TREE_DENSITY_MAX = 1.0d;

    // Rules
    private static final double TREE_GROWTH_PROBABILITY_INITIAL = 0.002d;
    private static final double TREE_GROWTH_PROBABILITY_MIN = 0.00d;
    private static final double TREE_GROWTH_PROBABILITY_MAX = 0.20d;
    private static final double LIGHTNING_IGNITION_PROBABILITY_INITIAL = 0.001d;
    private static final double LIGHTNING_IGNITION_PROBABILITY_MIN = 0.00d;
    private static final double LIGHTNING_IGNITION_PROBABILITY_MAX = 0.02d;
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_ONLY;

    // Initialization properties
    private final InputDoubleProperty treeDensity = InputDoubleProperty.of(
            TREE_DENSITY_INITIAL,
            TREE_DENSITY_MIN,
            TREE_DENSITY_MAX);

    // Rules properties
    private final InputDoubleProperty treeGrowthProbability = InputDoubleProperty.of(
            TREE_GROWTH_PROBABILITY_INITIAL,
            TREE_GROWTH_PROBABILITY_MIN,
            TREE_GROWTH_PROBABILITY_MAX);
    private final InputDoubleProperty lightningIgnitionProbability = InputDoubleProperty.of(
            LIGHTNING_IGNITION_PROBABILITY_INITIAL,
            LIGHTNING_IGNITION_PROBABILITY_MIN,
            LIGHTNING_IGNITION_PROBABILITY_MAX);
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(
            NEIGHBORHOOD_MODE_INITIAL,
            NeighborhoodMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

    public ForestConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public ForestConfig getConfig() {
        return new ForestConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                treeDensity.getValue(),
                treeGrowthProbability.getValue(),
                lightningIgnitionProbability.getValue(),
                neighborhoodMode.getValue()
        );
    }

    public InputDoubleProperty treeDensityProperty() {
        return treeDensity;
    }

    public InputDoubleProperty treeGrowthProbabilityProperty() {
        return treeGrowthProbability;
    }

    public InputDoubleProperty lightningIgnitionProbabilityProperty() {
        return lightningIgnitionProbability;
    }

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

}
