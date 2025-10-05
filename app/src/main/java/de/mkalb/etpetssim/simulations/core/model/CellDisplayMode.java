package de.mkalb.etpetssim.simulations.core.model;

/**
 * Defines the available display modes for drawing a grid cell on the canvas.
 * <p>
 * Each mode determines how a cell is visually represented:
 * <ul>
 *   <li>{@link #SHAPE}: Draws the cell shape without a border.</li>
 *   <li>{@link #SHAPE_BORDERED}: Draws the cell shape with a border.</li>
 *   <li>{@link #CIRCLE}: Draws an inner circle without a border.</li>
 *   <li>{@link #CIRCLE_BORDERED}: Draws an inner circle with a border.</li>
 *   <li>{@link #EMOJI}: Draws a single emoji character in the cell.</li>
 * </ul>
 * The {@code resourceKey} field allows for resource lookup, such as internationalized labels.
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

    /**
     * Constructs a cell display mode with the specified resource key.
     *
     * @param resourceKey the resource key for this cell display mode
     */
    CellDisplayMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum CellDisplayMode.
     *
     * @return the resource key for the label of the enum CellDisplayMode
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "celldisplaymode.label";
    }

    /**
     * Returns the resource key associated with this cell display mode.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this cell display mode
     */
    public String resourceKey() {
        return resourceKey;
    }

    /**
     * Indicates whether this display mode includes a border.
     *
     * @return true if the mode includes a border, false otherwise
     */
    public boolean hasBorder() {
        return (this == SHAPE_BORDERED) || (this == CIRCLE_BORDERED);
    }

}
