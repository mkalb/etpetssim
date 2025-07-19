package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import javafx.beans.property.*;

public final class ConwayConfigViewModel {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;

    private final DoubleProperty cellEdgeLength = new SimpleDoubleProperty(10.0d);
    private final IntegerProperty gridWidth = new SimpleIntegerProperty(64);
    private final IntegerProperty gridHeight = new SimpleIntegerProperty(32);
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

}
