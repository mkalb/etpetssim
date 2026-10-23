package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.function.*;

import static de.mkalb.etpetssim.simulations.wator.model.WatorConstraints.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class WatorConfigTest {

    private static boolean isValidWith(Consumer<ConfigValues> change) {
        ConfigValues values = new ConfigValues();
        change.accept(values);
        return values.toConfig().isValid();
    }

    @Test
    void testIsValidAcceptsDefaultConstraints() {
        assertTrue(isValidWith(_ -> {}));
    }

    @Test
    void testIsValidAcceptsSupportedAlternativeSelections() {
        assertTrue(isValidWith(v -> {
            v.cellShape = CellShape.TRIANGLE;
            v.gridEdgeBehavior = GridEdgeBehavior.BLOCK_XY;
            v.cellDisplayMode = CellDisplayMode.EMOJI;
            v.neighborhoodMode = NeighborhoodMode.EDGES_AND_VERTICES;
        }));
    }

    @Test
    void testIsValidAcceptsBoundaryValues() {
        assertAll(
                () -> assertTrue(isValidWith(v -> v.fishPercent = FISH_PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> {
                    v.fishPercent = FISH_PERCENT_MAX;
                    v.sharkPercent = SHARK_PERCENT_MIN;
                })),
                () -> assertTrue(isValidWith(v -> v.sharkPercent = SHARK_PERCENT_MIN)),
                () -> assertTrue(isValidWith(v -> {
                    v.fishPercent = FISH_PERCENT_MIN;
                    v.sharkPercent = SHARK_PERCENT_MAX;
                })),
                () -> assertTrue(isValidWith(v -> v.fishMaxAge = FISH_MAX_AGE_MIN)),
                () -> assertTrue(isValidWith(v -> v.fishMaxAge = FISH_MAX_AGE_MAX)),
                () -> assertTrue(isValidWith(v -> v.fishMinReproductionAge = FISH_MIN_REPRODUCTION_AGE_MIN)),
                () -> assertTrue(isValidWith(v -> v.fishMinReproductionAge = FISH_MIN_REPRODUCTION_AGE_MAX)),
                () -> assertTrue(isValidWith(v -> v.fishMinReproductionInterval = FISH_MIN_REPRODUCTION_INTERVAL_MIN)),
                () -> assertTrue(isValidWith(v -> v.fishMinReproductionInterval = FISH_MIN_REPRODUCTION_INTERVAL_MAX)),
                () -> assertTrue(isValidWith(v -> v.sharkMaxAge = SHARK_MAX_AGE_MIN)),
                () -> assertTrue(isValidWith(v -> v.sharkMaxAge = SHARK_MAX_AGE_MAX)),
                () -> assertTrue(isValidWith(v -> v.sharkBirthEnergy = SHARK_BIRTH_ENERGY_MIN)),
                () -> assertTrue(isValidWith(v -> v.sharkBirthEnergy = SHARK_BIRTH_ENERGY_MAX)),
                () -> assertTrue(isValidWith(v -> v.sharkEnergyLossPerStep = SHARK_ENERGY_LOSS_PER_STEP_MIN)),
                () -> assertTrue(isValidWith(v -> v.sharkEnergyLossPerStep = SHARK_ENERGY_LOSS_PER_STEP_MAX)),
                () -> assertTrue(isValidWith(v -> v.sharkEnergyGainPerFish = SHARK_ENERGY_GAIN_PER_FISH_MIN)),
                () -> assertTrue(isValidWith(v -> v.sharkEnergyGainPerFish = SHARK_ENERGY_GAIN_PER_FISH_MAX)),
                () -> assertTrue(isValidWith(v -> v.sharkMinReproductionAge = SHARK_MIN_REPRODUCTION_AGE_MIN)),
                () -> assertTrue(isValidWith(v -> v.sharkMinReproductionAge = SHARK_MIN_REPRODUCTION_AGE_MAX)),
                () -> assertTrue(isValidWith(v -> v.sharkMinReproductionEnergy = SHARK_MIN_REPRODUCTION_ENERGY_MIN)),
                () -> assertTrue(isValidWith(v -> v.sharkMinReproductionEnergy = SHARK_MIN_REPRODUCTION_ENERGY_MAX)),
                () -> assertTrue(isValidWith(v -> v.sharkMinReproductionInterval = SHARK_MIN_REPRODUCTION_INTERVAL_MIN)),
                () -> assertTrue(isValidWith(v -> v.sharkMinReproductionInterval = SHARK_MIN_REPRODUCTION_INTERVAL_MAX))
        );
    }

    @Test
    void testIsValidRejectsUnsupportedGridEdgeBehavior() {
        assertFalse(isValidWith(v -> v.gridEdgeBehavior = GridEdgeBehavior.ABSORB_XY));
    }

    @Test
    void testIsValidRejectsFishPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.fishPercent = Math.nextDown(FISH_PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> v.fishPercent = Math.nextUp(FISH_PERCENT_MAX)))
        );
    }

    @Test
    void testIsValidRejectsSharkPercentOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkPercent = Math.nextDown(SHARK_PERCENT_MIN))),
                () -> assertFalse(isValidWith(v -> v.sharkPercent = Math.nextUp(SHARK_PERCENT_MAX)))
        );
    }

    @Test
    void testIsValidRejectsFishMaxAgeOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.fishMaxAge = FISH_MAX_AGE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.fishMaxAge = FISH_MAX_AGE_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsFishMinReproductionAgeOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.fishMinReproductionAge = FISH_MIN_REPRODUCTION_AGE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.fishMinReproductionAge = FISH_MIN_REPRODUCTION_AGE_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsFishMinReproductionIntervalOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.fishMinReproductionInterval = FISH_MIN_REPRODUCTION_INTERVAL_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.fishMinReproductionInterval = FISH_MIN_REPRODUCTION_INTERVAL_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSharkMaxAgeOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkMaxAge = SHARK_MAX_AGE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sharkMaxAge = SHARK_MAX_AGE_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSharkBirthEnergyOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkBirthEnergy = SHARK_BIRTH_ENERGY_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sharkBirthEnergy = SHARK_BIRTH_ENERGY_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSharkEnergyLossPerStepOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkEnergyLossPerStep = SHARK_ENERGY_LOSS_PER_STEP_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sharkEnergyLossPerStep = SHARK_ENERGY_LOSS_PER_STEP_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSharkEnergyGainPerFishOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkEnergyGainPerFish = SHARK_ENERGY_GAIN_PER_FISH_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sharkEnergyGainPerFish = SHARK_ENERGY_GAIN_PER_FISH_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSharkMinReproductionAgeOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkMinReproductionAge = SHARK_MIN_REPRODUCTION_AGE_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sharkMinReproductionAge = SHARK_MIN_REPRODUCTION_AGE_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSharkMinReproductionEnergyOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkMinReproductionEnergy = SHARK_MIN_REPRODUCTION_ENERGY_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sharkMinReproductionEnergy = SHARK_MIN_REPRODUCTION_ENERGY_MAX + 1))
        );
    }

    @Test
    void testIsValidRejectsSharkMinReproductionIntervalOutsideRange() {
        assertAll(
                () -> assertFalse(isValidWith(v -> v.sharkMinReproductionInterval = SHARK_MIN_REPRODUCTION_INTERVAL_MIN - 1)),
                () -> assertFalse(isValidWith(v -> v.sharkMinReproductionInterval = SHARK_MIN_REPRODUCTION_INTERVAL_MAX + 1))
        );
    }

    @Test
    void testIsValidAcceptsPopulationShareSumAtOne() {
        assertTrue(isValidWith(v -> {
            v.fishPercent = 0.75d;
            v.sharkPercent = 0.25d;
        }));
    }

    @Test
    void testIsValidRejectsPopulationShareSumAboveOne() {
        assertFalse(isValidWith(v -> {
            v.fishPercent = 0.76d;
            v.sharkPercent = 0.25d;
        }));
    }

    private static final class ConfigValues {

        CellShape cellShape = CELL_SHAPE_DEFAULT;
        GridEdgeBehavior gridEdgeBehavior = GRID_EDGE_BEHAVIOR_DEFAULT;
        CellDisplayMode cellDisplayMode = CELL_DISPLAY_MODE_DEFAULT;
        double fishPercent = FISH_PERCENT_DEFAULT;
        double sharkPercent = SHARK_PERCENT_DEFAULT;
        NeighborhoodMode neighborhoodMode = NEIGHBORHOOD_MODE_DEFAULT;
        int fishMaxAge = FISH_MAX_AGE_DEFAULT;
        int fishMinReproductionAge = FISH_MIN_REPRODUCTION_AGE_DEFAULT;
        int fishMinReproductionInterval = FISH_MIN_REPRODUCTION_INTERVAL_DEFAULT;
        int sharkMaxAge = SHARK_MAX_AGE_DEFAULT;
        int sharkBirthEnergy = SHARK_BIRTH_ENERGY_DEFAULT;
        int sharkEnergyLossPerStep = SHARK_ENERGY_LOSS_PER_STEP_DEFAULT;
        int sharkEnergyGainPerFish = SHARK_ENERGY_GAIN_PER_FISH_DEFAULT;
        int sharkMinReproductionAge = SHARK_MIN_REPRODUCTION_AGE_DEFAULT;
        int sharkMinReproductionEnergy = SHARK_MIN_REPRODUCTION_ENERGY_DEFAULT;
        int sharkMinReproductionInterval = SHARK_MIN_REPRODUCTION_INTERVAL_DEFAULT;

        WatorConfig toConfig() {
            return new WatorConfig(cellShape, gridEdgeBehavior, GRID_WIDTH_DEFAULT, GRID_HEIGHT_DEFAULT,
                    CELL_EDGE_LENGTH_DEFAULT, cellDisplayMode, 1L, fishPercent, sharkPercent, neighborhoodMode,
                    fishMaxAge, fishMinReproductionAge, fishMinReproductionInterval, sharkMaxAge, sharkBirthEnergy,
                    sharkEnergyLossPerStep, sharkEnergyGainPerFish, sharkMinReproductionAge,
                    sharkMinReproductionEnergy, sharkMinReproductionInterval);
        }

    }

}
