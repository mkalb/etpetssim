package de.mkalb.etpetssim.simulations.core.view;

import javafx.scene.layout.Region;

/**
 * Contract for building the simulation observation region.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SimulationObservationView {

    /**
     * Builds the observation UI region.
     *
     * @return root region for simulation observation
     */
    Region buildObservationRegion();

}
