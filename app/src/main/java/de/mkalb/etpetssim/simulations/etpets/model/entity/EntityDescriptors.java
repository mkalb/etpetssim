package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.EntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {

    TERRAIN_GROUND(
            EtpetsEntity.DESCRIPTOR_ID_TERRAIN_GROUND,
            true,
            "etpets.entity.terrain.ground.short",
            "etpets.entity.terrain.ground.long",
            "etpets.entity.terrain.ground.description",
            null,
            Color.web("#2E2E2E"),
            null,
            1
    ),
    TERRAIN_ROCK(
            EtpetsEntity.DESCRIPTOR_ID_TERRAIN_ROCK,
            true,
            "etpets.entity.terrain.rock.short",
            "etpets.entity.terrain.rock.long",
            "etpets.entity.terrain.rock.description",
            null,
            Color.web("#737373"),
            null,
            2
    ),
    TERRAIN_WATER(
            EtpetsEntity.DESCRIPTOR_ID_TERRAIN_WATER,
            true,
            "etpets.entity.terrain.water.short",
            "etpets.entity.terrain.water.long",
            "etpets.entity.terrain.water.description",
            null,
            Color.web("#1F4FA0"),
            null,
            2
    ),
    TERRAIN_TRAIL(
            EtpetsEntity.DESCRIPTOR_ID_TERRAIN_TRAIL,
            true,
            "etpets.entity.terrain.trail.short",
            "etpets.entity.terrain.trail.long",
            "etpets.entity.terrain.trail.description",
            null,
            Color.web("#8E6D3A"),
            null,
            3
    ),
    RESOURCE_PLANT(
            EtpetsEntity.DESCRIPTOR_ID_RESOURCE_PLANT,
            true,
            "etpets.entity.resource.plant.short",
            "etpets.entity.resource.plant.long",
            "etpets.entity.resource.plant.description",
            null,
            Color.web("#4E9B3A"),
            null,
            4
    ),
    RESOURCE_INSECT(
            EtpetsEntity.DESCRIPTOR_ID_RESOURCE_INSECT,
            true,
            "etpets.entity.resource.insect.short",
            "etpets.entity.resource.insect.long",
            "etpets.entity.resource.insect.description",
            null,
            Color.web("#D98E04"),
            null,
            4
    ),
    AGENT_PET(
            EtpetsEntity.DESCRIPTOR_ID_AGENT_PET,
            true,
            "etpets.entity.agent.pet.short",
            "etpets.entity.agent.pet.long",
            "etpets.entity.agent.pet.description",
            null,
            Color.web("#8A2BE2"),
            null,
            5
    ),
    AGENT_PET_EGG(
            EtpetsEntity.DESCRIPTOR_ID_AGENT_PET_EGG,
            true,
            "etpets.entity.agent.egg.short",
            "etpets.entity.agent.egg.long",
            "etpets.entity.agent.egg.description",
            null,
            Color.web("#F0E68C"),
            null,
            6
    );

    private final EntityDescriptorSpec spec;

    EntityDescriptors(String descriptorId,
                      boolean visible,
                      String shortKey,
                      String longKey,
                      String descriptionKey,
                      @Nullable String emojiKey,
                      @Nullable Paint color,
                      @Nullable Color borderColor,
                      int renderPriority) {
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
