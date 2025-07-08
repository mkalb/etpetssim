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
     * Returns whether this neighborhood mode includes neighbors that share only a vertex (not just an edge).
     *
     * @return true if neighbors sharing a vertex are included, false otherwise
     */
    public boolean isVertexNeighborIncluded() {
        return this == EDGES_AND_VERTICES;
    }

    /**
     * Estimates the maximum number of neighbors for a given cell shape and radius.
     *
     * @param cellShape the geometric shape of the cell (TRIANGLE, SQUARE, HEXAGON)
     * @param radius the neighborhood radius (must be &gt; 0)
     * @return the estimated maximum number of neighbors
     */
    public int maxNeighborCount(CellShape cellShape, int radius) {
        if (radius <= 0) {
            return 0;
        }

        return switch (cellShape) {
            case TRIANGLE -> {
                // For triangles: degree = 3 (edges only) or 12 (edges and vertices)
                // Formula: 1 + ((degree * radius * (radius + 1)) / 2)
                int degree = (this == EDGES_ONLY) ? 3 : 12;
                yield 1 + ((degree * radius * (radius + 1)) / 2);
            }
            case SQUARE -> {
                // For squares: degree = 4 (edges only) or 8 (edges and vertices)
                // Formula: 1 + ((degree * radius * (radius + 1)) / 2)
                int degree = (this == EDGES_ONLY) ? 4 : 8;
                yield 1 + ((degree * radius * (radius + 1)) / 2);
            }
            case HEXAGON ->
                // For hexagons: always 6 neighbors per ring, so degree = 6
                // Formula: 1 + (3 * radius * (radius + 1))
                    1 + (3 * radius * (radius + 1));
        };
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
