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
            null,
            0
    ),
    HIGHLIGHTED(
            "highlighted",
            true,
            "lab.entity.highlighted.short",
            "lab.entity.highlighted.long",
            "lab.entity.highlighted.description",
            null,
            null,
            null,
            1
    );

    private final GridEntityDescriptorSpec spec;

    LabEntity(
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
        spec = new GridEntityDescriptorSpec(
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
