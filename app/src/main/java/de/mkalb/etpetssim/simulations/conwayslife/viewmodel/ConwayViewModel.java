package de.mkalb.etpetssim.simulations.conwayslife.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.*;
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

    private @Nullable SimulationExecutor<ConwayEntity> executor;
    private @Nullable ConwayStatisticsManager statisticsManager;

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
        Objects.requireNonNull(executor, "Simulation executor must not be null before accessing the grid structure.");
        return executor.currentModel().structure();
    }

    public ReadableGridModel<ConwayEntity> getCurrentModel() {
        Objects.requireNonNull(executor, "Simulation executor must not be null before accessing the current model.");
        return executor.currentModel();
    }

    public long getCurrentStep() {
        Objects.requireNonNull(executor, "Simulation executor must not be null before accessing the current step.");
        return executor.currentStep();
    }

    private void startSimulation() {
        // Resetting the executor to ensure a fresh start
        executor = null;

        ConwayConfig config = configViewModel.getConfig();
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(config.gridWidth(), config.gridHeight())
        );
        GridModel<ConwayEntity> model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        GridInitializers.placeRandomPercent(() -> ConwayEntity.ALIVE, config.alivePercent(), new Random()).initialize(model);

        //  GridEntityUtils.placePatternAt(new GridCoordinate(10, 10), model, ConwayPatterns.glider());

        SynchronousStepRunner<ConwayEntity> runner = new SynchronousStepRunner<>(model, new ConwayUpdateStrategy(structure));

        executor = new DefaultSimulationExecutor<>(runner, runner::currentModel, new ConwayTerminationCondition());

        statisticsManager = new ConwayStatisticsManager(structure.cellCount());
        statisticsManager.update(executor);
        observationViewModel.setStatistics(statisticsManager.statistics());

        if (simulationInitializedListener != null) {
            simulationInitializedListener.run();
        }
    }

    private void doSimulationStep() {
        Objects.requireNonNull(executor, "Simulation executor must not be null before executing a step.");
        Objects.requireNonNull(statisticsManager, "Statistics manager must not be null before executing a step.");
        AppLogger.info("Simulation step started.");

        executor.executeStep();

        statisticsManager.update(executor);
        observationViewModel.setStatistics(statisticsManager.statistics());

        if (simulationStepListener != null) {
            simulationStepListener.run();
        }

        if (!executor.isRunning()) {
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