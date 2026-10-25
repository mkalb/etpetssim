package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.*;
import javafx.geometry.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("MagicNumber")
final class GridGeometryTest {

    private static final double DELTA = 0.000_1d;
    private static final double EDGE_LENGTH = 10.0d;
    private static final double HALF_EDGE_LENGTH = EDGE_LENGTH / 2.0d;
    private static final int GRID_WIDTH = 8;
    private static final int GRID_HEIGHT = 8;
    private static final GridSize TEST_GRID_SIZE = new GridSize(GRID_WIDTH, GRID_HEIGHT);
    private static final GridCoordinate TEST_COORDINATE = new GridCoordinate(3, 4);

    @Test
    void testSqrtTwo() {
        assertEquals(Math.sqrt(2), GridGeometry.SQRT_TWO, DELTA);
    }

    @Test
    void testSqrtThree() {
        assertEquals(Math.sqrt(3), GridGeometry.SQRT_THREE, DELTA);
    }

    @Test
    void testZero() {
        assertEquals(0.0d, GridGeometry.ZERO, DELTA);
    }

    @Test
    void testTwo() {
        assertEquals(2.0d, GridGeometry.TWO, DELTA);
    }

    @Test
    void testOneThird() {
        assertEquals(1.0d / 3.0d, GridGeometry.ONE_THIRD, DELTA);
    }

    @Test
    void testTwoThirds() {
        assertEquals(2.0d / 3.0d, GridGeometry.TWO_THIRDS, DELTA);
    }

    @Test
    void testOneHalf() {
        assertEquals(0.5d, GridGeometry.ONE_HALF, DELTA);
    }

    @Test
    void testThreeHalves() {
        assertEquals(1.5d, GridGeometry.THREE_HALVES, DELTA);
    }

    @Test
    void testMinEdgeLength() {
        assertEquals(1.0d, GridGeometry.MIN_EDGE_LENGTH, DELTA);
    }

    @Test
    void testMaxEdgeLength() {
        assertEquals(1_024.0d, GridGeometry.MAX_EDGE_LENGTH, DELTA);
    }

    @Test
    void testComputeEdgeLengthToMatchWidthForSameShape() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.TRIANGLE, CellShape.TRIANGLE), DELTA),
                () -> assertEquals(EDGE_LENGTH, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.SQUARE, CellShape.SQUARE), DELTA),
                () -> assertEquals(EDGE_LENGTH, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.HEXAGON, CellShape.HEXAGON), DELTA)
        );
    }

    @Test
    void testComputeEdgeLengthToMatchWidthFromHexagon() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH * 2, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.HEXAGON, CellShape.TRIANGLE), DELTA),
                () -> assertEquals(EDGE_LENGTH * 2, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.HEXAGON, CellShape.SQUARE), DELTA)
        );
    }

    @Test
    void testComputeEdgeLengthToMatchWidthToHexagon() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH / 2, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.TRIANGLE, CellShape.HEXAGON), DELTA),
                () -> assertEquals(EDGE_LENGTH / 2, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.SQUARE, CellShape.HEXAGON), DELTA)
        );
    }

    @Test
    void testComputeEdgeLengthToMatchWidthBetweenTriangleAndSquare() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.TRIANGLE, CellShape.SQUARE), DELTA),
                () -> assertEquals(EDGE_LENGTH, GridGeometry.computeEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.SQUARE, CellShape.TRIANGLE), DELTA)
        );
    }

    @Test
    void testComputeCellDimensionReturnsValuesForTriangle() {
        CellDimension dimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);

        assertAll(
                () -> assertEquals(EDGE_LENGTH, dimension.edgeLength(), DELTA),
                () -> assertEquals(EDGE_LENGTH, dimension.width(), DELTA),
                () -> assertEquals(GridGeometry.SQRT_THREE * HALF_EDGE_LENGTH, dimension.height(), DELTA),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.halfEdgeLength(), DELTA),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.halfWidth(), DELTA),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.columnWidth(), DELTA)
        );
    }

    @Test
    void testComputeCellDimensionReturnsValuesForSquare() {
        CellDimension dimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(EDGE_LENGTH, dimension.edgeLength(), DELTA),
                () -> assertEquals(EDGE_LENGTH, dimension.width(), DELTA),
                () -> assertEquals(EDGE_LENGTH, dimension.height(), DELTA),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.halfEdgeLength(), DELTA),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.halfWidth(), DELTA),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.halfHeight(), DELTA),
                () -> assertEquals(EDGE_LENGTH, dimension.columnWidth(), DELTA),
                () -> assertEquals(EDGE_LENGTH, dimension.rowHeight(), DELTA)
        );
    }

    @Test
    void testComputeCellDimensionReturnsValuesForHexagon() {
        CellDimension dimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);

        assertAll(
                () -> assertEquals(EDGE_LENGTH, dimension.edgeLength(), DELTA),
                () -> assertEquals(EDGE_LENGTH * 2, dimension.width(), DELTA),
                () -> assertEquals(GridGeometry.SQRT_THREE * EDGE_LENGTH, dimension.height(), DELTA),
                () -> assertEquals(HALF_EDGE_LENGTH, dimension.halfEdgeLength(), DELTA),
                () -> assertEquals(EDGE_LENGTH, dimension.halfWidth(), DELTA),
                () -> assertEquals(EDGE_LENGTH * 1.5d, dimension.columnWidth(), DELTA),
                () -> assertEquals(EDGE_LENGTH, dimension.outerRadius(), DELTA)
        );
    }

    @Test
    void testComputeCellDimensionRejectsTooSmallEdgeLength() {
        assertThrows(IllegalArgumentException.class,
                () -> GridGeometry.computeCellDimension(GridGeometry.MIN_EDGE_LENGTH - 0.1d, CellShape.SQUARE));
    }

    @Test
    void testComputeCellDimensionRejectsTooLargeEdgeLength() {
        assertThrows(IllegalArgumentException.class,
                () -> GridGeometry.computeCellDimension(GridGeometry.MAX_EDGE_LENGTH + 0.1d, CellShape.SQUARE));
    }

    @Test
    void testComputeCellDimensionAcceptsMinAndMaxEdgeLength() {
        assertAll(
                () -> assertDoesNotThrow(() -> GridGeometry.computeCellDimension(GridGeometry.MIN_EDGE_LENGTH, CellShape.SQUARE)),
                () -> assertDoesNotThrow(() -> GridGeometry.computeCellDimension(GridGeometry.MAX_EDGE_LENGTH, CellShape.SQUARE))
        );
    }

    @Test
    void testComputeGridDimensionReturnsSizeForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(GRID_WIDTH * EDGE_LENGTH, gridDimension.getWidth(), DELTA),
                () -> assertEquals(GRID_HEIGHT * EDGE_LENGTH, gridDimension.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeGridDimensionReturnsSizeForTriangle() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.TRIANGLE);

        double expectedWidth = (GRID_WIDTH * cellDimension.columnWidth()) + cellDimension.halfEdgeLength();
        double expectedHeight = GRID_HEIGHT * cellDimension.rowHeight();

        assertAll(
                () -> assertEquals(expectedWidth, gridDimension.getWidth(), DELTA),
                () -> assertEquals(expectedHeight, gridDimension.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeGridDimensionReturnsSizeForHexagon() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.HEXAGON);

        double expectedWidth = (GRID_WIDTH * cellDimension.columnWidth()) + cellDimension.halfEdgeLength();
        double expectedHeight = (GRID_HEIGHT * cellDimension.rowHeight()) + cellDimension.halfHeight();

        assertAll(
                () -> assertEquals(expectedWidth, gridDimension.getWidth(), DELTA),
                () -> assertEquals(expectedHeight, gridDimension.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeGridDimensionWithEdgeLengthMatchesCellDimensionVariant() {
        Dimension2D directResult = GridGeometry.computeGridDimension(TEST_GRID_SIZE, EDGE_LENGTH, CellShape.SQUARE);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D expectedResult = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(expectedResult.getWidth(), directResult.getWidth(), DELTA),
                () -> assertEquals(expectedResult.getHeight(), directResult.getHeight(), DELTA)
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
    void testToCanvasPositionReturnsPositionForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Point2D position = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(TEST_COORDINATE.x() * EDGE_LENGTH, position.getX(), DELTA),
                () -> assertEquals(TEST_COORDINATE.y() * EDGE_LENGTH, position.getY(), DELTA)
        );
    }

    @Test
    void testToCanvasPositionReturnsPositionForTriangle() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        Point2D position = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.TRIANGLE);

        assertAll(
                () -> assertEquals(TEST_COORDINATE.x() * cellDimension.columnWidth(), position.getX(), DELTA),
                () -> assertEquals(TEST_COORDINATE.y() * cellDimension.rowHeight(), position.getY(), DELTA)
        );
    }

    @Test
    void testToCanvasPositionReturnsPositionForHexagonWithoutYOffset() {
        GridCoordinate evenXCoordinate = new GridCoordinate(2, 3);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);
        Point2D position = GridGeometry.toCanvasPosition(evenXCoordinate, cellDimension, CellShape.HEXAGON);

        assertAll(
                () -> assertFalse(evenXCoordinate.hasHexagonCellYOffset()),
                () -> assertEquals(evenXCoordinate.x() * cellDimension.columnWidth(), position.getX(), DELTA),
                () -> assertEquals(evenXCoordinate.y() * cellDimension.rowHeight(), position.getY(), DELTA)
        );
    }

    @Test
    void testToCanvasPositionReturnsPositionForHexagonWithYOffset() {
        GridCoordinate oddXCoordinate = new GridCoordinate(3, 3);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);
        Point2D position = GridGeometry.toCanvasPosition(oddXCoordinate, cellDimension, CellShape.HEXAGON);

        double expectedY = (oddXCoordinate.y() * cellDimension.rowHeight()) + cellDimension.halfHeight();

        assertAll(
                () -> assertTrue(oddXCoordinate.hasHexagonCellYOffset()),
                () -> assertEquals(oddXCoordinate.x() * cellDimension.columnWidth(), position.getX(), DELTA),
                () -> assertEquals(expectedY, position.getY(), DELTA)
        );
    }

    @Test
    void testEstimateGridCoordinateReturnsCoordinateForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.ABSORB_XY);
        GridStructure structure = new GridStructure(topology, TEST_GRID_SIZE);

        Point2D point = new Point2D(35.5d, 45.5d);
        GridCoordinate estimated = GridGeometry.estimateGridCoordinate(point, cellDimension, gridDimension, structure);

        assertAll(
                () -> assertEquals(3, estimated.x()),
                () -> assertEquals(4, estimated.y())
        );
    }

    @Test
    void testEstimateGridCoordinateReturnsIllegalForOutOfBoundsPoint() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.ABSORB_XY);
        GridStructure structure = new GridStructure(topology, TEST_GRID_SIZE);

        assertAll(
                () -> assertTrue(GridGeometry.estimateGridCoordinate(new Point2D(-1, 0), cellDimension, gridDimension, structure).isIllegal()),
                () -> assertTrue(GridGeometry.estimateGridCoordinate(new Point2D(0, -1), cellDimension, gridDimension, structure).isIllegal()),
                () -> assertTrue(GridGeometry.estimateGridCoordinate(new Point2D(gridDimension.getWidth(), 0), cellDimension, gridDimension, structure).isIllegal()),
                () -> assertTrue(GridGeometry.estimateGridCoordinate(new Point2D(0, gridDimension.getHeight()), cellDimension, gridDimension, structure).isIllegal())
        );
    }

    @Test
    void testFromCanvasPositionReturnsCoordinateForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.ABSORB_XY);
        GridStructure structure = new GridStructure(topology, TEST_GRID_SIZE);

        Point2D point = new Point2D(35.5d, 45.5d);
        GridCoordinate coordinate = GridGeometry.fromCanvasPosition(point, cellDimension, gridDimension, structure);

        assertAll(
                () -> assertEquals(3, coordinate.x()),
                () -> assertEquals(4, coordinate.y())
        );
    }

    @Test
    void testFromCanvasPositionRoundTripsForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.ABSORB_XY);
        GridStructure structure = new GridStructure(topology, TEST_GRID_SIZE);

        Point2D originalPoint = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.SQUARE);
        Point2D pointInCell = new Point2D(originalPoint.getX() + 1, originalPoint.getY() + 1);
        GridCoordinate roundTrip = GridGeometry.fromCanvasPosition(pointInCell, cellDimension, gridDimension, structure);

        assertEquals(TEST_COORDINATE, roundTrip);
    }

    @Test
    void testFromCanvasPositionReturnsIllegalForOutOfBoundsPoint() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);
        GridTopology topology = new GridTopology(CellShape.SQUARE, GridEdgeBehavior.ABSORB_XY);
        GridStructure structure = new GridStructure(topology, TEST_GRID_SIZE);

        Point2D outOfBoundsPoint = new Point2D(-10, -10);
        GridCoordinate coordinate = GridGeometry.fromCanvasPosition(outOfBoundsPoint, cellDimension, gridDimension, structure);

        assertTrue(coordinate.isIllegal());
    }

    @Test
    void testComputeCellCenterReturnsCenterForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Point2D center = GridGeometry.computeCellCenter(TEST_COORDINATE, cellDimension, CellShape.SQUARE);

        Point2D topLeft = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.SQUARE);
        double expectedX = topLeft.getX() + HALF_EDGE_LENGTH;
        double expectedY = topLeft.getY() + HALF_EDGE_LENGTH;

        assertAll(
                () -> assertEquals(expectedX, center.getX(), DELTA),
                () -> assertEquals(expectedY, center.getY(), DELTA)
        );
    }

    @Test
    void testComputeCellCenterReturnsCenterForTrianglePointingUp() {
        GridCoordinate upTriangle = new GridCoordinate(1, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        Point2D center = GridGeometry.computeCellCenter(upTriangle, cellDimension, CellShape.TRIANGLE);

        Point2D topLeft = GridGeometry.toCanvasPosition(upTriangle, cellDimension, CellShape.TRIANGLE);
        double expectedX = topLeft.getX() + cellDimension.halfWidth();
        double expectedY = topLeft.getY() + (cellDimension.height() * GridGeometry.TWO_THIRDS);

        assertAll(
                () -> assertFalse(upTriangle.isTriangleCellPointingDown()),
                () -> assertEquals(expectedX, center.getX(), DELTA),
                () -> assertEquals(expectedY, center.getY(), DELTA)
        );
    }

    @Test
    void testComputeCellCenterReturnsCenterForTrianglePointingDown() {
        GridCoordinate downTriangle = new GridCoordinate(0, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        Point2D center = GridGeometry.computeCellCenter(downTriangle, cellDimension, CellShape.TRIANGLE);

        Point2D topLeft = GridGeometry.toCanvasPosition(downTriangle, cellDimension, CellShape.TRIANGLE);
        double expectedX = topLeft.getX() + cellDimension.halfWidth();
        double expectedY = topLeft.getY() + (cellDimension.height() * GridGeometry.ONE_THIRD);

        assertAll(
                () -> assertTrue(downTriangle.isTriangleCellPointingDown()),
                () -> assertEquals(expectedX, center.getX(), DELTA),
                () -> assertEquals(expectedY, center.getY(), DELTA)
        );
    }

    @Test
    void testComputeCellCenterReturnsCenterForHexagon() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);
        Point2D center = GridGeometry.computeCellCenter(TEST_COORDINATE, cellDimension, CellShape.HEXAGON);

        Point2D topLeft = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.HEXAGON);
        double expectedX = topLeft.getX() + cellDimension.halfWidth();
        double expectedY = topLeft.getY() + cellDimension.halfHeight();

        assertAll(
                () -> assertEquals(expectedX, center.getX(), DELTA),
                () -> assertEquals(expectedY, center.getY(), DELTA)
        );
    }

    @Test
    void testComputeCellBounds() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Rectangle2D bounds = GridGeometry.computeCellBounds(TEST_COORDINATE, cellDimension, CellShape.SQUARE);

        Point2D topLeft = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(topLeft.getX(), bounds.getMinX(), DELTA),
                () -> assertEquals(topLeft.getY(), bounds.getMinY(), DELTA),
                () -> assertEquals(cellDimension.width(), bounds.getWidth(), DELTA),
                () -> assertEquals(cellDimension.height(), bounds.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeCellPolygonReturnsSquarePolygon() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        double[][] polygon = GridGeometry.computeCellPolygon(TEST_COORDINATE, cellDimension, CellShape.SQUARE);

        Point2D topLeft = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.SQUARE);
        double x = topLeft.getX();
        double y = topLeft.getY();

        assertAll(
                () -> assertEquals(4, polygon[0].length),
                () -> assertEquals(4, polygon[1].length),
                () -> assertEquals(x, polygon[0][0], DELTA),
                () -> assertEquals(y, polygon[1][0], DELTA),
                () -> assertEquals(x + EDGE_LENGTH, polygon[0][1], DELTA),
                () -> assertEquals(y, polygon[1][1], DELTA),
                () -> assertEquals(x + EDGE_LENGTH, polygon[0][2], DELTA),
                () -> assertEquals(y + EDGE_LENGTH, polygon[1][2], DELTA),
                () -> assertEquals(x, polygon[0][3], DELTA),
                () -> assertEquals(y + EDGE_LENGTH, polygon[1][3], DELTA)
        );
    }

    @Test
    void testComputeCellPolygonReturnsTrianglePolygonPointingDown() {
        GridCoordinate downTriangle = new GridCoordinate(0, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        double[][] polygon = GridGeometry.computeCellPolygon(downTriangle, cellDimension, CellShape.TRIANGLE);

        assertTrue(downTriangle.isTriangleCellPointingDown());
        assertEquals(3, polygon[0].length);
        assertEquals(3, polygon[1].length);
    }

    @Test
    void testComputeCellPolygonReturnsTrianglePolygonPointingUp() {
        GridCoordinate upTriangle = new GridCoordinate(1, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        double[][] polygon = GridGeometry.computeCellPolygon(upTriangle, cellDimension, CellShape.TRIANGLE);

        assertFalse(upTriangle.isTriangleCellPointingDown());
        assertEquals(3, polygon[0].length);
        assertEquals(3, polygon[1].length);
    }

    @Test
    void testComputeCellPolygonReturnsHexagonPolygon() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);
        double[][] polygon = GridGeometry.computeCellPolygon(TEST_COORDINATE, cellDimension, CellShape.HEXAGON);

        assertAll(
                () -> assertEquals(6, polygon[0].length),
                () -> assertEquals(6, polygon[1].length)
        );
    }

    @Test
    void testComputeCellFrameSegmentPolylineReturnsSidesForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);

        assertAll(
                () -> {
                    double[][] topSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.SQUARE, CellShapeSide.TOP);
                    assertEquals(2, topSegment[0].length);
                    assertEquals(2, topSegment[1].length);
                },
                () -> {
                    double[][] bottomSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.SQUARE, CellShapeSide.BOTTOM);
                    assertEquals(2, bottomSegment[0].length);
                    assertEquals(2, bottomSegment[1].length);
                },
                () -> {
                    double[][] leftSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.SQUARE, CellShapeSide.LEFT);
                    assertEquals(2, leftSegment[0].length);
                    assertEquals(2, leftSegment[1].length);
                },
                () -> {
                    double[][] rightSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.SQUARE, CellShapeSide.RIGHT);
                    assertEquals(2, rightSegment[0].length);
                    assertEquals(2, rightSegment[1].length);
                }
        );
    }

    @Test
    void testComputeCellFrameSegmentPolylineReturnsSidesForTriangleDown() {
        GridCoordinate downTriangle = new GridCoordinate(0, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);

        assertAll(
                () -> assertTrue(downTriangle.isTriangleCellPointingDown()),
                () -> {
                    double[][] topSegment = GridGeometry.computeCellFrameSegmentPolyline(downTriangle, cellDimension, CellShape.TRIANGLE, CellShapeSide.TOP);
                    assertEquals(2, topSegment[0].length);
                },
                () -> {
                    double[][] bottomSegment = GridGeometry.computeCellFrameSegmentPolyline(downTriangle, cellDimension, CellShape.TRIANGLE, CellShapeSide.BOTTOM);
                    assertEquals(3, bottomSegment[0].length);
                },
                () -> {
                    double[][] leftSegment = GridGeometry.computeCellFrameSegmentPolyline(downTriangle, cellDimension, CellShape.TRIANGLE, CellShapeSide.LEFT);
                    assertEquals(2, leftSegment[0].length);
                },
                () -> {
                    double[][] rightSegment = GridGeometry.computeCellFrameSegmentPolyline(downTriangle, cellDimension, CellShape.TRIANGLE, CellShapeSide.RIGHT);
                    assertEquals(2, rightSegment[0].length);
                }
        );
    }

    @Test
    void testComputeCellFrameSegmentPolylineReturnsSidesForTriangleUp() {
        GridCoordinate upTriangle = new GridCoordinate(1, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);

        assertAll(
                () -> assertFalse(upTriangle.isTriangleCellPointingDown()),
                () -> {
                    double[][] topSegment = GridGeometry.computeCellFrameSegmentPolyline(upTriangle, cellDimension, CellShape.TRIANGLE, CellShapeSide.TOP);
                    assertEquals(3, topSegment[0].length);
                },
                () -> {
                    double[][] bottomSegment = GridGeometry.computeCellFrameSegmentPolyline(upTriangle, cellDimension, CellShape.TRIANGLE, CellShapeSide.BOTTOM);
                    assertEquals(2, bottomSegment[0].length);
                }
        );
    }

    @Test
    void testComputeCellFrameSegmentPolylineReturnsSidesForHexagon() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);

        assertAll(
                () -> {
                    double[][] topSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.HEXAGON, CellShapeSide.TOP);
                    assertEquals(4, topSegment[0].length);
                    assertEquals(4, topSegment[1].length);
                },
                () -> {
                    double[][] bottomSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.HEXAGON, CellShapeSide.BOTTOM);
                    assertEquals(4, bottomSegment[0].length);
                    assertEquals(4, bottomSegment[1].length);
                },
                () -> {
                    double[][] leftSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.HEXAGON, CellShapeSide.LEFT);
                    assertEquals(3, leftSegment[0].length);
                    assertEquals(3, leftSegment[1].length);
                },
                () -> {
                    double[][] rightSegment = GridGeometry.computeCellFrameSegmentPolyline(TEST_COORDINATE, cellDimension, CellShape.HEXAGON, CellShapeSide.RIGHT);
                    assertEquals(3, rightSegment[0].length);
                    assertEquals(3, rightSegment[1].length);
                }
        );
    }

    @Test
    void testComputeTrianglePolygon() {
        Point2D topLeft = new Point2D(10, 20);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);

        double[][] downPolygon = GridGeometry.computeTrianglePolygon(topLeft, cellDimension, true);
        double[][] upPolygon = GridGeometry.computeTrianglePolygon(topLeft, cellDimension, false);

        assertAll(
                () -> assertEquals(3, downPolygon[0].length),
                () -> assertEquals(3, downPolygon[1].length),
                () -> assertEquals(3, upPolygon[0].length),
                () -> assertEquals(3, upPolygon[1].length)
        );
    }

    @Test
    void testComputeSquarePolygon() {
        Point2D topLeft = new Point2D(10, 20);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);

        double[][] polygon = GridGeometry.computeSquarePolygon(topLeft, cellDimension);

        assertAll(
                () -> assertEquals(4, polygon[0].length),
                () -> assertEquals(4, polygon[1].length),
                () -> assertEquals(topLeft.getX(), polygon[0][0], DELTA),
                () -> assertEquals(topLeft.getY(), polygon[1][0], DELTA)
        );
    }

    @Test
    void testComputeHexagonPolygon() {
        Point2D topLeft = new Point2D(10, 20);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);

        double[][] polygon = GridGeometry.computeHexagonPolygon(topLeft, cellDimension);

        assertAll(
                () -> assertEquals(6, polygon[0].length),
                () -> assertEquals(6, polygon[1].length)
        );
    }

}
