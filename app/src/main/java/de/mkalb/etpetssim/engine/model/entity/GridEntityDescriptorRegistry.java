package de.mkalb.etpetssim.engine.model.entity;

import de.mkalb.etpetssim.core.AppLocalization;

import java.util.*;

/**
 * Registry for looking up {@link GridEntityDescriptor} instances by their descriptor ID.
 *
 * <p>The factories create one descriptor per {@link GridEntityDescriptorProvider} and resolve its localization keys
 * with {@link AppLocalization#getText(String)}, so {@link AppLocalization} must be initialized before. If several
 * providers have the same descriptor ID, the descriptor of the last one is kept. The registry cannot be changed after
 * its creation.
 */
public final class GridEntityDescriptorRegistry {

    private final Map<String, GridEntityDescriptor> descriptorsById;

    private GridEntityDescriptorRegistry(int expectedDescriptorCount) {
        descriptorsById = HashMap.newHashMap(expectedDescriptorCount);
    }

    /**
     * Returns a new registry with the descriptors of the given providers, e.g., the constants of an entity enum
     * ({@code of(ConwayEntity.values())}).
     *
     * @param providers the descriptor providers to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry of(GridEntityDescriptorProvider... providers) {
        return ofCollection(Arrays.asList(providers));
    }

    /**
     * Returns a new registry with the descriptors of the given providers.
     *
     * @param providers the descriptor providers to register
     * @return a populated registry instance
     */
    public static GridEntityDescriptorRegistry ofCollection(Collection<? extends GridEntityDescriptorProvider> providers) {
        GridEntityDescriptorRegistry registry = new GridEntityDescriptorRegistry(providers.size());
        for (GridEntityDescriptorProvider provider : providers) {
            registry.register(provider);
        }
        return registry;
    }

    /**
     * Finds the {@link GridEntityDescriptor} for the given descriptor ID.
     *
     * @param descriptorId the unique descriptor identifier
     * @return an {@link Optional} containing the descriptor if present; otherwise {@link Optional#empty()}
     */
    public Optional<GridEntityDescriptor> findByDescriptorId(String descriptorId) {
        return Optional.ofNullable(descriptorsById.get(descriptorId));
    }

    /**
     * Returns the {@link GridEntityDescriptor} for the given descriptor ID.
     *
     * <p>Unlike {@link #findByDescriptorId(String)}, this method throws a {@link NoSuchElementException}
     * if no descriptor is registered for the specified ID.
     *
     * @param descriptorId the unique descriptor identifier
     * @return the {@link GridEntityDescriptor} associated with the given ID
     * @throws NoSuchElementException if no descriptor is found for the specified ID
     */
    public GridEntityDescriptor requireByDescriptorId(String descriptorId) {
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

    private void register(GridEntityDescriptorProvider provider) {
        String emojiKey = provider.emojiKey();
        GridEntityDescriptor descriptor = new GridEntityDescriptor(
                provider.descriptorId(),
                provider.visible(),
                AppLocalization.getText(provider.shortNameKey()),
                AppLocalization.getText(provider.longNameKey()),
                AppLocalization.getText(provider.descriptionKey()),
                (emojiKey != null) ? AppLocalization.getText(emojiKey) : null,
                provider.color(),
                provider.borderColor()
        );
        descriptorsById.put(descriptor.descriptorId(), descriptor);
    }

}
