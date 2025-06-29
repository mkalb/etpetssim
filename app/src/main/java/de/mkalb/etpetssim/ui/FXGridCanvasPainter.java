package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * A utility class for drawing grids on a JavaFX Canvas.
 * The parameter {@code cellSideLength} defines the length of each side of the cell (TRIANGLE, SQUARE, HEXAGON) in pixels.
 *
 * @see de.mkalb.etpetssim.engine.CellShape
 * @see de.mkalb.etpetssim.engine.GridSize
 * @see de.mkalb.etpetssim.engine.GridTopology
 */
public final class FXGridCanvasPainter {

    // commonly used double constants for calculations
    private static final double ZERO = 0.0d;
    private static final double ONE = 1.0d;
    private static final double TWO = 2.0d;

    // constructor parameters
    private final Canvas canvas;
    private final GridStructure gridStructure;
    private final double cellSideLength;

    //
    private final GraphicsContext gc;
    private final double gridPixelWidth;
    private final double gridPixelHeight;

    /**
     * Creates a new FXGridCanvasPainter instance.
     *
     * @param canvas the canvas to draw on
     * @param gridStructure the structure of the grid, defining its cell shape and size
     * @param cellSideLength the length of each side of the cell in pixels
     */
    public FXGridCanvasPainter(Canvas canvas, GridStructure gridStructure, double cellSideLength) {
        Objects.requireNonNull(canvas);
        Objects.requireNonNull(gridStructure);
        if (cellSideLength < 1) {
            throw new IllegalArgumentException("Cell side length must be at least 1 pixel.");
        }

        this.canvas = canvas;
        this.gridStructure = gridStructure;
        this.cellSideLength = cellSideLength;

        gc = canvas.getGraphicsContext2D();
        gridPixelWidth = gridStructure.size().width() * cellSideLength;
        gridPixelHeight = gridStructure.size().height() * cellSideLength;
    }

    /**
     * Returns the JavaFX Canvas on which the grid will be drawn.
     *
     * @return the canvas used for drawing the grid
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * Returns the structure of the grid, defining its cell shape and size.
     *
     * @return the structure of the grid, defining its cell shape and size
     */
    public GridStructure gridStructure() {
        return gridStructure;
    }

    /**
     * Returns the length of each side of the cell in pixels.
     * The value is at least 1 pixel.
     *
     * @return the length of each side of the cell in pixels
     */
    public double cellSideLength() {
        return cellSideLength;
    }

    /**
     * Returns the GraphicsContext used for drawing on the canvas.
     *
     * @return the GraphicsContext for the canvas
     */
    public GraphicsContext graphicsContext2D() {
        return gc;
    }

    /**
     * Returns the width of the grid area in pixels.
     *
     * @return the width of the grid area in pixels.
     */
    public double gridPixelWidth() {
        return gridPixelWidth;
    }

    /**
     * Returns the height of the grid area in pixels.
     *
     * @return the height of the grid area in pixels.
     */
    public double gridPixelHeight() {
        return gridPixelHeight;
    }

    /**
     * Checks whether the given grid coordinate lies outside the drawable grid area.
     * If a drawing method is called with a coordinate outside the grid, the result may be invisible,
     * clipped, or cause unexpected rendering behavior depending on the canvas size and context.
     *
     * @param coordinate the grid coordinate to check
     * @return true if the coordinate is outside the grid area, false otherwise
     * @see de.mkalb.etpetssim.engine.GridStructure#isCoordinateValid(de.mkalb.etpetssim.engine.GridCoordinate)
     */
    public boolean isOutsideGrid(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);

        return !gridStructure.isCoordinateValid(coordinate);
    }

    /**
     * Converts a grid coordinate to the top-left canvas position for a square cell in pixels.
     * This method is only valid for square cell shapes.
     *
     * @param coordinate the grid coordinate to convert
     * @return a Point2D representing the top-left canvas position
     */
    public Point2D toSquareCanvasPosition(GridCoordinate coordinate) {
        Objects.requireNonNull(coordinate);

        double x = coordinate.x() * cellSideLength;
        double y = coordinate.y() * cellSideLength;
        return new Point2D(x, y);
    }

    /**
     * Fills the entire canvas background with the specified color.
     *
     * @param fillColor the color used to fill the canvas background
     */
    public void fillCanvasBackground(Color fillColor) {
        Objects.requireNonNull(fillColor);

        gc.setFill(fillColor);
        gc.fillRect(ZERO, ZERO, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Fills the background of the grid area with the specified color.
     * The grid area is defined by the grid size and cell side length.
     *
     * @param fillColor the color used to fill the grid background
     */
    public void fillGridBackground(Color fillColor) {
        Objects.requireNonNull(fillColor);

        gc.setFill(fillColor);
        gc.fillRect(ZERO, ZERO, gridPixelWidth, gridPixelHeight);
    }

    /**
     * Fills a square cell at the specified grid coordinate using the given fill color.
     * The cell is drawn as a square with side length defined by {@code cellSideLength}.
     *
     * @param coordinate the grid coordinate of the cell to fill
     * @param fillColor the color used to fill the square
     */
    public void fillSquare(GridCoordinate coordinate, Color fillColor) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);

        Point2D pos = toSquareCanvasPosition(coordinate);
        gc.setFill(fillColor);
        gc.fillRect(pos.getX(), pos.getY(), cellSideLength, cellSideLength);
    }

    /**
     * Fills a square cell at the specified grid coordinate and optionally draws a border inside the cell.
     * If the line width is too large to fit inside the cell, the cell is filled entirely with the stroke color.
     * If the line width is zero or negative, only the fill color is used.
     *
     * @param coordinate the grid coordinate of the cell to draw
     * @param fillColor the color used to fill the square
     * @param strokeColor the color used for the border
     * @param lineWidth the width of the border in pixels
     */
    public void fillAndStrokeSquare(GridCoordinate coordinate, Color fillColor, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);
        Objects.requireNonNull(strokeColor);

        Point2D pos = toSquareCanvasPosition(coordinate);

        if (lineWidth < (cellSideLength / TWO)) {
            gc.setFill(fillColor);
            gc.fillRect(pos.getX(), pos.getY(), cellSideLength, cellSideLength);

            if (lineWidth > ZERO) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(lineWidth);
                double half = lineWidth / TWO;
                gc.strokeRect(pos.getX() + half, pos.getY() + half, cellSideLength - lineWidth, cellSideLength - lineWidth);
            }
        } else {
            gc.setFill(strokeColor);
            gc.fillRect(pos.getX(), pos.getY(), cellSideLength, cellSideLength);
        }
    }

}
