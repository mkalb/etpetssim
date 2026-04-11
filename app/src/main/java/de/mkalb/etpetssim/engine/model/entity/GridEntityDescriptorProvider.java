package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Interface for entities that provide descriptor keys for use with {@link GridEntityDescriptorRegistry}.
 * <p>
 * Unlike {@link GridEntityDescriptor}, this interface does not provide direct values (such as names or descriptions),
 * but instead supplies keys (e.g., {@code shortNameKey}, {@code longNameKey}, {@code descriptionKey}) for accessing
 * localized resources from a {@link java.util.ResourceBundle}.
 * </p>
 * <p>
 * This interface is primarily intended for use with {@link GridEntityDescriptorRegistry}.
 * </p>
 *
 * @see GridEntityDescriptorRegistry
 * @see GridEntityDescriptor
 */
public interface GridEntityDescriptorProvider {

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID
     */
    String descriptorId();

    /**
     * Indicates whether this entity should be visible in the grid.
     *
     * @return {@code true} if visible, {@code false} otherwise
     */
    boolean visible();

    /**
     * Returns the resource bundle key for the short name of this entity.
     *
     * @return the localization key for the short name
     */
    String shortNameKey();

    /**
     * Returns the resource bundle key for the long name of this entity.
     *
     * @return the localization key for the long name
     */
    String longNameKey();

    /**
     * Returns the resource bundle key for the description of this entity.
     *
     * @return the localization key for the description
     */
    String descriptionKey();

    /**
     * Returns the resource bundle key for the emoji representing this entity, or {@code null} if not applicable.
     *
     * @return the localization key for the emoji, or {@code null}
     */
    @Nullable
    String emojiKey();

    /**
     * Returns the primary color used to display this entity, or {@code null} if not specified.
     *
     * @return the color, or {@code null}
     */
    @Nullable
    Paint color();

    /**
     * Returns the border color for this entity, or {@code null} if not specified.
     *
     * @return the border color, or {@code null}
     */
    @Nullable
    Color borderColor();

    /**
     * Returns the render priority for this entity. Higher values indicate higher priority.
     *
     * @return the render priority
     */
    int renderPriority();

}
