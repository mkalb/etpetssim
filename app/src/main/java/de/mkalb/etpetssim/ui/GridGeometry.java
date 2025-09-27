package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.*;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.*;

/**
 * Utility class for grid geometry computations and conversions.
 */
public final class GridGeometry {

    /**
     * Square root of 2, used for various geometric calculations.
     * This constant is approximately 1.4142135623730951.
     */
    public static final double SQRT_TWO = Math.sqrt(2);
    /**
     * Square root of 3, used for various geometric calculations.
     * This constant is approximately 1.7320508075688772.
     */
    public static final double SQRT_THREE = Math.sqrt(3);

    /**
     * Number zero, as primitive double.
     */
    public static final double ZERO = 0.0d;
    /**
     * Number two, as primitive double.
     */
    public static final double TWO = 2.0d;
    /**
     * One third, used for fractional calculations.
     * This constant is approximately 0.3333333333333333.
     */
    public static final double ONE_THIRD = 1.0d / 3.0d;
    /**
     * Two thirds, used for fractional calculations.
     * This constant is approximately 0.6666666666666666.
     */
    public static final double TWO_THIRDS = 2.0d / 3.0d;
    /**
     * One half, used for fractional calculations.
     * This constant is approximately 0.5.
     */
    public static final double ONE_HALF = 1.0d / 2.0d;
    /**
     * Three halves, used for fractional calculations.
     * This constant is approximately 1.5.
     */
    public static final double THREE_HALVES = 3.0d / 2.0d;

    /**
     * Minimum edge length of a cell in pixels.
     * This constant is 1.0.
     */
    public static final double MIN_EDGE_LENGTH = 1.0d;
    /**
     * Maximum edge length of a cell in pixels.
     * This constant is 1,024.0.
     */
    public static final double MAX_EDGE_LENGTH = 1_024.0d;

    /**
     * Offset candidates for TRIANGLE cells, starting with the most likely center (0,0).
     */
    private static final List<int[]> TRIANGLE_NEIGHBOR_OFFSETS = List.of(
            new int[]{0, 0},
            new int[]{1, 0},
            new int[]{-1, 0}
    );
    /**
     * Offset candidates for HEXAGON cells, starting with the most likely center (0,0).
     */
    private static final List<int[]> HEXAGON_NEIGHBOR_OFFSETS = List.of(
            new int[]{0, 0},
            new int[]{-1, 0},
            new int[]{0, -1},
            new int[]{-1, -1},
            new int[]{-1, 1}
    );

    /**
     * Private constructor to prevent instantiation.
     */
    private GridGeometry() {
    }

    /**
     * Converts the edge length of a cell to match the width of another cell shape.
     * This method ensures that the resulting cell shape has the same width as the original cell shape.
     *
     * @param fromEdgeLength the edge length of the original cell shape
     * @param fromCellShape the shape of the original cell
     * @param toCellShape the shape of the target cell
     * @return the edge length required for the target cell shape to match the width of the original cell shape
     */
    public static double convertEdgeLengthToMatchWidth(double fromEdgeLength, CellShape fromCellShape, CellShape toCellShape) {
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
     * @param shape the shape of the cell
     * @return a CellDimension object representing the dimensions of the cell
     * @throws IllegalArgumentException if {@code edgeLength} is outside the inclusive range
     *                                  [{@link #MIN_EDGE_LENGTH}, {@link #MAX_EDGE_LENGTH}]
     * @see de.mkalb.etpetssim.ui.GridGeometry#MIN_EDGE_LENGTH
     * @see de.mkalb.etpetssim.ui.GridGeometry#MAX_EDGE_LENGTH
     */
    public static CellDimension computeCellDimension(double edgeLength, CellShape shape) {
        if ((edgeLength < MIN_EDGE_LENGTH) || (edgeLength > MAX_EDGE_LENGTH)) {
            throw new IllegalArgumentException("Edge length must be between " + MIN_EDGE_LENGTH + " and " + MAX_EDGE_LENGTH + ".");
        }

        double halfEdgeLength = edgeLength * ONE_HALF;
        double width = switch (shape) {
            case TRIANGLE, SQUARE -> edgeLength;
            case HEXAGON -> edgeLength + edgeLength;
        };
        double halfWidth = switch (shape) {
            case TRIANGLE, SQUARE -> halfEdgeLength;
            case HEXAGON -> edgeLength;
        };
        double height = switch (shape) {
            case TRIANGLE -> SQRT_THREE * halfEdgeLength;
            case SQUARE -> edgeLength;
            case HEXAGON -> SQRT_THREE * edgeLength;
        };
        double halfHeight = switch (shape) {
            case TRIANGLE, HEXAGON -> height * ONE_HALF;
            case SQUARE -> halfEdgeLength;
        };
        double innerRadius = switch (shape) {
            case TRIANGLE -> height * ONE_THIRD;
            case SQUARE -> halfEdgeLength;
            case HEXAGON -> halfHeight;
        };
        double outerRadius = switch (shape) {
            case TRIANGLE -> Math.sqrt(Math.pow(halfEdgeLength, TWO) + Math.pow(innerRadius, TWO));
            case SQUARE -> SQRT_TWO * halfEdgeLength;
            case HEXAGON -> edgeLength;
        };
        double columnWidth = switch (shape) {
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
     * @param gridSize the size of the grid in terms of columns and rows
     * @param cellDimension the dimensions of a single cell in the grid
     * @param shape the shape of the cells in the grid
     * @return a Dimension2D object representing the total width and height of the grid area
     * @throws IllegalArgumentException if {@code shape} is unsupported
     */
    public static Dimension2D computeGridDimension(GridSize gridSize, CellDimension cellDimension, CellShape shape) {
        double width;
        double height;

        switch (shape) {
            case TRIANGLE -> {
                double columnWidthOffset = cellDimension.halfEdgeLength();
                width = (gridSize.width() * cellDimension.columnWidth()) + columnWidthOffset;
                height = gridSize.height() * cellDimension.rowHeight();
            }
            case SQUARE -> {
                width = gridSize.width() * cellDimension.columnWidth();
                height = gridSize.height() * cellDimension.rowHeight();
            }
            case HEXAGON -> {
                double columnWidthOffset = cellDimension.halfEdgeLength();
                width = (gridSize.width() * cellDimension.columnWidth()) + columnWidthOffset;
                double rowHeightOffset = ((gridSize.width() > 1) ? cellDimension.halfHeight() : ZERO);
                height = (gridSize.height() * cellDimension.rowHeight()) + rowHeightOffset;
            }
            default -> throw new IllegalArgumentException("Unsupported CellShape: " + shape);
        }

        return new Dimension2D(width, height);
    }

    /**
     * Computes the total pixel dimensions of the grid area based on the grid size,
     * cell edge length, and cell shape.
     *
     * <p>This method is a convenience wrapper that combines the computation of
     * cell dimensions and grid dimensions into a single call.</p>
     *
     * @param gridSize the size of the grid in terms of columns and rows
     * @param edgeLength the length of each edge of the cell in pixels, must be between {@link #MIN_EDGE_LENGTH}
     *                        and  {@link #MAX_EDGE_LENGTH}
     * @param shape the shape of the cells in the grid
     * @return a Dimension2D object representing the total width and height of the grid area
     * @see de.mkalb.etpetssim.ui.GridGeometry#computeCellDimension(double, de.mkalb.etpetssim.engine.CellShape)
     * @see de.mkalb.etpetssim.ui.GridGeometry#computeGridDimension(de.mkalb.etpetssim.engine.GridSize, de.mkalb.etpetssim.ui.CellDimension, de.mkalb.etpetssim.engine.CellShape)
     */
    public static Dimension2D computeGridDimension(GridSize gridSize, double edgeLength, CellShape shape) {
        return computeGridDimension(gridSize, computeCellDimension(edgeLength, shape), shape);
    }

    /**
     * Determines whether a given point lies inside the triangle defined by three vertices.
     *
     * <p>This method uses barycentric coordinates to check if the point {@code p} is inside
     * the triangle formed by the points {@code a}, {@code b}, and {@code c}. The calculation
     * is robust for all triangle orientations and works for points on the edge as well.</p>
     *
     * @param p the point to test
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     * @return {@code true} if the point {@code p} lies inside or on the edge of the triangle; {@code false} otherwise
     */
    public static boolean isPointInTriangle(Point2D p, Point2D a, Point2D b, Point2D c) {
        double area = ONE_HALF * ((-b.getY() * c.getX()) + (a.getY() * (-b.getX() + c.getX())) + (a.getX() * (b.getY() - c.getY())) + (b.getX() * c.getY()));
        double s = (1 / (2 * area)) * (((a.getY() * c.getX()) - (a.getX() * c.getY())) + ((c.getY() - a.getY()) * p.getX()) + ((a.getX() - c.getX()) * p.getY()));
        double t = (1 / (2 * area)) * (((a.getX() * b.getY()) - (a.getY() * b.getX())) + ((a.getY() - b.getY()) * p.getX()) + ((b.getX() - a.getX()) * p.getY()));
        double u = 1 - s - t;
        return (s >= 0) && (t >= 0) && (u >= 0);
    }

    /**
     * Determines whether a given point lies inside the triangle defined by the cell at the specified grid coordinate.
     *
     * @param point the point to test
     * @param coordinate the grid coordinate of the triangle cell
     * @param cellDimension the dimensions of the triangle cell
     * @return {@code true} if the point {@code p} lies inside or on the edge of the triangle at the given coordinate; {@code false} otherwise
     */
    public static boolean isPointInTriangleCell(Point2D point, GridCoordinate coordinate, CellDimension cellDimension) {
        double[][] polygon = computeCellPolygon(coordinate, cellDimension, CellShape.TRIANGLE);
        return isPointInTriangle(point, new Point2D(polygon[0][0], polygon[1][0]), new Point2D(polygon[0][1], polygon[1][1]), new Point2D(polygon[0][2], polygon[1][2]));
    }

    /**
     * Converts a grid coordinate to its corresponding canvas position in pixel coordinates.
     *
     * @param coordinate the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return the canvas position of the cell in pixel coordinates
     */
    public static Point2D toCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension, CellShape shape) {
        double x = coordinate.x() * cellDimension.columnWidth();
        double yOffset = ((shape == CellShape.HEXAGON) && coordinate.hasHexagonCellYOffset()) ? cellDimension.halfHeight() : ZERO;
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
     * mapping, use {@link #fromCanvasPosition(javafx.geometry.Point2D, de.mkalb.etpetssim.ui.CellDimension, javafx.geometry.Dimension2D, de.mkalb.etpetssim.engine.GridStructure)}.</p>
     *
     * @param point the canvas position in pixel coordinates
     * @param cellDimension the dimensions of the cell
     * @param gridDimension the total dimensions of the grid area in pixels; used to check if the point is within bounds
     * @param structure the grid structure defining the cell shape and size
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
                boolean hasHexagonCellYOffset = (estimatedGridX % 2) != 0; // see GridCoordinate.hasHexagonCellYOffset()
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
     * For triangles, barycentric coordinate checks are used; for hexagons, the closest valid cell
     * center within a bounding box is selected.</p>
     *
     * @param point the canvas position in pixel coordinates
     * @param cellDimension the dimensions of the cell
     * @param gridDimension the total dimensions of the grid area in pixels; used to check if the point is within bounds
     * @param structure the grid structure defining the cell shape and size
     * @return the grid coordinate corresponding to the canvas position, or {@code GridCoordinate.ILLEGAL} if no match is found
     */
    public static GridCoordinate fromCanvasPosition(Point2D point, CellDimension cellDimension,
                                                    Dimension2D gridDimension, GridStructure structure) {
        GridCoordinate estimatedCoordinate = estimateGridCoordinate(point, cellDimension, gridDimension, structure);
        if (estimatedCoordinate.isIllegal()) {
            return estimatedCoordinate;
        }

        return switch (structure.cellShape()) {
            case TRIANGLE -> TRIANGLE_NEIGHBOR_OFFSETS.stream()
                                                      .map(offset -> new GridCoordinate(estimatedCoordinate.x() + offset[0], estimatedCoordinate.y() + offset[1]))
                                                      .filter(structure::isCoordinateValid)
                                                      .filter(c -> isPointInTriangleCell(point, c, cellDimension))
                                                      .findFirst()
                                                      .orElse(GridCoordinate.ILLEGAL);
            case SQUARE -> estimatedCoordinate;
            case HEXAGON -> {
                // Test candidate coordinates for the hexagon cell.
                GridCoordinate closestValidHexCell = GridCoordinate.ILLEGAL;
                double minDistance = Double.MAX_VALUE;
                double earlyAcceptThreshold = cellDimension.halfEdgeLength();
                for (int[] offset : HEXAGON_NEIGHBOR_OFFSETS) {
                    GridCoordinate candidate = new GridCoordinate(estimatedCoordinate.x() + offset[0], estimatedCoordinate.y() + offset[1]);

                    Point2D topLeft = toCanvasPosition(candidate, cellDimension, structure.cellShape());

                    // Check if the point is within the bounding box of the cell.
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
                        closestValidHexCell = candidate;
                        if (distance < earlyAcceptThreshold) {
                            break;
                        }
                    }
                }

                yield closestValidHexCell;
            }
        };
    }

    /**
     * Computes the center point of a cell on the canvas based on its coordinate, shape, and dimensions.
     *
     * @param coordinate the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return the center point of the cell in canvas coordinates
     */
    public static Point2D computeCellCenter(GridCoordinate coordinate, CellDimension cellDimension, CellShape shape) {
        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, shape);
        double xOffset = cellDimension.halfWidth();
        double yOffset = switch (shape) {
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
     * @param coordinate the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return the bounding rectangle of the cell
     */
    public static Rectangle2D computeCellBounds(GridCoordinate coordinate, CellDimension cellDimension, CellShape shape) {
        return cellDimension.boundingBoxAt(toCanvasPosition(coordinate, cellDimension, shape));
    }

    /**
     * Computes the x and y coordinates of the cell's polygon vertices in canvas space.
     *
     * @param coordinate the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    public static double[][] computeCellPolygon(GridCoordinate coordinate, CellDimension cellDimension, CellShape shape) {
        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, shape);

        return switch (shape) {
            case TRIANGLE -> computeTrianglePolygon(topLeft, cellDimension, coordinate.isTriangleCellPointingDown());
            case SQUARE -> computeSquarePolygon(topLeft, cellDimension);
            case HEXAGON -> computeHexagonPolygon(topLeft, cellDimension);
        };
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a cell's frame,
     * based on the specified grid coordinate, cell dimensions, cell shape, and frame segment direction.
     *
     * <p>
     * The returned 2D array contains the ordered points (as x and y arrays) that define the requested
     * segment of the cell's outline (frame) as a polyline. The order and number of points depend on
     * the cell shape and the specified {@link PolygonViewDirection}.
     * </p>
     *
     * @param coordinate the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @param direction the direction or segment of the cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     * @throws IllegalArgumentException if {@code direction} is unsupported
     */
    public static double[][] computeCellFrameSegmentPolyline(GridCoordinate coordinate, CellDimension cellDimension,
                                                             CellShape shape, PolygonViewDirection direction) {
        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, shape);

        return switch (shape) {
            case TRIANGLE ->
                    computeTriangleFrameSegmentPolyline(topLeft, cellDimension, coordinate.isTriangleCellPointingDown(), direction);
            case SQUARE -> computeSquareFrameSegmentPolyline(topLeft, cellDimension, direction);
            case HEXAGON -> computeHexagonFrameSegmentPolyline(topLeft, cellDimension, direction);
        };
    }

    /**
     * Computes the x and y coordinates of the triangle cell's polygon vertices in canvas space.
     *
     * @param topLeft the top-left position of the cell in canvas coordinates
     * @param cellDimension the dimensions of the triangle cell
     * @param isPointingDown whether the triangle is pointing downwards
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    public static double[][] computeTrianglePolygon(Point2D topLeft, CellDimension cellDimension, boolean isPointingDown) {
        double[] xPoints = new double[CellShape.TRIANGLE.vertexCount()];
        double[] yPoints = new double[CellShape.TRIANGLE.vertexCount()];
        double x = topLeft.getX();
        double y = topLeft.getY();

        if (isPointingDown) {
            xPoints[0] = x;
            yPoints[0] = y;
            xPoints[1] = x + cellDimension.halfEdgeLength();
            yPoints[1] = y + cellDimension.height();
            xPoints[2] = x + cellDimension.edgeLength();
            yPoints[2] = y;
        } else {
            xPoints[0] = x;
            yPoints[0] = y + cellDimension.height();
            xPoints[1] = x + cellDimension.halfEdgeLength();
            yPoints[1] = y;
            xPoints[2] = x + cellDimension.edgeLength();
            yPoints[2] = y + cellDimension.height();
        }

        return new double[][]{xPoints, yPoints};
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a triangle cell's frame,
     * based on the specified top-left position, cell dimensions, triangle orientation, and frame segment direction.
     *
     * <p>
     * This method is specifically designed for cells of shape {@link CellShape#TRIANGLE} and only computes
     * polyline segments for triangle cells. The returned 2D array contains the ordered points (as x and y arrays)
     * that define the requested segment of the triangle cell's outline (frame) as a polyline.
     * The number and order of points depend on the triangle's orientation and the specified {@link PolygonViewDirection}.
     * </p>
     *
     * @param topLeft the top-left position of the triangle cell in canvas coordinates
     * @param cellDimension the dimensions of the triangle cell
     * @param isPointingDown whether the triangle is pointing downwards
     * @param direction the direction or segment of the triangle cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     * @throws IllegalArgumentException if {@code direction} is unsupported
     */
    public static double[][] computeTriangleFrameSegmentPolyline(Point2D topLeft, CellDimension cellDimension, boolean isPointingDown, PolygonViewDirection direction) {
        double[] xPoints;
        double[] yPoints;
        double x = topLeft.getX();
        double y = topLeft.getY();

        if (isPointingDown) {
            switch (direction) {
                case TOP -> {
                    xPoints = new double[2];
                    yPoints = new double[2];
                    xPoints[0] = x;
                    yPoints[0] = y;
                    xPoints[1] = x + cellDimension.edgeLength();
                    yPoints[1] = y;
                }
                case BOTTOM -> {
                    xPoints = new double[3];
                    yPoints = new double[3];
                    xPoints[0] = x;
                    yPoints[0] = y;
                    xPoints[1] = x + cellDimension.halfEdgeLength();
                    yPoints[1] = y + cellDimension.height();
                    xPoints[2] = x + cellDimension.edgeLength();
                    yPoints[2] = y;
                }
                case LEFT -> {
                    xPoints = new double[2];
                    yPoints = new double[2];
                    xPoints[0] = x;
                    yPoints[0] = y;
                    xPoints[1] = x + cellDimension.halfEdgeLength();
                    yPoints[1] = y + cellDimension.height();
                }
                case RIGHT -> {
                    xPoints = new double[2];
                    yPoints = new double[2];
                    xPoints[0] = x + cellDimension.edgeLength();
                    yPoints[0] = y;
                    xPoints[1] = x + cellDimension.halfEdgeLength();
                    yPoints[1] = y + cellDimension.height();
                }
                default -> throw new IllegalArgumentException("Unsupported PolygonViewDirection: " + direction);
            }
        } else { // pointing up
            switch (direction) {
                case TOP -> {
                    xPoints = new double[3];
                    yPoints = new double[3];
                    xPoints[0] = x;
                    yPoints[0] = y + cellDimension.height();
                    xPoints[1] = x + cellDimension.halfEdgeLength();
                    yPoints[1] = y;
                    xPoints[2] = x + cellDimension.edgeLength();
                    yPoints[2] = y + cellDimension.height();
                }
                case BOTTOM -> {
                    xPoints = new double[2];
                    yPoints = new double[2];
                    xPoints[0] = x;
                    yPoints[0] = y + cellDimension.height();
                    xPoints[1] = x + cellDimension.edgeLength();
                    yPoints[1] = y + cellDimension.height();
                }
                case LEFT -> {
                    xPoints = new double[2];
                    yPoints = new double[2];
                    xPoints[0] = x;
                    yPoints[0] = y + cellDimension.height();
                    xPoints[1] = x + cellDimension.halfEdgeLength();
                    yPoints[1] = y;
                }
                case RIGHT -> {
                    xPoints = new double[2];
                    yPoints = new double[2];
                    xPoints[0] = x + cellDimension.halfEdgeLength();
                    yPoints[0] = y;
                    xPoints[1] = x + cellDimension.edgeLength();
                    yPoints[1] = y + cellDimension.height();
                }
                default -> throw new IllegalArgumentException("Unsupported PolygonViewDirection: " + direction);
            }
        }

        return new double[][]{xPoints, yPoints};
    }

    /**
     * Computes the x and y coordinates of the square cell's polygon vertices in canvas space.
     *
     * @param topLeft the top-left position of the square cell in canvas coordinates
     * @param cellDimension the dimensions of the square cell
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    public static double[][] computeSquarePolygon(Point2D topLeft, CellDimension cellDimension) {
        double[] xPoints = new double[CellShape.SQUARE.vertexCount()];
        double[] yPoints = new double[CellShape.SQUARE.vertexCount()];
        double x = topLeft.getX();
        double y = topLeft.getY();

        xPoints[0] = x;
        yPoints[0] = y;
        xPoints[1] = x + cellDimension.edgeLength();
        yPoints[1] = y;
        xPoints[2] = x + cellDimension.edgeLength();
        yPoints[2] = y + cellDimension.edgeLength();
        xPoints[3] = x;
        yPoints[3] = y + cellDimension.edgeLength();

        return new double[][]{xPoints, yPoints};
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a square cell's frame,
     * based on the specified top-left position, cell dimensions, and frame segment direction.
     *
     * <p>
     * This method is specifically designed for cells of shape {@link CellShape#SQUARE} and only computes
     * polyline segments for square cells. The returned 2D array contains the ordered points (as x and y arrays)
     * that define the requested segment of the square cell's outline (frame) as a polyline.
     * </p>
     *
     * @param topLeft the top-left position of the square cell in canvas coordinates
     * @param cellDimension the dimensions of the square cell
     * @param direction the direction or segment of the square cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     * @throws IllegalArgumentException if {@code direction} is unsupported
     */
    public static double[][] computeSquareFrameSegmentPolyline(Point2D topLeft, CellDimension cellDimension,
                                                               PolygonViewDirection direction) {
        double[] xPoints;
        double[] yPoints;
        double x = topLeft.getX();
        double y = topLeft.getY();

        switch (direction) {
            case TOP -> {
                xPoints = new double[2];
                yPoints = new double[2];
                xPoints[0] = x;
                yPoints[0] = y;
                xPoints[1] = x + cellDimension.edgeLength();
                yPoints[1] = y;
            }
            case BOTTOM -> {
                xPoints = new double[2];
                yPoints = new double[2];
                xPoints[0] = x;
                yPoints[0] = y + cellDimension.edgeLength();
                xPoints[1] = x + cellDimension.edgeLength();
                yPoints[1] = y + cellDimension.edgeLength();
            }
            case LEFT -> {
                xPoints = new double[2];
                yPoints = new double[2];
                xPoints[0] = x;
                yPoints[0] = y;
                xPoints[1] = x;
                yPoints[1] = y + cellDimension.edgeLength();
            }
            case RIGHT -> {
                xPoints = new double[2];
                yPoints = new double[2];
                xPoints[0] = x + cellDimension.edgeLength();
                yPoints[0] = y;
                xPoints[1] = x + cellDimension.edgeLength();
                yPoints[1] = y + cellDimension.edgeLength();
            }
            default -> throw new IllegalArgumentException("Unsupported PolygonViewDirection: " + direction);
        }

        return new double[][]{xPoints, yPoints};
    }

    /**
     * Computes the x and y coordinates of the hexagon cell's polygon vertices in canvas space.
     *
     * @param topLeft the top-left position of the cell in canvas coordinates
     * @param cellDimension the dimensions of the hexagon cell
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    public static double[][] computeHexagonPolygon(Point2D topLeft, CellDimension cellDimension) {
        double[] xPoints = new double[CellShape.HEXAGON.vertexCount()];
        double[] yPoints = new double[CellShape.HEXAGON.vertexCount()];
        double x = topLeft.getX();
        double y = topLeft.getY();

        xPoints[0] = x + cellDimension.halfEdgeLength();
        yPoints[0] = y;
        xPoints[1] = x + cellDimension.edgeLength() + cellDimension.halfEdgeLength();
        yPoints[1] = y;
        xPoints[2] = x + cellDimension.width();
        yPoints[2] = y + cellDimension.halfHeight();
        xPoints[3] = xPoints[1];
        yPoints[3] = y + cellDimension.height();
        xPoints[4] = x + cellDimension.halfEdgeLength();
        yPoints[4] = y + cellDimension.height();
        xPoints[5] = x;
        yPoints[5] = y + cellDimension.halfHeight();

        return new double[][]{xPoints, yPoints};
    }

    /**
     * Computes the x and y coordinates of the points forming a polyline segment of a hexagon cell's frame,
     * based on the specified top-left position, cell dimensions, and frame segment direction.
     *
     * <p>
     * This method is specifically designed for cells of shape {@link CellShape#HEXAGON} (flat-topped) and only computes
     * polyline segments for hexagon cells. The returned 2D array contains the ordered points (as x and y arrays)
     * that define the requested segment of the hexagon cell's outline (frame) as a polyline.
     * The number and order of points depend on the specified {@link PolygonViewDirection}.
     * </p>
     *
     * @param topLeft the top-left position of the hexagon cell in canvas coordinates
     * @param cellDimension the dimensions of the hexagon cell
     * @param direction the direction or segment of the hexagon cell frame to compute
     * @return a 2D array: [0] = xPoints, [1] = yPoints, representing the polyline segment
     * @throws IllegalArgumentException if {@code direction} is unsupported
     */
    @SuppressWarnings("DuplicateExpressions")
    public static double[][] computeHexagonFrameSegmentPolyline(Point2D topLeft, CellDimension cellDimension,
                                                                PolygonViewDirection direction) {
        double x = topLeft.getX();
        double y = topLeft.getY();
        double[] xPoints;
        double[] yPoints;

        switch (direction) {
            case TOP -> {
                xPoints = new double[4];
                yPoints = new double[4];
                xPoints[0] = x;
                yPoints[0] = y + cellDimension.halfHeight();
                xPoints[1] = x + cellDimension.halfEdgeLength();
                yPoints[1] = y;
                xPoints[2] = x + cellDimension.edgeLength() + cellDimension.halfEdgeLength();
                yPoints[2] = y;
                xPoints[3] = x + cellDimension.width();
                yPoints[3] = y + cellDimension.halfHeight();
            }
            case BOTTOM -> {
                xPoints = new double[4];
                yPoints = new double[4];
                xPoints[0] = x + cellDimension.width();
                yPoints[0] = y + cellDimension.halfHeight();
                xPoints[1] = x + cellDimension.edgeLength() + cellDimension.halfEdgeLength();
                yPoints[1] = y + cellDimension.height();
                xPoints[2] = x + cellDimension.halfEdgeLength();
                yPoints[2] = y + cellDimension.height();
                xPoints[3] = x;
                yPoints[3] = y + cellDimension.halfHeight();
            }
            case LEFT -> {
                xPoints = new double[3];
                yPoints = new double[3];
                xPoints[0] = x + cellDimension.halfEdgeLength();
                yPoints[0] = y;
                xPoints[1] = x;
                yPoints[1] = y + cellDimension.halfHeight();
                xPoints[2] = x + cellDimension.halfEdgeLength();
                yPoints[2] = y + cellDimension.height();
            }
            case RIGHT -> {
                xPoints = new double[3];
                yPoints = new double[3];
                xPoints[0] = x + cellDimension.edgeLength() + cellDimension.halfEdgeLength();
                yPoints[0] = y;
                xPoints[1] = x + cellDimension.width();
                yPoints[1] = y + cellDimension.halfHeight();
                xPoints[2] = x + cellDimension.edgeLength() + cellDimension.halfEdgeLength();
                yPoints[2] = y + cellDimension.height();
            }
            default -> throw new IllegalArgumentException("Unsupported PolygonViewDirection: " + direction);
        }

        return new double[][]{xPoints, yPoints};
    }

}
