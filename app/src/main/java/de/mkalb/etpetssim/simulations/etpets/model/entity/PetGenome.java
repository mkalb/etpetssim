package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

import java.util.Random;

/**
 * Immutable genome wrapper for ET Pets.
 *
 * @param traits the encoded pet traits used for gameplay behavior
 */
public record PetGenome(PetTraits traits) {

    /**
     * Creates a child genome by averaging the parent traits and applying optional mutation.
     *
     * <p>Each trait mutates with {@code mutationChancePerTrait} by {@code mutationDelta} times its average, in a random
     * direction; the result is rounded for integer traits and clamped to the trait ranges in {@link EtpetsBalance}.
     *
     * @param parentA                the first parent genome
     * @param parentB                the second parent genome
     * @param random                 the random source used for mutation
     * @param mutationChancePerTrait the mutation probability per trait
     * @param mutationDelta          the relative mutation strength
     * @return the derived child genome
     */
    public static PetGenome fromParents(PetGenome parentA,
                                        PetGenome parentB,
                                        Random random,
                                        double mutationChancePerTrait,
                                        double mutationDelta) {
        PetTraits traitsA = parentA.traits();
        PetTraits traitsB = parentB.traits();

        double averageMaxEnergy = (traitsA.maxEnergy() + traitsB.maxEnergy()) / 2.0d;
        int maxEnergy = Math.clamp(
                Math.round(mutate(averageMaxEnergy, random, mutationChancePerTrait, mutationDelta)),
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX);

        double averageMovementCostModifier = (traitsA.movementCostModifier() + traitsB.movementCostModifier()) / 2.0d;
        double movementCostModifier = Math.clamp(
                mutate(averageMovementCostModifier, random, mutationChancePerTrait, mutationDelta),
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MAX);

        double averageReproductionMinEnergy = (traitsA.reproductionMinEnergy() + traitsB.reproductionMinEnergy())
                / 2.0d;
        int reproductionMinEnergy = Math.clamp(
                Math.round(mutate(averageReproductionMinEnergy, random, mutationChancePerTrait, mutationDelta)),
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX);

        double averageReproductionCooldown = (traitsA.reproductionCooldown() + traitsB.reproductionCooldown()) / 2.0d;
        int reproductionCooldown = Math.clamp(
                Math.round(mutate(averageReproductionCooldown, random, mutationChancePerTrait, mutationDelta)),
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MAX);

        return new PetGenome(new PetTraits(maxEnergy, movementCostModifier, reproductionMinEnergy,
                reproductionCooldown));
    }

    private static double mutate(double averageValue, Random random, double mutationChance, double mutationDelta) {
        if (random.nextDouble() < mutationChance) {
            double sign = random.nextBoolean() ? 1.0d : -1.0d;
            return averageValue + (sign * mutationDelta * averageValue);
        }
        return averageValue;
    }

}
