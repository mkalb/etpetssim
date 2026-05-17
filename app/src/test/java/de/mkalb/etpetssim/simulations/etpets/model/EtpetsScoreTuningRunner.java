package de.mkalb.etpetssim.simulations.etpets.model;

import java.util.*;

/**
 * Manual tuner for score calculation formulas and balance constants.
 * Generates tabular output of raw scores for various input combinations
 * to support iterative refinement of {@code EtpetsScoreMath} calculations
 * and {@code EtpetsBalance} configuration values.
 */
@SuppressWarnings("MagicNumber")
public final class EtpetsScoreTuningRunner {

    private static final String HEADER_MOVE_SCORE = "=== MOVE SCORE (RAW) ===";
    private static final String HEADER_REPRODUCE_SCORE = "=== REPRODUCE SCORE (RAW) ===";
    private static final String HEADER_EAT_SCORE = "=== EAT SCORE (RAW) ===";
    private static final String COLUMNS_MOVE = "energyRatio, trailIntensity, resource, oscillationMatch, result,  rawResult";
    private static final String COLUMNS_REPRODUCE = "petQuality, partnerQuality, result,  rawResult";
    private static final String COLUMNS_EAT = "currentEnergy, maxEnergy, resourceGain,   age, result,  rawResult";

    private EtpetsScoreTuningRunner() {
    }

    static void main() {
        Locale.setDefault(Locale.ROOT);

        runMoveScoreSamples();
        runReproduceScoreSamples();
        runEatScoreSamples();
    }

    private static int clampToIntScore(double rawScore, int minScore) {
        return (rawScore < minScore) ? minScore : Math.toIntExact(Math.round(rawScore));
    }

    private static void runMoveScoreSamples() {
        System.out.println(HEADER_MOVE_SCORE);
        System.out.println(COLUMNS_MOVE);

        for (double energyRatio : List.of(0.15d, 0.50d, 0.85d)) {
            for (int trailIntensity : List.of(0, 1, 30, 50, 100, 200, 600, 800, EtpetsBalance.TRAIL_INTENSITY_RANGE_MAX - 10, EtpetsBalance.TRAIL_INTENSITY_RANGE_MAX)) {
                for (boolean hasResourceLookAhead : List.of(false, true)) {
                    for (boolean hasOscillationHistoryMatch : List.of(false, true)) {
                        double result = EtpetsScoreMath.computeRawMoveScore(
                                energyRatio,
                                1.0d,
                                hasResourceLookAhead,
                                false,
                                false,
                                false,
                                trailIntensity == 0,
                                trailIntensity,
                                hasOscillationHistoryMatch);
                        System.out.printf(Locale.ROOT,
                                "%11.2f, %14d, %8s, %16s, %6d, %10.3f%n",
                                energyRatio,
                                trailIntensity,
                                hasResourceLookAhead,
                                hasOscillationHistoryMatch,
                                clampToIntScore(result, EtpetsBalance.PET_MOVE_SCORE_RANGE_MIN),
                                result);
                    }
                }
            }
        }

        System.out.println();
    }

    private static void runReproduceScoreSamples() {
        System.out.println(HEADER_REPRODUCE_SCORE);
        System.out.println(COLUMNS_REPRODUCE);

        for (double petQuality : List.of(0.20d, 0.45d, 0.70d, 0.80d, 0.95d)) {
            for (double partnerQuality : List.of(0.20d, 0.45d, 0.70d, 0.95d)) {
                double result = EtpetsScoreMath.computeRawReproduceScore(petQuality, partnerQuality);
                System.out.printf(Locale.ROOT,
                        "%10.2f, %14.2f, %6d, %10.3f%n",
                        petQuality,
                        partnerQuality,
                        clampToIntScore(result, EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MIN),
                        result);
            }
        }

        System.out.println();
    }

    private static void runEatScoreSamples() {
        System.out.println(HEADER_EAT_SCORE);
        System.out.println(COLUMNS_EAT);
        for (int age : List.of(0, 10, 100, 1_000)) {
            for (int resourceEnergyGain : List.of(4, 20)) {
                for (int maxEnergy : List.of(75, 100, 145)) {
                    for (int currentEnergy : List.of(1, 5, 40, 65, maxEnergy - 5, maxEnergy - 1)) {
                        double result = EtpetsScoreMath.computeRawEatScore(
                                currentEnergy,
                                maxEnergy,
                                resourceEnergyGain,
                                age);
                        System.out.printf(Locale.ROOT,
                                "%13d, %9d, %12d, %5d, %6d, %10.3f%n",
                                currentEnergy,
                                maxEnergy,
                                resourceEnergyGain,
                                age,
                                clampToIntScore(result, EtpetsBalance.PET_EAT_SCORE_RANGE_MIN),
                                result);
                    }
                }
            }
        }

        System.out.println();
    }

}
