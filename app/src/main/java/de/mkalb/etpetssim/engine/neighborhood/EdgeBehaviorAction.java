package de.mkalb.etpetssim.engine.neighborhood;

/**
 * Outcome of applying the edge behavior of a grid structure to a coordinate, for example a neighbor coordinate.
 *
 * <p>A coordinate within the grid is {@link #VALID}; for a coordinate outside the grid, the
 * {@link de.mkalb.etpetssim.engine.EdgeBehavior} of each axis that the coordinate exceeds decides the outcome.
 *
 * @see de.mkalb.etpetssim.engine.GridEdgeBehavior
 */
public enum EdgeBehaviorAction {

    /**
     * The coordinate lies within the grid; no edge handling applies.
     */
    VALID,

    /**
     * The coordinate lies outside the grid, and the boundary blocks it.
     */
    BLOCKED,

    /**
     * The coordinate lies outside the grid and is wrapped to the opposite side of the grid.
     */
    WRAPPED,

    /**
     * The coordinate lies outside the grid and is absorbed by the boundary; an entity moving there leaves the
     * simulation.
     */
    ABSORBED;

    /**
     * Returns whether the mapped coordinate of this outcome lies within the grid, so that its cell can be accessed.
     *
     * @return {@code true} for {@link #VALID} and {@link #WRAPPED}, {@code false} for {@link #BLOCKED} and
     * {@link #ABSORBED}
     */
    public boolean isWithinGrid() {
        return (this == VALID) || (this == WRAPPED);
    }

}
