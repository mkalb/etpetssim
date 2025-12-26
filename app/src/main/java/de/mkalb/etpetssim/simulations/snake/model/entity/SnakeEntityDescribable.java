package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum SnakeEntityDescribable implements GridEntityDescribable {
    GROUND(
            SnakeEntity.DESCRIPTOR_ID_GROUND,
            true,
            "snake.entity.ground.short",
            "snake.entity.ground.long",
            "snake.entity.ground.description",
            null,
            Color.BLACK,
            null,
            1
    ),
    WALL(
            SnakeEntity.DESCRIPTOR_ID_WALL,
            true,
            "snake.entity.wall.short",
            "snake.entity.wall.long",
            "snake.entity.wall.description",
            "snake.entity.wall.emoji",
            Color.GRAY,
            Color.LIGHTGRAY,
            2
    ),
    GROWTH_FOOD(
            SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD,
            true,
            "snake.entity.growthfood.short",
            "snake.entity.growthfood.long",
            "snake.entity.growthfood.description",
            "snake.entity.growthfood.emoji",
            Color.GREEN,
            Color.LIGHTGREEN,
            3
    ),
    SNAKE_SEGMENT(
            SnakeEntity.DESCRIPTOR_ID_SNAKE_SEGMENT,
            true,
            "snake.entity.snakesegment.short",
            "snake.entity.snakesegment.long",
            "snake.entity.snakesegment.description",
            "snake.entity.snakesegment.emoji",
            null,
            null,
            4
    ),
    SNAKE_HEAD(
            SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD,
            true,
            "snake.entity.snakehead.short",
            "snake.entity.snakehead.long",
            "snake.entity.snakehead.description",
            "snake.entity.snakehead.emoji",
            null,
            null,
            5
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

    SnakeEntityDescribable(
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
