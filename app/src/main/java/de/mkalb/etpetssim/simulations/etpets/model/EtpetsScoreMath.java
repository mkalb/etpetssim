package de.mkalb.etpetssim.simulations.etpets.model;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance.*;

final class EtpetsScoreMath {

    private EtpetsScoreMath() {
    }

    static double computeRawMoveScore(double energyRatio,
                                      double movementCostModifier,
                                      boolean hasResourceLookAhead,
                                      boolean hasPartnerLookAhead,
                                      boolean hasLowMobilityPenalty,
                                      boolean hasCrowdingPenalty,
                                      boolean isGroundWithoutTrail,
                                      int trailIntensity,
                                      boolean hasOscillationHistoryMatch) {
        double survivalPressure = computeSurvivalPressure(energyRatio);

        double resourceBonus = 0.0d;
        if (hasResourceLookAhead) {
            resourceBonus = PET_MOVE_RESOURCE_BASE_WEIGHT + (PET_MOVE_RESOURCE_SURVIVAL_PRESSURE_WEIGHT * survivalPressure);
        }

        double partnerBonus = hasPartnerLookAhead ? PET_MOVE_PARTNER_WEIGHT : 0.0d;

        double positiveScoreTerms =
                resourceBonus
                        + partnerBonus
                        + computeTrailBonus(trailIntensity)
                        + computeExplorationBonus(energyRatio, movementCostModifier, isGroundWithoutTrail);
        double negativeScoreTerms =
                computeOscillationPenalty(hasOscillationHistoryMatch)
                        + computeLowMobilityPenalty(hasLowMobilityPenalty, survivalPressure, hasResourceLookAhead)
                        + computeCrowdingPenalty(hasCrowdingPenalty, hasPartnerLookAhead);
        return (PET_MOVE_SCORE_BASE + positiveScoreTerms) - negativeScoreTerms;
    }

    private static double computeSurvivalPressure(double energyRatio) {
        double hunger = clampToUnitRange(1.0d - energyRatio);
        return Math.pow(hunger, PET_MOVE_SURVIVAL_PRESSURE_EXPONENT);
    }

    private static double computeTrailBonus(int trailIntensity) {
        if (trailIntensity < PET_MOVE_TRAIL_BONUS_INTENSITY_THRESHOLD) {
            return 0.0d;
        }
        int trailIntensityAboveThreshold = trailIntensity - PET_MOVE_TRAIL_BONUS_INTENSITY_THRESHOLD;
        double normalizedTrailIntensity = 1.0d - Math.exp(-(trailIntensityAboveThreshold / PET_MOVE_TRAIL_BONUS_INTENSITY_SCALE));
        double trailBonusCurveNumerator = 1.0d - Math.exp(-PET_MOVE_TRAIL_BONUS_CURVE_SHARPNESS * normalizedTrailIntensity);
        double trailBonusCurveDenominator = 1.0d - Math.exp(-PET_MOVE_TRAIL_BONUS_CURVE_SHARPNESS);
        return PET_MOVE_TRAIL_BONUS_MAX * (trailBonusCurveNumerator / trailBonusCurveDenominator);
    }

    private static double computeExplorationBonus(double energyRatio, double movementCostModifier, boolean isGroundWithoutTrail) {
        if (!isGroundWithoutTrail) {
            return 0.0d;
        }
        double movementCostModifierSpan = PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX
                - PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN;
        double movementCostModifierNormalized = clampToUnitRange((movementCostModifier - PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN) / movementCostModifierSpan);
        double movementEfficiency = clampToUnitRange(1.0d - movementCostModifierNormalized);
        double explorationDrive = Math.pow(energyRatio, PET_MOVE_EXPLORATION_ENERGY_EXPONENT)
                * Math.pow(movementEfficiency, PET_MOVE_EXPLORATION_COST_EXPONENT);
        return PET_MOVE_EXPLORATION_WEIGHT * explorationDrive;
    }

    private static double computeOscillationPenalty(boolean hasOscillationHistoryMatch) {
        return hasOscillationHistoryMatch ? PET_MOVE_OSCILLATION_HISTORY_MATCH_PENALTY : 0.0d;
    }

    private static double computeLowMobilityPenalty(boolean hasLowMobilityPenalty, double survivalPressure, boolean hasResourceLookAhead) {
        if (!hasLowMobilityPenalty) {
            return 0.0d;
        }
        // No penalty if high survival pressure AND resource nearby.
        boolean skipPenalty = (survivalPressure >= PET_MOVE_SURVIVAL_PRESSURE_HIGH_THRESHOLD)
                && hasResourceLookAhead;
        return skipPenalty ? 0.0d : PET_MOVE_LOW_MOBILITY_PENALTY;
    }

    private static double computeCrowdingPenalty(boolean hasCrowdingPenalty, boolean hasPartnerLookAhead) {
        // No penalty if reproduction bonus available.
        return (hasCrowdingPenalty && !hasPartnerLookAhead) ? PET_MOVE_CROWDING_PENALTY : 0.0d;
    }

    @SuppressWarnings("MagicNumber")
    static double computeRawReproduceScore(double petQualityScore,
                                           double partnerQualityScore) {
        double normalizedPetQuality = clampToUnitRange(petQualityScore);
        double normalizedPartnerQuality = clampToUnitRange(partnerQualityScore);
        double averageQuality = (normalizedPetQuality + normalizedPartnerQuality) / 2.0d;
        double minimumQuality = Math.min(normalizedPetQuality, normalizedPartnerQuality);
        double weightedQualityScore = (PET_REPRODUCTION_SCORE_AVERAGE_QUALITY_WEIGHT * averageQuality)
                + (PET_REPRODUCTION_SCORE_MINIMUM_QUALITY_WEIGHT * minimumQuality);
        double normalizedWeightedQuality = clampToUnitRange(weightedQualityScore);

        return PET_REPRODUCTION_SCORE_RANGE_MIN
                + (normalizedWeightedQuality * (PET_REPRODUCTION_SCORE_RANGE_MAX - PET_REPRODUCTION_SCORE_RANGE_MIN));
    }

    static double computeRawEatScore(int currentEnergy,
                                     int maxEnergy,
                                     int resourceEnergyGain,
                                     int age) {
        double positiveScoreTerms =
                computeHungerScore(currentEnergy, maxEnergy)
                        + computePanicScore(currentEnergy)
                        + computeResourceGainScore(resourceEnergyGain)
                        + computeAgeBonus(age);
        double negativeScoreTerms = computeWastePenalty(currentEnergy, maxEnergy, resourceEnergyGain);
        return positiveScoreTerms - negativeScoreTerms;
    }

    private static double computeHungerScore(int currentEnergy, int maxEnergy) {
        // Relative hunger term: lower fill ratio increases eat pressure.
        double energyFillRatio = (double) currentEnergy / maxEnergy;
        double hunger = clampToUnitRange(1.0d - energyFillRatio);
        return PET_EAT_SCORE_HUNGER_WEIGHT
                * Math.pow(hunger, PET_EAT_SCORE_HUNGER_EXPONENT);
    }

    private static double computePanicScore(int currentEnergy) {
        // Panic term based on absolute energy threshold.
        double panicPressureNormalized = clampToUnitRange(
                (PET_EAT_SCORE_PANIC_ENERGY_THRESHOLD - currentEnergy)
                        / PET_EAT_SCORE_PANIC_ENERGY_THRESHOLD);
        return PET_EAT_SCORE_PANIC_WEIGHT
                * Math.pow(panicPressureNormalized, PET_EAT_SCORE_PANIC_EXPONENT);
    }

    private static double computeResourceGainScore(int resourceEnergyGain) {
        // Resource gain term normalized against INSECT_ENERGY_GAIN_PER_ACT as upper reference.
        double resourceGainNormalized = clampToUnitRange((double) resourceEnergyGain / INSECT_ENERGY_GAIN_PER_ACT);
        return PET_EAT_SCORE_RESOURCE_GAIN_WEIGHT
                * Math.pow(resourceGainNormalized, PET_EAT_SCORE_RESOURCE_GAIN_EXPONENT);
    }

    private static double computeWastePenalty(int currentEnergy, int maxEnergy, int resourceEnergyGain) {
        // Strong waste damping: overfilling available capacity is heavily penalized.
        int remainingEnergyCapacity = maxEnergy - currentEnergy;
        int overfillEnergy = Math.max(0, resourceEnergyGain - remainingEnergyCapacity);
        // Defensive guard for invalid/degenerate resource values.
        double overfillRatio = (resourceEnergyGain > 0)
                ? ((double) overfillEnergy / resourceEnergyGain)
                : 0.0d;
        return PET_EAT_SCORE_OVERFILL_PENALTY_WEIGHT
                * Math.pow(overfillRatio, PET_EAT_SCORE_OVERFILL_PENALTY_EXPONENT);
    }

    private static double computeAgeBonus(int age) {
        // Small age bonus that decays quickly.
        return PET_EAT_SCORE_AGE_WEIGHT
                * Math.exp(-(double) age / PET_EAT_SCORE_AGE_DECAY_STEPS);
    }

    private static double clampToUnitRange(double value) {
        return Math.clamp(value, 0.0d, 1.0d);
    }

}
