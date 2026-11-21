package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class FXGridCanvasPainterTest {

    private static final int CANVAS_SIZE = 120;
    private static final double EDGE_LENGTH = 10.0d;

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static GridStructure createStructure() {
        return createStructure(CellShape.SQUARE);
    }

    private static GridStructure createStructure(CellShape shape) {
        return new GridStructure(new GridTopology(shape, GridEdgeBehavior.ABSORB_XY),
                GridSize.square(GridSize.MIN_SIZE));
    }

    private static FXGridCanvasPainter createPainter(Canvas canvas) {
        return new FXGridCanvasPainter(canvas, createStructure(), EDGE_LENGTH);
    }

    private static Color snapshotColorAt(Canvas canvas, int x, int y) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage snapshot = canvas.snapshot(parameters, null);
        return snapshot.getPixelReader().getColor(x, y);
    }

    private static boolean isPaintedAt(Canvas canvas, int x, int y) {
        return snapshotColorAt(canvas, x, y).getOpacity() > 0.0d;
    }

    private static int countPaintedPixels(Canvas canvas) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        PixelReader pixelReader = canvas.snapshot(parameters, null).getPixelReader();
        int count = 0;
        for (int x = 0; x < CANVAS_SIZE; x++) {
            for (int y = 0; y < CANVAS_SIZE; y++) {
                if (pixelReader.getColor(x, y).getOpacity() > 0.0d) {
                    count++;
                }
            }
        }
        return count;
    }

    private static Canvas createStrokedRectangleCanvas(Rectangle2D rectangle, StrokeType strokeType) {
        Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        createPainter(canvas).drawRectangle(rectangle, null, Color.RED, 4.0d, strokeType);
        return canvas;
    }

    private static Canvas createStrokedCircleCanvas(StrokeType strokeType) {
        Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        createPainter(canvas).drawCircle(new Point2D(60, 60), 10.0d, null, Color.RED, 4.0d, strokeType);
        return canvas;
    }

    private static boolean isPaintedNear(Canvas canvas, int x, int y) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        PixelReader pixelReader = canvas.snapshot(parameters, null).getPixelReader();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                if (pixelReader.getColor(x + dx, y + dy).getOpacity() > 0.0d) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isCellOutsideCanvasWithSize(FXGridCanvasPainter painter, GridCoordinate coordinate,
                                                       double canvasWidth, double canvasHeight) {
        painter.canvas().setWidth(canvasWidth);
        painter.canvas().setHeight(canvasHeight);
        return painter.isCellOutsideCanvas(coordinate);
    }

    private static void assertColorEquals(Color expected, Color actual) {
        assertAll(
                () -> assertEquals(expected.getRed(), actual.getRed(), 0.000_001d),
                () -> assertEquals(expected.getGreen(), actual.getGreen(), 0.000_001d),
                () -> assertEquals(expected.getBlue(), actual.getBlue(), 0.000_001d),
                () -> assertEquals(expected.getOpacity(), actual.getOpacity(), 0.000_001d)
        );
    }

    @Test
    void testConstructor() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            GridStructure structure = createStructure();
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, structure, EDGE_LENGTH);

            assertAll(
                    () -> assertSame(canvas, painter.canvas()),
                    () -> assertSame(structure, painter.gridStructure()),
                    () -> assertSame(canvas.getGraphicsContext2D(), painter.graphicsContext2D()),
                    () -> assertEquals(GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE),
                            painter.cellDimension()),
                    () -> assertEquals(
                            GridGeometry.computeGridDimension(structure.size(), EDGE_LENGTH, CellShape.SQUARE),
                            painter.gridDimension2D())
            );
        });
    }

    @Test
    void testConstructorRejectsCellEdgeLengthOutOfRange() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            GridStructure structure = createStructure();

            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new FXGridCanvasPainter(canvas, structure, GridGeometry.MIN_EDGE_LENGTH - 0.5d)),
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> new FXGridCanvasPainter(canvas, structure, GridGeometry.MAX_EDGE_LENGTH + 0.5d))
            );
        });
    }

    @Test
    void testIsInsideGrid() {
        FXTestSupport.runAndWait(() -> {
            FXGridCanvasPainter painter = createPainter(new Canvas(CANVAS_SIZE, CANVAS_SIZE));
            GridSize size = painter.gridStructure().size();

            assertAll(
                    () -> assertTrue(painter.isInsideGrid(GridCoordinate.ORIGIN)),
                    () -> assertFalse(painter.isInsideGrid(new GridCoordinate(size.width(), size.height())))
            );
        });
    }

    @Test
    void testIsOutsideGrid() {
        FXTestSupport.runAndWait(() -> {
            FXGridCanvasPainter painter = createPainter(new Canvas(CANVAS_SIZE, CANVAS_SIZE));
            GridSize size = painter.gridStructure().size();

            assertAll(
                    () -> assertFalse(painter.isOutsideGrid(GridCoordinate.ORIGIN)),
                    () -> assertTrue(painter.isOutsideGrid(new GridCoordinate(size.width(), size.height())))
            );
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testFillCanvasBackground() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.fillCanvasBackground(Color.RED);

            assertColorEquals(Color.RED, snapshotColorAt(canvas, 5, 5));
        });
    }

    @Test
    void testClearCanvasBackground() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);
            painter.fillCanvasBackground(Color.RED);

            painter.clearCanvasBackground();

            assertEquals(0.0d, snapshotColorAt(canvas, 5, 5).getOpacity(), 0.000_001d);
        });
    }

    @Test
    void testFillGridBackground() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.fillGridBackground(Color.BLUE);

            assertAll(
                    () -> assertColorEquals(Color.BLUE, snapshotColorAt(canvas, 5, 5)),
                    () -> assertEquals(0.0d, snapshotColorAt(canvas, 95, 95).getOpacity(), 0.000_001d)
            );
        });
    }

    @Test
    void testClearGridBackground() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);
            painter.fillGridBackground(Color.BLUE);

            painter.clearGridBackground();

            assertEquals(0.0d, snapshotColorAt(canvas, 5, 5).getOpacity(), 0.000_001d);
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testDrawCell() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCell(GridCoordinate.ORIGIN, Color.RED, null, 0.0d);

            assertColorEquals(Color.RED, snapshotColorAt(canvas, 5, 5));
        });
    }

    @ParameterizedTest
    @EnumSource(CellShape.class)
    void testDrawCellFillsCellCenterForEachShape(CellShape shape) {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(shape), EDGE_LENGTH);
            GridCoordinate coordinate = new GridCoordinate(1, 1);
            Point2D center = GridGeometry.computeCellCenter(coordinate, painter.cellDimension(), shape);
            int centerX = Math.toIntExact(Math.round(center.getX()));
            int centerY = Math.toIntExact(Math.round(center.getY()));

            painter.drawCell(coordinate, Color.RED, null, 0.0d);

            assertAll(
                    () -> assertEquals(Color.RED, snapshotColorAt(canvas, centerX, centerY)),
                    () -> assertFalse(isPaintedAt(canvas, 0, 0))
            );
        });
    }

    @Test
    void testDrawCellDrawsPartlyVisibleCellsAtCanvasEdges() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(35, 35);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCell(new GridCoordinate(3, 0), Color.RED, null, 0.0d);
            painter.drawCell(new GridCoordinate(0, 3), Color.BLUE, null, 0.0d);

            assertAll(
                    () -> assertEquals(Color.RED, snapshotColorAt(canvas, 33, 5)),
                    () -> assertEquals(Color.BLUE, snapshotColorAt(canvas, 5, 33))
            );
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testDrawScaledCell() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawScaledCell(new GridCoordinate(1, 0), Color.GREEN, null, 0.0d, 1.0d);

            assertColorEquals(Color.GREEN, snapshotColorAt(canvas, 15, 5));
        });
    }

    @Test
    void testDrawCellFrameSegment() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCellFrameSegment(new GridCoordinate(2, 0), Color.BLUE, 1.0d, CellShapeSide.TOP);

            assertTrue(snapshotColorAt(canvas, 25, 0).getOpacity() > 0.0d);
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testDrawCellBoundingBox() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCellBoundingBox(new GridCoordinate(3, 0), Color.YELLOW, null, 0.0d, StrokeType.CENTERED);

            assertColorEquals(Color.YELLOW, snapshotColorAt(canvas, 35, 5));
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testDrawCellInnerCircle() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCellInnerCircle(new GridCoordinate(4, 0), Color.BLACK, null, 0.0d, StrokeType.CENTERED);

            assertColorEquals(Color.BLACK, snapshotColorAt(canvas, 45, 5));
        });
    }

    @Test
    void testDrawCellOuterCircle() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCellOuterCircle(new GridCoordinate(5, 0), Color.PURPLE, null, 0.0d, StrokeType.CENTERED);

            assertTrue(snapshotColorAt(canvas, 55, 5).getOpacity() > 0.0d);
        });
    }

    @Test
    void testDrawTriangle() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawTriangle(GridCoordinate.ORIGIN, EDGE_LENGTH, Color.RED, null, 0.0d);

            assertTrue(snapshotColorAt(canvas, 5, 5).getOpacity() > 0.0d);
        });
    }

    @Test
    void testDrawTriangleMatchingCellWidth() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawTriangleMatchingCellWidth(new GridCoordinate(2, 0), Color.GREEN, null, 0.0d);

            assertTrue(snapshotColorAt(canvas, 25, 5).getOpacity() > 0.0d);
        });
    }

    @Test
    void testDrawTriangleMatchingCellWidthSkipsEdgeLengthAboveMax() {
        FXTestSupport.runAndWait(() -> {
            double maxHexagonEdgeLength = GridGeometry.MAX_EDGE_LENGTH / 2.0d;
            Canvas atMaxCanvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            Canvas aboveMaxCanvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);

            new FXGridCanvasPainter(atMaxCanvas, createStructure(CellShape.HEXAGON), maxHexagonEdgeLength)
                    .drawTriangleMatchingCellWidth(GridCoordinate.ORIGIN, Color.GREEN, null, 0.0d);
            new FXGridCanvasPainter(aboveMaxCanvas, createStructure(CellShape.HEXAGON), maxHexagonEdgeLength + 1.0d)
                    .drawTriangleMatchingCellWidth(GridCoordinate.ORIGIN, Color.GREEN, null, 0.0d);

            assertAll(
                    () -> assertTrue(countPaintedPixels(atMaxCanvas) > 0),
                    () -> assertEquals(0, countPaintedPixels(aboveMaxCanvas))
            );
        });
    }

    @Test
    void testDrawHexagon() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawHexagon(new GridCoordinate(4, 0), EDGE_LENGTH / 2.0d, Color.BLUE, null, 0.0d);

            assertTrue(snapshotColorAt(canvas, 45, 5).getOpacity() > 0.0d);
        });
    }

    @Test
    void testDrawHexagonMatchingCellWidth() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawHexagonMatchingCellWidth(new GridCoordinate(6, 0), Color.YELLOW, null, 0.0d);

            assertTrue(snapshotColorAt(canvas, 65, 5).getOpacity() > 0.0d);
        });
    }

    @Test
    void testDrawHexagonMatchingCellWidthSkipsEdgeLengthBelowMin() {
        FXTestSupport.runAndWait(() -> {
            double minSquareEdgeLength = GridGeometry.MIN_EDGE_LENGTH * 2.0d;
            Canvas atMinCanvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            Canvas belowMinCanvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);

            new FXGridCanvasPainter(atMinCanvas, createStructure(), minSquareEdgeLength)
                    .drawHexagonMatchingCellWidth(new GridCoordinate(1, 1), Color.YELLOW, null, 0.0d);
            new FXGridCanvasPainter(belowMinCanvas, createStructure(), GridGeometry.MIN_EDGE_LENGTH)
                    .drawHexagonMatchingCellWidth(new GridCoordinate(1, 1), Color.YELLOW, null, 0.0d);

            assertAll(
                    () -> assertTrue(countPaintedPixels(atMinCanvas) > 0),
                    () -> assertEquals(0, countPaintedPixels(belowMinCanvas))
            );
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testDrawRectangle() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawRectangle(new Rectangle2D(1, 1, 10, 10), Color.RED, null, 0.0d, StrokeType.CENTERED);

            assertColorEquals(Color.RED, snapshotColorAt(canvas, 5, 5));
        });
    }

    @Test
    void testDrawRectangleAppliesStrokeType() {
        FXTestSupport.runAndWait(() -> {
            Rectangle2D rectangle = new Rectangle2D(20, 20, 20, 20);
            Canvas inside = createStrokedRectangleCanvas(rectangle, StrokeType.INSIDE);
            Canvas centered = createStrokedRectangleCanvas(rectangle, StrokeType.CENTERED);
            Canvas outside = createStrokedRectangleCanvas(rectangle, StrokeType.OUTSIDE);

            // The stroke of width 4 covers x in [20, 24] (inside), [18, 22] (centered), or [16, 20] (outside).
            assertAll(
                    () -> assertTrue(isPaintedAt(inside, 21, 30)),
                    () -> assertFalse(isPaintedAt(inside, 18, 30)),
                    () -> assertTrue(isPaintedAt(centered, 18, 30)),
                    () -> assertTrue(isPaintedAt(centered, 21, 30)),
                    () -> assertFalse(isPaintedAt(centered, 16, 30)),
                    () -> assertFalse(isPaintedAt(centered, 23, 30)),
                    () -> assertTrue(isPaintedAt(outside, 17, 30)),
                    () -> assertFalse(isPaintedAt(outside, 22, 30))
            );
        });
    }

    @Test
    void testDrawRectangleSkipsInsideStrokeAsWideAsShorterSide() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = createStrokedRectangleCanvas(new Rectangle2D(20, 20, 4, 20), StrokeType.INSIDE);

            assertEquals(0, countPaintedPixels(canvas));
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testDrawPolygon() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPolygon(new double[]{40, 50, 40}, new double[]{40, 40, 50}, Color.BLUE, null, 0.0d);

            assertColorEquals(Color.BLUE, snapshotColorAt(canvas, 42, 42));
        });
    }

    @Test
    void testDrawPolygonIgnoresInvalidPointArrays() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPolygon(new double[0], new double[0], Color.BLUE, Color.BLUE, 1.0d);
            painter.drawPolygon(new double[]{40, 50, 40}, new double[]{40, 40}, Color.BLUE, Color.BLUE, 1.0d);

            assertEquals(0, countPaintedPixels(canvas));
        });
    }

    @Test
    void testDrawPolyline() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPolyline(new double[]{60, 70}, new double[]{60, 60}, Color.BLACK, 1.0d);

            assertTrue(snapshotColorAt(canvas, 65, 60).getOpacity() > 0.0d);
        });
    }

    @Test
    void testDrawPolylineIgnoresInvalidPointArrays() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPolyline(new double[0], new double[0], Color.BLACK, 1.0d);
            painter.drawPolyline(new double[]{60, 70, 80}, new double[]{60, 60}, Color.BLACK, 1.0d);

            assertEquals(0, countPaintedPixels(canvas));
        });
    }

    @Test
    void testDrawPixelDirect() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPixelDirect(1, 1, Color.GREEN);
            painter.drawPixelDirect(2, 2, null);

            assertAll(
                    () -> assertColorEquals(Color.GREEN, snapshotColorAt(canvas, 1, 1)),
                    () -> assertEquals(0.0d, snapshotColorAt(canvas, 2, 2).getOpacity(), 0.000_001d)
            );
        });
    }

    @Test
    void testDrawPixelDirectIgnoresPixelsOutsideCanvas() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);
            int last = CANVAS_SIZE - 1;

            painter.drawPixelDirect(last, last, Color.BLUE);
            painter.drawPixelDirect(-1, 1, Color.RED);
            painter.drawPixelDirect(1, -1, Color.RED);
            painter.drawPixelDirect(last + 1, 1, Color.RED);
            painter.drawPixelDirect(1, last + 1, Color.RED);

            assertAll(
                    () -> assertEquals(Color.BLUE, snapshotColorAt(canvas, last, last)),
                    () -> assertEquals(1, countPaintedPixels(canvas))
            );
        });
    }

    @Test
    void testDrawPixelRect() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPixelRect(3, 3, Color.BLUE);
            painter.drawPixelRect(4, 4, null);

            assertAll(
                    () -> assertColorEquals(Color.BLUE, snapshotColorAt(canvas, 3, 3)),
                    () -> assertEquals(0.0d, snapshotColorAt(canvas, 4, 4).getOpacity(), 0.000_001d)
            );
        });
    }

    @Test
    void testDrawCenteredTextInCell() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCenteredTextInCell(new GridCoordinate(2, 2), "W", Color.BLACK, Font.font("System", 12.0d));

            assertAll(
                    () -> assertTrue(isPaintedNear(canvas, 25, 25)),
                    () -> assertFalse(isPaintedNear(canvas, 5, 5))
            );
        });
    }

    @Test
    void testDrawCenteredTextAt() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCenteredTextAt(new Point2D(60, 60), "W", Color.BLACK, Font.font("System", 12.0d));

            assertAll(
                    () -> assertTrue(isPaintedNear(canvas, 60, 60)),
                    () -> assertFalse(isPaintedNear(canvas, 5, 5))
            );
        });
    }

    @SuppressWarnings("TestMethodWithoutAssertion")
    @Test
    void testDrawCircle() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCircle(new Point2D(25, 25), 5, Color.GREEN, null, 0.0d, StrokeType.CENTERED);

            assertColorEquals(Color.GREEN, snapshotColorAt(canvas, 25, 25));
        });
    }

    @Test
    void testDrawCircleAppliesStrokeType() {
        FXTestSupport.runAndWait(() -> {
            Canvas inside = createStrokedCircleCanvas(StrokeType.INSIDE);
            Canvas centered = createStrokedCircleCanvas(StrokeType.CENTERED);
            Canvas outside = createStrokedCircleCanvas(StrokeType.OUTSIDE);

            // The stroke of width 4 covers the distances [6, 10] (inside), [8, 12] (centered), or [10, 14] (outside)
            // from the center.
            assertAll(
                    () -> assertTrue(isPaintedAt(inside, 68, 60)),
                    () -> assertFalse(isPaintedAt(inside, 71, 60)),
                    () -> assertTrue(isPaintedAt(centered, 69, 60)),
                    () -> assertFalse(isPaintedAt(centered, 66, 60)),
                    () -> assertFalse(isPaintedAt(centered, 73, 60)),
                    () -> assertTrue(isPaintedAt(outside, 72, 60)),
                    () -> assertFalse(isPaintedAt(outside, 67, 60))
            );
        });
    }

    @Test
    void testDrawCircleKeepsFillDiameterWhenInsideStrokeIsSkipped() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCircle(new Point2D(60, 60), 10.0d, Color.GREEN, Color.RED, 20.0d, StrokeType.INSIDE);

            assertTrue(isPaintedAt(canvas, 69, 60));
        });
    }

    @Test
    void testDrawCenteredTextWithBackgroundAt() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCenteredTextWithBackgroundAt(new Point2D(30, 30), "A", Color.WHITE, Font.font("System", 12.0d),
                    Color.BLACK, 4.0d);

            assertTrue(snapshotColorAt(canvas, 30, 30).getOpacity() > 0.0d);
        });
    }

    @ParameterizedTest
    @EnumSource(CellShape.class)
    void testIsCellOutsideCanvas(CellShape shape) {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(shape), EDGE_LENGTH);
            GridCoordinate coordinate = new GridCoordinate(3, 3);
            Rectangle2D bounds = GridGeometry.computeCellBounds(coordinate, painter.cellDimension(), shape);
            double left = bounds.getMinX();
            double top = bounds.getMinY();

            assertAll(
                    () -> assertFalse(painter.isCellOutsideCanvas(GridCoordinate.ORIGIN)),
                    () -> assertFalse(painter.isCellOutsideCanvas(coordinate)),
                    () -> assertFalse(isCellOutsideCanvasWithSize(painter, coordinate, left + 0.5d, top + 0.5d)),
                    () -> assertTrue(isCellOutsideCanvasWithSize(painter, coordinate, left, CANVAS_SIZE)),
                    () -> assertTrue(isCellOutsideCanvasWithSize(painter, coordinate, CANVAS_SIZE, top)),
                    () -> assertTrue(isCellOutsideCanvasWithSize(painter, coordinate, left - 0.5d, CANVAS_SIZE)),
                    () -> assertTrue(isCellOutsideCanvasWithSize(painter, coordinate, CANVAS_SIZE, top - 0.5d))
            );
        });
    }

    @Test
    void testComputeTextDimension() {
        FXTestSupport.runAndWait(() -> {
            FXGridCanvasPainter painter = createPainter(new Canvas(CANVAS_SIZE, CANVAS_SIZE));

            Dimension2D dimension = painter.computeTextDimension("A", Font.font("System", 12.0d));

            assertAll(
                    () -> assertTrue(dimension.getWidth() > 0.0d),
                    () -> assertTrue(dimension.getHeight() > 0.0d)
            );
        });
    }

    @Test
    void testComputeCenteredTextOffset() {
        FXTestSupport.runAndWait(() -> {
            FXGridCanvasPainter painter = createPainter(new Canvas(CANVAS_SIZE, CANVAS_SIZE));

            Point2D offset = painter.computeCenteredTextOffset("A", Font.font("System", 12.0d));

            assertTrue(offset.getX() < 0.0d);
        });
    }

    @Test
    void testToString() {
        FXTestSupport.runAndWait(() -> {
            FXGridCanvasPainter painter = createPainter(new Canvas(CANVAS_SIZE, CANVAS_SIZE));

            assertTrue(painter.toString().contains("FXGridCanvasPainter"));
        });
    }

}
