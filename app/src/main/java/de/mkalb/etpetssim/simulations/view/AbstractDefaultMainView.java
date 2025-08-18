package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
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

    private static final int DRAW_THROTTLER_HISTORY_SIZE = 5;

    private final DrawCallThrottler drawThrottler = new DrawCallThrottler(DRAW_THROTTLER_HISTORY_SIZE);

    private int lastDrawnStepCount = Integer.MIN_VALUE;

    protected AbstractDefaultMainView(DefaultMainViewModel<ENT, CON, STA> viewModel,
                                      CFV configView, DefaultControlView controlView, OV observationView,
                                      GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, configView, controlView, observationView, entityDescriptorRegistry);
    }

    @Override
    protected final void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(this::handleSimulationInitialized);
        viewModel.setSimulationStepListener(this::handleSimulationStep);
    }

    protected final void handleSimulationInitialized() {
        double cellEdgeLength = viewModel.getCellEdgeLength();
        ReadableGridModel<ENT> currentModel = viewModel.getCurrentModel();
        GridStructure structure = viewModel.getStructure();
        int stepCount = viewModel.getStepCount();
        CON config = viewModel.getCurrentConfig();

        initSimulation(config);

        createPainterAndUpdateCanvas(structure, cellEdgeLength);
        updateCanvasBorderPane(structure);

        controlView.updateStepCount(stepCount);

        observationView.updateObservationLabels();
        drawSimulation(currentModel, stepCount);
    }

    protected final void handleSimulationStep(SimulationStepEvent simulationStepEvent) {
        int stepCount = simulationStepEvent.stepCount();
        if (simulationStepEvent.batchModeRunning()) {
            // AppLogger.info("Updating view for batch mode step " + simulationStepEvent.stepCount());

            controlView.updateStepCount(stepCount);
        } else {
            // AppLogger.info("Drawing canvas for step " + simulationStepEvent.stepCount());

            controlView.updateStepCount(stepCount);

            observationView.updateObservationLabels();

            // Never draw the same step twice
            if (lastDrawnStepCount != stepCount) {
                throttleAndDrawSimulationStep(stepCount, simulationStepEvent.finalStep(), viewModel.getThrottleDrawMillis());
            }
        }
    }

    private void throttleAndDrawSimulationStep(int stepCount, boolean finalStep, long throttleDrawMillis) {
        if (finalStep || !drawThrottler.shouldSkip(stepCount, throttleDrawMillis)) {
            long start = System.currentTimeMillis();
            drawSimulation(viewModel.getCurrentModel(), stepCount);
            long duration = System.currentTimeMillis() - start;
            drawThrottler.recordDuration(duration);

            lastDrawnStepCount = stepCount;
        } else {
            AppLogger.warn("Skipping draw for step " + stepCount +
                    " due to high average draw time. Average: " + drawThrottler.getAverageDuration() +
                    "ms, Threshold: " + throttleDrawMillis + "ms");
        }
    }

    protected abstract void initSimulation(CON config);

    protected abstract void drawSimulation(ReadableGridModel<ENT> currentModel, int stepCount);

}
