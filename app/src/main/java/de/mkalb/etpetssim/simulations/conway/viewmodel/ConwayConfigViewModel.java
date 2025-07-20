package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.ui.ExtendedDoubleProperty;
import de.mkalb.etpetssim.ui.ExtendedDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.ExtendedIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public final class ConwayConfigViewModel {

    private static final int CELL_EDGE_LENGTH_INITIAL = 12;
    private static final int CELL_EDGE_LENGTH_MIN = 1;
    private static final int CELL_EDGE_LENGTH_MAX = 48;
    private static final int GRID_WIDTH_INITIAL = 32;
    private static final int GRID_WIDTH_MIN = 8;
    private static final int GRID_WIDTH_MAX = 512;
    private static final int GRID_WIDTH_STEP = 4;
    private static final int GRID_HEIGHT_INITIAL = 32;
    private static final int GRID_HEIGHT_MIN = 8;
    private static final int GRID_HEIGHT_MAX = 512;
    private static final int GRID_HEIGHT_STEP = 4;
    private static final double ALIVE_PERCENT_INITIAL = 0.1d;
    private static final double ALIVE_PERCENT_MIN = 0.0d;
    private static final double ALIVE_PERCENT_MAX = 1.0d;

    private final ReadOnlyObjectProperty<SimulationState> simulationState;

    private final ExtendedDoublePropertyIntRange cellEdgeLength = ExtendedDoublePropertyIntRange.of(CELL_EDGE_LENGTH_INITIAL,
            CELL_EDGE_LENGTH_MIN, CELL_EDGE_LENGTH_MAX);
    private final ExtendedIntegerProperty gridWidth = ExtendedIntegerProperty.of(GRID_WIDTH_INITIAL,
            GRID_WIDTH_MIN, GRID_WIDTH_MAX, GRID_WIDTH_STEP);
    private final ExtendedIntegerProperty gridHeight = ExtendedIntegerProperty.of(GRID_HEIGHT_INITIAL,
            GRID_HEIGHT_MIN, GRID_HEIGHT_MAX, GRID_HEIGHT_STEP);
    private final ExtendedDoubleProperty alivePercent = ExtendedDoubleProperty.of(ALIVE_PERCENT_INITIAL,
            ALIVE_PERCENT_MIN, ALIVE_PERCENT_MAX);

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ConwayConfig getConfig() {
        return new ConwayConfig(
                cellEdgeLength.getValue(),
                gridWidth.getValue(),
                gridHeight.getValue(),
                alivePercent.getValue());
    }

    public void setConfig(ConwayConfig config) {
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

    public ExtendedDoublePropertyIntRange cellEdgeLengthProperty() {
        return cellEdgeLength;
    }

    public ExtendedIntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    public ExtendedIntegerProperty gridHeightProperty() {
        return gridHeight;
    }

    public ExtendedDoubleProperty alivePercentProperty() {
        return alivePercent;
    }

}
