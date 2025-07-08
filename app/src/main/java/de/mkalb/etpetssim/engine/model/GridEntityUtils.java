package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.*;
import java.util.function.*;

/**
 * Utility class providing static methods for operations involving
 * {@link GridEntity}, {@link GridCell}, {@link GridModel}, {@link GridEntityDescriptor},
 * and {@link GridEntityDescriptorRegistry}.
 */
public final class GridEntityUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private GridEntityUtils() {
    }

    /**
     * Retrieves the {@link GridEntityDescriptor} for the entity located at the specified coordinate
     * in the given grid model, using the provided descriptor registry.
     *
     * @param coordinate the coordinate to look up
     * @param model the grid model containing the entities
     * @param entityDescriptorRegistry the registry to resolve descriptors
     * @param <T> the type of {@link GridEntity} in the model
     * @return an {@link Optional} containing the descriptor if present, or {@link Optional#empty()} if not found or invalid coordinate
     */
    public static <T extends GridEntity> Optional<GridEntityDescriptor> descriptorAt(
            GridCoordinate coordinate,
            ReadableGridModel<T> model,
            GridEntityDescriptorRegistry entityDescriptorRegistry) {
        if (model.isCoordinateValid(coordinate)) {
            return entityDescriptorRegistry.getByDescriptorId(model.getEntity(coordinate).descriptorId());
        }
        return Optional.empty();
    }

    /**
     * If a {@link GridEntityDescriptor} is present at the specified coordinate in the given grid model,
     * consumes it using the provided {@link Consumer}.
     *
     * @param coordinate the coordinate to look up
     * @param model the grid model containing the entities
     * @param entityDescriptorRegistry the registry to resolve descriptors
     * @param consumer the action to perform if a descriptor is present
     * @param <T> the type of {@link GridEntity} in the model
     */
    public static <T extends GridEntity> void consumeDescriptorAt(
            GridCoordinate coordinate,
            ReadableGridModel<T> model,
            GridEntityDescriptorRegistry entityDescriptorRegistry,
            Consumer<GridEntityDescriptor> consumer) {
        descriptorAt(coordinate, model, entityDescriptorRegistry).ifPresent(consumer);
    }

}