package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Defines the possible states in the lifecycle of a simulation.
 * <p>
 * Each constant represents a distinct phase or control state, used to manage simulation execution
 * and UI control availability in the application.
 * <p>
 * The state determines which actions (start, pause, resume, cancel) are available and
 * whether configuration controls are enabled or disabled.
 */
public enum SimulationState {

    /**
     * Application launched, no simulation has been started yet.
     * <p>
     * Configuration controls are enabled. Simulation can be started.
     */
    INITIAL,

    /**
     * Simulation is running in timed mode (stepwise execution with UI updates).
     * <p>
     * Configuration controls are disabled. Pause and cancel actions are available.
     */
    RUNNING_TIMED,

    /**
     * Simulation is running in batch mode (continuous execution without intermediate UI updates).
     * <p>
     * Configuration controls are disabled. Pause and cancel actions are available.
     */
    RUNNING_BATCH,

    /**
     * Pause requested during batch mode; waiting for a safe point to pause.
     * <p>
     * Configuration controls are disabled. Cancel action is available.
     */
    PAUSING_BATCH,

    /**
     * Simulation is fully paused (applies to both timed and batch modes).
     * <p>
     * Configuration controls are enabled. Resume and cancel actions are available.
     */
    PAUSED,

    /**
     * Cancellation requested during batch mode; waiting for a safe point to cancel.
     * <p>
     * Configuration controls are disabled. No further actions available until cancellation completes.
     */
    CANCELLING_BATCH,

    /**
     * Simulation was canceled by the user.
     * <p>
     * Configuration controls are enabled. Simulation can be started again.
     */
    CANCELED,

    /**
     * Simulation completed successfully.
     * <p>
     * Configuration controls are enabled. Simulation can be started again.
     */
    FINISHED,

    /**
     * Error state: Simulation could not be started or was terminated due to an error.
     * <p>
     * Configuration controls are enabled. Simulation can be started again.
     */
    ERROR,

    /**
     * Global shutdown initiated; simulation and resources are being terminated.
     * <p>
     * All controls are disabled.
     */
    SHUTTING_DOWN;

    /**
     * Checks if the simulation can be started from the current state.
     *
     * @return {@code true} if the simulation can be started, {@code false} otherwise
     */
    public boolean isStartable() {
        return switch (this) {
            case INITIAL, CANCELED, FINISHED, ERROR -> true;
            case RUNNING_TIMED, RUNNING_BATCH, PAUSING_BATCH, PAUSED, CANCELLING_BATCH, SHUTTING_DOWN -> false;
        };
    }

    /**
     * Checks if the simulation is currently running (timed or batch mode).
     *
     * @return {@code true} if the simulation is running, {@code false} otherwise
     */
    public boolean isRunning() {
        return switch (this) {
            case RUNNING_TIMED, RUNNING_BATCH -> true;
            case INITIAL, PAUSING_BATCH, PAUSED, CANCELLING_BATCH, CANCELED, FINISHED, ERROR, SHUTTING_DOWN -> false;
        };
    }

    /**
     * Checks if the simulation is currently paused.
     *
     * @return {@code true} if the simulation is paused, {@code false} otherwise
     */
    public boolean isPaused() {
        return this == PAUSED;
    }

}
