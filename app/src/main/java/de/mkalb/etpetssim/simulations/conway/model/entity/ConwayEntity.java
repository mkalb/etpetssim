package de.mkalb.etpetssim.simulations.conway.model.entity;

import de.mkalb.etpetssim.engine.model.entity.*;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for Conway's Game of Life.
 * <p>
 * Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 *
 * @see GridEntityDescriptorRegistry
 */
public enum ConwayEntity implements ConstantGridEntityDescriptorProvider {
    DEAD(
            "dead",
            true,
            "conway.entity.dead.short",
            "conway.entity.dead.long",
            "conway.entity.dead.description",
            null,
            Color.LIGHTYELLOW,
            null
    ),
    ALIVE(
            "alive",
            true,
            "conway.entity.alive.short",
            "conway.entity.alive.long",
            "conway.entity.alive.description",
            null,
            Color.DARKRED,
            Color.INDIANRED
    );

    private final GridEntityDescriptorSpec spec;

    ConwayEntity(String descriptorId,
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

    public boolean isDead() {
        return this == DEAD;
    }

    public boolean isAlive() {
        return this == ALIVE;
    }

}
