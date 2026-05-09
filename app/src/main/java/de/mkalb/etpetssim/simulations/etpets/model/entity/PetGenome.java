package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

import java.util.*;

/**
 * Immutable genome wrapper for ET Pets.
 *
 * @param traits the encoded pet traits used for gameplay behavior
 */
public record PetGenome(PetTraits traits) {

    /**
     * Creates a child genome by averaging the parent traits and applying optional mutation.
     *
     * @param parentA the first parent genome
     * @param parentB the second parent genome
     * @param random the random source used for mutation
     * @param mutationChancePerTrait the mutation probability per trait
     * @param mutationDelta the relative mutation strength
     * @return the derived child genome
     */
    @SuppressWarnings("MagicNumber")
    public static PetGenome fromParents(PetGenome parentA, PetGenome parentB,
                                        Random random,
                                        double mutationChancePerTrait, double mutationDelta) {
        PetTraits ta = parentA.traits();
        PetTraits tb = parentB.traits();

        double avgMaxEnergy = (ta.maxEnergy() + tb.maxEnergy()) / 2.0d;
        int maxEnergy = Math.clamp(
                (int) Math.round(mutate(avgMaxEnergy, random, mutationChancePerTrait, mutationDelta)),
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX);

        double avgMovCost = (ta.movementCostModifier() + tb.movementCostModifier()) / 2.0d;
        double movCost = Math.clamp(
                mutate(avgMovCost, random, mutationChancePerTrait, mutationDelta),
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX);

        double avgReproEnergy = (ta.reproductionMinEnergy() + tb.reproductionMinEnergy()) / 2.0d;
        int reproEnergy = Math.clamp(
                (int) Math.round(mutate(avgReproEnergy, random, mutationChancePerTrait, mutationDelta)),
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX);

        double avgReproCooldown = (ta.reproductionCooldown() + tb.reproductionCooldown()) / 2.0d;
        int reproCooldown = Math.clamp(
                (int) Math.round(mutate(avgReproCooldown, random, mutationChancePerTrait, mutationDelta)),
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX);

        return new PetGenome(new PetTraits(maxEnergy, movCost, reproEnergy, reproCooldown));
    }

    private static double mutate(double avg, Random random, double chance, double delta) {
        if (random.nextDouble() < chance) {
            double sign = random.nextBoolean() ? 1.0d : -1.0d;
            return avg + (sign * delta * avg);
        }
        return avg;
    }

}
