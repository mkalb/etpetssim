package de.mkalb.etpetssim.simulations.lab.shared;

/**
 * Patterns of highlighted cells that the Simulation Lab places at the center of the grid, scaled to the grid size.
 *
 * <p>Offsets, rotations, and mirrors work on coordinates, so the shapes may look distorted on triangle and hexagon
 * grids.
 */
public enum LabPattern {

    /**
     * A horizontal and a vertical line that cross at their centers.
     */
    CROSS("labpattern.cross"),

    /**
     * Two diagonal lines that cross at their centers.
     */
    DIAGONALS("labpattern.diagonals"),

    /**
     * The outline of a rectangle.
     */
    RECTANGLE_OUTLINE("labpattern.rectangleoutline"),

    /**
     * Several rectangle outlines nested in each other.
     */
    NESTED_RECTANGLES("labpattern.nestedrectangles"),

    /**
     * An asymmetric shape in four quadrants: unchanged and rotated by 90, 180, and 270 degrees.
     */
    ROTATIONS("labpattern.rotations"),

    /**
     * An asymmetric shape in four quadrants: unchanged, mirrored horizontally, vertically, and both.
     */
    MIRRORS("labpattern.mirrors"),

    /**
     * An asymmetric shape in four quadrants: shifted by an even or odd number of columns and rows, in all four
     * combinations.
     */
    PARITY("labpattern.parity");

    private final String resourceKey;

    LabPattern(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    public static String labelResourceKey() {
        return "labpattern.label";
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}
