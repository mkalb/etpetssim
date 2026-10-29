package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An implementation of {@link WritableGridModel} that stores grid entities in a two-dimensional array.
 * Efficient for dense grids with mostly non-default entities.
 *
 * @param <ENT> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public final class ArrayGridModel<ENT extends GridEntity> implements WritableGridModel<ENT> {

    private final GridStructure structure;

    private final ENT defaultEntity;

    private final Object[][] data;

    /**
     * Creates a model whose cells all hold the default entity.
     *
     * @param structure     the grid structure
     * @param defaultEntity the default entity for all cells
     */
    public ArrayGridModel(GridStructure structure, ENT defaultEntity) {
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
    public ENT defaultEntity() {
        return defaultEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ENT getEntity(GridCoordinate coordinate) {
        checkCoordinate(coordinate);
        return (ENT) data[coordinate.y()][coordinate.x()];
    }

    @Override
    public boolean isDefaultEntity(GridCoordinate coordinate) {
        checkCoordinate(coordinate);
        return Objects.equals(data[coordinate.y()][coordinate.x()], defaultEntity);
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public long countEntities(Predicate<ENT> entityPredicate) {
        int width = structure.size().width();
        int height = structure.size().height();
        long count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                ENT entity = (ENT) data[y][x];
                if (entityPredicate.test(entity)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Set<GridCoordinate> nonDefaultCoordinates() {
        int width = structure.size().width();
        int height = structure.size().height();
        Set<GridCoordinate> result = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                ENT entity = (ENT) data[y][x];
                if (!Objects.equals(entity, defaultEntity)) {
                    result.add(new GridCoordinate(x, y));
                }
            }
        }
        return result;
    }

    @Override
    public List<GridCoordinate> filteredCoordinates(Predicate<ENT> entityPredicate) {
        int width = structure.size().width();
        int height = structure.size().height();
        // Pre-size with full grid area: ArrayGridModel is for dense grids where the predicate typically
        // matches most cells, so pre-sizing avoids repeated ArrayList resizing (up to ~20 resizes for 1M cells).
        List<GridCoordinate> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                ENT entity = (ENT) data[y][x];
                if (entityPredicate.test(entity)) {
                    result.add(new GridCoordinate(x, y));
                }
            }
        }
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
                ENT entity = (ENT) data[y][x];
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
    public List<GridCell<ENT>> allCells() {
        // Direct array access to skip the redundant bounds check of getEntity(),
        // which is safe because all (x, y) pairs produced here are guaranteed to be valid.
        // Pre-size with full grid area: ArrayGridModel is for dense grids, so all cells are included.
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCell<ENT>> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                ENT entity = (ENT) data[y][x];
                result.add(new GridCell<>(new GridCoordinate(x, y), entity));
            }
        }
        return result;
    }

    @Override
    public List<GridCell<ENT>> nonDefaultCells() {
        // Check entity before creating GridCell to avoid allocating objects for default cells.
        // Explicit nested loop also avoids the Integer boxing overhead of IntStream.boxed().flatMap(...).
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCell<ENT>> result = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                ENT entity = (ENT) data[y][x];
                if (!Objects.equals(entity, defaultEntity)) {
                    result.add(new GridCell<>(new GridCoordinate(x, y), entity));
                }
            }
        }
        return result;
    }

    @Override
    public List<GridCell<ENT>> filteredCells(Predicate<ENT> entityPredicate) {
        int width = structure.size().width();
        int height = structure.size().height();
        // Pre-size with full grid area: ArrayGridModel is for dense grids where the predicate typically
        // matches most cells, so pre-sizing avoids repeated ArrayList resizing (up to ~20 resizes for 1M cells).
        List<GridCell<ENT>> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                @SuppressWarnings("unchecked")
                ENT entity = (ENT) data[y][x];
                if (entityPredicate.test(entity)) {
                    result.add(new GridCell<>(new GridCoordinate(x, y), entity));
                }
            }
        }
        return result;
    }

    @Override
    public List<GridCell<ENT>> filteredCellsSortedBy(Predicate<ENT> entityPredicate, Comparator<GridCell<ENT>> cellOrdering) {
        List<GridCell<ENT>> result = filteredCells(entityPredicate);
        result.sort(cellOrdering);
        return result;
    }

    @Override
    public ArrayGridModel<ENT> copy() {
        ArrayGridModel<ENT> clone = new ArrayGridModel<>(structure, defaultEntity);
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            System.arraycopy(data[y], 0, clone.data[y], 0, width);
        }
        return clone;
    }

    @Override
    public ArrayGridModel<ENT> copyWithDefaultEntity() {
        return new ArrayGridModel<>(structure, defaultEntity);
    }

    @Override
    public void setEntity(GridCoordinate coordinate, ENT entity) {
        checkCoordinate(coordinate);
        data[coordinate.y()][coordinate.x()] = entity;
    }

    @Override
    public void setEntityToDefault(GridCoordinate coordinate) {
        checkCoordinate(coordinate);
        data[coordinate.y()][coordinate.x()] = defaultEntity;
    }

    @Override
    public void fill(ENT entity) {
        for (Object[] row : data) {
            Arrays.fill(row, entity);
        }
    }

    @Override
    public void fill(Supplier<ENT> entitySupplier) {
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y][x] = entitySupplier.get();
            }
        }
    }

    @Override
    public void fill(Function<GridCoordinate, ENT> mapper) {
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
        fill(defaultEntity);
    }

    @Override
    public void setSwappedEntities(GridCell<ENT> cellA, GridCell<ENT> cellB) {
        GridCoordinate coordinateA = cellA.coordinate();
        GridCoordinate coordinateB = cellB.coordinate();
        checkCoordinate(coordinateA);
        checkCoordinate(coordinateB);
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

    private void checkCoordinate(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
    }

}
