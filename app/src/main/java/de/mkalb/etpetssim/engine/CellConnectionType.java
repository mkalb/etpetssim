package de.mkalb.etpetssim.engine;

/**
 * Specifies how two cells in a grid are connected:
 * either via a shared edge ({@link #EDGE}) or only via a shared vertex ({@link #VERTEX}).
 * <p>
 * Used in {@link CellNeighbor} to describe the type of neighbor relationship.
 * Note: Vertex-only connections are possible for SQUARE and TRIANGLE grids.
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
     * Returns the resource key for the label of the enum {@code CellConnectionType}.
     *
     * @return the resource key for the label of this enum
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "cellconnectiontype.label";
    }

    /**
     * Returns the resource key associated with this connection type.
     *
     * @return the resource key for this connection type
     */
    public String resourceKey() {
        return resourceKey;
    }

}