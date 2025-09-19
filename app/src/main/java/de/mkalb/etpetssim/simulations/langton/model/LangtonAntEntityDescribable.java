package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum LangtonAntEntityDescribable implements GridEntityDescribable {
    ANT(
            LangtonAntEntity.DESCRIPTOR_ID_ANT,
            true,
            "langton.entity.ant.short",
            "langton.entity.ant.long",
            "langton.entity.ant.description",
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

    LangtonAntEntityDescribable(
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

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return descriptorId;
    }

    /**
     * Indicates whether this entity should be visible in the UI.
     *
     * @return {@code true} if the entity is visible, {@code false} otherwise
     */
    @Override
    public boolean visible() {
        return visible;
    }

    /**
     * Returns the resource key for the short display name of this entity.
     *
     * @return the short name resource key
     */
    @Override
    public String shortKey() {
        return shortKey;
    }

    /**
     * Returns the resource key for the long display name of this entity.
     *
     * @return the long name resource key
     */
    @Override
    public String longKey() {
        return longKey;
    }

    /**
     * Returns the resource key for the description of this entity.
     *
     * @return the description resource key
     */
    @Override
    public String descriptionKey() {
        return descriptionKey;
    }

    /**
     * Returns the optional resource key for an emoji representation of this entity.
     *
     * @return the emoji resource key, or {@code null} if not set
     */
    @Override
    public @Nullable String emojiKey() {
        return emojiKey;
    }

    /**
     * Returns the optional fill color for rendering this entity.
     *
     * @return the fill color, or {@code null} if not set
     */
    @Override
    public @Nullable Paint color() {
        return color;
    }

    /**
     * Returns the optional border color for rendering this entity.
     *
     * @return the border color, or {@code null} if not set
     */
    @Override
    public @Nullable Color borderColor() {
        return borderColor;
    }

    /**
     * Returns the render priority for this entity.
     * Higher values are rendered above lower ones.
     *
     * @return the render priority
     */
    @Override
    public int renderPriority() {
        return renderPriority;
    }

}
