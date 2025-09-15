package de.mkalb.etpetssim.simulations.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.model.*;
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
    private final InputEnumProperty<CellDisplayMode> cellDisplayMode;
    private final SeedProperty seed;

    protected AbstractConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState,
                                      CommonConfigSettings commonConfigSettings) {
        this.simulationState = simulationState;

        cellShape = InputEnumProperty.of(
                commonConfigSettings.cellShapeInitial(),
                CellShape.class,
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        gridEdgeBehavior = InputEnumProperty.of(
                commonConfigSettings.gridEdgeBehaviorInitial(),
                commonConfigSettings.gridEdgeBehaviorValues(),
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        gridWidth = InputIntegerProperty.of(
                commonConfigSettings.gridWidthInitial(),
                commonConfigSettings.gridWidthMin(),
                commonConfigSettings.gridWidthMax(),
                commonConfigSettings.gridWidthStep());
        gridHeight = InputIntegerProperty.of(
                commonConfigSettings.gridHeightInitial(),
                commonConfigSettings.gridHeightMin(),
                commonConfigSettings.gridHeightMax(),
                commonConfigSettings.gridHeightStep());
        cellEdgeLength = InputDoublePropertyIntRange.of(
                commonConfigSettings.cellEdgeLengthInitial(),
                commonConfigSettings.cellEdgeLengthMin(),
                commonConfigSettings.cellEdgeLengthMax());
        cellDisplayMode = InputEnumProperty.of(
                commonConfigSettings.cellDisplayModeInitial(),
                commonConfigSettings.cellDisplayModeValues(),
                e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
        seed = new SeedProperty(commonConfigSettings.seedInitial());
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

    public final InputEnumProperty<CellDisplayMode> cellDisplayModeProperty() {
        return cellDisplayMode;
    }

    public final SeedProperty seedProperty() {
        return seed;
    }

    public record CommonConfigSettings(
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
            int cellEdgeLengthMax,
            CellDisplayMode cellDisplayModeInitial,
            List<CellDisplayMode> cellDisplayModeValues,
            String seedInitial
    ) {}

}
