package de.mkalb.etpetssim.engine;

/**
 * Represents an immutable coordinate in a two-dimensional simulation grid.
 * <p>
 * Each coordinate is defined by its non-negative {@code x} (horizontal) and {@code y} (vertical) values.
 * Negative values are considered illegal and indicate an invalid or uninitialized state.
 * <p>
 * This record provides utility methods for bounds checking, coordinate arithmetic, and
 * grid-specific queries (such as cell orientation and offset for different cell shapes).
 *
 * @param x the x-coordinate (horizontal position, must be non-negative for valid coordinates)
 * @param y the y-coordinate (vertical position, must be non-negative for valid coordinates)
 *
 * @see #isIllegal()
 * @see #isWithinBounds(int, int, int, int)
 * @see #clampToBounds(int, int, int, int)
 * @see CellShape
 */
public record GridCoordinate(int x, int y) {

    /**
     * A constant for the minimum valid coordinate value.
     * Only positive coordinates are valid in the grid.
     */
    public static final int MIN_VALID_COORDINATE = 0;

    /**
     * A constant for the origin coordinate (0, 0).
     */
    public static final GridCoordinate ORIGIN = new GridCoordinate(MIN_VALID_COORDINATE, MIN_VALID_COORDINATE);

    /**
     * A constant for an illegal coordinate.
     * This coordinate is used to represent an invalid or uninitialized state.
     */
    public static final GridCoordinate ILLEGAL = new GridCoordinate(Integer.MIN_VALUE, Integer.MIN_VALUE);

    /**
     * Checks if the coordinate is illegal.
     *
     * @return true if the coordinate is illegal, false otherwise
     */
    @SuppressWarnings("ObjectEquality")
    public boolean isIllegal() {
        // Compares first with identity equality for performance. The real check is done afterward.
        return (this == ILLEGAL) || (x < MIN_VALID_COORDINATE) || (y < MIN_VALID_COORDINATE);
    }

    /**
     * Checks if this coordinate is within the rectangular bounds defined by the given
     * minimum (inclusive) and maximum (exclusive) values.
     *
     * @param minX the minimum x-coordinate (inclusive)
     * @param minY the minimum y-coordinate (inclusive)
     * @param maxX the maximum x-coordinate (exclusive)
     * @param maxY the maximum y-coordinate (exclusive)
     * @return true if the coordinate lies within the specified bounds, false otherwise
     */
    public boolean isWithinBounds(int minX, int minY, int maxX, int maxY) {
        return (x >= minX) && (x < maxX) && (y >= minY) && (y < maxY);
    }

    /**
     * Checks if this coordinate is within the rectangular bounds defined by the given
     * minimum (inclusive) corner and maximum (exclusive) corner.
     *
     * @param minCorner the minimum corner (inclusive)
     * @param maxCorner the maximum corner (exclusive)
     * @return true if this coordinate lies within the bounds, false otherwise
     */
    public boolean isWithinBounds(GridCoordinate minCorner, GridCoordinate maxCorner) {
        return isWithinBounds(minCorner.x, minCorner.y, maxCorner.x, maxCorner.y);
    }

    /**
     * Checks if this coordinate is within the rectangular bounds defined by the origin (0, 0)
     * and the given exclusive corner coordinate.
     *
     * @param exclusiveCorner the exclusive corner coordinate defining the bounds
     * @return true if this coordinate lies within the defined bounds, false otherwise
     * @see #ORIGIN
     */
    public boolean isWithinOriginBounds(GridCoordinate exclusiveCorner) {
        return isWithinBounds(ORIGIN, exclusiveCorner);
    }

    /**
     * Checks if this coordinate is in an even-numbered column.
     *
     * @return {@code true} if the x-coordinate is even, {@code false} otherwise
     */
    public boolean isEvenColumn() {
        return (x % 2) == 0;
    }

    /**
     * Checks if this coordinate is in an odd-numbered column.
     *
     * @return {@code true} if the x-coordinate is odd, {@code false} otherwise
     */
    public boolean isOddColumn() {
        return (x % 2) != 0;
    }

    /**
     * Checks if this coordinate is in an even-numbered row.
     *
     * @return {@code true} if the y-coordinate is even, {@code false} otherwise
     */
    public boolean isEvenRow() {
        return (y % 2) == 0;
    }

    /**
     * Checks if this coordinate is in an odd-numbered row.
     *
     * @return {@code true} if the y-coordinate is odd, {@code false} otherwise
     */
    public boolean isOddRow() {
        return (y % 2) != 0;
    }

    /**
     * Returns whether a triangle cell at this coordinate would be oriented with its tip downwards.
     * Only meaningful if the cell shape is {@code TRIANGLE}.
     *
     * @return {@code true} if the triangle would point down, {@code false} if up
     * @see de.mkalb.etpetssim.engine.CellShape#TRIANGLE
     */
    public boolean isTriangleCellPointingDown() {
        return (x % 2) == (y % 2);
    }

    /**
     * Determines whether a hexagon cell at this coordinate should be vertically offset.
     * In a staggered hexagon grid, rows are vertically offset in every second column to create the staggered layout.
     * Even columns start at the base Y position, odd columns are shifted down by half a hexagon height.
     *
     * @return {@code true} if the hexagon cell at this coordinate is vertically offset, {@code false} otherwise
     */
    public boolean hasHexagonCellYOffset() {
        return (x % 2) != 0;
    }

    /**
     * Clamps this coordinate to the rectangular bounds defined by the given minimum (inclusive)
     * and maximum (exclusive) values. If the coordinate lies outside the bounds, it is adjusted
     * to the nearest valid value within the bounds.
     *
     * @param minX the minimum x-coordinate (inclusive)
     * @param minY the minimum y-coordinate (inclusive)
     * @param maxX the maximum x-coordinate (exclusive)
     * @param maxY the maximum y-coordinate (exclusive)
     * @return a new GridCoordinate clamped to the specified bounds
     */
    public GridCoordinate clampToBounds(int minX, int minY, int maxX, int maxY) {
        int clampedX = Math.max(minX, Math.min(x, maxX - 1));
        int clampedY = Math.max(minY, Math.min(y, maxY - 1));
        return new GridCoordinate(clampedX, clampedY);
    }

    /**
     * Clamps this coordinate to the rectangular bounds defined by the given
     * minimum (inclusive) corner and maximum (exclusive) corner.
     *
     * @param minCorner the minimum corner (inclusive)
     * @param maxCorner the maximum corner (exclusive)
     * @return a new GridCoordinate clamped to the specified bounds
     */
    public GridCoordinate clampToBounds(GridCoordinate minCorner, GridCoordinate maxCorner) {
        return clampToBounds(minCorner.x, minCorner.y, maxCorner.x, maxCorner.y);
    }

    /**
     * Clamps this coordinate to the rectangular bounds defined by the origin (0, 0)
     * and the given exclusive corner coordinate. If the coordinate lies outside the bounds, it is adjusted
     * to the nearest valid value within the bounds.
     *
     * @param exclusiveCorner the exclusive corner coordinate defining the bounds
     * @return a new GridCoordinate clamped to the specified bounds
     * @see #ORIGIN
     */
    public GridCoordinate clampToOriginBounds(GridCoordinate exclusiveCorner) {
        return clampToBounds(ORIGIN, exclusiveCorner);
    }

    /**
     * Returns a new GridCoordinate with both x and y incremented by 1.
     * Useful when converting bounds (inclusive <-> exclusive).
     *
     * @return a new GridCoordinate with x + 1 and y + 1
     */
    public GridCoordinate incremented() {
        return new GridCoordinate(x + 1, y + 1);
    }

    /**
     * Returns a new GridCoordinate with both x and y decremented by 1.
     * Useful when converting bounds (inclusive <-> exclusive).
     *
     * @return a new GridCoordinate with x - 1 and y - 1
     */
    public GridCoordinate decremented() {
        return new GridCoordinate(x - 1, y - 1);
    }

    /**
     * Returns a new GridCoordinate offset by the given delta values.
     *
     * @param dx the amount to add to the x-coordinate (horizontal offset)
     * @param dy the amount to add to the y-coordinate (vertical offset)
     * @return a new GridCoordinate with x + dx and y + dy
     * @see #offset(GridOffset)
     */
    public GridCoordinate offset(int dx, int dy) {
        return new GridCoordinate(x + dx, y + dy);
    }

    /**
     * Returns a new GridCoordinate offset by the given GridOffset.
     *
     * @param offset the GridOffset specifying the delta for x and y
     * @return a new GridCoordinate with x + offset.dx() and y + offset.dy()
     * @see #offset(int, int)
     */
    public GridCoordinate offset(GridOffset offset) {
        return new GridCoordinate(x + offset.dx(), y + offset.dy());
    }

    /**
     * Returns the offset (delta) from this coordinate to the specified target coordinate.
     * <p>
     * The offset is computed as (target.x - this.x, target.y - this.y).
     *
     * @param target the target coordinate
     * @return a new GridOffset representing the difference from this to the target coordinate
     * @see GridOffset#between(GridCoordinate, GridCoordinate)
     */
    public GridOffset offsetTo(GridCoordinate target) {
        return GridOffset.between(this, target);
    }

    /**
     * Returns a short, human-readable string representation of this coordinate.
     * <p>
     * Format: {@code (x, y)}
     * <br>
     * Example: {@code (15, 20)}
     * <br>
     * Returns {@code (illegal)} if this coordinate is the constant {@link #ILLEGAL}.
     *
     * @return a concise display string for this coordinate
     */
    @SuppressWarnings("ObjectEquality")
    public String toDisplayString() {
        if (this == ILLEGAL) {
            return "(illegal)";
        }
        return String.format("(%d, %d)", x, y);
    }

}
