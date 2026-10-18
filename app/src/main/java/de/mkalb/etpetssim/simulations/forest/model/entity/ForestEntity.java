package de.mkalb.etpetssim.simulations.forest.model.entity;

import de.mkalb.etpetssim.engine.model.entity.*;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for the forest-fire simulation.
 *
 * <p>Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 *
 * @see GridEntityDescriptorRegistry
 */
public enum ForestEntity implements ConstantGridEntityDescriptorProvider {
    EMPTY(
            "empty",
            true,
            "forest.entity.empty.short",
            "forest.entity.empty.long",
            "forest.entity.empty.description",
            null,
            Color.rgb(30, 20, 10),
            null
    ),
    TREE(
            "tree",
            true,
            "forest.entity.tree.short",
            "forest.entity.tree.long",
            "forest.entity.tree.description",
            "forest.entity.tree.emoji",
            Color.SEAGREEN,
            null
    ),
    BURNING(
            "burning",
            true,
            "forest.entity.burning.short",
            "forest.entity.burning.long",
            "forest.entity.burning.description",
            "forest.entity.burning.emoji",
            Color.ORANGERED,
            Color.ORANGE
    );

    private final GridEntityDescriptorSpec spec;

    @SuppressWarnings("SameParameterValue")
    ForestEntity(String descriptorId,
                 boolean visible,
                 String shortNameKey,
                 String longNameKey,
                 String descriptionKey,
                 @Nullable String emojiKey,
                 @Nullable Color color,
                 @Nullable Color borderColor) {
        spec = new GridEntityDescriptorSpec(
                descriptorId,
                visible,
                shortNameKey,
                longNameKey,
                descriptionKey,
                emojiKey,
                color,
                borderColor);
    }

    @Override
    public GridEntityDescriptorSpec descriptorSpec() {
        return spec;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isTree() {
        return this == TREE;
    }

    public boolean isBurning() {
        return this == BURNING;
    }

}
