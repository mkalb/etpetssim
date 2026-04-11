package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Represents a fully resolved descriptor for a grid entity, containing display-ready values.
 * <p>
 * Instances are created by {@link GridEntityDescriptorRegistry}, which resolves the localization
 * keys supplied by a {@link GridEntityDescriptorProvider} (for example {@code shortNameKey} and
 * {@code longNameKey}) into concrete display strings via {@code AppLocalization}.
 * </p>
 *
 * @param descriptorId unique identifier for the entity descriptor
 * @param visible whether the entity should be visible in the UI
 * @param shortName resolved short display name
 * @param longName resolved long display name
 * @param description resolved description text
 * @param emoji optional resolved emoji representation, or {@code null} if not set
 * @param color optional fill color for rendering, or {@code null} if not set
 * @param borderColor optional border color for rendering, or {@code null} if not set
 *
 * @see GridEntityDescriptorProvider
 * @see GridEntityDescriptorRegistry
 */
public record GridEntityDescriptor(
        String descriptorId,
        boolean visible,
        String shortName,
        String longName,
        String description,
        @Nullable String emoji,
        @Nullable Paint color,
        @Nullable Color borderColor
) {

    /**
     * Returns the emoji for this entity as an {@link Optional}.
     *
     * @return an {@link Optional} containing the emoji, or empty if not set
     */
    public Optional<String> emojiAsOptional() {
        return Optional.ofNullable(emoji);
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

    /**
     * Returns a short, human-readable string representation of this grid entity descriptor.
     * <p>
     * Format: {@code [short readable content]}
     * <br>
     * Example: {@code [WALL]}
     *
     * @return a concise display string for this grid entity descriptor
     */
    public String toDisplayString() {
        return "[" + shortName + "]";
    }

}
