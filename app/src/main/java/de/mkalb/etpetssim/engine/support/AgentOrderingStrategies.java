package de.mkalb.etpetssim.engine.support;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.Comparator;

/**
 * Comparators that define the order in which agent grid cells are processed in a simulation step.
 *
 * <p>The comparators sort {@link GridCell} instances by position or by descriptor ID.
 *
 * <p>The comparators are intended to be passed to {@code AsynchronousStepRunner} or to
 * {@code filteredCellsSortedBy(..)} of a grid model to determine the order in which agents are processed.
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
     * Returns a comparator that orders grid cells by the descriptor ID of their entity, which groups the cells by
     * the kind of entity.
     *
     * <p>Cells with the same descriptor ID are equal for this comparator; append
     * {@code .thenComparing(byPosition())} for a deterministic order within a group.
     *
     * @param <ENT> the type of entity stored in the grid cell
     * @return a comparator for ordering grid cells by entity descriptor ID
     */
    public static <ENT extends GridEntity> Comparator<GridCell<ENT>> byDescriptorId() {
        return Comparator.comparing(GridCell::descriptorId);
    }

}
