package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.model.entity.EntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecDescribableGridEntity;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum EntityDescriptors implements SpecDescribableGridEntity {
    GROUND(
            ReboundingEntity.DESCRIPTOR_ID_GROUND,
            true,
            "rebounding.entity.ground.short",
            "rebounding.entity.ground.long",
            "rebounding.entity.ground.description",
            null,
            Color.web("#0b1220"),
            null,
            1
    ),
    WALL(
            ReboundingEntity.DESCRIPTOR_ID_WALL,
            true,
            "rebounding.entity.wall.short",
            "rebounding.entity.wall.long",
            "rebounding.entity.wall.description",
            null,
            Color.web("#4a4a4a"),
            Color.web("#7a7a7a"),
            2
    ),
    MOVING_ENTITY(
            ReboundingEntity.DESCRIPTOR_ID_MOVING_ENTITY,
            true,
            "rebounding.entity.moving.short",
            "rebounding.entity.moving.long",
            "rebounding.entity.moving.description",
            null,
            Color.web("#ffcc00"),
            Color.web("#cc9900"),
            3
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
