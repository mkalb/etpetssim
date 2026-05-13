package de.mkalb.etpetssim.simulations.etpets.model;

import java.util.*;

public final class EtpetsScoreTuningRunner {

    private EtpetsScoreTuningRunner() {
    }

    static void main() {
        Locale.setDefault(Locale.ROOT);

        runMoveScoreSamples();
        runReproduceScoreSamples();
        runEatScoreSamples();
    }

    private static void runMoveScoreSamples() {
        System.out.println("=== MOVE SCORE (RAW) ===");
        System.out.println("energyRatio, trailIntensity, result,  rawResult");

        for (double energyRatio : List.of(0.15d, 0.50d, 0.85d)) {
            for (int trailIntensity : List.of(0, 50, 200, 2_000, 9_000)) {
                double result = EtpetsScoreMath.computeRawMoveScore(
                        energyRatio,
                        1.0d,
                        true,
                        true,
                        false,
                        false,
                        trailIntensity == 0,
                        trailIntensity,
                        false,
                        false);
                System.out.printf(Locale.ROOT,
                        "%11.2f, %14d, %6d, %10.3f%n",
                        energyRatio,
                        trailIntensity,
                        (result < EtpetsBalance.PET_MOVE_SCORE_RANGE_MIN) ? EtpetsBalance.PET_MOVE_SCORE_RANGE_MIN : Math.toIntExact(Math.round(result)),
                        result);
            }
        }

        System.out.println();
    }

    private static void runReproduceScoreSamples() {
        System.out.println("=== REPRODUCE SCORE (RAW) ===");
        System.out.println("petQuality, partnerQuality, result,  rawResult");

        for (double petQuality : List.of(0.20d, 0.45d, 0.70d, 0.80d, 0.95d)) {
            for (double partnerQuality : List.of(0.20d, 0.45d, 0.70d, 0.95d)) {
                double result = EtpetsScoreMath.computeRawReproduceScore(petQuality, partnerQuality);
                System.out.printf(Locale.ROOT,
                        "%10.2f, %14.2f, %6d, %10.3f%n",
                        petQuality,
                        partnerQuality,
                        (result < EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MIN) ? EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MIN : Math.toIntExact(Math.round(result)),
                        result);
            }
        }

        System.out.println();
    }

    private static void runEatScoreSamples() {
        System.out.println("=== EAT SCORE (RAW) ===");
        System.out.println("currentEnergy, maxEnergy, resourceGain,   age, result,  rawResult");
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
                                (result < EtpetsBalance.PET_EAT_SCORE_RANGE_MIN) ? EtpetsBalance.PET_EAT_SCORE_RANGE_MIN : Math.toIntExact(Math.round(result)),
                                result);
                    }
                }
            }
        }

        System.out.println();
    }

}
