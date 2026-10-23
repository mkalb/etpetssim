package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class PetGenomeTest {

    private static final double TOLERANCE = 1.0e-9d;

    private static PetGenome createGenome(int maxEnergy,
                                          double movementCostModifier,
                                          int reproductionMinEnergy,
                                          int reproductionCooldown) {
        return new PetGenome(new PetTraits(maxEnergy, movementCostModifier, reproductionMinEnergy,
                reproductionCooldown));
    }

    private static void assertTraits(PetTraits expected, PetTraits actual) {
        assertAll(
                () -> assertEquals(expected.maxEnergy(), actual.maxEnergy()),
                () -> assertEquals(expected.movementCostModifier(), actual.movementCostModifier(), TOLERANCE),
                () -> assertEquals(expected.reproductionMinEnergy(), actual.reproductionMinEnergy()),
                () -> assertEquals(expected.reproductionCooldown(), actual.reproductionCooldown())
        );
    }

    @Test
    void testFromParents() {
        PetGenome parentA = createGenome(80, 0.6d, 40, 120);
        PetGenome parentB = createGenome(91, 1.0d, 51, 181);

        PetGenome child = PetGenome.fromParents(parentA, parentB, new StubRandom(0.0d, true), 0.0d, 0.1d);

        assertTraits(new PetTraits(86, 0.8d, 46, 151), child.traits());
    }

    @Test
    void testFromParentsAppliesMutation() {
        PetGenome parentA = createGenome(100, 1.0d, 50, 200);
        PetGenome parentB = createGenome(100, 1.0d, 50, 200);

        PetGenome increased = PetGenome.fromParents(parentA, parentB, new StubRandom(0.0d, true), 1.0d, 0.1d);
        PetGenome decreased = PetGenome.fromParents(parentA, parentB, new StubRandom(0.0d, false), 1.0d, 0.1d);

        assertAll(
                () -> assertTraits(new PetTraits(110, 1.1d, 55, 220), increased.traits()),
                () -> assertTraits(new PetTraits(90, 0.9d, 45, 180), decreased.traits())
        );
    }

    @Test
    void testFromParentsClampsToTraitRanges() {
        PetGenome maxParent = createGenome(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX);
        PetGenome minParent = createGenome(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN);

        PetGenome aboveMax = PetGenome.fromParents(maxParent, maxParent, new StubRandom(0.0d, true), 1.0d, 0.5d);
        PetGenome belowMin = PetGenome.fromParents(minParent, minParent, new StubRandom(0.0d, false), 1.0d, 0.5d);

        assertAll(
                () -> assertTraits(maxParent.traits(), aboveMax.traits()),
                () -> assertTraits(minParent.traits(), belowMin.traits())
        );
    }

    private static final class StubRandom extends Random {

        private final double nextDoubleValue;
        private final boolean nextBooleanValue;

        private StubRandom(double nextDoubleValue, boolean nextBooleanValue) {
            this.nextDoubleValue = nextDoubleValue;
            this.nextBooleanValue = nextBooleanValue;
        }

        @Override
        public double nextDouble() {
            return nextDoubleValue;
        }

        @Override
        public boolean nextBoolean() {
            return nextBooleanValue;
        }

    }

}

