package de.mkalb.etpetssim.ui;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import javafx.geometry.*;
import org.junit.jupiter.api.*;
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

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        FxTestSupport.ensureStarted();
    }

    // --- Constants tests ---

    @Test
    void testMathConstants() {
        assertAll(
                () -> assertEquals(Math.sqrt(2), GridGeometry.SQRT_TWO, DELTA),
                () -> assertEquals(Math.sqrt(3), GridGeometry.SQRT_THREE, DELTA),
                () -> assertEquals(0.0d, GridGeometry.ZERO, DELTA),
                () -> assertEquals(2.0d, GridGeometry.TWO, DELTA)
        );
    }

    @Test
    void testFractionConstants() {
        assertAll(
                () -> assertEquals(1.0d / 3.0d, GridGeometry.ONE_THIRD, DELTA),
                () -> assertEquals(2.0d / 3.0d, GridGeometry.TWO_THIRDS, DELTA),
                () -> assertEquals(0.5d, GridGeometry.ONE_HALF, DELTA),
                () -> assertEquals(1.5d, GridGeometry.THREE_HALVES, DELTA)
        );
    }

    @Test
    void testEdgeLengthBoundaryConstants() {
        assertAll(
                () -> assertEquals(1.0d, GridGeometry.MIN_EDGE_LENGTH, DELTA),
                () -> assertEquals(1_024.0d, GridGeometry.MAX_EDGE_LENGTH, DELTA)
        );
    }

    // --- convertEdgeLengthToMatchWidth tests ---

    @Test
    void testConvertEdgeLengthToMatchWidthForSameShape() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.TRIANGLE, CellShape.TRIANGLE), DELTA),
                () -> assertEquals(EDGE_LENGTH, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.SQUARE, CellShape.SQUARE), DELTA),
                () -> assertEquals(EDGE_LENGTH, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.HEXAGON, CellShape.HEXAGON), DELTA)
        );
    }

    @Test
    void testConvertEdgeLengthToMatchWidthFromHexagon() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH * 2, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.HEXAGON, CellShape.TRIANGLE), DELTA),
                () -> assertEquals(EDGE_LENGTH * 2, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.HEXAGON, CellShape.SQUARE), DELTA)
        );
    }

    @Test
    void testConvertEdgeLengthToMatchWidthToHexagon() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH / 2, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.TRIANGLE, CellShape.HEXAGON), DELTA),
                () -> assertEquals(EDGE_LENGTH / 2, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.SQUARE, CellShape.HEXAGON), DELTA)
        );
    }

    @Test
    void testConvertEdgeLengthToMatchWidthBetweenTriangleAndSquare() {
        assertAll(
                () -> assertEquals(EDGE_LENGTH, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.TRIANGLE, CellShape.SQUARE), DELTA),
                () -> assertEquals(EDGE_LENGTH, GridGeometry.convertEdgeLengthToMatchWidth(EDGE_LENGTH, CellShape.SQUARE, CellShape.TRIANGLE), DELTA)
        );
    }

    // --- computeCellDimension tests ---

    @Test
    void testComputeCellDimensionForTriangle() {
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
    void testComputeCellDimensionForSquare() {
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
    void testComputeCellDimensionForHexagon() {
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> GridGeometry.computeCellDimension(GridGeometry.MIN_EDGE_LENGTH - 0.1d, CellShape.SQUARE));
        assertTrue(exception.getMessage().contains("Edge length must be between"));
    }

    @Test
    void testComputeCellDimensionRejectsTooLargeEdgeLength() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> GridGeometry.computeCellDimension(GridGeometry.MAX_EDGE_LENGTH + 0.1d, CellShape.SQUARE));
        assertTrue(exception.getMessage().contains("Edge length must be between"));
    }

    @Test
    void testComputeCellDimensionAcceptsMinAndMaxEdgeLength() {
        assertAll(
                () -> assertDoesNotThrow(() -> GridGeometry.computeCellDimension(GridGeometry.MIN_EDGE_LENGTH, CellShape.SQUARE)),
                () -> assertDoesNotThrow(() -> GridGeometry.computeCellDimension(GridGeometry.MAX_EDGE_LENGTH, CellShape.SQUARE))
        );
    }

    // --- computeGridDimension tests ---

    @Test
    void testComputeGridDimensionForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D gridDimension = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(GRID_WIDTH * EDGE_LENGTH, gridDimension.getWidth(), DELTA),
                () -> assertEquals(GRID_HEIGHT * EDGE_LENGTH, gridDimension.getHeight(), DELTA)
        );
    }

    @Test
    void testComputeGridDimensionForTriangle() {
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
    void testComputeGridDimensionForHexagon() {
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
    void testComputeGridDimensionConvenienceMethodWithEdgeLength() {
        Dimension2D directResult = GridGeometry.computeGridDimension(TEST_GRID_SIZE, EDGE_LENGTH, CellShape.SQUARE);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Dimension2D expectedResult = GridGeometry.computeGridDimension(TEST_GRID_SIZE, cellDimension, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(expectedResult.getWidth(), directResult.getWidth(), DELTA),
                () -> assertEquals(expectedResult.getHeight(), directResult.getHeight(), DELTA)
        );
    }

    // --- isPointInTriangle tests ---

    @Test
    void testIsPointInTriangleForPointInsideTriangle() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(10, 0);
        Point2D c = new Point2D(5, 10);
        Point2D p = new Point2D(5, 5);

        assertTrue(GridGeometry.isPointInTriangle(p, a, b, c));
    }

    @Test
    void testIsPointInTriangleForPointOutsideTriangle() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(10, 0);
        Point2D c = new Point2D(5, 10);
        Point2D p = new Point2D(15, 5);

        assertFalse(GridGeometry.isPointInTriangle(p, a, b, c));
    }

    @Test
    void testIsPointInTriangleForPointOnVertex() {
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
    void testIsPointInTriangleForPointOnEdge() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(10, 0);
        Point2D c = new Point2D(5, 10);
        Point2D p = new Point2D(5, 0);

        assertTrue(GridGeometry.isPointInTriangle(p, a, b, c));
    }

    // --- toCanvasPosition tests ---

    @Test
    void testToCanvasPositionForSquare() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.SQUARE);
        Point2D position = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.SQUARE);

        assertAll(
                () -> assertEquals(TEST_COORDINATE.x() * EDGE_LENGTH, position.getX(), DELTA),
                () -> assertEquals(TEST_COORDINATE.y() * EDGE_LENGTH, position.getY(), DELTA)
        );
    }

    @Test
    void testToCanvasPositionForTriangle() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        Point2D position = GridGeometry.toCanvasPosition(TEST_COORDINATE, cellDimension, CellShape.TRIANGLE);

        assertAll(
                () -> assertEquals(TEST_COORDINATE.x() * cellDimension.columnWidth(), position.getX(), DELTA),
                () -> assertEquals(TEST_COORDINATE.y() * cellDimension.rowHeight(), position.getY(), DELTA)
        );
    }

    @Test
    void testToCanvasPositionForHexagonWithoutYOffset() {
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
    void testToCanvasPositionForHexagonWithYOffset() {
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

    // --- estimateGridCoordinate tests ---

    @Test
    void testEstimateGridCoordinateForSquare() {
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

    // --- fromCanvasPosition tests ---

    @Test
    void testFromCanvasPositionForSquare() {
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
    void testFromCanvasPositionRoundTripForSquare() {
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

    // --- computeCellCenter tests ---

    @Test
    void testComputeCellCenterForSquare() {
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
    void testComputeCellCenterForTrianglePointingUp() {
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
    void testComputeCellCenterForTrianglePointingDown() {
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
    void testComputeCellCenterForHexagon() {
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

    // --- computeCellBounds tests ---

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

    // --- computeCellPolygon tests ---

    @Test
    void testComputeSquarePolygon() {
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
    void testComputeTrianglePolygonForPointingDown() {
        GridCoordinate downTriangle = new GridCoordinate(0, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        double[][] polygon = GridGeometry.computeCellPolygon(downTriangle, cellDimension, CellShape.TRIANGLE);

        assertTrue(downTriangle.isTriangleCellPointingDown());
        assertEquals(3, polygon[0].length);
        assertEquals(3, polygon[1].length);
    }

    @Test
    void testComputeTrianglePolygonForPointingUp() {
        GridCoordinate upTriangle = new GridCoordinate(1, 0);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.TRIANGLE);
        double[][] polygon = GridGeometry.computeCellPolygon(upTriangle, cellDimension, CellShape.TRIANGLE);

        assertFalse(upTriangle.isTriangleCellPointingDown());
        assertEquals(3, polygon[0].length);
        assertEquals(3, polygon[1].length);
    }

    @Test
    void testComputeHexagonPolygon() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);
        double[][] polygon = GridGeometry.computeCellPolygon(TEST_COORDINATE, cellDimension, CellShape.HEXAGON);

        assertAll(
                () -> assertEquals(6, polygon[0].length),
                () -> assertEquals(6, polygon[1].length)
        );
    }

    // --- computeCellFrameSegmentPolyline tests ---

    @Test
    void testComputeSquareFrameSegmentPolylineForAllSides() {
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
    void testComputeTriangleFrameSegmentPolylineForPointingDown() {
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
    void testComputeTriangleFrameSegmentPolylineForPointingUp() {
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
    void testComputeHexagonFrameSegmentPolylineForAllSides() {
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

    // --- Helper method tests ---

    @Test
    void testComputeTrianglePolygonDirectly() {
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
    void testComputeSquarePolygonDirectly() {
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
    void testComputeHexagonPolygonDirectly() {
        Point2D topLeft = new Point2D(10, 20);
        CellDimension cellDimension = GridGeometry.computeCellDimension(EDGE_LENGTH, CellShape.HEXAGON);

        double[][] polygon = GridGeometry.computeHexagonPolygon(topLeft, cellDimension);

        assertAll(
                () -> assertEquals(6, polygon[0].length),
                () -> assertEquals(6, polygon[1].length)
        );
    }

}
