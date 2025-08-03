package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.simulations.view.SimulationView;
import javafx.scene.layout.Region;

/**
 * Represents an instance of a simulation, encapsulating the type of simulation,
 * the view managing it, and the view region for the simulation.
 *
 * @param simulationType the type of simulation
 * @param simulationView the view managing the simulation
 * @param region the view region for the simulation
 */
public record SimulationInstance(
        SimulationType simulationType,
        SimulationView simulationView,
        Region region) {

    static SimulationInstance of(SimulationType type, SimulationView simulationView) {
        return new SimulationInstance(type, simulationView, simulationView.buildRegion());
    }

    /**
     * Returns a concise string representation of this simulation instance.
     * <p>
     * Format: {@code [SIMULATION_TYPE, VIEW_CLASS]}
     * <br>
     * Example: {@code [CONWAYS_LIFE, ConwayMainView]}
     *
     * @return a concise display string for this simulation instance
     */
    public String toDisplayString() {
        return String.format("[%s, %s]", simulationType, simulationView.getClass().getSimpleName());
    }

}
