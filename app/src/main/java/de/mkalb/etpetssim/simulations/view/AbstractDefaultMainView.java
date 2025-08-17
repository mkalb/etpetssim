package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.model.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;

public abstract class AbstractDefaultMainView<
        ENT extends GridEntity,
        CON extends SimulationConfig,
        STA extends AbstractTimedSimulationStatistics,
        CFV extends SimulationConfigView,
        OV extends AbstractObservationView<STA, DefaultObservationViewModel<STA>>>
        extends
        AbstractMainView<
                CON,
                STA,
                DefaultMainViewModel<ENT, CON, STA>,
                CFV,
                DefaultControlView,
                OV> {

    protected AbstractDefaultMainView(DefaultMainViewModel<ENT, CON, STA> viewModel,
                                      CFV configView, DefaultControlView controlView, OV observationView,
                                      GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, configView, controlView, observationView, entityDescriptorRegistry);

    }

    @Override
    protected final void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(this::initializeSimulationCanvas);
        viewModel.setSimulationStepListener(this::updateSimulationStep);
    }

    protected abstract void initializeSimulationCanvas();

    protected abstract void updateSimulationStep(SimulationStepEvent simulationStepEvent);

}
