package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.model.SimulationStatistics;
import javafx.beans.property.ReadOnlyObjectProperty;

public final class DefaultObservationViewModel<
        ENT extends GridEntity,
        STA extends SimulationStatistics>
        extends AbstractObservationViewModel<STA> {

    public DefaultObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

}
