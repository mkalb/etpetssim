package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {
    GROUND(
            ReboundingEntity.DESCRIPTOR_ID_GROUND,
            true,
            "rebounding.entity.ground.short",
            "rebounding.entity.ground.long",
            "rebounding.entity.ground.description",
            null,
            Color.web("#0b1220"),
            null
    ),
    WALL(
            ReboundingEntity.DESCRIPTOR_ID_WALL,
            true,
            "rebounding.entity.wall.short",
            "rebounding.entity.wall.long",
            "rebounding.entity.wall.description",
            null,
            Color.web("#4a4a4a"),
            Color.web("#7a7a7a")
    ),
    REBOUNDER(
            ReboundingEntity.DESCRIPTOR_ID_REBOUNDER,
            true,
            "rebounding.entity.rebounder.short",
            "rebounding.entity.rebounder.long",
            "rebounding.entity.rebounder.description",
            null,
            Color.web("#ffcc00"),
            Color.web("#cc9900")
    );

    private final GridEntityDescriptorSpec spec;

    EntityDescriptors(
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

}
