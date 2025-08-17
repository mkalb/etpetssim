package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.*;

public final class LabConfigViewModel
        extends AbstractConfigViewModel<LabConfig> {

    private static final CellShape CELL_SHAPE_INITIAL = CellShape.SQUARE;
    private static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_INITIAL = GridEdgeBehavior.WRAP_XY;
    private static final int GRID_WIDTH_INITIAL = GridSize.MIN_SIZE;
    private static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    private static final int GRID_WIDTH_MAX = GridSize.LARGE_SQUARE.width();
    private static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    private static final int GRID_HEIGHT_INITIAL = GridSize.MIN_SIZE;
    private static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    private static final int GRID_HEIGHT_MAX = GridSize.LARGE_SQUARE.height();
    private static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;
    private static final int CELL_EDGE_LENGTH_INITIAL = 80;
    private static final int CELL_EDGE_LENGTH_MIN = 1;
    private static final int CELL_EDGE_LENGTH_MAX = 200;

    private final InputEnumProperty<LabConfig.RenderingMode> renderingMode = InputEnumProperty.of(LabConfig.RenderingMode.SHAPE, LabConfig.RenderingMode.class, Enum::toString);
    private final InputEnumProperty<LabConfig.ColorMode> colorMode = InputEnumProperty.of(LabConfig.ColorMode.COLOR, LabConfig.ColorMode.class, Enum::toString);
    private final InputEnumProperty<LabConfig.StrokeMode> strokeMode = InputEnumProperty.of(LabConfig.StrokeMode.CENTERED, LabConfig.StrokeMode.class, Enum::toString);
    private final BooleanProperty configChangedRequested = new SimpleBooleanProperty(false);

    public LabConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, new GridStructureSettings(
                CELL_SHAPE_INITIAL,
                GRID_EDGE_BEHAVIOR_INITIAL,
                List.of(GridEdgeBehavior.values()),
                GRID_WIDTH_INITIAL,
                GRID_WIDTH_MIN,
                GRID_WIDTH_MAX,
                GRID_WIDTH_STEP,
                GRID_HEIGHT_INITIAL,
                GRID_HEIGHT_MIN,
                GRID_HEIGHT_MAX,
                GRID_HEIGHT_STEP,
                CELL_EDGE_LENGTH_INITIAL,
                CELL_EDGE_LENGTH_MIN,
                CELL_EDGE_LENGTH_MAX));
        setupConfigListeners();
    }

    @Override
    public LabConfig getConfig() {
        return new LabConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                renderingMode.getValue(),
                colorMode.getValue(),
                strokeMode.getValue()
        );
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
