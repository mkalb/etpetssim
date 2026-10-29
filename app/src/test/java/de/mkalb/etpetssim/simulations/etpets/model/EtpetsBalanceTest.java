package de.mkalb.etpetssim.simulations.etpets.model;

import org.junit.jupiter.api.Test;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.INSECT_CURRENT_AMOUNT_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.INSECT_CURRENT_AMOUNT_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.INSECT_MAX_AMOUNT_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.INSECT_MAX_AMOUNT_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.INSECT_REGENERATION_PER_STEP_BASE;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.INSECT_REGENERATION_PER_STEP_DELTA;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_CURRENT_ENERGY_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_CURRENT_ENERGY_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_EAT_SCORE_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_EAT_SCORE_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_EGG_INCUBATION_REMAINING_DEFAULT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_INITIAL_AGE_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_INITIAL_AGE_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_INITIAL_CURRENT_ENERGY_REDUCTION_DELTA;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_MOVE_SCORE_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_MOVE_SCORE_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_REPRODUCTION_SCORE_AVERAGE_QUALITY_WEIGHT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_REPRODUCTION_SCORE_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_REPRODUCTION_SCORE_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_REPRODUCTION_SCORE_MINIMUM_QUALITY_WEIGHT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_MAX_ENERGY_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PLANT_CURRENT_AMOUNT_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PLANT_CURRENT_AMOUNT_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PLANT_MAX_AMOUNT_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PLANT_MAX_AMOUNT_MIN;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PLANT_REGENERATION_PER_STEP_BASE;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.PLANT_REGENERATION_PER_STEP_DELTA;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.TRAIL_INTENSITY_DEFAULT;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.TRAIL_INTENSITY_MAX;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.TRAIL_INTENSITY_MIN;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EtpetsBalanceTest {

    private static final double DELTA = 1.0e-9d;

    private static void assertOrdered(String name, double min, double max) {
        assertTrue(min < max, name);
    }

    private static void assertWithin(String name, double value, double min, double max) {
        assertTrue((value >= min) && (value <= max), name);
    }

    @Test
    void testRangesAreOrdered() {
        assertAll(
                () -> assertOrdered("TRAIL_INTENSITY", TRAIL_INTENSITY_MIN, TRAIL_INTENSITY_MAX),
                () -> assertOrdered("PLANT_MAX_AMOUNT", PLANT_MAX_AMOUNT_MIN, PLANT_MAX_AMOUNT_MAX),
                () -> assertOrdered("PLANT_CURRENT_AMOUNT", PLANT_CURRENT_AMOUNT_MIN, PLANT_CURRENT_AMOUNT_MAX),
                () -> assertOrdered("INSECT_MAX_AMOUNT", INSECT_MAX_AMOUNT_MIN, INSECT_MAX_AMOUNT_MAX),
                () -> assertOrdered("INSECT_CURRENT_AMOUNT", INSECT_CURRENT_AMOUNT_MIN, INSECT_CURRENT_AMOUNT_MAX),
                () -> assertOrdered("PET_EGG_INCUBATION_REMAINING", PET_EGG_INCUBATION_REMAINING_MIN,
                        PET_EGG_INCUBATION_REMAINING_MAX),
                () -> assertOrdered("PET_TRAITS_MAX_ENERGY", PET_TRAITS_MAX_ENERGY_MIN, PET_TRAITS_MAX_ENERGY_MAX),
                () -> assertOrdered("PET_TRAITS_MOVEMENT_COST_MODIFIER", PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN,
                        PET_TRAITS_MOVEMENT_COST_MODIFIER_MAX),
                () -> assertOrdered("PET_TRAITS_REPRODUCTION_MIN_ENERGY", PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN,
                        PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX),
                () -> assertOrdered("PET_TRAITS_REPRODUCTION_COOLDOWN", PET_TRAITS_REPRODUCTION_COOLDOWN_MIN,
                        PET_TRAITS_REPRODUCTION_COOLDOWN_MAX),
                () -> assertOrdered("PET_CURRENT_ENERGY", PET_CURRENT_ENERGY_MIN, PET_CURRENT_ENERGY_MAX),
                () -> assertOrdered("PET_INITIAL_AGE", PET_INITIAL_AGE_MIN, PET_INITIAL_AGE_MAX),
                () -> assertOrdered("PET_MOVE_SCORE", PET_MOVE_SCORE_MIN, PET_MOVE_SCORE_MAX),
                () -> assertOrdered("PET_REPRODUCTION_SCORE", PET_REPRODUCTION_SCORE_MIN, PET_REPRODUCTION_SCORE_MAX),
                () -> assertOrdered("PET_EAT_SCORE", PET_EAT_SCORE_MIN, PET_EAT_SCORE_MAX)
        );
    }

    @Test
    void testDefaultsAreWithinRanges() {
        assertAll(
                () -> assertWithin("TRAIL_INTENSITY", TRAIL_INTENSITY_DEFAULT, TRAIL_INTENSITY_MIN,
                        TRAIL_INTENSITY_MAX),
                () -> assertWithin("PET_EGG_INCUBATION_REMAINING", PET_EGG_INCUBATION_REMAINING_DEFAULT,
                        PET_EGG_INCUBATION_REMAINING_MIN, PET_EGG_INCUBATION_REMAINING_MAX),
                () -> assertWithin("PET_TRAITS_MOVEMENT_COST_MODIFIER", PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN, PET_TRAITS_MOVEMENT_COST_MODIFIER_MAX),
                () -> assertWithin("PET_TRAITS_REPRODUCTION_MIN_ENERGY", PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN, PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX),
                () -> assertWithin("PET_TRAITS_REPRODUCTION_COOLDOWN", PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT,
                        PET_TRAITS_REPRODUCTION_COOLDOWN_MIN, PET_TRAITS_REPRODUCTION_COOLDOWN_MAX)
        );
    }

    @Test
    void testRegenerationDeltasArePositiveAndBelowBase() {
        assertAll(
                () -> assertOrdered("PLANT_REGENERATION_PER_STEP", 0.0d, PLANT_REGENERATION_PER_STEP_DELTA),
                () -> assertOrdered("PLANT_REGENERATION_PER_STEP", PLANT_REGENERATION_PER_STEP_DELTA,
                        PLANT_REGENERATION_PER_STEP_BASE),
                () -> assertOrdered("INSECT_REGENERATION_PER_STEP", 0.0d, INSECT_REGENERATION_PER_STEP_DELTA),
                () -> assertOrdered("INSECT_REGENERATION_PER_STEP", INSECT_REGENERATION_PER_STEP_DELTA,
                        INSECT_REGENERATION_PER_STEP_BASE)
        );
    }

    @Test
    void testInitialCurrentEnergyReductionKeepsMinimumEnergy() {
        assertAll(
                () -> assertTrue(PET_INITIAL_CURRENT_ENERGY_REDUCTION_DELTA >= 0),
                () -> assertTrue((PET_TRAITS_MAX_ENERGY_MIN - PET_INITIAL_CURRENT_ENERGY_REDUCTION_DELTA)
                        >= PET_CURRENT_ENERGY_MIN)
        );
    }

    @Test
    void testReproductionQualityWeightsSumToOne() {
        assertEquals(1.0d,
                PET_REPRODUCTION_SCORE_AVERAGE_QUALITY_WEIGHT + PET_REPRODUCTION_SCORE_MINIMUM_QUALITY_WEIGHT,
                DELTA);
    }

}
