package de.mkalb.etpetssim.simulations.forest.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.forest.model.ForestConstraints.*;

public final class ForestConfigViewModel
        extends AbstractConfigViewModel<ForestConfig> {

    /**
     * Number of fractional digits shown for tree growth probability inputs in the forest view.
     */
    public static final int TREE_GROWTH_DECIMALS = 3;

    /**
     * Number of fractional digits shown for lightning ignition probability inputs in the forest view.
     */
    public static final int LIGHTNING_IGNITION_DECIMALS = 4;

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
    private final InputDoubleProperty treeDensity = InputDoubleProperty.of(
            TREE_DENSITY_DEFAULT,
            TREE_DENSITY_MIN,
            TREE_DENSITY_MAX);

    // Rules - NeighborhoodMode
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(
            NEIGHBORHOOD_MODE_DEFAULT,
            NEIGHBORHOOD_MODE_VALUES,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

    // Rules
    private final InputDoubleProperty treeGrowthProbability = InputDoubleProperty.of(
            TREE_GROWTH_PROBABILITY_DEFAULT,
            TREE_GROWTH_PROBABILITY_MIN,
            TREE_GROWTH_PROBABILITY_MAX);
    private final InputDoubleProperty lightningIgnitionProbability = InputDoubleProperty.of(
            LIGHTNING_IGNITION_PROBABILITY_DEFAULT,
            LIGHTNING_IGNITION_PROBABILITY_MIN,
            LIGHTNING_IGNITION_PROBABILITY_MAX);

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
                neighborhoodMode.getValue(),
                treeGrowthProbability.getValue(),
                lightningIgnitionProbability.getValue()
        );
    }

    public InputDoubleProperty treeDensityProperty() {
        return treeDensity;
    }

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

    public InputDoubleProperty treeGrowthProbabilityProperty() {
        return treeGrowthProbability;
    }

    public InputDoubleProperty lightningIgnitionProbabilityProperty() {
        return lightningIgnitionProbability;
    }

}
