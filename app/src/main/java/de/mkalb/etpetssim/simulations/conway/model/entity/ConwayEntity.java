package de.mkalb.etpetssim.simulations.conway.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantDescribableGridEntity;
import de.mkalb.etpetssim.engine.model.entity.EntityDescriptorSpec;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for Conway's Game of Life.
 * <p>
 * Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 *
 * @see de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry
 */
public enum ConwayEntity implements ConstantDescribableGridEntity {
    DEAD(
            "dead",
            true,
            "conway.entity.dead.short",
            "conway.entity.dead.long",
            "conway.entity.dead.description",
            null,
            Color.LIGHTYELLOW,
            null,
            0
    ),
    ALIVE(
            "alive",
            true,
            "conway.entity.alive.short",
            "conway.entity.alive.long",
            "conway.entity.alive.description",
            null,
            Color.DARKRED,
            Color.INDIANRED,
            1
    );

    private final EntityDescriptorSpec spec;

    ConwayEntity(
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
     * Checks if this entity represents a dead cell.
     *
     * @return {@code true} if this entity is {@link #DEAD}, {@code false} otherwise
     */
    public boolean isDead() {
        return this == DEAD;
    }

    /**
     * Checks if this entity represents a living cell.
     *
     * @return {@code true} if this entity is {@link #ALIVE}, {@code false} otherwise
     */
    public boolean isAlive() {
        return this == ALIVE;
    }

}
