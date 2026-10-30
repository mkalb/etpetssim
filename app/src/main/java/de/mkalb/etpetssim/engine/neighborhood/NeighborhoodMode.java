package de.mkalb.etpetssim.engine.neighborhood;

/**
 * Defines the neighborhood mode for cell-based simulations.
 * Determines which neighboring cells are considered adjacent:
 * either only those sharing an edge, or those sharing an edge or a vertex.
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
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "neighborhoodmode.label";
    }

    /**
     * Returns whether this neighborhood mode includes neighbors that share only a vertex (not just an edge).
     *
     * @return {@code true} if neighbors sharing a vertex are included, {@code false} otherwise
     */
    public boolean includesVertexNeighbors() {
        return this == EDGES_AND_VERTICES;
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
