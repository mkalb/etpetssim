package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class GridGeometryTest {

    private static final double DELTA = 0.000_1d;
    private static final double EDGE_LENGTH = 10.0d;
    private static final double HALF_EDGE_LENGTH = EDGE_LENGTH / 2.0d;
    private static final double TRIANGLE_HEIGHT = Math.sqrt(3.0d) * HALF_EDGE_LENGTH;
    private static final double HEXAGON_HEIGHT = Math.sqrt(3.0d) * EDGE_LENGTH;
    private static final int GRID_WIDTH = 8;
    private static final int GRID_HEIGHT = 8;
    private static final GridSize TEST_GRID_SIZE = new GridSize(GRID_WIDTH, GRID_HEIGHT);
    private static final GridCoordinate TEST_COORDINATE = new GridCoordinate(3, 4);
    private static final GridCoordinate DOWN_TRIANGLE = new GridCoordinate(0, 0);
    private static final GridCoordinate UP_TRIANGLE = new GridCoordinate(1, 0);

    private static CellDimension createCellDimension(CellShape shape) {
        return GridGeometry.computeCellDimension(EDGE_LENGTH, shape);
    }

    private static GridStructure createStructure(CellShape shape) {
        return new GridStructure(new GridTopology(shape, GridEdgeBehavior.ABSORB_XY), TEST_GRID_SIZE);
    }

    private static Dimension2D createGridDimension(CellShape shape) {
        return GridGeometry.computeGridDimension(TEST_GRID_SIZE, createCellDimension(shape), shape);
    }

    private static double computeEdgeLengthToMatchWidth(CellShape fromCellShape, CellShape toCellShape) {
        return GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, fromCellShape, toCellShape);
    }

    private static GridCoordinate estimateGridCoordinate(Point2D point, CellShape shape) {
        return GridGeometry.estimateGridCoordinate(point, createCellDimension(shape), createGridDimension(shape),
                createStructure(shape));
    }

    private static GridCoordinate fromCanvasPosition(Point2D point, CellShape shape) {
        return GridGeometry.fromCanvasPosition(point, createCellDimension(shape), createGridDimension(shape),
                createStructure(shape));
    }

    private static double[][] computeFrameSegment(GridCoordinate coordinate, CellShape shape, CellShapeSide side) {
        return GridGeometry.computeCellFrameSegmentPolyline(coordinate, createCellDimension(shape), shape, side);
    }

    private static void assertCellDimension(CellDimension expected, CellDimension actual) {
        assertAll(
                () -> assertEquals(expected.edgeLength(), actual.edgeLength(), DELTA),
                () -> assertEquals(expected.width(), actual.width(), DELTA),
                () -> assertEquals(expected.height(), actual.height(), DELTA),
                () -> assertEquals(expected.halfEdgeLength(), actual.halfEdgeLength(), DELTA),
                () -> assertEquals(expected.halfWidth(), actual.halfWidth(), DELTA),
                () -> assertEquals(expected.halfHeight(), actual.halfHeight(), DELTA),
                () -> assertEquals(expected.innerRadius(), actual.innerRadius(), DELTA),
                () -> assertEquals(expected.outerRadius(), actual.outerRadius(), DELTA),
                () -> assertEquals(expected.columnWidth(), actual.columnWidth(), DELTA),
                () -> assertEquals(expected.rowHeight(), actual.rowHeight(), DELTA)
        );
    }

    private static void assertPoint(double expectedX, double expectedY, Point2D actual) {
        assertAll(
                () -> assertEquals(expectedX, actual.getX(), DELTA),
                () -> assertEquals(expectedY, actual.getY(), DELTA)
        );
    }

    private static void assertPoints(double[] expectedX, double[] expectedY, double[][] actual) {
        assertAll(
                () -> assertArrayEquals(expectedX, actual[0], DELTA),
                () -> assertArrayEquals(expectedY, actual[1], DELTA)
        );
    }

    @Test
    void testComputeEdgeLengthToMatchWidthForSameShape() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH, computeEdgeLengthToMatchWidth(CellShape.TRIANGLE, CellShape.TRIANGLE)),
                () -> assertEquals(EDGE_LENGTH, computeEdgeLengthToMatchWidth(CellShape.SQUARE, CellShape.SQUARE)),
                () -> assertEquals(EDGE_LENGTH, computeEdgeLengthToMatchWidth(CellShape.HEXAGON, CellShape.HEXAGON))
        );
    }

    @Test
    void testComputeEdgeLengthToMatchWidthFromHexagon() {
        assertAll(
                () -> assertEquals(20.0d, computeEdgeLengthToMatchWidth(CellShape.HEXAGON, CellShape.TRIANGLE)),
                () -> assertEquals(20.0d, computeEdgeLengthToMatchWidth(CellShape.HEXAGON, CellShape.SQUARE))
        );
    }

    @Test
    void testComputeEdgeLengthToMatchWidthToHexagon() {
        assertAll(
                () -> assertEquals(5.0d, computeEdgeLengthToMatchWidth(CellShape.TRIANGLE, CellShape.HEXAGON)),
                () -> assertEquals(5.0d, computeEdgeLengthToMatchWidth(CellShape.SQUARE, CellShape.HEXAGON))
        );
    }

    @Test
    void testComputeEdgeLengthToMatchWidthBetweenTriangleAndSquare() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH, computeEdgeLengthToMatchWidth(CellShape.TRIANGLE, CellShape.SQUARE)),
                () -> assertEquals(EDGE_LENGTH, computeEdgeLengthToMatchWidth(CellShape.SQUARE, CellShape.TRIANGLE))
        );
    }

    @Test
    void testComputeCellDimensionForTriangle() {
        CellDimension expected = new CellDimension(10.0d, 10.0d, TRIANGLE_HEIGHT, 5.0d, 5.0d, TRIANGLE_HEIGHT / 2.0d,
                TRIANGLE_HEIGHT / 3.0d, (TRIANGLE_HEIGHT * 2.0d) / 3.0d, 5.0d, TRIANGLE_HEIGHT);

        assertCellDimension(expected, createCellDimension(CellShape.TRIANGLE));
    }

    @Test
    void testComputeCellDimensionForSquare() {
        CellDimension expected = new CellDimension(10.0d, 10.0d, 10.0d, 5.0d, 5.0d, 5.0d,
                5.0d, Math.sqrt(2.0d) * 5.0d, 10.0d, 10.0d);

        assertCellDimension(expected, createCellDimension(CellShape.SQUARE));
    }

    @Test
    void testComputeCellDimensionForHexagon() {
        CellDimension expected = new CellDimension(10.0d, 20.0d, HEXAGON_HEIGHT, 5.0d, 10.0d, HEXAGON_HEIGHT / 2.0d,
                HEXAGON_HEIGHT / 2.0d, 10.0d, 15.0d, HEXAGON_HEIGHT);

        assertCellDimension(expected, createCellDimension(CellShape.HEXAGON));
    }

    @Test
    void testComputeCellDimensionAcceptsMinAndMaxEdgeLength() {
        assertAll(
                () -> assertEquals(GridGeometry.MIN_EDGE_LENGTH,
                        GridGeometry.computeCellDimension(GridGeometry.MIN_EDGE_LENGTH, CellShape.SQUARE).edgeLength()),
                () -> assertEquals(GridGeometry.MAX_EDGE_LENGTH,
                        GridGeometry.computeCellDimension(GridGeometry.MAX_EDGE_LENGTH, CellShape.SQUARE).edgeLength())
        );
    }

    @Test
    void testComputeCellDimensionRejectsEdgeLengthOutOfRange() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GridGeometry.computeCellDimension(GridGeometry.MIN_EDGE_LENGTH - 0.1d, CellShape.SQUARE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> GridGeometry.computeCellDimension(GridGeometry.MAX_EDGE_LENGTH + 0.1d, CellShape.SQUARE))
        );
    }

    @Test
    void testComputeGridDimensionForTriangle() {
        Dimension2D gridDimension = createGridDimension(CellShape.TRIANGLE);

        assertAll(
                () -> assertEquals((GRID_WIDTH * 5.0d) + 5.0d, gridDimension.getWidth(), DELTA),
                () -> assertEquals(GRID_HEIGHT * TRIANGLE_HEIGHT, gridDimension.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeGridDimensionForSquare() {
        Dimension2D gridDimension = createGridDimension(CellShape.SQUARE);

        assertAll(
                () -> assertEquals(GRID_WIDTH * EDGE_LENGTH, gridDimension.getWidth(), DELTA),
                () -> assertEquals(GRID_HEIGHT * EDGE_LENGTH, gridDimension.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeGridDimensionForHexagon() {
        Dimension2D gridDimension = createGridDimension(CellShape.HEXAGON);

        assertAll(
                () -> assertEquals((GRID_WIDTH * 15.0d) + 5.0d, gridDimension.getWidth(), DELTA),
                () -> assertEquals((GRID_HEIGHT + 0.5d) * HEXAGON_HEIGHT, gridDimension.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeGridDimensionWithEdgeLength() {
        assertEquals(createGridDimension(CellShape.HEXAGON),
                GridGeometry.computeGridDimension(TEST_GRID_SIZE, EDGE_LENGTH, CellShape.HEXAGON));
    }

    @Test
    void testToCanvasPositionForTriangle() {
        Point2D position = GridGeometry.toCanvasPosition(TEST_COORDINATE, createCellDimension(CellShape.TRIANGLE),
                CellShape.TRIANGLE);

        assertPoint(15.0d, 4.0d * TRIANGLE_HEIGHT, position);
    }

    @Test
    void testToCanvasPositionForSquare() {
        Point2D position = GridGeometry.toCanvasPosition(TEST_COORDINATE, createCellDimension(CellShape.SQUARE),
                CellShape.SQUARE);

        assertPoint(30.0d, 40.0d, position);
    }

    @Test
    void testToCanvasPositionForHexagon() {
        CellDimension cellDimension = createCellDimension(CellShape.HEXAGON);
        GridCoordinate evenXCoordinate = new GridCoordinate(2, 3);
        GridCoordinate oddXCoordinate = new GridCoordinate(3, 3);

        assertAll(
                () -> assertFalse(evenXCoordinate.hasHexagonCellYOffset()),
                () -> assertPoint(30.0d, 3.0d * HEXAGON_HEIGHT,
                        GridGeometry.toCanvasPosition(evenXCoordinate, cellDimension, CellShape.HEXAGON)),
                () -> assertTrue(oddXCoordinate.hasHexagonCellYOffset()),
                () -> assertPoint(45.0d, 3.5d * HEXAGON_HEIGHT,
                        GridGeometry.toCanvasPosition(oddXCoordinate, cellDimension, CellShape.HEXAGON))
        );
    }

    @Test
    void testEstimateGridCoordinateForSquare() {
        assertEquals(TEST_COORDINATE, estimateGridCoordinate(new Point2D(35.5d, 45.5d), CellShape.SQUARE));
    }

    @Test
    void testEstimateGridCoordinateReturnsIllegalForPointOutsideGrid() {
        Dimension2D gridDimension = createGridDimension(CellShape.SQUARE);

        assertAll(
                () -> assertTrue(estimateGridCoordinate(new Point2D(-1, 0), CellShape.SQUARE).isIllegal()),
                () -> assertTrue(estimateGridCoordinate(new Point2D(0, -1), CellShape.SQUARE).isIllegal()),
                () -> assertTrue(estimateGridCoordinate(new Point2D(gridDimension.getWidth(), 0), CellShape.SQUARE)
                        .isIllegal()),
                () -> assertTrue(estimateGridCoordinate(new Point2D(0, gridDimension.getHeight()), CellShape.SQUARE)
                        .isIllegal())
        );
    }

    @ParameterizedTest
    @EnumSource(CellShape.class)
    void testFromCanvasPositionReturnsCoordinateForPointsInsideCell(CellShape shape) {
        CellDimension cellDimension = createCellDimension(shape);
        List<Executable> checks = new ArrayList<>();
        for (GridCoordinate coordinate : createStructure(shape).coordinatesList()) {
            Point2D center = GridGeometry.computeCellCenter(coordinate, cellDimension, shape);
            double[][] polygon = GridGeometry.computeCellPolygon(coordinate, cellDimension, shape);
            checks.add(() -> assertEquals(coordinate, fromCanvasPosition(center, shape), center::toString));
            for (int i = 0; i < polygon[0].length; i++) {
                Point2D nearVertex = new Point2D(
                        center.getX() + (0.9d * (polygon[0][i] - center.getX())),
                        center.getY() + (0.9d * (polygon[1][i] - center.getY())));
                checks.add(() -> assertEquals(coordinate, fromCanvasPosition(nearVertex, shape), nearVertex::toString));
            }
        }

        assertAll(checks);
    }

    @Test
    void testFromCanvasPositionReturnsIllegalForPointOutsideGrid() {
        assertAll(
                () -> assertTrue(fromCanvasPosition(new Point2D(-10, -10), CellShape.TRIANGLE).isIllegal()),
                () -> assertTrue(fromCanvasPosition(new Point2D(-10, -10), CellShape.SQUARE).isIllegal()),
                () -> assertTrue(fromCanvasPosition(new Point2D(-10, -10), CellShape.HEXAGON).isIllegal())
        );
    }

    @Test
    void testComputeCellCenterForTriangle() {
        CellDimension cellDimension = createCellDimension(CellShape.TRIANGLE);

        assertAll(
                () -> assertPoint(5.0d, TRIANGLE_HEIGHT / 3.0d,
                        GridGeometry.computeCellCenter(DOWN_TRIANGLE, cellDimension, CellShape.TRIANGLE)),
                () -> assertPoint(10.0d, (TRIANGLE_HEIGHT * 2.0d) / 3.0d,
                        GridGeometry.computeCellCenter(UP_TRIANGLE, cellDimension, CellShape.TRIANGLE))
        );
    }

    @Test
    void testComputeCellCenterForSquare() {
        Point2D center = GridGeometry.computeCellCenter(TEST_COORDINATE, createCellDimension(CellShape.SQUARE),
                CellShape.SQUARE);

        assertPoint(35.0d, 45.0d, center);
    }

    @Test
    void testComputeCellCenterForHexagon() {
        Point2D center = GridGeometry.computeCellCenter(TEST_COORDINATE, createCellDimension(CellShape.HEXAGON),
                CellShape.HEXAGON);

        assertPoint(55.0d, 5.0d * HEXAGON_HEIGHT, center);
    }

    @Test
    void testComputeCellBounds() {
        Rectangle2D bounds = GridGeometry.computeCellBounds(TEST_COORDINATE, createCellDimension(CellShape.HEXAGON),
                CellShape.HEXAGON);

        assertEquals(new Rectangle2D(45.0d, 4.5d * HEXAGON_HEIGHT, 20.0d, HEXAGON_HEIGHT), bounds);
    }

    @Test
    void testComputeCellPolygonForTriangle() {
        CellDimension cellDimension = createCellDimension(CellShape.TRIANGLE);
        double h = TRIANGLE_HEIGHT;

        assertAll(
                () -> assertTrue(DOWN_TRIANGLE.isTriangleCellPointingDown()),
                () -> assertPoints(new double[]{0, 5, 10}, new double[]{0, h, 0},
                        GridGeometry.computeCellPolygon(DOWN_TRIANGLE, cellDimension, CellShape.TRIANGLE)),
                () -> assertFalse(UP_TRIANGLE.isTriangleCellPointingDown()),
                () -> assertPoints(new double[]{5, 10, 15}, new double[]{h, 0, h},
                        GridGeometry.computeCellPolygon(UP_TRIANGLE, cellDimension, CellShape.TRIANGLE))
        );
    }

    @Test
    void testComputeCellPolygonForSquare() {
        double[][] polygon = GridGeometry.computeCellPolygon(TEST_COORDINATE, createCellDimension(CellShape.SQUARE),
                CellShape.SQUARE);

        assertPoints(new double[]{30, 40, 40, 30}, new double[]{40, 40, 50, 50}, polygon);
    }

    @Test
    void testComputeCellPolygonForHexagon() {
        double[][] polygon = GridGeometry.computeCellPolygon(TEST_COORDINATE, createCellDimension(CellShape.HEXAGON),
                CellShape.HEXAGON);
        double y = 4.5d * HEXAGON_HEIGHT;
        double yCenter = y + (HEXAGON_HEIGHT / 2.0d);
        double yBottom = y + HEXAGON_HEIGHT;

        assertPoints(new double[]{50, 60, 65, 60, 50, 45},
                new double[]{y, y, yCenter, yBottom, yBottom, yCenter}, polygon);
    }

    @Test
    void testComputeCellFrameSegmentPolylineForTriangle() {
        double h = TRIANGLE_HEIGHT;

        assertAll(
                () -> assertPoints(new double[]{0, 10}, new double[]{0, 0},
                        computeFrameSegment(DOWN_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.TOP)),
                () -> assertPoints(new double[]{0, 5, 10}, new double[]{0, h, 0},
                        computeFrameSegment(DOWN_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.BOTTOM)),
                () -> assertPoints(new double[]{0, 5}, new double[]{0, h},
                        computeFrameSegment(DOWN_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.LEFT)),
                () -> assertPoints(new double[]{10, 5}, new double[]{0, h},
                        computeFrameSegment(DOWN_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.RIGHT)),
                () -> assertPoints(new double[]{5, 10, 15}, new double[]{h, 0, h},
                        computeFrameSegment(UP_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.TOP)),
                () -> assertPoints(new double[]{5, 15}, new double[]{h, h},
                        computeFrameSegment(UP_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.BOTTOM)),
                () -> assertPoints(new double[]{5, 10}, new double[]{h, 0},
                        computeFrameSegment(UP_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.LEFT)),
                () -> assertPoints(new double[]{10, 15}, new double[]{0, h},
                        computeFrameSegment(UP_TRIANGLE, CellShape.TRIANGLE, CellShapeSide.RIGHT))
        );
    }

    @Test
    void testComputeCellFrameSegmentPolylineForSquare() {
        assertAll(
                () -> assertPoints(new double[]{30, 40}, new double[]{40, 40},
                        computeFrameSegment(TEST_COORDINATE, CellShape.SQUARE, CellShapeSide.TOP)),
                () -> assertPoints(new double[]{30, 40}, new double[]{50, 50},
                        computeFrameSegment(TEST_COORDINATE, CellShape.SQUARE, CellShapeSide.BOTTOM)),
                () -> assertPoints(new double[]{30, 30}, new double[]{40, 50},
                        computeFrameSegment(TEST_COORDINATE, CellShape.SQUARE, CellShapeSide.LEFT)),
                () -> assertPoints(new double[]{40, 40}, new double[]{40, 50},
                        computeFrameSegment(TEST_COORDINATE, CellShape.SQUARE, CellShapeSide.RIGHT))
        );
    }

    @Test
    void testComputeCellFrameSegmentPolylineForHexagon() {
        double y = 4.5d * HEXAGON_HEIGHT;
        double yCenter = y + (HEXAGON_HEIGHT / 2.0d);
        double yBottom = y + HEXAGON_HEIGHT;

        assertAll(
                () -> assertPoints(new double[]{45, 50, 60, 65}, new double[]{yCenter, y, y, yCenter},
                        computeFrameSegment(TEST_COORDINATE, CellShape.HEXAGON, CellShapeSide.TOP)),
                () -> assertPoints(new double[]{65, 60, 50, 45}, new double[]{yCenter, yBottom, yBottom, yCenter},
                        computeFrameSegment(TEST_COORDINATE, CellShape.HEXAGON, CellShapeSide.BOTTOM)),
                () -> assertPoints(new double[]{50, 45, 50}, new double[]{y, yCenter, yBottom},
                        computeFrameSegment(TEST_COORDINATE, CellShape.HEXAGON, CellShapeSide.LEFT)),
                () -> assertPoints(new double[]{60, 65, 60}, new double[]{y, yCenter, yBottom},
                        computeFrameSegment(TEST_COORDINATE, CellShape.HEXAGON, CellShapeSide.RIGHT))
        );
    }

    @Test
    void testIsPointInTriangleReturnsTrueForPointInside() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(10, 0);
        Point2D c = new Point2D(5, 10);
        Point2D p = new Point2D(5, 5);

        assertTrue(GridGeometry.isPointInTriangle(p, a, b, c));
    }

    @Test
    void testIsPointInTriangleReturnsFalseForPointOutside() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(10, 0);
        Point2D c = new Point2D(5, 10);
        Point2D p = new Point2D(15, 5);

        assertFalse(GridGeometry.isPointInTriangle(p, a, b, c));
    }

    @Test
    void testIsPointInTriangleReturnsTrueForPointOnVertex() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(10, 0);
        Point2D c = new Point2D(5, 10);

        assertAll(
                () -> assertTrue(GridGeometry.isPointInTriangle(a, a, b, c)),
                () -> assertTrue(GridGeometry.isPointInTriangle(b, a, b, c)),
                () -> assertTrue(GridGeometry.isPointInTriangle(c, a, b, c))
        );
    }

    @Test
    void testIsPointInTriangleReturnsTrueForPointOnEdge() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(10, 0);
        Point2D c = new Point2D(5, 10);
        Point2D p = new Point2D(5, 0);

        assertTrue(GridGeometry.isPointInTriangle(p, a, b, c));
    }

    @Test
    void testIsPointInTriangleCell() {
        CellDimension cellDimension = createCellDimension(CellShape.TRIANGLE);
        Point2D downCenter = GridGeometry.computeCellCenter(DOWN_TRIANGLE, cellDimension, CellShape.TRIANGLE);
        Point2D upCenter = GridGeometry.computeCellCenter(UP_TRIANGLE, cellDimension, CellShape.TRIANGLE);

        assertAll(
                () -> assertTrue(GridGeometry.isPointInTriangleCell(downCenter, DOWN_TRIANGLE, cellDimension)),
                () -> assertFalse(GridGeometry.isPointInTriangleCell(upCenter, DOWN_TRIANGLE, cellDimension)),
                () -> assertTrue(GridGeometry.isPointInTriangleCell(upCenter, UP_TRIANGLE, cellDimension))
        );
    }

    @Test
    void testComputeTrianglePolygon() {
        Point2D topLeft = new Point2D(10, 20);
        CellDimension cellDimension = createCellDimension(CellShape.TRIANGLE);
        double yBottom = 20.0d + TRIANGLE_HEIGHT;

        assertAll(
                () -> assertPoints(new double[]{10, 15, 20}, new double[]{20, yBottom, 20},
                        GridGeometry.computeTrianglePolygon(topLeft, cellDimension, true)),
                () -> assertPoints(new double[]{10, 15, 20}, new double[]{yBottom, 20, yBottom},
                        GridGeometry.computeTrianglePolygon(topLeft, cellDimension, false))
        );
    }

    @Test
    void testComputeSquarePolygon() {
        double[][] polygon = GridGeometry.computeSquarePolygon(new Point2D(10, 20),
                createCellDimension(CellShape.SQUARE));

        assertPoints(new double[]{10, 20, 20, 10}, new double[]{20, 20, 30, 30}, polygon);
    }

    @Test
    void testComputeHexagonPolygon() {
        double[][] polygon = GridGeometry.computeHexagonPolygon(new Point2D(10, 20),
                createCellDimension(CellShape.HEXAGON));
        double yCenter = 20.0d + (HEXAGON_HEIGHT / 2.0d);
        double yBottom = 20.0d + HEXAGON_HEIGHT;

        assertPoints(new double[]{15, 25, 30, 25, 15, 10},
                new double[]{20, 20, yCenter, yBottom, yBottom, yCenter}, polygon);
    }

}
