package de.mkalb.etpetssim.simulations.sugar.viewmodel;

import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_INITIAL_ENERGY_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_MAX_AGE_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_METABOLISM_RATE_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_PERCENT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.AGENT_VISION_RANGE_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_DISPLAY_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_DISPLAY_MODE_VALUES;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_EDGE_LENGTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_EDGE_LENGTH_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_EDGE_LENGTH_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_SHAPE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.CELL_SHAPE_VALUES;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_EDGE_BEHAVIOR_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_EDGE_BEHAVIOR_VALUES;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_HEIGHT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_HEIGHT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_HEIGHT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_HEIGHT_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_WIDTH_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_WIDTH_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_WIDTH_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.GRID_WIDTH_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MAX_SUGAR_AMOUNT_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.NEIGHBORHOOD_MODE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SEED_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_PEAKS_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_RADIUS_LIMIT_STEP;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_MAX;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_MIN;
import static de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints.SUGAR_REGENERATION_RATE_STEP;

public final class SugarConfigViewModel
        extends AbstractConfigViewModel<SugarConfig> {

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
            SEED_DEFAULT
    );

    // Initialization
    private final InputDoubleProperty agentPercent = InputDoubleProperty.of(
            AGENT_PERCENT_DEFAULT,
            AGENT_PERCENT_MIN,
            AGENT_PERCENT_MAX);
    private final InputIntegerProperty sugarPeaks = InputIntegerProperty.of(
            SUGAR_PEAKS_DEFAULT,
            SUGAR_PEAKS_MIN,
            SUGAR_PEAKS_MAX,
            SUGAR_PEAKS_STEP);
    private final InputIntegerProperty sugarRadiusLimit = InputIntegerProperty.of(
            SUGAR_RADIUS_LIMIT_DEFAULT,
            SUGAR_RADIUS_LIMIT_MIN,
            SUGAR_RADIUS_LIMIT_MAX,
            SUGAR_RADIUS_LIMIT_STEP);
    private final InputIntegerProperty maxSugarAmount = InputIntegerProperty.of(
            MAX_SUGAR_AMOUNT_DEFAULT,
            MAX_SUGAR_AMOUNT_MIN,
            MAX_SUGAR_AMOUNT_MAX,
            MAX_SUGAR_AMOUNT_STEP);
    private final InputIntegerProperty agentInitialEnergy = InputIntegerProperty.of(
            AGENT_INITIAL_ENERGY_DEFAULT,
            AGENT_INITIAL_ENERGY_MIN,
            AGENT_INITIAL_ENERGY_MAX,
            AGENT_INITIAL_ENERGY_STEP);

    // Rules
    private final InputIntegerProperty sugarRegenerationRate = InputIntegerProperty.of(
            SUGAR_REGENERATION_RATE_DEFAULT,
            SUGAR_REGENERATION_RATE_MIN,
            SUGAR_REGENERATION_RATE_MAX,
            SUGAR_REGENERATION_RATE_STEP);
    private final InputIntegerProperty agentMetabolismRate = InputIntegerProperty.of(
            AGENT_METABOLISM_RATE_DEFAULT,
            AGENT_METABOLISM_RATE_MIN,
            AGENT_METABOLISM_RATE_MAX,
            AGENT_METABOLISM_RATE_STEP);
    private final InputIntegerProperty agentVisionRange = InputIntegerProperty.of(
            AGENT_VISION_RANGE_DEFAULT,
            AGENT_VISION_RANGE_MIN,
            AGENT_VISION_RANGE_MAX,
            AGENT_VISION_RANGE_STEP);
    private final InputIntegerProperty agentMaxAge = InputIntegerProperty.of(
            AGENT_MAX_AGE_DEFAULT,
            AGENT_MAX_AGE_MIN,
            AGENT_MAX_AGE_MAX,
            AGENT_MAX_AGE_STEP);

    public SugarConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public SugarConfig createConfig() {
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
                MIN_SUGAR_AMOUNT_DEFAULT,
                maxSugarAmount.getValue(),
                agentInitialEnergy.getValue(),
                NEIGHBORHOOD_MODE_DEFAULT,
                sugarRegenerationRate.getValue(),
                agentMetabolismRate.getValue(),
                agentVisionRange.getValue(),
                agentMaxAge.getValue()
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
