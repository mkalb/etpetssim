package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.TimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Abstract base of the standard main view family for regular 2D simulations.
 * <p>
 * Binds {@link DefaultMainViewModel} and provides drawing and throttling support.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GC>  grid cell type used for cell selection
 * @param <GM>  grid model type managed by the simulation
 * @param <CON> immutable simulation config type
 * @param <STA> timed statistics type
 * @param <SM>  concrete simulation manager type
 * @param <CTX> simulation-specific user action context type
 * @param <CFV> config view type
 * @param <OV>  observation view type
 */
public abstract class AbstractDefaultMainView<
        ENT extends GridEntity,
        GC extends GridCellView<ENT>,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics,
        SM extends AbstractTimedSimulationManager<ENT, GM, CON, STA>,
        CTX extends SimulationUserActionContext,
        CFV extends SimulationConfigView,
        OV extends AbstractObservationView<ENT, GC, STA, DefaultObservationViewModel<ENT, GC, STA>>>
        extends
        AbstractMainView<
                DefaultMainViewModel<ENT, GC, GM, CON, STA, SM, CTX>,
                CTX,
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

    private @Nullable ChangeListener<@Nullable GC> selectedGridCellListener;

    protected AbstractDefaultMainView(DefaultMainViewModel<ENT, GC, GM, CON, STA, SM, CTX> viewModel,
                                      CFV configView, SimulationControlView controlView, OV observationView,
                                      GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, configView, controlView, observationView, entityDescriptorRegistry);
    }

    @Override
    protected final void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(this::handleSimulationInitialized);
        viewModel.setSimulationResetListener(this::handleSimulationReset);
        viewModel.setSimulationStepListener(this::handleSimulationStep);
        selectedGridCellListener = (_, oldGridCell, newGridCell) -> {
            if (overlayPainter != null) {
                handleSelectedGridCellChanged(overlayPainter, oldGridCell, newGridCell);
            }
        };
        viewModel.selectedGridCellProperty().addListener(selectedGridCellListener);
    }

    @Override
    public SimulationTerminationHandle shutdownSimulation() {
        if (selectedGridCellListener != null) {
            viewModel.selectedGridCellProperty().removeListener(selectedGridCellListener);
            selectedGridCellListener = null;
        }
        return super.shutdownSimulation();
    }

    @SuppressWarnings("MagicNumber")
    protected final double computeStrokeLineWidth(CellDimension cellDimension) {
        if (cellDimension.innerRadius() < 2.0d) {
            return NO_STROKE_LINE_WIDTH;
        }
        return Math.log(cellDimension.innerRadius());
    }

    /**
     * Computes brightness variants of the descriptor color for value groups between {@code min} and {@code max}.
     *
     * @param descriptor     descriptor whose color is varied
     * @param min            lower bound of the values (inclusive)
     * @param max            upper bound of the values (inclusive)
     * @param groupCount     number of brightness groups
     * @param maxFactorDelta total brightness change (positive to brighten, negative to darken)
     * @return the color variants by value, or {@code null} if the descriptor color is not a {@link Color}
     * @see FXPaintFactory#computeBrightnessVariantsMap(Color, int, int, int, double)
     */
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
        applySelectedCellUserActionAndRedraw();
    }

    @Override
    protected final BooleanProperty editModeActiveProperty() {
        return viewModel.editModeActiveProperty();
    }

    @Override
    protected final ObjectProperty<String> selectedUserActionToolIdProperty() {
        return viewModel.selectedUserActionToolIdProperty();
    }

    @Override
    protected final void applyGlobalUserActionAndRedraw(SimulationUserActionDescriptor<CTX> descriptor) {
        boolean changed = viewModel.applyUserAction(descriptor);
        if (changed) {
            redrawAfterUserAction();
        }
    }

    private void applySelectedCellUserActionAndRedraw() {
        boolean changed = viewModel.applySelectedCellUserAction(createUserActionDescriptors());
        if (changed) {
            redrawAfterUserAction();
        }
    }

    private void redrawAfterUserAction() {
        observationView.updateObservationLabels();
        drawSimulation(viewModel.getCurrentGridModel(), viewModel.getStepCount(), viewModel.getStepCount());
    }

    private void handleSimulationInitialized() {
        int stepCount = viewModel.getStepCount();

        AppLogger.infof("%s: Starting simulation view initialization. step=%d", LOG_COMPONENT, stepCount);

        var cellDimension = recreatePaintersAndResizeCanvases(viewModel.getStructure(), viewModel.getCellEdgeLength());

        updateCanvasBorderPane(viewModel.getStructure());

        rebuildEditToolBar();

        controlView.setStepCount(stepCount);
        observationView.initializeObservationLabels();

        initializeSimulation(viewModel.getCurrentConfig(), cellDimension, viewModel.getCurrentGridModel());

        AppLogger.infof("%s: Simulation initialized in the view. step=%d", LOG_COMPONENT, stepCount);

        drawAndMeasureSimulationStep(stepCount);

        AppLogger.infof("%s: Initial simulation view draw completed. step=%d", LOG_COMPONENT, stepCount);
    }

    private void handleSimulationReset() {
        AppLogger.infof("%s: Resetting simulation view.", LOG_COMPONENT);
        clearCanvasLayers();
        controlView.setStepCount(0);
        observationView.initializeObservationLabels();
    }

    private void clearCanvasLayers() {
        if (basePainter != null) {
            basePainter.clearCanvasBackground();
        }
        if (dynamicPainter != null) {
            dynamicPainter.clearCanvasBackground();
        }
        if (overlayPainter != null) {
            overlayPainter.clearCanvasBackground();
        }
        skipOverlayActive = false;
    }

    private void handleSimulationStep(SimulationStepEvent simulationStepEvent) {
        int stepCount = simulationStepEvent.stepCount();
        if (simulationStepEvent.batchModeRunning()) {
            if (DEBUG_MODE) {
                AppLogger.infof("%s: Handle simulation step in view for batch mode. event=%s", LOG_COMPONENT, simulationStepEvent);
            }

            controlView.setStepCount(stepCount);
        } else {
            if (DEBUG_MODE) {
                AppLogger.infof("%s: Handle simulation step in view. event=%s", LOG_COMPONENT, simulationStepEvent);
            }

            controlView.setStepCount(stepCount);

            observationView.updateObservationLabels();

            // Never draw the same step twice
            if (lastDrawnStepCount != stepCount) {
                throttleAndDrawSimulationStep(stepCount, simulationStepEvent.finalStep(), viewModel.getThrottleDrawMillis());
            } else if (DEBUG_MODE) {
                AppLogger.infof("%s: Skipping draw because step was already drawn. event=%s", LOG_COMPONENT, simulationStepEvent);
            }
        }
    }

    protected abstract void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
                                                          @Nullable GC oldGridCell,
                                                          @Nullable GC newGridCell);

    private void drawAndMeasureSimulationStep(int stepCount) {
        long startNanos = System.nanoTime();
        drawSimulation(viewModel.getCurrentGridModel(), stepCount, lastDrawnStepCount);
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
                AppLogger.infof("%s: Skipping draw for step %d due to high average draw time. averageMillis=%d, thresholdMillis=%d",
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
                    resolvePreferredFont(SKIP_FONT_SIZE),
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

    protected abstract void initializeSimulation(CON config, CellDimension cellDimension, GM model);

    /**
     * Draws the current simulation state.
     *
     * @param currentGridModel   the grid model to draw
     * @param stepCount          the step count of the grid model
     * @param lastDrawnStepCount the step count of the previous draw, {@link Integer#MIN_VALUE} before the first draw,
     *                           or {@code stepCount} when the same step is redrawn after a user action; a gap greater
     *                           than {@code 1} means that intermediate steps were not drawn
     */
    @SuppressWarnings("ParameterHidesMemberVariable")
    protected abstract void drawSimulation(GM currentGridModel, int stepCount, int lastDrawnStepCount);

}
