package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import javafx.geometry.Dimension2D;
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
 * @see de.mkalb.etpetssim.engine.GridCoordinate
 */
public final class FXGridCanvasPainter {

    private final Canvas canvas;
    private final GridStructure gridStructure;
    private final double cellSideLength;

    private final GraphicsContext gc;
    private final Dimension2D cellDimension;
    private final Dimension2D gridDimension;

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
        if (cellSideLength < 1.0d) {
            throw new IllegalArgumentException("Cell side length must be at least 1 pixel.");
        }

        this.canvas = canvas;
        this.gridStructure = gridStructure;
        this.cellSideLength = cellSideLength;

        // Store the graphics context of the canvas
        gc = canvas.getGraphicsContext2D();

        // Calculate cell and grid dimension based on the cell shape
        cellDimension = GridGeometryConverter.calculateCellDimension(cellSideLength, gridStructure.cellShape());
        gridDimension = GridGeometryConverter.calculateGridDimension(gridStructure.size(), cellDimension, gridStructure.cellShape());
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
     * Returns the pixel width and height of a single cell in the grid.
     *
     * @return the pixel dimensions of a single cell in the grid
     */
    public Dimension2D cellDimension() {
        return cellDimension;
    }

    /**
     * Returns the pixel width and height of the entire grid area.
     *
     * @return the pixel dimensions of the grid area
     */
    public Dimension2D gridDimension() {
        return gridDimension;
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
     * Fills the entire canvas background with the specified color.
     *
     * @param fillColor the color used to fill the canvas background
     */
    public void fillCanvasBackground(Color fillColor) {
        Objects.requireNonNull(fillColor);

        gc.setFill(fillColor);
        gc.fillRect(0.0d, 0.0d, canvas.getWidth(), canvas.getHeight());
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
        gc.fillRect(0.0d, 0.0d, gridDimension.getWidth(), gridDimension.getHeight());
    }

    // --- TRIANGLE ---

    /**
     * Fills a triangle cell at the specified grid coordinate using the given fill color.
     *
     * @param coordinate the grid coordinate of the cell to draw
     * @param fillColor the color used to fill the cell
     */
    public void fillTriangle(GridCoordinate coordinate, Color fillColor) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(
                coordinate,
                cellSideLength,
                cellDimension.getHeight(),
                gridStructure.cellShape());
        double x = topLeft.getX();
        double y = topLeft.getY();

        boolean pointingDown = ((coordinate.y() % 2) == 0);

        int vertexCount = gridStructure.cellShape().vertexCount();
        double[] xPoints = new double[vertexCount];
        double[] yPoints = new double[vertexCount];

        if (pointingDown) {
            // top left vertex
            xPoints[0] = x;
            yPoints[0] = y;

            // bottom middle vertex
            xPoints[1] = x + (cellSideLength / 2);
            yPoints[1] = y + cellDimension.getHeight();

            // top right vertex
            xPoints[2] = x + cellSideLength;
            yPoints[2] = y;
        } else {
            // bottom left vertex
            xPoints[0] = x;
            yPoints[0] = y + cellDimension.getHeight();

            // top middle vertex
            xPoints[1] = x + (cellSideLength / 2);
            yPoints[1] = y;

            // bottom right vertex
            xPoints[2] = x + cellSideLength;
            yPoints[2] = y + cellDimension.getHeight();
        }

        gc.setFill(fillColor);
        gc.fillPolygon(xPoints, yPoints, vertexCount);
    }

    // --- SQUARE ---

    /**
     * Fills a square cell at the specified grid coordinate using the given fill color.
     *
     * @param coordinate the grid coordinate of the cell to fill
     * @param fillColor the color used to fill the cell
     */
    public void fillSquare(GridCoordinate coordinate, Color fillColor) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(
                coordinate,
                cellSideLength,
                cellDimension.getHeight(),
                gridStructure.cellShape());

        gc.setFill(fillColor);
        gc.fillRect(topLeft.getX(), topLeft.getY(), cellSideLength, cellSideLength);
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

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(
                coordinate,
                cellSideLength,
                cellDimension.getHeight(),
                gridStructure.cellShape());

        if (lineWidth < (cellSideLength / 2.0d)) {
            gc.setFill(fillColor);
            gc.fillRect(topLeft.getX(), topLeft.getY(), cellSideLength, cellSideLength);

            if (lineWidth > 0.0d) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(lineWidth);
                double half = lineWidth / 2.0d;
                gc.strokeRect(topLeft.getX() + half, topLeft.getY() + half, cellSideLength - lineWidth, cellSideLength - lineWidth);
            }
        } else {
            gc.setFill(strokeColor);
            gc.fillRect(topLeft.getX(), topLeft.getY(), cellSideLength, cellSideLength);
        }
    }

    // --- HEXAGON ---

    /**
     * Fills a hexagon cell at the specified grid coordinate using the given fill color.
     *
     * @param coordinate the grid coordinate of the cell to draw
     * @param fillColor the color used to fill the cell
     */
    public void fillHexagon(GridCoordinate coordinate, Color fillColor) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(
                coordinate,
                cellSideLength,
                cellDimension.getHeight(),
                gridStructure.cellShape());

        double x = topLeft.getX();
        double y = topLeft.getY();

        int vertexCount = gridStructure.cellShape().vertexCount();
        double[] xPoints = new double[vertexCount];
        double[] yPoints = new double[vertexCount];

        xPoints[0] = x + (cellSideLength * 0.5d);
        yPoints[0] = y;

        xPoints[1] = x + (cellSideLength * 1.5d);
        yPoints[1] = y;

        xPoints[2] = x + cellDimension.getWidth();
        yPoints[2] = y + (cellDimension.getHeight() * 0.5d);

        xPoints[3] = x + (cellSideLength * 1.5d);
        yPoints[3] = y + cellDimension.getHeight();

        xPoints[4] = x + (cellSideLength * 0.5d);
        yPoints[4] = y + cellDimension.getHeight();

        xPoints[5] = x;
        yPoints[5] = y + (cellDimension.getHeight() * 0.5d);

        gc.setFill(fillColor);
        gc.fillPolygon(xPoints, yPoints, vertexCount);
    }

}
