package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabViewModel {

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);

    private final InputEnumProperty<RenderingMode> renderingMode = InputEnumProperty.of(RenderingMode.SHAPE, RenderingMode.class);
    private final InputEnumProperty<ColorMode> colorMode = InputEnumProperty.of(ColorMode.COLOR, ColorMode.class);
    private final InputEnumProperty<StrokeMode> strokeMode = InputEnumProperty.of(StrokeMode.CENTERED, StrokeMode.class);
    private final InputEnumProperty<CellShape> shapeMode;

    private LabConfig config;
    private @Nullable LabSimulationManager simulationManager;

    public LabViewModel(LabConfig config) {
        this.config = config;
        shapeMode = InputEnumProperty.of(config.shape(), CellShape.class);
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

    public InputEnumProperty<CellShape> shapeModeProperty() {
        return shapeMode;
    }

    public CellShape getCellShape() {
        return shapeMode.getValue();
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

    public void onDrawButtonClicked() {
        config = new LabConfig(getCellShape(), config.cellEdgeLength(), config.gridWidth(), config.gridHeight());
        simulationManager = new LabSimulationManager(config);

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
