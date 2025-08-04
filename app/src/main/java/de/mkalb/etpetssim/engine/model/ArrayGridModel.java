package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * An implementation of {@link GridModel} that stores grid entities in a two-dimensional array.
 * Efficient for dense grids with mostly non-default entities.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public final class ArrayGridModel<T extends GridEntity> implements GridModel<T> {

    /** The structure describing the grid's dimensions and valid coordinates. */
    private final GridStructure structure;

    /** The default entity for all grid cells. */
    private final T defaultEntity;

    /** The two-dimensional array holding the grid entities. */
    private final Object[][] data;

    /**
     * Constructs a new {@code ArrayGridModel} with the given structure and default entity.
     * All cells are initialized to the default entity.
     *
     * @param structure the grid structure
     * @param defaultEntity the default entity for all cells
     */
    public ArrayGridModel(GridStructure structure, T defaultEntity) {
        this.structure = structure;
        this.defaultEntity = defaultEntity;
        data = new Object[structure.size().height()][structure.size().width()];
        clear();
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public T defaultEntity() {
        return defaultEntity;
    }

    /**
     * Creates a deep copy of this grid model, including all entities.
     *
     * @return a new {@code ArrayGridModel} with the same structure, default entity, and data
     */
    @Override
    public ArrayGridModel<T> copy() {
        ArrayGridModel<T> clone = new ArrayGridModel<>(structure, defaultEntity);
        for (int y = 0; y < structure.size().height(); y++) {
            System.arraycopy(data[y], 0, clone.data[y], 0, structure.size().width());
        }
        return clone;
    }

    /**
     * Creates a new {@code ArrayGridModel} with the same structure and default entity,
     * but with all cells set to the default entity.
     *
     * @return a blank copy of this grid model
     */
    @Override
    public ArrayGridModel<T> copyWithDefaultEntity() {
        return new ArrayGridModel<>(structure, defaultEntity);
    }

    /**
     * Returns the entity at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @return the entity at the given coordinate
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return (T) data[coordinate.y()][coordinate.x()];
    }

    /**
     * Sets the entity at the specified coordinate.
     *
     * @param coordinate the grid coordinate
     * @param entity the entity to set
     * @throws IndexOutOfBoundsException if the coordinate is not valid in this grid
     */
    @Override
    public void setEntity(GridCoordinate coordinate, T entity) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        data[coordinate.y()][coordinate.x()] = entity;
    }

    /**
     * Sets all grid cells to the default entity.
     */
    @Override
    public void clear() {
        for (int y = 0; y < structure.size().height(); y++) {
            Arrays.fill(data[y], defaultEntity);
        }
    }

    /**
     * Sets all grid cells to the specified entity.
     *
     * @param entity the entity to set
     */
    @Override
    public void fill(T entity) {
        for (Object[] row : data) {
            Arrays.fill(row, entity);
        }
    }

    /**
     * Indicates that this grid model is not sparse.
     *
     * @return {@code false}, as this implementation is for dense grids
     */
    @Override
    public boolean isModelSparse() {
        return false;
    }

    /**
     * Swaps the entities at the coordinates of the two given {@link GridCell} objects.
     * <p>
     * After execution, the entity from {@code cellA} will be at {@code cellB}'s coordinate,
     * and the entity from {@code cellB} will be at {@code cellA}'s coordinate.
     * </p>
     *
     * @param cellA the first grid cell whose entity and coordinate are involved in the swap
     * @param cellB the second grid cell whose entity and coordinate are involved in the swap
     * @throws IndexOutOfBoundsException if either coordinate is not valid in this grid
     */
    @Override
    public void swapEntities(GridCell<T> cellA, GridCell<T> cellB) {
        GridCoordinate coordinateA = cellA.coordinate();
        GridCoordinate coordinateB = cellB.coordinate();
        if (!structure.isCoordinateValid(coordinateA)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinateA + " for structure: " + structure);
        }
        if (!structure.isCoordinateValid(coordinateB)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinateB + " for structure: " + structure);
        }
        int xA = coordinateA.x();
        int yA = coordinateA.y();
        int xB = coordinateB.x();
        int yB = coordinateB.y();
        Object temp = data[yA][xA];
        data[yA][xA] = data[yB][xB];
        data[yB][xB] = temp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<GridCell<T>> nonDefaultCells() {
        return IntStream.range(0, structure.size().height())
                        .boxed()
                        .flatMap(y -> IntStream.range(0, structure.size().width())
                                               .mapToObj(x -> new GridCell<>(new GridCoordinate(x, y), (T) data[y][x]))
                                               .filter(cell -> !Objects.equals(cell.entity(), defaultEntity)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<GridCell<T>> filteredAndSortedCells(Predicate<T> entityPredicate, Comparator<GridCell<T>> cellOrdering) {
        List<GridCell<T>> result = new ArrayList<>();
        for (int y = 0; y < structure.size().height(); y++) {
            for (int x = 0; x < structure.size().width(); x++) {
                T entity = (T) data[y][x];
                if (entityPredicate.test(entity)) {
                    result.add(new GridCell<>(new GridCoordinate(x, y), entity));
                }
            }
        }
        result.sort(cellOrdering);
        return result;
    }

    /**
     * Returns a string representation of this grid model, including its structure and default entity.
     *
     * @return a string representation of this grid model
     */
    @Override
    public String toString() {
        return "ArrayGridModel{" +
                "structure=" + structure +
                ", defaultEntity=" + defaultEntity +
                ", data.length=" + data.length +
                '}';
    }

}
