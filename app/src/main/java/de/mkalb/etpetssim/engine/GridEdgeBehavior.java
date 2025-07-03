package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * Defines a combination of edge behaviors for the X and Y boundaries of a simulation grid.
 * Each constant specifies how entities interact with both the horizontal (X) and vertical (Y) edges,
 * enabling flexible configuration of grid boundary handling in various simulation scenarios.
 *
 * @see EdgeBehavior
 */
public enum GridEdgeBehavior {

    /**
     * Both X and Y edges are blocking. Entities cannot cross the grid boundaries.
     */
    BLOCK_X_BLOCK_Y(EdgeBehavior.BLOCK, EdgeBehavior.BLOCK),

    /**
     * X edge is blocking, Y edge wraps. Entities reappear on the opposite Y edge.
     */
    BLOCK_X_WRAP_Y(EdgeBehavior.BLOCK, EdgeBehavior.WRAP),

    /**
     * X edge wraps, Y edge is blocking. Entities reappear on the opposite X edge.
     */
    WRAP_X_BLOCK_Y(EdgeBehavior.WRAP, EdgeBehavior.BLOCK),

    /**
     * Both X and Y edges wrap. Entities reappear on the opposite edge in both directions.
     */
    WRAP_X_WRAP_Y(EdgeBehavior.WRAP, EdgeBehavior.WRAP),

    /**
     * Both X and Y edges absorb entities. Entities are removed when reaching any edge.
     */
    ABSORB_XY(EdgeBehavior.ABSORB, EdgeBehavior.ABSORB),

    /**
     * Both X and Y edges reflect entities. Entities bounce back upon reaching any edge.
     */
    REFLECT_XY(EdgeBehavior.REFLECT, EdgeBehavior.REFLECT);

    private final EdgeBehavior edgeBehaviorX;
    private final EdgeBehavior edgeBehaviorY;

    /**
     * Constructs a grid edge behavior with specified behaviors for the X and Y axes.
     *
     * @param edgeBehaviorX the behavior for the X edge (horizontal)
     * @param edgeBehaviorY the behavior for the Y edge (vertical)
     */
    GridEdgeBehavior(EdgeBehavior edgeBehaviorX, EdgeBehavior edgeBehaviorY) {
        Objects.requireNonNull(edgeBehaviorX, "edgeBehaviorX must not be null");
        Objects.requireNonNull(edgeBehaviorY, "edgeBehaviorY must not be null");
        this.edgeBehaviorX = edgeBehaviorX;
        this.edgeBehaviorY = edgeBehaviorY;
    }

    /**
     * Returns the edge behavior for the X-axis (horizontal boundary).
     *
     * @return the {@link EdgeBehavior} for the X edge
     */
    public EdgeBehavior edgeBehaviorX() {
        return edgeBehaviorX;
    }

    /**
     * Returns the edge behavior for the Y-axis (vertical boundary).
     *
     * @return the {@link EdgeBehavior} for the Y edge
     */
    public EdgeBehavior edgeBehaviorY() {
        return edgeBehaviorY;
    }

    /**
     * Checks if both X and Y edges use the same edge behavior.
     *
     * @return true if both edge behaviors are identical, false otherwise
     */
    public boolean isEqualEdgeBehavior() {
        return edgeBehaviorX == edgeBehaviorY;
    }

}
