package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.model.*;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public final class DefaultMainViewModel<ENT extends GridEntity, CON extends SimulationConfig,
        STA extends TimedSimulationStatistics>
        extends AbstractMainViewModel<CON> {

    private static final double TIMEOUT_FACTOR = 0.5d;

    private final SimulationConfigViewModel<CON> configViewModel;
    private final DefaultControlViewModel controlViewModel;
    private final DefaultObservationViewModel<STA> observationViewModel;
    private final SimulationTimer simulationTimer;
    private final Function<CON, AbstractTimedSimulationManager<ENT, CON, STA>> simulationManagerFactory;
    private @Nullable AbstractTimedSimulationManager<ENT, CON, STA> simulationManager;
    private Runnable simulationInitializedListener = () -> {};
    private Runnable simulationStepListener = () -> {};

    public DefaultMainViewModel(ObjectProperty<SimulationState> simulationState,
                                SimulationConfigViewModel<CON> configViewModel,
                                DefaultControlViewModel controlViewModel,
                                DefaultObservationViewModel<STA> observationViewModel,
                                Function<CON, AbstractTimedSimulationManager<ENT, CON, STA>> simulationManagerFactory) {
        super(simulationState);
        this.configViewModel = configViewModel;
        this.controlViewModel = controlViewModel;
        this.observationViewModel = observationViewModel;
        this.simulationManagerFactory = simulationManagerFactory;
        simulationTimer = new SimulationTimer(this::doSimulationStep);

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

    public void setSimulationStepListener(Runnable listener) {
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

    private void logSimulationInfo(String message) {
        if (simulationManager == null) {
            AppLogger.info(message);
        } else {
            AppLogger.info(message + " config=" + simulationManager.config() + ", statistics=" + simulationManager.statistics());
        }
    }

    private void handleSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        stopTimeline();
        setSimulationState(SimulationState.PAUSED);
        setSimulationTimeout(true);

        logSimulationInfo("Simulation has been paused because a timeout has occurred.");
    }

    private void configureSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        double stepDuration = getStepDuration();
        long timeoutMillis = (long) (stepDuration * TIMEOUT_FACTOR);
        simulationManager.configureStepTimeout(timeoutMillis, this::handleSimulationTimeout);
        setSimulationTimeout(false);
    }

    @Override
    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config().cellEdgeLength();
    }

    private double getStepDuration() {
        return controlViewModel.stepDurationProperty().getValue();
    }

    private void updateObservationStatistics(STA statistics) {
        observationViewModel.setStatistics(statistics);
    }

    private void doSimulationStep() {
        if (!simulationTimer.isRunning()) {
            AppLogger.error("Simulation timer is not running, cannot execute step.");
            return;
        }
        if (simulationManager == null) {
            AppLogger.error("Simulation manager is not initialized, cannot execute step.");
            stopTimeline();
            return;
        }
        if (getSimulationState() != SimulationState.RUNNING) {
            AppLogger.error("Simulation is not in RUNNING state, cannot execute step.");
            stopTimeline();
            return;
        }

        simulationManager.executeStep();

        updateObservationStatistics(simulationManager.statistics());

        simulationStepListener.run();

        if (!simulationManager.isRunning()) {
            stopTimeline();
            setSimulationState(SimulationState.READY); // Set state to READY when finished
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

    private void startSimulation(CON config) {
        simulationManager = simulationManagerFactory.apply(config);
        configureSimulationTimeout();

        updateObservationStatistics(simulationManager.statistics());

        simulationInitializedListener.run();
    }

    private void startTimeline() {
        simulationTimer.start(Duration.millis(getStepDuration()));
    }

    private void stopTimeline() {
        simulationTimer.stop();
    }

    private void handleActionButton() {
        // Stopping the timeline first to ensure no steps are executed while changing state
        stopTimeline();
        switch (getSimulationState()) {
            case READY -> {
                Optional<CON> config = createValidConfig();
                if (config.isEmpty()) {
                    simulationManager = null;
                    AppLogger.warn("Invalid configuration: " + config);
                    // TODO show message at view
                } else {
                    setSimulationState(SimulationState.RUNNING);
                    startSimulation(config.get());
                    startTimeline();
                    logSimulationInfo("Simulation was started by the user.  ");
                }
            }
            case RUNNING -> {
                setSimulationState(SimulationState.PAUSED);
                logSimulationInfo("Simulation was paused by the user.   ");
            }
            case PAUSED -> {
                setSimulationState(SimulationState.RUNNING);
                configureSimulationTimeout();
                startTimeline();
                logSimulationInfo("Simulation was resumed by the user.  ");
            }
        }
    }

    private void handleCancelButton() {
        // Stopping the timeline first to ensure no steps are executed while changing state
        stopTimeline();
        setSimulationState(SimulationState.READY);
        setSimulationTimeout(false);
        logSimulationInfo("Simulation was canceled by the user. ");
    }

}
