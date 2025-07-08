package de.mkalb.etpetssim.engine;

/**
 * Represents a direction in a two-dimensional grid using integer deltas.
 * <p>
 * Each instance defines a relative movement along the x and y axes.
 * Positive and negative values indicate direction along each axis.
 * <p>
 * <strong>Note:</strong> The interpretation of a direction depends on the {@link CellShape}
 * and may also be affected by the position on the grid (e.g., for hexagonal or triangular grids).
 *
 * @param dx the change in the x-coordinate (horizontal direction)
 * @param dy the change in the y-coordinate (vertical direction)
 */
public record GridDirection(int dx, int dy) {
}
