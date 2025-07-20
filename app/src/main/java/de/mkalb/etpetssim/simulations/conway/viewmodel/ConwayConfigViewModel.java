package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.core.PropertyAdjuster;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.ui.ExtendedIntegerProperty;
import javafx.beans.property.DoubleProperty;
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

    private final DoubleProperty cellEdgeLength = PropertyAdjuster.createDoublePropertyWithIntRange(CELL_EDGE_LENGTH_INITIAL, CELL_EDGE_LENGTH_MIN, CELL_EDGE_LENGTH_MAX);
    private final ExtendedIntegerProperty gridWidth = ExtendedIntegerProperty.of(GRID_WIDTH_INITIAL, GRID_WIDTH_MIN, GRID_WIDTH_MAX, GRID_WIDTH_STEP);
    private final ExtendedIntegerProperty gridHeight = ExtendedIntegerProperty.of(GRID_HEIGHT_INITIAL, GRID_HEIGHT_MIN, GRID_HEIGHT_MAX, GRID_HEIGHT_STEP);
    private final DoubleProperty alivePercent = PropertyAdjuster.createAdjustedDoubleProperty(ALIVE_PERCENT_INITIAL, ALIVE_PERCENT_MIN, ALIVE_PERCENT_MAX);

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ConwayConfig getConfig() {
        return new ConwayConfig(cellEdgeLength.get(), gridWidth.getValue(), gridHeight.getValue(), alivePercent.get());
    }

    public void setConfig(ConwayConfig config) {
        cellEdgeLength.set(config.cellEdgeLength());
        gridWidth.setValue(config.gridWidth());
        gridHeight.setValue(config.gridHeight());
        alivePercent.set(config.alivePercent());
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
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

    public int getCellEdgeLengthMin() {
        return CELL_EDGE_LENGTH_MIN;
    }

    public int getCellEdgeLengthMax() {
        return CELL_EDGE_LENGTH_MAX;
    }

    public ExtendedIntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    public ExtendedIntegerProperty gridHeightProperty() {
        return gridHeight;
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

    public double getAlivePercentMin() {
        return ALIVE_PERCENT_MIN;
    }

    public double getAlivePercentMax() {
        return ALIVE_PERCENT_MAX;
    }

}
