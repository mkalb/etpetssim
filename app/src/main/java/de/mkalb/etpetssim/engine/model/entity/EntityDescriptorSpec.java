package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Immutable descriptor metadata for a {@link GridEntityDescribable} entity.
 * <p>
 * This value object stores localization keys and rendering hints before they are
 * resolved to a {@link GridEntityDescriptor} by {@link GridEntityDescriptorRegistry}.
 * </p>
 *
 * @param descriptorId unique descriptor identifier
 * @param visible whether this entity should be visible
 * @param shortKey localization key for short name
 * @param longKey localization key for long name
 * @param descriptionKey localization key for description
 * @param emojiKey optional localization key for emoji
 * @param color optional fill color/paint
 * @param borderColor optional border color
 * @param renderPriority rendering order priority
 *
 * @see GridEntityDescriptorRegistry
 * @see GridEntityDescriptor
 */
public record EntityDescriptorSpec(
        String descriptorId,
        boolean visible,
        String shortKey,
        String longKey,
        String descriptionKey,
        @Nullable String emojiKey,
        @Nullable Paint color,
        @Nullable Color borderColor,
        int renderPriority
) {
}

