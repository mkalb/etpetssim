package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CoordinateDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayMainView
        extends AbstractDefaultMainView<
        ConwayEntity,
        WritableGridModel<ConwayEntity>,
        ConwayConfig,
        ConwayStatistics,
        ConwayConfigView,
        ConwayObservationView> {

    private final Color backgroundColor;
    private @Nullable CoordinateDrawer coordinateDrawer;

    public ConwayMainView(DefaultMainViewModel<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
                                  ConwayStatistics> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ConwayConfigView configView,
                          DefaultControlView controlView,
                          ConwayObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundColor = entityDescriptorRegistry
                .getRequiredByDescriptorId(ConwayEntity.DEAD.descriptorId())
                .colorOrFallback();
    }

    @Override
    protected void initSimulation(ConwayConfig config, CellDimension cellDimension) {
        var descriptor = entityDescriptorRegistry.getRequiredByDescriptorId(ConwayEntity.ALIVE.descriptorId());
        var aliveColor = descriptor.colorOrFallback();
        var aliveBorderColor = descriptor.borderColorOrFallback();

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        coordinateDrawer = switch (config.cellDisplayMode()) {
            case SHAPE -> (painter, coordinate, _) ->
                    painter.drawCell(
                            coordinate,
                            aliveColor,
                            null,
                            NO_STROKE_LINE_WIDTH);
            case SHAPE_BORDERED -> (painter, coordinate, _) ->
                    painter.drawCell(
                            coordinate,
                            aliveColor,
                            aliveBorderColor,
                            strokeLineWidth);
            case CIRCLE -> (painter, coordinate, _) ->
                    painter.drawCellInnerCircle(
                            coordinate,
                            aliveColor,
                            null,
                            NO_STROKE_LINE_WIDTH,
                            StrokeType.INSIDE);
            case CIRCLE_BORDERED -> (painter, coordinate, _) ->
                    painter.drawCellInnerCircle(
                            coordinate,
                            aliveColor,
                            aliveBorderColor,
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case CellDisplayMode.EMOJI -> throw new IllegalArgumentException("CellDisplayMode not supported!");
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ConwayEntity> oldGridCell,
                                          @Nullable GridCell<ConwayEntity> newGridCell) {
        // Do nothing
    }

    @Override
    protected void drawSimulation(WritableGridModel<ConwayEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (coordinateDrawer == null) {
            AppLogger.warn("CoordinateDrawer is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundColor);

        currentModel.nonDefaultCoordinates()
                    .forEach(coordinate -> coordinateDrawer.draw(
                            basePainter, coordinate, stepCount));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}

