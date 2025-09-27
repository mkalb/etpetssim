package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;

/**
 * Specifies the side of a {@link CellShape} as viewed within the grid.
 * <p>
 * This enum standardizes directional logic for rendering and interaction
 * across different cell shapes.
 * <p>
 * For {@link CellShape#SQUARE}, each side corresponds to two vertices and one edge.
 * For {@link CellShape#TRIANGLE} and {@link CellShape#HEXAGON}, the number and position
 * of vertices and edges per side may vary.
 */
public enum CellShapeSide {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
}
