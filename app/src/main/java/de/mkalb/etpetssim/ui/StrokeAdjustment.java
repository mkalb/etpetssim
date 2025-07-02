package de.mkalb.etpetssim.ui;

/**
 * Specifies how the stroke (border) of a shape is rendered relative to its outline.
 * <ul>
 *   <li>{@link #INSIDE} - The stroke is drawn entirely inside the shape's outline.</li>
 *   <li>{@link #OUTSIDE} - The stroke is drawn entirely outside the shape's outline.</li>
 *   <li>{@link #CENTERED} - The stroke is centered on the shape's outline (default behavior).</li>
 * </ul>
 * This enum can be used as a parameter for drawing methods to control stroke placement.
 */
public enum StrokeAdjustment {

    /** The stroke is drawn entirely inside the shape's outline. */
    INSIDE,
    /** The stroke is drawn entirely outside the shape's outline. */
    OUTSIDE,
    /** The stroke is centered on the shape's outline. */
    CENTERED

}