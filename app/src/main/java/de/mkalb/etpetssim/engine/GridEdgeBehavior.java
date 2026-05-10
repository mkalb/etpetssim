package de.mkalb.etpetssim.engine;

import de.mkalb.etpetssim.core.AppLocalizationKeys;

/**
 * Defines a combination of edge behaviors for the X and Y boundaries of a simulation grid.
 * Each constant specifies how entities interact with both the horizontal (X) and vertical (Y) edges,
 * enabling flexible configuration of grid boundary handling in various simulation scenarios.
 *
 * @see EdgeBehavior
 */
public enum GridEdgeBehavior {

    /**
     * Both X and Y edges are blocking. Entities cannot cross the grid boundaries.
     */
    BLOCK_XY(EdgeBehavior.BLOCK, EdgeBehavior.BLOCK, "gridedgebehavior.block_xy"),

    /**
     * Both X and Y edges wrap. Entities reappear on the opposite edge in both directions.
     */
    WRAP_XY(EdgeBehavior.WRAP, EdgeBehavior.WRAP, "gridedgebehavior.wrap_xy"),

    /**
     * Both X and Y edges absorb entities. Entities are removed when reaching any edge.
     */
    ABSORB_XY(EdgeBehavior.ABSORB, EdgeBehavior.ABSORB, "gridedgebehavior.absorb_xy"),

    /**
     * X edge is blocking, Y edge wraps. Entities reappear on the opposite Y edge.
     */
    BLOCK_X_WRAP_Y(EdgeBehavior.BLOCK, EdgeBehavior.WRAP, "gridedgebehavior.block_x_wrap_y"),

    /**
     * X edge wraps, Y edge is blocking. Entities reappear on the opposite X edge.
     */
    WRAP_X_BLOCK_Y(EdgeBehavior.WRAP, EdgeBehavior.BLOCK, "gridedgebehavior.wrap_x_block_y");

    private final EdgeBehavior edgeBehaviorX;
    private final EdgeBehavior edgeBehaviorY;
    private final String resourceKey;

    GridEdgeBehavior(EdgeBehavior edgeBehaviorX,
                     EdgeBehavior edgeBehaviorY,
                     String resourceKey) {
        this.edgeBehaviorX = edgeBehaviorX;
        this.edgeBehaviorY = edgeBehaviorY;
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
        return AppLocalizationKeys.ENUM_LABEL_GRIDEDGEBEHAVIOR;
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
    public boolean hasEqualEdgeBehaviors() {
        return edgeBehaviorX == edgeBehaviorY;
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
