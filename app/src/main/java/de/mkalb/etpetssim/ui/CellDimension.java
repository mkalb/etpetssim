package de.mkalb.etpetssim.ui;

/**
 * Represents the dimensions of a cell in a grid.
 * This record encapsulates the side length, width, height,
 * half side length, half width, and half height of the cell.
 * The values width and height represent the surrounding rectangle.
 *
 * @param sideLength side length of the cell in pixels
 * @param width width of the cell in pixels
 * @param height height of the cell in pixels
 * @param halfSideLength half side length of the cell in pixels
 * @param halfWidth half width of the cell in pixels
 * @param halfHeight half height of the cell in pixels
 */
public record CellDimension(
        double sideLength,
        double width,
        double height,
        double halfSideLength,
        double halfWidth,
        double halfHeight
) {

    private static final double DOUBLE_COMPARE_EPSILON = 1.0e-9;

    public CellDimension {
        if ((sideLength <= 0) || (width <= 0) || (height <= 0)) {
            throw new IllegalArgumentException("");
        }
        if ((Math.abs(sideLength - (2 * halfSideLength)) > DOUBLE_COMPARE_EPSILON)
                || (Math.abs(width - (2 * halfWidth)) > DOUBLE_COMPARE_EPSILON)
                || (Math.abs(height - (2 * halfHeight)) > DOUBLE_COMPARE_EPSILON)) {
            throw new IllegalArgumentException("");
        }
    }

    @SuppressWarnings("MagicNumber")
    public static CellDimension of(double sideLength, double width, double height) {
        return new CellDimension(sideLength, width, height, sideLength / 2.0d, width / 2.0d, height / 2.0d);
    }

}
