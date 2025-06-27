package de.mkalb.etpetssim.simulations;

import javafx.scene.layout.Region;

import java.util.*;

/**
 * Represents an instance of a simulation, encapsulating the type of simulation,
 * the controller managing it, and the view region for the simulation.
 *
 * @param simulationType the type of simulation
 * @param simulationController the controller managing the simulation
 * @param region the view region for the simulation
 */
public record SimulationInstance(
        SimulationType simulationType,
        SimulationController simulationController,
        Region region) {

    public SimulationInstance {
        Objects.requireNonNull(simulationType);
        Objects.requireNonNull(simulationController);
        Objects.requireNonNull(region);
    }

    static SimulationInstance of(SimulationType type, SimulationController controller) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(controller);
        return new SimulationInstance(type, controller, controller.buildViewRegion());
    }

}
