package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * Represents the topological configuration of a grid, including its cell shape and boundary behavior.
 * <p>
 * All cell shapes used in this topology are regular polygons
 * and are rendered with a consistent orientation strategy for visual clarity and simplicity.
 * <p>
 * <strong>Orientation:</strong><br>
 * - {@link CellShape#HEXAGON} is always rendered with a <strong>flat-top</strong> orientation,
 *   meaning one of its edges is aligned horizontally at the top.<br>
 * - {@link CellShape#SQUARE} is axis-aligned and does not require orientation logic.<br>
 * - {@link CellShape#TRIANGLE} alternates orientation by row: the top row (y = 0) is flat-top,
 *   and each subsequent row flips the triangle vertically. This creates a zigzag pattern
 *   that supports consistent neighbor relationships and visual symmetry.
 * <p>
 * This orientation design simplifies rendering logic and ensures a uniform appearance across different grid types,
 * especially when displayed in graphical environments.
 *
 * @param cellShape    the shape of each cell in the grid
 * @param gridEdgeBehavior the type of boundary behavior for the grid edges
 */
public record GridTopology(CellShape cellShape, GridEdgeBehavior gridEdgeBehavior) {

    public GridTopology {
        Objects.requireNonNull(cellShape);
        Objects.requireNonNull(gridEdgeBehavior);
    }

    /**
     * Returns the edge behavior for the X-axis of the grid.
     *
     * @return the edge behavior for the X-axis
     */
    public EdgeBehavior edgeBehaviorX() {
        return gridEdgeBehavior.edgeBehaviorX();
    }

    /**
     * Returns the edge behavior for the Y-axis of the grid.
     *
     * @return the edge behavior for the Y-axis
     */
    public EdgeBehavior edgeBehaviorY() {
        return gridEdgeBehavior.edgeBehaviorY();
    }

    /**
     * Returns the number of vertices of each individual cell shape.
     * This is also equal to the number of edges, since the shape is regular.
     * <p>
     * For example: TRIANGLE has 3, SQUARE has 4, HEXAGON has 6.
     * <p>
     * Note: This refers to the geometry of a single cell, not the grid as a whole.
     *
     * @return the number of vertices (and edges) of a single cell shape
     */
    public int cellVertexCount() {
        return cellShape.vertexCount();
    }

    /**
     * Returns a short, human-readable string representation of this grid topology.
     * <p>
     * Format: {@code [SHAPE EDGE_BEHAVIOR]} if both edge behaviors are identical,
     * or {@code [SHAPE EDGE_BEHAVIOR_X/EDGE_BEHAVIOR_Y]} if they differ.
     * <br>
     * Example: {@code [SQUARE ABSORB]} or {@code [HEXAGON BLOCK/WRAP]}
     *
     * @return a concise display string for this grid topology
     */
    public String toDisplayString() {
        if (gridEdgeBehavior.isEqualEdgeBehavior()) {
            return String.format("[%s %s]",
                    cellShape.name(),
                    gridEdgeBehavior.edgeBehaviorX().name());
        }
        return String.format("[%s %s/%s]",
                cellShape.name(),
                gridEdgeBehavior.edgeBehaviorX().name(),
                gridEdgeBehavior.edgeBehaviorY().name());
    }

}
