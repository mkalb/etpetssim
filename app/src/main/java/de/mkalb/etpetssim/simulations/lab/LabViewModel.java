package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.ui.InputEnumProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

public final class LabViewModel {

    private final LabSimulationManager manager;

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);
    private final InputEnumProperty<RenderingMode> renderingMode = InputEnumProperty.of(RenderingMode.SHAPE, RenderingMode.class);
    private final InputEnumProperty<ColorMode> colorMode = InputEnumProperty.of(ColorMode.COLOR, ColorMode.class);
    private final InputEnumProperty<StrokeMode> strokeMode = InputEnumProperty.of(StrokeMode.CENTERED, StrokeMode.class);

    public LabViewModel(LabConfig config) {
        manager = new LabSimulationManager(config);

        // Log information
        AppLogger.info("Structure:       " + manager.currentModel().structure().toDisplayString());
        AppLogger.info("Cell count:      " + manager.currentModel().structure().cellCount());
        AppLogger.info("NonDefaultCells: " + manager.currentModel().nonDefaultCells().count());
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

    public GridStructure getStructure() {
        return manager.structure();
    }

    public double getCellEdgeLength() {
        return manager.config().cellEdgeLength();
    }

    public ReadableGridModel<LabEntity> getModel() {
        return manager.currentModel();
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
