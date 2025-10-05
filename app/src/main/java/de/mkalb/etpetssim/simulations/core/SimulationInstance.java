package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import javafx.scene.layout.Region;

/**
 * Represents an instance of a simulation, encapsulating the type of simulation,
 * the view managing it, and the view region for the simulation.
 *
 * @param simulationType the type of simulation
 * @param simulationMainView the view managing the simulation lifecycle and UI composition
 * @param region the root UI region for the simulation
 */
public record SimulationInstance(
        SimulationType simulationType,
        SimulationMainView simulationMainView,
        Region region) {

    /**
     * Creates a new {@code SimulationInstance} for the given type and main view.
     * The region is built via {@link SimulationMainView#buildMainRegion()}.
     *
     * @param type the simulation type
     * @param simulationMainView the main view responsible for building the UI
     * @return a new instance bundling type, view, and its main region
     */
    static SimulationInstance of(SimulationType type, SimulationMainView simulationMainView) {
        return new SimulationInstance(type, simulationMainView, simulationMainView.buildMainRegion());
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
        return String.format("[%s, %s]", simulationType, simulationMainView.getClass().getSimpleName());
    }

}
