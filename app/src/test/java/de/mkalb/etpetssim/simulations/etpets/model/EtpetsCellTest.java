package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoResource;
import de.mkalb.etpetssim.simulations.etpets.model.entity.PetEgg;
import de.mkalb.etpetssim.simulations.etpets.model.entity.PetGenome;
import de.mkalb.etpetssim.simulations.etpets.model.entity.PetTraits;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Plant;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
final class EtpetsCellTest {

    private static final GridCoordinate COORDINATE = new GridCoordinate(1, 2);

    private static EtpetsGridModel createModel() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                GridSize.square(GridSize.MIN_SIZE));
        return new EtpetsGridModel(structure,
                new SparseGridModel<>(structure, TerrainConstant.GROUND),
                new SparseGridModel<>(structure, NoResource.NO_RESOURCE),
                new SparseGridModel<>(structure, NoAgent.NO_AGENT));
    }

    private static Plant createPlant() {
        return new Plant(2.0d, 5.0d, 0.25d);
    }

    private static PetEgg createPetEgg() {
        PetTraits traits = new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT);
        return new PetEgg(7, 1, 2, new PetGenome(traits), 3, 4);
    }

    @Test
    void testFrom() {
        EtpetsGridModel model = createModel();
        Plant plant = createPlant();
        PetEgg petEgg = createPetEgg();
        model.terrainModel().setEntity(COORDINATE, TerrainConstant.ROCK);
        model.resourceModel().setEntity(COORDINATE, plant);
        model.agentModel().setEntity(COORDINATE, petEgg);

        EtpetsCell cell = EtpetsCell.from(model, COORDINATE);

        assertAll(
                () -> assertEquals(COORDINATE, cell.coordinate()),
                () -> assertSame(TerrainConstant.ROCK, cell.terrainEntity()),
                () -> assertSame(plant, cell.resourceEntity()),
                () -> assertSame(petEgg, cell.agentEntity())
        );
    }

    @Test
    void testEntity() {
        Plant plant = createPlant();
        PetEgg petEgg = createPetEgg();

        assertAll(
                () -> assertSame(petEgg,
                        new EtpetsCell(COORDINATE, TerrainConstant.ROCK, plant, petEgg).entity()),
                () -> assertSame(plant,
                        new EtpetsCell(COORDINATE, TerrainConstant.ROCK, plant, NoAgent.NO_AGENT).entity()),
                () -> assertSame(TerrainConstant.ROCK,
                        new EtpetsCell(COORDINATE, TerrainConstant.ROCK, NoResource.NO_RESOURCE, NoAgent.NO_AGENT).entity())
        );
    }

    @Test
    void testToDisplayString() {
        EtpetsCell cell = new EtpetsCell(COORDINATE, TerrainConstant.GROUND, createPlant(), createPetEgg());

        assertEquals("(1, 2) [GROUND] [PLANT A=2.0/5.0 R=0.25] [PET_EGG #7 *3 I=4]", cell.toDisplayString());
    }

    @Test
    void testIsWalkable() {
        assertAll(
                () -> assertTrue(new EtpetsCell(COORDINATE, TerrainConstant.GROUND, NoResource.NO_RESOURCE,
                        NoAgent.NO_AGENT).isWalkable()),
                () -> assertFalse(new EtpetsCell(COORDINATE, TerrainConstant.ROCK, NoResource.NO_RESOURCE,
                        NoAgent.NO_AGENT).isWalkable()),
                () -> assertFalse(new EtpetsCell(COORDINATE, TerrainConstant.GROUND, createPlant(),
                        NoAgent.NO_AGENT).isWalkable()),
                () -> assertFalse(new EtpetsCell(COORDINATE, TerrainConstant.GROUND, NoResource.NO_RESOURCE,
                        createPetEgg()).isWalkable())
        );
    }

}
