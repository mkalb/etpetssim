package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
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

    protected final void updateSimulationStep(SimulationStepEvent simulationStepEvent) {
        if (simulationStepEvent.batchModeRunning()) {
            // TODO handle batch mode
            AppLogger.info("Updating view for batch mode step " + simulationStepEvent.stepCount());

            controlView.updateStepCount(simulationStepEvent.stepCount());
        } else {
            AppLogger.info("Drawing canvas for step " + simulationStepEvent.stepCount());

            controlView.updateStepCount(simulationStepEvent.stepCount());
            observationView.updateObservationLabels();

            drawCanvas(viewModel.getCurrentModel(), simulationStepEvent.stepCount());
        }
    }

    protected abstract void drawCanvas(ReadableGridModel<ENT> currentModel, int stepCount);

}
