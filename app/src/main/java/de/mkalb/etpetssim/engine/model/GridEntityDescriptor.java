package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.core.AppLocalization;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Describes the properties and visual appearance of an entity in the grid.
 * <p>
 * This record is used to define metadata, localization keys, and styling options
 * for entities rendered in the grid-based UI.
 * </p>
 *
 * @param visible         whether the entity should be visible in the UI
 * @param shortNameKey    localization key for the short name of the entity
 * @param longNameKey     localization key for the long name of the entity
 * @param descriptionKey  localization key for the description of the entity
 * @param emojiKey        optional localization key for an emoji representing the entity, or {@code null} if not set
 * @param color           optional fill color or paint for the entity, or {@code null} if not set
 * @param borderColor     optional border color for the entity, or {@code null} if not set
 * @param renderPriority  integer value indicating the rendering order (higher values are rendered above lower ones)
 */
public record GridEntityDescriptor(
        boolean visible,
        String shortNameKey,
        String longNameKey,
        String descriptionKey,
        @Nullable String emojiKey,
        @Nullable Paint color,
        @Nullable Color borderColor,
        int renderPriority
) {

    /**
     * Validates that required localization keys are not null.
     *
     * @throws NullPointerException if any required key is null
     */
    public GridEntityDescriptor {
        Objects.requireNonNull(shortNameKey);
        Objects.requireNonNull(longNameKey);
        Objects.requireNonNull(descriptionKey);
    }

    /**
     * Returns the localized short name for this entity, if available.
     *
     * @return an {@link Optional} containing the localized short name, or empty if not found
     */
    public Optional<String> shortName() {
        return AppLocalization.getOptionalText(shortNameKey);
    }

    /**
     * Returns the localized long name for this entity, if available.
     *
     * @return an {@link Optional} containing the localized long name, or empty if not found
     */
    public Optional<String> longName() {
        return AppLocalization.getOptionalText(longNameKey);
    }

    /**
     * Returns the localized description for this entity, if available.
     *
     * @return an {@link Optional} containing the localized description, or empty if not found
     */
    public Optional<String> description() {
        return AppLocalization.getOptionalText(descriptionKey);
    }

    /**
     * Returns the localized emoji for this entity, if an emoji key is set and found.
     *
     * @return an {@link Optional} containing the localized emoji, or empty if not set or not found
     */
    public Optional<String> emoji() {
        if (emojiKey == null) {
            return Optional.empty();
        } else {
            return AppLocalization.getOptionalText(emojiKey);
        }
    }

    /**
     * Returns the fill color or paint for this entity as an {@link Optional}.
     *
     * @return an {@link Optional} containing the fill color or paint, or empty if not set
     */
    public Optional<Paint> colorAsOptional() {
        return Optional.ofNullable(color);
    }

    /**
     * Returns the border color for this entity as an {@link Optional}.
     *
     * @return an {@link Optional} containing the border color, or empty if not set
     */
    public Optional<Color> borderColorAsOptional() {
        return Optional.ofNullable(borderColor);
    }

}
