package de.mkalb.etpetssim.ui;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Represents the dimensions of a cell in a grid.
 * This record encapsulates the side length, width, height,
 * half side length, half width, and half height of the cell.
 * The values width and height represent the surrounding rectangle (bounding box).
 *
 * @param sideLength side length of the cell in pixels
 * @param width width of the cell in pixels (bounding box)
 * @param height height of the cell in pixels (bounding box)
 * @param halfSideLength half side length of the cell in pixels
 * @param halfWidth half width of the cell in pixels (bounding box)
 * @param halfHeight half height of the cell in pixels (bounding box)
 * @param innerRadius inner radius of the cell in pixels
 * @param outerRadius outer radius of the cell in pixels
 * @param columnWidth width of the column in pixels
 * @param rowHeight height of the row in pixels
 */
public record CellDimension(
        double sideLength,
        double width,
        double height,
        double halfSideLength,
        double halfWidth,
        double halfHeight,
        double innerRadius,
        double outerRadius,
        double columnWidth,
        double rowHeight
) {

    private static final double DOUBLE_COMPARE_EPSILON = 1.0e-9;

    public CellDimension {
        if ((sideLength <= 0) || (width <= 0) || (height <= 0)) {
            throw new IllegalArgumentException("Dimensions must be positive.");
        }
        if ((Math.abs(sideLength - (2 * halfSideLength)) > DOUBLE_COMPARE_EPSILON)
                || (Math.abs(width - (2 * halfWidth)) > DOUBLE_COMPARE_EPSILON)
                || (Math.abs(height - (2 * halfHeight)) > DOUBLE_COMPARE_EPSILON)) {
            throw new IllegalArgumentException("Half dimensions must be half of the full dimensions.");
        }
        if ((innerRadius <= 0) || (outerRadius <= 0)) {
            throw new IllegalArgumentException("Inner and outer radius must be positive.");
        }
        if (outerRadius <= innerRadius) {
            throw new IllegalArgumentException("Outer radius must be greater than inner radius.");
        }
        if ((columnWidth <= 0) || (rowHeight <= 0)) {
            throw new IllegalArgumentException("Column width and row height must be positive.");
        }
    }

    /**
     * Returns the bounding box dimension of the cell.
     * This is equivalent to the width and height of the cell.
     *
     * @return the bounding box dimension of the cell
     */
    public Dimension2D boundingBoxDimension() {
        return new Dimension2D(width, height);
    }

    /**
     * Returns the bounding box of the cell at the specified top-left corner.
     *
     * @param topLeftX the x-coordinate of the top-left corner of the cell
     * @param topLeftY the y-coordinate of the top-left corner of the cell
     * @return a Rectangle2D representing the bounding box of the cell at the specified top-left corner.
     */
    public Rectangle2D boundingBoxAt(double topLeftX, double topLeftY) {
        return new Rectangle2D(topLeftX, topLeftY, width, height);
    }

    /**
     * Returns the bounding box of the cell at the specified top-left point.
     *
     * @param topLeftPoint the top-left point of the cell
     * @return a Rectangle2D representing the bounding box of the cell at the specified top-left point.
     */
    public Rectangle2D boundingBoxAt(Point2D topLeftPoint) {
        return new Rectangle2D(topLeftPoint.getX(), topLeftPoint.getY(), width, height);
    }

}
