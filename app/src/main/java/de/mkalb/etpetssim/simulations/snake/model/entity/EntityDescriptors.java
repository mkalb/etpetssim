package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {
    GROUND(
            SnakeEntity.DESCRIPTOR_ID_GROUND,
            true,
            "snake.entity.ground.short",
            "snake.entity.ground.long",
            "snake.entity.ground.description",
            null,
            Color.web("#0b1220"),
            null
    ),
    WALL(
            SnakeEntity.DESCRIPTOR_ID_WALL,
            true,
            "snake.entity.wall.short",
            "snake.entity.wall.long",
            "snake.entity.wall.description",
            null,
            Color.web("#4a4a4a"),
            Color.web("#7a7a7a")
    ),
    GROWTH_FOOD(
            SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD,
            true,
            "snake.entity.growthfood.short",
            "snake.entity.growthfood.long",
            "snake.entity.growthfood.description",
            null,
            Color.web("#ffcc00"),
            Color.web("#cc9900")
    ),
    SNAKE_SEGMENT(
            SnakeEntity.DESCRIPTOR_ID_SNAKE_SEGMENT,
            true,
            "snake.entity.snakesegment.short",
            "snake.entity.snakesegment.long",
            "snake.entity.snakesegment.description",
            null,
            Color.web("#4caf50"),
            Color.web("#2e7d32")
    ),
    SNAKE_HEAD(
            SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD,
            true,
            "snake.entity.snakehead.short",
            "snake.entity.snakehead.long",
            "snake.entity.snakehead.description",
            null,
            Color.web("#00bcd4"),
            Color.web("#008ba3")
    );

    private final GridEntityDescriptorSpec spec;

    EntityDescriptors(
            String descriptorId,
            boolean visible,
            String shortNameKey,
            String longNameKey,
            String descriptionKey,
            @Nullable String emojiKey,
            @Nullable Color color,
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

}
