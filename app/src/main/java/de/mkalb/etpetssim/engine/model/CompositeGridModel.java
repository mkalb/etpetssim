package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;

import java.util.*;

/**
 * A grid model composed of multiple layers, each represented by a {@link WritableGridModel}.
 * <p>
 * All layers must share the same grid structure. This model provides read-only access to entities
 * across all layers at a given coordinate.
 * </p>
 *
 * @param <T> the type of entities stored in the grid, must implement {@link GridEntity}
 */
@SuppressWarnings("ClassCanBeRecord")
public final class CompositeGridModel<T extends GridEntity> implements GridModel<T> {

    private final List<WritableGridModel<? extends T>> layers;

    /**
     * Constructs a composite grid model from the given layers.
     * <p>
     * All layers must have the same grid structure.
     *
     * @param layers the list of grid model layers
     * @throws IllegalArgumentException if no layers are provided or grid structures do not match
     */
    public CompositeGridModel(List<WritableGridModel<? extends T>> layers) {
        if (layers.isEmpty()) {
            throw new IllegalArgumentException("At least one layer required");
        }
        GridStructure structure = layers.getFirst().structure();
        for (WritableGridModel<?> layer : layers) {
            if (!layer.structure().equals(structure)) {
                throw new IllegalArgumentException("All grid structures must match");
            }
        }
        this.layers = layers;
    }

    @Override
    public GridStructure structure() {
        return layers.getFirst().structure();
    }

    /**
     * Returns a list of entities at the specified coordinate, one from each layer.
     *
     * @param coordinate the grid coordinate
     * @return a list of entities at the coordinate, in layer order
     */
    public List<T> getEntities(GridCoordinate coordinate) {
        List<T> entities = new ArrayList<>(layers.size());
        for (WritableGridModel<? extends T> layer : layers) {
            entities.add(layer.getEntity(coordinate));
        }
        return entities;
    }

    /**
     * Returns the number of layers in this composite grid model.
     *
     * @return the layer count
     */
    public int layerCount() {
        return layers.size();
    }

    /**
     * Returns the writable grid model for the specified layer index.
     *
     * @param index the layer index
     * @return the writable grid model at the given index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public WritableGridModel<?> getLayer(int index) {
        return layers.get(index);
    }

}