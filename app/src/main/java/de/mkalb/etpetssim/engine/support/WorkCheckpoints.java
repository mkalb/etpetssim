package de.mkalb.etpetssim.engine.support;

/**
 * Cooperative cancellation checkpoints for long-running loops.
 *
 * <p>A loop gets a checkpoint when its total iteration count can exceed {@link #CANCELLATION_CHECK_INTERVAL} within
 * the configuration limits of its simulation, e.g., a loop over all cells or a share of them. Nested loops count their
 * iterations with one running index across all levels. Loops with a lower maximum get no checkpoint; the cancellation
 * checks before and after the whole initialization cover them. Loops check for cancellation only at the checkpoints
 * of {@link #isCancellationCheckpoint(int)}, never in every iteration.
 */
public final class WorkCheckpoints {

    /**
     * Number of loop iterations between cooperative cancellation checks; a power of two.
     */
    public static final int CANCELLATION_CHECK_INTERVAL = 1_024;

    private static final int CANCELLATION_CHECK_MASK = CANCELLATION_CHECK_INTERVAL - 1;

    private WorkCheckpoints() {
    }

    /**
     * Returns whether a loop checks for cancellation at the given index.
     *
     * @param index the zero-based loop index
     * @return {@code true} for every multiple of {@link #CANCELLATION_CHECK_INTERVAL}, including {@code 0}
     */
    public static boolean isCancellationCheckpoint(int index) {
        return (index & CANCELLATION_CHECK_MASK) == 0;
    }

}
