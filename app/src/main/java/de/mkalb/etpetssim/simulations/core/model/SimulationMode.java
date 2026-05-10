package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

/**
 * Defines the available simulation modes for running a simulation.
 * <p>
 * Each mode represents a distinct way of executing the simulation:
 * <ul>
 *   <li>{@link #TIMED}: Executes simulation steps at a fixed interval using a timer.
 *   Drawing may be throttled if rendering is slow.</li>
 *   <li>{@link #BATCH_SINGLE}: Executes a fixed number of steps in a background thread, then pauses.
 *   Drawing occurs only after the batch completes.</li>
 *   <li>{@link #BATCH_CONTINUOUS}: Continuously executes batches of steps in a background thread,
 *   drawing after each batch, and automatically starts the next batch until paused or canceled.</li>
 * </ul>
 * The {@code resourceKey} field allows for resource lookup, such as internationalized labels.
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
     * Returns the resource-bundle key for the display label of this enum type.
     *
     * <p>The returned key is intended for localized lookup of the enum type name
     * (that is, the label for the enum as a whole, not for an individual enum constant).</p>
     *
     * @return the resource bundle key for this enum type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return AppLocalizationKeys.ENUM_LABEL_SIMULATIONMODE;
    }

    /**
     * Returns the resource bundle key associated with this enum constant.
     * <p>
     * The resource key can be used for localized message lookup via {@code AppLocalization}.
     *
     * @return the resource bundle key for this enum constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}
