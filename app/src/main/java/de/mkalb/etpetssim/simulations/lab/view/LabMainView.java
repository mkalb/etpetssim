package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.engine.support.GridEntityUtils;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.shared.*;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabMainViewModel;
import de.mkalb.etpetssim.ui.*;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabMainView
        extends AbstractMainView<
        LabMainViewModel,
        NoUserActionContext,
        LabConfigView,
        LabControlView,
        LabObservationView> {

    private static final Color MOUSE_CLICK_COLOR = Color.ROSYBROWN;
    private static final Color MOUSE_HOVER_COLOR = Color.DARKSLATEBLUE;
    private static final Color TEXT_COLOR = Color.DARKSLATEGRAY;
    private static final Color TEXT_COLOR_GRAYSCALE = Color.BLACK;
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Color CANVAS_COLOR = Color.BLACK;
    private static final Color GRID_BACKGROUND_COLOR = Color.DIMGRAY;
    private static final Color TRANSLUCENT_WHITE = FXPaintFactory.adjustColorAlpha(Color.WHITE, 0.2); // for lightening effect
    private static final double MOUSE_CLICK_LINE_WIDTH = 4.0d;
    private static final double MOUSE_HOVER_LINE_WIDTH = 2.0d;
    private static final double SHAPE_LINE_WIDTH = 0.5d;
    private static final double TEST_LINE_WIDTH = 6.0d;
    private static final double HOVER_MIN_EDGE_LENGTH = 6.0d;
    private static final double MODEL_FILL_ALPHA = 0.5d;
    private static final double TEST_FILL_ALPHA = 0.5d;
    private static final int OUTER_HIGHLIGHT_RING_RADIUS = 5;
    private static final int INNER_HIGHLIGHT_RING_RADIUS = 3;
    private static final int HIGHLIGHT_RING_STROKE_SCALE = 2;
    private static final int NEIGHBOR_DIRECTION_TEXT_CAPACITY = 4;
    private static final int CHECKER_GROUP_SIZE = 2;
    private static final int CHECKER_GROUP_SHIFT = 1;

    private static final double NO_STROKE_LINE_WIDTH = 0.0d;
    private static final double ESTIMATED_CELL_STROKE_WIDTH = 1.0d;
    private static final double NEIGHBOR_COORDINATE_STROKE_WIDTH = 2.0d;
    private static final double EDGE_RESULT_STROKE_WIDTH = 3.0d;
    private static final double HOVER_POINTER_RADIUS = 2.0d;

    private static final int TEST_BOUNDING_BOX_COLUMN_1 = 2;
    private static final int TEST_BOUNDING_BOX_COLUMN_2 = 4;
    private static final int TEST_INNER_CIRCLE_COLUMN_1 = 6;
    private static final int TEST_INNER_CIRCLE_COLUMN_2 = 8;
    private static final int TEST_ROW_1 = 4;
    private static final int TEST_ROW_2 = 6;
    private static final int TEST_ROW_3 = 8;
    private static final int TEST_ROW_4 = 10;
    private static final int TEST_TRIANGLE_X = 11;
    private static final int TEST_TRIANGLE_Y = 4;
    private static final int TEST_HEXAGON_X = 9;
    private static final int TEST_HEXAGON_Y = 3;
    private static final int TEST_FRAME_SEGMENT_1_X = 0;
    private static final int TEST_FRAME_SEGMENT_1_Y = 1;
    private static final int TEST_FRAME_SEGMENT_2_X = 1;
    private static final int TEST_FRAME_SEGMENT_2_Y = 2;
    private static final int TEST_PIXEL_START_X = 50;
    private static final int TEST_PIXEL_END_X = 100;
    private static final int TEST_PIXEL_X_SCALE = 4;
    private static final int TEST_PIXEL_DIRECT_Y = 100;
    private static final int TEST_PIXEL_RECT_Y = 120;

    public LabMainView(LabMainViewModel viewModel,
                       LabConfigView configView,
                       LabControlView controlView,
                       LabObservationView observationView,
                       GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    @Override
    protected void registerViewModelListeners() {
        viewModel.setConfigChangedListener(this::disableCanvas);
        viewModel.setDrawRequestedListener(this::drawBaseCanvas);
        viewModel.setDrawModelRequestedListener(this::drawModel);
        viewModel.setDrawTestRequestedListener(this::drawTest);
    }

    @Override
    protected void applyGlobalUserActionAndRedraw(SimulationUserActionDescriptor<NoUserActionContext> descriptor) {
    }

    @Override
    protected void handleMouseClickedCoordinate(Point2D mousePoint, GridCoordinate mouseCoordinate, FXGridCanvasPainter painter) {
        painter.clearCanvasBackground();

        if (isLastClickedCoordinate(mouseCoordinate)) {
            resetSelection();
            return;
        }

        viewModel.updateClickedCoordinateProperties(mouseCoordinate);
        viewModel.updateSelectedGridCell(mouseCoordinate);
        painter.drawCellOuterCircle(mouseCoordinate, TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeType.OUTSIDE);

        viewModel.computeNeighborhoodHighlights(mouseCoordinate, painter.gridStructure())
                 .ifPresent(highlights -> drawNeighborhoodHighlights(highlights, painter));
    }

    private boolean isLastClickedCoordinate(GridCoordinate coordinate) {
        return viewModel.getLastClickedCoordinate().filter(coordinate::equals).isPresent();
    }

    private void resetSelection() {
        viewModel.resetClickedCoordinateProperties();
        viewModel.resetSelectedGridCell();
    }

    private void drawNeighborhoodHighlights(LabNeighborhoodHighlights highlights,
                                            FXGridCanvasPainter painter) {
        drawRing(highlights.ringCellsByRadius(), OUTER_HIGHLIGHT_RING_RADIUS, Color.GOLD, painter);
        drawRing(highlights.ringCellsByRadius(), INNER_HIGHLIGHT_RING_RADIUS, Color.TOMATO, painter);
        drawNeighborCoordinates(highlights.validNeighborCoordinates(), painter);
        drawNeighborsWithEdgeBehavior(highlights.validNeighborsWithEdgeBehavior(), painter);
        drawNeighborEdgeResults(highlights.neighborEdgeResults(), painter);
    }

    private void drawRing(SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCells,
                          int ringRadius,
                          Color strokeColor,
                          FXGridCanvasPainter painter) {
        ringCells.getOrDefault(ringRadius, Collections.emptySortedMap())
                 .values()
                 .forEach(ringCell -> painter.drawCellInnerCircle(ringCell.coordinate(),
                         null,
                         strokeColor,
                         HIGHLIGHT_RING_STROKE_SCALE * ringCell.reachedFromPreviousRing().size(),
                         StrokeType.INSIDE));
    }

    private void drawNeighborCoordinates(Collection<GridCoordinate> neighborCoordinates,
                                         FXGridCanvasPainter painter) {
        neighborCoordinates.forEach(neighborCoordinate -> painter.drawCell(neighborCoordinate, null, Color.ORANGE, NEIGHBOR_COORDINATE_STROKE_WIDTH));
    }

    private void drawNeighborsWithEdgeBehavior(Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> neighborsWithEdgeBehavior,
                                               FXGridCanvasPainter painter) {
        neighborsWithEdgeBehavior.forEach((neighborCoordinate, neighborCells) -> {
            painter.drawCell(neighborCoordinate, Color.YELLOW, null, NO_STROKE_LINE_WIDTH);
            drawNeighborDirections(neighborCoordinate, neighborCells, painter);
        });
    }

    private void drawNeighborDirections(GridCoordinate coordinate,
                                        Collection<CellNeighborWithEdgeBehavior> neighborCells,
                                        FXGridCanvasPainter painter) {
        if (cellFont == null) {
            return;
        }

        StringBuilder builder = new StringBuilder(NEIGHBOR_DIRECTION_TEXT_CAPACITY);
        for (CellNeighborWithEdgeBehavior neighborCell : neighborCells) {
            if (!builder.isEmpty()) {
                builder.append(" : ");
            }
            builder.append(neighborCell.direction().arrow());
        }
        painter.drawCenteredTextInCell(coordinate, builder.toString(), Color.BLACK, cellFont);
    }

    private void drawNeighborEdgeResults(Collection<EdgeBehaviorResult> edgeResults,
                                         FXGridCanvasPainter painter) {
        edgeResults.forEach(edgeResult -> {
            switch (edgeResult.action()) {
                case VALID -> painter.drawCell(edgeResult.mapped(), null, Color.DARKORANGE, EDGE_RESULT_STROKE_WIDTH);
                case WRAPPED -> painter.drawCell(edgeResult.mapped(), null, Color.DARKRED, EDGE_RESULT_STROKE_WIDTH);
                case BLOCKED -> painter.drawCell(edgeResult.mapped(), Color.DARKRED, null, NO_STROKE_LINE_WIDTH);
                case ABSORBED -> {
                    // nothing to render
                }
            }
        });
    }

    private void registerOverlayCanvasEvents() {
        // Exited
        overlayCanvas.setOnMouseExited(_ -> {
            if (overlayPainter == null) {
                return;
            }
            overlayPainter.clearCanvasBackground();
        });

        // Moved
        overlayCanvas.setOnMouseMoved(event -> {
            Point2D mousePoint = new Point2D(event.getX(), event.getY());
            drawMouseHover(mousePoint);
        });
    }

    private void drawMouseHover(Point2D mousePoint) {
        FXGridCanvasPainter localBasePainter = basePainter;
        FXGridCanvasPainter localOverlayPainter = overlayPainter;
        if ((localBasePainter == null) || (localOverlayPainter == null)) {
            return;
        }

        localOverlayPainter.clearCanvasBackground();
        drawEstimatedHoverCoordinate(mousePoint, localBasePainter, localOverlayPainter);
        drawPreciseHoverCoordinate(mousePoint, localBasePainter, localOverlayPainter);
        drawLastClickedCoordinate(localOverlayPainter);
        localOverlayPainter.drawCircle(mousePoint, HOVER_POINTER_RADIUS, Color.DARKGREEN, null, NO_STROKE_LINE_WIDTH, StrokeType.CENTERED);
    }

    private void drawEstimatedHoverCoordinate(Point2D mousePoint,
                                              FXGridCanvasPainter localBasePainter,
                                              FXGridCanvasPainter localOverlayPainter) {
        GridCoordinate estimatedCoordinate = GridGeometry.estimateGridCoordinate(
                mousePoint,
                localBasePainter.cellDimension(),
                localOverlayPainter.gridDimension2D(),
                viewModel.getStructure());
        if (!estimatedCoordinate.isIllegal()) {
            localOverlayPainter.drawCell(estimatedCoordinate, null, Color.RED, ESTIMATED_CELL_STROKE_WIDTH);
        }
    }

    private void drawPreciseHoverCoordinate(Point2D mousePoint,
                                            FXGridCanvasPainter localBasePainter,
                                            FXGridCanvasPainter localOverlayPainter) {
        GridCoordinate coordinate = GridGeometry.fromCanvasPosition(
                mousePoint,
                localBasePainter.cellDimension(),
                localOverlayPainter.gridDimension2D(),
                viewModel.getStructure());
        if (coordinate.isIllegal() || !localOverlayPainter.isInsideGrid(coordinate)) {
            return;
        }

        localOverlayPainter.drawCellBoundingBox(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeType.OUTSIDE);
        if (localOverlayPainter.cellDimension().edgeLength() >= HOVER_MIN_EDGE_LENGTH) {
            localOverlayPainter.drawCellInnerCircle(coordinate, Color.WHITE, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeType.INSIDE);
            drawHoverDescriptor(coordinate, localOverlayPainter);
        }
    }

    private void drawHoverDescriptor(GridCoordinate coordinate, FXGridCanvasPainter localOverlayPainter) {
        if (cellFont == null) {
            return;
        }
        GridEntityUtils.consumeDescriptorAt(coordinate, viewModel.getCurrentModel(), entityDescriptorRegistry,
                descriptor -> localOverlayPainter.drawCenteredTextInCell(coordinate, descriptor.shortName(), Color.RED, cellFont));
    }

    private void drawLastClickedCoordinate(FXGridCanvasPainter localOverlayPainter) {
        viewModel.getLastClickedCoordinate().ifPresent(
                lastCoordinate -> localOverlayPainter.drawCellOuterCircle(lastCoordinate,
                        TRANSLUCENT_WHITE,
                        MOUSE_CLICK_COLOR,
                        MOUSE_CLICK_LINE_WIDTH,
                        StrokeType.OUTSIDE));
    }

    private void disableCanvas() {
        overlayCanvas.setOnMouseClicked(null);
        overlayCanvas.setOnMouseExited(null);
        overlayCanvas.setOnMouseMoved(null);

        viewModel.resetClickedCoordinateProperties();
        viewModel.resetSelectedGridCell();

        if ((basePainter != null) && (dynamicPainter != null) && (overlayPainter != null)) {
            basePainter.clearCanvasBackground();
            dynamicPainter.clearCanvasBackground();
            overlayPainter.clearCanvasBackground();
            basePainter = null;
            dynamicPainter = null;
            overlayPainter = null;
        }
    }

    private void initializeCanvasAndPainters() {
        resetSelection();

        double cellEdgeLength = viewModel.getCellEdgeLength();
        GridStructure structure = viewModel.getStructure();

        createPainterAndUpdateCanvas(structure, cellEdgeLength);

        updateCanvasBorderPane(structure);

        rebuildActionToolBar();

        registerOverlayCanvasEvents();
    }

    private void drawBaseCanvas() {
        if (!viewModel.hasSimulationManager()) {
            AppLogger.warn("Simulation manager is not initialized, cannot draw base canvas.");
            disableCanvas();
            return;
        }

        LabConfig config = viewModel.getCurrentConfig();

        initializeCanvasAndPainters();

        boolean colorModeGrayscale = (config.colorMode() == LabColorMode.GRAYSCALE);
        boolean renderingModeCircle = (config.cellDisplayMode() == CellDisplayMode.CIRCLE) || (config.cellDisplayMode() == CellDisplayMode.CIRCLE_BORDERED);
        Color textColor = colorModeGrayscale ? TEXT_COLOR_GRAYSCALE : TEXT_COLOR;
        Color strokeColor = config.cellDisplayMode().hasBorder() ? STROKE_COLOR : null;

        drawBaseCanvasBackground(colorModeGrayscale);

        viewModel.getStructure()
                 .coordinatesStream()
                 .forEachOrdered(coordinate ->
                         drawBaseCanvasCoordinate(coordinate, colorModeGrayscale, renderingModeCircle,
                                 strokeColor, SHAPE_LINE_WIDTH,
                                 textColor));

        observationView.initializeForDraw();
    }

    private void drawBaseCanvasBackground(boolean colorModeGrayscale) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(CANVAS_COLOR);
        if (colorModeGrayscale) {
            basePainter.fillGridBackground(Color.WHITE);
        } else {
            basePainter.fillGridBackground(FXPaintFactory.createHorizontalGradient(GRID_BACKGROUND_COLOR.darker(), GRID_BACKGROUND_COLOR.brighter()));
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void drawBaseCanvasCoordinate(GridCoordinate coordinate,
                                          boolean colorModeGrayscale,
                                          boolean renderingModeCircle,
                                          @Nullable Color strokeColor,
                                          double strokeLineWidth,
                                          Color textColor) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        Color color = colorModeGrayscale ? determineCheckerGrayscaleColor(coordinate) : determineCheckerColor(coordinate);
        if (renderingModeCircle) {
            basePainter.drawCellInnerCircle(coordinate, color, strokeColor, strokeLineWidth, StrokeType.CENTERED);
        } else {
            basePainter.drawCell(coordinate, color, strokeColor, strokeLineWidth);
        }
        if (cellFont != null) {
            basePainter.drawCenteredTextInCell(coordinate, coordinate.toDisplayString(), textColor, cellFont);
        }
    }

    private void drawModel() {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        Color fillColor = FXPaintFactory.adjustColorAlpha(Color.RED, MODEL_FILL_ALPHA);
        viewModel.getCurrentModel()
                 .nonDefaultCells()
                 .forEach((GridCell<LabEntity> cell) -> basePainter.drawCell(cell.coordinate(), fillColor, null, NO_STROKE_LINE_WIDTH));
    }

    private void drawTest() {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        Color t1 = FXPaintFactory.adjustColorAlpha(Color.RED, TEST_FILL_ALPHA);
        Color t2 = FXPaintFactory.adjustColorAlpha(Color.YELLOW, TEST_FILL_ALPHA);

        // Draw bounding box
        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_1, TEST_ROW_1), t1, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_1, TEST_ROW_2), t2, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_1, TEST_ROW_3), t1, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_1, TEST_ROW_4), t2, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_2, TEST_ROW_1), null, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_2, TEST_ROW_2), null, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_2, TEST_ROW_3), null, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(TEST_BOUNDING_BOX_COLUMN_2, TEST_ROW_4), null, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        // draw inner circle
        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_1, TEST_ROW_1), t1, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_1, TEST_ROW_2), t2, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_1, TEST_ROW_3), t1, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_1, TEST_ROW_4), t2, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_2, TEST_ROW_1), null, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_2, TEST_ROW_2), null, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_2, TEST_ROW_3), null, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(TEST_INNER_CIRCLE_COLUMN_2, TEST_ROW_4), null, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        // draw pixel
        for (int x = TEST_PIXEL_START_X; x < TEST_PIXEL_END_X; x++) {
            basePainter.drawPixelDirect(x * TEST_PIXEL_X_SCALE, TEST_PIXEL_DIRECT_Y, Color.MAGENTA);
            basePainter.drawPixelRect(x * TEST_PIXEL_X_SCALE, TEST_PIXEL_RECT_Y, Color.RED);
        }

        // draw shapes
        basePainter.drawTriangle(new GridCoordinate(TEST_TRIANGLE_X, TEST_TRIANGLE_Y),
                GridGeometry.convertEdgeLengthToMatchWidth(basePainter.cellDimension().edgeLength(), basePainter.gridStructure().cellShape(), CellShape.TRIANGLE),
                Color.WHITE, Color.BLACK, TEST_LINE_WIDTH);
        basePainter.drawHexagon(new GridCoordinate(TEST_HEXAGON_X, TEST_HEXAGON_Y),
                GridGeometry.convertEdgeLengthToMatchWidth(basePainter.cellDimension().edgeLength(), basePainter.gridStructure().cellShape(), CellShape.HEXAGON),
                Color.WHITE, Color.BLACK, TEST_LINE_WIDTH);

        // draw frame segment
        basePainter.drawCellFrameSegment(new GridCoordinate(TEST_FRAME_SEGMENT_1_X, TEST_FRAME_SEGMENT_1_Y), Color.DARKGREEN, TEST_LINE_WIDTH, CellShapeSide.LEFT);
        basePainter.drawCellFrameSegment(new GridCoordinate(TEST_FRAME_SEGMENT_2_X, TEST_FRAME_SEGMENT_2_Y), Color.DARKBLUE, TEST_LINE_WIDTH, CellShapeSide.LEFT);
    }

    private Color determineCheckerColor(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % CHECKER_GROUP_SIZE;
        int rowGroup = coordinate.y() % CHECKER_GROUP_SIZE;

        return switch ((columnGroup << CHECKER_GROUP_SHIFT) | rowGroup) {
            case 0 -> Color.LIGHTSKYBLUE;       // Column 0, Row 0
            case 1 -> Color.LIGHTSTEELBLUE;     // Column 0, Row 1
            case 2 -> Color.PALEGREEN;          // Column 1, Row 0
            case 3 -> Color.MEDIUMAQUAMARINE;   // Column 1, Row 1
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

    private Color determineCheckerGrayscaleColor(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % CHECKER_GROUP_SIZE;
        int rowGroup = coordinate.y() % CHECKER_GROUP_SIZE;

        return switch ((columnGroup << CHECKER_GROUP_SHIFT) | rowGroup) {
            case 0 -> Color.WHITE;              // Column 0, Row 0
            case 1 -> Color.LIGHTGRAY;          // Column 0, Row 1
            case 2 -> Color.DARKGRAY;           // Column 1, Row 0
            case 3 -> Color.GRAY;               // Column 1, Row 1
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

}
