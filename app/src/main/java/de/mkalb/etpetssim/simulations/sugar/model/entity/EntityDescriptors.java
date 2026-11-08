package de.mkalb.etpetssim.simulations.sugar.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Entity descriptors for the Sugarscape simulation.
 *
 * <p>Provides visual and metadata specifications for terrain, sugar resources, and agents.
 */
public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {
    TERRAIN(
            SugarEntity.DESCRIPTOR_ID_TERRAIN,
            "sugar.entity.terrain.short",
            "sugar.entity.terrain.long",
            "sugar.entity.terrain.description",
            null,
            Color.web("#453525"),
            null
    ),
    SUGAR(
            SugarEntity.DESCRIPTOR_ID_SUGAR,
            "sugar.entity.sugar.short",
            "sugar.entity.sugar.long",
            "sugar.entity.sugar.description",
            null,
            Color.web("#D7BE13"),
            null
    ),
    AGENT(
            SugarEntity.DESCRIPTOR_ID_AGENT,
            "sugar.entity.agent.short",
            "sugar.entity.agent.long",
            "sugar.entity.agent.description",
            null,
            Color.web("#1B5ABA"),
            null
    );

    private final GridEntityDescriptorSpec spec;

    @SuppressWarnings("SameParameterValue")
    EntityDescriptors(String descriptorId,
                      String shortNameKey,
                      String longNameKey,
                      String descriptionKey,
                      @Nullable String emojiKey,
                      @Nullable Color color,
                      @Nullable Color borderColor) {
        spec = new GridEntityDescriptorSpec(
                descriptorId,
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
