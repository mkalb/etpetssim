package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jspecify.annotations.Nullable;

/**
 * A utility class for drawing grids, cells, polygons and text on a JavaFX Canvas.
 * The parameter {@code cellEdgeLength} defines the length of each edge of the cell (TRIANGLE, SQUARE, HEXAGON)
 * in pixels.
 *
 * @see de.mkalb.etpetssim.engine.CellShape
 * @see de.mkalb.etpetssim.engine.GridSize
 * @see de.mkalb.etpetssim.engine.GridCoordinate
 */
@SuppressWarnings("MagicNumber")
public final class FXGridCanvasPainter {

    private final Canvas canvas;
    private final GridStructure structure;

    private final GraphicsContext gc;
    private final CellDimension cellDimension;
    private final Dimension2D gridDimension2D;
    private final Text textHelper;

    /**
     * Creates a new FXGridCanvasPainter instance.
     *
     * @param canvas the canvas to draw on
     * @param structure the structure of the grid, defining its cell shape and size
     * @param cellEdgeLength the length of each edge of the cell in pixels
     */
    public FXGridCanvasPainter(Canvas canvas, GridStructure structure, double cellEdgeLength) {
        if (cellEdgeLength < 1.0d) {
            throw new IllegalArgumentException("Cell edge length must be at least 1 pixel.");
        }

        this.canvas = canvas;
        this.structure = structure;

        // Store the graphics context of the canvas
        gc = canvas.getGraphicsContext2D();

        // Compute cell and grid dimension based on the cell shape
        cellDimension = GridGeometry.computeCellDimension(cellEdgeLength, structure.cellShape());
        gridDimension2D = GridGeometry.computeGridDimension(structure.size(), cellDimension, structure.cellShape());

        // Instance for reusable text measurement
        textHelper = new Text();
    }

    /**
     * Scales the vertices of a polygon outward or inward from a given center point by the specified scale factor.
     *
     * @param xPoints the x-coordinates of the polygon's vertices
     * @param yPoints the y-coordinates of the polygon's vertices
     * @param centerX the x-coordinate of the center point
     * @param centerY the y-coordinate of the center point
     * @param scale the scale factor (greater than 1 enlarges, less than 1 shrinks)
     * @return a two-dimensional array containing the scaled x and y coordinates
     */
    private static double[][] scalePolygonFromCenter(double[] xPoints, double[] yPoints,
                                                     double centerX, double centerY,
                                                     double scale) {
        double[] newX = new double[xPoints.length];
        double[] newY = new double[yPoints.length];
        for (int i = 0; i < xPoints.length; i++) {
            double dx = xPoints[i] - centerX;
            double dy = yPoints[i] - centerY;
            newX[i] = centerX + (dx * scale);
            newY[i] = centerY + (dy * scale);
        }
        return new double[][]{newX, newY};
    }

    @Override
    public String toString() {
        return "FXGridCanvasPainter{" +
                "structure=" + structure +
                ", gridDimension2D=[" + gridDimension2D.getWidth() + ", " + gridDimension2D.getHeight() + "]" +
                ", canvas=[" + canvas.getWidth() + ", " + canvas.getHeight() + "]" +
                ", cellDimension=" + cellDimension +
                '}';
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
        return structure;
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
     * @return the cell dimensions in pixels
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
        return !structure.isCoordinateValid(coordinate);
    }

    /**
     * Fills the entire canvas background with the specified color.
     *
     * @param fillColor the color used to fill the canvas background
     */
    public void fillCanvasBackground(Paint fillColor) {
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
     * The grid area is defined by the grid size and cell edge length.
     *
     * @param fillColor the color used to fill the grid background
     * @see #gridDimension2D
     */
    public void fillGridBackground(Paint fillColor) {
        gc.setFill(fillColor);
        gc.fillRect(0.0d, 0.0d, gridDimension2D.getWidth(), gridDimension2D.getHeight());
    }

    /**
     * CLear the background of the grid area.
     * The grid area is defined by the grid size, cell shape and cell edge length.
     *
     * @see #gridDimension2D
     */
    public void clearGridBackground() {
        gc.clearRect(0.0d, 0.0d, gridDimension2D.getWidth(), gridDimension2D.getHeight());
    }

    /**
     * Draws a cell at the specified grid coordinate using the given fill and stroke colors.
     *
     * @param coordinate the grid coordinate of the cell to draw
     * @param fillColor the color used to fill the cell, or null if no fill is desired
     * @param strokeColor the color used to draw the cell's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     */
    public void drawCell(GridCoordinate coordinate,
                         @Nullable Paint fillColor, @Nullable Paint strokeColor,
                         double strokeLineWidth) {
        // Optimization hint: Consider using `fillRect` or `PixelWriter.setColor()` for SQUARE cells.
        double[][] cellPolygon = GridGeometry.computeCellPolygon(coordinate, cellDimension, structure.cellShape());
        drawPolygon(cellPolygon[0], cellPolygon[1], fillColor, strokeColor, strokeLineWidth);
    }

    /**
     * Draws a cell at the specified grid coordinate, scaling its polygon shape from the center
     * by the given scale factor before rendering. This can be used to slightly enlarge or shrink
     * the cell to avoid rendering gaps between adjacent cells.
     *
     * @param coordinate the grid coordinate of the cell to draw
     * @param fillColor the color used to fill the cell, or null if no fill is desired
     * @param strokeColor the color used to draw the cell's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     * @param scale the scale factor to apply to the cell polygon (e.g. 1.01 for 1% larger)
     */
    public void drawScaledCell(GridCoordinate coordinate,
                               @Nullable Paint fillColor, @Nullable Paint strokeColor,
                               double strokeLineWidth,
                               double scale) {
        double[][] cellPolygon = GridGeometry.computeCellPolygon(coordinate, cellDimension, structure.cellShape());
        Point2D center = GridGeometry.computeCellCenter(coordinate, cellDimension, structure.cellShape());
        // Slightly enlarge the polygon (e.g. 1.01 = 1% larger)
        double[][] enlarged = scalePolygonFromCenter(cellPolygon[0], cellPolygon[1], center.getX(), center.getY(), scale);
        drawPolygon(enlarged[0], enlarged[1], fillColor, strokeColor, strokeLineWidth);
    }

    /**
     * Draws a partial frame (one or more connected edges) of a cell at the specified grid coordinate.
     * The frame segment is defined by the cell's shape and the given view direction.
     *
     * @param coordinate the grid coordinate of the cell whose frame segment is to be drawn
     * @param strokeColor the color used to draw the frame segment
     * @param strokeLineWidth the width of the stroke line in pixels
     * @param direction the direction specifying which part of the cell frame to draw
     */
    public void drawCellFrameSegment(GridCoordinate coordinate,
                                     Paint strokeColor,
                                     double strokeLineWidth,
                                     PolygonViewDirection direction) {
        double[][] cellPolygon = GridGeometry.computeCellFrameSegmentPolyline(coordinate, cellDimension, structure.cellShape(), direction);
        drawPolyline(cellPolygon[0], cellPolygon[1], strokeColor, strokeLineWidth);
    }

    /**
     * Draws the bounding box of a cell at the specified grid coordinate.
     * The bounding box is defined by the cell's shape and dimensions.
     *
     * @param coordinate the grid coordinate of the cell whose bounding box is to be drawn
     * @param fillColor the color used to fill the bounding box, or null if no fill is desired
     * @param strokeColor the color used to draw the bounding box's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     * @param strokeAdjustment specifies how the stroke is rendered relative to the bounding box outline
     */
    public void drawCellBoundingBox(GridCoordinate coordinate,
                                    @Nullable Paint fillColor, @Nullable Paint strokeColor,
                                    double strokeLineWidth, StrokeAdjustment strokeAdjustment) {
        Rectangle2D cellBounds = GridGeometry.computeCellBounds(coordinate, cellDimension, structure.cellShape());
        drawRectangle(cellBounds, fillColor, strokeColor, strokeLineWidth, strokeAdjustment);
    }

    /**
     * Draws the inner circle of a cell at the specified grid coordinate.
     * The inner circle is defined by the cell's inner radius and center point.
     *
     * @param coordinate the grid coordinate of the cell whose inner circle is to be drawn
     * @param fillColor the color used to fill the inner circle, or null if no fill is desired
     * @param strokeColor the color used to draw the inner circle's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     * @param strokeAdjustment specifies how the stroke is rendered relative to the circle outline
     */
    public void drawCellInnerCircle(GridCoordinate coordinate,
                                    @Nullable Paint fillColor, @Nullable Paint strokeColor,
                                    double strokeLineWidth, StrokeAdjustment strokeAdjustment) {
        Point2D cellCenter = GridGeometry.computeCellCenter(coordinate, cellDimension, structure.cellShape());
        drawCircle(cellCenter, cellDimension.innerRadius(), fillColor, strokeColor, strokeLineWidth, strokeAdjustment);
    }

    /**
     * Draws the outer circle of a cell at the specified grid coordinate.
     * The outer circle is defined by the cell's outer radius and center point.
     *
     * @param coordinate the grid coordinate of the cell whose outer circle is to be drawn
     * @param fillColor the color used to fill the outer circle, or null if no fill is desired
     * @param strokeColor the color used to draw the outer circle's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     * @param strokeAdjustment specifies how the stroke is rendered relative to the circle outline
     */
    public void drawCellOuterCircle(GridCoordinate coordinate,
                                    @Nullable Paint fillColor, @Nullable Paint strokeColor,
                                    double strokeLineWidth, StrokeAdjustment strokeAdjustment) {
        Point2D cellCenter = GridGeometry.computeCellCenter(coordinate, cellDimension, structure.cellShape());
        drawCircle(cellCenter, cellDimension.outerRadius(), fillColor, strokeColor, strokeLineWidth, strokeAdjustment);
    }

    /**
     * Draws a triangle on the canvas at the specified grid coordinate.
     * The triangle is defined by its edge length and is drawn with optional fill and stroke properties.
     *
     * @param coordinate the grid coordinate where the triangle will be drawn
     * @param triangleEdgeLength the length of each edge of the triangle in pixels
     * @param fillColor the color used to fill the triangle, or null if no fill is desired
     * @param strokeColor the color used to draw the triangle's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     */
    public void drawTriangle(GridCoordinate coordinate, double triangleEdgeLength,
                             @Nullable Paint fillColor, @Nullable Paint strokeColor,
                             double strokeLineWidth) {
        // Compute the position from the current dimension and shape
        Point2D cellTopLeft = GridGeometry.toCanvasPosition(coordinate, cellDimension, structure.cellShape());

        // Compute a new dimension for the triangle based on the triangle edge length
        CellDimension triangleCellDimension = GridGeometry.computeCellDimension(triangleEdgeLength, CellShape.TRIANGLE);
        double[][] trianglePoints = GridGeometry.computeTrianglePolygon(cellTopLeft, triangleCellDimension, coordinate.isTriangleCellPointingDown());
        drawPolygon(trianglePoints[0], trianglePoints[1], fillColor, strokeColor, strokeLineWidth);
    }

    /**
     * Draws a triangle at the specified grid coordinate with its edge length converted
     * so that the triangle matches the width of the current cell shape.
     * The triangle is drawn with optional fill and stroke properties.
     *
     * @param coordinate the grid coordinate where the triangle will be drawn
     * @param fillColor the color used to fill the triangle, or null if no fill is desired
     * @param strokeColor the color used to draw the triangle's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     */
    public void drawTriangleMatchingCellWidth(GridCoordinate coordinate,
                                              @Nullable Paint fillColor, @Nullable Paint strokeColor,
                                              double strokeLineWidth) {
        double convertedEdgeLength = GridGeometry.convertEdgeLengthToMatchWidth(cellDimension.edgeLength(), structure.cellShape(), CellShape.TRIANGLE);
        drawTriangle(coordinate, convertedEdgeLength, fillColor, strokeColor, strokeLineWidth);
    }

    /**
     * Draws a hexagon on the canvas at the specified grid coordinate.
     * The hexagon is defined by its edge length and is drawn with optional fill and stroke properties.
     *
     * @param coordinate the grid coordinate where the hexagon will be drawn
     * @param hexagonEdgeLength the length of each edge of the hexagon in pixels
     * @param fillColor the color used to fill the hexagon, or null if no fill is desired
     * @param strokeColor the color used to draw the hexagon's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     */
    public void drawHexagon(GridCoordinate coordinate, double hexagonEdgeLength,
                            @Nullable Paint fillColor, @Nullable Paint strokeColor,
                            double strokeLineWidth) {
        // Compute the position from the current dimension and shape
        Point2D cellTopLeft = GridGeometry.toCanvasPosition(coordinate, cellDimension, structure.cellShape());

        // Compute a new dimension for the hexagon based on the hexagon edge length
        CellDimension hexagonCellDimension = GridGeometry.computeCellDimension(hexagonEdgeLength, CellShape.HEXAGON);
        double[][] hexagonPoints = GridGeometry.computeHexagonPolygon(cellTopLeft, hexagonCellDimension);
        drawPolygon(hexagonPoints[0], hexagonPoints[1], fillColor, strokeColor, strokeLineWidth);
    }

    /**
     * Draws a hexagon at the specified grid coordinate with its edge length converted
     * so that the hexagon matches the width of the current cell shape.
     * The hexagon is drawn with optional fill and stroke properties.
     *
     * @param coordinate the grid coordinate where the hexagon will be drawn
     * @param fillColor the color used to fill the hexagon, or null if no fill is desired
     * @param strokeColor the color used to draw the hexagon's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     */
    public void drawHexagonMatchingCellWidth(GridCoordinate coordinate,
                                             @Nullable Paint fillColor, @Nullable Paint strokeColor,
                                             double strokeLineWidth) {
        double convertedEdgeLength = GridGeometry.convertEdgeLengthToMatchWidth(cellDimension.edgeLength(), structure.cellShape(), CellShape.HEXAGON);
        drawHexagon(coordinate, convertedEdgeLength, fillColor, strokeColor, strokeLineWidth);
    }

    /**
     * Draws a rectangle on the canvas with optional fill and stroke properties.
     * The stroke can be adjusted to be inside, outside, or centered relative to the rectangle's outline.
     *
     * @param rectangle the rectangle to be drawn
     * @param fillColor the color used to fill the rectangle, or null if no fill is desired
     * @param strokeColor the color used to draw the rectangle's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     * @param strokeAdjustment specifies how the stroke is rendered relative to the rectangle's outline
     */
    public void drawRectangle(Rectangle2D rectangle,
                              @Nullable Paint fillColor, @Nullable Paint strokeColor,
                              double strokeLineWidth, StrokeAdjustment strokeAdjustment) {
        if ((rectangle.getWidth() > 0.0d) && (rectangle.getHeight() > 0.0d)) {
            if (fillColor != null) {
                gc.setFill(fillColor);
                gc.fillRect(rectangle.getMinX(), rectangle.getMinY(), rectangle.getWidth(), rectangle.getHeight());
            }

            if ((strokeColor != null) && (strokeLineWidth > 0.0d)) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(strokeLineWidth);
                double adjustment = switch (strokeAdjustment) {
                    case INSIDE -> strokeLineWidth / 2.0d;
                    case OUTSIDE -> -strokeLineWidth / 2.0d;
                    case CENTERED -> 0.0d;
                };
                gc.strokeRect(
                        rectangle.getMinX() + adjustment,
                        rectangle.getMinY() + adjustment,
                        rectangle.getWidth() - (adjustment * 2),
                        rectangle.getHeight() - (adjustment * 2)
                );
            }
        }
    }

    /**
     * Draws a circle on the canvas with optional fill and stroke properties.
     * The stroke can be adjusted to be inside, outside, or centered relative to the circle's outline.
     *
     * @param center the center point of the circle
     * @param radius the radius of the circle in pixels
     * @param fillColor the color used to fill the circle, or null if no fill is desired
     * @param strokeColor the color used to draw the circle's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     * @param strokeAdjustment specifies how the stroke is rendered relative to the circle's outline
     */
    public void drawCircle(Point2D center, double radius,
                           @Nullable Paint fillColor, @Nullable Paint strokeColor,
                           double strokeLineWidth, StrokeAdjustment strokeAdjustment) {
        if (radius > 0.0d) {
            if (fillColor != null) {
                double diameter = radius * 2;
                double x = center.getX() - radius;
                double y = center.getY() - radius;
                gc.setFill(fillColor);
                gc.fillOval(x, y, diameter, diameter);
            }

            if ((strokeColor != null) && (strokeLineWidth > 0.0d)) {
                double adjustedRadius = switch (strokeAdjustment) {
                    case INSIDE -> radius - (strokeLineWidth / 2.0d);
                    case OUTSIDE -> radius + (strokeLineWidth / 2.0d);
                    case CENTERED -> radius;
                };

                double diameter = adjustedRadius * 2;
                double x = center.getX() - adjustedRadius;
                double y = center.getY() - adjustedRadius;
                gc.setStroke(strokeColor);
                gc.setLineWidth(strokeLineWidth);
                gc.strokeOval(x, y, diameter, diameter);
            }
        }
    }

    /**
     * Draws a polygon on the canvas with optional fill and stroke properties.
     * The stroke can be adjusted to be inside, outside, or centered relative to the polygon's outline.
     *
     * @param xPoints an array of x-coordinates for the vertices of the polygon
     * @param yPoints an array of y-coordinates for the vertices of the polygon
     * @param fillColor the color used to fill the polygon, or null if no fill is desired
     * @param strokeColor the color used to draw the polygon's border, or null if no border is desired
     * @param strokeLineWidth the width of the stroke line in pixels
     */
    public void drawPolygon(double[] xPoints, double[] yPoints,
                            @Nullable Paint fillColor, @Nullable Paint strokeColor,
                            double strokeLineWidth) {
        if ((xPoints.length > 0) || (xPoints.length == yPoints.length)) {
            if (fillColor != null) {
                gc.setFill(fillColor);
                gc.fillPolygon(xPoints, yPoints, xPoints.length);
            }
            if ((strokeColor != null) && (strokeLineWidth > 0)) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(strokeLineWidth);
                gc.strokePolygon(xPoints, yPoints, xPoints.length);
            }
        }
    }

    /**
     * Draws a polyline (an open series of connected line segments) on the canvas.
     * The polyline is defined by arrays of x- and y-coordinates for its vertices.
     *
     * @param xPoints an array of x-coordinates for the vertices of the polyline
     * @param yPoints an array of y-coordinates for the vertices of the polyline
     * @param strokeColor the color used to draw the polyline's border
     * @param strokeLineWidth the width of the stroke line in pixels
     */
    public void drawPolyline(double[] xPoints, double[] yPoints,
                             Paint strokeColor,
                             double strokeLineWidth) {
        if ((xPoints.length > 0) && (xPoints.length == yPoints.length)) {
            if (strokeLineWidth > 0) {
                gc.setStroke(strokeColor);
                gc.setLineWidth(strokeLineWidth);
                gc.strokePolyline(xPoints, yPoints, xPoints.length);
            }
        }
    }

    /**
     * Draws a single pixel directly on the canvas using the PixelWriter.
     * This method checks if the given coordinates are within the canvas bounds
     * and if a valid color is provided before drawing the pixel.
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @param fillColor the color of the pixel, or null if nothing should be drawn
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public void drawPixelDirect(int x, int y, @Nullable Color fillColor) {
        if ((fillColor != null)
                && (x >= 0) && (x < (int) canvas.getWidth())
                && (y >= 0) && (y < (int) canvas.getHeight())) {
            PixelWriter pw = gc.getPixelWriter();
            pw.setColor(x, y, fillColor);
        }
    }

    /**
     * Draws a single pixel on the canvas by filling a 1x1 rectangle at the specified position.
     * This method checks if the given coordinates are within the canvas bounds
     * and if a valid color is provided before drawing the pixel.
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @param fillColor the color of the pixel, or null if nothing should be drawn
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    public void drawPixelRect(int x, int y, @Nullable Paint fillColor) {
        if ((fillColor != null)
                && (x >= 0) && (x < (int) canvas.getWidth())
                && (y >= 0) && (y < (int) canvas.getHeight())) {
            gc.setFill(fillColor);
            gc.fillRect(x, y, 1, 1);
        }
    }

    /**
     * Draws text centered within the specified cell.
     *
     * @param coordinate the grid coordinate of the cell
     * @param text the text to draw
     * @param textColor the color of the text
     * @param font the font used for rendering the text
     */
    public void drawCenteredTextInCell(GridCoordinate coordinate, String text, Paint textColor, Font font) {
        Point2D center = GridGeometry.computeCellCenter(coordinate, cellDimension, structure.cellShape());
        drawCenteredTextAt(center, text, textColor, font);
    }

    /**
     * Draws text centered at the specified canvas position.
     * The text is positioned so that its center aligns with the given point.
     *
     * @param center the center position on the canvas where the text should be drawn
     * @param text the text to draw
     * @param textColor the color of the text
     * @param font the font used for rendering the text
     */
    public void drawCenteredTextAt(Point2D center, String text, Paint textColor, Font font) {
        Point2D textOffset = computeCenteredTextOffset(text, font);

        gc.setFill(textColor);
        gc.setFont(font);
        gc.fillText(text,
                center.getX() + textOffset.getX(),
                center.getY() + textOffset.getY()
        );
    }

    /**
     * Draws text centered at the specified canvas position with a background rectangle.
     * The background rectangle is sized to fit the text plus the given padding and is centered at the provided point.
     *
     * @param center the center position on the canvas where the text and background should be drawn
     * @param text the text to draw
     * @param textColor the color of the text
     * @param font the font used for rendering the text
     * @param backgroundColor the color used to fill the background rectangle
     * @param padding the padding added around the text inside the background rectangle
     */
    public void drawCenteredTextWithBackgroundAt(Point2D center, String text, Paint textColor, Font font, Paint backgroundColor, double padding) {
        Dimension2D textDimension = computeTextDimension(text, font);

        double bgWidth = textDimension.getWidth() + padding;
        double bgHeight = textDimension.getHeight() + padding;
        double bgX = center.getX() - (bgWidth / 2);
        double bgY = center.getY() - (bgHeight / 2);

        // Draw background rectangle
        gc.setFill(backgroundColor);
        gc.fillRect(bgX, bgY, bgWidth, bgHeight);

        // Draw centered text
        drawCenteredTextAt(center, text, textColor, font);
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
        Dimension2D textDimension = computeTextDimension(text, font);

        double baselineOffset = textHelper.getBaselineOffset();
        double xOffset = -(textDimension.getWidth() / 2.0d);
        double yOffset = -(textDimension.getHeight() / 2.0d) + baselineOffset;
        return new Point2D(xOffset, yOffset);
    }

}
