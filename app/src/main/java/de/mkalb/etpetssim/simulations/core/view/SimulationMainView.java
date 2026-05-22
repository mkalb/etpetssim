package de.mkalb.etpetssim.simulations.core.view;

import javafx.scene.layout.Region;

/**
 * Contract for building and shutting down a simulation main view.
 */
public interface SimulationMainView {

    /**
     * Builds the root region for a simulation screen.
     *
     * @return root region for the simulation view
     */
    Region buildMainRegion();

    /**
     * Stops active simulation-related work and releases view-side resources.
     */
    void shutdownSimulation();

}
