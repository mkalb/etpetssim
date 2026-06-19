package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.rebounding.model.*;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.ui.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ReboundingMainView
        extends AbstractDefaultMainView<
        ReboundingEntity,
        GridCell<ReboundingEntity>,
        WritableGridModel<ReboundingEntity>,
        ReboundingConfig,
        ReboundingStatistics,
        ReboundingSimulationManager,
        NoUserActionContext,
        ReboundingConfigView,
        ReboundingObservationView> {

    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;
    private static final String REBOUNDING_TOOLBAR_ADD_WALL = "rebounding.toolbar.addwall";
    private static final String REBOUNDING_TOOLBAR_ADD_WALL_TOOLTIP = "rebounding.toolbar.addwall.tooltip";

    private @Nullable CellDrawer<ReboundingEntity> cellDrawer;

    public ReboundingMainView(DefaultMainViewModel<ReboundingEntity, GridCell<ReboundingEntity>, WritableGridModel<ReboundingEntity>, ReboundingConfig, ReboundingStatistics, ReboundingSimulationManager, NoUserActionContext> viewModel,
                              GridEntityDescriptorRegistry entityDescriptorRegistry,
                              ReboundingConfigView configView,
                              DefaultControlView controlView,
                              ReboundingObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    @Override
    protected void initSimulation(ReboundingConfig config, CellDimension cellDimension, WritableGridModel<ReboundingEntity> model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(ReboundingEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.colorOrFallback(),
                            null,
                            NO_STROKE_LINE_WIDTH);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.colorOrFallback(),
                            backgroundColor,
                            strokeLineWidth);
            case CellDisplayMode.CIRCLE, CellDisplayMode.CIRCLE_BORDERED, CellDisplayMode.EMOJI ->
                    throw new IllegalArgumentException("CellDisplayMode not supported: " + config.cellDisplayMode());
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ReboundingEntity> oldGridCell,
                                          @Nullable GridCell<ReboundingEntity> newGridCell) {
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
    protected void drawSimulation(WritableGridModel<ReboundingEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        currentModel.nonDefaultCells()
                    .forEach(cell -> cellDrawer.draw(
                            entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                            dynamicPainter, cell, stepCount));
    }

    @Override
    protected List<SimulationUserActionDescriptor<NoUserActionContext>> createUserActionDescriptors() {
        return List.of(new SimulationUserActionDescriptor<>(
                NoUserActionContext.NO_CONTEXT,
                SimulationUserActionScope.CELL_SELECTED,
                REBOUNDING_TOOLBAR_ADD_WALL,
                REBOUNDING_TOOLBAR_ADD_WALL_TOOLTIP));
    }

}
