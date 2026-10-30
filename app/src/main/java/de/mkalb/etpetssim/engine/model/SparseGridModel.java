package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An implementation of {@link WritableGridModel} that stores only non-default entities in a map.
 * Efficient for sparse grids where most cells contain the default entity.
 *
 * @param <ENT> the type of entities stored in the grid, must implement {@link GridEntity}
 */
public final class SparseGridModel<ENT extends GridEntity> implements WritableGridModel<ENT> {

    /**
     * Maximum number of random probes attempted in phase 1 of {@link #findRandomDefaultCoordinate(Random)}
     * before falling back to the guaranteed linear scan in phase 2.
     */
    private static final int MAX_RANDOM_DEFAULT_SAMPLING_ATTEMPTS = 64;

    private final GridStructure structure;

    private final ENT defaultEntity;

    private final Map<GridCoordinate, ENT> data;

    /**
     * Creates a model whose cells all hold the default entity.
     *
     * @param structure     the grid structure
     * @param defaultEntity the default entity for all cells
     */
    public SparseGridModel(GridStructure structure, ENT defaultEntity) {
        this.structure = structure;
        this.defaultEntity = defaultEntity;
        data = new HashMap<>();
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
    public ENT getEntity(GridCoordinate coordinate) {
        checkCoordinate(coordinate);
        return data.getOrDefault(coordinate, defaultEntity);
    }

    @Override
    public boolean isDefaultEntity(GridCoordinate coordinate) {
        checkCoordinate(coordinate);
        return !data.containsKey(coordinate);
    }

    @Override
    public boolean isSparse() {
        return true;
    }

    @Override
    public long countEntities(Predicate<ENT> entityPredicate) {
        // Sparse optimization: only iterate the non-default entries stored in the map (O(non-default)).
        // All cells not present in the map hold the default entity; their count is derived arithmetically.
        // This avoids iterating all cells (e.g. 1_000_000 for a 1000x1000 grid) when only a few are non-default.
        long count = 0;
        for (ENT entity : data.values()) {
            if (entityPredicate.test(entity)) {
                count++;
            }
        }
        if (entityPredicate.test(defaultEntity)) {
            count += (long) structure.size().area() - data.size();
        }
        return count;
    }

    @Override
    public Set<GridCoordinate> nonDefaultCoordinates() {
        // A HashSet snapshot instead of the live key set view keeps the contract of a mutable, independent set.
        // HashSet is preferred over Set.copyOf(): the latter uses ImmutableCollections.SetN with open
        // addressing that has poor hash distribution for GridCoordinate, making contains() slow.
        return new HashSet<>(data.keySet());
    }

    @Override
    public List<GridCoordinate> filteredCoordinates(Predicate<ENT> entityPredicate) {
        boolean includeDefault = entityPredicate.test(defaultEntity);

        // Fast path: default does NOT match -> only stored non-default entries can match.
        if (!includeDefault) {
            List<GridCoordinate> result = new ArrayList<>(data.size());
            for (Map.Entry<GridCoordinate, ENT> entry : data.entrySet()) {
                ENT entity = entry.getValue();
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
                ENT entity = data.get(coordinate);
                if ((entity == null) || entityPredicate.test(entity)) {
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
    public List<GridCell<ENT>> allCells() {
        // Use data.getOrDefault() directly to skip the redundant bounds check of getEntity(),
        // which is safe because all (x, y) pairs produced here are guaranteed to be valid.
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCell<ENT>> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                result.add(new GridCell<>(coordinate, data.getOrDefault(coordinate, defaultEntity)));
            }
        }
        return result;
    }

    @Override
    public List<GridCell<ENT>> nonDefaultCells() {
        List<GridCell<ENT>> snapshot = new ArrayList<>(data.size());
        for (Map.Entry<GridCoordinate, ENT> entry : data.entrySet()) {
            snapshot.add(new GridCell<>(entry.getKey(), entry.getValue()));
        }
        return snapshot;
    }

    @Override
    public List<GridCell<ENT>> filteredCells(Predicate<ENT> entityPredicate) {
        boolean includeDefault = entityPredicate.test(defaultEntity);

        // Fast path: default does NOT match -> only stored non-default entries can match.
        if (!includeDefault) {
            List<GridCell<ENT>> result = new ArrayList<>(data.size());
            for (Map.Entry<GridCoordinate, ENT> entry : data.entrySet()) {
                ENT entity = entry.getValue();
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
        List<GridCell<ENT>> result = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                ENT entity = data.get(coordinate);
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
    public SparseGridModel<ENT> copy() {
        SparseGridModel<ENT> clone = new SparseGridModel<>(structure, defaultEntity);
        clone.data.putAll(data);
        return clone;
    }

    @Override
    public SparseGridModel<ENT> copyWithDefaultEntity() {
        return new SparseGridModel<>(structure, defaultEntity);
    }

    @Override
    public void setEntity(GridCoordinate coordinate, ENT entity) {
        checkCoordinate(coordinate);
        if (entity.equals(defaultEntity)) {
            data.remove(coordinate);
        } else {
            data.put(coordinate, entity);
        }
    }

    @Override
    public void setEntityToDefault(GridCoordinate coordinate) {
        checkCoordinate(coordinate);
        data.remove(coordinate);
    }

    @Override
    public void fill(ENT entity) {
        data.clear();
        if (!entity.equals(defaultEntity)) {
            structure.coordinatesStream().forEach(coordinate -> data.put(coordinate, entity));
        }
    }

    @Override
    public void fill(Supplier<ENT> entitySupplier) {
        // Inline nested loop avoids the intermediate ArrayList created by coordinatesList().
        data.clear();
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ENT entity = entitySupplier.get();
                if (!entity.equals(defaultEntity)) {
                    data.put(new GridCoordinate(x, y), entity);
                }
            }
        }
    }

    @Override
    public void fill(Function<GridCoordinate, ENT> mapper) {
        // Inline nested loop avoids the intermediate ArrayList created by coordinatesList().
        data.clear();
        int width = structure.size().width();
        int height = structure.size().height();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                GridCoordinate coordinate = new GridCoordinate(x, y);
                ENT entity = mapper.apply(coordinate);
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
    public void setSwappedEntities(GridCell<ENT> cellA, GridCell<ENT> cellB) {
        GridCoordinate coordinateA = cellA.coordinate();
        GridCoordinate coordinateB = cellB.coordinate();
        checkCoordinate(coordinateA);
        checkCoordinate(coordinateB);
        ENT entityA = cellA.entity();
        ENT entityB = cellB.entity();

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

    private void checkCoordinate(GridCoordinate coordinate) {
        if (!structure.isCoordinateValid(coordinate)) {
            throw new IndexOutOfBoundsException(
                    "coordinate must be valid for structure " + structure + ", but was: " + coordinate);
        }
    }

}
