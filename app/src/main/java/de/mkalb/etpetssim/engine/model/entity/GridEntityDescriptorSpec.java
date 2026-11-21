package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Immutable descriptor spec used by {@link GridEntityDescriptorProvider} implementations.
 *
 * <p>Stores localization keys and rendering hints before they are resolved to a {@link GridEntityDescriptor} by
 * {@link GridEntityDescriptorRegistry}.
 *
 * @param descriptorId   unique descriptor identifier
 * @param nameKey        localization key for the name
 * @param descriptionKey localization key for the description
 * @param emojiKey       optional localization key for the emoji
 * @param color          optional fill color
 * @param borderColor    optional border color
 */
public record GridEntityDescriptorSpec(
        String descriptorId,
        String nameKey,
        String descriptionKey,
        @Nullable String emojiKey,
        @Nullable Color color,
        @Nullable Color borderColor
) {
}
