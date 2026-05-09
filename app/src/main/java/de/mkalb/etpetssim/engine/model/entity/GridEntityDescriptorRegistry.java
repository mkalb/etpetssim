package de.mkalb.etpetssim.engine.model.entity;

import de.mkalb.etpetssim.core.AppLocalization;
import javafx.scene.paint.Color;
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
     * @param expectedDescriptorCount the anticipated number of descriptors to be registered,
     *                               used to set the initial capacity of the internal map
     */
    public GridEntityDescriptorRegistry(int expectedDescriptorCount) {
        descriptorsById = HashMap.newHashMap(expectedDescriptorCount);
    }

    /**
     * Returns a new {@link GridEntityDescriptorRegistry} populated with the given
     * {@link GridEntityDescriptorProvider} providers supplied as varargs.
     *
     * @param providers one or more descriptor providers to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry of(GridEntityDescriptorProvider... providers) {
        return ofArray(providers);
    }

    /**
     * Returns a new {@link GridEntityDescriptorRegistry} populated with the given
     * array of {@link GridEntityDescriptorProvider} providers.
     *
     * @param providers an array of descriptor providers to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry ofArray(GridEntityDescriptorProvider[] providers) {
        GridEntityDescriptorRegistry registry = new GridEntityDescriptorRegistry(providers.length);
        registry.registerProviders(Arrays.asList(providers));
        return registry;
    }

    /**
     * Returns a new {@link GridEntityDescriptorRegistry} populated with the given
     * {@link Collection} of {@link GridEntityDescriptorProvider} providers.
     *
     * @param providers a collection of descriptor providers to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry ofCollection(Collection<? extends GridEntityDescriptorProvider> providers) {
        GridEntityDescriptorRegistry registry = new GridEntityDescriptorRegistry(providers.size());
        registry.registerProviders(providers);
        return registry;
    }

    private void registerProviders(Iterable<? extends GridEntityDescriptorProvider> providers) {
        for (GridEntityDescriptorProvider provider : providers) {
            registerProvider(provider);
        }
    }

    private void registerProvider(GridEntityDescriptorProvider provider) {
        register(
                provider.descriptorId(),
                provider.visible(),
                provider.shortNameKey(),
                provider.longNameKey(),
                provider.descriptionKey(),
                provider.emojiKey(),
                provider.color(),
                provider.borderColor()
        );
    }

    /**
     * Registers a new {@link GridEntityDescriptor} in the registry.
     * <p>
     * Localized strings are resolved using {@link AppLocalization#getText(String)}.
     * The descriptor is stored for lookup by its unique descriptor ID.
     * </p>
     *
     * @param descriptorId unique identifier for the descriptor
     * @param visible whether the entity should be visible in the UI
     * @param shortNameKey localization key for the short name
     * @param longNameKey localization key for the long name
     * @param descriptionKey localization key for the description
     * @param emojiKey optional localization key for the emoji, or {@code null}
     * @param color optional fill color, or {@code null}
     * @param borderColor optional border color, or {@code null}
     * @return the registered {@link GridEntityDescriptor}; an existing descriptor with the same
     * descriptor ID is replaced
     */
    @SuppressWarnings("UnusedReturnValue")
    public GridEntityDescriptor register(
            String descriptorId,
            boolean visible,
            String shortNameKey,
            String longNameKey,
            String descriptionKey,
            @Nullable String emojiKey,
            @Nullable Color color,
            @Nullable Color borderColor
    ) {
        GridEntityDescriptor descriptor = new GridEntityDescriptor(
                descriptorId,
                visible,
                AppLocalization.getText(shortNameKey),
                AppLocalization.getText(longNameKey),
                AppLocalization.getText(descriptionKey),
                (emojiKey != null) ? AppLocalization.getText(emojiKey) : null,
                color,
                borderColor
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
