package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("SameParameterValue")
public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {
    ANT(
            AntEntity.DESCRIPTOR_ID_ANT,
            true,
            "langton.entity.ant.short",
            "langton.entity.ant.long",
            "langton.entity.ant.description",
            null,
            Color.RED,
            Color.BLACK,
            1
    );

    private final GridEntityDescriptorSpec spec;

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

}
