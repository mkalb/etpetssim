package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.model.LangtonGridModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
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

    private final Color backgroundColor;
    private @Nullable CellDrawer<TerrainConstant> cellGroundDrawer;
    private @Nullable CellDrawer<AntEntity> cellAntDrawer;

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
        backgroundColor = entityDescriptorRegistry
                .getRequiredByDescriptorId(TerrainConstant.UNVISITED.descriptorId())
                .colorOrFallback();
    }

    @SuppressWarnings("MagicNumber")
    @Override
    protected void initSimulation(LangtonConfig config, CellDimension cellDimension) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        basePainter.fillCanvasBackground(backgroundColor);

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellGroundDrawer = switch (config.cellDisplayMode()) {
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
                            backgroundColor,
                            strokeLineWidth);
            default -> throw new IllegalArgumentException("CellDisplayMode not supported!");
        };
        cellAntDrawer = (descriptor, painter, cell, _) -> {
            if ((cellEmojiFont != null)
                    && (cellDimension.innerRadius() > 5.0d)
                    && (descriptor.borderColor() != null)
                    && (cell.entity() instanceof Ant ant)) {
                painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), descriptor.borderColor(), 1.0d, StrokeType.INSIDE);
                painter.drawCenteredTextInCell(cell.coordinate(), ant.direction().arrow(), descriptor.borderColor(), cellEmojiFont);
            } else if (cellDimension.innerRadius() > 2.0d) {
                painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), null, NO_STROKE_LINE_WIDTH, StrokeType.INSIDE);
            } else {
                painter.drawCell(cell.coordinate(), descriptor.color(), null, NO_STROKE_LINE_WIDTH);
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

        ReadableGridModel<TerrainConstant> groundModel = currentModel.groundModel();
        ReadableGridModel<AntEntity> antModel = currentModel.antModel();

        if ((lastDrawnStepCount + 1) < stepCount) {
            // draw ground
            groundModel.nonDefaultCells()
                       .forEachOrdered(groundCell -> cellGroundDrawer.draw(
                               entityDescriptorRegistry.getRequiredByDescriptorId(groundCell.descriptorId()),
                               basePainter, groundCell, stepCount));
            // draw ant
            antModel.nonDefaultCells()
                    .forEachOrdered(antCell -> cellAntDrawer.draw(
                            entityDescriptorRegistry.getRequiredByDescriptorId(antCell.descriptorId()),
                            overlayPainter, antCell, stepCount));
        } else {
            antModel.nonDefaultCells()
                    .forEachOrdered(antCell -> {
                        GridCell<TerrainConstant> groundCell = groundModel.getGridCell(antCell.coordinate());
                        // draw ground
                        cellGroundDrawer.draw(
                                entityDescriptorRegistry.getRequiredByDescriptorId(groundCell.descriptorId()),
                                basePainter, groundCell, stepCount);
                        // draw ant
                        cellAntDrawer.draw(
                                entityDescriptorRegistry.getRequiredByDescriptorId(antCell.descriptorId()),
                                overlayPainter, antCell, stepCount);
                    });
        }
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
