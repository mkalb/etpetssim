package de.mkalb.etpetssim.simulations.lab.model.entity;

import de.mkalb.etpetssim.engine.model.entity.*;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for the simulation lab.
 * <p>
 * Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 *
 * @see GridEntityDescriptorRegistry
 */
public enum LabEntity implements ConstantGridEntityDescriptorProvider {
    NORMAL(
            "normal",
            true,
            "lab.entity.normal.short",
            "lab.entity.normal.long",
            "lab.entity.normal.description",
            null,
            null,
            null
    ),
    HIGHLIGHTED(
            "highlighted",
            true,
            "lab.entity.highlighted.short",
            "lab.entity.highlighted.long",
            "lab.entity.highlighted.description",
            null,
            null,
            null
    );

    private final GridEntityDescriptorSpec spec;

    LabEntity(String descriptorId,
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

    public boolean isNormal() {
        return this == NORMAL;
    }

    public boolean isHighlighted() {
        return this == HIGHLIGHTED;
    }

}
