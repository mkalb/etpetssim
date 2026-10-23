package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;

/**
 * Immutable trait bundle describing a pet genome.
 *
 * <p>Each trait lies within its {@code PET_TRAITS_*_RANGE_MIN} and {@code PET_TRAITS_*_RANGE_MAX} bounds in
 * {@link EtpetsBalance}.
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

    /**
     * Creates a trait bundle.
     *
     * @throws IllegalArgumentException if a trait lies outside its range in {@link EtpetsBalance}
     */
    public PetTraits {
        if ((maxEnergy < EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN)
                || (maxEnergy > EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX)) {
            throw new IllegalArgumentException("maxEnergy out of range: " + maxEnergy);
        }
        if (!(movementCostModifier >= EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN)
                || !(movementCostModifier <= EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX)) {
            throw new IllegalArgumentException("movementCostModifier out of range: " + movementCostModifier);
        }
        if ((reproductionMinEnergy < EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN)
                || (reproductionMinEnergy > EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX)) {
            throw new IllegalArgumentException("reproductionMinEnergy out of range: " + reproductionMinEnergy);
        }
        if ((reproductionCooldown < EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN)
                || (reproductionCooldown > EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX)) {
            throw new IllegalArgumentException("reproductionCooldown out of range: " + reproductionCooldown);
        }
    }

    private static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    /**
     * Computes a normalized aggregate quality score across all trait dimensions.
     *
     * @return the average normalized trait score, from {@code 0.0} (all traits at their minimum) to {@code 1.0}
     * (all traits at their maximum)
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
