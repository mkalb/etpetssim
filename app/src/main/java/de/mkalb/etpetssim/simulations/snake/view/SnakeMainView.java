package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeMainView
        extends AbstractDefaultMainView<
        SnakeEntity,
        WritableGridModel<SnakeEntity>,
        SnakeConfig,
        SnakeStatistics,
        SnakeConfigView,
        SnakeObservationView> {

    private static final Color FALLBACK_COLOR_BACKGROUND = Color.BLACK;
    private static final Color SELECTED_STROKE_COLOR = Color.PINK;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private final Paint backgroundPaint;

    public SnakeMainView(DefaultMainViewModel<SnakeEntity, WritableGridModel<SnakeEntity>, SnakeConfig,
                                 SnakeStatistics> viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         SnakeConfigView configView,
                         DefaultControlView controlView,
                         SnakeObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(SnakeEntity.DESCRIPTOR_ID_GROUND)
                .colorAsOptional().orElse(FALLBACK_COLOR_BACKGROUND);
    }

    @Override
    protected void initSimulation(SnakeConfig config, CellDimension cellDimension) {
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<SnakeEntity> oldGridCell,
                                          @Nullable GridCell<SnakeEntity> newGridCell) {
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
    protected void drawSimulation(WritableGridModel<SnakeEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundPaint);

        var wallDescriptor = entityDescriptorRegistry.getRequiredByDescriptorId(SnakeEntity.DESCRIPTOR_ID_WALL);
        var growthFoodDescriptor = entityDescriptorRegistry.getRequiredByDescriptorId(SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD);

        currentModel.filteredCoordinates(e ->
                            Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_WALL))
                    .forEach(coordinate ->
                            basePainter.drawCell(coordinate, wallDescriptor.color(), wallDescriptor.borderColor(), 1.0d));
        currentModel.filteredCoordinates(e ->
                            Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD))
                    .forEach(coordinate ->
                            basePainter.drawCellInnerCircle(coordinate, growthFoodDescriptor.color(), growthFoodDescriptor.borderColor(), 1.0d, StrokeType.INSIDE));
        currentModel.filteredCells(e -> Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD))
                    .forEach(cell -> {
                        if (cell.entity() instanceof SnakeHead head) {
                            // TODO Choose colors based on snake ID.
                            Color snakeHeadColor = head.isDead() ? Color.PINK : Color.LIGHTBLUE;
                            Color snakeSegmentColor = head.isDead() ? Color.RED : Color.BLUE;
                            for (GridCoordinate coordinate : head.currentSegments()) {
                                basePainter.drawCell(coordinate, snakeSegmentColor, snakeSegmentColor, 1.0d);
                            }
                            basePainter.drawCellInnerCircle(cell.coordinate(), snakeHeadColor, snakeHeadColor, 1.0d,
                                    StrokeType.OUTSIDE);
                        }
                    });
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
