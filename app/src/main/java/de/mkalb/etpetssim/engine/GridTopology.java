package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * Represents the topological configuration of a grid, including its cell shape and boundary behavior.
 * <p>
 * All cell shapes used in this topology are regular polygons (triangle, square, hexagon),
 * and are rendered with a consistent orientation for visual clarity and simplicity.
 * <p>
 * Although some shapes like TRIANGLE and HEXAGON can be oriented differently (e.g., pointy-top vs. flat-top),
 * this implementation deliberately omits orientation as a separate parameter.
 * Instead, all shapes are consistently rendered with a <strong>flat top</strong> orientation,
 * meaning one of the polygon's sides is aligned horizontally at the top.
 * <p>
 * This design choice simplifies rendering logic and ensures a uniform appearance across different grid types,
 * especially when displayed in a JavaFX canvas or similar graphical environments.
 *
 * @param cellShape     the shape of each cell in the grid (TRIANGLE, SQUARE, HEXAGON)
 * @param boundaryType  the type of boundary behavior for the grid edges
 */
public record GridTopology(CellShape cellShape, BoundaryType boundaryType) {

    public GridTopology {
        Objects.requireNonNull(cellShape);
        Objects.requireNonNull(boundaryType);
    }

    /**
     * Returns the edge behavior for the X-axis of the grid.
     *
     * @return the edge behavior for the X-axis
     */
    public EdgeBehavior edgeBehaviorX() {
        return boundaryType.edgeBehaviorX();
    }

    /**
     * Returns the edge behavior for the Y-axis of the grid.
     *
     * @return the edge behavior for the Y-axis
     */
    public EdgeBehavior edgeBehaviorY() {
        return boundaryType.edgeBehaviorY();
    }

    /**
     * Returns the number of vertices (corners) of the cell shape.
     *
     * @return the number of vertices of the cell shape
     */
    public int vertexCount() {
        return cellShape.vertexCount();
    }

    /**
     * Returns a string representation of the grid topology.
     * Example: [SQUARE BLOCK/BLOCK]
     *
     * @return a string representation of the grid topology
     */
    public String asString() {
        return String.format("[%s %s/%s]",
                cellShape.name(),
                boundaryType.edgeBehaviorX().name(),
                boundaryType.edgeBehaviorY().name());
    }

}
