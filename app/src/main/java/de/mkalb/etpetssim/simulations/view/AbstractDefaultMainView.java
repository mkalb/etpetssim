package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.model.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("StringConcatenationMissingWhitespace")
public abstract class AbstractDefaultMainView<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends AbstractTimedSimulationStatistics,
        CFV extends SimulationConfigView,
        OV extends AbstractObservationView<STA, DefaultObservationViewModel<ENT, STA>>>
        extends
        AbstractMainView<
                CON,
                STA,
                DefaultMainViewModel<ENT, GM, CON, STA>,
                CFV,
                DefaultControlView,
                OV> {

    private static final Color SKIP_OVERLAY_TEXT_COLOR = FXPaintFactory.adjustColorAlpha(FXPaintFactory.BORDER_COLOR, 0.95);
    private static final Color SKIP_OVERLAY_BACKGROUND_COLOR = FXPaintFactory.adjustColorAlpha(FXPaintFactory.BACKGROUND_COLOR, 0.95);
    private static final String SKIP_OVERLAY_SYMBOL = "⏳"; // Hourglass Not Done
    private static final double SKIP_FONT_SIZE = 48.0d;
    private static final double SKIP_PADDING = 24.0d;

    /**
     * Only used for debugging purposes to log draw calls and performance.
     */
    private static final boolean DEBUG_MODE = false;

    private static final int DRAW_THROTTLER_HISTORY_SIZE = 3;
    private static final int DRAW_THROTTLER_MAX_SKIPS = 4;

    private final DrawCallThrottler drawThrottler = new DrawCallThrottler(DRAW_THROTTLER_HISTORY_SIZE, DRAW_THROTTLER_MAX_SKIPS);

    private int lastDrawnStepCount = Integer.MIN_VALUE;

    private boolean skipOverlayActive = false;

    protected AbstractDefaultMainView(DefaultMainViewModel<ENT, GM, CON, STA> viewModel,
                                      CFV configView, DefaultControlView controlView, OV observationView,
                                      GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, configView, controlView, observationView, entityDescriptorRegistry);
    }

    @Override
    protected final void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(this::handleSimulationInitialized);
        viewModel.setSimulationStepListener(this::handleSimulationStep);
        viewModel.selectedGridCellProperty().addListener((_, oldGridCell, newGridCell) -> {
            if (overlayPainter != null) {
                handleGridCellSelected(overlayPainter, oldGridCell, newGridCell);
            }
        });
    }

    @Override
    protected void handleMouseClickedCoordinate(Point2D mousePoint, GridCoordinate mouseCoordinate, FXGridCanvasPainter painter) {
        viewModel.updateClickedCoordinateProperties(mouseCoordinate);
    }

    protected final void handleSimulationInitialized() {
        int stepCount = viewModel.getStepCount();

        var cellDimension = createPainterAndUpdateCanvas(viewModel.getStructure(), viewModel.getCellEdgeLength());

        updateCanvasBorderPane(viewModel.getStructure());

        controlView.updateStepCount(stepCount);
        observationView.updateObservationLabels();

        initSimulation(viewModel.getCurrentConfig(), cellDimension);

        drawAndMeasureSimulationStep(stepCount);

        if (DEBUG_MODE) {
            AppLogger.info("MainView: Simulation initialized and drawn in the view. step=" + stepCount);
        }
    }

    protected final void handleSimulationStep(SimulationStepEvent simulationStepEvent) {
        int stepCount = simulationStepEvent.stepCount();
        if (simulationStepEvent.batchModeRunning()) {
            if (DEBUG_MODE) {
                AppLogger.info("MainView: Handle simulation step at view for running batch mode. " + simulationStepEvent);
            }

            controlView.updateStepCount(stepCount);
        } else {
            if (DEBUG_MODE) {
                AppLogger.info("MainView: Handle simulation step at view. " + simulationStepEvent);
            }

            controlView.updateStepCount(stepCount);

            observationView.updateObservationLabels();

            // Never draw the same step twice
            if (lastDrawnStepCount != stepCount) {
                throttleAndDrawSimulationStep(stepCount, simulationStepEvent.finalStep(), viewModel.getThrottleDrawMillis());
            } else if (DEBUG_MODE) {
                AppLogger.info("MainView: Skipping draw for step because it was already drawn. " + simulationStepEvent);
            }
        }
    }

    protected abstract void handleGridCellSelected(FXGridCanvasPainter painter,
                                                   @Nullable GridCell<ENT> oldGridCell,
                                                   @Nullable GridCell<ENT> newGridCell);

    private void drawAndMeasureSimulationStep(int stepCount) {
        long start = System.currentTimeMillis();
        drawSimulation(viewModel.getCurrentModel(), stepCount, lastDrawnStepCount);
        long duration = System.currentTimeMillis() - start;
        drawThrottler.recordDuration(duration);

        lastDrawnStepCount = stepCount;

        if (DEBUG_MODE) {
            AppLogger.info("MainView: Drawn step " + stepCount +
                    " in " + duration + "ms. Average draw time: " + drawThrottler.getAverageDuration() + "ms");
        }
    }

    private void throttleAndDrawSimulationStep(int stepCount, boolean finalStep, long throttleDrawMillis) {
        if (finalStep || !drawThrottler.shouldSkip(throttleDrawMillis)) {
            clearSkipOverlay();
            drawAndMeasureSimulationStep(stepCount);
        } else {
            showSkipOverlay();
            if (DEBUG_MODE) {
                AppLogger.warn("MainView: Skipping draw for step " + stepCount +
                        " due to high average draw time. Average: " + drawThrottler.getAverageDuration() +
                        "ms, Threshold: " + throttleDrawMillis + "ms");
            }
        }
    }

    private void showSkipOverlay() {
        if (!skipOverlayActive && (overlayPainter != null)) {
            overlayPainter.clearCanvasBackground();

            Point2D center = computeVisibleCanvasCenter(overlayCanvas);

            overlayPainter.drawCenteredTextWithBackgroundAt(
                    center,
                    SKIP_OVERLAY_SYMBOL,
                    SKIP_OVERLAY_TEXT_COLOR,
                    getPreferredFont(SKIP_FONT_SIZE),
                    SKIP_OVERLAY_BACKGROUND_COLOR,
                    SKIP_PADDING
            );
        }
        skipOverlayActive = true;
    }

    private void clearSkipOverlay() {
        if (skipOverlayActive && (overlayPainter != null)) {
            overlayPainter.clearCanvasBackground();
        }
        skipOverlayActive = false;
    }

    protected abstract void initSimulation(CON config, CellDimension cellDimension);

    protected abstract void drawSimulation(GM currentModel, int stepCount, int lastDrawnStepCount);

}
