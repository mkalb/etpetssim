package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

public record PetTraits(
        int maxEnergy,
        double movementCostModifier,
        int reproductionMinEnergy,
        int reproductionCooldown) {

    private static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    @SuppressWarnings("MagicNumber")
    public double genomeQualityScore() {
        double normMax = normalize(maxEnergy,
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX);
        double normCost = normalize(movementCostModifier,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX);
        double normRepE = normalize(reproductionMinEnergy,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX);
        double normRepC = normalize(reproductionCooldown,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX);
        return (normMax + normCost + normRepE + normRepC) / 4.0d;
    }

}
