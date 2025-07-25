package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabViewModel {

    private static final CellShape CELL_SHAPE_INITIAL = CellShape.SQUARE;
    private static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_INITIAL = GridEdgeBehavior.WRAP_X_WRAP_Y;
    private static final int GRID_WIDTH_INITIAL = 8;
    private static final int GRID_WIDTH_MAX = 64;
    private static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    private static final int GRID_WIDTH_STEP = 1;
    private static final int GRID_HEIGHT_INITIAL = 8;
    private static final int GRID_HEIGHT_MAX = 64;
    private static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    private static final int GRID_HEIGHT_STEP = 1;
    private static final int CELL_EDGE_LENGTH_INITIAL = 80;
    private static final int CELL_EDGE_LENGTH_MAX = 200;
    private static final int CELL_EDGE_LENGTH_MIN = 1;

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);

    private final InputEnumProperty<RenderingMode> renderingMode = InputEnumProperty.of(RenderingMode.SHAPE, RenderingMode.class, Enum::toString);
    private final InputEnumProperty<ColorMode> colorMode = InputEnumProperty.of(ColorMode.COLOR, ColorMode.class, Enum::toString);
    private final InputEnumProperty<StrokeMode> strokeMode = InputEnumProperty.of(StrokeMode.CENTERED, StrokeMode.class, Enum::toString);

    private final InputEnumProperty<CellShape> cellShape = InputEnumProperty.of(
            CELL_SHAPE_INITIAL,
            CellShape.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final InputEnumProperty<GridEdgeBehavior> gridEdgeBehavior = InputEnumProperty.of(
            GRID_EDGE_BEHAVIOR_INITIAL,
            GridEdgeBehavior.class,
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

    private @Nullable LabSimulationManager simulationManager;

    public LabViewModel() {
    }

    public LabConfig getConfig() {
        return new LabConfig(
                cellShape.getValue(),
                gridEdgeBehavior.getValue(),
                gridWidth.getValue(),
                gridHeight.getValue(),
                cellEdgeLength.getValue()
        );
    }

    public void setConfig(LabConfig config) {
        cellShape.setValue(config.cellShape());
        gridEdgeBehavior.setValue(config.gridEdgeBehavior());
        cellEdgeLength.setValue(config.cellEdgeLength());
        gridWidth.setValue(config.gridWidth());
        gridHeight.setValue(config.gridHeight());
    }

    public ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

    public @Nullable GridCoordinate getLastClickedCoordinate() {
        return lastClickedCoordinate.get();
    }

    public void setLastClickedCoordinate(@Nullable GridCoordinate value) {
        lastClickedCoordinate.set(value);
    }

    public InputEnumProperty<RenderingMode> renderingModeProperty() {
        return renderingMode;
    }

    public InputEnumProperty<ColorMode> colorModeProperty() {
        return colorMode;
    }

    public InputEnumProperty<StrokeMode> strokeModeProperty() {
        return strokeMode;
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

    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.structure();
    }

    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config().cellEdgeLength();
    }

    public ReadableGridModel<LabEntity> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    public boolean hasSimulationManager() {
        return simulationManager != null;
    }

    public void onDrawButtonClicked() {
        // Reset the simulation manager if it exists
        simulationManager = null;

        LabConfig config = getConfig();
        if (!config.isValid()) {
            AppLogger.warn("Invalid configuration: " + config);
            return;
        }

        simulationManager = new LabSimulationManager(getConfig());

        // Log information
        AppLogger.info("Structure:       " + simulationManager.currentModel().structure().toDisplayString());
        AppLogger.info("Cell count:      " + simulationManager.currentModel().structure().cellCount());
        AppLogger.info("NonDefaultCells: " + simulationManager.currentModel().nonDefaultCells().count());
    }

    public enum RenderingMode {
        SHAPE, CIRCLE
    }

    public enum ColorMode {
        COLOR, BLACK_WHITE
    }

    public enum StrokeMode {
        NONE, CENTERED
    }

}
