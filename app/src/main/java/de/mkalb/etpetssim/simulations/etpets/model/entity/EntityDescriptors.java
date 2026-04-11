package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {

    GROUND(
            EtpetsEntity.DESCRIPTOR_ID_GROUND,
            true,
            "etpets.entity.terrain.ground.short",
            "etpets.entity.terrain.ground.long",
            "etpets.entity.terrain.ground.description",
            null,
            Color.web("#2E2E2E"),
            null,
            1
    ),
    ROCK(
            EtpetsEntity.DESCRIPTOR_ID_ROCK,
            true,
            "etpets.entity.terrain.rock.short",
            "etpets.entity.terrain.rock.long",
            "etpets.entity.terrain.rock.description",
            null,
            Color.web("#737373"),
            null,
            2
    ),
    WATER(
            EtpetsEntity.DESCRIPTOR_ID_WATER,
            true,
            "etpets.entity.terrain.water.short",
            "etpets.entity.terrain.water.long",
            "etpets.entity.terrain.water.description",
            null,
            Color.web("#1F4FA0"),
            null,
            2
    ),
    TRAIL(
            EtpetsEntity.DESCRIPTOR_ID_TRAIL,
            true,
            "etpets.entity.terrain.trail.short",
            "etpets.entity.terrain.trail.long",
            "etpets.entity.terrain.trail.description",
            null,
            Color.web("#8E6D3A"),
            null,
            3
    ),
    PLANT(
            EtpetsEntity.DESCRIPTOR_ID_PLANT,
            true,
            "etpets.entity.resource.plant.short",
            "etpets.entity.resource.plant.long",
            "etpets.entity.resource.plant.description",
            null,
            Color.web("#4E9B3A"),
            null,
            4
    ),
    INSECT(
            EtpetsEntity.DESCRIPTOR_ID_INSECT,
            true,
            "etpets.entity.resource.insect.short",
            "etpets.entity.resource.insect.long",
            "etpets.entity.resource.insect.description",
            null,
            Color.web("#D98E04"),
            null,
            4
    ),
    PET(
            EtpetsEntity.DESCRIPTOR_ID_PET,
            true,
            "etpets.entity.agent.pet.short",
            "etpets.entity.agent.pet.long",
            "etpets.entity.agent.pet.description",
            null,
            Color.web("#8A2BE2"),
            null,
            5
    ),
    PET_EGG(
            EtpetsEntity.DESCRIPTOR_ID_PET_EGG,
            true,
            "etpets.entity.agent.petegg.short",
            "etpets.entity.agent.petegg.long",
            "etpets.entity.agent.petegg.description",
            null,
            Color.web("#F0E68C"),
            null,
            6
    );

    private final GridEntityDescriptorSpec spec;

    EntityDescriptors(String descriptorId,
                      boolean visible,
                      String shortNameKey,
                      String longNameKey,
                      String descriptionKey,
                      @Nullable String emojiKey,
                      @Nullable Paint color,
                      @Nullable Color borderColor,
                      int renderPriority) {
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
