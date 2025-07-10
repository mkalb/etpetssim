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
