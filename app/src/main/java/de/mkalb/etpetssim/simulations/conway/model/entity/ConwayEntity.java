package de.mkalb.etpetssim.simulations.conway.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntityDescriptorProvider;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
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

    ConwayEntity(
            String descriptorId,
            boolean visible,
            String shortNameKey,
            String longNameKey,
            String descriptionKey,
            @Nullable String emojiKey,
            @Nullable Paint color,
            @Nullable Color borderColor
    ) {
        spec = new GridEntityDescriptorSpec(
                descriptorId,
                visible,
                shortNameKey,
                longNameKey,
                descriptionKey,
                emojiKey,
                color,
                borderColor
        );
    }

    @Override
    public GridEntityDescriptorSpec descriptorSpec() {
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
