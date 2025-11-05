package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.model.ConstantGridEntity;
import de.mkalb.etpetssim.engine.model.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Defines the possible cell states for Forest-fire model simulation.
 * <p>
 * Each enum constant represents a cell state in the simulation grid:
 * <ul>
 *   <li>{@link #EMPTY}: An empty cell.</li>
 *   <li>{@link #TREE}: A cell occupied by a healthy tree.</li>
 *   <li>{@link #BURNING}: A cell with a burning tree.</li>
 * </ul>
 * Provides all necessary descriptor information for registration and rendering.
 *
 * <ul>
 *   <li><b>descriptorId</b>: Unique identifier for the entity descriptor.</li>
 *   <li><b>visible</b>: Whether the entity should be visible in the UI.</li>
 *   <li><b>shortKey</b>: Resource key for the short display name.</li>
 *   <li><b>longKey</b>: Resource key for the long display name.</li>
 *   <li><b>descriptionKey</b>: Resource key for the entity description.</li>
 *   <li><b>emojiKey</b>: Optional resource key for an emoji representation.</li>
 *   <li><b>color</b>: Optional fill color for rendering the entity.</li>
 *   <li><b>borderColor</b>: Optional border color for rendering the entity.</li>
 *   <li><b>renderPriority</b>: Priority for rendering order (higher values are rendered above lower ones).</li>
 * </ul>
 *
 * @see de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry
 * @see de.mkalb.etpetssim.engine.model.ConstantGridEntity
 */
public enum ForestEntity implements ConstantGridEntity, GridEntityDescribable {
    EMPTY(
            "empty",
            true,
            "forest.entity.empty.short",
            "forest.entity.empty.long",
            "forest.entity.empty.description",
            null,
            Color.rgb(45, 30, 15),
            null,
            0
    ),
    TREE(
            "tree",
            true,
            "forest.entity.tree.short",
            "forest.entity.tree.long",
            "forest.entity.tree.description",
            null,
            Color.FORESTGREEN,
            null,
            1
    ),
    BURNING(
            "burning",
            true,
            "forest.entity.burning.short",
            "forest.entity.burning.long",
            "forest.entity.burning.description",
            null,
            Color.ORANGERED,
            null,
            2
    );

    private final String descriptorId;
    private final boolean visible;
    private final String shortKey;
    private final String longKey;
    private final String descriptionKey;
    private final @Nullable String emojiKey;
    private final @Nullable Paint color;
    private final @Nullable Color borderColor;
    private final int renderPriority;

    ForestEntity(
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
        this.descriptorId = descriptorId;
        this.visible = visible;
        this.shortKey = shortKey;
        this.longKey = longKey;
        this.descriptionKey = descriptionKey;
        this.emojiKey = emojiKey;
        this.color = color;
        this.borderColor = borderColor;
        this.renderPriority = renderPriority;
    }

    @Override
    public String descriptorId() {
        return descriptorId;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public String shortKey() {
        return shortKey;
    }

    @Override
    public String longKey() {
        return longKey;
    }

    @Override
    public String descriptionKey() {
        return descriptionKey;
    }

    @Override
    public @Nullable String emojiKey() {
        return emojiKey;
    }

    @Override
    public @Nullable Paint color() {
        return color;
    }

    @Override
    public @Nullable Color borderColor() {
        return borderColor;
    }

    @Override
    public int renderPriority() {
        return renderPriority;
    }

    /**
     * Checks if this entity represents an empty cell.
     *
     * @return {@code true} if this entity is {@link #EMPTY}, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this == EMPTY;
    }

    /**
     * Checks if this entity represents a tree cell.
     *
     * @return {@code true} if this entity is {@link #TREE}, {@code false} otherwise
     */
    public boolean isTree() {
        return this == TREE;
    }

    /**
     * Checks if this entity represents a burning cell.
     *
     * @return {@code true} if this entity is {@link #BURNING}, {@code false} otherwise
     */
    public boolean isBurning() {
        return this == BURNING;
    }

}
