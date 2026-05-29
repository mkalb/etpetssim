package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.TimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;

/**
 * Default main view base with drawing and throttling support.
 */
public abstract class AbstractDefaultMainView<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics,
        CFV extends SimulationConfigView,
        OV extends AbstractObservationView<ENT, STA, DefaultObservationViewModel<ENT, STA>>>
        extends
        AbstractMainView<
                DefaultMainViewModel<ENT, GM, CON, STA>,
                CFV,
                SimulationControlView,
                OV> {

    protected static final double NO_STROKE_LINE_WIDTH = 0.0d;

    private static final Color SKIP_OVERLAY_TEXT_COLOR = FXPaintFactory.adjustColorAlpha(FXPaintFactory.BORDER_COLOR, 0.95);
    private static final Color SKIP_OVERLAY_BACKGROUND_COLOR = FXPaintFactory.adjustColorAlpha(FXPaintFactory.BACKGROUND_COLOR, 0.95);
    private static final String SKIP_OVERLAY_SYMBOL = "⏳"; // Hourglass Not Done
    private static final double SKIP_FONT_SIZE = 48.0d;
    private static final double SKIP_PADDING = 24.0d;

    /**
     * Only used for debugging purposes to log draw calls and performance.
     */
    private static final boolean DEBUG_MODE = false;
    private static final String LOG_COMPONENT = "MainView";

    private static final int DRAW_THROTTLER_HISTORY_SIZE = 3;
    private static final int DRAW_THROTTLER_MAX_SKIPS = 4;

    private final DrawCallThrottler drawThrottler = new DrawCallThrottler(DRAW_THROTTLER_HISTORY_SIZE, DRAW_THROTTLER_MAX_SKIPS);

    private int lastDrawnStepCount = Integer.MIN_VALUE;

    private boolean skipOverlayActive = false;

    protected AbstractDefaultMainView(DefaultMainViewModel<ENT, GM, CON, STA> viewModel,
                                      CFV configView, SimulationControlView controlView, OV observationView,
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

    @SuppressWarnings("MagicNumber")
    protected final double computeStrokeLineWidth(CellDimension cellDimension) {
        if (cellDimension.innerRadius() < 2.0d) {
            return NO_STROKE_LINE_WIDTH;
        }
        return Math.log(cellDimension.innerRadius());
    }

    @Nullable
    protected final Map<Integer, Color> computeBrightnessVariantsMap(GridEntityDescriptor descriptor,
                                                                     int min,
                                                                     int max,
                                                                     int groupCount,
                                                                     double maxFactorDelta) {
        if (!(descriptor.color() instanceof Color baseColor)) {
            return null;
        }
        return FXPaintFactory.computeBrightnessVariantsMap(baseColor, min, max, groupCount, maxFactorDelta);
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
        observationView.initializeObservationLabels();

        initSimulation(viewModel.getCurrentConfig(), cellDimension);

        drawAndMeasureSimulationStep(stepCount);

        if (DEBUG_MODE) {
            AppLogger.infof("%s: Simulation initialized and drawn in the view. step=%d", LOG_COMPONENT, stepCount);
        }
    }

    protected final void handleSimulationStep(SimulationStepEvent simulationStepEvent) {
        int stepCount = simulationStepEvent.stepCount();
        if (simulationStepEvent.batchModeRunning()) {
            if (DEBUG_MODE) {
                AppLogger.infof("%s: Handle simulation step in view for batch mode. event=%s", LOG_COMPONENT, simulationStepEvent);
            }

            controlView.updateStepCount(stepCount);
        } else {
            if (DEBUG_MODE) {
                AppLogger.infof("%s: Handle simulation step in view. event=%s", LOG_COMPONENT, simulationStepEvent);
            }

            controlView.updateStepCount(stepCount);

            observationView.updateObservationLabels();

            // Never draw the same step twice
            if (lastDrawnStepCount != stepCount) {
                throttleAndDrawSimulationStep(stepCount, simulationStepEvent.finalStep(), viewModel.getThrottleDrawMillis());
            } else if (DEBUG_MODE) {
                AppLogger.infof("%s: Skipping draw because step was already drawn. event=%s", LOG_COMPONENT, simulationStepEvent);
            }
        }
    }

    protected abstract void handleGridCellSelected(FXGridCanvasPainter painter,
                                                   @Nullable GridCell<ENT> oldGridCell,
                                                   @Nullable GridCell<ENT> newGridCell);

    private void drawAndMeasureSimulationStep(int stepCount) {
        long startNanos = System.nanoTime();
        drawSimulation(viewModel.getCurrentModel(), stepCount, lastDrawnStepCount);
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        drawThrottler.recordDurationMillis(durationMillis);

        lastDrawnStepCount = stepCount;

        if (DEBUG_MODE) {
            AppLogger.infof("%s: Drawn step %d in %dms. averageDrawMillis=%d",
                    LOG_COMPONENT,
                    stepCount,
                    durationMillis,
                    drawThrottler.getAverageDurationMillis());
        }
    }

    private void throttleAndDrawSimulationStep(int stepCount, boolean finalStep, long throttleDrawMillis) {
        if (finalStep || !drawThrottler.shouldSkip(throttleDrawMillis)) {
            clearSkipOverlay();
            drawAndMeasureSimulationStep(stepCount);
        } else {
            showSkipOverlay();
            if (DEBUG_MODE) {
                AppLogger.warnf("%s: Skipping draw for step %d due to high average draw time. averageMillis=%d, thresholdMillis=%d",
                        LOG_COMPONENT,
                        stepCount,
                        drawThrottler.getAverageDurationMillis(),
                        throttleDrawMillis);
            }
        }
    }

    private void showSkipOverlay() {
        if ((overlayPainter != null) && !skipOverlayActive) {
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
            skipOverlayActive = true;
        }
    }

    private void clearSkipOverlay() {
        if (skipOverlayActive && (overlayPainter != null)) {
            overlayPainter.clearCanvasBackground();
        }
        skipOverlayActive = false;
    }

    protected abstract void initSimulation(CON config, CellDimension cellDimension);

    @SuppressWarnings("ParameterHidesMemberVariable")
    protected abstract void drawSimulation(GM currentModel, int stepCount, int lastDrawnStepCount);

}
