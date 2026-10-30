package de.mkalb.etpetssim.engine.neighborhood;

/**
 * Specifies how two cells in a grid are connected:
 * either via a shared edge ({@link #EDGE}) or only via a shared vertex ({@link #VERTEX}).
 *
 * <p>Used in {@link CellNeighbor} to describe the type of neighbor relationship.
 * Note: Vertex-only connections are possible for SQUARE and TRIANGLE grids.
 *
 * @see NeighborhoodMode
 */
public enum CellConnectionType {

    /**
     * Connection via a shared edge (always involves two vertices).
     */
    EDGE("cellconnectiontype.edge"),

    /**
     * Connection only via a shared vertex (possible for SQUARE and TRIANGLE grids).
     */
    VERTEX("cellconnectiontype.vertex");

    private final String resourceKey;

    CellConnectionType(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "cellconnectiontype.label";
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
