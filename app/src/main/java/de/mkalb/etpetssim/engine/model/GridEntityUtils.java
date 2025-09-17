package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.*;
import java.util.function.*;

/**
 * Utility class providing static methods for operations involving
 * {@link GridEntity}, {@link GridCell}, {@link WritableGridModel}, {@link GridPattern} {@link GridEntityDescriptor},
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

    /**
     * Places all entities from the given {@link GridPattern} into the specified {@link WritableGridModel},
     * offsetting each entity's position by the provided anchor coordinate.
     * <p>
     * For each entry in the pattern, the entity is placed at the coordinate computed by adding
     * the pattern's offset to the anchor coordinate. Entities are only placed if the resulting
     * coordinate is valid in the target grid model.
     *
     * @param coordinate the anchor coordinate at which to place the pattern's origin
     * @param model the grid model to modify
     * @param pattern the pattern of entities to place
     * @param <T> the type of {@link GridEntity} in the model and pattern
     */
    public static <T extends GridEntity> void placePatternAt(GridCoordinate coordinate,
                                                             WritableGridModel<T> model,
                                                             GridPattern<T> pattern) {
        pattern.offsetMap()
               .forEach((offset, entity) -> {
                   GridCoordinate targetCoordinate = coordinate.offset(offset);
                   if (model.isCoordinateValid(targetCoordinate)) {
                       model.setEntity(targetCoordinate, entity);
                   }
               });
    }

}
