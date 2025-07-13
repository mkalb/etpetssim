package de.mkalb.etpetssim.simulations.conwayslife.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.SimulationController;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayPatterns;
import de.mkalb.etpetssim.simulations.conwayslife.view.ConwayView;
import javafx.beans.property.*;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public final class ConwayViewModel implements SimulationController {

    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final ConwayView view;
    private final DoubleProperty cellEdgeLength = new SimpleDoubleProperty(20.0);
    private final IntegerProperty gridWidth = new SimpleIntegerProperty(64);  // Default value
    private final IntegerProperty gridHeight = new SimpleIntegerProperty(32); // Default value
    private final ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.READY);
    private @Nullable SimulationStartedListener simulationStartedListener;
    private @Nullable GridModel<ConwayEntity> model;
    private @Nullable SimulationExecutor<ConwayEntity> executor;

    public ConwayViewModel() {
        entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(ConwayEntity.values());
        view = new ConwayView(this, entityDescriptorRegistry);
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

    @Override
    public Region buildViewRegion() {
        return view.buildViewRegion();
    }

    public void setModelCreatedListener(SimulationStartedListener simulationStartedListener) {
        this.simulationStartedListener = simulationStartedListener;
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

    public void startSimulation() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(getGridWidth(), getGridHeight())
        );
        model = new SparseGridModel<>(structure, ConwayEntity.DEAD);
        GridInitializers.placeRandomCounted(3, () -> ConwayEntity.ALIVE, new Random())
                        .initialize(model);
        GridEntityUtils.placePatternAt(new GridCoordinate(10, 10), model, ConwayPatterns.glider());

        BiConsumer<ReadableGridModel<ConwayEntity>, GridModel<ConwayEntity>> updateStrategy = (currentModel, nextModel) -> {};  // TODO Optimize later
        SimulationStep<ConwayEntity> runner = new SynchronousStepRunner<>(model, updateStrategy);

        SimulationTerminationCondition<ConwayEntity> terminationCondition = (currentModel, step) -> false; // TODO Optimize later
        executor = new DefaultSimulationExecutor<>(runner, () -> model, terminationCondition);

        executor.executeStep();

        AppLogger.info("Structure:       " + structure.toDisplayString());

        if (simulationStartedListener != null) {
            simulationStartedListener.onSimulationStarted(structure);
        }
    }

    public void onActionButton() {
        simulationState.set(switch (getSimulationState()) {
            case READY, PAUSED -> SimulationState.RUNNING;
            case RUNNING -> SimulationState.PAUSED;
        });
    }

    public void onCancelButton() {
        simulationState.set(SimulationState.READY);
    }

    public enum SimulationState {READY, RUNNING, PAUSED}

    @FunctionalInterface
    public interface SimulationStartedListener {

        void onSimulationStarted(GridStructure structure);

    }

}