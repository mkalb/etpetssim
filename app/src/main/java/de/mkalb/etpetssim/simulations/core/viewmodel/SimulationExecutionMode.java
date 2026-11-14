package de.mkalb.etpetssim.simulations.core.viewmodel;

/**
 * Defines the available execution modes for running a simulation.
 */
public enum SimulationExecutionMode {

    /**
     * Timed mode: Executes simulation steps at a fixed interval (using a timer).
     * Drawing may be skipped if rendering takes too long.
     */
    TIMED("simulationexecutionmode.timed"),

    /**
     * Batch (single) mode: Executes a fixed number of steps in a background thread, then pauses.
     * Drawing occurs only after the batch completes.
     */
    BATCH_SINGLE("simulationexecutionmode.batchsingle"),

    /**
     * Batch (continuous) mode: Continuously executes batches of steps in a background thread.
     * After each batch, drawing occurs and the next batch starts automatically until paused or finished.
     */
    BATCH_CONTINUOUS("simulationexecutionmode.batchcontinuous");

    private final String resourceKey;

    SimulationExecutionMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    public static String labelResourceKey() {
        return "simulationexecutionmode.label";
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
