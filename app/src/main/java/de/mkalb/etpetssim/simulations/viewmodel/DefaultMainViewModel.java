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
        STA extends SimulationStatistics>
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

    private void handleSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        stopTimeline();
        setSimulationState(SimulationState.PAUSED);
        setSimulationTimeout(true);

        AppLogger.info("Simulation has been paused because a timeout has occurred. config=" + simulationManager.config() + ", statistics=" + simulationManager.statistics());
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
        Objects.requireNonNull(simulationManager, "Simulation manager must not be null before executing a step.");
        AppLogger.info("Simulation step started.");

        simulationManager.executeStep();

        updateObservationStatistics(simulationManager.statistics());

        simulationStepListener.run();

        if (!simulationManager.isRunning()) {
            stopTimeline();
            setSimulationState(SimulationState.READY); // Set state to READY when finished
            AppLogger.info("Simulation has ended itself. config=" + simulationManager.config() + ", statistics=" + simulationManager.statistics());
        }
    }

    private void startSimulation() {
        simulationManager = simulationManagerFactory.apply(configViewModel.getConfig());
        configureSimulationTimeout();

        updateObservationStatistics(simulationManager.statistics());

        simulationInitializedListener.run();

        AppLogger.info("Simulation was started by the user. config=" + simulationManager.config() + ", statistics=" + simulationManager.statistics());
    }

    private void startTimeline() {
        simulationTimer.start(Duration.millis(getStepDuration()));
    }

    private void stopTimeline() {
        if (simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
    }

    private void handleActionButton() {
        // Stopping the timeline first to ensure no steps are executed while changing state
        stopTimeline();
        switch (getSimulationState()) {
            case READY -> {
                AppLogger.info("Starting simulation...");
                setSimulationState(SimulationState.RUNNING);
                startSimulation();
                startTimeline();
            }
            case RUNNING -> {
                AppLogger.info("Pausing simulation...");
                setSimulationState(SimulationState.PAUSED);
            }
            case PAUSED -> {
                AppLogger.info("Resuming simulation...");
                setSimulationState(SimulationState.RUNNING);
                configureSimulationTimeout();
                startTimeline();
            }
        }
    }

    private void handleCancelButton() {
        // Stopping the timeline first to ensure no steps are executed while changing state
        stopTimeline();
        AppLogger.info("Cancelling simulation...");
        setSimulationState(SimulationState.READY);
        setSimulationTimeout(false);
        if (simulationManager != null) {
            AppLogger.info("Simulation was canceled by the user. config=" + simulationManager.config() + ", statistics=" + simulationManager.statistics());
        }
    }

}
