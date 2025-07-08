package de.mkalb.etpetssim.ui;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.*;

/**
 * Describes the geometric pixel dimensions of a single cell shape
 * (e.g. triangle, square, hexagon) used in a 2D grid layout.
 * <p>
 * This record provides precomputed values derived from the cell's edge length,
 * including full and half dimensions, inner and outer radius,
 * and spacing between adjacent cell centers in both directions.
 * <p>
 * The {@code width} and {@code height} refer to the bounding box
 * that fully encloses the polygonal cell shape, aligned with the axes.
 *
 * @param edgeLength the length of one edge of the cell in pixels
 * @param width the full width of the cell's bounding box in pixels
 * @param height the full height of the cell's bounding box in pixels
 * @param halfEdgeLength half the edge length of the cell in pixels
 * @param halfWidth half the width of the cell's bounding box in pixels
 * @param halfHeight half the height of the cell's bounding box in pixels
 * @param innerRadius the distance from the center to the midpoint of an edge
 * @param outerRadius the distance from the center to a vertex
 * @param columnWidth horizontal spacing between adjacent cell centers
 * @param rowHeight vertical spacing between adjacent cell centers
 */
public record CellDimension(
        double edgeLength,
        double width,
        double height,
        double halfEdgeLength,
        double halfWidth,
        double halfHeight,
        double innerRadius,
        double outerRadius,
        double columnWidth,
        double rowHeight
) {

    private static final double DOUBLE_COMPARE_EPSILON = 1.0e-9;

    public CellDimension {
        if ((edgeLength <= 0) || (width <= 0) || (height <= 0)
                || (innerRadius <= 0) || (outerRadius <= 0)
                || (columnWidth <= 0) || (rowHeight <= 0)) {
            throw new IllegalArgumentException("All dimensions must be positive.");
        }
        if ((Math.abs(edgeLength - (2 * halfEdgeLength)) > DOUBLE_COMPARE_EPSILON)
                || (Math.abs(width - (2 * halfWidth)) > DOUBLE_COMPARE_EPSILON)
                || (Math.abs(height - (2 * halfHeight)) > DOUBLE_COMPARE_EPSILON)) {
            throw new IllegalArgumentException("Half dimensions must be half of the full dimensions.");
        }
        if (outerRadius <= innerRadius) {
            throw new IllegalArgumentException("Outer radius must be greater than inner radius.");
        }
    }

    /**
     * Returns the dimensions of the bounding box that fully contains the cell shape.
     * <p>
     * The bounding box is a rectangle aligned with the axes, defined by the full
     * {@code width} and {@code height} of the cell. It does not necessarily match
     * the actual polygonal shape but encloses it completely.
     *
     * @return a {@code Dimension2D} representing the width and height of the cell's bounding box
     */
    public Dimension2D boundingBoxDimension() {
        return new Dimension2D(width, height);
    }

    /**
     * Returns the bounding box of the cell positioned at the specified top-left coordinates.
     * <p>
     * The bounding box is a rectangle that fully contains the cell shape.
     * The given coordinates specify the top-left corner of this bounding box.
     *
     * @param topLeftX the x-coordinate of the top-left corner of the bounding box
     * @param topLeftY the y-coordinate of the top-left corner of the bounding box
     * @return a {@code Rectangle2D} representing the bounding box of the cell at the specified position
     */
    public Rectangle2D boundingBoxAt(double topLeftX, double topLeftY) {
        return new Rectangle2D(topLeftX, topLeftY, width, height);
    }

    /**
     * Returns the bounding box of the cell positioned at the specified top-left point.
     * <p>
     * The bounding box is a rectangle that fully contains the cell shape.
     * The given point specifies the top-left corner of this bounding box.
     *
     * @param topLeftPoint the top-left point of the bounding box
     * @return a {@code Rectangle2D} representing the bounding box of the cell at the specified position
     */
    public Rectangle2D boundingBoxAt(Point2D topLeftPoint) {
        return new Rectangle2D(topLeftPoint.getX(), topLeftPoint.getY(), width, height);
    }

    /**
     * Returns a concise string representation of this cell dimension.
     * <p>
     * Format: {@code [edgeLength, width × height]}
     * <br>
     * Example: {@code [10.0, 20.0 × 30.0]}
     * <p>
     * Uses {@link java.util.Locale#US} to ensure a dot as decimal separator and no thousands' separator.
     *
     * @return a concise display string for this cell dimension
     */
    public String toDisplayString() {
        return String.format(Locale.US, "[%.1f, %.1f × %.1f]", edgeLength, width, height);
    }

}
