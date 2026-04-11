package de.mkalb.etpetssim.simulations.forest.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntityDescriptorProvider;
import de.mkalb.etpetssim.engine.model.entity.EntityDescriptorSpec;
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
public enum ForestEntity implements ConstantGridEntityDescriptorProvider {
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

    private final EntityDescriptorSpec spec;

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
        spec = new EntityDescriptorSpec(
                descriptorId,
                visible,
                shortKey,
                longKey,
                descriptionKey,
                emojiKey,
                color,
                borderColor,
                renderPriority
        );
    }

    @Override
    public EntityDescriptorSpec descriptorSpec() {
        return spec;
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
