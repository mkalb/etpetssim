package de.mkalb.etpetssim.simulations.etpets.model;

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
            resourceBonus = EtpetsBalance.PET_MOVE_RESOURCE_BASE_WEIGHT
                    + (EtpetsBalance.PET_MOVE_RESOURCE_SURVIVAL_PRESSURE_WEIGHT * survivalPressure);
        }

        double partnerBonus = hasPartnerLookAhead
                ? EtpetsBalance.PET_MOVE_PARTNER_WEIGHT
                : 0.0d;

        double trailBonus = 0.0d;
        if (trailIntensity >= EtpetsBalance.PET_MOVE_TRAIL_BONUS_INTENSITY_THRESHOLD) {
            int trailIntensityAboveThreshold = trailIntensity - EtpetsBalance.PET_MOVE_TRAIL_BONUS_INTENSITY_THRESHOLD;
            double trailBonusIntensityScale = EtpetsBalance.PET_MOVE_TRAIL_BONUS_INTENSITY_SCALE;
            double normalizedTrailIntensity = 1.0d - Math.exp(-(trailIntensityAboveThreshold / trailBonusIntensityScale));
            double trailBonusCurveNumerator = 1.0d - Math.exp(-EtpetsBalance.PET_MOVE_TRAIL_BONUS_CURVE_SHARPNESS * normalizedTrailIntensity);
            double trailBonusCurveDenominator = 1.0d - Math.exp(-EtpetsBalance.PET_MOVE_TRAIL_BONUS_CURVE_SHARPNESS);
            trailBonus = EtpetsBalance.PET_MOVE_TRAIL_BONUS_MAX * (trailBonusCurveNumerator / trailBonusCurveDenominator);
        }

        double movementCostModifierSpan = EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX
                - EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN;
        double movementCostModifierNormalized = clampToUnitRange((movementCostModifier - EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN) / movementCostModifierSpan);
        double movementEfficiency = clampToUnitRange(1.0d - movementCostModifierNormalized);
        double explorationDrive = Math.pow(energyRatio, EtpetsBalance.PET_MOVE_EXPLORATION_ENERGY_EXPONENT)
                * Math.pow(movementEfficiency, EtpetsBalance.PET_MOVE_EXPLORATION_COST_EXPONENT);
        double explorationBonus = isGroundWithoutTrail
                ? (EtpetsBalance.PET_MOVE_EXPLORATION_WEIGHT * explorationDrive)
                : 0.0d;

        double oscillationPenalty = 0.0d;
        if (isPreviousCoordinate) {
            oscillationPenalty += EtpetsBalance.PET_MOVE_OSCILLATION_ONE_STEP_BACK_PENALTY;
        }
        if (isPreviousPreviousCoordinate) {
            oscillationPenalty += EtpetsBalance.PET_MOVE_OSCILLATION_TWO_STEPS_BACK_PENALTY;
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

        // if (lowMobilityPenalty > 0) {
        //     AppLogger.infof("Applying low mobility penalty: hasLowMobilityPenalty=%s, survivalPressure=%.3f, hasResourceLookAhead=%s, calculatedPenalty=%.1f, trailBonus=%.1f, explorationBonus=%.1f",
        //             hasLowMobilityPenalty, survivalPressure, hasResourceLookAhead, lowMobilityPenalty, trailBonus, explorationBonus);
        // }
        double positiveScoreTerms = resourceBonus + partnerBonus + trailBonus + explorationBonus;
        return (EtpetsBalance.PET_MOVE_SCORE_BASE + positiveScoreTerms) - oscillationPenalty - lowMobilityPenalty - crowdingPenalty;
    }

    @SuppressWarnings("MagicNumber")
    static double computeRawReproduceScore(double petQualityScore,
                                           double partnerQualityScore) {
        double normalizedPetQuality = clampToUnitRange(petQualityScore);
        double normalizedPartnerQuality = clampToUnitRange(partnerQualityScore);
        double averageQuality = (normalizedPetQuality + normalizedPartnerQuality) / 2.0d;
        double minimumQuality = Math.min(normalizedPetQuality, normalizedPartnerQuality);
        double weightedQualityScore = (EtpetsBalance.PET_REPRODUCTION_SCORE_AVERAGE_QUALITY_WEIGHT * averageQuality)
                + (EtpetsBalance.PET_REPRODUCTION_SCORE_MINIMUM_QUALITY_WEIGHT * minimumQuality);

        double normalizedWeightedQuality = clampToUnitRange(weightedQualityScore);

        int minScore = EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MIN;
        int maxScore = EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MAX;
        int scoreSpan = maxScore - minScore;

        return minScore + (normalizedWeightedQuality * scoreSpan);
    }

    static double computeRawEatScore(int currentEnergy,
                                     int maxEnergy,
                                     int resourceEnergyGain,
                                     int age) {
        // Relative hunger term: lower fill ratio increases eat pressure.
        double energyFillRatio = (double) currentEnergy / maxEnergy;
        double hunger = clampToUnitRange(1.0d - energyFillRatio);
        double hungerScore = EtpetsBalance.PET_EAT_SCORE_HUNGER_WEIGHT
                * Math.pow(hunger, EtpetsBalance.PET_EAT_SCORE_HUNGER_EXPONENT);

        // Panic term based on absolute energy threshold.
        double panicPressureNormalized = clampToUnitRange(
                (EtpetsBalance.PET_EAT_SCORE_PANIC_ENERGY_THRESHOLD - currentEnergy)
                        / EtpetsBalance.PET_EAT_SCORE_PANIC_ENERGY_THRESHOLD);
        double panicScore = EtpetsBalance.PET_EAT_SCORE_PANIC_WEIGHT
                * Math.pow(panicPressureNormalized, EtpetsBalance.PET_EAT_SCORE_PANIC_EXPONENT);

        // Resource gain term (uses existing resource gain constants indirectly via resourceEnergyGain values).
        int maxResourceEnergyGainReference = EtpetsBalance.INSECT_ENERGY_GAIN_PER_ACT;
        double resourceGainNormalized = clampToUnitRange((double) resourceEnergyGain / maxResourceEnergyGainReference);
        double resourceGainScore = EtpetsBalance.PET_EAT_SCORE_RESOURCE_GAIN_WEIGHT
                * Math.pow(resourceGainNormalized, EtpetsBalance.PET_EAT_SCORE_RESOURCE_GAIN_EXPONENT);

        // Strong waste damping: overfilling available capacity is heavily penalized.
        int remainingEnergyCapacity = maxEnergy - currentEnergy;
        int overfillEnergy = Math.max(0, resourceEnergyGain - remainingEnergyCapacity);
        // Defensive guard for invalid/degenerate resource values.
        double overfillRatio = (resourceEnergyGain > 0)
                ? ((double) overfillEnergy / resourceEnergyGain)
                : 0.0d;
        double wastePenalty = EtpetsBalance.PET_EAT_SCORE_OVERFILL_PENALTY_WEIGHT
                * Math.pow(overfillRatio, EtpetsBalance.PET_EAT_SCORE_OVERFILL_PENALTY_EXPONENT);

        // Small age bonus that decays quickly.
        double ageBonus = EtpetsBalance.PET_EAT_SCORE_AGE_WEIGHT
                * Math.exp(-(double) age / EtpetsBalance.PET_EAT_SCORE_AGE_DECAY_STEPS);

        double positiveScoreTerms = hungerScore + panicScore + resourceGainScore + ageBonus;
        return positiveScoreTerms - wastePenalty;
    }

    static double clampToUnitRange(double value) {
        return Math.clamp(value, 0.0d, 1.0d);
    }

}
