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
 * A utility class for drawing grids and cells on a JavaFX Canvas.
 * The parameter {@code cellSideLength} defines the length of each side of the cell (TRIANGLE, SQUARE, HEXAGON) in pixels.
 *
 * @see de.mkalb.etpetssim.engine.CellShape
 * @see de.mkalb.etpetssim.engine.GridSize
 * @see de.mkalb.etpetssim.engine.GridCoordinate
 */
@SuppressWarnings("MagicNumber")
public final class FXGridCanvasPainter {

    private final Canvas canvas;
    private final GridStructure gridStructure;

    private final GraphicsContext gc;
    private final CellDimension cellDimension;
    private final Dimension2D gridDimension2D;

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

        // Store the graphics context of the canvas
        gc = canvas.getGraphicsContext2D();

        // Calculate cell and grid dimension based on the cell shape
        cellDimension = GridGeometryConverter.computeCellDimension(cellSideLength, gridStructure.cellShape());
        gridDimension2D = GridGeometryConverter.computeGridDimension(gridStructure.size(), cellDimension, gridStructure.cellShape());
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
     * Returns the GraphicsContext used for drawing on the canvas.
     *
     * @return the GraphicsContext for the canvas
     */
    public GraphicsContext graphicsContext2D() {
        return gc;
    }

    /**
     * Returns the cell dimensions in pixels.
     *
     * @return the cell dimensions in pixels, including width, height, and side length
     */
    public CellDimension cellDimension() {
        return cellDimension;
    }

    /**
     * Returns the pixel width and height of the entire grid area.
     *
     * @return the pixel dimensions of the grid area
     */
    public Dimension2D gridDimension2D() {
        return gridDimension2D;
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
        gc.fillRect(0.0d, 0.0d, gridDimension2D.getWidth(), gridDimension2D.getHeight());
    }

    /**
     * Fills a cell at the specified grid coordinate using the given fill color.
     * The appropriate fill method is selected based on the cell shape.
     *
     * @param coordinate the grid coordinate of the cell to fill
     * @param fillColor the color used to fill the cell
     */
    public void fillCell(GridCoordinate coordinate, Color fillColor) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);

        switch (gridStructure.cellShape()) {
            case TRIANGLE -> fillTriangle(coordinate, fillColor);
            case SQUARE -> fillSquare(coordinate, fillColor);
            case HEXAGON -> fillHexagon(coordinate, fillColor);
        }
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
                cellDimension,
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
            xPoints[1] = x + cellDimension.halfSideLength();
            yPoints[1] = y + cellDimension.height();

            // top right vertex
            xPoints[2] = x + cellDimension.sideLength();
            yPoints[2] = y;
        } else {
            // bottom left vertex
            xPoints[0] = x;
            yPoints[0] = y + cellDimension.height();

            // top middle vertex
            xPoints[1] = x + cellDimension.halfSideLength();
            yPoints[1] = y;

            // bottom right vertex
            xPoints[2] = x + cellDimension.sideLength();
            yPoints[2] = y + cellDimension.height();
        }

        gc.setFill(fillColor);
        gc.fillPolygon(xPoints, yPoints, vertexCount);
    }

    /**
     * Fills a triangle cell at the specified grid coordinate and optionally draws a border.
     * The border is drawn centered on the triangle edges and may extend slightly outside the cell.
     * If the line width is too large to fit visually, the triangle is filled entirely with the stroke color.
     * If the line width is zero or negative, only the fill color is used.
     *
     * @param coordinate the grid coordinate of the triangle cell
     * @param fillColor the color used to fill the triangle
     * @param strokeColor the color used for the triangle border
     * @param lineWidth the width of the border in pixels
     */
    public void fillAndStrokeTriangle(GridCoordinate coordinate, Color fillColor, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);
        Objects.requireNonNull(strokeColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(coordinate, cellDimension, gridStructure.cellShape());
        double x = topLeft.getX();
        double y = topLeft.getY();
        boolean pointingDown = ((coordinate.y() % 2) == 0);
        int vertexCount = 3;
        double[] xPoints = new double[vertexCount];
        double[] yPoints = new double[vertexCount];

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

        if (lineWidth < cellDimension.halfSideLength()) {
            gc.setFill(fillColor);
            gc.fillPolygon(xPoints, yPoints, vertexCount);
            if (lineWidth > 0.0d) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(lineWidth);
                gc.strokePolygon(xPoints, yPoints, vertexCount);
            }
        } else {
            gc.setFill(strokeColor);
            gc.fillPolygon(xPoints, yPoints, vertexCount);
        }
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
                cellDimension,
                gridStructure.cellShape());

        gc.setFill(fillColor);
        gc.fillRect(topLeft.getX(), topLeft.getY(), cellDimension.sideLength(), cellDimension.sideLength());
    }

    /**
     * Fills a square cell at the specified grid coordinate and optionally draws a border.
     * The border is drawn centered on the square edges and may extend slightly outside the cell.
     * If the line width is too large to fit visually, the square is filled entirely with the stroke color.
     * If the line width is zero or negative, only the fill color is used.
     *
     * @param coordinate the grid coordinate of the square cell
     * @param fillColor the color used to fill the square
     * @param strokeColor the color used for the square border
     * @param lineWidth the width of the border in pixels
     */
    public void fillAndStrokeSquare(GridCoordinate coordinate, Color fillColor, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);
        Objects.requireNonNull(strokeColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(coordinate, cellDimension, gridStructure.cellShape());
        double x = topLeft.getX();
        double y = topLeft.getY();
        double size = cellDimension.sideLength();

        if (lineWidth < cellDimension.halfSideLength()) {
            gc.setFill(fillColor);
            gc.fillRect(x, y, size, size);
            if (lineWidth > 0.0d) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(lineWidth);
                gc.strokeRect(x, y, size, size); // centered stroke
            }
        } else {
            gc.setFill(strokeColor);
            gc.fillRect(x, y, size, size);
        }
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
    public void fillAndStrokeSquareInset(GridCoordinate coordinate, Color fillColor, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);
        Objects.requireNonNull(strokeColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(
                coordinate,
                cellDimension,
                gridStructure.cellShape());

        if (lineWidth < cellDimension.halfSideLength()) {
            gc.setFill(fillColor);
            gc.fillRect(topLeft.getX(), topLeft.getY(), cellDimension.sideLength(), cellDimension.sideLength());

            if (lineWidth > 0.0d) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(lineWidth);
                double half = lineWidth / 2.0d;
                gc.strokeRect(topLeft.getX() + half, topLeft.getY() + half, cellDimension.sideLength() - lineWidth, cellDimension.sideLength() - lineWidth);
            }
        } else {
            gc.setFill(strokeColor);
            gc.fillRect(topLeft.getX(), topLeft.getY(), cellDimension.sideLength(), cellDimension.sideLength());
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
                cellDimension,
                gridStructure.cellShape());

        double x = topLeft.getX();
        double y = topLeft.getY();

        int vertexCount = gridStructure.cellShape().vertexCount();
        double[] xPoints = new double[vertexCount];
        double[] yPoints = new double[vertexCount];

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

        gc.setFill(fillColor);
        gc.fillPolygon(xPoints, yPoints, vertexCount);
    }

    /**
     * Fills a hexagon cell at the specified grid coordinate and optionally draws a border.
     * The border is drawn centered on the hexagon edges and may extend slightly outside the cell.
     * If the line width is too large to fit visually, the hexagon is filled entirely with the stroke color.
     * If the line width is zero or negative, only the fill color is used.
     *
     * @param coordinate the grid coordinate of the hexagon cell
     * @param fillColor the color used to fill the hexagon
     * @param strokeColor the color used for the hexagon border
     * @param lineWidth the width of the border in pixels
     */
    public void fillAndStrokeHexagon(GridCoordinate coordinate, Color fillColor, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(fillColor);
        Objects.requireNonNull(strokeColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(coordinate, cellDimension, gridStructure.cellShape());
        double x = topLeft.getX();
        double y = topLeft.getY();
        int vertexCount = 6;
        double[] xPoints = new double[vertexCount];
        double[] yPoints = new double[vertexCount];

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

        if (lineWidth < cellDimension.halfSideLength()) {
            gc.setFill(fillColor);
            gc.fillPolygon(xPoints, yPoints, vertexCount);
            if (lineWidth > 0.0d) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(lineWidth);
                gc.strokePolygon(xPoints, yPoints, vertexCount);
            }
        } else {
            gc.setFill(strokeColor);
            gc.fillPolygon(xPoints, yPoints, vertexCount);
        }
    }

    // --- CIRCLE ---

    /**
     * Draws a circle centered inside the specified cell.
     * The circle fits entirely within the cell's shape.
     *
     * @param coordinate the grid coordinate of the cell
     * @param strokeColor the color used to draw the circle
     * @param lineWidth the width of the circle's stroke
     */
    public void drawInnerCircle(GridCoordinate coordinate, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(strokeColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(coordinate, cellDimension, gridStructure.cellShape());
        double centerX;
        double centerY;
        double radius;

        switch (gridStructure.cellShape()) {
            case SQUARE -> {
                centerX = topLeft.getX() + cellDimension.halfSideLength();
                centerY = topLeft.getY() + cellDimension.halfSideLength();
                radius = cellDimension.halfSideLength();
            }
            case HEXAGON -> {
                centerX = topLeft.getX() + cellDimension.halfWidth();
                centerY = topLeft.getY() + cellDimension.halfHeight();
                radius = Math.min(cellDimension.halfWidth(), cellDimension.halfHeight());
            }
            case TRIANGLE -> {
                boolean pointingDown = (coordinate.y() % 2) == 0;
                centerX = topLeft.getX() + cellDimension.halfSideLength();
                centerY = pointingDown
                        ? (topLeft.getY() + ((1.0 / 3.0) * cellDimension.height()))
                        : (topLeft.getY() + ((2.0 / 3.0) * cellDimension.height()));
                radius = Math.min(cellDimension.halfSideLength(), cellDimension.height() / 3.0);
            }
            default -> throw new IllegalStateException("Unexpected cell shape: " + gridStructure.cellShape());
        }

        gc.setStroke(strokeColor);
        gc.setLineWidth(lineWidth);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    /**
     * Draws a circle that fully encloses the specified cell.
     * The circle's radius is large enough to surround the cell's shape.
     *
     * @param coordinate the grid coordinate of the cell
     * @param strokeColor the color used to draw the circle
     * @param lineWidth the width of the circle's stroke
     */
    public void drawOuterCircle(GridCoordinate coordinate, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(strokeColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(coordinate, cellDimension, gridStructure.cellShape());
        double centerX;
        double centerY;
        double radius;

        switch (gridStructure.cellShape()) {
            case SQUARE -> {
                centerX = topLeft.getX() + cellDimension.halfSideLength();
                centerY = topLeft.getY() + cellDimension.halfSideLength();
                radius = cellDimension.halfSideLength() * Math.sqrt(2); // diagonal radius
            }
            case HEXAGON -> {
                centerX = topLeft.getX() + cellDimension.halfWidth();
                centerY = topLeft.getY() + cellDimension.halfHeight();
                radius = Math.max(cellDimension.halfWidth(), cellDimension.halfHeight());
            }
            case TRIANGLE -> {
                boolean pointingDown = (coordinate.y() % 2) == 0;
                centerX = topLeft.getX() + cellDimension.halfSideLength();
                centerY = pointingDown
                        ? (topLeft.getY() + ((1.0 / 3.0) * cellDimension.height()))
                        : (topLeft.getY() + ((2.0 / 3.0) * cellDimension.height()));
                radius = Math.sqrt(Math.pow(cellDimension.halfSideLength(), 2) + Math.pow(cellDimension.height() / 3.0, 2));
            }
            default -> throw new IllegalStateException("Unexpected cell shape: " + gridStructure.cellShape());
        }

        gc.setStroke(strokeColor);
        gc.setLineWidth(lineWidth);
        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    // --- RECTANGLE ---

    /**
     * Draws a bounding rectangle that fully encloses the specified cell.
     * The rectangle adapts to the cell shape and its bounding box.
     *
     * @param coordinate the grid coordinate of the cell
     * @param strokeColor the color used to draw the rectangle
     * @param lineWidth the width of the rectangle's stroke
     */
    public void drawBoundingBox(GridCoordinate coordinate, Color strokeColor, double lineWidth) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(strokeColor);

        Point2D topLeft = GridGeometryConverter.toCanvasPosition(coordinate, cellDimension, gridStructure.cellShape());
        double x = topLeft.getX();
        double y = topLeft.getY();
        double width;
        double height;

        switch (gridStructure.cellShape()) {
            case SQUARE -> {
                width = cellDimension.sideLength();
                height = cellDimension.sideLength();
            }
            case HEXAGON -> {
                width = cellDimension.width();
                height = cellDimension.height();
            }
            case TRIANGLE -> {
                width = cellDimension.sideLength();
                height = cellDimension.height();
            }
            default -> throw new IllegalStateException("Unexpected cell shape: " + gridStructure.cellShape());
        }

        gc.setStroke(strokeColor);
        gc.setLineWidth(lineWidth);
        gc.strokeRect(x, y, width, height);
    }

}
