package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.forest.model.*;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import de.mkalb.etpetssim.ui.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public final class ForestMainView
        extends AbstractDefaultMainView<
        ForestEntity,
        GridCell<ForestEntity>,
        WritableGridModel<ForestEntity>,
        ForestConfig,
        ForestStatistics,
        ForestSimulationManager,
        NoUserActionContext,
        ForestConfigView,
        ForestObservationView> {

    private static final String FOREST_TOOLBAR_CYCLE_STATE = "forest.toolbar.cyclestate";
    private static final String FOREST_TOOLBAR_CYCLE_STATE_TOOLTIP = "forest.toolbar.cyclestate.tooltip";

    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private @Nullable CellDrawer<ForestEntity> cellDrawer;

    public ForestMainView(DefaultMainViewModel<ForestEntity, GridCell<ForestEntity>, WritableGridModel<ForestEntity>, ForestConfig, ForestStatistics, ForestSimulationManager, NoUserActionContext> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ForestConfigView configView,
                          DefaultControlView controlView,
                          ForestObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    @Override
    protected void initSimulation(ForestConfig config, CellDimension cellDimension, WritableGridModel<ForestEntity> model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(ForestEntity.EMPTY.descriptorId())
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            null,
                            NO_STROKE_LINE_WIDTH);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            descriptor.borderColorAsOptional().orElse(backgroundColor),
                            strokeLineWidth);
            case CellDisplayMode.CIRCLE -> (descriptor, painter, cell, _) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            descriptor.color(),
                            null,
                            NO_STROKE_LINE_WIDTH,
                            StrokeType.INSIDE);
            case CellDisplayMode.CIRCLE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            descriptor.color(),
                            descriptor.borderColorAsOptional().orElse(backgroundColor),
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case CellDisplayMode.EMOJI -> {
                if (cellEmojiFont == null) {
                    yield (descriptor, painter, cell, _) ->
                            painter.drawCellInnerCircle(
                                    cell.coordinate(),
                                    descriptor.color(),
                                    null,
                                    NO_STROKE_LINE_WIDTH,
                                    StrokeType.INSIDE);
                }
                yield (descriptor, painter, cell, _) ->
                        painter.drawCenteredTextInCell(
                                cell.coordinate(),
                                descriptor.emojiAsOptional().orElse("#"),
                                descriptor.colorOrFallback(),
                                cellEmojiFont);
            }
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ForestEntity> oldGridCell,
                                          @Nullable GridCell<ForestEntity> newGridCell) {
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
    protected void drawSimulation(WritableGridModel<ForestEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        // small helper lambda to avoid code duplication when drawing different entity types
        Consumer<GridCell<ForestEntity>> drawCell = cell -> cellDrawer.draw(
                entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                dynamicPainter, cell, stepCount);

        // draw tree cells first
        currentModel.filteredCells(ForestEntity::isTree)
                    .forEach(drawCell);

        // then draw burning cells on top for better visibility
        currentModel.filteredCells(ForestEntity::isBurning)
                    .forEach(drawCell);
    }

    @Override
    protected List<SimulationUserActionDescriptor<NoUserActionContext>> createUserActionDescriptors() {
        return List.of(new SimulationUserActionDescriptor<>(
                NoUserActionContext.NO_CONTEXT,
                SimulationUserActionScope.CELL_SELECTED,
                FOREST_TOOLBAR_CYCLE_STATE,
                FOREST_TOOLBAR_CYCLE_STATE_TOOLTIP));
    }

}
