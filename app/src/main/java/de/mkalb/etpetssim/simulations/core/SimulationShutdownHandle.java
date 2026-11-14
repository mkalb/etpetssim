package de.mkalb.etpetssim.simulations.core;

import java.util.concurrent.TimeUnit;

/**
 * Owns bounded termination of a simulation executor after immediate disposal.
 */
public interface SimulationShutdownHandle {

    /**
     * Returns a shutdown handle for simulations that do not own background work.
     *
     * @return completed shutdown handle
     */
    static SimulationShutdownHandle completed() {
        return CompletedShutdownHandle.INSTANCE;
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

    /**
     * Shutdown handle without background work; it reports immediate termination.
     */
    enum CompletedShutdownHandle implements SimulationShutdownHandle {
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
