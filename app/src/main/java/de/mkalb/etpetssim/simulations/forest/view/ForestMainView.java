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
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ForestMainView
        extends AbstractDefaultMainView<
        ForestEntity,
        WritableGridModel<ForestEntity>,
        ForestConfig,
        ForestStatistics,
        ForestConfigView,
        ForestObservationView> {

    private static final double BORDER_WIDTH = 1.0d;

    private final Paint backgroundPaint;
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
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(ForestEntity.EMPTY.descriptorId())
                .colorAsOptional().orElse(Color.BLACK);
    }

    @Override
    protected void initSimulation(ForestConfig config, CellDimension cellDimension) {
        double strokeLineWidth = config.cellDisplayMode().hasBorder() ? BORDER_WIDTH : 0.0d;

        cellDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            null,
                            strokeLineWidth);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            backgroundPaint,
                            strokeLineWidth);
            case CellDisplayMode.CIRCLE -> (descriptor, painter, cell, _) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            descriptor.color(),
                            null,
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case CellDisplayMode.CIRCLE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            descriptor.color(),
                            backgroundPaint,
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case CellDisplayMode.EMOJI ->
                    throw new IllegalArgumentException("EMOJI cell display mode is not supported in Forest simulation.");
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

        basePainter.fillCanvasBackground(backgroundPaint);

        currentModel.nonDefaultCells()
                    .forEachOrdered(cell ->
                            cellDrawer.draw(entityDescriptorRegistry.getRequiredByDescriptorId(cell.entity().descriptorId()),
                                    basePainter, cell, stepCount));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}

