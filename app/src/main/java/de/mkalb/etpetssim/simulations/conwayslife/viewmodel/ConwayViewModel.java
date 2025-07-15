package de.mkalb.etpetssim.simulations.conwayslife.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.SimulationController;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conwayslife.model.*;
import de.mkalb.etpetssim.simulations.conwayslife.view.ConwayView;
import de.mkalb.etpetssim.ui.SimulationTimer;
import javafx.beans.property.*;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayViewModel implements SimulationController {

    private final SimulationTimer simulationTimer;
    private final ConwayView view;

    private final DoubleProperty cellEdgeLength = new SimpleDoubleProperty(10.0d);
    private final IntegerProperty gridWidth = new SimpleIntegerProperty(64);  // Default value
    private final IntegerProperty gridHeight = new SimpleIntegerProperty(32); // Default value
    private final DoubleProperty alivePercent = new SimpleDoubleProperty(0.1d);
    private final ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.READY);

    private @Nullable Runnable simulationInitializedListener;
    private @Nullable Runnable simulationStepListener;

    private @Nullable SimulationExecutor<ConwayEntity> executor;
    private @Nullable ConwayStatistics statistics;

    public ConwayViewModel() {
        simulationTimer = new SimulationTimer(this::doSimulationStep);
        view = new ConwayView(this, GridEntityDescriptorRegistry.ofArray(ConwayEntity.values()));
    }

    @Override
    public Region buildViewRegion() {
        return view.buildViewRegion();
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

    public DoubleProperty cellEdgeLengthProperty() {
        return cellEdgeLength;
    }

    public double getCellEdgeLength() {
        return cellEdgeLength.get();
    }

    public void setCellEdgeLength(double value) {
        cellEdgeLength.set(value);
    }

    public IntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    public int getGridWidth() {
        return gridWidth.get();
    }

    public void setGridWidth(int value) {
        gridWidth.set(value);
    }

    public IntegerProperty gridHeightProperty() {
        return gridHeight;
    }

    public int getGridHeight() {
        return gridHeight.get();
    }

    public void setGridHeight(int value) {
        gridHeight.set(value);
    }

    public DoubleProperty alivePercentProperty() {
        return alivePercent;
    }

    public double getAlivePercent() {
        return alivePercent.get();
    }

    public void setAlivePercent(double value) {
        alivePercent.set(value);
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

    public ConwayStatistics getStatistics() {
        Objects.requireNonNull(statistics, "Statistics must not be null before accessing.");
        return statistics;
    }

    private void startSimulation() {
        // Resetting the executor to ensure a fresh start
        executor = null;

        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(getGridWidth(), getGridHeight())
        );
        GridModel<ConwayEntity> model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        GridInitializers.placeRandomPercent(() -> ConwayEntity.ALIVE, getAlivePercent(), new Random()).initialize(model);

        //  GridEntityUtils.placePatternAt(new GridCoordinate(10, 10), model, ConwayPatterns.glider());

        SynchronousStepRunner<ConwayEntity> runner = new SynchronousStepRunner<>(model, new ConwayUpdateStrategy(structure));

        executor = new DefaultSimulationExecutor<>(runner, runner::currentModel, new ConwayTerminationCondition());

        statistics = new ConwayStatistics(structure.cellCount());
        updateStatistics();

        if (simulationInitializedListener != null) {
            simulationInitializedListener.run();
        }
    }

    private void updateStatistics() {
        long step = getCurrentStep();
        long alive = getCurrentModel().count(cell -> cell.entity().isAlive());
        statistics.update(step, alive);
    }

    private void doSimulationStep() {
        Objects.requireNonNull(executor, "Simulation executor must not be null before executing a step.");
        AppLogger.info("Simulation step started.");

        executor.executeStep();

        updateStatistics();

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