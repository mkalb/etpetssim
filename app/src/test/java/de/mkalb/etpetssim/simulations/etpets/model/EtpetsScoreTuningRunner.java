package de.mkalb.etpetssim.simulations.etpets.model;

import java.util.Locale;

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

        double[] energyRatios = {0.15d, 0.50d, 0.85d};
        int[] trailIntensities = {0, 120, 800, 2_500};

        for (double energyRatio : energyRatios) {
            for (int trailIntensity : trailIntensities) {
                double result = EtpetsScoreMath.computeRawMoveScore(
                        energyRatio,
                        1.0d,
                        true,
                        false,
                        false,
                        false,
                        trailIntensity == 0,
                        trailIntensity,
                        false,
                        false);
                System.out.printf(Locale.ROOT,
                        "energyRatio=%.2f, trailIntensity=%d, resource=true -> rawMoveScore=%.3f%n",
                        energyRatio,
                        trailIntensity,
                        result);
            }
        }

        System.out.println();
    }

    private static void runReproduceScoreSamples() {
        System.out.println("=== REPRODUCE SCORE (RAW) ===");

        double[][] qualityPairs = {
                {0.20d, 0.20d},
                {0.45d, 0.70d},
                {0.80d, 0.95d}
        };

        for (double[] pair : qualityPairs) {
            double result = EtpetsScoreMath.computeRawReproduceScore(pair[0], pair[1]);
            System.out.printf(Locale.ROOT,
                    "petQuality=%.2f, partnerQuality=%.2f -> rawReproduceScore=%.3f%n",
                    pair[0],
                    pair[1],
                    result);
        }

        System.out.println();
    }

    private static void runEatScoreSamples() {
        System.out.println("=== EAT SCORE (RAW) ===");

        int[][] cases = {
                {15, 120, 4, 20},
                {30, 120, 20, 80},
                {90, 120, 20, 250}
        };

        for (int[] currentCase : cases) {
            int currentEnergy = currentCase[0];
            int maxEnergy = currentCase[1];
            int resourceEnergyGain = currentCase[2];
            int age = currentCase[3];

            double result = EtpetsScoreMath.computeRawEatScore(
                    currentEnergy,
                    maxEnergy,
                    resourceEnergyGain,
                    age);
            System.out.printf(Locale.ROOT,
                    "currentEnergy=%d, maxEnergy=%d, resourceGain=%d, age=%d -> rawEatScore=%.3f%n",
                    currentEnergy,
                    maxEnergy,
                    resourceEnergyGain,
                    age,
                    result);
        }

        System.out.println();
    }
}

