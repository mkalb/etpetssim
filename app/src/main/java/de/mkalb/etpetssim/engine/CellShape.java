package de.mkalb.etpetssim.engine;

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
    TRIANGLE(3),

    /**
     * Regular quadrilateral (square): 4 vertices and 4 edges.
     */
    SQUARE(4),

    /**
     * Regular hexagon: 6 vertices and 6 edges.
     */
    HEXAGON(6);

    private final int vertexCount;

    /**
     * Constructs a cell shape with the specified number of vertices (and edges).
     *
     * @param vertexCount the number of vertices (and edges) for this shape
     */
    CellShape(int vertexCount) {
        this.vertexCount = vertexCount;
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

}
