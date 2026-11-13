package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.model.AbstractSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCanceledException;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Main view model of the simulations that orchestrates timed and batch execution.
 *
 * <p>Coordinates the config, control, and observation view models, the notification, and the cell selection.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GC>  grid cell type used for cell selection
 * @param <GM>  grid model type managed by the simulation
 * @param <CON> immutable simulation config type
 * @param <STA> statistics type exposed to observation views
 * @param <SM>  concrete simulation manager type
 * @param <CTX> simulation-specific user action context type
 */
public final class MainViewModel<
        ENT extends GridEntity,
        GC extends GridCellView<ENT>,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends SimulationStatistics,
        SM extends AbstractSimulationManager<ENT, GM, CON, STA>,
        CTX extends SimulationUserActionContext> {

    private static final double TIMEOUT_EXECUTE_FACTOR = 0.4d;
    private static final double TIMEOUT_VIEW_FACTOR = 0.5d;
    private static final double THROTTLE_DRAW_FACTOR = 0.3d;
    private static final String LOG_COMPONENT = "MainViewModel";
    private static final Runnable NO_OP_SIMULATION_INITIALIZED_LISTENER = () -> {};
    private static final Consumer<SimulationStepEvent> NO_OP_SIMULATION_STEP_LISTENER = _ -> {};
    private static final Runnable NO_OP_SIMULATION_RESET_LISTENER = () -> {};

    private final ObjectProperty<SimulationState> simulationState;
    private final ObjectProperty<SimulationNotificationType> notificationType =
            new SimpleObjectProperty<>(SimulationNotificationType.NONE);
    private final AbstractConfigViewModel<CON> configViewModel;
    private final ControlViewModel controlViewModel;
    private final ObservationViewModel<ENT, GC, STA> observationViewModel;
    private final BiFunction<CON, SimulationInitializationCancellation, SM> simulationManagerFactory;
    private final SimulationTimer timer;
    private final ExecutorService lifecycleExecutor;
    private final ChangeListener<Boolean> actionButtonRequestedListener;
    private final ChangeListener<Boolean> cancelButtonRequestedListener;
    private final ChangeListener<SimulationState> simulationStateListener;
    private final ChangeListener<@Nullable GridCoordinate> lastClickedCoordinateListener;
    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable GC> selectedGridCell = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable ENT> lastSelectedEntity = new SimpleObjectProperty<>();
    private final BiFunction<GM, GridCoordinate, GC> selectedGridCellProvider;
    private final SimulationUserAction<ENT, GM, CON, STA, SM, CTX> simulationUserAction;
    private final EditToolBarViewModel<CTX> editToolBarViewModel;
    private @Nullable SM simulationManager;
    private @Nullable Future<?> initializationFuture;
    private @Nullable Future<?> batchFuture;
    private volatile @Nullable Thread batchThread;
    private long lifecycleGeneration;
    private boolean disposed;
    private @Nullable SimulationTerminationHandle terminationHandle;
    private long timeoutExecuteNanos = Long.MAX_VALUE;
    private long timeoutViewMillis = Long.MAX_VALUE;
    private long throttleDrawMillis = Long.MAX_VALUE;

    // Listener for view
    private Runnable simulationInitializedListener = NO_OP_SIMULATION_INITIALIZED_LISTENER;
    private Consumer<SimulationStepEvent> simulationStepListener = NO_OP_SIMULATION_STEP_LISTENER;
    private Runnable simulationResetListener = NO_OP_SIMULATION_RESET_LISTENER;

    /**
     * Creates a main view model.
     *
     * @param simulationState          shared simulation state property
     * @param configViewModel          config view model
     * @param controlViewModel         control view model
     * @param observationViewModel     observation view model
     * @param simulationManagerFactory factory used to create simulation managers for validated configurations
     * @param selectedGridCellProvider mapping from a clicked coordinate to the corresponding selected cell
     * @param simulationUserAction     user action applied to the current paused simulation state
     */
    public MainViewModel(ObjectProperty<SimulationState> simulationState,
                         AbstractConfigViewModel<CON> configViewModel,
                         ControlViewModel controlViewModel,
                         ObservationViewModel<ENT, GC, STA> observationViewModel,
                         BiFunction<CON, SimulationInitializationCancellation, SM> simulationManagerFactory,
                         BiFunction<GM, GridCoordinate, GC> selectedGridCellProvider,
                         SimulationUserAction<ENT, GM, CON, STA, SM, CTX> simulationUserAction) {
        this(simulationState, configViewModel, controlViewModel, observationViewModel, simulationManagerFactory,
                selectedGridCellProvider, simulationUserAction, createLifecycleExecutor());
    }

    MainViewModel(ObjectProperty<SimulationState> simulationState,
                  AbstractConfigViewModel<CON> configViewModel,
                  ControlViewModel controlViewModel,
                  ObservationViewModel<ENT, GC, STA> observationViewModel,
                  BiFunction<CON, SimulationInitializationCancellation, SM> simulationManagerFactory,
                  BiFunction<GM, GridCoordinate, GC> selectedGridCellProvider,
                  SimulationUserAction<ENT, GM, CON, STA, SM, CTX> simulationUserAction,
                  ExecutorService lifecycleExecutor) {
        this.simulationState = simulationState;
        this.configViewModel = configViewModel;
        this.controlViewModel = controlViewModel;
        this.observationViewModel = observationViewModel;
        this.simulationManagerFactory = simulationManagerFactory;
        this.selectedGridCellProvider = selectedGridCellProvider;
        this.simulationUserAction = simulationUserAction;
        editToolBarViewModel = new EditToolBarViewModel<>();
        timer = new SimulationTimer(this::executeTimerStep);
        this.lifecycleExecutor = lifecycleExecutor;

        actionButtonRequestedListener = (_, _, newVal) -> {
            if (newVal) {
                handleActionButton();
                controlViewModel.actionButtonRequestedProperty().set(false);
            }
        };
        controlViewModel.actionButtonRequestedProperty().addListener(actionButtonRequestedListener);

        cancelButtonRequestedListener = (_, _, newVal) -> {
            if (newVal) {
                handleCancelButton();
                controlViewModel.cancelButtonRequestedProperty().set(false);
            }
        };
        controlViewModel.cancelButtonRequestedProperty().addListener(cancelButtonRequestedListener);

        simulationStateListener = (_, _, newState) -> {
            if (newState != SimulationState.PAUSED) {
                editToolBarViewModel.resetToSelectMode();
            }
        };
        simulationStateProperty().addListener(simulationStateListener);

        // Initialize selected grid cell handling
        observationViewModel.bindSelectedGridCellProperty(selectedGridCell);
        lastClickedCoordinateListener = (_, _, newValue) -> {
            if ((newValue != null) && hasSimulationManager() && isSelectionState(getSimulationState())) {
                refreshSelectedGridCell(getCurrentGridModel(), newValue);
            } else {
                selectedGridCell.set(null);
            }
        };
        lastClickedCoordinate.addListener(lastClickedCoordinateListener);
    }

    private static ExecutorService createLifecycleExecutor() {
        return Executors.newSingleThreadExecutor(task -> {
            var thread = new Thread(task, "simulation-lifecycle-executor");
            thread.setDaemon(true);
            return thread;
        });
    }

    private static boolean isSelectionState(SimulationState simulationState) {
        return switch (simulationState) {
            case PAUSED, CANCELED, FINISHED -> true;
            case READY, INITIALIZING, RUNNING_TIMED, RUNNING_BATCH, PAUSING_BATCH, CANCELLING_BATCH, ERROR,
                 SHUTTING_DOWN -> false;
        };
    }

    /**
     * Exposes the selected grid cell resolved from the latest click.
     *
     * @return selected-cell property, nullable when no cell is selected
     */
    public ObjectProperty<@Nullable GC> selectedGridCellProperty() {
        return selectedGridCell;
    }

    /**
     * Exposes the entity of the last non-null selected cell.
     *
     * @return last-selected entity property
     */
    public ObjectProperty<@Nullable ENT> lastSelectedEntityProperty() {
        return lastSelectedEntity;
    }

    public BooleanProperty editModeActiveProperty() {
        return editToolBarViewModel.editModeActiveProperty();
    }

    public ObjectProperty<String> selectedUserActionToolIdProperty() {
        return editToolBarViewModel.selectedUserActionToolIdProperty();
    }

    /**
     * Sets the callback invoked after simulation initialization; replaces any previous callback.
     *
     * @param listener callback invoked after manager creation and initial statistics update
     */
    public void setSimulationInitializedListener(Runnable listener) {
        simulationInitializedListener = listener;
    }

    /**
     * Sets the callback invoked for simulation step notifications; replaces any previous callback.
     *
     * @param listener callback receiving step events
     */
    public void setSimulationStepListener(Consumer<SimulationStepEvent> listener) {
        simulationStepListener = listener;
    }

    /**
     * Sets the callback invoked whenever statistics are reset, i.e. right after
     * {@link ObservationViewModel#resetStatistics()} is called on simulation restart or shutdown.
     *
     * <p>Use this to refresh observation labels/charts to their initial placeholder state so stale
     * values from a previous or finished simulation do not linger until the next simulation initializes.
     *
     * @param listener callback invoked after statistics are reset
     */
    public void setSimulationResetListener(Runnable listener) {
        simulationResetListener = listener;
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    private void setSimulationState(SimulationState simulationState) {
        this.simulationState.set(simulationState);
    }

    /**
     * Exposes the notification type shown by the main view; {@link SimulationNotificationType#NONE} hides the
     * notification.
     *
     * @return mutable notification type property
     */
    public ObjectProperty<SimulationNotificationType> notificationTypeProperty() {
        return notificationType;
    }

    public void setNotificationType(SimulationNotificationType notificationType) {
        this.notificationType.set(notificationType);
    }

    /**
     * Returns the grid structure of the active simulation.
     *
     * @return simulation structure
     */
    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.structure();
    }

    /**
     * Returns the configured visual edge length of one cell of the active simulation.
     *
     * @return edge length in pixels
     */
    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.config().cellEdgeLength();
    }

    /**
     * Immediately stops the simulation and releases resources on the JavaFX Application Thread.
     *
     * <p>This operation is idempotent and must not wait for background work to terminate.
     *
     * @return termination handle for application-exit bookkeeping
     */
    public SimulationTerminationHandle shutdownSimulation() {
        if (disposed) {
            return Objects.requireNonNull(terminationHandle);
        }
        disposed = true;
        AppLogger.infof("%s: Shutting down simulation: state=%s", LOG_COMPONENT, getSimulationState());
        setSimulationState(SimulationState.SHUTTING_DOWN);

        controlViewModel.actionButtonRequestedProperty().removeListener(actionButtonRequestedListener);
        controlViewModel.cancelButtonRequestedProperty().removeListener(cancelButtonRequestedListener);
        lastClickedCoordinate.removeListener(lastClickedCoordinateListener);
        simulationStateProperty().removeListener(simulationStateListener);
        observationViewModel.selectedGridCellProperty().unbind();

        editToolBarViewModel.resetToSelectMode();
        resetSelectedProperties();
        resetClickedCoordinate();
        stopTimer();
        cancelInitialization();
        cancelBatch();
        shutdownLifecycleExecutor();
        simulationManager = null;
        observationViewModel.resetStatistics();
        terminationHandle = new ExecutorSimulationTerminationHandle(lifecycleExecutor);
        Runnable resetListener = simulationResetListener;
        simulationInitializedListener = NO_OP_SIMULATION_INITIALIZED_LISTENER;
        simulationStepListener = NO_OP_SIMULATION_STEP_LISTENER;
        simulationResetListener = NO_OP_SIMULATION_RESET_LISTENER;
        try {
            resetListener.run();
        } catch (RuntimeException e) {
            AppLogger.error(e, LOG_COMPONENT + ": Failed to reset the simulation view during shutdown");
        }
        return terminationHandle;
    }

    /**
     * Returns the configuration of the active simulation.
     *
     * @return current immutable config
     */
    public CON getCurrentConfig() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.config();
    }

    /**
     * Checks whether a simulation manager is currently available.
     *
     * @return {@code true} when a manager exists
     */
    public boolean hasSimulationManager() {
        return simulationManager != null;
    }

    /**
     * Returns the grid model of the active simulation.
     *
     * @return current model snapshot
     */
    public GM getCurrentGridModel() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.currentGridModel();
    }

    public int getStepCount() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
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

    /**
     * Stores a new last-clicked coordinate, which selects the cell at this coordinate if the simulation state allows a
     * selection.
     *
     * @param coordinate new coordinate
     */
    public void updateClickedCoordinate(GridCoordinate coordinate) {
        lastClickedCoordinate.set(coordinate);
    }

    /**
     * Applies the configured user action to the current simulation state.
     *
     * <p>The action is only applied when a simulation manager is active and the simulation is currently paused.
     * The active simulation manager, the context, and the selected cell are passed to the configured
     * {@link SimulationUserAction}. If a cell was selected before the action, the selection is refreshed afterward.
     *
     * @param context simulation-specific action context
     * @return {@code true} if the user action was applied, which does not mean that the model changed; {@code false} if
     * no active manager exists or the simulation is not paused
     */
    public boolean applyUserAction(CTX context) {
        var manager = simulationManager;
        if ((manager != null)
                && (getSimulationState() == SimulationState.PAUSED)) {
            GC currentSelectedCell = selectedGridCell.get();
            logSimulationInfo("Applying user action to the current simulation state: selectedCell="
                    + ((currentSelectedCell != null) ? currentSelectedCell.toDisplayString() : "null"));
            simulationUserAction.apply(manager, context, currentSelectedCell);
            if (currentSelectedCell != null) {
                refreshSelectedGridCell(manager.currentGridModel(), currentSelectedCell.coordinate());
            }
            return true;
        } else {
            AppLogger.errorf("%s: Failed to apply user action: invalid state, "
                            + "thread=%s, state=%s, managerActive=%s",
                    LOG_COMPONENT,
                    Thread.currentThread().getName(),
                    getSimulationState(),
                    manager != null);
            return false;
        }
    }

    /**
     * Applies the resolved user action described by the given descriptor.
     *
     * @param descriptor descriptor whose current context should be resolved and applied
     * @return {@code true} if the action context resolved and the action was applied; {@code false} otherwise
     */
    @SuppressWarnings("OptionalIsPresent")
    public boolean applyUserAction(SimulationUserActionDescriptor<CTX> descriptor) {
        var context = descriptor.resolveContext();
        if (context.isEmpty()) {
            return false;
        }
        return applyUserAction(context.get());
    }

    /**
     * Applies the currently selected cell-scoped action descriptor.
     *
     * @param descriptors descriptors currently available in the toolbar
     * @return {@code true} when an action was applied; {@code false} when edit mode is inactive, no cell action or
     * no grid cell is selected, the action context does not resolve, or the simulation state does not allow applying
     * actions
     */
    public boolean applySelectedCellUserAction(List<SimulationUserActionDescriptor<CTX>> descriptors) {
        if (!editToolBarViewModel.isEditModeActive()) {
            return false;
        }

        var descriptor = editToolBarViewModel.findSelectedCellActionDescriptor(descriptors);
        if (descriptor.isEmpty()) {
            return false;
        }
        if (selectedGridCell.get() == null) {
            return false;
        }
        return applyUserAction(descriptor.get());
    }

    boolean isTimerRunning() {
        return timer.isRunning();
    }

    boolean hasViewListeners() {
        return !NO_OP_SIMULATION_INITIALIZED_LISTENER.equals(simulationInitializedListener)
                || !NO_OP_SIMULATION_STEP_LISTENER.equals(simulationStepListener)
                || !NO_OP_SIMULATION_RESET_LISTENER.equals(simulationResetListener);
    }

    private void resetClickedCoordinate() {
        lastClickedCoordinate.set(null);
    }

    private void resetSelectedProperties() {
        selectedGridCell.set(null);
        lastSelectedEntity.set(null);
    }

    private void refreshSelectedGridCell(GM currentGridModel, GridCoordinate coordinate) {
        try {
            var cell = selectedGridCellProvider.apply(currentGridModel, coordinate);
            selectedGridCell.set(cell);
            lastSelectedEntity.set(cell.entity());
            AppLogger.infof("%s: Cell selected: %s", LOG_COMPONENT, cell.toDisplayString());
        } catch (RuntimeException e) {
            AppLogger.errorf(e, "%s: Failed to determine selected cell: coordinate=%s", LOG_COMPONENT,
                    coordinate.toDisplayString());
            selectedGridCell.set(null);
        }
    }

    private void handleActionButton() {
        if (!getSimulationState().isRunning() && (isTimerRunning() || isBatchRunning())) {
            throw new IllegalStateException("simulationState must be RUNNING_TIMED or RUNNING_BATCH, but was: "
                    + getSimulationState());
        }

        if (getSimulationState().isStartable()) {
            handleStartAction();
        } else if (getSimulationState().isRunning()) {
            handlePauseAction();
        } else if (getSimulationState().isPaused()) {
            handleResumeAction();
        } else {
            AppLogger.warnf("%s: Ignoring action button: state=%s", LOG_COMPONENT, getSimulationState());
        }
    }

    private void handleCancelButton() {
        setNotificationType(SimulationNotificationType.NONE);
        resetClickedCoordinate();

        switch (getSimulationState()) {
            case RUNNING_TIMED -> {
                stopTimer();
                setSimulationState(SimulationState.CANCELED);
                logSimulationInfo("Simulation (timer) was canceled by the user");

                int stepCount = (simulationManager != null) ? simulationManager.stepCount() : 0;
                simulationStepListener.accept(new SimulationStepEvent(false, stepCount, true));
            }
            case RUNNING_BATCH -> {
                setSimulationState(SimulationState.CANCELLING_BATCH);
                logSimulationInfo("Simulation (batch) was canceled by the user; waiting for batch to finish");

                cancelBatch();
            }
            case PAUSED -> {
                setSimulationState(SimulationState.CANCELED);
                logSimulationInfo("Simulation (paused) was canceled by the user");
            }
            default -> {
                stopTimer();
                cancelBatch();
            }
        }
    }

    private void handleStartAction() {
        setNotificationType(SimulationNotificationType.NONE);
        resetClickedCoordinate();
        resetSelectedProperties();

        observationViewModel.resetStatistics();
        simulationResetListener.run();
        simulationManager = null;

        Optional<CON> config = createValidConfig();
        if (config.isEmpty()) {
            setSimulationState(SimulationState.ERROR);
            AppLogger.warnf("%s: Ignoring start request: configuration is invalid", LOG_COMPONENT);
            setNotificationType(SimulationNotificationType.INVALID_CONFIG);
            return;
        }

        SimulationStartRequest<CON> request = new SimulationStartRequest<>(config.get(),
                controlViewModel.isStartPaused(),
                controlViewModel.isModeTimed(), controlViewModel.isModeBatchContinuous(),
                controlViewModel.stepDurationProperty().getValue(), controlViewModel.stepCountProperty().getValue(),
                controlViewModel.isTerminationChecked());
        ++lifecycleGeneration;
        long generation = lifecycleGeneration;

        setSimulationState(SimulationState.INITIALIZING);
        logSimulationInfo("Simulation initialization started by the user: generation=" + generation + ", request="
                + request);

        initializationFuture = lifecycleExecutor.submit(() -> initializeSimulation(request, generation,
                System.nanoTime()));
    }

    private void handlePauseAction() {
        resetClickedCoordinate();

        if (getSimulationState() == SimulationState.RUNNING_TIMED) {
            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation (timer) was paused by the user");

            notifyFinalStepAndStopTimer();
        } else if (getSimulationState() == SimulationState.RUNNING_BATCH) {
            setSimulationState(SimulationState.PAUSING_BATCH);
            logSimulationInfo("Simulation (batch) was paused by the user; waiting for batch to finish");

            cancelBatch();
        }
    }

    private void handleResumeAction() {
        setNotificationType(SimulationNotificationType.NONE);
        resetClickedCoordinate();

        configureSimulationTimeout(controlViewModel.isModeTimed(), controlViewModel.stepDurationProperty().getValue());
        if (controlViewModel.isModeTimed()) {
            setSimulationState(SimulationState.RUNNING_TIMED);
            logSimulationInfo("Simulation (timer) was resumed by the user");

            startTimer(controlViewModel.stepDurationProperty().getValue());
        } else if (controlViewModel.isModeBatch()) {
            setSimulationState(SimulationState.RUNNING_BATCH);
            logSimulationInfo("Simulation (batch) was resumed by the user");

            executeBatchSteps(controlViewModel.stepCountProperty().getValue(), controlViewModel.isTerminationChecked(),
                    controlViewModel.isModeBatchContinuous());
        }
    }

    private Optional<CON> createValidConfig() {
        CON config = configViewModel.createConfig();
        if (!config.isValid()) {
            return Optional.empty();
        }
        return Optional.of(config);
    }

    private void initializeSimulation(SimulationStartRequest<CON> request, long generation, long startNanos) {
        try {
            AppLogger.infof("%s: Simulation initialization started: generation=%d, request=%s", LOG_COMPONENT,
                    generation, request);
            SM manager = simulationManagerFactory.apply(request.config(),
                    SimulationInitializationCancellation.interruptionAware());
            AppLogger
                    .infof("%s: Simulation manager was created; scheduling initialization completion: generation=%d, "
                                    + "request=%s",
                            LOG_COMPONENT, generation, request);
            Platform.runLater(() -> completeInitialization(request, generation, startNanos, manager));
        } catch (SimulationInitializationCanceledException e) {
            AppLogger.infof("%s: Simulation initialization was canceled: generation=%d, request=%s",
                    LOG_COMPONENT, generation, request);
        } catch (RuntimeException | Error e) {
            Platform.runLater(() -> failInitialization(request, generation, e));
        }
    }

    private void completeInitialization(SimulationStartRequest<CON> request,
                                        long generation,
                                        long startNanos,
                                        SM manager) {
        if (!isInitializationActive(generation)) {
            AppLogger
                    .infof("%s: Discarding completed initialization because it is no longer active: generation=%d, "
                                    + "activeGeneration=%d, state=%s",
                            LOG_COMPONENT, generation, lifecycleGeneration, getSimulationState());
            return;
        }

        try {
            initializationFuture = null;
            simulationManager = manager;
            configureSimulationTimeout(request.timedMode(), request.stepDurationMillis());
            updateObservationStatistics(manager.statistics());
            simulationInitializedListener.run();

            long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
            if (request.startPaused()) {
                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation was started in paused state by the user: durationMillis="
                        + durationMillis);
            } else if (request.timedMode()) {
                setSimulationState(SimulationState.RUNNING_TIMED);
                logSimulationInfo("Simulation (timer) was started by the user: durationMillis=" + durationMillis);
                startTimer(request.stepDurationMillis());
            } else {
                setSimulationState(SimulationState.RUNNING_BATCH);
                logSimulationInfo("Simulation (batch) was started by the user: durationMillis=" + durationMillis);
                executeBatchSteps(request.stepCount(), request.terminationChecked(), request.continuousBatchMode());
            }
        } catch (RuntimeException e) {
            if (isInitializationActive(generation)) {
                simulationManager = null;
                failInitialization(request, generation, e);
            }
        }
    }

    private void failInitialization(SimulationStartRequest<CON> request, long generation, Throwable exception) {
        if (!isInitializationActive(generation)) {
            return;
        }

        initializationFuture = null;
        setSimulationState(SimulationState.ERROR);
        setNotificationType(SimulationNotificationType.EXCEPTION);
        AppLogger.errorf(exception, "%s: Failed to initialize simulation: generation=%d, request=%s",
                LOG_COMPONENT, generation, request);
    }

    private boolean isInitializationActive(long generation) {
        return isActiveTask(generation) && (getSimulationState() == SimulationState.INITIALIZING);
    }

    private boolean isActiveTask(long generation) {
        return !disposed && (lifecycleGeneration == generation);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void configureSimulationTimeout(boolean timedMode, double stepDurationMillis) {
        if (timedMode) {
            long timeoutExecuteMillis = Math.max(1L, (long) (stepDurationMillis * TIMEOUT_EXECUTE_FACTOR));
            timeoutExecuteNanos = TimeUnit.MILLISECONDS.toNanos(timeoutExecuteMillis);
            timeoutViewMillis = Math.max(1L, (long) (stepDurationMillis * TIMEOUT_VIEW_FACTOR));
            throttleDrawMillis = Math.max(1L, (long) (stepDurationMillis * THROTTLE_DRAW_FACTOR));
        }
    }

    private void executeTimerStep() {
        if (simulationManager == null) {
            AppLogger.errorf("%s: Failed to execute timer step: simulation manager is not initialized", LOG_COMPONENT);
            stopTimer();
            return;
        }
        if (getSimulationState() != SimulationState.RUNNING_TIMED) {
            AppLogger.errorf("%s: Failed to execute timer step: simulation is not RUNNING_TIMED, state=%s",
                    LOG_COMPONENT,
                    getSimulationState());
            stopTimer();
            return;
        }

        try {
            simulationManager.executeStep();

            AppLogger.debugf("%s: Simulation (timer) executed step: durationNanos=%d",
                    LOG_COMPONENT,
                    simulationManager.stepTimingStatistics().currentNanos());

            updateObservationStatistics(simulationManager.statistics());

            if (controlViewModel.isTerminationChecked() && simulationManager.isFinished()) {
                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation (timer) has ended itself");
            }

            if (simulationManager.isExecutorFinished()) {
                setSimulationState(SimulationState.FINISHED);
                logSimulationInfo("Simulation (timer) executor has finished");
            }

            long startViewNanos = System.nanoTime();
            simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount(), false));
            long durationViewMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startViewNanos);

            AppLogger.debugf("%s: Simulation (timer) informed step listener: durationViewMillis=%d",
                    LOG_COMPONENT,
                    durationViewMillis);

            // Check timeout if still running (not finished) and not the first step
            if ((getSimulationState() == SimulationState.RUNNING_TIMED) && (simulationManager.stepCount() > 1)) {
                // Check for calculation timeout
                if (simulationManager.stepTimingStatistics().currentNanos() > timeoutExecuteNanos) {
                    setNotificationType(SimulationNotificationType.TIMEOUT);

                    setSimulationState(SimulationState.PAUSED);
                    logSimulationInfo("Simulation (timer) has been paused because the simulation step took too long to "
                            + "calculate: durationNanos=" + simulationManager.stepTimingStatistics().currentNanos()
                            + ", " + "timeoutExecuteNanos=" + timeoutExecuteNanos);
                }

                // Check for view timeout
                if (durationViewMillis > timeoutViewMillis) {
                    setNotificationType(SimulationNotificationType.TIMEOUT);

                    setSimulationState(SimulationState.PAUSED);
                    logSimulationInfo("Simulation (timer) has been paused because the view took too long to process: " +
                            "durationViewMillis=" + durationViewMillis + ", " +
                            "timeoutViewMillis=" + timeoutViewMillis);
                }
            }
        } catch (RuntimeException e) {
            setNotificationType(SimulationNotificationType.EXCEPTION);

            setSimulationState(SimulationState.ERROR);
            AppLogger.errorf(e, "%s: Failed to execute timer step; simulation stopped", LOG_COMPONENT);
        }

        // If simulation is paused, finished or caught an error,
        // notify view for final step and stop timer.
        if (getSimulationState() != SimulationState.RUNNING_TIMED) {
            notifyFinalStepAndStopTimer();
        }
    }

    private void executeBatchSteps(int count, boolean checkTermination, boolean restartBatchIfPossible) {
        ++lifecycleGeneration;
        long generation = lifecycleGeneration;
        batchFuture = lifecycleExecutor.submit(() -> {
            batchThread = Thread.currentThread();
            try {
                var manager = simulationManager;
                if (manager == null) {
                    AppLogger.errorf("%s: Failed to execute batch steps: simulation manager is not initialized",
                            LOG_COMPONENT);
                    return;
                }

                var executionResult = manager.executeSteps(count, checkTermination, () -> {
                    // Create the event before the "runLater".
                    var stepEvent = new SimulationStepEvent(true, manager.stepCount(), false);
                    Platform.runLater(() -> {
                        // Check at JavaFX-Thread if it is still running.
                        if (isActiveTask(generation) && (getSimulationState() == SimulationState.RUNNING_BATCH)) {
                            simulationStepListener.accept(stepEvent);
                        }
                    });
                });

                // Create the event and statistics before the "runLater".
                var stepEvent = new SimulationStepEvent(false, manager.stepCount(), true);
                var statistics = manager.statistics();
                boolean executorFinished = executionResult.isFinished() && manager.isExecutorFinished();

                Platform.runLater(() -> {
                    if (!isActiveTask(generation)) {
                        return;
                    }
                    if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                        if (executorFinished) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation (batch) finished and executor finished: "
                                    + "transition=RUNNING_BATCH -> FINISHED, count=" + count + ", executionResult="
                                    + executionResult);
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                            if (executionResult.isFinished()) {
                                logSimulationInfo("Simulation (batch) finished and simulation finished: "
                                        + "transition=RUNNING_BATCH -> PAUSED, count=" + count + ", executionResult="
                                        + executionResult);
                            } else if (count >= 100) {
                                logSimulationInfo("Simulation (batch) finished: transition=RUNNING_BATCH -> PAUSED, "
                                        + "count=" + count + ", executionResult=" + executionResult);
                            }
                        }
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                        if (restartBatchIfPossible && !executionResult.isFinished()) {
                            setSimulationState(SimulationState.RUNNING_BATCH);
                            if (count >= 100) {
                                logSimulationInfo("Simulation (batch) finished; restarting new batch: "
                                        + "transition=RUNNING_BATCH -> RUNNING_BATCH, count=" + count
                                        + ", executionResult=" + executionResult);
                            }
                            executeBatchSteps(count, checkTermination, true);
                        }
                    } else if (getSimulationState() == SimulationState.PAUSING_BATCH) {
                        if (executorFinished) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation (batch) finished and executor finished: "
                                    + "transition=PAUSING_BATCH -> FINISHED, count=" + count + ", executionResult="
                                    + executionResult);
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                            if (executionResult.isFinished()) {
                                logSimulationInfo("Simulation (batch) finished and simulation finished: "
                                        + "transition=PAUSING_BATCH -> PAUSED, count=" + count + ", executionResult="
                                        + executionResult);
                            } else {
                                logSimulationInfo("Simulation (batch) finished: transition=PAUSING_BATCH -> PAUSED, "
                                        + "count=" + count + ", executionResult=" + executionResult);
                            }
                        }
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                    } else if (getSimulationState() == SimulationState.CANCELLING_BATCH) {
                        setSimulationState(SimulationState.CANCELED);
                        logSimulationInfo("Simulation (batch) finished: transition=CANCELLING_BATCH -> CANCELED, "
                                + "count=" + count + ", executionResult=" + executionResult);
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                    } else if (getSimulationState() == SimulationState.SHUTTING_DOWN) {
                        logSimulationInfo("Simulation (batch) finished: state=SHUTTING_DOWN, count=" + count
                                + ", executionResult=" + executionResult);
                    } else {
                        AppLogger.errorf("%s: Failed to execute batch steps: invalid state, thread=%s, state=%s",
                                LOG_COMPONENT,
                                Thread.currentThread().getName(),
                                getSimulationState());
                    }
                });
            } catch (RuntimeException | Error e) {
                Platform.runLater(() -> {
                    if (!isActiveTask(generation)) {
                        return;
                    }
                    setNotificationType(SimulationNotificationType.EXCEPTION);

                    setSimulationState(SimulationState.ERROR);
                    AppLogger.errorf(e, "%s: Failed to execute batch steps; simulation stopped", LOG_COMPONENT);
                });
            } finally {
                batchThread = null;
            }
        });
    }

    private boolean isBatchRunning() {
        Thread thread = batchThread;
        return (thread != null) && thread.isAlive();
    }

    private void startTimer(double stepDurationMillis) {
        timer.start(Duration.millis(stepDurationMillis));
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
            AppLogger.infof("%s: Requesting batch cancellation: state=%s", LOG_COMPONENT, getSimulationState());
            batchFuture.cancel(true); // Attempts to interrupt
        }
        batchFuture = null;
    }

    private void cancelInitialization() {
        if ((initializationFuture != null) && !initializationFuture.isDone()) {
            AppLogger.infof("%s: Requesting initialization cancellation: generation=%d, state=%s",
                    LOG_COMPONENT, lifecycleGeneration, getSimulationState());
            initializationFuture.cancel(true);
        }
        ++lifecycleGeneration;
        initializationFuture = null;
    }

    private void shutdownLifecycleExecutor() {
        lifecycleExecutor.shutdown();
    }

    private void updateObservationStatistics(STA statistics) {
        var manager = simulationManager;
        var extrema = (manager != null) ? manager.statisticsExtrema() : null;
        var history = (manager != null) ? manager.statisticsHistory() : null;
        if (Platform.isFxApplicationThread()) {
            observationViewModel.setStatistics(statistics);
            if (extrema != null) {
                observationViewModel.setStatisticsExtrema(extrema);
            }
            if (history != null) {
                observationViewModel.setStatisticsHistory(history);
            }
            return;
        }
        Platform.runLater(() -> {
            if (getSimulationState() != SimulationState.SHUTTING_DOWN) {
                observationViewModel.setStatistics(statistics);
                if (extrema != null) {
                    observationViewModel.setStatisticsExtrema(extrema);
                }
                if (history != null) {
                    observationViewModel.setStatisticsHistory(history);
                }
            }
        });
    }

    private void logSimulationInfo(String message) {
        if (simulationManager == null) {
            AppLogger.infof("%s: %s", LOG_COMPONENT, message);
        } else {
            AppLogger.infof("%s: %s; config=%s, statistics=%s",
                    LOG_COMPONENT,
                    message,
                    simulationManager.config(),
                    simulationManager.statistics());
        }
    }

    /**
     * Termination handle for the lifecycle executor.
     *
     * <p>The ViewModel shuts the executor down; this handle only waits for its termination or forces it.
     *
     * @param executor the lifecycle executor
     */
    private record ExecutorSimulationTerminationHandle(ExecutorService executor)
            implements SimulationTerminationHandle {

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return executor.awaitTermination(timeout, unit);
        }

        @Override
        public void shutdownNow() {
            executor.shutdownNow();
        }

    }

    private record SimulationStartRequest<CON extends SimulationConfig>(
            CON config,
            boolean startPaused,
            boolean timedMode,
            boolean continuousBatchMode,
            double stepDurationMillis,
            int stepCount,
            boolean terminationChecked) {
    }

}
