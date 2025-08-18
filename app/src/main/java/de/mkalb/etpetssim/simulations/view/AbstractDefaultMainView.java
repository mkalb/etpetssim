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

@SuppressWarnings("StringConcatenationMissingWhitespace")
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

    /**
     * Only used for debugging purposes to log draw calls and performance.
     */
    private static final boolean DEBUG_MODE = false;

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
        int stepCount = viewModel.getStepCount();

        initSimulation(viewModel.getCurrentConfig());

        createPainterAndUpdateCanvas(viewModel.getStructure(), viewModel.getCellEdgeLength());
        updateCanvasBorderPane(viewModel.getStructure());

        controlView.updateStepCount(stepCount);
        observationView.updateObservationLabels();
        drawAndMeasureSimulationStep(stepCount);

        if (DEBUG_MODE) {
            AppLogger.info("Simulation initialized and drawn in the view. step=" + stepCount);
        }
    }

    protected final void handleSimulationStep(SimulationStepEvent simulationStepEvent) {
        int stepCount = simulationStepEvent.stepCount();
        if (simulationStepEvent.batchModeRunning()) {
            if (DEBUG_MODE) {
                AppLogger.info("Handle simulation step at view for batch mode. " + simulationStepEvent);
            }

            controlView.updateStepCount(stepCount);
        } else {
            if (DEBUG_MODE) {
                AppLogger.info("Handle simulation step at view for live mode. " + simulationStepEvent);
            }

            controlView.updateStepCount(stepCount);

            observationView.updateObservationLabels();

            // Never draw the same step twice
            if (lastDrawnStepCount != stepCount) {
                throttleAndDrawSimulationStep(stepCount, simulationStepEvent.finalStep(), viewModel.getThrottleDrawMillis());
            } else if (DEBUG_MODE) {
                AppLogger.info("Skipping draw for step because it was already drawn. " + simulationStepEvent);
            }
        }
    }

    private void drawAndMeasureSimulationStep(int stepCount) {
        long start = System.currentTimeMillis();
        drawSimulation(viewModel.getCurrentModel(), stepCount);
        long duration = System.currentTimeMillis() - start;
        drawThrottler.recordDuration(duration);

        lastDrawnStepCount = stepCount;

        if (DEBUG_MODE) {
            AppLogger.info("Drawn step " + stepCount +
                    " in " + duration + "ms. Average draw time: " + drawThrottler.getAverageDuration() + "ms");
        }
    }

    private void throttleAndDrawSimulationStep(int stepCount, boolean finalStep, long throttleDrawMillis) {
        if (finalStep || !drawThrottler.shouldSkip(stepCount, throttleDrawMillis)) {
            drawAndMeasureSimulationStep(stepCount);
        } else {
            if (DEBUG_MODE) {
                AppLogger.warn("Skipping draw for step " + stepCount +
                        " due to high average draw time. Average: " + drawThrottler.getAverageDuration() +
                        "ms, Threshold: " + throttleDrawMillis + "ms");
            }
        }
    }

    protected abstract void initSimulation(CON config);

    protected abstract void drawSimulation(ReadableGridModel<ENT> currentModel, int stepCount);

}
