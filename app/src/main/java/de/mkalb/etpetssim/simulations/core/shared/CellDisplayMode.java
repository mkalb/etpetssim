package de.mkalb.etpetssim.simulations.core.shared;

/**
 * Defines the available display modes for drawing a grid cell on the canvas.
 */
public enum CellDisplayMode {

    /**
     * Draws the cell shape without a border.
     */
    SHAPE("celldisplaymode.shape"),

    /**
     * Draws the cell shape with a border.
     */
    SHAPE_BORDERED("celldisplaymode.shapebordered"),

    /**
     * Draws an inner circle without a border.
     */
    CIRCLE("celldisplaymode.circle"),

    /**
     * Draws an inner circle with a border.
     */
    CIRCLE_BORDERED("celldisplaymode.circlebordered"),

    /**
     * Draws a single emoji character in the cell.
     */
    EMOJI("celldisplaymode.emoji");

    private final String resourceKey;

    CellDisplayMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "celldisplaymode.label";
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

    /**
     * Indicates whether this display mode includes a border.
     *
     * @return {@code true} if the mode includes a border, {@code false} otherwise
     */
    public boolean hasBorder() {
        return switch (this) {
            case SHAPE_BORDERED, CIRCLE_BORDERED -> true;
            case SHAPE, CIRCLE, EMOJI -> false;
        };
    }

}
