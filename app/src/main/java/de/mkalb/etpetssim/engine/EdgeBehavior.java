package de.mkalb.etpetssim.engine;

/**
 * Enum representing the behavior of entities when they reach the edge of a grid.
 * This is used in grid-based simulations to define how entities interact with the boundaries.
 */
public enum EdgeBehavior {

    /**
     * Blocks movement at the edge of the grid.
     */
    BLOCK,
    /**
     * Wraps around to the opposite edge of the grid.
     */
    WRAP,
    /**
     * Absorbs entities that reach the edge of the grid, removing them from the simulation.
     */
    ABSORB,
    /**
     * Reflects entities that reach the edge of the grid, reversing their direction.
     */
    REFLECT

}
