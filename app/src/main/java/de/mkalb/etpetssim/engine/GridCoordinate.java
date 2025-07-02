package de.mkalb.etpetssim.engine;

import java.util.*;

/**
 * Represents a coordinate in a grid for the simulation.
 * The coordinate is defined by its x and y values.
 * Negative coordinates are considered illegal.
 *
 * @param x the x-coordinate of the grid
 * @param y the y-coordinate of the grid
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
        Objects.requireNonNull(minCorner);
        Objects.requireNonNull(maxCorner);
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
        Objects.requireNonNull(exclusiveCorner);
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
        return (y % 2) == 0;
    }

    /**
     * Returns the logical triangle row index for this coordinate.
     * In a triangle grid, each logical row consists of two stacked triangle rows.
     * This method divides the y-coordinate by 2 to determine the triangle row.
     *
     * @return the logical triangle row index
     */
    public int triangleRow() {
        return y / 2;
    }

    /**
     * Determines whether a triangle cell at this coordinate should be horizontally offset.
     * In a flat-topped triangle grid, every two rows form a repeating vertical pattern.
     * Depending on the row's position in the 4-row cycle, some rows are horizontally offset by half a cell
     * to ensure that upward- and downward-pointing triangles interlock correctly.
     *
     * @return {@code true} if the triangle cell at this coordinate is horizontally offset, {@code false} otherwise
     */
    public boolean hasTriangleCellXOffset() {
        int triangleOrientationCycle = y % 4;
        return ((triangleOrientationCycle == 1) || (triangleOrientationCycle == 2));
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
        Objects.requireNonNull(minCorner);
        Objects.requireNonNull(maxCorner);
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
     * @param dx the amount to add to the x-coordinate
     * @param dy the amount to add to the y-coordinate
     * @return a new GridCoordinate with x + dx and y + dy
     */
    public GridCoordinate offset(int dx, int dy) {
        return new GridCoordinate(x + dx, y + dy);
    }

    /**
     * Returns a string representation of the coordinate in the format "(x, y)".
     * Example: (15, 20)
     *
     * @return a string representation of the coordinate
     */
    public String asString() {
        return String.format("(%d, %d)", x, y);
    }

}
