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

    /**
     * Calculates the dimensions of a cell based on its side length and shape.
     *
     * @param cellSideLength the length of each side of the cell in pixels
     * @param shape the shape of the cell
     * @return a CellDimension object representing the dimensions of the cell
     */
    public static CellDimension calculateCellDimension(double cellSideLength, CellShape shape) {
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
        return CellDimension.of(cellSideLength, width, height);
    }

    /**
     * Calculates the total pixel dimensions of the grid area based on size, cell dimension and shape.
     *
     * @param gridSize the size of the grid in terms of columns and rows
     * @param cellDimension the dimensions of a single cell in the grid
     * @param  shape the shape of the cells in the grid
     */
    public static Dimension2D calculateGridDimension(GridSize gridSize, CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(gridSize);
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
        Objects.requireNonNull(shape);
        return switch (shape) {
            case TRIANGLE -> toTriangleCanvasPosition(coordinate, cellDimension);
            case SQUARE -> toSquareCanvasPosition(coordinate, cellDimension);
            case HEXAGON -> toHexagonCanvasPosition(coordinate, cellDimension);
        };
    }

    public static GridCoordinate fromCanvasPosition(Point2D point, CellDimension cellDimension, CellShape shape) {
        Objects.requireNonNull(point);
        Objects.requireNonNull(shape);
        return switch (shape) {
            case TRIANGLE -> fromTriangleCanvasPosition(point, cellDimension);
            case SQUARE -> fromSquareCanvasPosition(point, cellDimension);
            case HEXAGON -> fromHexagonCanvasPosition(point, cellDimension);
        };
    }

    // --- TRIANGLE ---
    public static Point2D toTriangleCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension) {
        Objects.requireNonNull(coordinate);

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

        int row = (int) (point.getY() / cellDimension.height()) * 2; // Logical row
        double xOffset = (((row % 4) == 1) || ((row % 4) == 2)) ? cellDimension.halfSideLength() : 0.0d;
        int column = (int) ((point.getX() - xOffset) / cellDimension.sideLength());

        return new GridCoordinate(column, row);
    }

    // --- SQUARE ---
    public static Point2D toSquareCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension) {
        Objects.requireNonNull(coordinate);
        double x = coordinate.x() * cellDimension.width();
        double y = coordinate.y() * cellDimension.height();
        return new Point2D(x, y);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public static GridCoordinate fromSquareCanvasPosition(Point2D point, CellDimension cellDimension) {
        Objects.requireNonNull(point);
        int x = (int) (point.getX() / cellDimension.width());
        int y = (int) (point.getY() / cellDimension.height());
        return new GridCoordinate(x, y);
    }

    // --- HEXAGON ---
    public static Point2D toHexagonCanvasPosition(GridCoordinate coordinate, CellDimension cellDimension) {
        Objects.requireNonNull(coordinate);

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

        int column = (int) (point.getX() / (cellDimension.sideLength() * 1.5d));
        double yOffset = ((column % 2) == 0) ? 0.0d : cellDimension.halfHeight();
        int row = (int) ((point.getY() - yOffset) / cellDimension.height());

        return new GridCoordinate(column, row);
    }

}
