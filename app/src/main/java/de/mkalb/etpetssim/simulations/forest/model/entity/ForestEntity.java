package de.mkalb.etpetssim.simulations.forest.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for the forest-fire simulation.
 * <p>
 * Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 *
 * @see de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry
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
