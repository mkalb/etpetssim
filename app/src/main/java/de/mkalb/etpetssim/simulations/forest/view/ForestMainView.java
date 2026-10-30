package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CanvasPainters;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.forest.model.ForestConfig;
import de.mkalb.etpetssim.simulations.forest.model.ForestSimulationManager;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

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

    private static final String FOREST_TOOL_ID_CYCLE_STATE = "forest.cycleState";
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
    protected void initializeSimulation(CanvasPainters painters, ForestConfig config, CellDimension cellDimension, WritableGridModel<ForestEntity> model) {
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(ForestEntity.EMPTY.descriptorId())
                .colorOrFallback();
        painters.base().fillCanvasBackground(backgroundColor);

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
    protected void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
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
    protected void drawSimulation(CanvasPainters painters, WritableGridModel<ForestEntity> currentGridModel, int stepCount, int lastDrawnStepCount) {
        if (cellDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        painters.dynamic().clearCanvasBackground();

        // small helper lambda to avoid code duplication when drawing different entity types
        Consumer<GridCell<ForestEntity>> drawCell = cell -> cellDrawer.draw(
                entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                painters.dynamic(), cell, stepCount);

        // draw tree cells first
        currentGridModel.filteredCells(ForestEntity::isTree)
                .forEach(drawCell);

        // then draw burning cells on top for better visibility
        currentGridModel.filteredCells(ForestEntity::isBurning)
                .forEach(drawCell);
    }

    @Override
    protected List<SimulationUserActionDescriptor<NoUserActionContext>> createUserActionDescriptors() {
        return List.of(new SimulationUserActionDescriptor<>(
                FOREST_TOOL_ID_CYCLE_STATE,
                NoUserActionContext.NO_CONTEXT,
                SimulationUserActionScope.CELL_SELECTED,
                FOREST_TOOLBAR_CYCLE_STATE,
                FOREST_TOOLBAR_CYCLE_STATE_TOOLTIP));
    }

}
