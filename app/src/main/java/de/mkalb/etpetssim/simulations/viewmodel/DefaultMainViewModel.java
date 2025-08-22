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
    private final SimulationTimer liveTimer;
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
                                DefaultObservationViewModel<STA> observationViewModel,
                                Function<CON, AbstractTimedSimulationManager<ENT, CON, STA>> simulationManagerFactory) {
        super(simulationState, configViewModel, observationViewModel);
        this.controlViewModel = controlViewModel;
        this.simulationManagerFactory = simulationManagerFactory;
        liveTimer = new SimulationTimer(this::runLiveStep);
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
        stopLiveTimer();
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
        if (!getSimulationState().isRunning() && (isLiveRunning() || isBatchRunning())) {
            throw new IllegalStateException("Simulation is running but state is not RUNNING_LIVE or RUNNING_BATCH: " + getSimulationState());
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

        // Stop batch and live, if running.
        cancelBatch();
        stopLiveTimer();

        if (getSimulationState() == SimulationState.RUNNING_LIVE) {
            setSimulationState(SimulationState.CANCELLED);
            logSimulationInfo("Simulation (live) was canceled by the user.");

            notifyFinalStepAndStopLiveTimer();
        } else if (getSimulationState() == SimulationState.RUNNING_BATCH) {
            setSimulationState(SimulationState.CANCELLING_BATCH);
            logSimulationInfo("Simulation (batch) was canceled by the user. Waiting for batch to finish.");
        } else if (getSimulationState() == SimulationState.PAUSED) {
            setSimulationState(SimulationState.CANCELLED);
            logSimulationInfo("Simulation (paused) was canceled by the user.");
        }
    }

    private void handleStartAction() {
        Optional<CON> config = createValidConfig();
        if (config.isEmpty()) {
            setSimulationState(SimulationState.ERROR);
            AppLogger.warn("Cannot start simulation, because configuration is invalid. " + config);
            setNotificationType(SimulationNotificationType.INVALID_CONFIG);
            return;
        }

        createAndInitSimulation(config.get());
        if (controlViewModel.isLiveMode()) {
            setSimulationState(SimulationState.RUNNING_LIVE);
            logSimulationInfo("Simulation (live) was started by the user.");

            startLiveTimer();
        } else if (controlViewModel.isBatchMode()) {
            setSimulationState(SimulationState.RUNNING_BATCH);
            logSimulationInfo("Simulation (batch) was started by the user.");

            runBatchSteps(getControlStepCount());
        }
    }

    private void handlePauseAction() {
        if (getSimulationState() == SimulationState.RUNNING_LIVE) {
            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation (live) was paused by the user.");

            notifyFinalStepAndStopLiveTimer();
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
        if (controlViewModel.isLiveMode()) {
            setSimulationState(SimulationState.RUNNING_LIVE);
            logSimulationInfo("Simulation (live) was resumed by the user.");

            startLiveTimer();
        } else if (controlViewModel.isBatchMode()) {
            setSimulationState(SimulationState.RUNNING_BATCH);
            logSimulationInfo("Simulation (batch) was resumed by the user.");

            runBatchSteps(getControlStepCount());
        }
    }

    private Optional<CON> createValidConfig() {
        try {
            CON config = configViewModel.getConfig();
            if (!config.isValid()) {
                return Optional.empty();
            }
            return Optional.of(config);
        } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
            AppLogger.error("Failed to create simulation configuration: " + e.getMessage(), e);
            return Optional.empty();
        }
    }

    private void createAndInitSimulation(CON config) {
        // TODO handle initialization errors
        simulationManager = simulationManagerFactory.apply(config);

        configureSimulationTimeout();

        updateObservationStatistics(simulationManager.statistics());

        simulationInitializedListener.run();
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void configureSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        if (controlViewModel.isLiveMode()) {
            double stepDuration = getControlStepDuration();
            timeoutExecuteMillis = Math.max(1L, (long) (stepDuration * TIMEOUT_EXECUTE_FACTOR));
            timeoutViewMillis = Math.max(1L, (long) (stepDuration * TIMEOUT_VIEW_FACTOR));
            throttleDrawMillis = Math.max(1L, (long) (stepDuration * THROTTLE_DRAW_FACTOR));
        }
    }

    private void runLiveStep() {
        // Check simulation manager and state
        if (simulationManager == null) {
            AppLogger.error("Simulation manager is not initialized, cannot execute step.");
            stopLiveTimer();
            return;
        }
        if (getSimulationState() != SimulationState.RUNNING_LIVE) {
            AppLogger.error("Simulation is not in RUNNING_LIVE state, cannot execute step.");
            stopLiveTimer();
            return;
        }

        // Execute simulation step
        simulationManager.executeStep();

        // Update statistics
        updateObservationStatistics(simulationManager.statistics());

        // Check if simulation finished
        if (!simulationManager.isRunning()) {
            setSimulationState(SimulationState.FINISHED);
            logSimulationInfo("Simulation (live) has ended itself.");
        }

        // Notify view about the step and measure duration
        long startView = System.currentTimeMillis();
        simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount(), false));
        long durationView = System.currentTimeMillis() - startView;

        // Check timeout if still running (not finished)
        if (getSimulationState() == SimulationState.RUNNING_LIVE) {
            // Check for calculation timeout
            if (simulationManager.stepTimingStatistics().current() > timeoutExecuteMillis) {
                setNotificationType(SimulationNotificationType.TIMEOUT);

                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation (live) has been paused because the simulation step took too long to " +
                        "calculate. duration=" + simulationManager.stepTimingStatistics().current() + " " +
                        "timeoutExecuteMillis=" + timeoutExecuteMillis);
            }

            // Check for view timeout
            if (durationView > timeoutViewMillis) {
                setNotificationType(SimulationNotificationType.TIMEOUT);

                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation (live) has been paused because the view took too long to process. " +
                        "duration=" + durationView + " " +
                        "timeoutViewMillis=" + timeoutViewMillis);
            }
        }

        // If simulation is paused or finished, notify view for final step and stop live timer.
        if (getSimulationState() != SimulationState.RUNNING_LIVE) {
            notifyFinalStepAndStopLiveTimer();
        }
    }

    private void runBatchSteps(int count) {
        batchFuture = batchExecutor.submit(() -> {
            batchThread = Thread.currentThread();
            try {
                if (simulationManager == null) {
                    AppLogger.error("Simulation manager is not initialized, cannot execute steps.");
                    return;
                }

                var executionResult = simulationManager.executeSteps(count, () -> {
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

                Platform.runLater(() -> {
                    if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state RUNNING_BATCH.");
                        if (!executionResult.isRunning()) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation has ended itself.");
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                        }
                        updateObservationStatistics(statistics);
                        simulationStepListener.accept(stepEvent);
                    } else if (getSimulationState() == SimulationState.PAUSING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state PAUSING_BATCH.");
                        if (!executionResult.isRunning()) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation has ended itself.");
                        } else {
                            setSimulationState(SimulationState.PAUSED);
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

    private boolean isLiveRunning() {
        return liveTimer.isRunning();
    }

    private boolean isBatchRunning() {
        Thread thread = batchThread;
        return (thread != null) && thread.isAlive();
    }

    private void startLiveTimer() {
        liveTimer.start(Duration.millis(getControlStepDuration()));
    }

    private void stopLiveTimer() {
        liveTimer.stop();
    }

    private void notifyFinalStepAndStopLiveTimer() {
        if (simulationManager != null) {
            simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount(), true));
        }
        stopLiveTimer();
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

    private double getControlStepDuration() {
        return controlViewModel.stepDurationProperty().getValue();
    }

    private int getControlStepCount() {
        return controlViewModel.stepCountProperty().getValue();
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
