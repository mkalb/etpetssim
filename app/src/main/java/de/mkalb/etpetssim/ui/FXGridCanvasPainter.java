package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
    private final Text textHelper;

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

        // Compute cell and grid dimension based on the cell shape
        cellDimension = GridGeometry.computeCellDimension(cellSideLength, gridStructure.cellShape());
        gridDimension2D = GridGeometry.computeGridDimension(gridStructure.size(), cellDimension, gridStructure.cellShape());

        // Instance for reusable text measurement
        textHelper = new Text();
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
     * Clear the entire canvas background.
     */
    public void clearCanvasBackground() {
        gc.clearRect(0.0d, 0.0d, canvas.getWidth(), canvas.getHeight());
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
     * CLear the background of the grid area.
     * The grid area is defined by the grid size and cell side length.
     */
    public void clearGridBackground() {
        gc.clearRect(0.0d, 0.0d, gridDimension2D.getWidth(), gridDimension2D.getHeight());
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

        /*
         * Possible optimization for small square. Evaluate later.
         * PixelWriter pw = gc.getPixelWriter();
         * pw.setColor(x, y, color);
         * pw.setColor(x + 1, y, color);
         * pw.setColor(x, y + 1, color);
         * pw.setColor(x + 1, y + 1, color);
         */

        double[][] polygon = GridGeometry.computeCellPolygon(coordinate, cellDimension, gridStructure.cellShape());

        gc.setFill(fillColor);
        gc.fillPolygon(polygon[0], polygon[1], gridStructure.cellShape().vertexCount());
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

        Point2D topLeft = GridGeometry.toCanvasPosition(coordinate, cellDimension, gridStructure.cellShape());

        if (lineWidth < cellDimension.halfSideLength()) {
            gc.setFill(fillColor);
            gc.fillRect(topLeft.getX(), topLeft.getY(), cellDimension.sideLength(), cellDimension.sideLength());

            if (lineWidth > 0.0d) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(lineWidth);
                double halfLineWidth = lineWidth / 2.0d;
                gc.strokeRect(topLeft.getX() + halfLineWidth, topLeft.getY() + halfLineWidth,
                        cellDimension.sideLength() - lineWidth, cellDimension.sideLength() - lineWidth);
            }
        } else {
            gc.setFill(strokeColor);
            gc.fillRect(topLeft.getX(), topLeft.getY(), cellDimension.sideLength(), cellDimension.sideLength());
        }
    }

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

        Point2D center = GridGeometry.computeCellCenter(coordinate, cellDimension, gridStructure.cellShape());
        double radius = cellDimension.innerRadius();
        double diameter = radius * 2;

        gc.setStroke(strokeColor);
        gc.setLineWidth(lineWidth);
        gc.strokeOval(center.getX() - radius, center.getY() - radius, diameter, diameter);
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

        Point2D center = GridGeometry.computeCellCenter(coordinate, cellDimension, gridStructure.cellShape());
        double radius = cellDimension.outerRadius();
        double diameter = radius * 2;

        gc.setStroke(strokeColor);
        gc.setLineWidth(lineWidth);
        gc.strokeOval(center.getX() - radius, center.getY() - radius, diameter, diameter);
    }

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

        Rectangle2D bounds = GridGeometry.computeCellBounds(coordinate, cellDimension, gridStructure.cellShape());

        gc.setStroke(strokeColor);
        gc.setLineWidth(lineWidth);
        gc.strokeRect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    /**
     * Draws text centered within the specified cell.
     *
     * @param coordinate the grid coordinate of the cell
     * @param text the text to draw
     * @param textColor the color of the text
     * @param font the font used for rendering the text
     */
    public void drawCenteredTextInCell(GridCoordinate coordinate, String text, Color textColor, Font font) {
        Objects.requireNonNull(coordinate);
        Objects.requireNonNull(text);
        Objects.requireNonNull(textColor);
        Objects.requireNonNull(font);

        Point2D center = GridGeometry.computeCellCenter(coordinate, cellDimension, gridStructure.cellShape());
        Point2D textOffset = computeCenteredTextOffset(text, font);

        gc.setFill(textColor);
        gc.setFont(font);
        gc.fillText(text,
                center.getX() + textOffset.getX(),
                center.getY() + textOffset.getY()
        );
    }

    /**
     * Computes the width and height of the given text when rendered with the specified font.
     * It is not thread-safe and should not be called from multiple threads simultaneously.
     *
     * @param text the text to measure
     * @param font the font used for rendering the text
     * @return the width and height of the text as a Dimension2D object
     */
    Dimension2D computeTextDimension(String text, Font font) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(font);

        textHelper.setText(text);
        textHelper.setFont(font);
        double textWidth = textHelper.getLayoutBounds().getWidth();
        double textHeight = textHelper.getLayoutBounds().getHeight();
        return new Dimension2D(textWidth, textHeight);
    }

    /**
     * Computes the offset needed to center the given text.
     * It is not thread-safe and should not be called from multiple threads simultaneously.
     *
     * @param text the text to center
     * @param font the font used for rendering the text
     * @return the offset as a Point2D object, where x is the horizontal offset and y is the vertical offset
     */
    Point2D computeCenteredTextOffset(String text, Font font) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(font);

        textHelper.setText(text);
        textHelper.setFont(font);
        double textWidth = textHelper.getLayoutBounds().getWidth();
        double textHeight = textHelper.getLayoutBounds().getHeight();
        double baselineOffset = textHelper.getBaselineOffset();
        double xOffset = -(textWidth / 2.0d);
        double yOffset = -(textHeight / 2.0d) + baselineOffset;
        return new Point2D(xOffset, yOffset);
    }

}
