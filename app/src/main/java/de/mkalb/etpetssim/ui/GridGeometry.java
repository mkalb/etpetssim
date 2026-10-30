package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.List;

/**
 * Computes cell and grid dimensions, canvas positions, polygons, and frame segments of grid cells, and converts
 * canvas positions to grid coordinates.
 *
 * <p>All coordinates are canvas pixels; polygons and polylines are returned as a 2D array with the x points at
 * index {@code 0} and the y points at index {@code 1}.
 */
public final class GridGeometry {

    /**
     * Minimum edge length of a cell in pixels.
     */
    public static final double MIN_EDGE_LENGTH = 1.0d;
    /**
     * Maximum edge length of a cell in pixels.
     */
    public static final double MAX_EDGE_LENGTH = 1_024.0d;

    private static final double SQRT_TWO = Math.sqrt(2);
    private static final double SQRT_THREE = Math.sqrt(3);
    private static final double ZERO = 0.0d;
    private static final double ONE_THIRD = 1.0d / 3.0d;
    private static final double TWO_THIRDS = 2.0d / 3.0d;
    private static final double ONE_HALF = 1.0d / 2.0d;
    private static final double THREE_HALVES = 3.0d / 2.0d;

    /**
     * Offset candidates for TRIANGLE cells, starting with the most likely center (0,0).
     */
    private static final List<GridOffset> TRIANGLE_NEIGHBOR_OFFSETS = List.of(
            new GridOffset(0, 0),
            new GridOffset(1, 0),
            new GridOffset(-1, 0)
    );
    /**
     * Offset candidates for HEXAGON cells, starting with the most likely center (0,0).
     */
    private static final List<GridOffset> HEXAGON_NEIGHBOR_OFFSETS = List.of(
            new GridOffset(0, 0),
            new GridOffset(-1, 0),
            new GridOffset(0, -1),
            new GridOffset(-1, -1),
            new GridOffset(-1, 1)
    );

    private GridGeometry() {
    }

    /**
     * Converts the edge length of a cell to match the width of another cell shape.
     * Ensures that the resulting cell shape has the same width as the original cell shape.
     *
     * @param fromEdgeLength the edge length of the original cell shape
     * @param fromCellShape  the shape of the original cell
     * @param toCellShape    the shape of the target cell
     * @return the edge length required for the target cell shape to match the width of the original cell shape
     */
    public static double computeEdgeLengthToMatchWidth(double fromEdgeLength, CellShape fromCellShape,
                                                       CellShape toCellShape) {
        if (fromCellShape == toCellShape) {
            return fromEdgeLength;
        }

        if (fromCellShape == CellShape.HEXAGON) {
            return fromEdgeLength * 2;
        }

        if (toCellShape == CellShape.HEXAGON) {
            return fromEdgeLength / 2;
        }

        return fromEdgeLength;
    }

    /**
     * Computes the dimensions of a cell based on its edge length and shape.
     *
     * @param edgeLength the length of each edge of the cell in pixels, must be between {@link #MIN_EDGE_LENGTH}
     *                   and  {@link #MAX_EDGE_LENGTH}
     * @param cellShape  the shape of the cell
     * @return a CellDimension object representing the dimensions of the cell
     * @throws IllegalArgumentException if {@code edgeLength} is outside the inclusive range
     *                                  [{@link #MIN_EDGE_LENGTH}, {@link #MAX_EDGE_LENGTH}]
     */
    public static CellDimension computeCellDimension(double edgeLength, CellShape cellShape) {
        if ((edgeLength < MIN_EDGE_LENGTH) || (edgeLength > MAX_EDGE_LENGTH)) {
            throw new IllegalArgumentException("edgeLength must be in [" + MIN_EDGE_LENGTH + ", " + MAX_EDGE_LENGTH
                    + "], but was: " + edgeLength);
        }

        double halfEdgeLength = edgeLength * ONE_HALF;
        double width = switch (cellShape) {
            case TRIANGLE, SQUARE -> edgeLength;
            case HEXAGON -> edgeLength + edgeLength;
        };
        double halfWidth = switch (cellShape) {
            case TRIANGLE, SQUARE -> halfEdgeLength;
            case HEXAGON -> edgeLength;
        };
        double height = switch (cellShape) {
            case TRIANGLE -> SQRT_THREE * halfEdgeLength;
            case SQUARE -> edgeLength;
            case HEXAGON -> SQRT_THREE * edgeLength;
        };
        double halfHeight = switch (cellShape) {
            case TRIANGLE, HEXAGON -> height * ONE_HALF;
            case SQUARE -> halfEdgeLength;
        };
        double innerRadius = switch (cellShape) {
            case TRIANGLE -> height * ONE_THIRD;
            case SQUARE -> halfEdgeLength;
            case HEXAGON -> halfHeight;
        };
        double outerRadius = switch (cellShape) {
            case TRIANGLE -> Math.hypot(halfEdgeLength, innerRadius);
            case SQUARE -> SQRT_TWO * halfEdgeLength;
            case HEXAGON -> edgeLength;
        };
        double columnWidth = switch (cellShape) {
            case TRIANGLE -> halfEdgeLength;
            case SQUARE -> edgeLength;
            case HEXAGON -> edgeLength * THREE_HALVES;
        };
        return new CellDimension(edgeLength, width, height,
                halfEdgeLength, halfWidth, halfHeight,
                innerRadius, outerRadius,
                columnWidth, height);
    }

    /**
     * Computes the total pixel dimensions of the grid area based on size, cell dimension and shape.
     *
     * @param gridSize      the size of the grid in terms of columns and rows
     * @param cellDimension the dimensions of a single cell in the grid
     * @param cellShape     the shape of the cells in the grid
     * @return a Dimension2D object representing the total width and height of the grid area
     */
    public static Dimension2D computeGridDimension(GridSize gridSize, CellDimension cellDimension,
                                                   CellShape cellShape) {
        double columnsWidth = gridSize.width() * cellDimension.columnWidth();
        double rowsHeight = gridSize.height() * cellDimension.rowHeight();
        return switch (cellShape) {
            case TRIANGLE -> new Dimension2D(columnsWidth + cellDimension.halfEdgeLength(), rowsHeight);
            case SQUARE -> new Dimension2D(columnsWidth, rowsHeight);
            case HEXAGON -> new Dimension2D(columnsWidth + cellDimension.halfEdgeLength(),
                    rowsHeight + ((gridSize.width() > 1) ? cellDimension.halfHeight() : ZERO));
        };
    }

    /**
     * Computes the total pixel dimensions of the grid area based on the grid size,
     * cell edge length, and cell shape.
     *
     * <p>This method is a convenience wrapper that combines the computation of
     * cell dimensions and grid dimensions into a single call.
     *
     * @param gridSize   the size of the grid in terms of columns and rows
     * @param edgeLength the length of each edge of the cell in pixels, must be between {@link #MIN_EDGE_LENGTH}
     *                   and  {@link #MAX_EDGE_LENGTH}
     * @param cellShape  the shape of the cells in the grid
     * @return a Dimension2D object representing the total width and height of the grid area
     * @see #computeCellDimension(double, CellShape)
     * @see #computeGridDimension(GridSize, CellDimension, CellShape)
     */
    public static Dimension2D computeGridDimension(GridSize gridSize, double edgeLength, CellShape cellShape) {
        return computeGridDimension(gridSize, computeCellDimension(edgeLength, cellShape), cellShape);
    }

    /**
     * Converts a grid coordinate to its corresponding canvas position in pixel coordinates.
     *
     * @param coordinate    the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param cellShape     the shape of the cell
     * @return the canvas position of the cell in pixel coordinates
     */
    public static Point2D toCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension,
                                           CellShape cellShape) {
        double x = coordinate.x() * cellDimension.columnWidth();
        double yOffset = ((cellShape == CellShape.HEXAGON) && coordinate.hasHexagonCellYOffset())
                ? cellDimension.halfHeight()
                : ZERO;
        double y = (coordinate.y() * cellDimension.rowHeight()) + yOffset;
        return new Point2D(x, y);
    }

    /**
     * Estimates the grid coordinate corresponding to a given canvas position in pixel coordinates.
     *
     * <p>This method performs a fast, direct mathematical estimation of the grid coordinate for the specified
     * canvas position, based on the cell dimensions and grid structure. For {@code SQUARE} cells, the conversion
     * is exact. For {@code TRIANGLE} and {@code HEXAGON} cells, the result is an initial estimate that may not
     * always match the actual cell containing the point, due to staggered or interlocking layouts. For precise
     * mapping, use {@link #fromCanvasPosition(Point2D, CellDimension, Dimension2D, GridStructure)}.
     *
     * @param point         the canvas position in pixel coordinates
     * @param cellDimension the dimensions of the cell
     * @param gridDimension the total dimensions of the grid area in pixels; used to check if the point is within bounds
     * @param structure     the grid structure defining the cell shape and size
     * @return the estimated grid coordinate, or {@code GridCoordinate.ILLEGAL} if the point is outside the grid area
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static GridCoordinate estimateGridCoordinate(Point2D point, CellDimension cellDimension,
                                                        Dimension2D gridDimension, GridStructure structure) {
        if ((point.getX() < 0)
                || (point.getY() < 0)
                || (point.getX() >= gridDimension.getWidth())
                || (point.getY() >= gridDimension.getHeight())) {
            return GridCoordinate.ILLEGAL;
        }

        return switch (structure.cellShape()) {
            case TRIANGLE -> {
                double xOffset = cellDimension.columnWidth() / 2;
                int estimatedGridX = (int) ((point.getX() - xOffset) / cellDimension.columnWidth());
                int y = (int) (point.getY() / cellDimension.rowHeight());
                yield new GridCoordinate(estimatedGridX, y);
            }
            case SQUARE -> {
                int x = (int) (point.getX() / cellDimension.columnWidth());
                int y = (int) (point.getY() / cellDimension.rowHeight());
                yield new GridCoordinate(x, y);
            }
            case HEXAGON -> {
                int estimatedGridX = (int) (point.getX() / cellDimension.columnWidth());
                // Same rule as GridCoordinate.hasHexagonCellYOffset().
                boolean hasHexagonCellYOffset = (estimatedGridX % 2) != 0;
                double yOffset = hasHexagonCellYOffset ? ONE_HALF : ZERO;
                int estimatedGridY = (int) ((point.getY() / cellDimension.rowHeight()) - yOffset);
                yield new GridCoordinate(estimatedGridX, estimatedGridY);
            }
        };
    }

    /**
     * Converts a canvas position in pixel coordinates to the corresponding grid coordinate.
     *
     * <p>For {@code SQUARE} cells, this method performs a direct mathematical conversion.
     * For {@code TRIANGLE} and {@code HEXAGON} cells, the conversion is not analytically invertible
     * due to the staggered or interlocking layout. Instead, the method estimates a base coordinate
     * and evaluates a set of nearby candidate cells to determine which one contains the given point.
     * For triangles, barycentric coordinate checks are used; for hexagons, the closest in-bounds cell
     * center within a bounding box is selected.
     *
     * @param point         the canvas position in pixel coordinates
     * @param cellDimension the dimensions of the cell
     * @param gridDimension the total dimensions of the grid area in pixels; used to check if the point is within bounds
     * @param structure     the grid structure defining the cell shape and size
     * @return the grid coordinate corresponding to the canvas position, or {@code GridCoordinate.ILLEGAL} if no match
     * is found
     */
    public static GridCoordinate fromCanvasPosition(Point2D point, CellDimension cellDimension,
                                                    Dimension2D gridDimension, GridStructure structure) {
        GridCoordinate estimatedCoordinate = estimateGridCoordinate(point, cellDimension, gridDimension, structure);
        if (estimatedCoordinate.isIllegal()) {
            return estimatedCoordinate;
        }

        return switch (structure.cellShape()) {
            case TRIANGLE -> TRIANGLE_NEIGHBOR_OFFSETS.stream()
                    .map(estimatedCoordinate::offset)
                    .filter(structure::isCoordinateValid)
                    .filter(c -> isPointInTriangleCell(point, c, cellDimension))
                    .findFirst()
                    .orElse(GridCoordinate.ILLEGAL);
            case SQUARE -> estimatedCoordinate;
            case HEXAGON -> {
                GridCoordinate closestHexCell = GridCoordinate.ILLEGAL;
                double minDistance = Double.MAX_VALUE;
                double earlyAcceptThreshold = cellDimension.halfEdgeLength();
                for (GridOffset offset : HEXAGON_NEIGHBOR_OFFSETS) {
                    GridCoordinate candidate = estimatedCoordinate.offset(offset);

                    Point2D topLeft = toCanvasPosition(candidate, cellDimension, structure.cellShape());

                    if (!cellDimension.boundingBoxAt(topLeft).contains(point)) {
                        continue;
                    }

                    Point2D center = new Point2D(
                            topLeft.getX() + cellDimension.halfWidth(),
                            topLeft.getY() + cellDimension.halfHeight()
                    );
                    double distance = center.distance(point);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestHexCell = candidate;
                        if (distance < earlyAcceptThreshold) {
                            break;
                        }
                    }
                }

                yield structure.isCoordinateValid(closestHexCell) ? closestHexCell : GridCoordinate.ILLEGAL;
            }
        };
    }

    /**
     * Computes the center point of a cell on the canvas based on its coordinate, shape, and dimensions.
     *
     * @param coordinate    the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param cellShape     the shape of the cell
     * @return the center point of the cell in canvas coordinates
     */
    public static Point2D computeCellCenter(GridCoordinate coordinate, CellDimension cellDimension,
                                            CellShape cellShape) {
        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, cellShape);
        double xOffset = cellDimension.halfWidth();
        double yOffset = switch (cellShape) {
            case TRIANGLE -> coordinate.isTriangleCellPointingDown()
                    ? (cellDimension.height() * ONE_THIRD)
                    : (cellDimension.height() * TWO_THIRDS);
            case SQUARE, HEXAGON -> cellDimension.halfHeight();
        };
        return new Point2D(topLeft.getX() + xOffset, topLeft.getY() + yOffset);
    }

    /**
     * Computes the bounding rectangle of a cell in canvas coordinates.
     *
     * @param coordinate    the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param cellShape     the shape of the cell
     * @return the bounding rectangle of the cell
     */
    public static Rectangle2D computeCellBounds(GridCoordinate coordinate, CellDimension cellDimension,
                                                CellShape cellShape) {
        return cellDimension.boundingBoxAt(toCanvasPosition(coordinate, cellDimension, cellShape));
    }

    /**
     * Computes the x and y coordinates of the cell's polygon vertices in canvas space.
     *
     * @param coordinate    the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param cellShape     the shape of the cell
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    public static double[][] computeCellPolygon(GridCoordinate coordinate, CellDimension cellDimension,
                                                CellShape cellShape) {
        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, cellShape);

        return switch (cellShape) {
            case TRIANGLE -> computeTrianglePolygon(topLeft, cellDimension, coordinate.isTriangleCellPointingDown());
            case SQUARE -> computeSquarePolygon(topLeft, cellDimension);
            case HEXAGON -> computeHexagonPolygon(topLeft, cellDimension);
        };
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a cell's frame,
     * based on the specified grid coordinate, cell dimensions, cell shape, and frame segment side.
     *
     * <p>The returned 2D array contains the ordered points (as x and y arrays) that define the requested
     * segment of the cell's outline (frame) as a polyline. The order and number of points depend on
     * the cell shape and the specified {@link CellShapeSide}.
     *
     * @param coordinate    the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param cellShape     the shape of the cell
     * @param side          the side or segment of the cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     */
    public static double[][] computeCellFrameSegmentPolyline(GridCoordinate coordinate, CellDimension cellDimension,
                                                             CellShape cellShape, CellShapeSide side) {
        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, cellShape);

        return switch (cellShape) {
            case TRIANGLE -> computeTriangleFrameSegmentPolyline(topLeft, cellDimension,
                    coordinate.isTriangleCellPointingDown(), side);
            case SQUARE -> computeSquareFrameSegmentPolyline(topLeft, cellDimension, side);
            case HEXAGON -> computeHexagonFrameSegmentPolyline(topLeft, cellDimension, side);
        };
    }

    /**
     * Determines whether a given point lies inside the triangle defined by three vertices.
     *
     * <p>This method uses barycentric coordinates to check if the point {@code p} is inside
     * the triangle formed by the points {@code a}, {@code b}, and {@code c}. The calculation
     * is robust for all triangle orientations and works for points on the edge as well.
     *
     * @param p the point to test
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     * @return {@code true} if the point {@code p} lies inside or on the edge of the triangle; {@code false} otherwise
     */
    static boolean isPointInTriangle(Point2D p, Point2D a, Point2D b, Point2D c) {
        double area = ONE_HALF * ((-b.getY() * c.getX()) + (a.getY() * (-b.getX() + c.getX()))
                + (a.getX() * (b.getY() - c.getY())) + (b.getX() * c.getY()));
        double s = (1 / (2 * area)) * (((a.getY() * c.getX()) - (a.getX() * c.getY()))
                + ((c.getY() - a.getY()) * p.getX()) + ((a.getX() - c.getX()) * p.getY()));
        double t = (1 / (2 * area)) * (((a.getX() * b.getY()) - (a.getY() * b.getX()))
                + ((a.getY() - b.getY()) * p.getX()) + ((b.getX() - a.getX()) * p.getY()));
        double u = 1 - s - t;
        return (s >= 0) && (t >= 0) && (u >= 0);
    }

    /**
     * Determines whether a given point lies inside the triangle defined by the cell at the specified grid coordinate.
     *
     * @param point         the point to test
     * @param coordinate    the grid coordinate of the triangle cell
     * @param cellDimension the dimensions of the triangle cell
     * @return {@code true} if the point lies inside or on the edge of the triangle at the given coordinate;
     * {@code false} otherwise
     */
    static boolean isPointInTriangleCell(Point2D point, GridCoordinate coordinate, CellDimension cellDimension) {
        double[][] polygon = computeCellPolygon(coordinate, cellDimension, CellShape.TRIANGLE);
        return isPointInTriangle(point,
                new Point2D(polygon[0][0], polygon[1][0]),
                new Point2D(polygon[0][1], polygon[1][1]),
                new Point2D(polygon[0][2], polygon[1][2]));
    }

    /**
     * Computes the x and y coordinates of the triangle cell's polygon vertices in canvas space.
     *
     * @param topLeft        the top-left position of the cell in canvas coordinates
     * @param cellDimension  the dimensions of the triangle cell
     * @param isPointingDown whether the triangle is pointing downwards
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    static double[][] computeTrianglePolygon(Point2D topLeft, CellDimension cellDimension, boolean isPointingDown) {
        double x = topLeft.getX();
        double y = topLeft.getY();
        double xCenter = x + cellDimension.halfEdgeLength();
        double xRight = x + cellDimension.edgeLength();
        double yBottom = y + cellDimension.height();

        if (isPointingDown) {
            return new double[][]{{x, xCenter, xRight}, {y, yBottom, y}};
        }
        return new double[][]{{x, xCenter, xRight}, {yBottom, y, yBottom}};
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a triangle cell's frame,
     * based on the specified top-left position, cell dimensions, triangle orientation, and frame segment side.
     *
     * <p>The number and order of points depend on the triangle's orientation and the specified
     * {@link CellShapeSide}.
     *
     * @param topLeft        the top-left position of the triangle cell in canvas coordinates
     * @param cellDimension  the dimensions of the triangle cell
     * @param isPointingDown whether the triangle is pointing downwards
     * @param side           the side or segment of the triangle cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     */
    static double[][] computeTriangleFrameSegmentPolyline(Point2D topLeft, CellDimension cellDimension,
                                                          boolean isPointingDown, CellShapeSide side) {
        double x = topLeft.getX();
        double y = topLeft.getY();
        double xCenter = x + cellDimension.halfEdgeLength();
        double xRight = x + cellDimension.edgeLength();
        double yBottom = y + cellDimension.height();

        if (isPointingDown) {
            return switch (side) {
                case TOP -> new double[][]{{x, xRight}, {y, y}};
                case BOTTOM -> new double[][]{{x, xCenter, xRight}, {y, yBottom, y}};
                case LEFT -> new double[][]{{x, xCenter}, {y, yBottom}};
                case RIGHT -> new double[][]{{xRight, xCenter}, {y, yBottom}};
            };
        }
        return switch (side) {
            case TOP -> new double[][]{{x, xCenter, xRight}, {yBottom, y, yBottom}};
            case BOTTOM -> new double[][]{{x, xRight}, {yBottom, yBottom}};
            case LEFT -> new double[][]{{x, xCenter}, {yBottom, y}};
            case RIGHT -> new double[][]{{xCenter, xRight}, {y, yBottom}};
        };
    }

    /**
     * Computes the x and y coordinates of the square cell's polygon vertices in canvas space.
     *
     * @param topLeft       the top-left position of the square cell in canvas coordinates
     * @param cellDimension the dimensions of the square cell
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    static double[][] computeSquarePolygon(Point2D topLeft, CellDimension cellDimension) {
        double x = topLeft.getX();
        double y = topLeft.getY();
        double xRight = x + cellDimension.edgeLength();
        double yBottom = y + cellDimension.edgeLength();

        return new double[][]{{x, xRight, xRight, x}, {y, y, yBottom, yBottom}};
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a square cell's frame,
     * based on the specified top-left position, cell dimensions, and frame segment side.
     *
     * @param topLeft       the top-left position of the square cell in canvas coordinates
     * @param cellDimension the dimensions of the square cell
     * @param side          the side or segment of the square cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     */
    static double[][] computeSquareFrameSegmentPolyline(Point2D topLeft, CellDimension cellDimension,
                                                        CellShapeSide side) {
        double x = topLeft.getX();
        double y = topLeft.getY();
        double xRight = x + cellDimension.edgeLength();
        double yBottom = y + cellDimension.edgeLength();

        return switch (side) {
            case TOP -> new double[][]{{x, xRight}, {y, y}};
            case BOTTOM -> new double[][]{{x, xRight}, {yBottom, yBottom}};
            case LEFT -> new double[][]{{x, x}, {y, yBottom}};
            case RIGHT -> new double[][]{{xRight, xRight}, {y, yBottom}};
        };
    }

    /**
     * Computes the x and y coordinates of the hexagon cell's polygon vertices in canvas space.
     *
     * @param topLeft       the top-left position of the cell in canvas coordinates
     * @param cellDimension the dimensions of the hexagon cell
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    static double[][] computeHexagonPolygon(Point2D topLeft, CellDimension cellDimension) {
        double x = topLeft.getX();
        double y = topLeft.getY();
        double xTopLeft = x + cellDimension.halfEdgeLength();
        double xTopRight = x + cellDimension.edgeLength() + cellDimension.halfEdgeLength();
        double xRight = x + cellDimension.width();
        double yCenter = y + cellDimension.halfHeight();
        double yBottom = y + cellDimension.height();

        return new double[][]{
                {xTopLeft, xTopRight, xRight, xTopRight, xTopLeft, x},
                {y, y, yCenter, yBottom, yBottom, yCenter}};
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a hexagon cell's frame,
     * based on the specified top-left position, cell dimensions, and frame segment side.
     *
     * <p>The hexagon is flat-topped. The number and order of points depend on the specified
     * {@link CellShapeSide}.
     *
     * @param topLeft       the top-left position of the hexagon cell in canvas coordinates
     * @param cellDimension the dimensions of the hexagon cell
     * @param side          the side or segment of the hexagon cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     */
    static double[][] computeHexagonFrameSegmentPolyline(Point2D topLeft, CellDimension cellDimension,
                                                         CellShapeSide side) {
        double x = topLeft.getX();
        double y = topLeft.getY();
        double xTopLeft = x + cellDimension.halfEdgeLength();
        double xTopRight = x + cellDimension.edgeLength() + cellDimension.halfEdgeLength();
        double xRight = x + cellDimension.width();
        double yCenter = y + cellDimension.halfHeight();
        double yBottom = y + cellDimension.height();

        return switch (side) {
            case TOP -> new double[][]{{x, xTopLeft, xTopRight, xRight}, {yCenter, y, y, yCenter}};
            case BOTTOM -> new double[][]{{xRight, xTopRight, xTopLeft, x}, {yCenter, yBottom, yBottom, yCenter}};
            case LEFT -> new double[][]{{xTopLeft, x, xTopLeft}, {y, yCenter, yBottom}};
            case RIGHT -> new double[][]{{xTopRight, xRight, xTopRight}, {y, yCenter, yBottom}};
        };
    }

}
