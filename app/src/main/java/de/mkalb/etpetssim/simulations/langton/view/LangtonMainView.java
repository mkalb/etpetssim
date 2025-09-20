package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.view.CellDrawer;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.StrokeAdjustment;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LangtonMainView
        extends AbstractDefaultMainView<
        LangtonEntity,
        CompositeGridModel<LangtonEntity>,
        LangtonConfig,
        LangtonStatistics,
        LangtonConfigView,
        LangtonObservationView> {

    private final Paint backgroundPaint;
    private @Nullable CellDrawer<LangtonGroundEntity> cellGroundDrawer;
    private @Nullable CellDrawer<LangtonAntEntity> cellAntDrawer;

    public LangtonMainView(DefaultMainViewModel<LangtonEntity, CompositeGridModel<LangtonEntity>, LangtonConfig, LangtonStatistics> viewModel,
                           GridEntityDescriptorRegistry entityDescriptorRegistry,
                           LangtonConfigView configView,
                           DefaultControlView controlView,
                           LangtonObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(LangtonGroundEntity.UNVISITED.descriptorId())
                .colorAsOptional().orElse(Color.WHITE);
    }

    @Override
    protected void initSimulation(LangtonConfig config, CellDimension cellDimension) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        basePainter.fillCanvasBackground(backgroundPaint);

        cellGroundDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            null,
                            0.0d);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            backgroundPaint,
                            1.0d);
            default -> null;
        };
        cellAntDrawer = (descriptor, painter, cell, stepCount) -> {
            painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), descriptor.borderColor(), 1.0d, StrokeAdjustment.INSIDE);
            if ((cellEmojiFont != null)
                    && (descriptor.borderColor() != null)
                    && (cell.entity() instanceof LangtonAnt ant)) {
                painter.drawCenteredTextInCell(cell.coordinate(), ant.direction().arrow(), descriptor.borderColor(), cellEmojiFont);
            }
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<LangtonEntity> oldGridCell,
                                          @Nullable GridCell<LangtonEntity> newGridCell) {
        // Do nothing
    }

    @Override
    protected void drawSimulation(CompositeGridModel<LangtonEntity> currentModel, int stepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (overlayPainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellGroundDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }
        if (cellAntDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        overlayPainter.clearCanvasBackground();

        ReadableGridModel<LangtonGroundEntity> groundModel = (ReadableGridModel<LangtonGroundEntity>) currentModel.getLayer(LangtonSimulationManager.MODEL_LAYER_GROUND);
        ReadableGridModel<LangtonAntEntity> antModel = (ReadableGridModel<LangtonAntEntity>) currentModel.getLayer(LangtonSimulationManager.MODEL_LAYER_ANT);

        antModel.nonDefaultCells()
                .forEachOrdered(antCell -> {
                    GridCell<LangtonGroundEntity> groundCell = groundModel.getGridCell(antCell.coordinate());
                    // draw ground
                    cellGroundDrawer.draw(entityDescriptorRegistry.getRequiredByDescriptorId(groundCell.entity().descriptorId()),
                            basePainter, groundCell, stepCount);
                    // draw ant
                    cellAntDrawer.draw(entityDescriptorRegistry.getRequiredByDescriptorId(antCell.entity().descriptorId()),
                            overlayPainter, antCell, stepCount);
                });
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}

