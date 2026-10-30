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
    ABSORB("edgebehavior.absorb");

    private final String resourceKey;

    EdgeBehavior(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "edgebehavior.label";
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}
