package de.mkalb.etpetssim.simulations.etpets.model.entity;

public record EtpetsPetTraits(
        int maxEnergy,
        double movementCostModifier,
        int reproductionMinEnergy,
        int reproductionCooldownMax) {

    public static final int MAX_ENERGY_MIN = 60;
    public static final int MAX_ENERGY_MAX = 140;
    public static final double MOVEMENT_COST_MIN = 0.5d;
    public static final double MOVEMENT_COST_MAX = 1.5d;
    public static final int REPRO_ENERGY_MIN = 50;
    public static final int REPRO_ENERGY_MAX = 90;
    public static final int REPRO_COOLDOWN_MIN = 120;
    public static final int REPRO_COOLDOWN_MAX = 320;

    private static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    @SuppressWarnings("MagicNumber")
    public double genomeQualityScore() {
        double normMax = normalize(maxEnergy, MAX_ENERGY_MIN, MAX_ENERGY_MAX);
        double normCost = normalize(movementCostModifier, MOVEMENT_COST_MIN, MOVEMENT_COST_MAX);
        double normRepE = normalize(reproductionMinEnergy, REPRO_ENERGY_MIN, REPRO_ENERGY_MAX);
        double normRepC = normalize(reproductionCooldownMax, REPRO_COOLDOWN_MIN, REPRO_COOLDOWN_MAX);
        return (normMax + normCost + normRepE + normRepC) / 4.0d;
    }

}
