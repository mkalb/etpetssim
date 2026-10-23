package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class PetTraitsTest {

    private static final double TOLERANCE = 1.0e-9d;

    private static PetTraits createMinTraits() {
        return new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN);
    }

    private static PetTraits createMaxTraits() {
        return new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX);
    }

    private static PetTraits createTraits(int maxEnergy,
                                          double movementCostModifier,
                                          int reproductionMinEnergy,
                                          int reproductionCooldown) {
        return new PetTraits(maxEnergy, movementCostModifier, reproductionMinEnergy, reproductionCooldown);
    }

    @Test
    void testRecord() {
        PetTraits minTraits = createMinTraits();
        PetTraits maxTraits = createMaxTraits();

        assertAll(
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN, minTraits.maxEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN,
                        minTraits.movementCostModifier(), TOLERANCE),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN,
                        minTraits.reproductionMinEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN,
                        minTraits.reproductionCooldown()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX, maxTraits.maxEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX,
                        maxTraits.movementCostModifier(), TOLERANCE),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX,
                        maxTraits.reproductionMinEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX,
                        maxTraits.reproductionCooldown())
        );
    }

    @Test
    void testConstructorRejectsMaxEnergyOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN - 1,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX + 1,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT))
        );
    }

    @Test
    void testConstructorRejectsMovementCostModifierOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                        Math.nextDown(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN),
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                        Math.nextUp(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX),
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                        Double.NaN,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT))
        );
    }

    @Test
    void testConstructorRejectsReproductionMinEnergyOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN - 1,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX + 1,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT))
        );
    }

    @Test
    void testConstructorRejectsReproductionCooldownOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN - 1)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX + 1))
        );
    }

    @Test
    void testGenomeQualityScore() {
        PetTraits mixedTraits = createTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN);

        assertAll(
                () -> assertEquals(0.0d, createMinTraits().genomeQualityScore(), TOLERANCE),
                () -> assertEquals(1.0d, createMaxTraits().genomeQualityScore(), TOLERANCE),
                () -> assertEquals(0.5d, mixedTraits.genomeQualityScore(), TOLERANCE)
        );
    }

}

