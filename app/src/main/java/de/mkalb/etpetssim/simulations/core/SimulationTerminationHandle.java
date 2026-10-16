package de.mkalb.etpetssim.simulations.core;

import java.util.concurrent.*;

/**
 * Owns bounded termination of a simulation executor after immediate disposal.
 */
public interface SimulationTerminationHandle {

    /**
     * Returns a termination handle for simulations that do not own background work.
     *
     * @return completed termination handle
     */
    static SimulationTerminationHandle completed() {
        return CompletedSimulationTerminationHandle.INSTANCE;
    }

    /**
     * Waits for the disposed executor to terminate.
     *
     * @param timeout maximum time to wait
     * @param unit    unit of {@code timeout}
     * @return {@code true} when the executor terminated
     * @throws InterruptedException if interrupted while waiting
     */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * Requests immediate interruption of remaining executor work.
     */
    void shutdownNow();

    enum CompletedSimulationTerminationHandle implements SimulationTerminationHandle {
        INSTANCE;

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return true;
        }

        @Override
        public void shutdownNow() {
        }
    }

}
