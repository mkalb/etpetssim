package de.mkalb.etpetssim.engine.model;

import java.util.*;

/**
 * Provides common strategies for ordering agent grid cells in a simulation.
 * <p>
 * This utility class offers reusable {@link Comparator} implementations for
 * sorting {@link GridCell} instances by position, entity class, or descriptor ID.
 * These strategies can be used to control the order in which agents are processed
 * during simulation steps.
 * <p>
 * It is used by {@link AsynchronousStepRunner} to determine the order in which
 * agents are processed in the grid model.
 *
 * @see GridCell
 * @see GridEntity
 * @see AsynchronousStepRunner
 */
public final class AgentOrderingStrategies {

    /**
     * Private constructor to prevent instantiation.
     */
    private AgentOrderingStrategies() {
    }

    /**
     * Returns a comparator that orders grid cells by their position in the grid.
     * <p>
     * Cells are first compared by their y-coordinate, then by their x-coordinate.
     *
     * @param <T> the type of entity stored in the grid cell
     * @return a comparator for ordering grid cells by position
     */
    public static <T extends GridEntity> Comparator<GridCell<T>> byPosition() {
        return Comparator.comparingInt((GridCell<T> cell) -> cell.coordinate().y())
                         .thenComparingInt(cell -> cell.coordinate().x());
    }

    /**
     * Returns a comparator that orders grid cells by the simple class name of their entity.
     * <p>
     * This strategy groups cells by the type of entity they contain.
     *
     * @param <T> the type of entity stored in the grid cell
     * @return a comparator for ordering grid cells by entity class name
     */
    public static <T extends GridEntity> Comparator<GridCell<T>> byEntityClass() {
        return Comparator.comparing((GridCell<T> cell) -> cell.entity().getClass().getSimpleName());
    }

    /**
     * Returns a comparator that orders grid cells by the descriptor ID of their entity.
     * <p>
     * This strategy sorts cells based on the unique descriptor ID of the contained entity.
     *
     * @param <T> the type of entity stored in the grid cell
     * @return a comparator for ordering grid cells by entity descriptor ID
     */
    public static <T extends GridEntity> Comparator<GridCell<T>> byDescriptorId() {
        return Comparator.comparing(cell -> cell.entity().descriptorId());
    }

}
