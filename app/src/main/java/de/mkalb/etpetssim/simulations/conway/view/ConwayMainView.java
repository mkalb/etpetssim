package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.ui.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayMainView
        extends AbstractDefaultMainView<
        ConwayEntity,
        GridCell<ConwayEntity>,
        WritableGridModel<ConwayEntity>,
        ConwayConfig,
        ConwayStatistics,
        ConwaySimulationManager,
        NoUserActionContext,
        ConwayConfigView,
        ConwayObservationView> {

    private static final Color SELECTED_STROKE_COLOR = Color.BLACK;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private @Nullable CoordinateDrawer coordinateDrawer;

    public ConwayMainView(DefaultMainViewModel<ConwayEntity, GridCell<ConwayEntity>, WritableGridModel<ConwayEntity>, ConwayConfig, ConwayStatistics, ConwaySimulationManager, NoUserActionContext> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ConwayConfigView configView,
                          DefaultControlView controlView,
                          ConwayObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    @Override
    protected void initSimulation(ConwayConfig config, CellDimension cellDimension, WritableGridModel<ConwayEntity> model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(ConwayEntity.DEAD.descriptorId())
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);

        var descriptor = entityDescriptorRegistry.requireByDescriptorId(ConwayEntity.ALIVE.descriptorId());
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
            case EMOJI -> throw new IllegalArgumentException("CellDisplayMode not supported!");
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ConwayEntity> oldGridCell,
                                          @Nullable GridCell<ConwayEntity> newGridCell) {
        if (oldGridCell != null) {
            painter.clearCanvasBackground();
        }
        if (newGridCell != null) {
            painter.drawCellOuterCircle(newGridCell.coordinate(), null,
                    SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH,
                    StrokeType.OUTSIDE);
        }
    }

    @Override
    protected void drawSimulation(WritableGridModel<ConwayEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (coordinateDrawer == null) {
            AppLogger.warn("CoordinateDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        currentModel.nonDefaultCoordinates()
                    .forEach(coordinate -> coordinateDrawer.draw(
                            dynamicPainter, coordinate, stepCount));
    }

    @Override
    protected List<Node> createActionToolBarNodes() {
        return List.of();
    }

}

