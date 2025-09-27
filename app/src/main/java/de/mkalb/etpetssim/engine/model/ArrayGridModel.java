package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * An implementation of {@link WritableGridModel} that stores grid entities in a two-dimensional array.
 * Efficient for dense grids with mostly non-default entities.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public final class ArrayGridModel<T extends GridEntity> implements WritableGridModel<T> {

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

    @Override
    @SuppressWarnings("unchecked")
    public T getEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return (T) data[coordinate.y()][coordinate.x()];
    }

    @Override
    public boolean isDefaultEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return Objects.equals(data[coordinate.y()][coordinate.x()], defaultEntity);
    }

    @Override
    public boolean isModelSparse() {
        return false;
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

    @Override
    public Set<GridCoordinate> nonDefaultCoordinates() {
        Set<GridCoordinate> result = new HashSet<>();
        for (int y = 0; y < structure.size().height(); y++) {
            for (int x = 0; x < structure.size().width(); x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                if (!Objects.equals(entity, defaultEntity)) {
                    result.add(new GridCoordinate(x, y));
                }
            }
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public long countCells(Predicate<? super GridCell<T>> predicate) {
        long count = 0;
        for (int y = 0; y < structure.size().height(); y++) {
            for (int x = 0; x < structure.size().width(); x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                GridCell<T> cell = new GridCell<>(new GridCoordinate(x, y), entity);
                if (predicate.test(cell)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public long countEntities(Predicate<? super T> predicate) {
        long count = 0;
        for (int y = 0; y < structure.size().height(); y++) {
            for (int x = 0; x < structure.size().width(); x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                if (predicate.test(entity)) {
                    count++;
                }
            }
        }
        return count;
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

    @Override
    public ArrayGridModel<T> copy() {
        ArrayGridModel<T> clone = new ArrayGridModel<>(structure, defaultEntity);
        for (int y = 0; y < structure.size().height(); y++) {
            System.arraycopy(data[y], 0, clone.data[y], 0, structure.size().width());
        }
        return clone;
    }

    @Override
    public ArrayGridModel<T> copyWithDefaultEntity() {
        return new ArrayGridModel<>(structure, defaultEntity);
    }

    @Override
    public void setEntity(GridCoordinate coordinate, T entity) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        data[coordinate.y()][coordinate.x()] = entity;
    }

    @Override
    public void setEntityToDefault(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        data[coordinate.y()][coordinate.x()] = defaultEntity;
    }

    @Override
    public void fill(T entity) {
        for (Object[] row : data) {
            Arrays.fill(row, entity);
        }
    }

    @Override
    public void fill(Supplier<T> supplier) {
        for (int y = 0; y < structure.size().height(); y++) {
            for (int x = 0; x < structure.size().width(); x++) {
                data[y][x] = supplier.get();
            }
        }
    }

    @Override
    public void fill(Function<GridCoordinate, T> mapper) {
        for (int y = 0; y < structure.size().height(); y++) {
            for (int x = 0; x < structure.size().width(); x++) {
                data[y][x] = mapper.apply(new GridCoordinate(x, y));
            }
        }
    }

    @Override
    public void clear() {
        for (int y = 0; y < structure.size().height(); y++) {
            Arrays.fill(data[y], defaultEntity);
        }
    }

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

    @Override
    public String toString() {
        return "ArrayGridModel{" +
                "structure=" + structure +
                ", defaultEntity=" + defaultEntity +
                ", data.length=" + data.length +
                '}';
    }

}
