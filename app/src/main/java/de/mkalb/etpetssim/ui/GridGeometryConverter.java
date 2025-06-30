package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridSize;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class GridGeometryConverter {

    /**
     * Private constructor to prevent instantiation.
     */
    private GridGeometryConverter() {
    }

    /**
     * Calculates the side length of a cell from its bounding box width.
     *
     * @param width the width of the cell's bounding box
     * @param shape the shape of the cell
     * @return the side length of the cell
     */
    public static double computeCellSideLengthFromWidth(double width, CellShape shape) {
        Objects.requireNonNull(shape);

        return switch (shape) {
            case SQUARE, TRIANGLE -> width;
            case HEXAGON -> width / 2.0;
        };
    }

    /**
     * Calculates the dimensions of a cell based on its side length and shape.
     *
     * @param sideLength the length of each side of the cell in pixels
     * @param shape the shape of the cell
     * @return a CellDimension object representing the dimensions of the cell
     */
    public static CellDimension computeCellDimension(double sideLength, CellShape shape) {
        Objects.requireNonNull(shape);

        double width = switch (shape) {
            case TRIANGLE, SQUARE -> sideLength;
            case HEXAGON -> sideLength * 2; // Hexagon width is twice the side length
        };
        double height = switch (shape) {
            case TRIANGLE -> (Math.sqrt(3) / 2) * sideLength;
            case SQUARE -> sideLength;
            case HEXAGON -> Math.sqrt(3) * sideLength;
        };
        return CellDimension.of(sideLength, width, height);
    }

    /**
     * Calculates the total pixel dimensions of the grid area based on size, cell dimension and shape.
     *
     * @param gridSize the size of the grid in terms of columns and rows
     * @param cellDimension the dimensions of a single cell in the grid
     * @param  shape the shape of the cells in the grid
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
                double columnWidth = cellDimension.width() * 0.75d;
                double rowHeightOffset = cellDimension.halfHeight();

                width = (gridSize.width() * columnWidth) + (cellDimension.width() * 0.25d);
                height = (gridSize.height() * cellDimension.height()) + ((gridSize.width() > 1) ? rowHeightOffset : 0.0d);
            }
            default -> throw new IllegalArgumentException("Unsupported CellShape: " + shape);
        }

        return new Dimension2D(width, height);
    }

    public static Point2D toCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        return switch (shape) {
            case TRIANGLE -> toTriangleCanvasPosition(coordinate, cellDimension);
            case SQUARE -> toSquareCanvasPosition(coordinate, cellDimension);
            case HEXAGON -> toHexagonCanvasPosition(coordinate, cellDimension);
        };
    }

    public static GridCoordinate fromCanvasPosition(Point2D point, CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(point);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        return switch (shape) {
            case TRIANGLE -> fromTriangleCanvasPosition(point, cellDimension);
            case SQUARE -> fromSquareCanvasPosition(point, cellDimension);
            case HEXAGON -> fromHexagonCanvasPosition(point, cellDimension);
        };
    }

    public static Point2D toTriangleCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);

        // In a flat-top triangle grid, every two rows form a repeating vertical pattern.
        // Depending on the row's position in the 4-row cycle, some rows are horizontally offset by half a cell.
        // This ensures that upward- and downward-pointing triangles interlock correctly.
        int triangleOrientationCycle = coordinate.y() % 4;
        boolean isOffsetTriangleRow = ((triangleOrientationCycle == 1) || (triangleOrientationCycle == 2));
        double xOffset = isOffsetTriangleRow ? cellDimension.halfSideLength() : 0.0d;
        double x = (coordinate.x() * cellDimension.sideLength()) + xOffset;

        // Each logical row consists of two triangle rows stacked vertically.
        // The vertical position is based on the row index multiplied by the triangle height.
        int row = coordinate.y() / 2; // Round down
        double y = row * cellDimension.height();

        // The resulting point represents the top-left corner of the bounding box for the triangle.
        return new Point2D(x, y);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static GridCoordinate fromTriangleCanvasPosition(Point2D point, CellDimension cellDimension) {
        Objects.requireNonNull(point);
        Objects.requireNonNull(cellDimension);

        int row = (int) (point.getY() / cellDimension.height()) * 2; // Logical row
        double xOffset = (((row % 4) == 1) || ((row % 4) == 2)) ? cellDimension.halfSideLength() : 0.0d;
        int column = (int) ((point.getX() - xOffset) / cellDimension.sideLength());

        return new GridCoordinate(column, row);
    }

    public static Point2D toSquareCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);

        double x = coordinate.x() * cellDimension.width();
        double y = coordinate.y() * cellDimension.height();
        return new Point2D(x, y);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static GridCoordinate fromSquareCanvasPosition(Point2D point, CellDimension cellDimension) {
        Objects.requireNonNull(point);
        Objects.requireNonNull(cellDimension);

        int x = (int) (point.getX() / cellDimension.width());
        int y = (int) (point.getY() / cellDimension.height());
        return new GridCoordinate(x, y);
    }

    public static Point2D toHexagonCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);

        // In a flat-top hexagon grid, each column is spaced 1.5 times the side length apart.
        // This accounts for the horizontal overlap between adjacent hexagons.
        double x = coordinate.x() * cellDimension.sideLength() * 1.5d;

        // Hexagon rows are vertically offset in every second column to create a staggered layout.
        // Even columns start at the base Y position, odd columns are shifted down by half a hexagon height.
        double y;
        if ((coordinate.x() % 2) == 0) {
            y = coordinate.y() * cellDimension.height();
        } else {
            y = (coordinate.y() * cellDimension.height()) + cellDimension.halfHeight();
        }

        // The resulting point represents the top-left corner of the bounding box for the hexagon.
        return new Point2D(x, y);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static GridCoordinate fromHexagonCanvasPosition(Point2D point, CellDimension cellDimension) {
        Objects.requireNonNull(point);
        Objects.requireNonNull(cellDimension);

        int column = (int) (point.getX() / (cellDimension.sideLength() * 1.5d));
        double yOffset = ((column % 2) == 0) ? 0.0d : cellDimension.halfHeight();
        int row = (int) ((point.getY() - yOffset) / cellDimension.height());

        return new GridCoordinate(column, row);
    }

    /**
     * Calculates the center point of a cell on the canvas based on its coordinate, shape, and dimensions.
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
        return switch (shape) {
            case TRIANGLE -> {
                boolean pointingDown = (coordinate.y() % 2) == 0;
                double centerY = pointingDown
                        ? (topLeft.getY() + ((1.0 / 3.0) * cellDimension.height()))
                        : (topLeft.getY() + ((2.0 / 3.0) * cellDimension.height()));
                yield new Point2D(topLeft.getX() + cellDimension.halfSideLength(), centerY);
            }
            case SQUARE ->
                    new Point2D(topLeft.getX() + cellDimension.halfSideLength(), topLeft.getY() + cellDimension.halfSideLength());
            case HEXAGON ->
                    new Point2D(topLeft.getX() + cellDimension.halfWidth(), topLeft.getY() + cellDimension.halfHeight());
        };
    }

    /**
     * Calculates the radius of a circle that fits entirely inside the cell shape.
     *
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return the radius of the largest circle that fits inside the cell
     */
    public static double computeCellInnerRadius(CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        return switch (shape) {
            case TRIANGLE -> Math.min(cellDimension.halfSideLength(), cellDimension.height() / 3.0);
            case SQUARE -> cellDimension.halfSideLength();
            case HEXAGON -> Math.min(cellDimension.halfWidth(), cellDimension.halfHeight());
        };
    }

    /**
     * Calculates the radius of a circle that fully encloses the cell.
     *
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return the radius of the enclosing circle
     */
    public static double computeCellOuterRadius(CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        return switch (shape) {
            case TRIANGLE ->
                    Math.sqrt(Math.pow(cellDimension.halfSideLength(), 2) + Math.pow(cellDimension.height() / 3.0, 2));
            case SQUARE -> cellDimension.halfSideLength() * Math.sqrt(2);
            case HEXAGON -> Math.max(cellDimension.halfWidth(), cellDimension.halfHeight());
        };
    }

    /**
     * Returns the bounding box dimensions of a cell based on its shape and dimensions.
     *
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return the width and height of the cell's bounding box
     */
    public static Dimension2D computeCellBoundingBox(CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        return switch (shape) {
            case TRIANGLE -> new Dimension2D(cellDimension.sideLength(), cellDimension.height());
            case SQUARE -> new Dimension2D(cellDimension.sideLength(), cellDimension.sideLength());
            case HEXAGON -> new Dimension2D(cellDimension.width(), cellDimension.height());
        };
    }

    /**
     * Returns the bounding rectangle of a cell in canvas coordinates.
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

        Point2D topLeft = toCanvasPosition(coordinate, cellDimension, shape);
        Dimension2D size = computeCellBoundingBox(cellDimension, shape);
        return new Rectangle2D(topLeft.getX(), topLeft.getY(), size.getWidth(), size.getHeight());
    }

    /**
     * Returns the x and y coordinates of the cell's polygon vertices in canvas space.
     *
     * @param coordinate the grid coordinate of the cell
     * @param cellDimension the dimensions of the cell
     * @param shape the shape of the cell
     * @return a 2D array: [0] = xPoints, [1] = yPoints
     */
    public static double[][] computePolygon(GridCoordinate coordinate, CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(cellDimension);
        Objects.requireNonNull(shape);

        return switch (shape) {
            case TRIANGLE -> {
                Point2D topLeft = toTriangleCanvasPosition(coordinate, cellDimension);
                double x = topLeft.getX();
                double y = topLeft.getY();
                boolean pointingDown = (coordinate.y() % 2) == 0;

                double[] xPoints = new double[3];
                double[] yPoints = new double[3];

                if (pointingDown) {
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

                yield new double[][]{xPoints, yPoints};
            }
            case SQUARE -> {
                Point2D topLeft = toCanvasPosition(coordinate, cellDimension, shape);
                double x = topLeft.getX();
                double y = topLeft.getY();
                double[] xPoints = {x, x + cellDimension.sideLength(), x + cellDimension.sideLength(), x};
                double[] yPoints = {y, y, y + cellDimension.sideLength(), y + cellDimension.sideLength()};
                yield new double[][]{xPoints, yPoints};
            }
            case HEXAGON -> {
                Point2D topLeft = toHexagonCanvasPosition(coordinate, cellDimension);
                double x = topLeft.getX();
                double y = topLeft.getY();

                double[] xPoints = new double[6];
                double[] yPoints = new double[6];

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

                yield new double[][]{xPoints, yPoints};
            }
        };
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

}
