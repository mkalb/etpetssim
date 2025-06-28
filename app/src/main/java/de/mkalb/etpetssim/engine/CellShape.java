package de.mkalb.etpetssim.engine;

/**
 * Represents the shape of a cell in a grid.
 * All shapes are regular polygons.
 * They are convex, symmetrical, and equilateral.
 * Each shape has a specific number of vertices (corners)
 * which is equal to the number of sides (edges).
 */
public enum CellShape {

    /**
     * Regular triangle with 3 corners.
     */
    TRIANGLE(3),
    /**
     * Regular quadrilateral (square) with 4 corners.
     */
    SQUARE(4),
    /**
     * Regular hexagon with 6 corners.
     */
    HEXAGON(6);

    private final int vertexCount;

    CellShape(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    /**
     * Returns the number of vertices (corners) of the cell shape.
     * This is also the number of sides (edges) of the shape.
     * TRIANGLE has 3, SQUARE has 4, HEXAGON has 6.
     *
     * @return the number of vertices of the cell shape
     */
    public int vertexCount() {
        return vertexCount;
    }

}
