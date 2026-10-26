package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;

/**
 * Comparators that define the order in which agent grid cells are processed in a simulation step.
 *
 * <p>The comparators sort {@link GridCell} instances by position, entity class, or descriptor ID.
 *
 * <p>The comparators are intended to be passed to {@link de.mkalb.etpetssim.engine.executor.AsynchronousStepRunner}
 * to determine the order in which agents are processed in the grid model.
 *
 * @see GridCell
 * @see GridEntity
 * @see de.mkalb.etpetssim.engine.executor.AsynchronousStepRunner
 */
public final class AgentOrderingStrategies {

    private AgentOrderingStrategies() {
    }

    /**
     * Returns a comparator that orders grid cells by their position in the grid.
     *
     * <p>Cells are first compared by their y-coordinate, then by their x-coordinate.
     *
     * @param <ENT> the type of entity stored in the grid cell
     * @return a comparator for ordering grid cells by position
     */
    public static <ENT extends GridEntity> Comparator<GridCell<ENT>> byPosition() {
        return Comparator.comparingInt((GridCell<ENT> cell) -> cell.coordinate().y())
                         .thenComparingInt(cell -> cell.coordinate().x());
    }

    /**
     * Returns a comparator that orders grid cells by the simple class name of their entity.
     *
     * <p>Groups cells by the type of entity they contain.
     *
     * @param <ENT> the type of entity stored in the grid cell
     * @return a comparator for ordering grid cells by entity class name
     */
    public static <ENT extends GridEntity> Comparator<GridCell<ENT>> byEntityClass() {
        return Comparator.comparing((GridCell<ENT> cell) -> cell.entity().getClass().getSimpleName());
    }

    /**
     * Returns a comparator that orders grid cells by the descriptor ID of their entity.
     *
     * <p>Sorts cells based on the unique descriptor ID of the contained entity.
     *
     * @param <ENT> the type of entity stored in the grid cell
     * @return a comparator for ordering grid cells by entity descriptor ID
     */
    public static <ENT extends GridEntity> Comparator<GridCell<ENT>> byDescriptorId() {
        return Comparator.comparing(GridCell::descriptorId);
    }

}
