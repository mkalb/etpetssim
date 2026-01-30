package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridEntityUtils;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabMainViewModel;
import de.mkalb.etpetssim.ui.*;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class LabMainView
        extends AbstractMainView<
        LabMainViewModel,
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
    private static final Color TRANSLUCENT_SILVER = FXPaintFactory.adjustColorAlpha(Color.SILVER, 0.7d); // for reflection
    private static final double MOUSE_CLICK_LINE_WIDTH = 4.0d;
    private static final double MOUSE_HOVER_LINE_WIDTH = 2.0d;
    private static final double SHAPE_LINE_WIDTH = 0.5d;
    private static final double TEST_LINE_WIDTH = 6.0d;
    private static final double HOVER_MIN_EDGE_LENGTH = 6.0d;
    private static final double COLOR_ALPHA = 0.5d;

    public LabMainView(LabMainViewModel viewModel,
                       LabConfigView configView,
                       LabControlView controlView,
                       LabObservationView observationView,
                       GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel,
                configView, controlView, observationView,
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
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

    @Override
    protected void handleMouseClickedCoordinate(Point2D mousePoint, GridCoordinate mouseCoordinate, FXGridCanvasPainter painter) {
        var neighborhoodMode = viewModel.getCurrentConfig().neighborhoodMode();

        painter.clearCanvasBackground();

        if (viewModel.getLastClickedCoordinate().filter(c -> c.equals(mouseCoordinate)).isEmpty()) {
            viewModel.updateClickedCoordinateProperties(mouseCoordinate);
            painter.drawCellOuterCircle(mouseCoordinate, TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeType.OUTSIDE);

            GridStructure gridStructure = painter.gridStructure();
            CellNeighborhoods.cellNeighborsWithEdgeBehavior(mouseCoordinate, neighborhoodMode,
                                     gridStructure)
                             .forEach((neighborCoordinate, neighborCells) -> {
                                 if (gridStructure.isCoordinateValid(neighborCoordinate)) {
                                     painter.drawCell(neighborCoordinate, Color.YELLOW, null, 0.0d);
                                     if (cellFont != null) {
                                         StringBuilder b = new StringBuilder(4);
                                         for (CellNeighborWithEdgeBehavior cellNeighbor : neighborCells) {
                                             if (!b.isEmpty()) {
                                                 b.append(" : ");
                                             }
                                             b.append(cellNeighbor.direction().arrow());
                                         }
                                         painter.drawCenteredTextInCell(neighborCoordinate, b.toString(), Color.BLACK, cellFont);
                                     }
                                 }
                             });
            CellNeighborhoods.coordinatesOfNeighbors(mouseCoordinate,
                                     neighborhoodMode,
                                     gridStructure.cellShape(),
                                     2)
                             .forEach(neighborCoordinate -> {
                                 if (gridStructure.isCoordinateValid(neighborCoordinate)) {
                                     painter.drawCell(neighborCoordinate, null, Color.ORANGE, 2.0d);
                                 }
                             });
            CellNeighborhoods.neighborEdgeResults(mouseCoordinate,
                                     neighborhoodMode,
                                     gridStructure)
                             .forEach((edgeResult) -> {
                                 switch (edgeResult.action()) {
                                     case VALID -> painter.drawCell(edgeResult.mapped(), null, Color.DARKORANGE, 3.0d);
                                     case WRAPPED -> painter.drawCell(edgeResult.mapped(), null, Color.DARKRED, 3.0d);
                                     case REFLECTED ->
                                             painter.drawCell(edgeResult.mapped(), TRANSLUCENT_SILVER, null, 0.0d);
                                     case BLOCKED -> painter.drawCell(edgeResult.mapped(), Color.DARKRED, null, 0.0d);
                                     case ABSORBED -> {
                                         // do nothing
                                     }
                                 }
                             });
        } else {
            viewModel.resetClickedCoordinateProperties();
        }
    }

    private void registerEvents() {
        // Exited
        overlayCanvas.setOnMouseExited(_ -> {
            if ((basePainter == null) || (overlayPainter == null)) {
                return;
            }
            overlayPainter.clearCanvasBackground();
        });

        // Moved
        overlayCanvas.setOnMouseMoved(event -> {
            if ((basePainter == null) || (overlayPainter == null)) {
                return;
            }
            overlayPainter.clearCanvasBackground();
            Point2D mousePoint = new Point2D(event.getX(), event.getY());
            GridCoordinate estimatedCoordinate = GridGeometry.estimateGridCoordinate(mousePoint, basePainter.cellDimension(), overlayPainter.gridDimension2D(), viewModel.getStructure());
            if (!estimatedCoordinate.isIllegal()) {
                overlayPainter.drawCell(estimatedCoordinate, null, Color.RED, 1.0d);
            }
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(mousePoint, basePainter.cellDimension(), overlayPainter.gridDimension2D(), viewModel.getStructure());
            if (!coordinate.isIllegal() && !overlayPainter.isOutsideGrid(coordinate)) {
                overlayPainter.drawCellBoundingBox(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeType.OUTSIDE);
                if (overlayPainter.cellDimension().edgeLength() >= HOVER_MIN_EDGE_LENGTH) {
                    overlayPainter.drawCellInnerCircle(coordinate, Color.WHITE, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeType.INSIDE);
                    if (cellFont != null) {
                        GridEntityUtils.consumeDescriptorAt(coordinate, viewModel.getCurrentModel(), entityDescriptorRegistry,
                                descriptor -> overlayPainter.drawCenteredTextInCell(coordinate, descriptor.shortName(), Color.RED, cellFont));
                    }
                }
            }
            viewModel.getLastClickedCoordinate().ifPresent(lastCoordinate -> overlayPainter.drawCellOuterCircle(lastCoordinate, TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeType.OUTSIDE));

            overlayPainter.drawCircle(mousePoint, 2.0d, Color.DARKGREEN, null, 0.0d, StrokeType.CENTERED);
        });
    }

    private void disableCanvas() {
        overlayCanvas.setOnMouseClicked(null);
        overlayCanvas.setOnMouseExited(null);
        overlayCanvas.setOnMouseMoved(null);

        viewModel.resetClickedCoordinateProperties();

        if (basePainter != null) {
            basePainter.clearCanvasBackground();
        }
        basePainter = null;
        overlayPainter = null;
    }

    private void resetCanvasAndPainter(LabConfig config) {
        viewModel.resetClickedCoordinateProperties();

        double cellEdgeLength = viewModel.getCellEdgeLength();
        GridStructure structure = viewModel.getStructure();

        createPainterAndUpdateCanvas(structure, cellEdgeLength);

        updateCanvasBorderPane(structure);

        registerEvents();
    }

    private void drawBaseCanvas() {
        if (!viewModel.hasSimulationManager()) {
            AppLogger.warn("Simulation manager is not initialized, cannot draw base canvas.");
            disableCanvas();
            return;
        }

        LabConfig config = viewModel.getCurrentConfig();

        resetCanvasAndPainter(config);

        boolean colorModeGrayscale = (config.colorMode() == LabConfig.ColorMode.GRAYSCALE);
        boolean renderingModeCircle = (config.cellDisplayMode() == CellDisplayMode.CIRCLE) || (config.cellDisplayMode() == CellDisplayMode.CIRCLE_BORDERED);
        Color textColor = colorModeGrayscale ? TEXT_COLOR_GRAYSCALE : TEXT_COLOR;
        Color strokeColor = config.cellDisplayMode().hasBorder() ? STROKE_COLOR : null;

        drawBaseCanvasBackground(colorModeGrayscale);

        viewModel.getStructure()
                 .coordinatesStream()
                 .forEachOrdered(coordinate ->
                         drawCoordinateAtBaseCanvas(coordinate, colorModeGrayscale, renderingModeCircle,
                                 strokeColor, SHAPE_LINE_WIDTH,
                                 textColor));
    }

    private void drawBaseCanvasBackground(boolean colorModeBW) {
        if ((basePainter == null) || (overlayPainter == null)) {
            return;
        }

        basePainter.fillCanvasBackground(CANVAS_COLOR);
        if (colorModeBW) {
            basePainter.fillGridBackground(Color.WHITE);
        } else {
            basePainter.fillGridBackground(FXPaintFactory.createHorizontalGradient(GRID_BACKGROUND_COLOR.darker(), GRID_BACKGROUND_COLOR.brighter()));
        }
    }

    private void drawCoordinateAtBaseCanvas(GridCoordinate coordinate,
                                            boolean colorModeGrayscale, boolean renderingModeCircle,
                                            @Nullable Color strokeColor, double strokeLineWidth,
                                            Color textColor) {
        if ((basePainter == null) || (overlayPainter == null)) {
            return;
        }

        Color color = colorModeGrayscale ? determineColumnGrayscaleColor(coordinate) : determineColumnSimilarityColor(coordinate);
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
        if ((basePainter == null) || (overlayPainter == null)) {
            return;
        }

        Color fillColor = FXPaintFactory.adjustColorAlpha(Color.RED, COLOR_ALPHA);
        viewModel.getCurrentModel()
                 .nonDefaultCells()
                 .forEach((GridCell<LabEntity> cell) -> basePainter.drawCell(cell.coordinate(), fillColor, null, 0.0d));
    }

    private void drawTest() {
        if ((basePainter == null) || (overlayPainter == null)) {
            return;
        }

        Color t1 = FXPaintFactory.adjustColorAlpha(Color.RED, COLOR_ALPHA);
        Color t2 = FXPaintFactory.adjustColorAlpha(Color.YELLOW, COLOR_ALPHA);

        // Draw bounding box
        basePainter.drawCellBoundingBox(new GridCoordinate(2, 4), t1, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(2, 6), t2, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(2, 8), t1, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(2, 10), t2, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        basePainter.drawCellBoundingBox(new GridCoordinate(4, 4), null, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(4, 6), null, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(4, 8), null, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(4, 10), null, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        // draw inner circle
        basePainter.drawCellInnerCircle(new GridCoordinate(6, 4), t1, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(6, 6), t2, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(6, 8), t1, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(6, 10), t2, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        basePainter.drawCellInnerCircle(new GridCoordinate(8, 4), null, t2, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(8, 6), null, t1, TEST_LINE_WIDTH, StrokeType.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(8, 8), null, t2, TEST_LINE_WIDTH, StrokeType.OUTSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(8, 10), null, t1, TEST_LINE_WIDTH, StrokeType.OUTSIDE);

        // draw pixel
        for (int x = 50; x < 100; x++) {
            basePainter.drawPixelDirect(x * 4, 100, Color.MAGENTA);
            basePainter.drawPixelRect(x * 4, 120, Color.RED);
        }

        // draw shapes
        basePainter.drawTriangle(new GridCoordinate(11, 4),
                GridGeometry.convertEdgeLengthToMatchWidth(basePainter.cellDimension().edgeLength(), basePainter.gridStructure().cellShape(), CellShape.TRIANGLE),
                Color.WHITE, Color.BLACK, TEST_LINE_WIDTH);
        basePainter.drawHexagon(new GridCoordinate(9, 3),
                GridGeometry.convertEdgeLengthToMatchWidth(basePainter.cellDimension().edgeLength(), basePainter.gridStructure().cellShape(), CellShape.HEXAGON),
                Color.WHITE, Color.BLACK, TEST_LINE_WIDTH);

        // draw frame segment
        basePainter.drawCellFrameSegment(new GridCoordinate(0, 1), Color.DARKGREEN, TEST_LINE_WIDTH, CellShapeSide.LEFT);
        basePainter.drawCellFrameSegment(new GridCoordinate(1, 2), Color.DARKBLUE, TEST_LINE_WIDTH, CellShapeSide.LEFT);
    }

    private Color determineColumnSimilarityColor(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % 2;
        int rowGroup = coordinate.y() % 2;

        return switch ((columnGroup << 1) | rowGroup) {
            case 0 -> Color.LIGHTSKYBLUE;       // Column 0, Row 0
            case 1 -> Color.LIGHTSTEELBLUE;     // Column 0, Row 1
            case 2 -> Color.PALEGREEN;          // Column 1, Row 0
            case 3 -> Color.MEDIUMAQUAMARINE;   // Column 1, Row 1
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

    private Color determineColumnGrayscaleColor(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % 2;
        int rowGroup = coordinate.y() % 2;

        return switch ((columnGroup << 1) | rowGroup) {
            case 0 -> Color.WHITE;              // Column 0, Row 0
            case 1 -> Color.LIGHTGRAY;          // Column 0, Row 1
            case 2 -> Color.DARKGRAY;           // Column 1, Row 0
            case 3 -> Color.GRAY;               // Column 1, Row 1
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

}
