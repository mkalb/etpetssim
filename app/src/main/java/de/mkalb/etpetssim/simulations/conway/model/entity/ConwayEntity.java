package de.mkalb.etpetssim.simulations.conway.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;
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
public enum ConwayEntity implements ConstantGridEntity, GridEntityDescribable {
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

    private final String descriptorId;
    private final boolean visible;
    private final String shortKey;
    private final String longKey;
    private final String descriptionKey;
    private final @Nullable String emojiKey;
    private final @Nullable Paint color;
    private final @Nullable Color borderColor;
    private final int renderPriority;

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
