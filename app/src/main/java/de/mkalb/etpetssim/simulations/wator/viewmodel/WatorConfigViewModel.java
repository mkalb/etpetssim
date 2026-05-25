package de.mkalb.etpetssim.simulations.wator.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorConfig;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import static de.mkalb.etpetssim.simulations.wator.model.WatorConstraints.*;

public final class WatorConfigViewModel
        extends AbstractConfigViewModel<WatorConfig> {

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

    // Initialization properties
    private final InputDoubleProperty fishPercent = InputDoubleProperty.of(
            FISH_PERCENT_DEFAULT,
            FISH_PERCENT_MIN,
            FISH_PERCENT_MAX);
    private final InputDoubleProperty sharkPercent = InputDoubleProperty.of(
            SHARK_PERCENT_DEFAULT,
            SHARK_PERCENT_MIN,
            SHARK_PERCENT_MAX);

    // Rules properties
    private final InputIntegerProperty fishMaxAge = InputIntegerProperty.of(
            FISH_MAX_AGE_DEFAULT,
            FISH_MAX_AGE_MIN,
            FISH_MAX_AGE_MAX,
            FISH_MAX_AGE_STEP);
    private final InputIntegerProperty fishMinReproductionAge = InputIntegerProperty.of(
            FISH_MIN_REPRODUCTION_AGE_DEFAULT,
            FISH_MIN_REPRODUCTION_AGE_MIN,
            FISH_MIN_REPRODUCTION_AGE_MAX,
            FISH_MIN_REPRODUCTION_AGE_STEP);
    private final InputIntegerProperty fishMinReproductionInterval = InputIntegerProperty.of(
            FISH_MIN_REPRODUCTION_INTERVAL_DEFAULT,
            FISH_MIN_REPRODUCTION_INTERVAL_MIN,
            FISH_MIN_REPRODUCTION_INTERVAL_MAX,
            FISH_MIN_REPRODUCTION_INTERVAL_STEP);
    private final InputIntegerProperty sharkMaxAge = InputIntegerProperty.of(
            SHARK_MAX_AGE_DEFAULT,
            SHARK_MAX_AGE_MIN,
            SHARK_MAX_AGE_MAX,
            SHARK_MAX_AGE_STEP);
    private final InputIntegerProperty sharkBirthEnergy = InputIntegerProperty.of(
            SHARK_BIRTH_ENERGY_DEFAULT,
            SHARK_BIRTH_ENERGY_MIN,
            SHARK_BIRTH_ENERGY_MAX,
            SHARK_BIRTH_ENERGY_STEP);
    private final InputIntegerProperty sharkEnergyLossPerStep = InputIntegerProperty.of(
            SHARK_ENERGY_LOSS_PER_STEP_DEFAULT,
            SHARK_ENERGY_LOSS_PER_STEP_MIN,
            SHARK_ENERGY_LOSS_PER_STEP_MAX,
            SHARK_ENERGY_LOSS_PER_STEP_STEP);
    private final InputIntegerProperty sharkEnergyGainPerFish = InputIntegerProperty.of(
            SHARK_ENERGY_GAIN_PER_FISH_DEFAULT,
            SHARK_ENERGY_GAIN_PER_FISH_MIN,
            SHARK_ENERGY_GAIN_PER_FISH_MAX,
            SHARK_ENERGY_GAIN_PER_FISH_STEP);
    private final InputIntegerProperty sharkMinReproductionAge = InputIntegerProperty.of(
            SHARK_MIN_REPRODUCTION_AGE_DEFAULT,
            SHARK_MIN_REPRODUCTION_AGE_MIN,
            SHARK_MIN_REPRODUCTION_AGE_MAX,
            SHARK_MIN_REPRODUCTION_AGE_STEP);
    private final InputIntegerProperty sharkMinReproductionEnergy = InputIntegerProperty.of(
            SHARK_MIN_REPRODUCTION_ENERGY_DEFAULT,
            SHARK_MIN_REPRODUCTION_ENERGY_MIN,
            SHARK_MIN_REPRODUCTION_ENERGY_MAX,
            SHARK_MIN_REPRODUCTION_ENERGY_STEP);
    private final InputIntegerProperty sharkMinReproductionInterval = InputIntegerProperty.of(
            SHARK_MIN_REPRODUCTION_INTERVAL_DEFAULT,
            SHARK_MIN_REPRODUCTION_INTERVAL_MIN,
            SHARK_MIN_REPRODUCTION_INTERVAL_MAX,
            SHARK_MIN_REPRODUCTION_INTERVAL_STEP);
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(
            NEIGHBORHOOD_MODE_DEFAULT,
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
