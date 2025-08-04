package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractObservationViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;

public final class ConwayObservationViewModel extends AbstractObservationViewModel<ConwayStatistics> {

    public ConwayObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

}