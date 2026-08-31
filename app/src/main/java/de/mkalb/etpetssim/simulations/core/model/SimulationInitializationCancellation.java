package de.mkalb.etpetssim.simulations.core.model;

/**
 * Cooperative cancellation contract for work performed exclusively during simulation initialization.
 */
@FunctionalInterface
public interface SimulationInitializationCancellation {

    /**
     * Returns a cancellation contract that never cancels.
     *
     * @return non-canceling contract
     */
    static SimulationInitializationCancellation none() {
        return () -> {};
    }

    /**
     * Returns a cancellation contract backed by the current thread's interruption state.
     *
     * @return interruption-aware cancellation contract
     */
    static SimulationInitializationCancellation interruptionAware() {
        return () -> {
            if (Thread.currentThread().isInterrupted()) {
                throw new SimulationInitializationCanceledException();
            }
        };
    }

    /**
     * Checks whether initialization must stop.
     *
     * @throws SimulationInitializationCanceledException if cancellation was requested or the current thread was
     *                                                   interrupted
     */
    void checkCanceled();

}
