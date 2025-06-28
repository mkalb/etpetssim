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
