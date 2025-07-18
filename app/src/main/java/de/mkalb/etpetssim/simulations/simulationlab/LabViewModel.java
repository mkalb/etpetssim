package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

public final class LabViewModel {

    private final LabSimulationManager manager;

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);
    private final ObjectProperty<RenderingMode> renderingMode = new SimpleObjectProperty<>(RenderingMode.SHAPE);
    private final ObjectProperty<ColorMode> colorMode = new SimpleObjectProperty<>(ColorMode.COLOR);
    private final ObjectProperty<StrokeMode> strokeMode = new SimpleObjectProperty<>(StrokeMode.CENTERED);

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

    public ObjectProperty<RenderingMode> renderingModeProperty() {
        return renderingMode;
    }

    public RenderingMode getRenderingMode() {
        return renderingMode.get();
    }

    public void setRenderingMode(RenderingMode value) {
        renderingMode.set(value);
    }

    public ObjectProperty<ColorMode> colorModeProperty() {
        return colorMode;
    }

    public ColorMode getColorMode() {
        return colorMode.get();
    }

    public void setColorMode(ColorMode value) {
        colorMode.set(value);
    }

    public ObjectProperty<StrokeMode> strokeModeProperty() {
        return strokeMode;
    }

    public StrokeMode getStrokeMode() {
        return strokeMode.get();
    }

    public void setStrokeMode(StrokeMode value) {
        strokeMode.set(value);
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
