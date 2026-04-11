package de.mkalb.etpetssim.simulations.wator.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {
    WATER(
            WatorEntity.DESCRIPTOR_ID_WATER,
            true,
            "wator.entity.water.short",
            "wator.entity.water.long",
            "wator.entity.water.description",
            null,
            Color.rgb(20, 25, 100),
            null
    ),
    FISH(
            WatorEntity.DESCRIPTOR_ID_FISH,
            true,
            "wator.entity.fish.short",
            "wator.entity.fish.long",
            "wator.entity.fish.description",
            "wator.entity.fish.emoji",
            Color.rgb(0, 160, 100),
            Color.rgb(20, 25, 100)
    ),
    SHARK(
            WatorEntity.DESCRIPTOR_ID_SHARK,
            true,
            "wator.entity.shark.short",
            "wator.entity.shark.long",
            "wator.entity.shark.description",
            "wator.entity.shark.emoji",
            Color.rgb(115, 120, 120),
            Color.rgb(20, 25, 100)
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
