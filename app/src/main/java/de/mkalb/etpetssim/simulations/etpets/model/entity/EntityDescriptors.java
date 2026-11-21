package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;

/**
 * Entity descriptors for the ET Pets simulation.
 *
 * <p>Provides visual and metadata specifications for terrain, resources, pets, and pet eggs.
 */
public enum EntityDescriptors implements SpecBackedGridEntityDescriptorProvider {

    GROUND(
            EtpetsEntity.DESCRIPTOR_ID_GROUND,
            "etpets.entity.terrain.ground.name",
            "etpets.entity.terrain.ground.description",
            null,
            Color.web("#2E2E2E"),
            null
    ),
    ROCK(
            EtpetsEntity.DESCRIPTOR_ID_ROCK,
            "etpets.entity.terrain.rock.name",
            "etpets.entity.terrain.rock.description",
            null,
            Color.web("#737373"),
            null
    ),
    WATER(
            EtpetsEntity.DESCRIPTOR_ID_WATER,
            "etpets.entity.terrain.water.name",
            "etpets.entity.terrain.water.description",
            null,
            Color.web("#1F4FA0"),
            null
    ),
    TRAIL(
            EtpetsEntity.DESCRIPTOR_ID_TRAIL,
            "etpets.entity.terrain.trail.name",
            "etpets.entity.terrain.trail.description",
            null,
            Color.web("#503812"),
            null
    ),
    PLANT(
            EtpetsEntity.DESCRIPTOR_ID_PLANT,
            "etpets.entity.resource.plant.name",
            "etpets.entity.resource.plant.description",
            null,
            Color.web("#3A8020"),
            Color.web("#3A8020", 0.5d)
    ),
    INSECT(
            EtpetsEntity.DESCRIPTOR_ID_INSECT,
            "etpets.entity.resource.insect.name",
            "etpets.entity.resource.insect.description",
            null,
            Color.web("#B8A000"),
            Color.web("#B8A000", 0.5d)
    ),
    PET(
            EtpetsEntity.DESCRIPTOR_ID_PET,
            "etpets.entity.agent.pet.name",
            "etpets.entity.agent.pet.description",
            null,
            Color.web("#7820CC"),
            null
    ),
    PET_EGG(
            EtpetsEntity.DESCRIPTOR_ID_PET_EGG,
            "etpets.entity.agent.petegg.name",
            "etpets.entity.agent.petegg.description",
            null,
            Color.web("#D8B6CC"),
            Color.web("#4B3A66")
    );

    private final GridEntityDescriptorSpec spec;

    @SuppressWarnings("SameParameterValue")
    EntityDescriptors(String descriptorId,
                      String nameKey,
                      String descriptionKey,
                      @Nullable String emojiKey,
                      @Nullable Color color,
                      @Nullable Color borderColor) {
        spec = new GridEntityDescriptorSpec(
                descriptorId,
                nameKey,
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
