package de.mkalb.etpetssim.simulations.conwayslife.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwaySimulationManager;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayViewModel {

    private final ConwayConfigViewModel configViewModel;
    private final ConwayObservationViewModel observationViewModel;
    private final SimulationTimer simulationTimer;

    private final ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.READY);

    private @Nullable Runnable simulationInitializedListener;
    private @Nullable Runnable simulationStepListener;

    private @Nullable ConwaySimulationManager simulationManager;

    public ConwayViewModel() {
        configViewModel = new ConwayConfigViewModel(simulationState);
        observationViewModel = new ConwayObservationViewModel(simulationState);
        simulationTimer = new SimulationTimer(this::doSimulationStep);
    }

    public ConwayConfigViewModel getConfigViewModel() {
        return configViewModel;
    }

    public ConwayObservationViewModel getObservationViewModel() {
        return observationViewModel;
    }

    public void setSimulationInitializedListener(@Nullable Runnable listener) {
        simulationInitializedListener = listener;
    }

    public void setSimulationStepListener(@Nullable Runnable listener) {
        simulationStepListener = listener;
    }

    public ObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public void setSimulationState(SimulationState state) {
        simulationState.set(state);
    }

    public double getCellEdgeLength() {
        return configViewModel.getCellEdgeLength();
    }

    public GridStructure getGridStructure() {
        Objects.requireNonNull(simulationManager, "Simulation executor must not be null before accessing the grid structure.");
        return simulationManager.currentModel().structure();
    }

    public ReadableGridModel<ConwayEntity> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation executor must not be null before accessing the current model.");
        return simulationManager.currentModel();
    }

    public long getCurrentStep() {
        Objects.requireNonNull(simulationManager, "Simulation manager must not be null before accessing the current step.");
        return simulationManager.currentStep();
    }

    private void startSimulation() {
        simulationManager = new ConwaySimulationManager(configViewModel.getConfig());

        observationViewModel.setStatistics(simulationManager.statistics());

        if (simulationInitializedListener != null) {
            simulationInitializedListener.run();
        }
    }

    private void doSimulationStep() {
        Objects.requireNonNull(simulationManager, "Simulation manager must not be null before executing a step.");
        AppLogger.info("Simulation step started.");

        simulationManager.executeStep();

        observationViewModel.setStatistics(simulationManager.statistics());

        if (simulationStepListener != null) {
            simulationStepListener.run();
        }

        if (!simulationManager.isRunning()) {
            stopTimeline();
            AppLogger.info("Simulation finished at step " + getCurrentStep());
            setSimulationState(SimulationState.READY); // Set state to READY when finished
        }
    }

    private void startTimeline() {
        // TODO Optimize with speedProperty
        simulationTimer.start(Duration.millis(300));
    }

    private void stopTimeline() {
        if (simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
    }

    public void onActionButton() {
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
                startTimeline();
            }
        }
    }

    public void onCancelButton() {
        stopTimeline();
        AppLogger.info("Cancelling simulation...");
        setSimulationState(SimulationState.READY);
    }

}