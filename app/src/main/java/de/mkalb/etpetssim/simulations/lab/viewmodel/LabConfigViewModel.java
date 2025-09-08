package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
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

    private static final GridStructureSettings STRUCTURE_SETTINGS = new GridStructureSettings(
            CellShape.HEXAGON,
            GridEdgeBehavior.WRAP_XY,
            List.of(GridEdgeBehavior.values()),
            16,
            GridSize.MIN_SIZE,
            GridSize.LARGE_SQUARE.width(),
            1,
            12,
            GridSize.MIN_SIZE,
            GridSize.LARGE_SQUARE.height(),
            1,
            40,
            1,
            500);
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_AND_VERTICES;

    private final InputEnumProperty<LabConfig.RenderingMode> renderingMode = InputEnumProperty.of(LabConfig.RenderingMode.SHAPE, LabConfig.RenderingMode.class, Enum::toString);
    private final InputEnumProperty<LabConfig.ColorMode> colorMode = InputEnumProperty.of(LabConfig.ColorMode.COLOR, LabConfig.ColorMode.class, Enum::toString);
    private final InputEnumProperty<LabConfig.StrokeMode> strokeMode = InputEnumProperty.of(LabConfig.StrokeMode.CENTERED, LabConfig.StrokeMode.class, Enum::toString);
    private final InputEnumProperty<NeighborhoodMode> neighborhoodMode = InputEnumProperty.of(NEIGHBORHOOD_MODE_INITIAL, NeighborhoodMode.class,
            e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString()));
    private final BooleanProperty configChangedRequested = new SimpleBooleanProperty(false);

    public LabConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, STRUCTURE_SETTINGS, "");
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
                seedProperty().computeSeed(),
                renderingMode.getValue(),
                colorMode.getValue(),
                strokeMode.getValue(),
                neighborhoodMode.getValue()
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

    public InputEnumProperty<NeighborhoodMode> neighborhoodModeProperty() {
        return neighborhoodMode;
    }

    public BooleanProperty configChangedRequestedProperty() {
        return configChangedRequested;
    }

    private void setupConfigListeners() {
        cellShapeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridEdgeBehaviorProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridWidthProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        gridHeightProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        cellEdgeLengthProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        renderingModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        colorModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        strokeModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
        neighborhoodModeProperty().property().addListener((_, _, _) -> configChangedRequested.set(true));
    }

}
