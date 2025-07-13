package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayViewModel;
import de.mkalb.etpetssim.simulations.simulationlab.SimulationLabController;
import de.mkalb.etpetssim.simulations.startscreen.StartScreenController;
import de.mkalb.etpetssim.simulations.wator.WaTorController;
import javafx.scene.layout.Region;

/**
 * Interface for simulation controllers, providing a method to build the view region for the simulation.
 */
public sealed interface SimulationController
        permits StartScreenController, SimulationLabController, WaTorController, ConwayViewModel {

    /**
     * Builds the view region for the simulation.
     * @return the view region for the simulation
     */
    Region buildViewRegion();

}
