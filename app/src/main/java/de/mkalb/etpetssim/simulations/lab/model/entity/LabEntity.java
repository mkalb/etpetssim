package de.mkalb.etpetssim.simulations.lab.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for the simulation lab.
 * <p>
 * Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 *
 * @see de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry
 */
public enum LabEntity implements ConstantGridEntity, GridEntityDescribable {
    NORMAL(
            "normal",
            true,
            "lab.entity.normal.short",
            "lab.entity.normal.long",
            "lab.entity.normal.description",
            null,
            null,
            null,
            0
    ),
    HIGHLIGHTED(
            "highlighted",
            true,
            "lab.entity.highlighted.short",
            "lab.entity.highlighted.long",
            "lab.entity.highlighted.description",
            null,
            null,
            null,
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

    LabEntity(
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
     * Checks if this entity represents the normal cell state.
     *
     * @return {@code true} if this entity is {@link #NORMAL}, {@code false} otherwise
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * Checks if this entity represents the highlighted cell state.
     *
     * @return {@code true} if this entity is {@link #HIGHLIGHTED}, {@code false} otherwise
     */
    public boolean isHighlighted() {
        return this == HIGHLIGHTED;
    }

}
