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
 * supporting lookup by unique identifier or by the associated {@link GridEntity} class.
 * It also handles the resolution of localized strings using {@link AppLocalization}
 * during descriptor creation.
 * </p>
 * <p>
 * The registry is intended to be used as a central point for descriptor management
 * within the application, ensuring consistent access and avoiding duplicate registrations.
 * </p>
 */
public final class GridEntityDescriptorRegistry {

    private final Map<String, GridEntityDescriptor> byId = new HashMap<>();
    private final Map<Class<? extends GridEntity>, GridEntityDescriptor> byEntityClass = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private GridEntityDescriptorRegistry() {
    }

    /**
     * Registers a new {@link GridEntityDescriptor} in the registry.
     * <p>
     * Localized strings are resolved using {@link AppLocalization#getText(String)}.
     * The descriptor is stored for lookup by its unique ID and, if provided, by its entity class.
     * </p>
     *
     * @param id             unique identifier for the descriptor
     * @param visible        whether the entity should be visible in the UI
     * @param shortNameKey   localization key for the short name
     * @param longNameKey    localization key for the long name
     * @param descriptionKey localization key for the description
     * @param emojiKey       optional localization key for the emoji, or {@code null}
     * @param color          optional fill color or paint, or {@code null}
     * @param borderColor    optional border color, or {@code null}
     * @param renderPriority rendering order priority
     * @param entityClass    optional class of the associated {@link GridEntity}, or {@code null}
     * @return the registered {@link GridEntityDescriptor}
     */
    public GridEntityDescriptor register(
            String id,
            boolean visible,
            String shortNameKey,
            String longNameKey,
            String descriptionKey,
            @Nullable String emojiKey,
            @Nullable Paint color,
            @Nullable Color borderColor,
            int renderPriority,
            @Nullable Class<? extends GridEntity> entityClass
    ) {
        GridEntityDescriptor descriptor = new GridEntityDescriptor(
                id,
                visible,
                AppLocalization.getText(shortNameKey),
                AppLocalization.getText(longNameKey),
                AppLocalization.getText(descriptionKey),
                (emojiKey != null) ? AppLocalization.getText(emojiKey) : null,
                color,
                borderColor,
                renderPriority
        );
        byId.put(id, descriptor);
        if (entityClass != null) {
            byEntityClass.put(entityClass, descriptor);
        }
        return descriptor;
    }

    /**
     * Retrieves a {@link GridEntityDescriptor} by its unique identifier.
     *
     * @param id the unique identifier of the descriptor
     * @return an {@link Optional} containing the descriptor if found, or empty if not present
     */
    public Optional<GridEntityDescriptor> getById(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /**
     * Retrieves a {@link GridEntityDescriptor} by the class of its associated {@link GridEntity}.
     *
     * @param clazz the class of the entity
     * @return an {@link Optional} containing the descriptor if found, or empty if not present
     */
    public Optional<GridEntityDescriptor> getByEntityClass(Class<? extends GridEntity> clazz) {
        return Optional.ofNullable(byEntityClass.get(clazz));
    }

}