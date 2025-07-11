package de.mkalb.etpetssim.engine;

/**
 * Represents an immutable offset (delta) in a two-dimensional simulation grid.
 * <p>
 * Each offset is defined by its {@code dx} (horizontal delta) and {@code dy} (vertical delta) values.
 * Positive and negative values indicate direction along each axis.
 * <p>
 * The interpretation of an offset depends on the {@link CellShape}
 * and may also be affected by the position on the grid (e.g., for hexagonal or triangular grids).
 *
 * @param dx the change in the x-coordinate (horizontal direction)
 * @param dy the change in the y-coordinate (vertical direction)
 *
 * @see CellShape
 * @see GridCoordinate#offset(GridOffset)
 */
public record GridOffset(int dx, int dy) {

    /**
     * Calculates the offset (delta) between two {@link GridCoordinate} instances.
     * <p>
     * The offset is computed as the difference of the x and y coordinates:
     * dx = to.x - from.x, dy = to.y - from.y.
     *
     * @param from the starting coordinate
     * @param to   the target coordinate
     * @return a new {@code GridOffset} representing the difference between the two coordinates
     */
    public static GridOffset between(GridCoordinate from, GridCoordinate to) {
        return new GridOffset(to.x() - from.x(), to.y() - from.y());
    }

    /**
     * Returns a new GridOffset that is the sum of this and another offset.
     *
     * @param other the offset to add
     * @return a new GridOffset representing the sum
     */
    public GridOffset add(GridOffset other) {
        return new GridOffset(dx + other.dx, dy + other.dy);
    }

    /**
     * Returns a new GridOffset that is the difference between this and another offset.
     *
     * @param other the offset to subtract
     * @return a new GridOffset representing the difference
     */
    public GridOffset subtract(GridOffset other) {
        return new GridOffset(dx - other.dx, dy - other.dy);
    }

    /**
     * Returns a new GridOffset that is the negation of this offset.
     *
     * @return a new GridOffset with both dx and dy negated
     */
    public GridOffset negate() {
        return new GridOffset(-dx, -dy);
    }

    /**
     * Returns a new GridOffset scaled by the given factor.
     *
     * @param factor the scale factor
     * @return a new GridOffset with dx and dy multiplied by factor
     */
    public GridOffset scale(int factor) {
        return new GridOffset(dx * factor, dy * factor);
    }

    /**
     * Checks if this offset is zero (no movement).
     *
     * @return true if dx and dy are both zero, false otherwise
     */
    public boolean isZero() {
        return (dx == 0) && (dy == 0);
    }

    /**
     * Returns the Manhattan length (L1 norm) of this offset.
     * <p>
     * This method is only meaningful for grids with {@link CellShape#SQUARE}.
     *
     * @return the sum of the absolute values of dx and dy
     */
    public int manhattanLength() {
        return Math.abs(dx) + Math.abs(dy);
    }

    /**
     * Returns the Euclidean length (L2 norm) of this offset.
     * <p>
     * This method is only meaningful for grids with {@link CellShape#SQUARE}.
     *
     * @return the Euclidean distance represented by this offset
     */
    public double euclideanLength() {
        return Math.hypot(dx, dy);
    }

    /**
     * Returns a short, human-readable string representation of this offset.
     * <p>
     * Format: {@code [dx, dy]}
     * <br>
     * Example: {@code [+1, -1]}
     *
     * @return a concise display string for this offset
     */
    public String toDisplayString() {
        return String.format("[%+d, %+d]", dx, dy);
    }

}
