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

public final class DefaultMainViewModel<ENT extends GridEntity, CON extends SimulationConfig,
        STA extends TimedSimulationStatistics>
        extends AbstractMainViewModel<CON, STA> {

    private static final double TIMEOUT_FACTOR = 0.5d;

    private final DefaultControlViewModel controlViewModel;
    private final Function<CON, AbstractTimedSimulationManager<ENT, CON, STA>> simulationManagerFactory;
    private final SimulationTimer liveTimer;
    private final ExecutorService batchExecutor;
    private @Nullable AbstractTimedSimulationManager<ENT, CON, STA> simulationManager;
    private @Nullable Future<?> batchFuture;
    private volatile @Nullable Thread batchThread;

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

    // TODO Add to AbstractMainViewModel
    public ReadableGridModel<ENT> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    // TODO Add to AbstractMainViewModel
    public int getStepCount() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.stepCount();
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
        // Stop batch and live, if running.
        cancelBatch();
        stopLiveTimer();
        // Reset timeout
        setSimulationTimeout(false);

        if (getSimulationState() == SimulationState.RUNNING_LIVE) {
            setSimulationState(SimulationState.CANCELLED);
            logSimulationInfo("Simulation (live) was canceled by the user.");
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
            // TODO show message at view
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

            stopLiveTimer();
        } else if (getSimulationState() == SimulationState.RUNNING_BATCH) {
            setSimulationState(SimulationState.PAUSING_BATCH);
            logSimulationInfo("Simulation (batch) was paused by the user. Waiting for batch to finish.");

            cancelBatch();
        }
    }

    private void handleResumeAction() {
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
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        if (controlViewModel.isLiveMode()) {
            double stepDuration = getControlStepDuration();
            long timeoutMillis = (long) (stepDuration * TIMEOUT_FACTOR);
            simulationManager.configureStepTimeout(timeoutMillis, this::handleSimulationTimeout);
        } else if (controlViewModel.isBatchMode()) {
            simulationManager.configureStepTimeout(Long.MAX_VALUE, () -> {});
        }
        setSimulationTimeout(false);
    }

    private void handleSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        if ((getSimulationState() == SimulationState.RUNNING_LIVE) && isLiveRunning()) {
            setSimulationTimeout(true);

            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation (live) has been paused because a timeout has occurred.");

            stopLiveTimer();
        }
    }

    private void runLiveStep() {
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

        simulationManager.executeStep();

        updateObservationStatistics(simulationManager.statistics());

        simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount()));

        if (!simulationManager.isRunning()) {
            setSimulationState(SimulationState.FINISHED);
            logSimulationInfo("Simulation (live) has ended itself.");

            stopLiveTimer();
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
                    var stepEvent = new SimulationStepEvent(true, simulationManager.stepCount());
                    Platform.runLater(() -> {
                        // Check at JavaFX-Thread if it is still running.
                        if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                            simulationStepListener.accept(stepEvent);
                        }
                    });
                });

                logSimulationInfo("Simulation (batch) finished. Requested steps: " + count + ", " + executionResult);

                // Create the event and statistics before the "runLater".
                var stepEvent = new SimulationStepEvent(false, simulationManager.stepCount());
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
