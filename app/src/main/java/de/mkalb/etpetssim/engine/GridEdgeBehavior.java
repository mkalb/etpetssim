package de.mkalb.etpetssim.engine;

/**
 * Defines a combination of edge behaviors for the X and Y boundaries of a simulation grid.
 * Each constant specifies how entities interact with both the horizontal (X) and vertical (Y) edges,
 * enabling flexible configuration of grid boundary handling in various simulation scenarios.
 *
 * <p><b>Note:</b> The combination of {@link EdgeBehavior#WRAP} and {@link EdgeBehavior#REFLECT}
 * for X and Y edges must never be used together (e.g., X=WRAP and Y=REFLECT or vice versa),
 * as this would make grid calculations extremely complex.</p>
 *
 * @see EdgeBehavior
 */
public enum GridEdgeBehavior {

    /**
     * Both X and Y edges are blocking. Entities cannot cross the grid boundaries.
     */
    BLOCK_X_BLOCK_Y(EdgeBehavior.BLOCK, EdgeBehavior.BLOCK, "gridedgebehavior.block_x_block_y"),

    /**
     * X edge is blocking, Y edge wraps. Entities reappear on the opposite Y edge.
     */
    BLOCK_X_WRAP_Y(EdgeBehavior.BLOCK, EdgeBehavior.WRAP, "gridedgebehavior.block_x_wrap_y"),

    /**
     * X edge wraps, Y edge is blocking. Entities reappear on the opposite X edge.
     */
    WRAP_X_BLOCK_Y(EdgeBehavior.WRAP, EdgeBehavior.BLOCK, "gridedgebehavior.wrap_x_block_y"),

    /**
     * Both X and Y edges wrap. Entities reappear on the opposite edge in both directions.
     */
    WRAP_X_WRAP_Y(EdgeBehavior.WRAP, EdgeBehavior.WRAP, "gridedgebehavior.wrap_x_wrap_y"),

    /**
     * Both X and Y edges absorb entities. Entities are removed when reaching any edge.
     */
    ABSORB_XY(EdgeBehavior.ABSORB, EdgeBehavior.ABSORB, "gridedgebehavior.absorb_xy"),

    /**
     * Both X and Y edges reflect entities. Entities bounce back upon reaching any edge.
     */
    REFLECT_XY(EdgeBehavior.REFLECT, EdgeBehavior.REFLECT, "gridedgebehavior.reflect_xy");

    private final EdgeBehavior edgeBehaviorX;
    private final EdgeBehavior edgeBehaviorY;
    private final String resourceKey;

    /**
     * Constructs a grid edge behavior with specified behaviors for the X and Y axes and a resource key.
     *
     * @param edgeBehaviorX the behavior for the X edge (horizontal)
     * @param edgeBehaviorY the behavior for the Y edge (vertical)
     * @param resourceKey the resource key for this grid edge behavior
     */
    GridEdgeBehavior(EdgeBehavior edgeBehaviorX, EdgeBehavior edgeBehaviorY, String resourceKey) {
        this.edgeBehaviorX = edgeBehaviorX;
        this.edgeBehaviorY = edgeBehaviorY;
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum GridEdgeBehavior.
     *
     * @return the resource key for the label of the enum GridEdgeBehavior
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "gridedgebehavior.label";
    }

    /**
     * Returns the edge behavior for the X-axis (horizontal boundary).
     *
     * @return the {@link EdgeBehavior} for the X edge
     */
    public EdgeBehavior edgeBehaviorX() {
        return edgeBehaviorX;
    }

    /**
     * Returns the edge behavior for the Y-axis (vertical boundary).
     *
     * @return the {@link EdgeBehavior} for the Y edge
     */
    public EdgeBehavior edgeBehaviorY() {
        return edgeBehaviorY;
    }

    /**
     * Checks if both X and Y edges use the same edge behavior.
     *
     * @return true if both edge behaviors are identical, false otherwise
     */
    public boolean isEqualEdgeBehavior() {
        return edgeBehaviorX == edgeBehaviorY;
    }

    /**
     * Returns the resource key associated with this grid edge behavior.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this grid edge behavior
     */
    public String resourceKey() {
        return resourceKey;
    }

}
