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

    // Entity color
    public static final Color HEAD_ALIVE_FILL = Color.web("#00BCD4");
    public static final Color HEAD_ALIVE_BORDER = Color.web("#008BA3");
    public static final Color HEAD_DEAD_FILL = Color.web("#A33A3A");
    public static final Color HEAD_DEAD_BORDER = Color.web("#6E2626");
    public static final Color SEGMENT_ALIVE_FILL = Color.web("#4CAF50");
    public static final Color SEGMENT_ALIVE_BORDER = Color.web("#2E7D32");
    public static final Color SEGMENT_DEAD_FILL = Color.web("#8D6E63");
    public static final Color SEGMENT_DEAD_BORDER = Color.web("#5D4037");

    // Entity stroke widths
    private static final double WALL_STROKE_LINE_WIDTH = 0.5d;
    private static final double FOOD_STROKE_LINE_WIDTH = 2.0d;
    private static final double SNAKE_SEGMENT_STROKE_LINE_WIDTH = 1.0d;
    private static final double SNAKE_HEAD_STROKE_LINE_WIDTH = 2.0d;

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
        if ((oldGridCell != null) || (newGridCell != null)) {
            painter.clearCanvasBackground();
        }
        if (newGridCell != null) {
            if (newGridCell.entity().isStaticTerrain()) {
                painter.drawCell(newGridCell.coordinate(), null,
                        SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH);
            } else {
                painter.drawCellOuterCircle(newGridCell.coordinate(), null,
                        SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH,
                        StrokeType.OUTSIDE);
                if (newGridCell.entity() instanceof SnakeHead head) {
                    for (GridCoordinate coordinate : head.currentSegments()) {
                        painter.drawCell(coordinate, null,
                                SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH);
                    }
                }
            }
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
                            basePainter.drawCell(coordinate, wallDescriptor.color(), wallDescriptor.borderColor(), WALL_STROKE_LINE_WIDTH));
        currentModel.filteredCoordinates(e ->
                            Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD))
                    .forEach(coordinate ->
                            basePainter.drawCellInnerCircle(coordinate, growthFoodDescriptor.color(), growthFoodDescriptor.borderColor(), FOOD_STROKE_LINE_WIDTH, StrokeType.INSIDE));
        currentModel.filteredCells(e -> Objects.equals(e.descriptorId(), SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD))
                    .forEach(cell -> {
                        if (cell.entity() instanceof SnakeHead head) {
                            Color snakeHeadColor = head.isDead() ? HEAD_DEAD_FILL : HEAD_ALIVE_FILL;
                            Color snakeHeadColorBorder = head.isDead() ? HEAD_DEAD_BORDER : HEAD_ALIVE_BORDER;
                            Color snakeSegmentColor = head.isDead() ? SEGMENT_DEAD_FILL : SEGMENT_ALIVE_FILL;
                            Color snakeSegmentColorBorder = head.isDead() ? SEGMENT_DEAD_BORDER : SEGMENT_ALIVE_BORDER;
                            for (GridCoordinate coordinate : head.currentSegments()) {
                                basePainter.drawCell(coordinate, snakeSegmentColor, snakeSegmentColorBorder, SNAKE_SEGMENT_STROKE_LINE_WIDTH);
                            }
                            basePainter.drawCellInnerCircle(cell.coordinate(), snakeHeadColor, snakeHeadColorBorder, SNAKE_HEAD_STROKE_LINE_WIDTH,
                                    StrokeType.OUTSIDE);
                        }
                    });
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
