package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.core.AppLogger;

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
                                      boolean isPreviousCoordinate,
                                      boolean isPreviousPreviousCoordinate) {
        double hunger = clampToUnitRange(1.0d - energyRatio);
        double survivalPressure = Math.pow(hunger, EtpetsBalance.PET_MOVE_SURVIVAL_PRESSURE_EXPONENT);

        double resourceBonus = 0.0d;
        if (hasResourceLookAhead) {
            resourceBonus = EtpetsBalance.PET_MOVE_RESOURCE_WEIGHT_BASE
                    + (EtpetsBalance.PET_MOVE_RESOURCE_WEIGHT_SURVIVAL * survivalPressure);
        }

        double partnerBonus = hasPartnerLookAhead
                ? EtpetsBalance.PET_MOVE_PARTNER_WEIGHT
                : 0.0d;

        double trailBonus = 0.0d;
        if (trailIntensity >= EtpetsBalance.PET_MOVE_TRAIL_BONUS_START_INTENSITY) {
            int effectiveTrailIntensity = trailIntensity - EtpetsBalance.PET_MOVE_TRAIL_BONUS_START_INTENSITY;
            double scale = EtpetsBalance.PET_MOVE_TRAIL_BONUS_INTENSITY_SCALE;
            if (scale > 0.0d) {
                double normalizedTrailIntensity = 1.0d - Math.exp(-(effectiveTrailIntensity / scale));
                double curveNumerator = 1.0d - Math.exp(-EtpetsBalance.PET_MOVE_TRAIL_BONUS_CURVE_K * normalizedTrailIntensity);
                double curveDenominator = 1.0d - Math.exp(-EtpetsBalance.PET_MOVE_TRAIL_BONUS_CURVE_K);
                if (curveDenominator > 0.0d) {
                    trailBonus = EtpetsBalance.PET_MOVE_TRAIL_BONUS_MAX * (curveNumerator / curveDenominator);
                }
            }
        }

        double moveCostSpan = EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX
                - EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN;
        double moveCostNormalized = (moveCostSpan > 0.0d)
                ? clampToUnitRange((movementCostModifier - EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN) / moveCostSpan)
                : 0.0d;
        double moveCostEfficiency = clampToUnitRange(1.0d - moveCostNormalized);
        double explorationDrive = Math.pow(energyRatio, EtpetsBalance.PET_MOVE_EXPLORATION_ENERGY_EXPONENT)
                * Math.pow(moveCostEfficiency, EtpetsBalance.PET_MOVE_EXPLORATION_COST_EXPONENT);
        double explorationBonus = isGroundWithoutTrail
                ? (EtpetsBalance.PET_MOVE_EXPLORATION_WEIGHT * explorationDrive)
                : 0.0d;

        double oscillationPenalty = 0.0d;
        if (isPreviousCoordinate) {
            oscillationPenalty += EtpetsBalance.PET_MOVE_OSCILLATION_PREVIOUS_PENALTY;
        }
        if (isPreviousPreviousCoordinate) {
            oscillationPenalty += EtpetsBalance.PET_MOVE_OSCILLATION_PREVIOUS_PREVIOUS_PENALTY;
        }

        // Low mobility penalty: no penalty if high survival pressure AND resource nearby.
        double lowMobilityPenalty = 0.0d;
        if (hasLowMobilityPenalty) {
            boolean skipLowMobilityPenalty = (survivalPressure >= EtpetsBalance.PET_MOVE_SURVIVAL_PRESSURE_HIGH_THRESHOLD)
                    && hasResourceLookAhead;
            if (!skipLowMobilityPenalty) {
                lowMobilityPenalty = EtpetsBalance.PET_MOVE_LOW_MOBILITY_PENALTY;
            }
        }

        // Crowding penalty: no penalty if reproduction bonus available.
        double crowdingPenalty = 0.0d;
        if (hasCrowdingPenalty && !hasPartnerLookAhead) {
            crowdingPenalty = EtpetsBalance.PET_MOVE_CROWDING_PENALTY;
        }

        if (lowMobilityPenalty > 0) {
            AppLogger.infof("Applying low mobility penalty: hasLowMobilityPenalty=%s, survivalPressure=%.3f, hasResourceLookAhead=%s, calculatedPenalty=%.1f, trailBonus=%.1f, explorationBonus=%.1f",
                    hasLowMobilityPenalty, survivalPressure, hasResourceLookAhead, lowMobilityPenalty, trailBonus, explorationBonus);
        }
        double positiveTerms = resourceBonus + partnerBonus + trailBonus + explorationBonus;
        return (EtpetsBalance.PET_MOVE_SCORE_BASE + positiveTerms) - oscillationPenalty - lowMobilityPenalty - crowdingPenalty;
    }

    @SuppressWarnings("MagicNumber")
    static double computeRawReproduceScore(double petQualityScore,
                                           double partnerQualityScore) {
        double petQuality = clampToUnitRange(petQualityScore);
        double partnerQuality = clampToUnitRange(partnerQualityScore);
        double averageQuality = (petQuality + partnerQuality) / 2.0d;
        double minimumQuality = Math.min(petQuality, partnerQuality);
        double weightedQuality = (EtpetsBalance.PET_REPRODUCTION_SCORE_WEIGHT_AVG_QUALITY * averageQuality)
                + (EtpetsBalance.PET_REPRODUCTION_SCORE_WEIGHT_MIN_QUALITY * minimumQuality);

        double normalizedQuality = clampToUnitRange(weightedQuality);

        int minScore = EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MIN;
        int maxScore = EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MAX;
        int scoreSpan = maxScore - minScore;

        return minScore + (normalizedQuality * scoreSpan);
    }

    static double computeRawEatScore(int currentEnergy,
                                     int maxEnergy,
                                     int resourceEnergyGain,
                                     int age) {
        // Relative hunger term: lower fill ratio increases eat pressure.
        double saturation = (double) currentEnergy / maxEnergy;
        double hunger = clampToUnitRange(1.0d - saturation);
        double hungerScore = EtpetsBalance.PET_EAT_SCORE_HUNGER_WEIGHT
                * Math.pow(hunger, EtpetsBalance.PET_EAT_SCORE_HUNGER_EXPONENT);

        // Panic term based on absolute energy threshold.
        double panicNormalized = clampToUnitRange(
                (EtpetsBalance.PET_EAT_SCORE_PANIC_THRESHOLD - currentEnergy)
                        / EtpetsBalance.PET_EAT_SCORE_PANIC_THRESHOLD);
        double panicScore = EtpetsBalance.PET_EAT_SCORE_PANIC_WEIGHT
                * Math.pow(panicNormalized, EtpetsBalance.PET_EAT_SCORE_PANIC_EXPONENT);

        // Resource gain term (uses existing resource gain constants indirectly via resourceEnergyGain values).
        int maxGainReference = EtpetsBalance.INSECT_ENERGY_GAIN_PER_ACT;
        double gainNormalized = clampToUnitRange((double) resourceEnergyGain / maxGainReference);
        double gainScore = EtpetsBalance.PET_EAT_SCORE_GAIN_WEIGHT
                * Math.pow(gainNormalized, EtpetsBalance.PET_EAT_SCORE_GAIN_EXPONENT);

        // Strong waste damping: overfilling available capacity is heavily penalized.
        int missingEnergy = maxEnergy - currentEnergy;
        int wasteEnergy = Math.max(0, resourceEnergyGain - missingEnergy);
        // Defensive guard for invalid/degenerate resource values.
        double wasteRatio = (resourceEnergyGain > 0)
                ? ((double) wasteEnergy / resourceEnergyGain)
                : 0.0d;
        double wastePenalty = EtpetsBalance.PET_EAT_SCORE_WASTE_WEIGHT
                * Math.pow(wasteRatio, EtpetsBalance.PET_EAT_SCORE_WASTE_EXPONENT);

        // Small age bonus that decays quickly.
        double ageBonus = EtpetsBalance.PET_EAT_SCORE_AGE_WEIGHT
                * Math.exp(-(double) age / EtpetsBalance.PET_EAT_SCORE_AGE_DECAY);

        double positiveTerms = hungerScore + panicScore + gainScore + ageBonus;
        return positiveTerms - wastePenalty;
    }

    static double clampToUnitRange(double value) {
        return Math.clamp(value, 0.0d, 1.0d);
    }

}

