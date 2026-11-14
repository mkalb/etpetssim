package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import javafx.scene.layout.Region;

/**
 * Represents the screen of a shown simulation, encapsulating the type of simulation,
 * the view managing it, and the view region for the simulation.
 *
 * <p>The record groups the view and its region without copying them; the caller controls the lifecycle through
 * {@link SimulationMainView#shutdownSimulation()}.
 *
 * @param simulationType     the type of simulation
 * @param simulationMainView the view managing the simulation lifecycle and UI composition
 * @param region             the root UI region for the simulation
 */
public record SimulationScreen(
        SimulationType simulationType,
        SimulationMainView simulationMainView,
        Region region) {

    /**
     * Creates a new {@code SimulationScreen} for the given type and main view.
     * The region is built via {@link SimulationMainView#buildMainRegion()}.
     *
     * <p>Callers must ensure that the provided type and view are compatible; used by {@link SimulationFactory}.
     *
     * @param simulationType     the simulation type
     * @param simulationMainView the main view responsible for building the UI
     * @return a new screen bundling type, view, and its main region
     */
    static SimulationScreen of(SimulationType simulationType, SimulationMainView simulationMainView) {
        return new SimulationScreen(simulationType, simulationMainView, simulationMainView.buildMainRegion());
    }

}
