package de.mkalb.etpetssim.engine;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

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
     * Returns the resource-bundle key for the display label of this enum type.
     *
     * <p>The returned key is intended for localized lookup of the enum type name
     * (that is, the label for the enum as a whole, not for an individual enum constant).</p>
     *
     * @return the resource bundle key for this enum type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return AppLocalizationKeys.ENUM_LABEL_EDGEBEHAVIOR;
    }

    /**
     * Returns the resource bundle key associated with this enum constant.
     * <p>
     * The resource key can be used for localized message lookup via {@code AppLocalization}.
     *
     * @return the resource bundle key for this enum constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}
