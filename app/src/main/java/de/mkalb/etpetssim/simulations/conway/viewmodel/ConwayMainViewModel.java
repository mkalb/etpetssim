package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractMainViewModel;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayMainViewModel extends AbstractMainViewModel {

    private final ConwayConfigViewModel configViewModel;
    private final ConwayControlViewModel controlViewModel;
    private final ConwayObservationViewModel observationViewModel;
    private final SimulationTimer simulationTimer;

    private Runnable simulationInitializedListener = () -> {};
    private Runnable simulationStepListener = () -> {};

    private @Nullable ConwaySimulationManager simulationManager;

    public ConwayMainViewModel(ObjectProperty<SimulationState> simulationState,
                               ConwayConfigViewModel configViewModel,
                               ConwayControlViewModel controlViewModel,
                               ConwayObservationViewModel observationViewModel) {
        super(simulationState);
        this.configViewModel = configViewModel;
        this.controlViewModel = controlViewModel;
        this.observationViewModel = observationViewModel;
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

    public double getCellEdgeLength() {
        return configViewModel.cellEdgeLengthProperty().getValue();
    }

    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.structure();
    }

    public ReadableGridModel<ConwayEntity> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    public long getCurrentStep() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentStep();
    }

    private void startSimulation() {
        simulationManager = new ConwaySimulationManager(configViewModel.getConfig());

        observationViewModel.setStatistics(simulationManager.statistics());

        simulationInitializedListener.run();
    }

    private void doSimulationStep() {
        Objects.requireNonNull(simulationManager, "Simulation manager must not be null before executing a step.");
        AppLogger.info("Simulation step started.");

        simulationManager.executeStep();

        observationViewModel.setStatistics(simulationManager.statistics());

        simulationStepListener.run();

        if (!simulationManager.isRunning()) {
            stopTimeline();
            AppLogger.info("Simulation finished at step " + getCurrentStep());
            setSimulationState(SimulationState.READY); // Set state to READY when finished
        }
    }

    private void startTimeline() {
        simulationTimer.start(Duration.millis(controlViewModel.stepDurationProperty().getValue()));
    }

    private void stopTimeline() {
        if (simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
    }

    public void handleActionButton() {
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

    public void handleCancelButton() {
        stopTimeline();
        AppLogger.info("Cancelling simulation...");
        setSimulationState(SimulationState.READY);
    }

}
