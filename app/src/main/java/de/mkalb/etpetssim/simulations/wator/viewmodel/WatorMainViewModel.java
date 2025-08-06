package de.mkalb.etpetssim.simulations.wator.viewmodel;

import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractTimedMainViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.*;
import javafx.beans.property.ObjectProperty;

public final class WatorMainViewModel
        extends AbstractTimedMainViewModel<WatorEntity, WatorConfig, WatorStatistics> {

    private final WatorConfigViewModel configViewModel;
    private final DefaultControlViewModel controlViewModel;
    private final DefaultObservationViewModel<WatorStatistics> observationViewModel;

    public WatorMainViewModel(ObjectProperty<SimulationState> simulationState,
                              WatorConfigViewModel configViewModel,
                              DefaultControlViewModel controlViewModel,
                              DefaultObservationViewModel<WatorStatistics> observationViewModel) {
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
    protected void updateObservationStatistics(WatorStatistics statistics) {
        observationViewModel.setStatistics(statistics);
    }

    @Override
    protected WatorSimulationManager createSimulationManager() {
        return new WatorSimulationManager(configViewModel.getConfig());
    }

}
