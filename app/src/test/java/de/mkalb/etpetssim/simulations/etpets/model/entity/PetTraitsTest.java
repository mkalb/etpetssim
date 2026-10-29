package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("MagicNumber")
final class PetTraitsTest {

    private static final double TOLERANCE = 1.0e-9d;

    private static PetTraits createMinTraits() {
        return new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN);
    }

    private static PetTraits createMaxTraits() {
        return new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MAX);
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
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MAX_ENERGY_MIN, minTraits.maxEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN,
                        minTraits.movementCostModifier(), TOLERANCE),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN,
                        minTraits.reproductionMinEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN,
                        minTraits.reproductionCooldown()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX, maxTraits.maxEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MAX,
                        maxTraits.movementCostModifier(), TOLERANCE),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX,
                        maxTraits.reproductionMinEnergy()),
                () -> assertEquals(EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MAX,
                        maxTraits.reproductionCooldown())
        );
    }

    @Test
    void testConstructorRejectsMaxEnergyOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MIN - 1,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX + 1,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT))
        );
    }

    @Test
    void testConstructorRejectsMovementCostModifierOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                        Math.nextDown(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN),
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                        Math.nextUp(EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MAX),
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                        Double.NaN,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT))
        );
    }

    @Test
    void testConstructorRejectsReproductionMinEnergyOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN - 1,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX + 1,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT))
        );
    }

    @Test
    void testConstructorRejectsReproductionCooldownOutsideRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN - 1)),
                () -> assertThrows(IllegalArgumentException.class, () -> createTraits(
                        EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                        EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                        EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MAX + 1))
        );
    }

    @Test
    void testGenomeQualityScore() {
        PetTraits mixedTraits = createTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN);

        assertAll(
                () -> assertEquals(0.0d, createMinTraits().genomeQualityScore(), TOLERANCE),
                () -> assertEquals(1.0d, createMaxTraits().genomeQualityScore(), TOLERANCE),
                () -> assertEquals(0.5d, mixedTraits.genomeQualityScore(), TOLERANCE)
        );
    }

}

