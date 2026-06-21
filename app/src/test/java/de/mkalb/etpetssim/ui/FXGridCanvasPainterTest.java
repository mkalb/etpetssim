package de.mkalb.etpetssim.ui;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.engine.*;
import javafx.geometry.*;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("MagicNumber")
final class FXGridCanvasPainterTest {

    private static final double CANVAS_SIZE = 120.0d;
    private static final double EDGE_LENGTH = 10.0d;

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
    }

    @SuppressWarnings("SameParameterValue")
    private static GridStructure createStructure(CellShape shape) {
        return new GridStructure(new GridTopology(shape, GridEdgeBehavior.ABSORB_XY), GridSize.EXTRA_SMALL_SQUARE);
    }

    private static Color snapshotColorAt(Canvas canvas, int x, int y) {
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage snapshot = canvas.snapshot(parameters, null);
        return snapshot.getPixelReader().getColor(x, y);
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
    void testConstructorInitializesAccessorsAndGridChecks() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            GridStructure structure = createStructure(CellShape.SQUARE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, structure, EDGE_LENGTH);

            assertAll(
                    () -> assertSame(canvas, painter.canvas()),
                    () -> assertSame(structure, painter.gridStructure()),
                    () -> assertSame(canvas.getGraphicsContext2D(), painter.graphicsContext2D()),
                    () -> assertEquals(GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE), painter.cellDimension()),
                    () -> assertEquals(GridGeometry.computeGridDimension(structure.size(), EDGE_LENGTH, CellShape.SQUARE), painter.gridDimension2D()),
                    () -> assertTrue(painter.isInsideGrid(GridCoordinate.ORIGIN)),
                    () -> assertFalse(painter.isOutsideGrid(GridCoordinate.ORIGIN)),
                    () -> assertFalse(painter.isInsideGrid(new GridCoordinate(structure.size().width(), structure.size().height()))),
                    () -> assertTrue(painter.isOutsideGrid(new GridCoordinate(structure.size().width(), structure.size().height()))),
                    () -> assertTrue(painter.toString().contains("FXGridCanvasPainter"))
            );
        });
    }

    @Test
    void testConstructorRejectsTooSmallCellEdgeLength() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            GridStructure structure = createStructure(CellShape.SQUARE);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new FXGridCanvasPainter(canvas, structure, 0.5d));

            assertTrue(exception.getMessage().contains("Cell edge length must be at least 1 pixel."));
        });
    }

    @Test
    void testFillAndClearCanvasBackgroundAffectSnapshotPixels() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);

            painter.fillCanvasBackground(Color.RED);
            Color filledColor = snapshotColorAt(canvas, 5, 5);
            painter.clearCanvasBackground();
            Color clearedColor = snapshotColorAt(canvas, 5, 5);

            assertAll(
                    () -> assertColorEquals(Color.RED, filledColor),
                    () -> assertEquals(0.0d, clearedColor.getOpacity(), 0.000_001d)
            );
        });
    }

    @Test
    void testFillAndClearGridBackgroundAffectOnlyGridArea() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);

            painter.fillGridBackground(Color.BLUE);
            Color insideGridColor = snapshotColorAt(canvas, 5, 5);
            Color outsideGridColor = snapshotColorAt(canvas, 95, 95);
            painter.clearGridBackground();
            Color clearedInsideGridColor = snapshotColorAt(canvas, 5, 5);

            assertAll(
                    () -> assertColorEquals(Color.BLUE, insideGridColor),
                    () -> assertEquals(0.0d, outsideGridColor.getOpacity(), 0.000_001d),
                    () -> assertEquals(0.0d, clearedInsideGridColor.getOpacity(), 0.000_001d)
            );
        });
    }

    @Test
    void testDrawPixelDirectAndDrawPixelRectRespectBoundsAndNullPaint() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);

            painter.drawPixelDirect(1, 1, Color.GREEN);
            painter.drawPixelDirect(-1, 1, Color.RED);
            painter.drawPixelDirect(2, 2, null);
            painter.drawPixelRect(3, 3, Color.BLUE);
            painter.drawPixelRect(4, 4, null);

            assertAll(
                    () -> assertColorEquals(Color.GREEN, snapshotColorAt(canvas, 1, 1)),
                    () -> assertEquals(0.0d, snapshotColorAt(canvas, 2, 2).getOpacity(), 0.000_001d),
                    () -> assertColorEquals(Color.BLUE, snapshotColorAt(canvas, 3, 3)),
                    () -> assertEquals(0.0d, snapshotColorAt(canvas, 4, 4).getOpacity(), 0.000_001d)
            );
        });
    }

    @Test
    void testDrawRectangleCirclePolygonAndPolylineCanRenderVisiblePixels() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);

            painter.drawRectangle(new Rectangle2D(1, 1, 10, 10), Color.RED, null, 0.0d, StrokeType.CENTERED);
            painter.drawCircle(new Point2D(25, 25), 5, Color.GREEN, null, 0.0d, StrokeType.CENTERED);
            painter.drawPolygon(new double[]{40, 50, 40}, new double[]{40, 40, 50}, Color.BLUE, null, 0.0d);
            painter.drawPolyline(new double[]{60, 70}, new double[]{60, 60}, Color.BLACK, 1.0d);

            assertAll(
                    () -> assertColorEquals(Color.RED, snapshotColorAt(canvas, 5, 5)),
                    () -> assertColorEquals(Color.GREEN, snapshotColorAt(canvas, 25, 25)),
                    () -> assertColorEquals(Color.BLUE, snapshotColorAt(canvas, 42, 42)),
                    () -> assertTrue(snapshotColorAt(canvas, 65, 60).getOpacity() > 0.0d)
            );
        });
    }

    @Test
    void testDrawCellAndShapeHelpersCanRenderCells() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);

            painter.drawCell(GridCoordinate.ORIGIN, Color.RED, null, 0.0d);
            painter.drawScaledCell(new GridCoordinate(1, 0), Color.GREEN, null, 0.0d, 1.0d);
            painter.drawCellFrameSegment(new GridCoordinate(2, 0), Color.BLUE, 1.0d, CellShapeSide.TOP);
            painter.drawCellBoundingBox(new GridCoordinate(3, 0), Color.YELLOW, null, 0.0d, StrokeType.CENTERED);
            painter.drawCellInnerCircle(new GridCoordinate(4, 0), Color.BLACK, null, 0.0d, StrokeType.CENTERED);
            painter.drawCellOuterCircle(new GridCoordinate(5, 0), Color.PURPLE, null, 0.0d, StrokeType.CENTERED);

            assertAll(
                    () -> assertColorEquals(Color.RED, snapshotColorAt(canvas, 5, 5)),
                    () -> assertColorEquals(Color.GREEN, snapshotColorAt(canvas, 15, 5)),
                    () -> assertTrue(snapshotColorAt(canvas, 25, 0).getOpacity() > 0.0d),
                    () -> assertColorEquals(Color.YELLOW, snapshotColorAt(canvas, 35, 5)),
                    () -> assertColorEquals(Color.BLACK, snapshotColorAt(canvas, 45, 5)),
                    () -> assertTrue(snapshotColorAt(canvas, 55, 5).getOpacity() > 0.0d)
            );
        });
    }

    @Test
    void testDrawTriangleAndHexagonHelpersCanRenderVisiblePixels() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);

            painter.drawTriangle(GridCoordinate.ORIGIN, EDGE_LENGTH, Color.RED, null, 0.0d);
            painter.drawTriangleMatchingCellWidth(new GridCoordinate(2, 0), Color.GREEN, null, 0.0d);
            painter.drawHexagon(new GridCoordinate(4, 0), EDGE_LENGTH / 2.0d, Color.BLUE, null, 0.0d);
            painter.drawHexagonMatchingCellWidth(new GridCoordinate(6, 0), Color.YELLOW, null, 0.0d);

            assertAll(
                    () -> assertTrue(snapshotColorAt(canvas, 5, 5).getOpacity() > 0.0d),
                    () -> assertTrue(snapshotColorAt(canvas, 25, 5).getOpacity() > 0.0d),
                    () -> assertTrue(snapshotColorAt(canvas, 45, 5).getOpacity() > 0.0d),
                    () -> assertTrue(snapshotColorAt(canvas, 65, 5).getOpacity() > 0.0d)
            );
        });
    }

    @Test
    void testTextMeasurementAndDrawingProduceExpectedOffsetsAndPixels() {
        FxTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);
            Font font = Font.font("System", 12.0d);

            Dimension2D dimension = painter.computeTextDimension("A", font);
            Point2D offset = painter.computeCenteredTextOffset("A", font);
            painter.drawCenteredTextWithBackgroundAt(new Point2D(30, 30), "A", Color.WHITE, font, Color.BLACK, 4.0d);

            assertAll(
                    () -> assertTrue(dimension.getWidth() > 0.0d),
                    () -> assertTrue(dimension.getHeight() > 0.0d),
                    () -> assertTrue(offset.getX() < 0.0d),
                    () -> assertTrue(snapshotColorAt(canvas, 30, 30).getOpacity() > 0.0d)
            );
        });
    }

}
