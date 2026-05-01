package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Contract for simulation control state shared with control views.
 */
public interface SimulationControlViewModel {

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

}
