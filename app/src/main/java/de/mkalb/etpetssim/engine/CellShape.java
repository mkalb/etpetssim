package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * Defines the possible shapes for a cell in a regular two-dimensional grid.
 * <p>
 * Each shape is a regular polygon, meaning it is convex, symmetrical, and all sides and angles are equal.
 * The number of {@code vertices} (corner points) is always equal to the number of {@code edges} (sides).
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

    /**
     * Constructs a cell shape with the specified number of vertices (and edges) and a resource key.
     *
     * @param vertexCount the number of vertices (and edges) for this shape
     * @param resourceKey the resource key for this shape
     */
    CellShape(int vertexCount, String resourceKey) {
        Objects.requireNonNull(resourceKey);
        this.vertexCount = vertexCount;
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum CellShape
     *
     * @return the resource key for the label of the enum CellShape
     */
    public static String labelResourceKey() {
        return "cellshape.label";
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
     * Returns the resource key associated with this cell shape.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this cell shape
     */
    public String resourceKey() {
        return resourceKey;
    }

}
