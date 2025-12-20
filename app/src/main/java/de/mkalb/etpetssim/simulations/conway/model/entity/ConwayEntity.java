package de.mkalb.etpetssim.simulations.conway.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Defines the possible cell states for Conway's Game of Life.
 * <p>
 * Each enum constant represents a cell state in the simulation grid:
 * <ul>
 *   <li>{@link #DEAD}: A dead cell (background).</li>
 *   <li>{@link #ALIVE}: A living (active) cell.</li>
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
 * @see de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry
 * @see de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity
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
