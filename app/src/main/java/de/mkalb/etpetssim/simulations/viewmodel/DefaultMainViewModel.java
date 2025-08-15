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

    // Manager
    private final Function<CON, AbstractTimedSimulationManager<ENT, CON, STA>> simulationManagerFactory;
    // Execution (live or batch)
    private final SimulationTimer liveTimer;
    private final ExecutorService batchExecutor;
    private @Nullable AbstractTimedSimulationManager<ENT, CON, STA> simulationManager;
    private @Nullable Future<?> batchFuture;
    private volatile @Nullable Thread batchThread;

    // Listener
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

    public ReadableGridModel<ENT> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    public int getStepCount() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.stepCount();
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
    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config().cellEdgeLength();
    }

    private void logSimulationInfo(String message) {
        if (simulationManager == null) {
            AppLogger.info(Thread.currentThread().getName() + " : " + message);
        } else {
            AppLogger.info(Thread.currentThread().getName() + " : " + message + " config=" + simulationManager.config() + ", statistics=" + simulationManager.statistics());
        }
    }

    private void handleSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        if ((getSimulationState() == SimulationState.RUNNING_LIVE) && isLiveRunning()) {
            stopLiveTimer();
            setSimulationState(SimulationState.PAUSED);
            setSimulationTimeout(true);

            logSimulationInfo("Simulation has been paused because a timeout has occurred.");
        }
    }

    private void configureSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        if (controlViewModel.isLiveMode()) {
            double stepDuration = getStepDuration();
            long timeoutMillis = (long) (stepDuration * TIMEOUT_FACTOR);
            simulationManager.configureStepTimeout(timeoutMillis, this::handleSimulationTimeout);
        } else if (controlViewModel.isBatchMode()) {
            simulationManager.configureStepTimeout(Long.MAX_VALUE, () -> {});
        }
        setSimulationTimeout(false);
    }

    private double getStepDuration() {
        return controlViewModel.stepDurationProperty().getValue();
    }

    private void updateObservationStatistics(STA statistics) {
        observationViewModel.setStatistics(statistics);
    }

    private void runLiveStep() {
        if (!liveTimer.isRunning()) {
            AppLogger.error("Simulation timer is not running, cannot execute step.");
            return;
        }
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
            stopLiveTimer();
            setSimulationState(SimulationState.FINISHED);
            logSimulationInfo("Simulation has ended itself.");
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

    private void startLiveTimer() {
        liveTimer.start(Duration.millis(getStepDuration()));
    }

    private void stopLiveTimer() {
        liveTimer.stop();
    }

    private void handleActionButton() {
        if (getSimulationState().canStart()) {
            if (isLiveRunning()) {
                setSimulationState(SimulationState.ERROR);
                AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation timer is already running, cannot start a new simulation.");
                // TODO show message at view
                return;
            }
            if (isBatchRunning()) {
                setSimulationState(SimulationState.ERROR);
                AppLogger.error(Thread.currentThread().getName() + " : " + "A batch is already running, cannot start a new simulation.");
                // TODO show message at view
                return;
            }
            Optional<CON> config = createValidConfig();
            if (config.isEmpty()) {
                setSimulationState(SimulationState.ERROR);
                AppLogger.warn("Invalid configuration: " + config);
                // TODO show message at view
            } else if (controlViewModel.isLiveMode()) {
                createAndInitSimulation(config.get());
                setSimulationState(SimulationState.RUNNING_LIVE);
                logSimulationInfo("Simulation (live) was started by the user.  ");
                startLiveTimer();
            } else if (controlViewModel.isBatchMode()) {
                createAndInitSimulation(config.get());
                setSimulationState(SimulationState.RUNNING_BATCH);
                logSimulationInfo("Simulation (batch) was started by the user.  ");
                runBatchSteps(100);
            }
        } else if (getSimulationState() == SimulationState.RUNNING_LIVE) {
            stopLiveTimer();
            setSimulationState(SimulationState.PAUSED);
            logSimulationInfo("Simulation (live) was paused by the user.   ");
        } else if (getSimulationState() == SimulationState.RUNNING_BATCH) {
            setSimulationState(SimulationState.PAUSING_BATCH);
            logSimulationInfo("Simulation (batch) is pausing, waiting for batch to finish.  ");
            cancelBatch();
        } else if (getSimulationState() == SimulationState.PAUSED) {
            if (controlViewModel.isLiveMode()) {
                configureSimulationTimeout();
                setSimulationState(SimulationState.RUNNING_LIVE);
                logSimulationInfo("Simulation (live) was resumed by the user.  ");
                startLiveTimer();
            } else if (controlViewModel.isBatchMode()) {
                configureSimulationTimeout();
                setSimulationState(SimulationState.RUNNING_BATCH);
                logSimulationInfo("Simulation (batch) was resumed by the user.  ");
                runBatchSteps(100);
            }
        } else {
            setSimulationState(SimulationState.ERROR);
            AppLogger.error(Thread.currentThread().getName() + " : " + "Cannot handle action button in current state: " + getSimulationState());
        }
    }

    private void handleCancelButton() {
        if (getSimulationState() == SimulationState.RUNNING_LIVE) {
            stopLiveTimer();
            setSimulationState(SimulationState.CANCELLED);
            setSimulationTimeout(false);
            logSimulationInfo("Simulation was canceled by the user. ");
        } else if (getSimulationState() == SimulationState.RUNNING_BATCH) {
            setSimulationState(SimulationState.CANCELLING_BATCH);
            cancelBatch();
        } else if (getSimulationState() == SimulationState.PAUSED) {
            setSimulationState(SimulationState.CANCELLED);
            setSimulationTimeout(false);
            logSimulationInfo("Simulation was canceled by the user. ");
        }
    }

    private boolean isLiveRunning() {
        return liveTimer.isRunning();
    }

    private boolean isBatchRunning() {
        Thread thread = batchThread;
        return (thread != null) && thread.isAlive();
    }

    private void runBatchSteps(int count) {
        if (getSimulationState() != SimulationState.RUNNING_BATCH) {
            AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation is not in RUNNING_BATCH state, cannot execute steps.");
            return;
        }
        if (isLiveRunning()) {
            AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation timer is running, cannot execute steps in batch mode.");
            return;
        }
        if (isBatchRunning()) {
            AppLogger.error(Thread.currentThread().getName() + " : " + "A batch is already running. New batch will not be started.");
            return;
        }

        batchFuture = batchExecutor.submit(() -> {
            batchThread = Thread.currentThread();
            try {
                if (getSimulationState() != SimulationState.RUNNING_BATCH) {
                    AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation is not in RUNNING_BATCH state, cannot execute steps.");
                    return;
                }
                if (isLiveRunning()) {
                    AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation timer is running, cannot execute steps in batch mode.");
                    return;
                }
                if (simulationManager == null) {
                    AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation manager is not initialized, cannot execute steps.");
                    stopLiveTimer();
                    return;
                }

                simulationManager.executeSteps(count, () -> {
                    SimulationStepEvent stepEvent = new SimulationStepEvent(true, simulationManager.stepCount());
                    Platform.runLater(() -> {
                        if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                            simulationStepListener.accept(stepEvent);
                        }
                    });
                });
                logSimulationInfo("Batch execution finished. Plan to finish batch execution in UI thread.");
                Platform.runLater(() -> {
                    if (getSimulationState() == SimulationState.RUNNING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state RUNNING_BATCH.");
                        if (!simulationManager.isRunning()) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation has ended itself.");
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                        }
                        updateObservationStatistics(simulationManager.statistics());
                        simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount()));
                    } else if (getSimulationState() == SimulationState.PAUSING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state PAUSING_BATCH.");
                        if (!simulationManager.isRunning()) {
                            setSimulationState(SimulationState.FINISHED);
                            logSimulationInfo("Simulation has ended itself.");
                        } else {
                            setSimulationState(SimulationState.PAUSED);
                        }
                        updateObservationStatistics(simulationManager.statistics());
                        simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount()));
                    } else if (getSimulationState() == SimulationState.CANCELLING_BATCH) {
                        logSimulationInfo("Finishing batch execution at state CANCELLING_BATCH.");
                        setSimulationState(SimulationState.CANCELLED);
                        updateObservationStatistics(simulationManager.statistics());
                        simulationStepListener.accept(new SimulationStepEvent(false, simulationManager.stepCount()));
                    } else if (getSimulationState() == SimulationState.SHUTTING_DOWN) {
                        logSimulationInfo("Finishing batch execution at state SHUTTING_DOWN.");
                    } else {
                        AppLogger.error(Thread.currentThread().getName() + " : " + "Simulation is not in a valid state for batch execution: " + getSimulationState());
                    }
                });
            } finally {
                batchThread = null;
                logSimulationInfo("Batch thread has finished execution.");
            }
        });
    }

    private void cancelBatch() {
        if ((batchFuture != null) && !batchFuture.isDone()) {
            batchFuture.cancel(true); // Attempts to interrupt
        }
    }

    private void shutdownThreadExecutor() {
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

    @Override
    public void shutdownSimulation() {
        setSimulationState(SimulationState.SHUTTING_DOWN);
        stopLiveTimer();
        cancelBatch();
        shutdownThreadExecutor();
    }

}
