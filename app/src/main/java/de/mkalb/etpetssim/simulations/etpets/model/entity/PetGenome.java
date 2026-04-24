package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

import java.util.*;

public record PetGenome(PetTraits traits) {

    @SuppressWarnings("MagicNumber")
    public static PetGenome fromParents(PetGenome parentA, PetGenome parentB,
                                        Random random,
                                        double mutationChancePerTrait, double mutationDelta) {
        PetTraits ta = parentA.traits();
        PetTraits tb = parentB.traits();

        double avgMaxEnergy = (ta.maxEnergy() + tb.maxEnergy()) / 2.0d;
        int maxEnergy = clampInt(
                mutate(avgMaxEnergy, random, mutationChancePerTrait, mutationDelta),
                EtpetsBalance.PET_MAX_ENERGY_MIN, EtpetsBalance.PET_MAX_ENERGY_MAX);

        double avgMovCost = (ta.movementCostModifier() + tb.movementCostModifier()) / 2.0d;
        double movCost = clampDouble(
                mutate(avgMovCost, random, mutationChancePerTrait, mutationDelta),
                EtpetsBalance.PET_MOVEMENT_COST_MODIFIER_MIN,
                EtpetsBalance.PET_MOVEMENT_COST_MODIFIER_MAX);

        double avgReproEnergy = (ta.reproductionMinEnergy() + tb.reproductionMinEnergy()) / 2.0d;
        int reproEnergy = clampInt(
                mutate(avgReproEnergy, random, mutationChancePerTrait, mutationDelta),
                EtpetsBalance.PET_REPRODUCTION_MIN_ENERGY_MIN,
                EtpetsBalance.PET_REPRODUCTION_MIN_ENERGY_MAX);

        double avgReproCooldown = (ta.reproductionCooldown() + tb.reproductionCooldown()) / 2.0d;
        int reproCooldown = clampInt(
                mutate(avgReproCooldown, random, mutationChancePerTrait, mutationDelta),
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_MIN,
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_MAX);

        return new PetGenome(new PetTraits(maxEnergy, movCost, reproEnergy, reproCooldown));
    }

    private static double mutate(double avg, Random random, double chance, double delta) {
        if (random.nextDouble() < chance) {
            double sign = random.nextBoolean() ? 1.0d : -1.0d;
            return avg + (sign * delta * avg);
        }
        return avg;
    }

    private static int clampInt(double value, int min, int max) {
        return (int) Math.max(min, Math.min(max, Math.round(value)));
    }

    private static double clampDouble(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

}
