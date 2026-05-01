package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Contract for exposing simulation configuration state to the view layer.
 */
public interface SimulationConfigViewModel<CON extends SimulationConfig> {

    /**
     * Exposes the current simulation state as read-only JavaFX property.
     *
     * @return read-only simulation state property
     */
    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    /**
     * Returns the current simulation state.
     *
     * @return current simulation state
     */
    SimulationState getSimulationState();

    /**
     * Returns the immutable simulation configuration.
     *
     * @return simulation configuration
     */
    CON getConfig();

}
