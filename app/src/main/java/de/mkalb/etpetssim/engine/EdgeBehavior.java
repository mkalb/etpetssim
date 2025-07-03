package de.mkalb.etpetssim.engine;

/**
 * Defines the possible behaviors for entities interacting with the edge of a simulation grid.
 * Used to specify how entities are handled when reaching a grid boundary in grid-based simulations.
 *
 * @see GridEdgeBehavior
 */
public enum EdgeBehavior {

    /**
     * Prevents entities from moving beyond the grid edge.
     * Entities are stopped at the boundary and cannot proceed further.
     */
    BLOCK,

    /**
     * Causes entities reaching one edge of the grid to reappear at the opposite edge.
     * This creates a continuous, toroidal topology.
     */
    WRAP,

    /**
     * Removes entities from the simulation when they reach the grid edge.
     * Entities are absorbed and do not continue to exist beyond the boundary.
     */
    ABSORB,

    /**
     * Reverses the direction of entities upon reaching the grid edge.
     * Entities are reflected back into the grid, simulating a bounce effect.
     */
    REFLECT
}
