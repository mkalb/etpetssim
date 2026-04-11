package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.model.entity.EntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
            Color.web("#4a4a4a"),
            Color.web("#7a7a7a"),
            2
    ),
    GROWTH_FOOD(
            SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD,
            true,
            "snake.entity.growthfood.short",
            "snake.entity.growthfood.long",
            "snake.entity.growthfood.description",
            "snake.entity.growthfood.emoji",
            Color.web("#ffcc00"),
            Color.web("#cc9900"),
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

    private final EntityDescriptorSpec spec;

    EntityDescriptors(
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

}
