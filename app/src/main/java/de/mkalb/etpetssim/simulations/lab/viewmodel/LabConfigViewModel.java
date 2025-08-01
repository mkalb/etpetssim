package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.SimulationState;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.InputDoublePropertyIntRange;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class LabConfigViewModel extends AbstractConfigViewModel<LabConfig> {

    private static final CellShape CELL_SHAPE_INITIAL = CellShape.SQUARE;
    private static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_INITIAL = GridEdgeBehavior.WRAP_XY;
    private static final int GRID_WIDTH_INITIAL = GridSize.MIN_SIZE;
    private static final int GRID_WIDTH_MAX = GridSize.LARGE_SQUARE.width();
    private static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    private static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    private static final int GRID_HEIGHT_INITIAL = GridSize.MIN_SIZE;
    private static final int GRID_HEIGHT_MAX = GridSize.LARGE_SQUARE.height();
    private static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    private static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;
    private static final int CELL_EDGE_LENGTH_INITIAL = 80;
    private static final int CELL_EDGE_LENGTH_MAX = 200;
    private static final int CELL_EDGE_LENGTH_MIN = 1;

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
    private final InputEnumProperty<LabConfig.RenderingMode> renderingMode = InputEnumProperty.of(LabConfig.RenderingMode.SHAPE, LabConfig.RenderingMode.class, Enum::toString);
    private final InputEnumProperty<LabConfig.ColorMode> colorMode = InputEnumProperty.of(LabConfig.ColorMode.COLOR, LabConfig.ColorMode.class, Enum::toString);
    private final InputEnumProperty<LabConfig.StrokeMode> strokeMode = InputEnumProperty.of(LabConfig.StrokeMode.CENTERED, LabConfig.StrokeMode.class, Enum::toString);
    private final BooleanProperty configChangedRequested = new SimpleBooleanProperty(false);

    public LabConfigViewModel(SimpleObjectProperty<SimulationState> simulationState) {
        super(simulationState);
        setupConfigListeners();
    }

    @Override
    public LabConfig getConfig() {
        return new LabConfig(
                cellShape.getValue(),
                gridEdgeBehavior.getValue(),
                gridWidth.getValue(),
                gridHeight.getValue(),
                cellEdgeLength.getValue(),
                renderingMode.getValue(),
                colorMode.getValue(),
                strokeMode.getValue()
        );
    }

    @Override
    public void setConfig(LabConfig config) {
        cellShape.setValue(config.cellShape());
        gridEdgeBehavior.setValue(config.gridEdgeBehavior());
        cellEdgeLength.setValue(config.cellEdgeLength());
        gridWidth.setValue(config.gridWidth());
        gridHeight.setValue(config.gridHeight());
        renderingMode.setValue(config.renderingMode());
        colorMode.setValue(config.colorMode());
        strokeMode.setValue(config.strokeMode());
    }

    public InputEnumProperty<LabConfig.RenderingMode> renderingModeProperty() {
        return renderingMode;
    }

    public InputEnumProperty<LabConfig.ColorMode> colorModeProperty() {
        return colorMode;
    }

    public InputEnumProperty<LabConfig.StrokeMode> strokeModeProperty() {
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

    private void setupConfigListeners() {
        cellShapeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridEdgeBehaviorProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridWidthProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridHeightProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        cellEdgeLengthProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        colorModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        renderingModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        strokeModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
    }

    public BooleanProperty configChangedRequestedProperty() {
        return configChangedRequested;
    }

}
