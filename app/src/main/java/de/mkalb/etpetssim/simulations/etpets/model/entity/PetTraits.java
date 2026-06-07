package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

/**
 * Immutable trait bundle describing a pet genome.
 *
 * @param maxEnergy             the maximum energy capacity
 * @param movementCostModifier  the movement energy cost modifier
 * @param reproductionMinEnergy the minimum energy required for reproduction
 * @param reproductionCooldown  the cooldown between reproduction attempts
 */
public record PetTraits(
        int maxEnergy,
        double movementCostModifier,
        int reproductionMinEnergy,
        int reproductionCooldown) {

    private static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    /**
     * Computes a normalized aggregate quality score across all trait dimensions.
     *
     * @return the average normalized trait score
     */
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
