package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * Contract for exposing simulation configuration state to the view layer.
 *
 * @param <CON> immutable simulation config type
 */
public interface SimulationConfigViewModel<CON extends SimulationConfig> {

    ReadOnlyObjectProperty<SimulationState> simulationStateProperty();

    SimulationState getSimulationState();

    /**
     * Creates an immutable simulation configuration from the current input values.
     * <p>
     * The configuration may be invalid; callers check it with {@link SimulationConfig#isValid()}.
     *
     * @return simulation configuration
     */
    CON createConfig();

}
