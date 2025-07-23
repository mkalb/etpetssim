package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class ConwayConfigViewModel {

    private static final CellShape CELL_SHAPE_INITIAL = CellShape.SQUARE;
    private static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_INITIAL = GridEdgeBehavior.BLOCK_X_BLOCK_Y;
    private static final int GRID_WIDTH_INITIAL = 64;
    private static final int GRID_WIDTH_MAX = 512;
    private static final int GRID_WIDTH_MIN = 8;
    private static final int GRID_WIDTH_STEP = 4;
    private static final int GRID_HEIGHT_INITIAL = 32;
    private static final int GRID_HEIGHT_MAX = 512;
    private static final int GRID_HEIGHT_MIN = 8;
    private static final int GRID_HEIGHT_STEP = 4;
    private static final int CELL_EDGE_LENGTH_INITIAL = 10;
    private static final int CELL_EDGE_LENGTH_MAX = 48;
    private static final int CELL_EDGE_LENGTH_MIN = 1;
    private static final double ALIVE_PERCENT_INITIAL = 0.15d;
    private static final double ALIVE_PERCENT_MAX = 1.0d;
    private static final double ALIVE_PERCENT_MIN = 0.0d;

    private final ReadOnlyObjectProperty<SimulationState> simulationState;

    private final InputEnumProperty<CellShape> cellShape = InputEnumProperty.of(
            CELL_SHAPE_INITIAL,
            CellShape.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputEnumProperty<GridEdgeBehavior> gridEdgeBehavior = InputEnumProperty.of(
            GRID_EDGE_BEHAVIOR_INITIAL,
            List.of(GridEdgeBehavior.BLOCK_X_BLOCK_Y, GridEdgeBehavior.WRAP_X_WRAP_Y),
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputIntegerProperty gridWidth = InputIntegerProperty.of(
            GRID_WIDTH_INITIAL,
            GRID_WIDTH_MIN,
            GRID_WIDTH_MAX,
            GRID_WIDTH_STEP);
    private final InputIntegerProperty gridHeight = InputIntegerProperty.of(
            GRID_HEIGHT_INITIAL,
            GRID_HEIGHT_MIN,
            GRID_HEIGHT_MAX,
            GRID_HEIGHT_STEP);
    private final InputDoublePropertyIntRange cellEdgeLength = InputDoublePropertyIntRange.of(
            CELL_EDGE_LENGTH_INITIAL,
            CELL_EDGE_LENGTH_MIN,
            CELL_EDGE_LENGTH_MAX);
    private final InputDoubleProperty alivePercent = InputDoubleProperty.of(
            ALIVE_PERCENT_INITIAL,
            ALIVE_PERCENT_MIN,
            ALIVE_PERCENT_MAX);

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ConwayConfig getConfig() {
        return new ConwayConfig(
                cellShape.getValue(),
                gridEdgeBehavior.getValue(),
                gridWidth.getValue(),
                gridHeight.getValue(),
                cellEdgeLength.getValue(),
                alivePercent.getValue()
        );
    }

    public void setConfig(ConwayConfig config) {
        cellShape.setValue(config.cellShape());
        gridEdgeBehavior.setValue(config.gridEdgeBehavior());
        cellEdgeLength.setValue(config.cellEdgeLength());
        gridWidth.setValue(config.gridWidth());
        gridHeight.setValue(config.gridHeight());
        alivePercent.setValue(config.alivePercent());
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    public InputEnumProperty<CellShape> cellShapeProperty() {
        return cellShape;
    }

    public InputEnumProperty<GridEdgeBehavior> gridEdgeBehaviorProperty() {
        return gridEdgeBehavior;
    }

    public InputIntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    public InputIntegerProperty gridHeightProperty() {
        return gridHeight;
    }

    public InputDoublePropertyIntRange cellEdgeLengthProperty() {
        return cellEdgeLength;
    }

    public InputDoubleProperty alivePercentProperty() {
        return alivePercent;
    }

}
