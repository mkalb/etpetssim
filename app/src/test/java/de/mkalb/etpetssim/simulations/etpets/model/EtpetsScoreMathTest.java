package de.mkalb.etpetssim.simulations.etpets.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class EtpetsScoreMathTest {

    private static final double DELTA = 1.0e-9d;
    private static final double MOVEMENT_COST_MODIFIER = EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN;
    private static final int OLD_AGE = 1_000_000;

    private static double moveScore(double energyRatio,
                                    boolean hasResourceLookAhead,
                                    boolean hasPartnerLookAhead,
                                    boolean hasLowMobilityPenalty,
                                    boolean hasCrowdingPenalty,
                                    boolean isGroundWithoutTrail,
                                    int trailIntensity,
                                    boolean hasOscillationHistoryMatch) {
        return EtpetsScoreMath.computeRawMoveScore(energyRatio, MOVEMENT_COST_MODIFIER, hasResourceLookAhead,
                hasPartnerLookAhead, hasLowMobilityPenalty, hasCrowdingPenalty, isGroundWithoutTrail, trailIntensity,
                hasOscillationHistoryMatch);
    }

    private static double baseMoveScore() {
        return moveScore(1.0d, false, false, false, false, false, 0, false);
    }

    @Test
    void testComputeRawMoveScore() {
        assertEquals(EtpetsBalance.PET_MOVE_SCORE_BASE, baseMoveScore(), DELTA);
    }

    @Test
    void testComputeRawMoveScoreAddsBonuses() {
        double base = baseMoveScore();
        double trailScore = moveScore(1.0d, false, false, false, false, false, EtpetsBalance.TRAIL_INTENSITY_MAX, false);

        assertAll(
                () -> assertEquals(base + EtpetsBalance.PET_MOVE_RESOURCE_BASE_WEIGHT,
                        moveScore(1.0d, true, false, false, false, false, 0, false), DELTA),
                () -> assertEquals(base + EtpetsBalance.PET_MOVE_RESOURCE_BASE_WEIGHT
                                + EtpetsBalance.PET_MOVE_RESOURCE_SURVIVAL_PRESSURE_WEIGHT,
                        moveScore(0.0d, true, false, false, false, false, 0, false), DELTA),
                () -> assertEquals(base + EtpetsBalance.PET_MOVE_PARTNER_WEIGHT,
                        moveScore(1.0d, false, true, false, false, false, 0, false), DELTA),
                () -> assertEquals(base + EtpetsBalance.PET_MOVE_EXPLORATION_WEIGHT,
                        moveScore(1.0d, false, false, false, false, true, 0, false), DELTA),
                () -> assertTrue(trailScore > base),
                () -> assertTrue(trailScore <= (base + EtpetsBalance.PET_MOVE_TRAIL_BONUS_MAX + DELTA))
        );
    }

    @Test
    void testComputeRawMoveScoreSubtractsPenalties() {
        double base = baseMoveScore();

        assertAll(
                () -> assertEquals(base - EtpetsBalance.PET_MOVE_OSCILLATION_HISTORY_MATCH_PENALTY,
                        moveScore(1.0d, false, false, false, false, false, 0, true), DELTA),
                () -> assertEquals(base - EtpetsBalance.PET_MOVE_LOW_MOBILITY_PENALTY,
                        moveScore(1.0d, false, false, true, false, false, 0, false), DELTA),
                () -> assertEquals(base - EtpetsBalance.PET_MOVE_CROWDING_PENALTY,
                        moveScore(1.0d, false, false, false, true, false, 0, false), DELTA)
        );
    }

    @Test
    void testComputeRawMoveScoreSkipsPenaltiesWithLookAhead() {
        assertAll(
                () -> assertEquals(moveScore(0.0d, true, false, false, false, false, 0, false),
                        moveScore(0.0d, true, false, true, false, false, 0, false), DELTA),
                () -> assertEquals(moveScore(1.0d, false, true, false, false, false, 0, false),
                        moveScore(1.0d, false, true, false, true, false, 0, false), DELTA)
        );
    }

    @Test
    void testComputeRawReproduceScore() {
        double mediumScore = EtpetsScoreMath.computeRawReproduceScore(0.5d, 0.5d);

        assertAll(
                () -> assertEquals(EtpetsBalance.PET_REPRODUCTION_SCORE_MIN,
                        EtpetsScoreMath.computeRawReproduceScore(0.0d, 0.0d), DELTA),
                () -> assertEquals(EtpetsBalance.PET_REPRODUCTION_SCORE_MAX,
                        EtpetsScoreMath.computeRawReproduceScore(1.0d, 1.0d), DELTA),
                () -> assertTrue(mediumScore > EtpetsBalance.PET_REPRODUCTION_SCORE_MIN),
                () -> assertTrue(mediumScore < EtpetsBalance.PET_REPRODUCTION_SCORE_MAX),
                () -> assertTrue(EtpetsScoreMath.computeRawReproduceScore(0.5d, 0.1d) < mediumScore)
        );
    }

    @Test
    void testComputeRawReproduceScoreClampsQualityScores() {
        assertAll(
                () -> assertEquals(EtpetsBalance.PET_REPRODUCTION_SCORE_MIN,
                        EtpetsScoreMath.computeRawReproduceScore(-1.0d, -1.0d), DELTA),
                () -> assertEquals(EtpetsBalance.PET_REPRODUCTION_SCORE_MAX,
                        EtpetsScoreMath.computeRawReproduceScore(2.0d, 2.0d), DELTA)
        );
    }

    @Test
    void testComputeRawEatScore() {
        int maxEnergy = EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX;
        int gain = EtpetsBalance.PLANT_ENERGY_GAIN_PER_ACT;

        assertTrue(EtpetsScoreMath.computeRawEatScore(10, maxEnergy, gain, OLD_AGE)
                > EtpetsScoreMath.computeRawEatScore(100, maxEnergy, gain, OLD_AGE));
    }

    @Test
    void testComputeRawEatScorePenalizesOverfill() {
        int maxEnergy = EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX;
        int gain = EtpetsBalance.INSECT_ENERGY_GAIN_PER_ACT;

        assertAll(
                () -> assertTrue(EtpetsScoreMath.computeRawEatScore(maxEnergy, maxEnergy, gain, OLD_AGE) < 0.0d),
                () -> assertTrue(EtpetsScoreMath.computeRawEatScore(maxEnergy - gain, maxEnergy, gain, OLD_AGE)
                        > EtpetsScoreMath.computeRawEatScore(maxEnergy, maxEnergy, gain, OLD_AGE))
        );
    }

    @Test
    void testClampToUnitRange() {
        assertAll(
                () -> assertEquals(0.0d, EtpetsScoreMath.clampToUnitRange(-0.5d), DELTA),
                () -> assertEquals(0.25d, EtpetsScoreMath.clampToUnitRange(0.25d), DELTA),
                () -> assertEquals(1.0d, EtpetsScoreMath.clampToUnitRange(1.5d), DELTA)
        );
    }

}
