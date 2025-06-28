package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * Represents the behavior of the edges of a grid in a simulation.
 * Each boundary type defines how entities behave when they reach the edges of the grid.
 */
public enum BoundaryType {

    BLOCK_X_BLOCK_Y(EdgeBehavior.BLOCK, EdgeBehavior.BLOCK),
    BLOCK_X_WRAP_Y(EdgeBehavior.BLOCK, EdgeBehavior.WRAP),
    WRAP_X_BLOCK_Y(EdgeBehavior.WRAP, EdgeBehavior.BLOCK),
    WRAP_X_WRAP_Y(EdgeBehavior.WRAP, EdgeBehavior.WRAP),
    ABSORB_XY(EdgeBehavior.ABSORB, EdgeBehavior.ABSORB),
    REFLECT_XY(EdgeBehavior.REFLECT, EdgeBehavior.REFLECT);

    private final EdgeBehavior edgeBehaviorX;
    private final EdgeBehavior edgeBehaviorY;

    BoundaryType(EdgeBehavior edgeBehaviorX, EdgeBehavior edgeBehaviorY) {
        Objects.requireNonNull(edgeBehaviorX);
        Objects.requireNonNull(edgeBehaviorY);
        this.edgeBehaviorX = edgeBehaviorX;
        this.edgeBehaviorY = edgeBehaviorY;
    }

    /**
     * Returns the edge behavior for the X-axis of the grid.
     *
     * @return the edge behavior for the X-axis
     */
    public EdgeBehavior edgeBehaviorX() {
        return edgeBehaviorX;
    }

    /**
     * Returns the edge behavior for the Y-axis of the grid.
     *
     * @return the edge behavior for the Y-axis
     */
    public EdgeBehavior edgeBehaviorY() {
        return edgeBehaviorY;
    }

    /**
     * Checks if the edge behavior for both axes is the same.
     *
     * @return true if both edge behaviors are equal, false otherwise
     */
    public boolean isEqualEdgeBehavior() {
        return edgeBehaviorX == edgeBehaviorY;
    }

}
