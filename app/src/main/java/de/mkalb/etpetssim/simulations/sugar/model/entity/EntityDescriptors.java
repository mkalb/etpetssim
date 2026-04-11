package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {
    TERRAIN(
            SugarEntity.DESCRIPTOR_ID_TERRAIN,
            true,
            "sugar.entity.terrain.short",
            "sugar.entity.terrain.long",
            "sugar.entity.terrain.description",
            null,
            Color.web("#453525"),
            null,
            1
    ),
    SUGAR(
            SugarEntity.DESCRIPTOR_ID_SUGAR,
            true,
            "sugar.entity.sugar.short",
            "sugar.entity.sugar.long",
            "sugar.entity.sugar.description",
            "sugar.entity.sugar.emoji",
            Color.web("#D7BE13"),
            null,
            2
    ),
    AGENT(
            SugarEntity.DESCRIPTOR_ID_AGENT,
            true,
            "sugar.entity.agent.short",
            "sugar.entity.agent.long",
            "sugar.entity.agent.description",
            "sugar.entity.agent.emoji",
            Color.web("#1B5ABA"),
            null,
            3
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
            @Nullable Color borderColor,
            int renderPriority
    ) {
        spec = new GridEntityDescriptorSpec(
                descriptorId,
                visible,
                shortNameKey,
                longNameKey,
                descriptionKey,
                emojiKey,
                color,
                borderColor,
                renderPriority
        );
    }

    @Override
    public GridEntityDescriptorSpec descriptorSpec() {
        return spec;
    }

}
