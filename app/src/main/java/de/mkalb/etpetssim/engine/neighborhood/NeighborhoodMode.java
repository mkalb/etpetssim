package de.mkalb.etpetssim.engine.neighborhood;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

/**
 * Defines the neighborhood mode for cell-based simulations.
 * Determines which neighboring cells are considered adjacent:
 * either only those sharing an edge, or those sharing an edge or a vertex.
 *
 * @see de.mkalb.etpetssim.engine.CellShape
 */
public enum NeighborhoodMode {

    /**
     * Only neighbors sharing an edge with the cell are considered.
     */
    EDGES_ONLY("neighborhoodmode.edgesonly"),

    /**
     * Neighbors sharing either an edge or a vertex with the cell are considered.
     */
    EDGES_AND_VERTICES("neighborhoodmode.edgesandvertices");

    private final String resourceKey;

    NeighborhoodMode(String resourceKey) {
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
        return AppLocalizationKeys.ENUM_LABEL_NEIGHBORHOODMODE;
    }

    /**
     * Returns whether this neighborhood mode includes neighbors that share only a vertex (not just an edge).
     *
     * @return true if neighbors sharing a vertex are included, false otherwise
     */
    public boolean includesVertexNeighbors() {
        return this == EDGES_AND_VERTICES;
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
