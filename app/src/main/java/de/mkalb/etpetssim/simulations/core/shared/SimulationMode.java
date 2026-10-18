package de.mkalb.etpetssim.simulations.core.shared;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

/**
 * Defines the available simulation modes for running a simulation.
 */
public enum SimulationMode {

    /**
     * Timed mode: Executes simulation steps at a fixed interval (using a timer).
     * Drawing may be skipped if rendering takes too long.
     */
    TIMED("simulationmode.timed"),

    /**
     * Batch (single) mode: Executes a fixed number of steps in a background thread, then pauses.
     * Drawing occurs only after the batch completes.
     */
    BATCH_SINGLE("simulationmode.batchsingle"),

    /**
     * Batch (continuous) mode: Continuously executes batches of steps in a background thread.
     * After each batch, drawing occurs and the next batch starts automatically until paused or finished.
     */
    BATCH_CONTINUOUS("simulationmode.batchcontinuous");

    private final String resourceKey;

    SimulationMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return AppLocalizationKeys.ENUM_LABEL_SIMULATIONMODE;
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}
