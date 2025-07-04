package de.mkalb.etpetssim.engine;

import java.util.*;

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
    BLOCK("edgebehavior.block"),

    /**
     * Causes entities reaching one edge of the grid to reappear at the opposite edge.
     * This creates a continuous, toroidal topology.
     */
    WRAP("edgebehavior.wrap"),

    /**
     * Removes entities from the simulation when they reach the grid edge.
     * Entities are absorbed and do not continue to exist beyond the boundary.
     */
    ABSORB("edgebehavior.absorb"),

    /**
     * Reverses the direction of entities upon reaching the grid edge.
     * Entities are reflected back into the grid, simulating a bounce effect.
     */
    REFLECT("edgebehavior.reflect");

    private final String resourceKey;

    /**
     * Constructs an edge behavior with the specified resource key.
     *
     * @param resourceKey the resource key for this edge behavior
     */
    EdgeBehavior(String resourceKey) {
        Objects.requireNonNull(resourceKey);
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum EdgeBehavior.
     *
     * @return the resource key for the label of the enum EdgeBehavior
     */
    public static String labelResourceKey() {
        return "edgebehavior.label";
    }

    /**
     * Returns the resource key associated with this edge behavior.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this edge behavior
     */
    public String resourceKey() {
        return resourceKey;
    }

}
