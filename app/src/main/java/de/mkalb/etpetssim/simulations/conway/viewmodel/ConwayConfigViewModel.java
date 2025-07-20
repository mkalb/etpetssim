package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.core.PropertyAdjuster;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import javafx.beans.property.*;

public final class ConwayConfigViewModel {

    public static final int GRID_WIDTH_INITIAL = 32;
    public static final int GRID_WIDTH_MIN = 8;
    public static final int GRID_WIDTH_MAX = 512;
    public static final int GRID_WIDTH_STEP = 4;
    public static final int GRID_HEIGHT_INITIAL = 32;
    public static final int GRID_HEIGHT_MIN = 8;
    public static final int GRID_HEIGHT_MAX = 512;
    public static final int GRID_HEIGHT_STEP = 4;

    private final ReadOnlyObjectProperty<SimulationState> simulationState;

    private final DoubleProperty cellEdgeLength = new SimpleDoubleProperty(10.0d);
    private final IntegerProperty gridWidth = PropertyAdjuster.createAdjustedIntProperty(GRID_WIDTH_INITIAL, GRID_WIDTH_MIN, GRID_WIDTH_MAX, GRID_WIDTH_STEP);
    private final IntegerProperty gridHeight = PropertyAdjuster.createAdjustedIntProperty(GRID_HEIGHT_INITIAL, GRID_HEIGHT_MIN, GRID_HEIGHT_MAX, GRID_HEIGHT_STEP);
    private final DoubleProperty alivePercent = new SimpleDoubleProperty(0.1d);

    public ConwayConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        this.simulationState = simulationState;
    }

    public ConwayConfig getConfig() {
        return new ConwayConfig(cellEdgeLength.get(), gridWidth.get(), gridHeight.get(), alivePercent.get());
    }

    public void setConfig(ConwayConfig config) {
        cellEdgeLength.set(config.cellEdgeLength());
        gridWidth.set(config.gridWidth());
        gridHeight.set(config.gridHeight());
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

    public IntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    public int getGridWidth() {
        return gridWidth.get();
    }

    public void setGridWidth(int value) {
        gridWidth.set(value);
    }

    public int getGridWidthMin() {
        return GRID_WIDTH_MIN;
    }

    public int getGridWidthMax() {
        return GRID_WIDTH_MAX;
    }

    public int getGridWidthStep() {
        return GRID_WIDTH_STEP;
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

    public int getGridHeightMin() {
        return GRID_HEIGHT_MIN;
    }

    public int getGridHeightMax() {
        return GRID_HEIGHT_MAX;
    }

    public int getGridHeightStep() {
        return GRID_HEIGHT_STEP;
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

}
