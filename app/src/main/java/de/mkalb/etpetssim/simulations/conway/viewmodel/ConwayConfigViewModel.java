package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class ConwayConfigViewModel extends AbstractConfigViewModel<ConwayConfig> {

    private static final CellShape CELL_SHAPE_INITIAL = CellShape.SQUARE;
    private static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_INITIAL = GridEdgeBehavior.WRAP_XY;
    private static final int GRID_WIDTH_INITIAL = 64;
    private static final int GRID_WIDTH_MAX = 1_024;
    private static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    private static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    private static final int GRID_HEIGHT_INITIAL = 32;
    private static final int GRID_HEIGHT_MAX = 1_024;
    private static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    private static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;
    private static final int CELL_EDGE_LENGTH_INITIAL = 10;
    private static final int CELL_EDGE_LENGTH_MAX = 48;
    private static final int CELL_EDGE_LENGTH_MIN = 1;
    private static final double ALIVE_PERCENT_INITIAL = 0.15d;
    private static final double ALIVE_PERCENT_MAX = 1.0d;
    private static final double ALIVE_PERCENT_MIN = 0.0d;

    private final InputEnumProperty<CellShape> cellShape = InputEnumProperty.of(
            CELL_SHAPE_INITIAL,
            CellShape.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputEnumProperty<GridEdgeBehavior> gridEdgeBehavior = InputEnumProperty.of(
            GRID_EDGE_BEHAVIOR_INITIAL,
            List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY),
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
        super(simulationState);
    }

    @Override
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

    @Override
    public void setConfig(ConwayConfig config) {
        cellShape.setValue(config.cellShape());
        gridEdgeBehavior.setValue(config.gridEdgeBehavior());
        cellEdgeLength.setValue(config.cellEdgeLength());
        gridWidth.setValue(config.gridWidth());
        gridHeight.setValue(config.gridHeight());
        alivePercent.setValue(config.alivePercent());
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
