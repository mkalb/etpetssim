package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * An implementation of {@link WritableGridModel} that stores grid entities in a two-dimensional array.
 * Efficient for dense grids with mostly non-default entities.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link de.mkalb.etpetssim.engine.model.entity.GridEntity}
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
    public boolean isSparse() {
        return false;
    }

    @Override
    public Stream<GridCell<T>> cells() {
        // Direct array access to skip the redundant bounds check of getEntity(),
        // which is safe because all (x, y) pairs produced here are guaranteed to be valid.
        int width = structure.size().width();
        int height = structure.size().height();
        return IntStream.range(0, height)
                        .boxed()
                        .flatMap(y -> IntStream.range(0, width)
                                               .mapToObj(x -> {
                                                   @SuppressWarnings("unchecked")
                                                   T entity = (T) data[y][x];
                                                   return new GridCell<>(new GridCoordinate(x, y), entity);
                                               }));
    }

    @Override
    public Stream<GridCell<T>> nonDefaultCells() {
        // Check entity before creating GridCell to avoid allocating objects for default cells.
        // Explicit nested loop also avoids the Integer boxing overhead of IntStream.boxed().flatMap(...).
        // No snapshot is needed: arrays have no fail-fast iterators, so there is no ConcurrentModificationException
        // risk if the caller mutates the model (via setEntity / setEntityToDefault) during stream consumption.
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCell<T>> result = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                if (!Objects.equals(entity, defaultEntity)) {
                    result.add(new GridCell<>(new GridCoordinate(x, y), entity));
                }
            }
        }
        return result.stream();
    }

    @Override
    public Set<GridCoordinate> nonDefaultCoordinates() {
        // No snapshot copy is needed for ConcurrentModificationException safety:
        // the array backing has no fail-fast iterators, unlike the HashMap in SparseGridModel.
        int width = structure.size().width();
        int height = structure.size().height();
        Set<GridCoordinate> result = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
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
    public long countEntities(Predicate<? super T> predicate) {
        int width = structure.size().width();
        int height = structure.size().height();
        long count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                if (predicate.test(entity)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public List<GridCoordinate> filteredCoordinates(Predicate<T> entityPredicate) {
        int width = structure.size().width();
        int height = structure.size().height();
        // Pre-size with full grid area: ArrayGridModel is for dense grids where the predicate typically
        // matches most cells, so pre-sizing avoids repeated ArrayList resizing (up to ~20 resizes for 1M cells).
        List<GridCoordinate> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                if (entityPredicate.test(entity)) {
                    result.add(new GridCoordinate(x, y));
                }
            }
        }
        return result;
    }

    @Override
    public List<GridCell<T>> filteredCells(Predicate<T> entityPredicate) {
        int width = structure.size().width();
        int height = structure.size().height();
        // Pre-size with full grid area: ArrayGridModel is for dense grids where the predicate typically
        // matches most cells, so pre-sizing avoids repeated ArrayList resizing (up to ~20 resizes for 1M cells).
        List<GridCell<T>> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                if (entityPredicate.test(entity)) {
                    result.add(new GridCell<>(new GridCoordinate(x, y), entity));
                }
            }
        }
        return result;
    }

    @Override
    public List<GridCell<T>> filteredCellsSortedBy(Predicate<T> entityPredicate, Comparator<GridCell<T>> cellOrdering) {
        List<GridCell<T>> result = filteredCells(entityPredicate);
        result.sort(cellOrdering);
        return result;
    }

    @Override
    public Optional<GridCoordinate> findRandomDefaultCoordinate(Random random) {
        // Reservoir sampling (k=1): O(N) time, O(1) extra space.
        // Direct array access avoids the bounds check overhead of isDefaultEntity().
        int width = structure.size().width();
        int height = structure.size().height();
        GridCoordinate selected = null;
        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                T entity = (T) data[y][x];
                if (Objects.equals(entity, defaultEntity)) {
                    count++;
                    // Replace current candidate with probability 1/count.
                    if (random.nextInt(count) == 0) {
                        selected = new GridCoordinate(x, y);
                    }
                }
            }
        }
        return Optional.ofNullable(selected);
    }

    @Override
    public ArrayGridModel<T> copy() {
        // Entity instances in the copy share the same references as this model (shallow copy).
        // This is safe because GridEntity implementations are treated as immutable value types.
        ArrayGridModel<T> clone = new ArrayGridModel<>(structure, defaultEntity);
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            System.arraycopy(data[y], 0, clone.data[y], 0, width);
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
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y][x] = supplier.get();
            }
        }
    }

    @Override
    public void fill(Function<GridCoordinate, T> mapper) {
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y][x] = mapper.apply(new GridCoordinate(x, y));
            }
        }
    }

    @Override
    public void clear() {
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            Arrays.fill(data[y], defaultEntity);
        }
    }

    @Override
    public void swapInputCellEntities(GridCell<T> cellA, GridCell<T> cellB) {
        GridCoordinate coordinateA = cellA.coordinate();
        GridCoordinate coordinateB = cellB.coordinate();
        if (!structure.isCoordinateValid(coordinateA)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinateA + " for structure: " + structure);
        }
        if (!structure.isCoordinateValid(coordinateB)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinateB + " for structure: " + structure);
        }
        data[coordinateA.y()][coordinateA.x()] = cellB.entity();
        data[coordinateB.y()][coordinateB.x()] = cellA.entity();
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
