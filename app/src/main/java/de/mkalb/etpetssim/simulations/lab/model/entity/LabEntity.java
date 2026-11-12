package de.mkalb.etpetssim.simulations.lab.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntityDescriptorProvider;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Cell states of the Simulation Lab.
 *
 * <p>Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 */
public enum LabEntity implements ConstantGridEntityDescriptorProvider {
    NORMAL(
            "normal",
            "lab.entity.normal.short",
            "lab.entity.normal.long",
            "lab.entity.normal.description",
            null,
            null,
            null
    ),
    HIGHLIGHTED(
            "highlighted",
            "lab.entity.highlighted.short",
            "lab.entity.highlighted.long",
            "lab.entity.highlighted.description",
            null,
            null,
            null
    );

    private final GridEntityDescriptorSpec spec;

    @SuppressWarnings("SameParameterValue")
    LabEntity(String descriptorId,
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
