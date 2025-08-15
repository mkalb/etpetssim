package de.mkalb.etpetssim.simulations.model;

/**
 * Defines the possible states of a simulation's lifecycle.
 * Used to represent and control the current execution state of a simulation.
 *
 */
public enum SimulationState {

    /** App launched, no simulation yet */
    INITIAL,

    /** Live mode running (stepwise with UI updates) */
    RUNNING_LIVE,

    /** Batch mode running (no intermediate UI updates) */
    RUNNING_BATCH,

    /** Batch pause requested; waiting for safe point */
    PAUSING_BATCH,

    /** Fully paused (applies to live and batch) */
    PAUSED,

    /** Batch cancellation requested; waiting for safe point */
    CANCELLING_BATCH,

    /** Batch finalization/cleanup in progress */
    FINALIZING_BATCH,

    /** Simulation was cancelled */
    CANCELLED,

    /** Simulation completed successfully */
    FINISHED,

    /**
     * Error state: The simulation could not be started or was terminated due to an error.
     * A completely new simulation can be started from this state.
     */
    ERROR,

    /** Global shutdown initiated */
    SHUTTING_DOWN;

    /** Whether a (re)start of the simulation is allowed */
    public boolean canStart() {
        return switch (this) {
            case INITIAL, CANCELLED, FINISHED, ERROR -> true;
            default -> false;
        };
    }

    /** Whether the simulation is actively running (live or batch) */
    public boolean isRunning() {
        return (this == RUNNING_LIVE) || (this == RUNNING_BATCH);
    }

    /** Whether the simulation is fully paused (not just pausing) */
    public boolean isPaused() {
        return this == PAUSED;
    }

    /** Whether a batch cancellation is in progress */
    public boolean isCancelling() {
        return this == CANCELLING_BATCH;
    }

    /** Whether batch finalization/cleanup is in progress */
    public boolean isFinalizing() {
        return this == FINALIZING_BATCH;
    }

    /** Whether a global shutdown is in progress */
    public boolean isShuttingDown() {
        return this == SHUTTING_DOWN;
    }

    /** Whether the simulation reached a terminal end state */
    public boolean isTerminal() {
        return switch (this) {
            case CANCELLED, FINISHED, ERROR -> true;
            default -> false;
        };
    }

    /** Optional: lock user interactions and scheduling during teardown */
    public boolean isInteractionLocked() {
        return isShuttingDown() || isCancelling() || isFinalizing();
    }

    public boolean canTransitionTo(SimulationState next) {
        return switch (this) {
            case INITIAL -> (next == RUNNING_LIVE) || (next == RUNNING_BATCH) || (next == SHUTTING_DOWN);
            case RUNNING_LIVE -> (next == PAUSED) || (next == SHUTTING_DOWN);
            case RUNNING_BATCH ->
                    (next == PAUSING_BATCH) || (next == CANCELLING_BATCH) || (next == FINALIZING_BATCH) || (next == SHUTTING_DOWN);
            // TODO Add all states
            default -> false;
        };
    }

}
