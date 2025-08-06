package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractTimedMainViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import javafx.beans.property.ObjectProperty;

public final class ConwayMainViewModel
        extends AbstractTimedMainViewModel<ConwayEntity, ConwayConfig, ConwayStatistics> {

    private final ConwayConfigViewModel configViewModel;
    private final DefaultControlViewModel controlViewModel;
    private final DefaultObservationViewModel<ConwayStatistics> observationViewModel;

    public ConwayMainViewModel(ObjectProperty<SimulationState> simulationState,
                               ConwayConfigViewModel configViewModel,
                               DefaultControlViewModel controlViewModel,
                               DefaultObservationViewModel<ConwayStatistics> observationViewModel) {
        super(simulationState);
        this.configViewModel = configViewModel;
        this.controlViewModel = controlViewModel;
        this.observationViewModel = observationViewModel;

        controlViewModel.actionButtonRequestedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                handleActionButton();
                controlViewModel.actionButtonRequestedProperty().set(false); // reset
            }
        });
        controlViewModel.cancelButtonRequestedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                handleCancelButton();
                controlViewModel.cancelButtonRequestedProperty().set(false); // reset
            }
        });
    }

    @Override
    public double getCellEdgeLength() {
        return configViewModel.cellEdgeLengthProperty().getValue();
    }

    @Override
    protected double getStepDuration() {
        return controlViewModel.stepDurationProperty().getValue();
    }

    @Override
    protected void updateObservationStatistics(ConwayStatistics statistics) {
        observationViewModel.setStatistics(statistics);
    }

    @Override
    protected ConwaySimulationManager createSimulationManager() {
        return new ConwaySimulationManager(configViewModel.getConfig());
    }

}
