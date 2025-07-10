package de.mkalb.etpetssim.engine;

/**
 * Defines the neighborhood mode for cell-based simulations.
 * Determines which neighboring cells are considered adjacent:
 * either only those sharing an edge, or those sharing an edge or a vertex.
 *
 * @see CellShape
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

    /**
     * Constructs a neighborhood mode with the specified resource key.
     *
     * @param resourceKey the resource key for this neighborhood mode
     */
    NeighborhoodMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum NeighborhoodMode.
     *
     * @return the resource key for the label of the enum NeighborhoodMode
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "neighborhoodmode.label";
    }

    /**
     * Returns whether this neighborhood mode includes neighbors that share only a vertex (not just an edge).
     *
     * @return true if neighbors sharing a vertex are included, false otherwise
     */
    public boolean isVertexNeighborIncluded() {
        return this == EDGES_AND_VERTICES;
    }

    /**
     * Returns the resource key associated with this neighborhood mode.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this neighborhood mode
     */
    public String resourceKey() {
        return resourceKey;
    }
}
