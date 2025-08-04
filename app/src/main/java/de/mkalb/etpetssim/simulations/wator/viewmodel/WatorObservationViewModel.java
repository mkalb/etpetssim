package de.mkalb.etpetssim.simulations.wator.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import javafx.beans.property.ReadOnlyObjectProperty;

public final class WatorObservationViewModel extends AbstractObservationViewModel<WatorStatistics> {

    public WatorObservationViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState);
    }

}