package de.mkalb.etpetssim.simulations.sugar.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class SugarConfigViewModel
        extends AbstractConfigViewModel<SugarConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.SQUARE,
            List.of(CellShape.SQUARE, CellShape.HEXAGON),
            GridEdgeBehavior.BLOCK_XY,
            List.of(GridEdgeBehavior.BLOCK_XY,
                    GridEdgeBehavior.WRAP_XY,
                    GridEdgeBehavior.BLOCK_X_WRAP_Y,
                    GridEdgeBehavior.WRAP_X_BLOCK_Y),
            100,
            10,
            1_000,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            100,
            10,
            1_000,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            6,
            4,
            32,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE),
            ""
    );
    // Initialization
    private static final double SUGAR_PERCENT_INITIAL = 0.05d;
    private static final double SUGAR_PERCENT_MIN = 0.0d;
    private static final double SUGAR_PERCENT_MAX = 1.0d;
    private static final double AGENT_PERCENT_INITIAL = 0.05d;
    private static final double AGENT_PERCENT_MIN = 0.0d;
    private static final double AGENT_PERCENT_MAX = 1.0d;
    private static final int MAX_SUGAR_AMOUNT_INITIAL = 6;
    private static final int MAX_SUGAR_AMOUNT_MIN = 1;
    private static final int MAX_SUGAR_AMOUNT_MAX = 20;
    private static final int MAX_SUGAR_AMOUNT_STEP = 1;
    private static final int AGENT_INITIAL_ENERGY_INITIAL = 5;
    private static final int AGENT_INITIAL_ENERGY_MIN = 1;
    private static final int AGENT_INITIAL_ENERGY_MAX = 20;
    private static final int AGENT_INITIAL_ENERGY_STEP = 1;
    // Rules
    private static final int SUGAR_REGENERATION_RATE_INITIAL = 1;
    private static final int SUGAR_REGENERATION_RATE_MIN = 1;
    private static final int SUGAR_REGENERATION_RATE_MAX = 10;
    private static final int SUGAR_REGENERATION_RATE_STEP = 1;
    private static final int AGENT_METABOLISM_RATE_INITIAL = 1;
    private static final int AGENT_METABOLISM_RATE_MIN = 1;
    private static final int AGENT_METABOLISM_RATE_MAX = 10;
    private static final int AGENT_METABOLISM_RATE_STEP = 1;
    private static final int AGENT_VISION_RANGE_INITIAL = 4;
    private static final int AGENT_VISION_RANGE_MIN = 1;
    private static final int AGENT_VISION_RANGE_MAX = 10;
    private static final int AGENT_VISION_RANGE_STEP = 1;
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_ONLY;

    // Initialization
    private final InputDoubleProperty sugarPercent = InputDoubleProperty.of(
            SUGAR_PERCENT_INITIAL,
            SUGAR_PERCENT_MIN,
            SUGAR_PERCENT_MAX);
    private final InputDoubleProperty agentPercent = InputDoubleProperty.of(
            AGENT_PERCENT_INITIAL,
            AGENT_PERCENT_MIN,
            AGENT_PERCENT_MAX);
    private final InputIntegerProperty maxSugarAmount = InputIntegerProperty.of(
            MAX_SUGAR_AMOUNT_INITIAL,
            MAX_SUGAR_AMOUNT_MIN,
            MAX_SUGAR_AMOUNT_MAX,
            MAX_SUGAR_AMOUNT_STEP);
    private final InputIntegerProperty agentInitialEnergy = InputIntegerProperty.of(
            AGENT_INITIAL_ENERGY_INITIAL,
            AGENT_INITIAL_ENERGY_MIN,
            AGENT_INITIAL_ENERGY_MAX,
            AGENT_INITIAL_ENERGY_STEP);
    // Rules
    private final InputIntegerProperty sugarRegenerationRate = InputIntegerProperty.of(
            SUGAR_REGENERATION_RATE_INITIAL,
            SUGAR_REGENERATION_RATE_MIN,
            SUGAR_REGENERATION_RATE_MAX,
            SUGAR_REGENERATION_RATE_STEP);
    private final InputIntegerProperty agentMetabolismRate = InputIntegerProperty.of(
            AGENT_METABOLISM_RATE_INITIAL,
            AGENT_METABOLISM_RATE_MIN,
            AGENT_METABOLISM_RATE_MAX,
            AGENT_METABOLISM_RATE_STEP);
    private final InputIntegerProperty agentVisionRange = InputIntegerProperty.of(
            AGENT_VISION_RANGE_INITIAL,
            AGENT_VISION_RANGE_MIN,
            AGENT_VISION_RANGE_MAX,
            AGENT_VISION_RANGE_STEP);

    public SugarConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public SugarConfig getConfig() {
        return new SugarConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                sugarPercent.getValue(),
                agentPercent.getValue(),
                maxSugarAmount.getValue(),
                agentInitialEnergy.getValue(),
                sugarRegenerationRate.getValue(),
                agentMetabolismRate.getValue(),
                agentVisionRange.getValue(),
                NEIGHBORHOOD_MODE_INITIAL
        );
    }

    public InputDoubleProperty sugarPercentProperty() {
        return sugarPercent;
    }

    public InputDoubleProperty agentPercentProperty() {
        return agentPercent;
    }

    public InputIntegerProperty maxSugarAmountProperty() {
        return maxSugarAmount;
    }

    public InputIntegerProperty agentInitialEnergyProperty() {
        return agentInitialEnergy;
    }

    public InputIntegerProperty sugarRegenerationRateProperty() {
        return sugarRegenerationRate;
    }

    public InputIntegerProperty agentMetabolismRateProperty() {
        return agentMetabolismRate;
    }

    public InputIntegerProperty agentVisionRangeProperty() {
        return agentVisionRange;
    }

}
