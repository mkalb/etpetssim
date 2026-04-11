package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
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

    private static final Color FALLBACK_COLOR_BACKGROUND = Color.BLACK;
    private static final Color FALLBACK_COLOR_CELL = Color.GRAY;
    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 2.0d;

    private final Paint backgroundPaint;

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
                .colorAsOptional().orElse(FALLBACK_COLOR_BACKGROUND);
    }

    @Override
    protected void initSimulation(ReboundingConfig config, CellDimension cellDimension) {
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

        basePainter.fillCanvasBackground(backgroundPaint);

        currentModel.nonDefaultCells().forEachOrdered(cell ->
                basePainter.drawCell(cell.coordinate(),
                        entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId())
                                                .colorAsOptional().orElse(FALLBACK_COLOR_CELL),
                        null, 0.0d));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
