package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

/**
 * Base implementation for configuration view models.
 */
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
                commonConfigSettings.cellShapeValues(),
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

    /**
     * Shared defaults and ranges used to initialize common config controls.
     *
     * @param cellShapeInitial initial cell shape
     * @param cellShapeValues selectable cell shapes
     * @param gridEdgeBehaviorInitial initial grid edge behavior
     * @param gridEdgeBehaviorValues selectable grid edge behaviors
     * @param gridWidthInitial initial grid width
     * @param gridWidthMin minimum grid width
     * @param gridWidthMax maximum grid width
     * @param gridWidthStep grid width step size
     * @param gridHeightInitial initial grid height
     * @param gridHeightMin minimum grid height
     * @param gridHeightMax maximum grid height
     * @param gridHeightStep grid height step size
     * @param cellEdgeLengthInitial initial cell edge length
     * @param cellEdgeLengthMin minimum cell edge length
     * @param cellEdgeLengthMax maximum cell edge length
     * @param cellDisplayModeInitial initial cell display mode
     * @param cellDisplayModeValues selectable cell display modes
     * @param seedInitial initial seed text
     */
    public record CommonConfigSettings(
            CellShape cellShapeInitial,
            List<CellShape> cellShapeValues,
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
