package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
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
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
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
        NoUserActionContext,
        LangtonConfigView,
        LangtonObservationView> {

    private static final String LANGTON_TOOLBAR_ADD_ANT = "langton.toolbar.addant";
    private static final String LANGTON_TOOLBAR_ADD_ANT_TOOLTIP = "langton.toolbar.addant.tooltip";

    private static final Color SELECTED_STROKE_COLOR = Color.RED;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private @Nullable CellDrawer<TerrainConstant> cellGroundDrawer;
    private @Nullable CellDrawer<AntEntity> cellAntDrawer;

    public LangtonMainView(DefaultMainViewModel<LangtonEntity, LangtonGridModel, LangtonConfig,
                                   LangtonStatistics, NoUserActionContext> viewModel,
                           GridEntityDescriptorRegistry entityDescriptorRegistry,
                           LangtonConfigView configView,
                           DefaultControlView controlView,
                           LangtonObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    @SuppressWarnings("MagicNumber")
    @Override
    protected void initSimulation(LangtonConfig config, CellDimension cellDimension) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(TerrainConstant.UNVISITED.descriptorId())
                .colorOrFallback();
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
            case CellDisplayMode.CIRCLE, CellDisplayMode.CIRCLE_BORDERED, CellDisplayMode.EMOJI ->
                    throw new IllegalArgumentException("CellDisplayMode not supported: " + config.cellDisplayMode());
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
    protected void drawSimulation(LangtonGridModel currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if ((cellGroundDrawer == null) || (cellAntDrawer == null)) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        var groundModel = currentModel.groundModel();
        var antModel = currentModel.antModel();

        if ((lastDrawnStepCount + 1) < stepCount) {
            // draw ground
            groundModel.nonDefaultCells()
                       .forEach(groundCell -> cellGroundDrawer.draw(
                               entityDescriptorRegistry.requireByDescriptorId(groundCell.descriptorId()),
                               basePainter, groundCell, stepCount));
            // draw ant
            antModel.nonDefaultCells()
                    .forEach(antCell -> cellAntDrawer.draw(
                            entityDescriptorRegistry.requireByDescriptorId(antCell.descriptorId()),
                            dynamicPainter, antCell, stepCount));
        } else {
            antModel.nonDefaultCells()
                    .forEach(antCell -> {
                        GridCell<TerrainConstant> groundCell = groundModel.getGridCell(antCell.coordinate());
                        // draw ground
                        cellGroundDrawer.draw(
                                entityDescriptorRegistry.requireByDescriptorId(groundCell.descriptorId()),
                                basePainter, groundCell, stepCount);
                        // draw ant
                        cellAntDrawer.draw(
                                entityDescriptorRegistry.requireByDescriptorId(antCell.descriptorId()),
                                dynamicPainter, antCell, stepCount);
                    });
        }
    }

    @Override
    protected List<Node> createActionToolBarNodes() {
        Button addNewAntButton = new Button(AppLocalization.getText(LANGTON_TOOLBAR_ADD_ANT));
        addNewAntButton.getStyleClass().add(FXStyleClasses.SIMULATION_TOOLBAR_BUTTON);
        addNewAntButton.setTooltip(new Tooltip(AppLocalization.getText(LANGTON_TOOLBAR_ADD_ANT_TOOLTIP)));
        addNewAntButton.setOnAction(_ -> applyUserActionAndRedraw(NoUserActionContext.NO_CONTEXT));
        addNewAntButton.disableProperty().bind(Bindings.isNull(viewModel.selectedGridCellProperty()));

        return List.of(addNewAntButton);
    }

}
