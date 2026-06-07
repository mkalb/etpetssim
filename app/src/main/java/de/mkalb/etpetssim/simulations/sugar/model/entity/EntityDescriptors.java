package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.*;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Entity descriptors for the Sugarscape simulation.
 * <p>
 * Provides visual and metadata specifications for terrain, sugar resources, and agents.
 */
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
            null
    ),
    SUGAR(
            SugarEntity.DESCRIPTOR_ID_SUGAR,
            true,
            "sugar.entity.sugar.short",
            "sugar.entity.sugar.long",
            "sugar.entity.sugar.description",
            null,
            Color.web("#D7BE13"),
            null
    ),
    AGENT(
            SugarEntity.DESCRIPTOR_ID_AGENT,
            true,
            "sugar.entity.agent.short",
            "sugar.entity.agent.long",
            "sugar.entity.agent.description",
            null,
            Color.web("#1B5ABA"),
            null
    );

    private final GridEntityDescriptorSpec spec;

    EntityDescriptors(String descriptorId,
                      boolean visible,
                      String shortNameKey,
                      String longNameKey,
                      String descriptionKey,
                      @Nullable String emojiKey,
                      @Nullable Color color,
                      @Nullable Color borderColor) {
        spec = new GridEntityDescriptorSpec(
                descriptorId,
                visible,
                shortNameKey,
                longNameKey,
                descriptionKey,
                emojiKey,
                color,
                borderColor);
    }

    @Override
    public GridEntityDescriptorSpec descriptorSpec() {
        return spec;
    }

}
