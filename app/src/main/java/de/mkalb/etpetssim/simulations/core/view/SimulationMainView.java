package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import javafx.scene.layout.Region;

/**
 * Contract for building and shutting down a simulation main view.
 */
public interface SimulationMainView {

    /**
     * Builds the root region for a simulation screen.
     *
     * <p>Repeated calls for one view instance return the same root region.
     *
     * @return root region for the simulation view
     */
    Region buildMainRegion();

    /**
     * Stops active simulation-related work and releases view-side resources.
     *
     * <p>This operation is idempotent and must not wait for background work to terminate.
     *
     * @return termination handle for application-exit bookkeeping
     */
    SimulationTerminationHandle shutdownSimulation();

}
