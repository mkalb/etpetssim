package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.core.AppLocalization;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Registry for managing and looking up {@link GridEntityDescriptor} instances.
 * <p>
 * This class provides centralized registration and retrieval of entity descriptors,
 * supporting lookup by unique identifier.
 * It also handles the resolution of localized strings using {@link AppLocalization}
 * during descriptor creation.
 * </p>
 * <p>
 * The registry is intended to be used as a central point for descriptor management
 * within the application, ensuring consistent access and avoiding duplicate registrations.
 * </p>
 */
public final class GridEntityDescriptorRegistry {

    private final Map<String, GridEntityDescriptor> descriptorsById;

    /**
     * Creates a new, empty {@link GridEntityDescriptorRegistry} with an initial capacity
     * for the expected number of entity descriptors.
     *
     * @param expectedEntities the anticipated number of descriptors to be registered,
     *                        used to set the initial capacity of the internal map
     */
    public GridEntityDescriptorRegistry(int expectedEntities) {
        descriptorsById = HashMap.newHashMap(expectedEntities);
    }

    /**
     * Returns a new {@link GridEntityDescriptorRegistry} populated with the given
     * {@link GridEntityDescribable} entities provided as varargs.
     *
     * @param entities one or more describable entities to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry of(GridEntityDescribable... entities) {
        return ofArray(entities);
    }

    /**
     * Returns a new {@link GridEntityDescriptorRegistry} populated with the given
     * array of {@link GridEntityDescribable} entities.
     *
     * @param entities an array of describable entities to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry ofArray(GridEntityDescribable[] entities) {
        GridEntityDescriptorRegistry registry = new GridEntityDescriptorRegistry(entities.length);
        for (GridEntityDescribable entity : entities) {
            registry.register(
                    entity.descriptorId(),
                    entity.visible(),
                    entity.shortKey(),
                    entity.longKey(),
                    entity.descriptionKey(),
                    entity.emojiKey(),
                    entity.color(),
                    entity.borderColor(),
                    entity.renderPriority()
            );
        }
        return registry;
    }

    /**
     * Returns a new {@link GridEntityDescriptorRegistry} populated with the given
     * {@link Collection} of {@link GridEntityDescribable} entities.
     *
     * @param entities a collection of describable entities to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry fromCollection(Collection<? extends GridEntityDescribable> entities) {
        GridEntityDescriptorRegistry registry = new GridEntityDescriptorRegistry(entities.size());
        for (GridEntityDescribable entity : entities) {
            registry.register(
                    entity.descriptorId(),
                    entity.visible(),
                    entity.shortKey(),
                    entity.longKey(),
                    entity.descriptionKey(),
                    entity.emojiKey(),
                    entity.color(),
                    entity.borderColor(),
                    entity.renderPriority()
            );
        }
        return registry;
    }

    /**
     * Registers a new {@link GridEntityDescriptor} in the registry.
     * <p>
     * Localized strings are resolved using {@link AppLocalization#getText(String)}.
     * The descriptor is stored for lookup by its unique descriptor ID.
     * </p>
     *
     * @param descriptorId    unique identifier for the descriptor
     * @param visible         whether the entity should be visible in the UI
     * @param shortNameKey    localization key for the short name
     * @param longNameKey     localization key for the long name
     * @param descriptionKey  localization key for the description
     * @param emojiKey        optional localization key for the emoji, or {@code null}
     * @param color           optional fill color or paint, or {@code null}
     * @param borderColor     optional border color, or {@code null}
     * @param renderPriority  rendering order priority
     * @return the registered {@link GridEntityDescriptor}
     */
    @SuppressWarnings("UnusedReturnValue")
    public GridEntityDescriptor register(
            String descriptorId,
            boolean visible,
            String shortNameKey,
            String longNameKey,
            String descriptionKey,
            @Nullable String emojiKey,
            @Nullable Paint color,
            @Nullable Color borderColor,
            int renderPriority
    ) {
        GridEntityDescriptor descriptor = new GridEntityDescriptor(
                descriptorId,
                visible,
                AppLocalization.getText(shortNameKey),
                AppLocalization.getText(longNameKey),
                AppLocalization.getText(descriptionKey),
                (emojiKey != null) ? AppLocalization.getText(emojiKey) : null,
                color,
                borderColor,
                renderPriority
        );
        descriptorsById.put(descriptorId, descriptor);
        return descriptor;
    }

    /**
     * Retrieves a {@link GridEntityDescriptor} by its unique descriptor ID.
     *
     * @param descriptorId the unique identifier of the descriptor
     * @return an {@link Optional} containing the descriptor if found, or empty if not present
     */
    public Optional<GridEntityDescriptor> getByDescriptorId(String descriptorId) {
        return Optional.ofNullable(descriptorsById.get(descriptorId));
    }

    /**
     * Retrieves the {@link GridEntityDescriptor} for the given descriptor ID.
     * <p>
     * If no descriptor is registered for the specified ID, a {@link NoSuchElementException} is thrown.
     * This method is intended for use cases where the presence of the descriptor is guaranteed.
     * </p>
     *
     * @param descriptorId the unique identifier of the descriptor
     * @return the {@link GridEntityDescriptor} associated with the given ID
     * @throws NoSuchElementException if no descriptor is found for the specified ID
     */
    public GridEntityDescriptor getRequiredByDescriptorId(String descriptorId) {
        GridEntityDescriptor descriptor = descriptorsById.get(descriptorId);
        if (descriptor == null) {
            throw new NoSuchElementException("No GridEntityDescriptor found for id: " + descriptorId);
        }
        return descriptor;
    }

    @Override
    public String toString() {
        return "GridEntityDescriptorRegistry{" +
                "descriptorsById=" + descriptorsById +
                '}';
    }

}