package de.mkalb.etpetssim.simulations.wator.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class WatorConfigViewModel
        extends AbstractConfigViewModel<WatorConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.SQUARE,
            GridEdgeBehavior.WRAP_XY,
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
            2,
            64,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE,
                    CellDisplayMode.SHAPE_BORDERED,
                    CellDisplayMode.CIRCLE,
                    CellDisplayMode.CIRCLE_BORDERED,
                    CellDisplayMode.EMOJI),
            ""
    );

    // Initialization
    private static final double FISH_PERCENT_INITIAL = 0.20d;
    private static final double FISH_PERCENT_MIN = 0.0d;
    private static final double FISH_PERCENT_MAX = 1.0d;
    private static final double SHARK_PERCENT_INITIAL = 0.05d;
    private static final double SHARK_PERCENT_MIN = 0.0d;
    private static final double SHARK_PERCENT_MAX = 1.0d;

    // Rules
    private static final int FISH_MAX_AGE_INITIAL = 20;
    private static final int FISH_MAX_AGE_MIN = 1;
    private static final int FISH_MAX_AGE_MAX = 1_000;
    private static final int FISH_MAX_AGE_STEP = 1;
    private static final int FISH_MIN_REPRODUCTION_AGE_INITIAL = 5;
    private static final int FISH_MIN_REPRODUCTION_AGE_MIN = 1;
    private static final int FISH_MIN_REPRODUCTION_AGE_MAX = 1_000;
    private static final int FISH_MIN_REPRODUCTION_AGE_STEP = 1;
    private static final int FISH_MIN_REPRODUCTION_INTERVAL_INITIAL = 3;
    private static final int FISH_MIN_REPRODUCTION_INTERVAL_MIN = 1;
    private static final int FISH_MIN_REPRODUCTION_INTERVAL_MAX = 1_000;
    private static final int FISH_MIN_REPRODUCTION_INTERVAL_STEP = 1;
    private static final int SHARK_MAX_AGE_INITIAL = 40;
    private static final int SHARK_MAX_AGE_MIN = 1;
    private static final int SHARK_MAX_AGE_MAX = 1_000;
    private static final int SHARK_MAX_AGE_STEP = 1;
    private static final int SHARK_BIRTH_ENERGY_INITIAL = 8;
    private static final int SHARK_BIRTH_ENERGY_MIN = 1;
    private static final int SHARK_BIRTH_ENERGY_MAX = 1_000;
    private static final int SHARK_BIRTH_ENERGY_STEP = 1;
    private static final int SHARK_ENERGY_LOSS_PER_STEP_INITIAL = 1;
    private static final int SHARK_ENERGY_LOSS_PER_STEP_MIN = 1;
    private static final int SHARK_ENERGY_LOSS_PER_STEP_MAX = 1_000;
    private static final int SHARK_ENERGY_LOSS_PER_STEP_STEP = 1;
    private static final int SHARK_ENERGY_GAIN_PER_FISH_INITIAL = 2;
    private static final int SHARK_ENERGY_GAIN_PER_FISH_MIN = 1;
    private static final int SHARK_ENERGY_GAIN_PER_FISH_MAX = 1_000;
    private static final int SHARK_ENERGY_GAIN_PER_FISH_STEP = 1;
    private static final int SHARK_MIN_REPRODUCTION_AGE_INITIAL = 15;
    private static final int SHARK_MIN_REPRODUCTION_AGE_MIN = 1;
    private static final int SHARK_MIN_REPRODUCTION_AGE_MAX = 1_000;
    private static final int SHARK_MIN_REPRODUCTION_AGE_STEP = 1;
    private static final int SHARK_MIN_REPRODUCTION_ENERGY_INITIAL = 5;
    private static final int SHARK_MIN_REPRODUCTION_ENERGY_MIN = 1;
    private static final int SHARK_MIN_REPRODUCTION_ENERGY_MAX = 1_000;
    private static final int SHARK_MIN_REPRODUCTION_ENERGY_STEP = 1;
    private static final int SHARK_MIN_REPRODUCTION_INTERVAL_INITIAL = 3;
    private static final int SHARK_MIN_REPRODUCTION_INTERVAL_MIN = 1;
    private static final int SHARK_MIN_REPRODUCTION_INTERVAL_MAX = 1_000;
    private static final int SHARK_MIN_REPRODUCTION_INTERVAL_STEP = 1;

    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_ONLY;

    // Initialization
    private final InputDoubleProperty fishPercent = InputDoubleProperty.of(
            FISH_PERCENT_INITIAL,
            FISH_PERCENT_MIN,
            FISH_PERCENT_MAX);
    private final InputDoubleProperty sharkPercent = InputDoubleProperty.of(
            SHARK_PERCENT_INITIAL,
            SHARK_PERCENT_MIN,
            SHARK_PERCENT_MAX);

    // Rules
    private final InputIntegerProperty fishMaxAge = InputIntegerProperty.of(
            FISH_MAX_AGE_INITIAL,
            FISH_MAX_AGE_MIN,
            FISH_MAX_AGE_MAX,
            FISH_MAX_AGE_STEP);
    private final InputIntegerProperty fishMinReproductionAge = InputIntegerProperty.of(
            FISH_MIN_REPRODUCTION_AGE_INITIAL,
            FISH_MIN_REPRODUCTION_AGE_MIN,
            FISH_MIN_REPRODUCTION_AGE_MAX,
            FISH_MIN_REPRODUCTION_AGE_STEP);
    private final InputIntegerProperty fishMinReproductionInterval = InputIntegerProperty.of(
            FISH_MIN_REPRODUCTION_INTERVAL_INITIAL,
            FISH_MIN_REPRODUCTION_INTERVAL_MIN,
            FISH_MIN_REPRODUCTION_INTERVAL_MAX,
            FISH_MIN_REPRODUCTION_INTERVAL_STEP);
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
    private final InputIntegerProperty sharkEnergyLossPerStep = InputIntegerProperty.of(
            SHARK_ENERGY_LOSS_PER_STEP_INITIAL,
            SHARK_ENERGY_LOSS_PER_STEP_MIN,
            SHARK_ENERGY_LOSS_PER_STEP_MAX,
            SHARK_ENERGY_LOSS_PER_STEP_STEP);
    private final InputIntegerProperty sharkEnergyGainPerFish = InputIntegerProperty.of(
            SHARK_ENERGY_GAIN_PER_FISH_INITIAL,
            SHARK_ENERGY_GAIN_PER_FISH_MIN,
            SHARK_ENERGY_GAIN_PER_FISH_MAX,
            SHARK_ENERGY_GAIN_PER_FISH_STEP);
    private final InputIntegerProperty sharkMinReproductionAge = InputIntegerProperty.of(
            SHARK_MIN_REPRODUCTION_AGE_INITIAL,
            SHARK_MIN_REPRODUCTION_AGE_MIN,
            SHARK_MIN_REPRODUCTION_AGE_MAX,
            SHARK_MIN_REPRODUCTION_AGE_STEP);
    private final InputIntegerProperty sharkMinReproductionEnergy = InputIntegerProperty.of(
            SHARK_MIN_REPRODUCTION_ENERGY_INITIAL,
            SHARK_MIN_REPRODUCTION_ENERGY_MIN,
            SHARK_MIN_REPRODUCTION_ENERGY_MAX,
            SHARK_MIN_REPRODUCTION_ENERGY_STEP);
    private final InputIntegerProperty sharkMinReproductionInterval = InputIntegerProperty.of(
            SHARK_MIN_REPRODUCTION_INTERVAL_INITIAL,
            SHARK_MIN_REPRODUCTION_INTERVAL_MIN,
            SHARK_MIN_REPRODUCTION_INTERVAL_MAX,
            SHARK_MIN_REPRODUCTION_INTERVAL_STEP);
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(
            NEIGHBORHOOD_MODE_INITIAL,
            NeighborhoodMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));

    public WatorConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public WatorConfig getConfig() {
        return new WatorConfig(
                // Structure
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                // Initialization
                fishPercent.getValue(),
                sharkPercent.getValue(),
                // Rules
                fishMaxAge.getValue(),
                fishMinReproductionAge.getValue(),
                fishMinReproductionInterval.getValue(),
                sharkMaxAge.getValue(),
                sharkBirthEnergy.getValue(),
                sharkEnergyLossPerStep.getValue(),
                sharkEnergyGainPerFish.getValue(),
                sharkMinReproductionAge.getValue(),
                sharkMinReproductionEnergy.getValue(),
                sharkMinReproductionInterval.getValue(),
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

    public InputIntegerProperty fishMinReproductionAgeProperty() {
        return fishMinReproductionAge;
    }

    public InputIntegerProperty fishMinReproductionIntervalProperty() {
        return fishMinReproductionInterval;
    }

    public InputIntegerProperty sharkMaxAgeProperty() {
        return sharkMaxAge;
    }

    public InputIntegerProperty sharkBirthEnergyProperty() {
        return sharkBirthEnergy;
    }

    public InputIntegerProperty sharkEnergyLossPerStepProperty() {
        return sharkEnergyLossPerStep;
    }

    public InputIntegerProperty sharkEnergyGainPerFishProperty() {
        return sharkEnergyGainPerFish;
    }

    public InputIntegerProperty sharkMinReproductionAgeProperty() {
        return sharkMinReproductionAge;
    }

    public InputIntegerProperty sharkMinReproductionEnergyProperty() {
        return sharkMinReproductionEnergy;
    }

    public InputIntegerProperty sharkMinReproductionIntervalProperty() {
        return sharkMinReproductionInterval;
    }

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

}
