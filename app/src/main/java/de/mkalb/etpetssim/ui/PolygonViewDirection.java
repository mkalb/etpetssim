package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.engine.CellShape;

/**
 * Specifies the viewing direction of a polygon ({@link CellShape}) within the grid.
 * <p>
 * This direction determines which vertices and edges are considered
 * when rendering or interacting with the shape.
 * <p>
 * For {@link CellShape#SQUARE}, each direction corresponds to two vertices and one edge.
 * For {@link CellShape#TRIANGLE} and {@link CellShape#HEXAGON}, the number and position
 * of vertices and edges per direction may vary.
 * <p>
 * This enum is used to standardize directional logic across different cell shapes.
 */
public enum PolygonViewDirection {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT
}
