package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;
import java.util.function.*;

/**
 * An implementation of {@link WritableGridModel} that stores only non-default entities in a map.
 * Efficient for sparse grids where most cells contain the default entity.
 *
 * @param <T> the type of entities stored in the grid, must implement {@link de.mkalb.etpetssim.engine.model.entity.GridEntity}
 */
public final class SparseGridModel<T extends GridEntity> implements WritableGridModel<T> {

    /**
     * Maximum number of random probes attempted in phase 1 of {@link #findRandomDefaultCoordinate(Random)}
     * before falling back to the guaranteed linear scan in phase 2.
     */
    private static final int MAX_RANDOM_DEFAULT_SAMPLING_ATTEMPTS = 64;

    /**
     * The structure describing the grid's dimensions and valid coordinates.
     */
    private final GridStructure structure;

    /**
     * The default entity for all grid cells.
     */
    private final T defaultEntity;

    /**
     * The map holding non-default grid entities, keyed by coordinate.
     */
    private final Map<GridCoordinate, T> data;

    /**
     * Constructs a new {@code SparseGridModel} with the given structure and default entity.
     * Initially, all cells are set to the default entity (i.e., the map is empty).
     *
     * @param structure     the grid structure
     * @param defaultEntity the default entity for all cells
     */
    public SparseGridModel(GridStructure structure, T defaultEntity) {
        this.structure = structure;
        this.defaultEntity = defaultEntity;
        data = new HashMap<>();
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
    public T getEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return data.getOrDefault(coordinate, defaultEntity);
    }

    @Override
    public boolean isDefaultEntity(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        return !data.containsKey(coordinate);
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public long countEntities(Predicate<? super T> predicate) {
        // Sparse optimization: only iterate the non-default entries stored in the map (O(non-default)).
        // All cells not present in the map hold the default entity; their count is derived arithmetically.
        // This avoids iterating all cells (e.g. 1_000_000 for a 1000x1000 grid) when only a few are non-default.
        long count = 0;
        for (T entity : data.values()) {
            if (predicate.test(entity)) {
                count++;
            }
        }
        if (predicate.test(defaultEntity)) {
            count += (long) structure.size().area() - data.size();
        }
        return count;
    }

    @Override
    public Set<GridCoordinate> nonDefaultCoordinates() {
        // Returns a HashSet snapshot instead of the live key set view: prevents ConcurrentModificationException
        // if the caller iterates the returned set and mutates the model in the same loop (e.g. via setEntity
        // or setEntityToDefault). Java's fail-fast iterators throw ConcurrentModificationException on any
        // structural change to the backing map during iteration, even in single-threaded code.
        // HashSet is preferred over Set.copyOf(): the latter uses ImmutableCollections.SetN with open
        // addressing that has poor hash distribution for GridCoordinate, making contains() slow.
        return new HashSet<>(data.keySet());
    }

    @Override
    public List<GridCoordinate> filteredCoordinates(Predicate<T> entityPredicate) {
        boolean includeDefault = entityPredicate.test(defaultEntity);

        // Fast path: default does NOT match -> only stored non-default entries can match.
        if (!includeDefault) {
            if (data.isEmpty()) {
                return List.of();
            }
            List<GridCoordinate> result = new ArrayList<>(data.size());
            for (Map.Entry<GridCoordinate, T> entry : data.entrySet()) {
                T entity = entry.getValue();
                if (entityPredicate.test(entity)) {
                    result.add(entry.getKey());
                }
            }
            return result;
        }

        // Default matches: all cells not present in the map are included; non-default cells are tested.
        // Inline nested loop avoids the intermediate ArrayList created by coordinatesList().
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCoordinate> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                T entity = data.get(coordinate);
                if (entity == null) {
                    result.add(coordinate);
                } else if (entityPredicate.test(entity)) {
                    result.add(coordinate);
                }
            }
        }
        return result;
    }

    @Override
    public Optional<GridCoordinate> findRandomDefaultCoordinate(Random random) {
        int area = structure.size().area();
        int defaultCount = area - data.size();
        if (defaultCount <= 0) {
            return Optional.empty();
        }

        int width = structure.size().width();
        int height = structure.size().height();

        // Phase 1 – random sampling: try up to MAX_RANDOM_DEFAULT_SAMPLING_ATTEMPTS random positions.
        // Succeeds quickly when the grid is sparsely populated (few non-default cells).
        int maxAttempts = Math.min(area, MAX_RANDOM_DEFAULT_SAMPLING_ATTEMPTS);
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            GridCoordinate coordinate = new GridCoordinate(random.nextInt(width), random.nextInt(height));
            if (!data.containsKey(coordinate)) {
                return Optional.of(coordinate);
            }
        }

        // Phase 2 – linear fallback: scan the full grid from a random start position.
        // Guarantees a result when random sampling fails (e.g. when the grid is nearly full).
        int startIndex = random.nextInt(area);
        for (int offset = 0; offset < area; offset++) {
            int index = (startIndex + offset) % area;
            int y = index / width;
            int x = index % width;
            GridCoordinate coordinate = new GridCoordinate(x, y);
            if (!data.containsKey(coordinate)) {
                return Optional.of(coordinate);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<GridCell<T>> allCells() {
        // Use data.getOrDefault() directly to skip the redundant bounds check of getEntity(),
        // which is safe because all (x, y) pairs produced here are guaranteed to be valid.
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCell<T>> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                result.add(new GridCell<>(coordinate, data.getOrDefault(coordinate, defaultEntity)));
            }
        }
        return result;
    }

    @Override
    public List<GridCell<T>> nonDefaultCells() {
        // Snapshot the entries into a new list: prevents ConcurrentModificationException
        // if the caller mutates the model (e.g. via setEntity or setEntityToDefault) while
        // iterating the returned list. Java's fail-fast iterators throw
        // ConcurrentModificationException on any structural change during iteration, even in
        // single-threaded code. For a sparse model the non-default set is small, so the copy is cheap.
        List<GridCell<T>> snapshot = new ArrayList<>(data.size());
        for (Map.Entry<GridCoordinate, T> entry : data.entrySet()) {
            snapshot.add(new GridCell<>(entry.getKey(), entry.getValue()));
        }
        return snapshot;
    }

    @Override
    public List<GridCell<T>> filteredCells(Predicate<T> entityPredicate) {
        boolean includeDefault = entityPredicate.test(defaultEntity);

        // Fast path: default does NOT match -> only stored non-default entries can match.
        if (!includeDefault) {
            if (data.isEmpty()) {
                // Return a mutable empty list: filteredCellsSortedBy sorts the result in-place.
                return new ArrayList<>(0);
            }
            List<GridCell<T>> result = new ArrayList<>(data.size());
            for (Map.Entry<GridCoordinate, T> entry : data.entrySet()) {
                T entity = entry.getValue();
                if (entityPredicate.test(entity)) {
                    result.add(new GridCell<>(entry.getKey(), entity));
                }
            }
            return result;
        }

        // Default matches: all cells not present in the map are included; non-default cells are tested.
        // Inline nested loop avoids the intermediate ArrayList created by coordinatesList().
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCell<T>> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                T entity = data.get(coordinate);
                if (entity == null) {
                    result.add(new GridCell<>(coordinate, defaultEntity));
                } else if (entityPredicate.test(entity)) {
                    result.add(new GridCell<>(coordinate, entity));
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
    public SparseGridModel<T> copy() {
        SparseGridModel<T> clone = new SparseGridModel<>(structure, defaultEntity);
        clone.data.putAll(data);
        return clone;
    }

    @Override
    public SparseGridModel<T> copyWithDefaultEntity() {
        return new SparseGridModel<>(structure, defaultEntity);
    }

    @Override
    public void setEntity(GridCoordinate coordinate, T entity) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        if (entity.equals(defaultEntity)) {
            data.remove(coordinate);
        } else {
            data.put(coordinate, entity);
        }
    }

    @Override
    public void setEntityToDefault(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException("Coordinate out of bounds: " + coordinate + " for structure: " + structure);
        }
        data.remove(coordinate);
    }

    @Override
    public void fill(T entity) {
        data.clear();
        if (!entity.equals(defaultEntity)) {
            structure.coordinatesStream().forEach(coordinate -> data.put(coordinate, entity));
        }
    }

    @Override
    public void fill(Supplier<T> supplier) {
        // Inline nested loop avoids the intermediate ArrayList created by coordinatesList().
        data.clear();
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                T entity = supplier.get();
                if (!entity.equals(defaultEntity)) {
                    data.put(new GridCoordinate(x, y), entity);
                }
            }
        }
    }

    @Override
    public void fill(Function<GridCoordinate, T> mapper) {
        // Inline nested loop avoids the intermediate ArrayList created by coordinatesList().
        data.clear();
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                T entity = mapper.apply(coordinate);
                if (!entity.equals(defaultEntity)) {
                    data.put(coordinate, entity);
                }
            }
        }
    }

    @Override
    public void clear() {
        data.clear();
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
        T entityA = cellA.entity();
        T entityB = cellB.entity();

        if (entityB.equals(defaultEntity)) {
            data.remove(coordinateA);
        } else {
            data.put(coordinateA, entityB);
        }

        if (entityA.equals(defaultEntity)) {
            data.remove(coordinateB);
        } else {
            data.put(coordinateB, entityA);
        }
    }

    @Override
    public String toString() {
        return "SparseGridModel{" +
                "structure=" + structure +
                ", defaultEntity=" + defaultEntity +
                ", data.size()=" + data.size() +
                '}';
    }

}
