package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestConfig;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public final class ForestMainView
        extends AbstractDefaultMainView<
        ForestEntity,
        WritableGridModel<ForestEntity>,
        ForestConfig,
        ForestStatistics,
        ForestConfigView,
        ForestObservationView> {

    private final Color backgroundColor;
    private @Nullable CellDrawer<ForestEntity> cellDrawer;

    public ForestMainView(DefaultMainViewModel<ForestEntity, WritableGridModel<ForestEntity>, ForestConfig,
                                  ForestStatistics> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ForestConfigView configView,
                          DefaultControlView controlView,
                          ForestObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundColor = entityDescriptorRegistry
                .getRequiredByDescriptorId(ForestEntity.EMPTY.descriptorId())
                .colorOrFallback();
    }

    @Override
    protected void initSimulation(ForestConfig config, CellDimension cellDimension) {
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
        // Do nothing
    }

    @Override
    protected void drawSimulation(WritableGridModel<ForestEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundColor);

        // small helper lambda to avoid code duplication when drawing different entity types
        Consumer<GridCell<ForestEntity>> drawCell = cell -> cellDrawer.draw(
                entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId()),
                basePainter, cell, stepCount);

        // draw tree cells first
        currentModel.filteredCells(ForestEntity::isTree)
                    .forEach(drawCell);

        // then draw burning cells on top for better visibility
        currentModel.filteredCells(ForestEntity::isBurning)
                    .forEach(drawCell);
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
