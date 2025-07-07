package de.mkalb.etpetssim.simulations;

import javafx.scene.layout.Region;

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

    static SimulationInstance of(SimulationType type, SimulationController controller) {
        return new SimulationInstance(type, controller, controller.buildViewRegion());
    }

    /**
     * Returns a concise string representation of this simulation instance.
     * <p>
     * Format: {@code [SIMULATION_TYPE, CONTROLLER_CLASS]}
     * <br>
     * Example: {@code [SIMULATION_LAB, SimulationLabController]}
     *
     * @return a concise display string for this simulation instance
     */
    public String toDisplayString() {
        return String.format("[%s, %s]", simulationType, simulationController.getClass().getSimpleName());
    }

}
