package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LangtonMainView
        extends AbstractDefaultMainView<
        LangtonEntity,
        LangtonGridModel,
        LangtonConfig,
        LangtonStatistics,
        LangtonConfigView,
        LangtonObservationView> {

    private final Paint backgroundPaint;
    private @Nullable CellDrawer<LangtonGroundEntity> cellGroundDrawer;
    private @Nullable CellDrawer<LangtonAntEntity> cellAntDrawer;

    public LangtonMainView(DefaultMainViewModel<LangtonEntity, LangtonGridModel, LangtonConfig, LangtonStatistics> viewModel,
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

    @SuppressWarnings("MagicNumber")
    @Override
    protected void initSimulation(LangtonConfig config, CellDimension cellDimension) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        basePainter.fillCanvasBackground(backgroundPaint);

        double strokeLineWidth = (cellDimension.innerRadius() < 2.0d) ? 0.0d : 1.0d;

        cellGroundDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            null,
                            0.0d);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            backgroundPaint,
                            strokeLineWidth);
            default -> null;
        };
        cellAntDrawer = (descriptor, painter, cell, _) -> {
            if ((cellEmojiFont != null)
                    && (cellDimension.innerRadius() > 5.0d)
                    && (descriptor.borderColor() != null)
                    && (cell.entity() instanceof LangtonAnt ant)) {
                painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), descriptor.borderColor(), 1.0d, StrokeType.INSIDE);
                painter.drawCenteredTextInCell(cell.coordinate(), ant.direction().arrow(), descriptor.borderColor(), cellEmojiFont);
            } else if (cellDimension.innerRadius() > 2.0d) {
                painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), null, 0.0d, StrokeType.INSIDE);
            } else {
                painter.drawCell(cell.coordinate(), descriptor.color(), null, 0.0d);
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
    protected void drawSimulation(LangtonGridModel currentModel, int stepCount, int lastDrawnStepCount) {
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

        ReadableGridModel<LangtonGroundEntity> groundModel = currentModel.groundModel();
        ReadableGridModel<LangtonAntEntity> antModel = currentModel.antModel();

        if ((lastDrawnStepCount + 1) < stepCount) {
            groundModel.nonDefaultCells()
                       .forEachOrdered(groundCell -> {
                           // draw ground
                           cellGroundDrawer.draw(entityDescriptorRegistry.getRequiredByDescriptorId(groundCell.entity().descriptorId()),
                                   basePainter, groundCell, stepCount);
                       });
            antModel.nonDefaultCells()
                    .forEachOrdered(antCell -> {
                        // draw ant
                        cellAntDrawer.draw(entityDescriptorRegistry.getRequiredByDescriptorId(antCell.entity().descriptorId()),
                                overlayPainter, antCell, stepCount);
                    });
        } else {
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
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
