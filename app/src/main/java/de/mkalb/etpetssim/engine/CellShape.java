package de.mkalb.etpetssim.engine;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

/**
 * Defines the possible shapes for a cell in a regular two-dimensional grid.
 * <p>
 * Each shape is a regular polygon, meaning it is convex, symmetrical, and all edges (sides) and angles are equal.
 * The number of {@code vertices} (corner points) is always equal to the number of {@code edges} (sides).
 * Constants are ordered by increasing vertex count (TRIANGLE=3, SQUARE=4, HEXAGON=6);
 * this order is not semantically significant for external contracts.
 * <p>
 * <strong>Terminology:</strong><br>
 * - {@code vertex} (plural: {@code vertices}): a corner point of the polygon.<br>
 * - {@code edge} (plural: {@code edges}): a straight line connecting two vertices.<br>
 * These terms are used consistently throughout the framework for clarity and precision.
 */
public enum CellShape {

    /**
     * Regular triangle: 3 vertices and 3 edges.
     */
    TRIANGLE(3, "cellshape.triangle"),

    /**
     * Regular quadrilateral (square): 4 vertices and 4 edges.
     */
    SQUARE(4, "cellshape.square"),

    /**
     * Regular hexagon: 6 vertices and 6 edges.
     */
    HEXAGON(6, "cellshape.hexagon");

    private final int vertexCount;
    private final String resourceKey;

    CellShape(int vertexCount,
              String resourceKey) {
        this.vertexCount = vertexCount;
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
        return AppLocalizationKeys.ENUM_LABEL_CELLSHAPE;
    }

    /**
     * Returns the number of vertices of this cell shape.
     * This value is also equal to the number of edges, since the shape is regular.
     * <p>
     * For example: {@link #TRIANGLE} has 3, {@link #SQUARE} has 4, {@link #HEXAGON} has 6.
     *
     * @return the number of vertices (and edges) of this cell shape
     */
    public int vertexCount() {
        return vertexCount;
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

}
