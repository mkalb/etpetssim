package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ReboundingMainView
        extends AbstractDefaultMainView<
        ReboundingEntity,
        WritableGridModel<ReboundingEntity>,
        ReboundingConfig,
        ReboundingStatistics,
        ReboundingConfigView,
        ReboundingObservationView> {

    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private final Paint backgroundPaint;
    private @Nullable CellDrawer<ReboundingEntity> cellDrawer;

    public ReboundingMainView(DefaultMainViewModel<ReboundingEntity, WritableGridModel<ReboundingEntity>, ReboundingConfig, ReboundingStatistics> viewModel,
                              GridEntityDescriptorRegistry entityDescriptorRegistry,
                              ReboundingConfigView configView,
                              DefaultControlView controlView,
                              ReboundingObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(ReboundingEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
    }

    @Override
    protected void initSimulation(ReboundingConfig config, CellDimension cellDimension) {
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
                            backgroundPaint,
                            strokeLineWidth);
            default -> throw new IllegalArgumentException("CellDisplayMode not supported!");
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ReboundingEntity> oldGridCell,
                                          @Nullable GridCell<ReboundingEntity> newGridCell) {
        if ((oldGridCell != null) && oldGridCell.entity().isRebounder()) {
            painter.clearCanvasBackground();
        }
        if ((newGridCell != null) && newGridCell.entity().isRebounder()) {
            painter.drawCellOuterCircle(newGridCell.coordinate(), null,
                    SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH,
                    StrokeType.OUTSIDE);
        }
    }

    @Override
    protected void drawSimulation(WritableGridModel<ReboundingEntity> currentModel, int stepCount, int lastDrawnStepCount) {
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
                    .forEachOrdered(cell -> cellDrawer.draw(
                            entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId()),
                            basePainter, cell, stepCount));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
