package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

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

    CellDisplayMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource-bundle key for the display label of this enum type.
     *
     * <p>The returned key is intended for localized lookup of the enum type name
     * (that is, the label for the enum as a whole, not for an individual enum constant).</p>
     *
     * @return the resource bundle key for this enum type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return AppLocalizationKeys.ENUM_LABEL_CELLDISPLAYMODE;
    }

    /**
     * Returns the resource bundle key associated with this enum constant.
     * <p>
     * The resource key can be used for localized message lookup via {@code AppLocalization}.
     *
     * @return the resource bundle key for this enum constant
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
