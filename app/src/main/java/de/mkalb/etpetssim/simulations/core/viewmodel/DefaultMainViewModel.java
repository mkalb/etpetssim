package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.TimedSimulationStatistics;
import de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStepEvent;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Default main view-model implementation that orchestrates timed and batch execution.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GM> grid model type managed by the simulation
 * @param <CON> immutable simulation config type
 * @param <STA> timed statistics type exposed to observation views
 */
public final class DefaultMainViewModel<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics>
        extends AbstractMainViewModel<ENT, GM, CON, STA> {

    private static final double TIMEOUT_EXECUTE_FACTOR = 0.4d;
    private static final double TIMEOUT_VIEW_FACTOR = 0.5d;
    private static final double THROTTLE_DRAW_FACTOR = 0.3d;
    private static final String LOG_COMPONENT = "DefaultMainViewModel";

    private final DefaultControlViewModel controlViewModel;
    private final DefaultObservationViewModel<ENT, STA> observationStateViewModel;
    private final Function<CON, AbstractTimedSimulationManager<ENT, GM, CON, STA>> simulationManagerFactory;
    private final SimulationTimer timer;
    private final ExecutorService batchExecutor;
    private final ChangeListener<Boolean> actionButtonRequestedListener;
    private final ChangeListener<Boolean> cancelButtonRequestedListener;
    private final @Nullable ChangeListener<@Nullable GridCoordinate> lastClickedCoordinateListener;
    private final ObjectProperty<@Nullable GridCell<ENT>> selectedGridCell = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable GridCoordinate> lastSelectedCoordinate = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable ENT> lastSelectedEntity = new SimpleObjectProperty<>();
    private @Nullable AbstractTimedSimulationManager<ENT, GM, CON, STA> simulationManager;
    private @Nullable Future<?> batchFuture;
    private volatile @Nullable Thread batchThread;
    private long timeoutExecuteNanos = Long.MAX_VALUE;
    private long timeoutViewMillis = Long.MAX_VALUE;
    private long throttleDrawMillis = Long.MAX_VALUE;

    // Listener for view
    private Runnable simulationInitializedListener = () -> {};
    private Consumer<SimulationStepEvent> simulationStepListener = _ -> {};

    /**
     * Creates a main view model without cell-selection support.
     *
     * @param simulationState shared simulation state property
     * @param configViewModel config view model
     * @param controlViewModel control view model
     * @param observationViewModel observation view model
     * @param simulationManagerFactory factory used to initialize simulation managers
     */
    public DefaultMainViewModel(ObjectProperty<SimulationState> simulationState,
                                SimulationConfigViewModel<CON> configViewModel,
                                DefaultControlViewModel controlViewModel,
                                DefaultObservationViewModel<ENT, STA> observationViewModel,
                                Function<CON, AbstractTimedSimulationManager<ENT, GM, CON, STA>> simulationManagerFactory) {
        this(simulationState, configViewModel, controlViewModel, observationViewModel, simulationManagerFactory, null);
    }

    /**
     * Creates a main view model with optional cell-selection support.
     *
     * @param simulationState shared simulation state property
     * @param configViewModel config view model
     * @param controlViewModel control view model
     * @param observationViewModel observation view model
     * @param simulationManagerFactory factory used to initialize simulation managers
     * @param selectedGridCellProvider optional mapping from clicked coordinate to selected cell
     */
    public DefaultMainViewModel(ObjectProperty<SimulationState> simulationState,
                                SimulationConfigViewModel<CON> configViewModel,
                                DefaultControlViewModel controlViewModel,
                                DefaultObservationViewModel<ENT, STA> observationViewModel,
                                Function<CON, AbstractTimedSimulationManager<ENT, GM, CON, STA>> simulationManagerFactory,
                                @Nullable BiFunction<GM, GridCoordinate, GridCell<ENT>> selectedGridCellProvider) {
        super(simulationState, configViewModel, observationViewModel);
        this.controlViewModel = controlViewModel;
        // Keep a concrete-typed reference because the inherited `observationViewModel`
        // is declared as `SimulationObservationViewModel` and does not expose
        // `selectedGridCellProperty()` used during shutdown/unbinding.
        observationStateViewModel = observationViewModel;
        this.simulationManagerFactory = simulationManagerFactory;
        timer = new SimulationTimer(this::runTimerStep);
        batchExecutor = Executors.newSingleThreadExecutor(task -> {
            var thread = new Thread(task, "simulation-batch-executor");
            thread.setDaemon(true);
            return thread;
        });

        actionButtonRequestedListener = (_, _, newVal) -> {
            if (newVal) {
                handleActionButton();
                controlViewModel.actionButtonRequestedProperty().set(false); // reset
            }
        };
        controlViewModel.actionButtonRequestedProperty().addListener(actionButtonRequestedListener);

        cancelButtonRequestedListener = (_, _, newVal) -> {
            if (newVal) {
                handleCancelButton();
                controlViewModel.cancelButtonRequestedProperty().set(false); // reset
            }
        };
        controlViewModel.cancelButtonRequestedProperty().addListener(cancelButtonRequestedListener);

        // Initialize selected grid cell handling if provider is given
        if (selectedGridCellProvider != null) {
            observationViewModel.bindSelectedGridCellProperty(selectedGridCell);
            lastClickedCoordinateListener = ((_, _, newValue) -> {
                if ((newValue != null) && hasSimulationManager() && isSelectionState(getSimulationState())) {
                    try {
                        var cell = selectedGridCellProvider.apply(getCurrentModel(), newValue);
                        selectedGridCell.set(cell);
                        lastSelectedCoordinate.set(cell.coordinate());
                        lastSelectedEntity.set(cell.entity());
                        AppLogger.infof("%s: Cell selected: %s", LOG_COMPONENT, cell.toDisplayString());
                    } catch (RuntimeException e) {
                        AppLogger.errorf(e, "%s: Cannot determine selected cell for coordinate=%s", LOG_COMPONENT, newValue.toDisplayString());
                        selectedGridCell.set(null);
                        lastSelectedCoordinate.set(null);
                        lastSelectedEntity.set(null);
                    }
                } else {
                    selectedGridCell.set(null);
                }
            });
            lastClickedCoordinateProperty().addListener(lastClickedCoordinateListener);
        } else {
            lastClickedCoordinateListener = null;
        }
    }

    private static boolean isSelectionState(SimulationState simulationState) {
        return switch (simulationState) {
            case PAUSED, CANCELED, FINISHED -> true;
            case INITIAL, RUNNING_TIMED, RUNNING_BATCH, PAUSING_BATCH, CANCELLING_BATCH, ERROR, SHUTTING_DOWN -> false;
        };
    }

    /**
     * Exposes the selected grid cell resolved from the latest click.
     *
     * @return selected-cell property, nullable when no cell is selected
     */
    public ObjectProperty<@Nullable GridCell<ENT>> selectedGridCellProperty() {
        return selectedGridCell;
    }

    /**
     * Exposes the coordinate of the last non-null selected cell.
     *
     * @return last-selected coordinate property
     */
    public ObjectProperty<@Nullable GridCoordinate> lastSelectedCoordinateProperty() {
        return lastSelectedCoordinate;
    }

    /**
     * Exposes the entity of the last non-null selected cell.
     *
     * @return last-selected entity property
     */
    public ObjectProperty<@Nullable ENT> lastSelectedEntityProperty() {
        return lastSelectedEntity;
    }

    /**
     * Clears selection-related properties.
     */
    public void resetSelectedProperties() {
        selectedGridCell.set(null);
        lastSelectedCoordinate.set(null);
        lastSelectedEntity.set(null);
    }

    /**
     * Registers a callback invoked after simulation initialization.
     *
     * @param listener callback invoked after manager creation and initial statistics update
     */
    public void setSimulationInitializedListener(Runnable listener) {
        simulationInitializedListener = listener;
    }

    /**
     * Registers a callback invoked for simulation step notifications.
     *
     * @param listener callback receiving step events
     */
    public void setSimulationStepListener(Consumer<SimulationStepEvent> listener) {
        simulationStepListener = listener;
    }

    @Override
    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.structure();
    }

    @Override
    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config().cellEdgeLength();
    }

    @Override
    public void shutdownSimulation() {
        AppLogger.infof("%s: Shutting down simulation during state=%s", LOG_COMPONENT, getSimulationState());
        setSimulationState(SimulationState.SHUTTING_DOWN);

        controlViewModel.actionButtonRequestedProperty().removeListener(actionButtonRequestedListener);
        controlViewModel.cancelButtonRequestedProperty().removeListener(cancelButtonRequestedListener);
        if (lastClickedCoordinateListener != null) {
            lastClickedCoordinateProperty().removeListener(lastClickedCoordinateListener);
        }
        observationViewModel.lastClickedCoordinateProperty().unbind();
        observationStateViewModel.selectedGridCellProperty().unbind();

        resetSelectedProperties();
        resetClickedCoordinateProperties();
        stopTimer();
        cancelBatch();
        shutdownBatchExecutor();
        simulationManager = null;
    }

    @Override
    public CON getCurrentConfig() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config();
    }

    @Override
    public boolean hasSimulationManager() {
        return simulationManager != null;
    }

    @Override
    public GM getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    public int getStepCount() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.stepCount();
    }

    /**
     * Returns the current draw-throttling threshold used by timed-mode rendering.
     *
     * @return draw-throttling threshold in milliseconds
     */
    public long getThrottleDrawMillis() {
        return throttleDrawMillis;
    }

    private void handleActionButton() {
        if (!getSimulationState().isRunning() && (isTimerRunning() || isBatchRunning())) {
            throw new IllegalStateException("Simulation is running but state is not RUNNING_TIMED or RUNNING_BATCH: " + getSimulationState());
        }

        resetClickedCoordinateProperties();

        if (getSimulationState().isStartable()) {
            handleStartAction();
        } else if (getSimulationState().isRunning()) {
            handlePauseAction();
        } else if (getSimulationState().isPaused()) {
            handleResumeAction();
        } else {
            AppLogger.warnf("%s: Cannot handle action button in state=%s", LOG_COMPONENT, getSimulationState());
        }
    }

    private void handleCancelButton() {
        setNotificationType(SimulationNotificationType.NONE);
        resetClickedCoordinateProperties();

        switch (getSimulationState()) {
            case RUNNING_TIMED -> {
                stopTimer();
                setSimulationState(SimulationState.CANCELED);
                logSimulationInfo("Simulation (timer) was canceled by the user.");
                int stepCount = (simulationManager != null) ? simulationManager.stepCount() : 0;
                simulationStepListener.accept(new SimulationStepEvent(false, stepCount, true));
            }
            case RUNNING_BATCH -> {
                setSimulationState(SimulationState.CANCELLING_BATCH);
                logSimulationInfo("Simulation (batch) was canceled by the user. Waiting for batch to finish.");
                cancelBatch();
            }
            case PAUSED -> {
                setSimulationState(SimulationState.CANCELED);
                logSimulationInfo("Simulation (paused) was canceled by the user.");
            }
            default -> {
                stopTimer();
                cancelBatch();
            }
        }
    }

    private void handleStartAction() {
        // Reset notification type.
        setNotificationType(SimulationNotificationType.NONE);

        resetSelectedProperties();

        long startNanos = System.nanoTime();
        try {
            Optional<CON> config = createValidConfig();
            if (config.isEmpty()) {
                setSimulationState(SimulationState.ERROR);
                AppLogger.warnf("%s: Cannot start simulation because configuration is invalid.", LOG_COMPONENT);
                setNotificationType(SimulationNotificationType.INVALID_CONFIG);
                return;
            }

            createAndInitSimulation(config.get());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException
                 | IndexOutOfBoundsException | NoSuchElementException | UnsupportedOperationException e) {
            setSimulationState(SimulationState.ERROR);
            AppLogger.errorf(e, "%s: Failed to start simulation.", LOG_COMPONENT);
            setNotificationType(SimulationNotificationType.EXCEPTION);
            return;
        }
        long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

        if (controlViewModel.isStartPaused()) {
            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation was started in paused state by the user. durationMillis=" + durationMillis);
        } else if (controlViewModel.isModeTimed()) {
            setSimulationState(SimulationState.RUNNING_TIMED);
            logSimulationInfo("Simulation (timer) was started by the user. durationMillis=" + durationMillis);

            startTimer();
        } else if (controlViewModel.isModeBatch()) {
            setSimulationState(SimulationState.RUNNING_BATCH);
            logSimulationInfo("Simulation (batch) was started by the user. durationMillis=" + durationMillis);

            runBatchSteps(controlViewModel.stepCountProperty().getValue(), controlViewModel.isTerminationChecked(),
                    controlViewModel.isModeBatchContinuous());
        }
    }

    private void handlePauseAction() {
        if (getSimulationState() == SimulationState.RUNNING_TIMED) {
            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation (timer) was paused by the user.");

            notifyFinalStepAndStopTimer();
        } else if (getSimulationState() == SimulationState.RUNNING_BATCH) {
            setSimulationState(SimulationState.PAUSING_BATCH);
            logSimulationInfo("Simulation (batch) was paused by the user. Waiting for batch to finish.");

            cancelBatch();
        }
    }

    private void handleResumeAction() {
        // Reset notification type.
        setNotificationType(SimulationNotificationType.NONE);

        configureSimulationTimeout();
        if (controlViewModel.isModeTimed()) {
            setSimulationState(SimulationState.RUNNING_TIMED);
            logSimulationInfo("Simulation (timer) was resumed by the user.");

            startTimer();
        } else if (controlViewModel.isModeBatch()) {
            setSimulationState(SimulationState.RUNNING_BATCH);
            logSimulationInfo("Simulation (batch) was resumed by the user.");

            runBatchSteps(controlViewModel.stepCountProperty().getValue(), controlViewModel.isTerminationChecked(),
                    controlViewModel.isModeBatchContinuous());
        }
    }

    private Optional<CON> createValidConfig() {
        CON config = configViewModel.getConfig();
        if (!config.isValid()) {
            return Optional.empty();
        }
        return Optional.of(config);
    }

    private void createAndInitSimulation(CON config) {
        simulationManager = simulationManagerFactory.apply(config);
        Objects.requireNonNull(simulationManager, "Simulation manager factory returned null.");

        configureSimulationTimeout();

        updateObservationStatistics(simulationManager.statistics());

        simulationInitializedListener.run();
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void configureSimulationTimeout() {
        if (controlViewModel.isModeTimed()) {
            double stepDurationMillis = controlViewModel.stepDurationProperty().getValue();
            long timeoutExecuteMillis = Math.max(1L, (long) (stepDurationMillis * TIMEOUT_EXECUTE_FACTOR));
            timeoutExecuteNanos = TimeUnit.MILLISECONDS.toNanos(timeoutExecuteMillis);
            timeoutViewMillis = Math.max(1L, (long) (stepDurationMillis * TIMEOUT_VIEW_FACTOR));
            throttleDrawMillis = Math.max(1L, (long) (stepDurationMillis * THROTTLE_DRAW_FACTOR));
        }
    }

    private void runTimerStep() {
        if (simulationManager == null) {
            AppLogger.errorf("%s: Simulation manager is not initialized; cannot execute timer step.", LOG_COMPONENT);
            stopTimer();
            return;
        }
        if (getSimulationState() != SimulationState.RUNNING_TIMED) {
            AppLogger.errorf("%s: Simulation is not RUNNING_TIMED; cannot execute timer step. state=%s",
                    LOG_COMPONENT,
                    getSimulationState());
            stopTimer();
            return;
        }

        try {
            simulationManager.executeStep();

            AppLogger.debugf("%s: Simulation (timer) executed step. durationNanos=%d",
                    LOG_COMPONENT,
                    simulationManager.stepTimingStatistics().currentNanos());

            updateObservationStatistics(simulationManager.statistics());

            if (controlViewModel.isTerminationChecked() && simulationManager.isFinished()) {
                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation (timer) has ended itself.");
            }

            if (simulationManager.isExecutorFinished()) {
                setSimulationState(SimulationState.FINISHED);
                logSimulationInfo("Simulation (timer) executor has finished.");
            }

            long startViewNanos = System.nanoTime();
            simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount(), false));
            long durationViewMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startViewNanos);

            AppLogger.debugf("%s: Simulation (timer) informed step listener. durationViewMillis=%d",
                    LOG_COMPONENT,
                    durationViewMillis);

            // Check timeout if still running (not finished) and not the first step
            if ((getSimulationState() == SimulationState.RUNNING_TIMED) && (simulationManager.stepCount() > 1)) {
                // Check for calculation timeout
                if (simulationManager.stepTimingStatistics().currentNanos() > timeoutExecuteNanos) {
                    setNotificationType(SimulationNotificationType.TIMEOUT);

                    setSimulationState(SimulationState.PAUSED);
                    logSimulationInfo("Simulation (timer) has been paused because the simulation step took too long to " +
                            "calculate. durationNanos=" + simulationManager.stepTimingStatistics().currentNanos() + " " +
                            "timeoutExecuteNanos=" + timeoutExecuteNanos);
                }

                // Check for view timeout
                if (durationViewMillis > timeoutViewMillis) {
                    setNotificationType(SimulationNotificationType.TIMEOUT);

                    setSimulationState(SimulationState.PAUSED);
                    logSimulationInfo("Simulation (timer) has been paused because the view took too long to process. " +
                            "durationViewMillis=" + durationViewMillis + " " +
                            "timeoutViewMillis=" + timeoutViewMillis);
                }
            }
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException
                 | IndexOutOfBoundsException | NoSuchElementException | UnsupportedOperationException e) {
            setNotificationType(SimulationNotificationType.EXCEPTION);

            setSimulationState(SimulationState.ERROR);
            AppLogger.errorf(e, "%s: Simulation (timer) encountered an error and was stopped.", LOG_COMPONENT);
        }

        // If simulation is paused, finished or caught an error,
        // notify view for final step and stop timer.
        if (getSimulationState() != SimulationState.RUNNING_TIMED) {
            notifyFinalStepAndStopTimer();
        }
    }

    private void runBatchSteps(int count, boolean checkTermination, boolean restartBatchIfPossible) {
        batchFuture = batchExecutor.submit(() -> {
            batchThread = Thread.currentThread();
            try {
                var manager = simulationManager;
                if (manager == null) {
                    AppLogger.errorf("%s: Simulation manager is not initialized; cannot execute batch steps.", LOG_COMPONENT);
                    return;
                }

                var executionResult = manager.executeSteps(count, checkTermination, () -> {
                    // Create the event before the "runLater".
                    var stepEvent = new SimulationStepEvent(true, manager.stepCount(), false);
                    Platform.runLater(() -> {
                        // Check at JavaFX-Thread if it is still running.
                        if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                            simulationStepListener.accept(stepEvent);
                        }
                    });
                });

                // Create the event and statistics before the "runLater".
                var stepEvent = new SimulationStepEvent(false, manager.stepCount(), true);
                var statistics = manager.statistics();
                boolean executorFinished = executionResult.isFinished() && manager.isExecutorFinished();

                Platform.runLater(() -> {
                    if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                        if (executorFinished) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation (batch) finished and executor finished. RUNNING_BATCH -> FINISHED count=" + count + ", executionResult=" + executionResult);
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                            if (executionResult.isFinished()) {
                                logSimulationInfo("Simulation (batch) finished and simulation finished. RUNNING_BATCH -> PAUSED count=" + count + ", executionResult=" + executionResult);
                            } else if (count >= 100) {
                                logSimulationInfo("Simulation (batch) finished. RUNNING_BATCH -> PAUSED count=" + count + ", executionResult=" + executionResult);
                            }
                        }
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                        if (restartBatchIfPossible && !executionResult.isFinished()) {
                            setSimulationState(SimulationState.RUNNING_BATCH);
                            if (count >= 100) {
                                logSimulationInfo("Simulation (batch) finished. Restart new batch. RUNNING_BATCH -> RUNNING_BATCH count=" + count + ", executionResult=" + executionResult);
                            }
                            runBatchSteps(count, checkTermination, true);
                        }
                    } else if (getSimulationState() == SimulationState.PAUSING_BATCH) {
                        if (executorFinished) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation (batch) finished and executor finished. PAUSING_BATCH -> FINISHED count=" + count + ", executionResult=" + executionResult);
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                            if (executionResult.isFinished()) {
                                logSimulationInfo("Simulation (batch) finished and simulation finished. PAUSING_BATCH -> PAUSED count=" + count + ", executionResult=" + executionResult);
                            } else {
                                logSimulationInfo("Simulation (batch) finished. PAUSING_BATCH -> PAUSED count=" + count + ", executionResult=" + executionResult);
                            }
                        }
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                    } else if (getSimulationState() == SimulationState.CANCELLING_BATCH) {
                        setSimulationState(SimulationState.CANCELED);
                        logSimulationInfo("Simulation (batch) finished. CANCELLING_BATCH -> CANCELED count=" + count + ", executionResult=" + executionResult);
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                    } else if (getSimulationState() == SimulationState.SHUTTING_DOWN) {
                        logSimulationInfo("Simulation (batch) finished. SHUTTING_DOWN. count=" + count + ", executionResult=" + executionResult);
                    } else {
                        AppLogger.errorf("%s: Simulation is not in a valid state for batch execution. thread=%s, state=%s",
                                LOG_COMPONENT,
                                Thread.currentThread().getName(),
                                getSimulationState());
                    }
                });
            } catch (IllegalArgumentException | IllegalStateException | NullPointerException
                     | IndexOutOfBoundsException | NoSuchElementException | UnsupportedOperationException e) {
                Platform.runLater(() -> {
                    setNotificationType(SimulationNotificationType.EXCEPTION);

                    setSimulationState(SimulationState.ERROR);
                    AppLogger.errorf(e, "%s: Simulation (batch) encountered an error and was stopped.", LOG_COMPONENT);
                });
            } finally {
                batchThread = null;
            }
        });
    }

    private boolean isTimerRunning() {
        return timer.isRunning();
    }

    private boolean isBatchRunning() {
        Thread thread = batchThread;
        return (thread != null) && thread.isAlive();
    }

    private void startTimer() {
        timer.start(Duration.millis(controlViewModel.stepDurationProperty().getValue()));
    }

    private void stopTimer() {
        timer.stop();
    }

    private void notifyFinalStepAndStopTimer() {
        if (simulationManager != null) {
            simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount(), true));
        }
        stopTimer();
    }

    private void cancelBatch() {
        if ((batchFuture != null) && !batchFuture.isDone()) {
            batchFuture.cancel(true); // Attempts to interrupt
        }
        batchFuture = null;
    }

    private void shutdownBatchExecutor() {
        batchExecutor.shutdown();
        try {
            if (!batchExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                batchExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            batchExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void updateObservationStatistics(STA statistics) {
        if (Platform.isFxApplicationThread()) {
            observationViewModel.setStatistics(statistics);
            return;
        }
        Platform.runLater(() -> {
            if (getSimulationState() != SimulationState.SHUTTING_DOWN) {
                observationViewModel.setStatistics(statistics);
            }
        });
    }

    private void logSimulationInfo(String message) {
        if (simulationManager == null) {
            AppLogger.infof("%s: %s", LOG_COMPONENT, message);
        } else {
            AppLogger.infof("%s: %s config=%s, statistics=%s",
                    LOG_COMPONENT,
                    message,
                    simulationManager.config(),
                    simulationManager.statistics());
        }
    }

}
