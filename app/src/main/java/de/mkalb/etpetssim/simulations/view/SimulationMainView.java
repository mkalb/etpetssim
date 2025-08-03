package de.mkalb.etpetssim.simulations.view;

import javafx.scene.layout.Region;

/**
 * The SimulationMainView interface represents a contract for all simulation views
 * in the application.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
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

}
