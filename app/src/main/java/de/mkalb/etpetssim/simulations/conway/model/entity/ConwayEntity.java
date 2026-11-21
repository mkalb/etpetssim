package de.mkalb.etpetssim.simulations.conway.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntityDescriptorProvider;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for Conway's Game of Life.
 *
 * <p>Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 */
public enum ConwayEntity implements ConstantGridEntityDescriptorProvider {
    DEAD(
            "dead",
            "conway.entity.dead.name",
            "conway.entity.dead.description",
            null,
            Color.LIGHTYELLOW,
            null
    ),
    ALIVE(
            "alive",
            "conway.entity.alive.name",
            "conway.entity.alive.description",
            null,
            Color.DARKRED,
            Color.INDIANRED
    );

    private final GridEntityDescriptorSpec spec;

    @SuppressWarnings("SameParameterValue")
    ConwayEntity(String descriptorId,
                 String nameKey,
                 String descriptionKey,
                 @Nullable String emojiKey,
                 @Nullable Color color,
                 @Nullable Color borderColor) {
        spec = new GridEntityDescriptorSpec(
                descriptorId,
                nameKey,
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
