package de.mkalb.etpetssim.simulations.etpets.model.entity;

import java.util.*;

public record EtpetsPetGenome(EtpetsPetTraits traits) {

    @SuppressWarnings("MagicNumber")
    public static EtpetsPetGenome fromParents(EtpetsPetGenome parentA, EtpetsPetGenome parentB,
                                              Random random,
                                              double mutationChancePerTrait, double mutationDelta) {
        EtpetsPetTraits ta = parentA.traits();
        EtpetsPetTraits tb = parentB.traits();

        double avgMaxEnergy = (ta.maxEnergy() + tb.maxEnergy()) / 2.0d;
        int maxEnergy = clampInt(
                mutate(avgMaxEnergy, random, mutationChancePerTrait, mutationDelta),
                EtpetsPetTraits.MAX_ENERGY_MIN, EtpetsPetTraits.MAX_ENERGY_MAX);

        double avgMovCost = (ta.movementCostModifier() + tb.movementCostModifier()) / 2.0d;
        double movCost = clampDouble(
                mutate(avgMovCost, random, mutationChancePerTrait, mutationDelta),
                EtpetsPetTraits.MOVEMENT_COST_MIN, EtpetsPetTraits.MOVEMENT_COST_MAX);

        double avgReproEnergy = (ta.reproductionMinEnergy() + tb.reproductionMinEnergy()) / 2.0d;
        int reproEnergy = clampInt(
                mutate(avgReproEnergy, random, mutationChancePerTrait, mutationDelta),
                EtpetsPetTraits.REPRO_ENERGY_MIN, EtpetsPetTraits.REPRO_ENERGY_MAX);

        double avgReproCooldown = (ta.reproductionCooldownMax() + tb.reproductionCooldownMax()) / 2.0d;
        int reproCooldown = clampInt(
                mutate(avgReproCooldown, random, mutationChancePerTrait, mutationDelta),
                EtpetsPetTraits.REPRO_COOLDOWN_MIN, EtpetsPetTraits.REPRO_COOLDOWN_MAX);

        return new EtpetsPetGenome(new EtpetsPetTraits(maxEnergy, movCost, reproEnergy, reproCooldown));
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
