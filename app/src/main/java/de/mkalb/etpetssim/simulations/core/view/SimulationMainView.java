package de.mkalb.etpetssim.simulations.core.view;

import javafx.scene.layout.Region;

/**
 * The SimulationMainView interface represents a contract for all simulation views
 * in the application.
 */
public interface SimulationMainView {

    /**
     * Builds the main view region for the simulation.
     * <p>
     * This method is responsible for constructing and returning the primary
     * UI component (a {@link Region}) for the simulation. Each implementation
     * of this interface should provide its own specific view region.
     *
     * @return the main view region for the simulation
     */
    Region buildMainRegion();

    /**
     * Shuts down the simulation and releases all associated resources.
     * <p>
     * This method should be called when the simulation is no longer needed,
     * for example, when the user closes the simulation window. It is responsible
     * for stopping background tasks, terminating executors, and performing any
     * necessary cleanup to prevent resource leaks.
     * </p>
     * <p>
     * After calling this method, the simulation instance should not be used again.
     * </p>
     */
    void shutdownSimulation();

}
