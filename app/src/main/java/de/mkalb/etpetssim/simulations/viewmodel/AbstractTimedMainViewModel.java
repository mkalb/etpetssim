package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.*;

public abstract class AbstractTimedMainViewModel<ENT extends GridEntity, CON, STA> extends AbstractMainViewModel {

    private static final double TIMEOUT_FACTOR = 0.5d;

    private final SimulationTimer simulationTimer;
    private @Nullable AbstractTimedSimulationManager<ENT, CON, STA> simulationManager;
    private Runnable simulationInitializedListener = () -> {};
    private Runnable simulationStepListener = () -> {};

    protected AbstractTimedMainViewModel(ObjectProperty<SimulationState> simulationState) {
        super(simulationState);
        simulationTimer = new SimulationTimer(this::doSimulationStep);
    }

    public final void setSimulationInitializedListener(Runnable listener) {
        simulationInitializedListener = listener;
    }

    public final void setSimulationStepListener(Runnable listener) {
        simulationStepListener = listener;
    }

    public final GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.structure();
    }

    public final ReadableGridModel<ENT> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    public final int getStepCount() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.stepCount();
    }

    public final CON getCurrentConfig() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config();
    }

    private void handleSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        stopTimeline();
        //     AppLogger.warn("Simulation step took too long at step " + getStepCount() +
        //             " (" + simulationManager.statistics().currentStepMillis() + " ms), pausing simulation.");
        setSimulationState(SimulationState.PAUSED);
        setSimulationTimeout(true);
    }

    private void configureSimulationTimeout() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        double stepDuration = getStepDuration();
        long timeoutMillis = (long) (stepDuration * TIMEOUT_FACTOR);
        simulationManager.configureStepTimeout(timeoutMillis, this::handleSimulationTimeout);
        setSimulationTimeout(false);
    }

    public abstract double getCellEdgeLength();

    protected abstract double getStepDuration();

    protected abstract void updateObservationStatistics(STA statistics);

    protected abstract AbstractTimedSimulationManager<ENT, CON, STA> createSimulationManager();

    private void doSimulationStep() {
        Objects.requireNonNull(simulationManager, "Simulation manager must not be null before executing a step.");
        AppLogger.info("Simulation step started.");

        simulationManager.executeStep();

        updateObservationStatistics(simulationManager.statistics());

        simulationStepListener.run();

        if (!simulationManager.isRunning()) {
            stopTimeline();
            AppLogger.info("Simulation finished after step " + getStepCount());
            setSimulationState(SimulationState.READY); // Set state to READY when finished
        }
    }

    private void startSimulation() {
        simulationManager = createSimulationManager();
        configureSimulationTimeout();

        updateObservationStatistics(simulationManager.statistics());

        simulationInitializedListener.run();
    }

    private void startTimeline() {
        simulationTimer.start(Duration.millis(getStepDuration()));
    }

    private void stopTimeline() {
        if (simulationTimer.isRunning()) {
            simulationTimer.stop();
        }
    }

    protected final void handleActionButton() {
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

    protected final void handleCancelButton() {
        stopTimeline();
        AppLogger.info("Cancelling simulation...");
        setSimulationState(SimulationState.READY);
        setSimulationTimeout(false);
    }

}
