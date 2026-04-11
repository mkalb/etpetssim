package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsConfig;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsGridModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsMainView extends AbstractDefaultMainView<
        EtpetsEntity,
        EtpetsGridModel,
        EtpetsConfig,
        EtpetsStatistics,
        EtpetsConfigView,
        EtpetsObservationView> {

    private static final Color FALLBACK_COLOR_BACKGROUND = Color.BLACK;
    private static final Color FALLBACK_COLOR_CELL = Color.GRAY;
    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 2.0d;

    private final Paint backgroundPaint;

    public EtpetsMainView(DefaultMainViewModel<EtpetsEntity, EtpetsGridModel, EtpetsConfig, EtpetsStatistics> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          EtpetsConfigView configView,
                          DefaultControlView controlView,
                          EtpetsObservationView observationView) {
        super(viewModel, configView, controlView, observationView, entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_GROUND)
                .colorAsOptional().orElse(FALLBACK_COLOR_BACKGROUND);
    }

    @Override
    protected void initSimulation(EtpetsConfig config, CellDimension cellDimension) {
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<EtpetsEntity> oldGridCell,
                                          @Nullable GridCell<EtpetsEntity> newGridCell) {
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
    protected void drawSimulation(EtpetsGridModel currentModel, int stepCount, int lastDrawnStepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundPaint);

        currentModel.terrainModel().nonDefaultCells().forEach(cell ->
                basePainter.drawCell(cell.coordinate(),
                        entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId())
                                                .colorAsOptional().orElse(FALLBACK_COLOR_CELL),
                        null,
                        0.0d));

        currentModel.resourceModel().nonDefaultCells().forEach(cell ->
                basePainter.drawCellInnerCircle(cell.coordinate(),
                        entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId())
                                                .colorAsOptional().orElse(FALLBACK_COLOR_CELL),
                        null,
                        0.0d,
                        StrokeType.INSIDE));

        currentModel.agentModel().nonDefaultCells().forEach(cell ->
                basePainter.drawCellInnerCircle(cell.coordinate(),
                        entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId())
                                                .colorAsOptional().orElse(FALLBACK_COLOR_CELL),
                        null,
                        0.0d,
                        StrokeType.INSIDE));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}

