package de.mkalb.etpetssim.simulations.core.model;

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

    /**
     * Constructs a simulation mode with the specified resource key.
     *
     * @param resourceKey the resource key for this simulation mode
     */
    SimulationMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum SimulationMode.
     *
     * @return the resource key for the label of the enum SimulationMode
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "simulationmode.label";
    }

    /**
     * Returns the resource key associated with this simulation mode.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this simulation mode
     */
    public String resourceKey() {
        return resourceKey;
    }

}
