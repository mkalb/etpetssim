package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.EntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecDescribableGridEntity;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum EntityDescriptors implements SpecDescribableGridEntity {
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
    RESOURCE_SUGAR(
            SugarEntity.DESCRIPTOR_ID_RESOURCE_SUGAR,
            true,
            "sugar.entity.resource.short",
            "sugar.entity.resource.long",
            "sugar.entity.resource.description",
            "sugar.entity.resource.emoji",
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
