package de.mkalb.etpetssim.engine.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Describes the properties and visual appearance of an entity in the grid.
 * <p>
 * This record holds metadata and styling options for entities rendered in the grid-based UI.
 * All texts are already localized/resolved.
 * </p>
 *
 * @param id             unique identifier for the entity descriptor (for lookup)
 * @param visible        whether the entity should be visible in the UI
 * @param shortName      localized short name of the entity
 * @param longName       localized long name of the entity
 * @param description    localized description of the entity
 * @param emoji          optional emoji representing the entity, or {@code null} if not set
 * @param color          optional fill color or paint for the entity, or {@code null} if not set
 * @param borderColor    optional border color for the entity, or {@code null} if not set
 * @param renderPriority integer value indicating the rendering order (higher values are rendered above lower ones)
 */
public record GridEntityDescriptor(
        String id,
        boolean visible,
        String shortName,
        String longName,
        String description,
        @Nullable String emoji,
        @Nullable Paint color,
        @Nullable Color borderColor,
        int renderPriority
) {

    /**
     * Validates that required fields are not null.
     *
     * @throws NullPointerException if any required field is null
     */
    public GridEntityDescriptor {
        Objects.requireNonNull(id);
        Objects.requireNonNull(shortName);
        Objects.requireNonNull(longName);
        Objects.requireNonNull(description);
    }

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
