package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.*;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.*;
import java.util.stream.*;

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
     * Number one, as primitive double.
     */
    public static final double ONE = 1.0d;
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
     * One quarter, used for fractional calculations.
     * This constant is approximately 0.25.
     */
    public static final double ONE_QUARTER = 1.0d / 4.0d;
    /**
     * Three quarters, used for fractional calculations.
     * This constant is approximately 0.75.
     */
    public static final double THREE_QUARTERS = 3.0d / 4.0d;

    public static final double MIN_SIDE_LENGTH = 1.0d;
    public static final double MAX_SIDE_LENGTH = 4_096.0d;

    /**
     * Private constructor to prevent instantiation.
     */
    private GridGeometry() {
    }

    /**
     * Computes the dimensions of a cell based on its side length and shape.
     *
     * @param sideLength the length of each side of the cell in pixels, must be between MIN_SIDE_LENGTH and MAX_SIDE_LENGTH
     * @param shape the shape of the cell
     * @return a CellDimension object representing the dimensions of the cell
     * @see GridGeometry#MIN_SIDE_LENGTH
     * @see GridGeometry#MAX_SIDE_LENGTH
     */
    public static CellDimension computeCellDimension(double sideLength, CellShape shape) {
        Objects.requireNonNull(shape);
        if ((sideLength < MIN_SIDE_LENGTH) || (sideLength > MAX_SIDE_LENGTH)) {
            throw new IllegalArgumentException("Side length must be between " + MIN_SIDE_LENGTH + " and " + MAX_SIDE_LENGTH + ".");
        }

        double halfSideLength = sideLength * ONE_HALF;
        double width = switch (shape) {
            case TRIANGLE, SQUARE -> sideLength;
            case HEXAGON -> sideLength * TWO;
        };
        double halfWidth = switch (shape) {
            case TRIANGLE, SQUARE -> halfSideLength;
            case HEXAGON -> sideLength;
        };
        double height = switch (shape) {
            case TRIANGLE -> SQRT_THREE * halfSideLength;
            case SQUARE -> sideLength;
            case HEXAGON -> SQRT_THREE * sideLength;
        };
        double halfHeight = switch (shape) {
            case TRIANGLE, HEXAGON -> height * ONE_HALF;
            case SQUARE -> halfSideLength;
        };
        double innerRadius = switch (shape) {
            case TRIANGLE -> height * ONE_THIRD;
            case SQUARE -> halfSideLength;
            case HEXAGON -> halfHeight;
        };
        double outerRadius = switch (shape) {
            case TRIANGLE -> Math.sqrt(Math.pow(halfSideLength, TWO) + Math.pow(innerRadius, TWO));
            case SQUARE -> SQRT_TWO * halfSideLength;
            case HEXAGON -> sideLength;
        };
        return new CellDimension(sideLength, width, height,
                halfSideLength, halfWidth, halfHeight,
                innerRadius, outerRadius);
    }

    /**
     * Computes the total pixel dimensions of the grid area based on size, cell dimension and shape.
     *
     * @param gridSize the size of the grid in terms of columns and rows
     * @param cellDimension the dimensions of a single cell in the grid
     * @param shape the shape of the cells in the grid
     * @return a Dimension2D object representing the total width and height of the grid area
     */
    public static Dimension2D computeGridDimension(GridSize gridSize, CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(gridSize);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        double width;
        double height;

        switch (shape) {
            case TRIANGLE -> {
                width = (gridSize.width() * cellDimension.width()) + cellDimension.halfWidth();
                int rows = (gridSize.height() + 1) / 2; // Two triangles per logical row. Round up for odd heights.
                height = rows * cellDimension.height();
            }
            case SQUARE -> {
                width = gridSize.width() * cellDimension.width();
                height = gridSize.height() * cellDimension.height();
            }
            case HEXAGON -> {
                double columnWidth = cellDimension.width() * THREE_QUARTERS;
                width = (gridSize.width() * columnWidth) + (cellDimension.width() * ONE_QUARTER);
                double rowHeightOffset = cellDimension.halfHeight();
                height = (gridSize.height() * cellDimension.height()) + ((gridSize.width() > 1) ? rowHeightOffset : ZERO);
            }
            default -> throw new IllegalArgumentException("Unsupported CellShape: " + shape);
        }

        return new Dimension2D(width, height);
    }

    /**
     * Determines whether a triangle cell is pointing down.
     *
     * @param coordinate the grid coordinate of the triangle
     * @return true if the triangle points down, false if it points up
     */
    public static boolean isTrianglePointingDown(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);

        return (coordinate.y() % 2) == 0;
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
     * @param coordinate the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return {@code true} if the point {@code p} lies inside or on the edge of the triangle at the given coordinate; {@code false} otherwise
     */
    public static boolean isPointInTriangleAt(Point2D point, GridCoordinate coordinate, CellDimension cellDimension, CellShape shape) {
        double[][] polygon = computeCellPolygon(coordinate, cellDimension, shape);
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
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        double x;
        double y;

        switch (shape) {
            case TRIANGLE -> {
                // In a flat-top triangle grid, every two rows form a repeating vertical pattern.
                // Depending on the row's position in the 4-row cycle, some rows are horizontally offset by half a cell.
                // This ensures that upward- and downward-pointing triangles interlock correctly.
                int triangleOrientationCycle = coordinate.y() % 4;
                boolean isOffsetTriangleRow = ((triangleOrientationCycle == 1) || (triangleOrientationCycle == 2));
                double xOffset = isOffsetTriangleRow ? cellDimension.halfSideLength() : ZERO;
                x = (coordinate.x() * cellDimension.sideLength()) + xOffset;

                // Each logical row consists of two triangle rows stacked vertically.
                // The vertical position is based on the row index multiplied by the triangle height.
                int row = coordinate.y() / 2; // Round down
                y = row * cellDimension.height();
            }
            case SQUARE -> {
                x = coordinate.x() * cellDimension.width();
                y = coordinate.y() * cellDimension.height();
            }
            case HEXAGON -> {
                // In a flat-top hexagon grid, each column is spaced 1.5 times the side length apart.
                // This accounts for the horizontal overlap between adjacent hexagons.
                x = coordinate.x() * cellDimension.sideLength() * THREE_HALVES;

                // Hexagon rows are vertically offset in every second column to create a staggered layout.
                // Even columns start at the base Y position, odd columns are shifted down by half a hexagon height.
                if ((coordinate.x() % 2) == 0) {
                    y = coordinate.y() * cellDimension.height();
                } else {
                    y = (coordinate.y() * cellDimension.height()) + cellDimension.halfHeight();
                }
            }
            default -> throw new IllegalArgumentException("Unsupported CellShape: " + shape);
        }
        return new Point2D(x, y);
    }

    /**
     * Converts a canvas position in pixel coordinates to the corresponding grid coordinate.
     *
     * @param point the canvas position in pixel coordinates
     * @param cellDimension the dimensions of the cell
     * @param structure the grid structure defining the cell shape and size
     * @return the grid coordinate corresponding to the canvas position
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static GridCoordinate fromCanvasPosition(Point2D point, CellDimension cellDimension, GridStructure structure) {
        Objects.requireNonNull(point);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(structure);

        return switch (structure.cellShape()) {
            case TRIANGLE -> {
                int approxX = (int) (point.getX() / cellDimension.width());
                int approxY = (int) (point.getY() / cellDimension.height()) * 2;

                // Test four candidate coordinates for the triangle cell.
                yield Stream.of(new GridCoordinate(approxX, approxY),
                                    new GridCoordinate(approxX - 1, approxY + 1),
                                    new GridCoordinate(approxX, approxY + 1),
                                    new GridCoordinate(approxX - 1, approxY))
                            .filter(structure::isCoordinateValid)
                            .filter(c -> isPointInTriangleAt(point, c, cellDimension, structure.cellShape()))
                            .findFirst()
                            .orElse(GridCoordinate.ILLEGAL);
            }
            case SQUARE -> {
                int x = (int) (point.getX() / cellDimension.width());
                int y = (int) (point.getY() / cellDimension.height());
                yield new GridCoordinate(x, y);
            }
            case HEXAGON -> {
                double approxX = point.getX() / (cellDimension.sideLength() * THREE_HALVES);
                double approxY = point.getY() / cellDimension.height();

                int baseX = (int) Math.floor(approxX);
                int baseY = (int) Math.floor(approxY - (((baseX % 2) == 0) ? 0 : ONE_HALF));

                // Test nine candidate coordinates for the hexagon cell.
                GridCoordinate bestMatch = GridCoordinate.ILLEGAL;
                double minDist = Double.MAX_VALUE;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        GridCoordinate candidate = new GridCoordinate(baseX + dx, baseY + dy);
                        if (structure.isCoordinateValid(candidate)) {
                            Point2D center = computeCellCenter(candidate, cellDimension, structure.cellShape());
                            double dist = center.distance(point);
                            if (dist < minDist) {
                                minDist = dist;
                                bestMatch = candidate;
                            }
                        }
                    }
                }
                yield bestMatch;
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
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, shape);
        double xOffset;
        double yOffset;

        switch (shape) {
            case TRIANGLE -> {
                xOffset = cellDimension.halfSideLength();
                yOffset = isTrianglePointingDown(coordinate)
                        ? (cellDimension.height() * ONE_THIRD)
                        : (cellDimension.height() * TWO_THIRDS);
            }
            case SQUARE -> {
                xOffset = cellDimension.halfSideLength();
                yOffset = cellDimension.halfSideLength();
            }
            case HEXAGON -> {
                xOffset = cellDimension.halfWidth();
                yOffset = cellDimension.halfHeight();
            }
            default -> throw new IllegalArgumentException("Unsupported CellShape: " + shape);
        }
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
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

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
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, shape);
        double x = topLeft.getX();
        double y = topLeft.getY();
        double[] xPoints = new double[shape.vertexCount()];
        double[] yPoints = new double[shape.vertexCount()];

        switch (shape) {
            case TRIANGLE -> {
                if (isTrianglePointingDown(coordinate)) {
                    xPoints[0] = x;
                    yPoints[0] = y;
                    xPoints[1] = x + cellDimension.halfSideLength();
                    yPoints[1] = y + cellDimension.height();
                    xPoints[2] = x + cellDimension.sideLength();
                    yPoints[2] = y;
                } else {
                    xPoints[0] = x;
                    yPoints[0] = y + cellDimension.height();
                    xPoints[1] = x + cellDimension.halfSideLength();
                    yPoints[1] = y;
                    xPoints[2] = x + cellDimension.sideLength();
                    yPoints[2] = y + cellDimension.height();
                }
            }
            case SQUARE -> {
                xPoints[0] = x;
                yPoints[0] = y;
                xPoints[1] = x + cellDimension.sideLength();
                yPoints[1] = y;
                xPoints[2] = x + cellDimension.sideLength();
                yPoints[2] = y + cellDimension.sideLength();
                xPoints[3] = x;
                yPoints[3] = y + cellDimension.sideLength();
            }
            case HEXAGON -> {
                xPoints[0] = x + cellDimension.halfSideLength();
                yPoints[0] = y;
                xPoints[1] = x + cellDimension.sideLength() + cellDimension.halfSideLength();
                yPoints[1] = y;
                xPoints[2] = x + cellDimension.width();
                yPoints[2] = y + cellDimension.halfHeight();
                xPoints[3] = xPoints[1];
                yPoints[3] = y + cellDimension.height();
                xPoints[4] = x + cellDimension.halfSideLength();
                yPoints[4] = y + cellDimension.height();
                xPoints[5] = x;
                yPoints[5] = y + cellDimension.halfHeight();
            }
        }
        return new double[][]{xPoints, yPoints};
    }

}
