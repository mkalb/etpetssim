package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import javafx.geometry.Point2D;

import java.util.*;

public final class GridCoordinateConverter {

    private static final double ZERO = 0.0d;
    private static final double ONE_HALF = 0.5d;
    private static final double THREE_HALVES = 1.5d;

    /**
     * Private constructor to prevent instantiation.
     */
    private GridCoordinateConverter() {
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
        double xOffset = isOffsetTriangleRow ? (cellSideLength * ONE_HALF) : ZERO;
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
        throw new UnsupportedOperationException("Not yet implemented");
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
        double x = coordinate.x() * cellSideLength * THREE_HALVES;

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
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
