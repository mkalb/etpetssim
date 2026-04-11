package de.mkalb.etpetssim.simulations.lab.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntityDescriptorProvider;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Cell states for the simulation lab.
 * <p>
 * Each enum constant represents one grid cell state and carries descriptor metadata
 * used for descriptor registry registration and rendering.
 *
 * @see de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry
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

    LabEntity(
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

    /**
     * Checks if this entity represents the normal cell state.
     *
     * @return {@code true} if this entity is {@link #NORMAL}, {@code false} otherwise
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * Checks if this entity represents the highlighted cell state.
     *
     * @return {@code true} if this entity is {@link #HIGHLIGHTED}, {@code false} otherwise
     */
    public boolean isHighlighted() {
        return this == HIGHLIGHTED;
    }

}
