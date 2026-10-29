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
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("MagicNumber")
final class FXGridCanvasPainterTest {

    private static final double CANVAS_SIZE = 120.0d;
    private static final double EDGE_LENGTH = 10.0d;

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    @SuppressWarnings("SameParameterValue")
    private static GridStructure createStructure(CellShape shape) {
        return new GridStructure(new GridTopology(shape, GridEdgeBehavior.ABSORB_XY), GridSize.square(GridSize.MIN_SIZE));
    }

    private static FXGridCanvasPainter createPainter(Canvas canvas) {
        return new FXGridCanvasPainter(canvas, createStructure(CellShape.SQUARE), EDGE_LENGTH);
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
    void testConstructor() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            GridStructure structure = createStructure(CellShape.SQUARE);
            FXGridCanvasPainter painter = new FXGridCanvasPainter(canvas, structure, EDGE_LENGTH);

            assertAll(
                    () -> assertSame(canvas, painter.canvas()),
                    () -> assertSame(structure, painter.gridStructure()),
                    () -> assertSame(canvas.getGraphicsContext2D(), painter.graphicsContext2D()),
                    () -> assertEquals(GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE), painter.cellDimension()),
                    () -> assertEquals(GridGeometry.computeGridDimension(structure.size(), EDGE_LENGTH, CellShape.SQUARE), painter.gridDimension2D())
            );
        });
    }

    @Test
    void testConstructorRejectsTooSmallCellEdgeLength() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            GridStructure structure = createStructure(CellShape.SQUARE);

            assertThrows(IllegalArgumentException.class,
                    () -> new FXGridCanvasPainter(canvas, structure, 0.5d));
        });
    }

    @Test
    void testToString() {
        FXTestSupport.runAndWait(() -> {
            FXGridCanvasPainter painter = createPainter(new Canvas(CANVAS_SIZE, CANVAS_SIZE));

            assertTrue(painter.toString().contains("FXGridCanvasPainter"));
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
    void testDrawPolyline() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPolyline(new double[]{60, 70}, new double[]{60, 60}, Color.BLACK, 1.0d);

            assertTrue(snapshotColorAt(canvas, 65, 60).getOpacity() > 0.0d);
        });
    }

    @Test
    void testDrawPixelDirect() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawPixelDirect(1, 1, Color.GREEN);
            painter.drawPixelDirect(-1, 1, Color.RED);
            painter.drawPixelDirect(2, 2, null);

            assertAll(
                    () -> assertColorEquals(Color.GREEN, snapshotColorAt(canvas, 1, 1)),
                    () -> assertEquals(0.0d, snapshotColorAt(canvas, 2, 2).getOpacity(), 0.000_001d)
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
    void testDrawCenteredTextWithBackgroundAt() {
        FXTestSupport.runAndWait(() -> {
            Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
            FXGridCanvasPainter painter = createPainter(canvas);

            painter.drawCenteredTextWithBackgroundAt(new Point2D(30, 30), "A", Color.WHITE, Font.font("System", 12.0d), Color.BLACK, 4.0d);

            assertTrue(snapshotColorAt(canvas, 30, 30).getOpacity() > 0.0d);
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

}
