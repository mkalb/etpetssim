package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.model.*;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public final class DefaultMainViewModel<
        ENT extends GridEntity,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics>
        extends AbstractMainViewModel<ENT, CON, STA> {

    private static final double TIMEOUT_EXECUTE_FACTOR = 0.4d;
    private static final double TIMEOUT_VIEW_FACTOR = 0.5d;
    private static final double THROTTLE_DRAW_FACTOR = 0.3d;

    private final DefaultControlViewModel controlViewModel;
    private final Function<CON, AbstractTimedSimulationManager<ENT, CON, STA>> simulationManagerFactory;
    private final SimulationTimer timer;
    private final ExecutorService batchExecutor;
    private @Nullable AbstractTimedSimulationManager<ENT, CON, STA> simulationManager;
    private @Nullable Future<?> batchFuture;
    private volatile @Nullable Thread batchThread;
    private long timeoutExecuteMillis = Long.MAX_VALUE;
    private long timeoutViewMillis = Long.MAX_VALUE;
    private long throttleDrawMillis = Long.MAX_VALUE;

    // Listener for view
    private Runnable simulationInitializedListener = () -> {};
    private Consumer<SimulationStepEvent> simulationStepListener = _ -> {};

    public DefaultMainViewModel(ObjectProperty<SimulationState> simulationState,
                                SimulationConfigViewModel<CON> configViewModel,
                                DefaultControlViewModel controlViewModel,
                                DefaultObservationViewModel<ENT, STA> observationViewModel,
                                Function<CON, AbstractTimedSimulationManager<ENT, CON, STA>> simulationManagerFactory) {
        super(simulationState, configViewModel, observationViewModel);
        this.controlViewModel = controlViewModel;
        this.simulationManagerFactory = simulationManagerFactory;
        timer = new SimulationTimer(this::runTimerStep);
        batchExecutor = Executors.newSingleThreadExecutor();

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

    public void setSimulationInitializedListener(Runnable listener) {
        simulationInitializedListener = listener;
    }

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
        setSimulationState(SimulationState.SHUTTING_DOWN);
        stopTimer();
        cancelBatch();
        shutdownBatchExecutor();
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
    public ReadableGridModel<ENT> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    public int getStepCount() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.stepCount();
    }

    public long getThrottleDrawMillis() {
        return throttleDrawMillis;
    }

    private void handleActionButton() {
        if (!getSimulationState().isRunning() && (isTimerRunning() || isBatchRunning())) {
            throw new IllegalStateException("Simulation is running but state is not RUNNING_TIMED or RUNNING_BATCH: " + getSimulationState());
        }

        if (getSimulationState().canStart()) {
            handleStartAction();
        } else if (getSimulationState().isRunning()) {
            handlePauseAction();
        } else if (getSimulationState().isPaused()) {
            handleResumeAction();
        } else {
            AppLogger.warn("Cannot handle action button in current state: " + getSimulationState());
        }
    }

    private void handleCancelButton() {
        // Reset notification type.
        setNotificationType(SimulationNotificationType.NONE);

        // Stop batch and timer, if running.
        cancelBatch();
        stopTimer();

        if (getSimulationState() == SimulationState.RUNNING_TIMED) {
            setSimulationState(SimulationState.CANCELLED);
            logSimulationInfo("Simulation (timer) was canceled by the user.");

            notifyFinalStepAndStopTimer();
        } else if (getSimulationState() == SimulationState.RUNNING_BATCH) {
            setSimulationState(SimulationState.CANCELLING_BATCH);
            logSimulationInfo("Simulation (batch) was canceled by the user. Waiting for batch to finish.");
        } else if (getSimulationState() == SimulationState.PAUSED) {
            setSimulationState(SimulationState.CANCELLED);
            logSimulationInfo("Simulation (paused) was canceled by the user.");
        }
    }

    private void handleStartAction() {
        // Reset notification type.
        setNotificationType(SimulationNotificationType.NONE);

        resetClickedCoordinateProperties();

        long start = System.currentTimeMillis();
        try {
            Optional<CON> config = createValidConfig();
            if (config.isEmpty()) {
                setSimulationState(SimulationState.ERROR);
                AppLogger.warn("Cannot start simulation, because configuration is invalid.");
                setNotificationType(SimulationNotificationType.INVALID_CONFIG);
                return;
            }

            createAndInitSimulation(config.get());
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException
                 | IndexOutOfBoundsException | NoSuchElementException e) {
            setSimulationState(SimulationState.ERROR);
            AppLogger.error("Failed to start simulation: " + e.getMessage(), e);
            setNotificationType(SimulationNotificationType.EXCEPTION);
            return;
        }
        long duration = System.currentTimeMillis() - start;

        if (controlViewModel.isStartPaused()) {
            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation was started in paused state by the user. duration=" + duration);
        } else if (controlViewModel.isModeTimed()) {
            setSimulationState(SimulationState.RUNNING_TIMED);
            logSimulationInfo("Simulation (timer) was started by the user. duration=" + duration);

            startTimer();
        } else if (controlViewModel.isModeBatch()) {
            setSimulationState(SimulationState.RUNNING_BATCH);
            logSimulationInfo("Simulation (batch) was started by the user. duration=" + duration);

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

        configureSimulationTimeout();

        updateObservationStatistics(simulationManager.statistics());

        simulationInitializedListener.run();
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void configureSimulationTimeout() {
        if (controlViewModel.isModeTimed()) {
            double stepDuration = controlViewModel.stepDurationProperty().getValue();
            timeoutExecuteMillis = Math.max(1L, (long) (stepDuration * TIMEOUT_EXECUTE_FACTOR));
            timeoutViewMillis = Math.max(1L, (long) (stepDuration * TIMEOUT_VIEW_FACTOR));
            throttleDrawMillis = Math.max(1L, (long) (stepDuration * THROTTLE_DRAW_FACTOR));
        }
    }

    private void runTimerStep() {
        // Check simulation manager and state
        if (simulationManager == null) {
            AppLogger.error("Simulation manager is not initialized, cannot execute step.");
            stopTimer();
            return;
        }
        if (getSimulationState() != SimulationState.RUNNING_TIMED) {
            AppLogger.error("Simulation is not in RUNNING_TIMED state, cannot execute step.");
            stopTimer();
            return;
        }

        // Execute simulation step
        simulationManager.executeStep();

        AppLogger.debug(() -> "Simulation (timer) has executed step. duration=" + simulationManager.stepTimingStatistics().current());

        // Update statistics
        updateObservationStatistics(simulationManager.statistics());

        // Check if simulation finished
        if (controlViewModel.isTerminationChecked() && simulationManager.isFinished()) {
            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation (timer) has ended itself.");
        }

        if (simulationManager.isExecutorFinished()) {
            setSimulationState(SimulationState.FINISHED);
            logSimulationInfo("Simulation (timer) executor has finished.");
        }

        // Notify view about the step and measure duration
        long startView = System.currentTimeMillis();
        simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount(), false));
        long durationView = System.currentTimeMillis() - startView;

        AppLogger.debug(() -> "Simulation (timer) has informed step listener. duration=" + durationView);

        // Check timeout if still running (not finished) and not the first step
        if ((getSimulationState() == SimulationState.RUNNING_TIMED) && (simulationManager.stepCount() > 1)) {
            // Check for calculation timeout
            if (simulationManager.stepTimingStatistics().current() > timeoutExecuteMillis) {
                setNotificationType(SimulationNotificationType.TIMEOUT);

                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation (timer) has been paused because the simulation step took too long to " +
                        "calculate. duration=" + simulationManager.stepTimingStatistics().current() + " " +
                        "timeoutExecuteMillis=" + timeoutExecuteMillis);
            }

            // Check for view timeout
            if (durationView > timeoutViewMillis) {
                setNotificationType(SimulationNotificationType.TIMEOUT);

                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation (timer) has been paused because the view took too long to process. " +
                        "duration=" + durationView + " " +
                        "timeoutViewMillis=" + timeoutViewMillis);
            }
        }

        // If simulation is paused or finished, notify view for final step and stop timer.
        if (getSimulationState() != SimulationState.RUNNING_TIMED) {
            notifyFinalStepAndStopTimer();
        }
    }

    private void runBatchSteps(int count, boolean checkTermination, boolean restartBatchIfPossible) {
        batchFuture = batchExecutor.submit(() -> {
            batchThread = Thread.currentThread();
            try {
                if (simulationManager == null) {
                    AppLogger.error("Simulation manager is not initialized, cannot execute steps.");
                    return;
                }

                var executionResult = simulationManager.executeSteps(count, checkTermination, () -> {
                    // Create the event before the "runLater".
                    var stepEvent = new SimulationStepEvent(true, simulationManager.stepCount(), false);
                    Platform.runLater(() -> {
                        // Check at JavaFX-Thread if it is still running.
                        if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                            simulationStepListener.accept(stepEvent);
                        }
                    });
                });

                logSimulationInfo("Simulation (batch) finished. Requested steps: " + count + ", " + executionResult);

                // Create the event and statistics before the "runLater".
                var stepEvent = new SimulationStepEvent(false, simulationManager.stepCount(), true);
                var statistics = simulationManager.statistics();
                boolean executorFinished = executionResult.isFinished() && simulationManager.isExecutorFinished();

                Platform.runLater(() -> {
                    if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state RUNNING_BATCH.");
                        if (executorFinished) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation executor has finished.");
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                            if (executionResult.isFinished()) {
                                logSimulationInfo("Simulation has ended itself.");
                            }
                        }
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                        if (restartBatchIfPossible && !executionResult.isFinished()) {
                            setSimulationState(SimulationState.RUNNING_BATCH);
                            logSimulationInfo("Restarting batch execution for next steps.");
                            runBatchSteps(count, checkTermination, restartBatchIfPossible);
                        }
                    } else if (getSimulationState() == SimulationState.PAUSING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state PAUSING_BATCH.");
                        if (executorFinished) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation executor has finished.");
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                            if (executionResult.isFinished()) {
                                logSimulationInfo("Simulation has ended itself.");
                            }
                        }
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                    } else if (getSimulationState() == SimulationState.CANCELLING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state CANCELLING_BATCH.");
                        setSimulationState(SimulationState.CANCELLED);
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                    } else if (getSimulationState() == SimulationState.SHUTTING_DOWN) {
                        logSimulationInfo("Finishing batch execution at state SHUTTING_DOWN.");
                    } else {
                        AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation is not in a valid state for batch execution: " + getSimulationState());
                    }
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
        observationViewModel.setStatistics(statistics);
    }

    private void logSimulationInfo(String message) {
        if (simulationManager == null) {
            AppLogger.info(message);
        } else {
            AppLogger.info(message + " config=" + simulationManager.config() + ", statistics=" + simulationManager.statistics());
        }
    }

}
