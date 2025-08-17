package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public abstract class AbstractConfigViewModel<CON extends SimulationConfig>
        implements SimulationConfigViewModel<CON> {

    private final ReadOnlyObjectProperty<SimulationState> simulationState;

    private final InputEnumProperty<CellShape> cellShape;
    private final InputEnumProperty<GridEdgeBehavior> gridEdgeBehavior;
    private final InputIntegerProperty gridWidth;
    private final InputIntegerProperty gridHeight;
    private final InputDoublePropertyIntRange cellEdgeLength;

    protected AbstractConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState,
                                      GridStructureSettings gridStructureSettings) {
        this.simulationState = simulationState;

        cellShape = InputEnumProperty.of(
                gridStructureSettings.cellShapeInitial(),
                CellShape.class,
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        gridEdgeBehavior = InputEnumProperty.of(
                gridStructureSettings.gridEdgeBehaviorInitial(),
                gridStructureSettings.gridEdgeBehaviorValues(),
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        gridWidth = InputIntegerProperty.of(
                gridStructureSettings.gridWidthInitial(),
                gridStructureSettings.gridWidthMin(),
                gridStructureSettings.gridWidthMax(),
                gridStructureSettings.gridWidthStep());
        gridHeight = InputIntegerProperty.of(
                gridStructureSettings.gridHeightInitial(),
                gridStructureSettings.gridHeightMin(),
                gridStructureSettings.gridHeightMax(),
                gridStructureSettings.gridHeightStep());
        cellEdgeLength = InputDoublePropertyIntRange.of(
                gridStructureSettings.cellEdgeLengthInitial(),
                gridStructureSettings.cellEdgeLengthMin(),
                gridStructureSettings.cellEdgeLengthMax());
    }

    @Override
    public final ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    @Override
    public final SimulationState getSimulationState() {
        return simulationState.get();
    }

    public final InputEnumProperty<CellShape> cellShapeProperty() {
        return cellShape;
    }

    public final InputEnumProperty<GridEdgeBehavior> gridEdgeBehaviorProperty() {
        return gridEdgeBehavior;
    }

    public final InputIntegerProperty gridWidthProperty() {
        return gridWidth;
    }

    public final InputIntegerProperty gridHeightProperty() {
        return gridHeight;
    }

    public final InputDoublePropertyIntRange cellEdgeLengthProperty() {
        return cellEdgeLength;
    }

    public record GridStructureSettings(
            CellShape cellShapeInitial,
            GridEdgeBehavior gridEdgeBehaviorInitial,
            List<GridEdgeBehavior> gridEdgeBehaviorValues,
            int gridWidthInitial,
            int gridWidthMin,
            int gridWidthMax,
            int gridWidthStep,
            int gridHeightInitial,
            int gridHeightMin,
            int gridHeightMax,
            int gridHeightStep,
            int cellEdgeLengthInitial,
            int cellEdgeLengthMin,
            int cellEdgeLengthMax
    ) {}

}
