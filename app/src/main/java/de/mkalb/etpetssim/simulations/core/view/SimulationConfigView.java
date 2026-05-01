package de.mkalb.etpetssim.simulations.core.view;

import javafx.scene.layout.Region;

/**
 * Contract for building the simulation configuration region.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SimulationConfigView {

    /**
     * Builds the configuration UI region.
     *
     * @return root region for simulation configuration
     */
    Region buildConfigRegion();

}
