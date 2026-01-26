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
            GridEdgeBehavior.WRAP_XY,
            List.of(GridEdgeBehavior.BLOCK_XY,
                    GridEdgeBehavior.WRAP_XY,
                    GridEdgeBehavior.BLOCK_X_WRAP_Y,
                    GridEdgeBehavior.WRAP_X_BLOCK_Y),
            50,
            10,
            1_000,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            50,
            10,
            1_000,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            8,
            4,
            32,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE),
            ""
    );

    // Initialization
    private static final double AGENT_PERCENT_INITIAL = 0.2d;
    private static final double AGENT_PERCENT_MIN = 0.0d;
    private static final double AGENT_PERCENT_MAX = 1.0d;
    private static final int SUGAR_PEAKS_INITIAL = 4;
    private static final int SUGAR_PEAKS_MIN = 1;
    private static final int SUGAR_PEAKS_MAX = 5;
    private static final int SUGAR_PEAKS_STEP = 1;
    private static final int SUGAR_RADIUS_LIMIT_INITIAL = 14;
    private static final int SUGAR_RADIUS_LIMIT_MIN = 0;
    private static final int SUGAR_RADIUS_LIMIT_MAX = 100;
    private static final int SUGAR_RADIUS_LIMIT_STEP = 1;
    private static final int MIN_SUGAR_AMOUNT = 1;
    private static final int MAX_SUGAR_AMOUNT_INITIAL = 8;
    private static final int MAX_SUGAR_AMOUNT_MIN = 1;
    private static final int MAX_SUGAR_AMOUNT_MAX = 20;
    private static final int MAX_SUGAR_AMOUNT_STEP = 1;
    private static final int AGENT_INITIAL_ENERGY_INITIAL = 12;
    private static final int AGENT_INITIAL_ENERGY_MIN = 1;
    private static final int AGENT_INITIAL_ENERGY_MAX = 20;
    private static final int AGENT_INITIAL_ENERGY_STEP = 1;

    // Rules
    private static final int SUGAR_REGENERATION_RATE_INITIAL = 1;
    private static final int SUGAR_REGENERATION_RATE_MIN = 1;
    private static final int SUGAR_REGENERATION_RATE_MAX = 10;
    private static final int SUGAR_REGENERATION_RATE_STEP = 1;
    private static final int AGENT_METABOLISM_RATE_INITIAL = 2;
    private static final int AGENT_METABOLISM_RATE_MIN = 1;
    private static final int AGENT_METABOLISM_RATE_MAX = 10;
    private static final int AGENT_METABOLISM_RATE_STEP = 1;
    private static final int AGENT_VISION_RANGE_INITIAL = 8;
    private static final int AGENT_VISION_RANGE_MIN = 1;
    private static final int AGENT_VISION_RANGE_MAX = 10;
    private static final int AGENT_VISION_RANGE_STEP = 1;
    private static final int AGENT_MAX_AGE_INITIAL = 100;
    private static final int AGENT_MAX_AGE_MIN = 1;
    private static final int AGENT_MAX_AGE_MAX = 1_000;
    private static final int AGENT_MAX_AGE_STEP = 1;
    private static final NeighborhoodMode NEIGHBORHOOD_MODE = NeighborhoodMode.EDGES_ONLY;

    // Initialization properties
    private final InputDoubleProperty agentPercent = InputDoubleProperty.of(
            AGENT_PERCENT_INITIAL,
            AGENT_PERCENT_MIN,
            AGENT_PERCENT_MAX);
    private final InputIntegerProperty sugarPeaks = InputIntegerProperty.of(
            SUGAR_PEAKS_INITIAL,
            SUGAR_PEAKS_MIN,
            SUGAR_PEAKS_MAX,
            SUGAR_PEAKS_STEP);
    private final InputIntegerProperty sugarRadiusLimit = InputIntegerProperty.of(
            SUGAR_RADIUS_LIMIT_INITIAL,
            SUGAR_RADIUS_LIMIT_MIN,
            SUGAR_RADIUS_LIMIT_MAX,
            SUGAR_RADIUS_LIMIT_STEP);
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

    // Rules properties
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
    private final InputIntegerProperty agentMaxAge = InputIntegerProperty.of(
            AGENT_MAX_AGE_INITIAL,
            AGENT_MAX_AGE_MIN,
            AGENT_MAX_AGE_MAX,
            AGENT_MAX_AGE_STEP);

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
                agentPercent.getValue(),
                sugarPeaks.getValue(),
                sugarRadiusLimit.getValue(),
                MIN_SUGAR_AMOUNT,
                maxSugarAmount.getValue(),
                agentInitialEnergy.getValue(),
                sugarRegenerationRate.getValue(),
                agentMetabolismRate.getValue(),
                agentVisionRange.getValue(),
                agentMaxAge.getValue(),
                NEIGHBORHOOD_MODE
        );
    }

    public InputDoubleProperty agentPercentProperty() {
        return agentPercent;
    }

    public InputIntegerProperty sugarPeaksProperty() {
        return sugarPeaks;
    }

    public InputIntegerProperty sugarRadiusLimitProperty() {
        return sugarRadiusLimit;
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

    public InputIntegerProperty agentMaxAgeProperty() {
        return agentMaxAge;
    }

}
