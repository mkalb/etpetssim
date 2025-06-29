package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridSize;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class GridGeometryConverter {

    /**
     * Private constructor to prevent instantiation.
     */
    private GridGeometryConverter() {
    }

    public static Dimension2D calculateCellDimension(double cellSideLength, CellShape shape) {
        Objects.requireNonNull(shape);

        double width = switch (shape) {
            case TRIANGLE, SQUARE -> cellSideLength;
            case HEXAGON -> cellSideLength * 2; // Hexagon width is twice the side length
        };
        double height = switch (shape) {
            case TRIANGLE -> (Math.sqrt(3) / 2) * cellSideLength;
            case SQUARE -> cellSideLength;
            case HEXAGON -> Math.sqrt(3) * cellSideLength;
        };
        return new Dimension2D(width, height);
    }

    public static Dimension2D calculateGridDimension(GridSize gridSize, Dimension2D cellDimension, CellShape shape) {
        Objects.requireNonNull(gridSize);
        Objects.requireNonNull(shape);

        double width;
        double height;

        switch (shape) {
            case TRIANGLE -> {
                width = (gridSize.width() * cellDimension.getWidth()) + (cellDimension.getWidth() / 2);
                int rows = (gridSize.height() + 1) / 2; // Two triangles per logical row. Round up for odd heights.
                height = rows * cellDimension.getHeight();
            }
            case SQUARE -> {
                width = gridSize.width() * cellDimension.getWidth();
                height = gridSize.height() * cellDimension.getHeight();
            }
            case HEXAGON -> {
                double columnWidth = cellDimension.getWidth() * 0.75d;
                double rowHeightOffset = cellDimension.getHeight() * 0.5d;

                width = (gridSize.width() * columnWidth) + (cellDimension.getWidth() * 0.25d);
                height = (gridSize.height() * cellDimension.getHeight()) + ((gridSize.width() > 1) ? rowHeightOffset : 0.0d);
            }
            default -> throw new IllegalArgumentException("Unsupported CellShape: " + shape);
        }

        return new Dimension2D(width, height);
    }

    public static Point2D toCanvasPosition(GridCoordinate coordinate, double cellSideLength, double cellHeight, CellShape shape) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(shape);
        return switch (shape) {
            case TRIANGLE -> toTriangleCanvasPosition(coordinate, cellSideLength, cellHeight);
            case SQUARE -> toSquareCanvasPosition(coordinate, cellSideLength);
            case HEXAGON -> toHexagonCanvasPosition(coordinate, cellSideLength, cellHeight);
        };
    }

    public static GridCoordinate fromCanvasPosition(Point2D point, double cellSideLength, double cellHeight, CellShape shape) {
        Objects.requireNonNull(point);
        Objects.requireNonNull(shape);
        return switch (shape) {
            case TRIANGLE -> fromTriangleCanvasPosition(point, cellSideLength, cellHeight);
            case SQUARE -> fromSquareCanvasPosition(point, cellSideLength);
            case HEXAGON -> fromHexagonCanvasPosition(point, cellSideLength, cellHeight);
        };
    }

    // --- TRIANGLE ---
    public static Point2D toTriangleCanvasPosition(GridCoordinate coordinate, double cellSideLength, double cellHeight) {
        Objects.requireNonNull(coordinate);

        // In a flat-top triangle grid, every two rows form a repeating vertical pattern.
        // Depending on the row's position in the 4-row cycle, some rows are horizontally offset by half a cell.
        // This ensures that upward- and downward-pointing triangles interlock correctly.
        int triangleOrientationCycle = coordinate.y() % 4;
        boolean isOffsetTriangleRow = ((triangleOrientationCycle == 1) || (triangleOrientationCycle == 2));
        double xOffset = isOffsetTriangleRow ? (cellSideLength * 0.5d) : 0.0d;
        double x = (coordinate.x() * cellSideLength) + xOffset;

        // Each logical row consists of two triangle rows stacked vertically.
        // The vertical position is based on the row index multiplied by the triangle height.
        int row = coordinate.y() / 2; // Round down
        double y = row * cellHeight;

        // The resulting point represents the top-left corner of the bounding box for the triangle.
        return new Point2D(x, y);
    }

    public static GridCoordinate fromTriangleCanvasPosition(Point2D point, double cellSideLength, double cellHeight) {
        Objects.requireNonNull(point);

        int row = (int) (point.getY() / cellHeight) * 2; // Logical row
        double xOffset = (((row % 4) == 1) || ((row % 4) == 2)) ? (cellSideLength * 0.5d) : 0.0d;
        int column = (int) ((point.getX() - xOffset) / cellSideLength);

        return new GridCoordinate(column, row);
    }

    // --- SQUARE ---
    public static Point2D toSquareCanvasPosition(GridCoordinate coordinate, double cellSideLength) {
        Objects.requireNonNull(coordinate);
        double x = coordinate.x() * cellSideLength;
        double y = coordinate.y() * cellSideLength;
        return new Point2D(x, y);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static GridCoordinate fromSquareCanvasPosition(Point2D point, double cellSideLength) {
        Objects.requireNonNull(point);
        int x = (int) (point.getX() / cellSideLength);
        int y = (int) (point.getY() / cellSideLength);
        return new GridCoordinate(x, y);
    }

    // --- HEXAGON ---
    public static Point2D toHexagonCanvasPosition(GridCoordinate coordinate, double cellSideLength, double cellHeight) {
        Objects.requireNonNull(coordinate);

        // In a flat-top hexagon grid, each column is spaced 1.5 times the side length apart.
        // This accounts for the horizontal overlap between adjacent hexagons.
        double x = coordinate.x() * cellSideLength * 1.5d;

        // Hexagon rows are vertically offset in every second column to create a staggered layout.
        // Even columns start at the base Y position, odd columns are shifted down by half a hexagon height.
        double y;
        if ((coordinate.x() % 2) == 0) {
            y = coordinate.y() * cellHeight;
        } else {
            y = (coordinate.y() * cellHeight) + (cellHeight / 2);
        }

        // The resulting point represents the top-left corner of the bounding box for the hexagon.
        return new Point2D(x, y);
    }

    public static GridCoordinate fromHexagonCanvasPosition(Point2D point, double cellSideLength, double cellHeight) {
        Objects.requireNonNull(point);

        int column = (int) (point.getX() / (cellSideLength * 1.5d));
        double yOffset = ((column % 2) == 0) ? 0.0d : (cellHeight / 2);
        int row = (int) ((point.getY() - yOffset) / cellHeight);

        return new GridCoordinate(column, row);
    }

}
