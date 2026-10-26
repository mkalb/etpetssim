package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import de.mkalb.etpetssim.simulations.etpets.shared.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OverlyStrongTypeCast")
final class EtpetsUserActionTest {

    private static EtpetsConfig createConfig() {
        return new EtpetsConfig(
                CellShape.HEXAGON,
                GridEdgeBehavior.BLOCK_XY,
                EtpetsConstraints.GRID_WIDTH_DEFAULT,
                EtpetsConstraints.GRID_HEIGHT_DEFAULT,
                EtpetsConstraints.CELL_EDGE_LENGTH_DEFAULT,
                CellDisplayMode.SHAPE,
                1L,
                0.0d,
                0.0d,
                0.0d,
                0.0d,
                0,
                NeighborhoodMode.EDGES_ONLY
        );
    }

    private static Pet createPet() {
        PetTraits traits = new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT);
        return new Pet(
                1,
                null,
                null,
                0,
                EtpetsBalance.PET_CURRENT_ENERGY_MAX,
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_REMAINING_MIN,
                traits);
    }

    @Test
    void testApplySetTerrainReplacesTrailWhenResourceAndAgentAreEmpty() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().terrainModel().setEntity(coordinate, new Trail(EtpetsBalance.TRAIL_INTENSITY_DEFAULT));

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetTerrain(EtpetsTerrainChoice.ROCK),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        assertSame(TerrainConstant.ROCK, manager.currentGridModel().terrainModel().getEntity(coordinate));
    }

    @Test
    void testApplySetTerrainDoesNothingWhenResourceExists() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().resourceModel().setEntity(coordinate, new Plant(
                EtpetsBalance.PLANT_MAX_AMOUNT_MAX,
                EtpetsBalance.PLANT_MAX_AMOUNT_MAX,
                EtpetsBalance.PLANT_REGENERATION_PER_STEP_BASE));

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetTerrain(EtpetsTerrainChoice.WATER),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        assertSame(TerrainConstant.GROUND, manager.currentGridModel().terrainModel().getEntity(coordinate));
    }

    @Test
    void testApplySetTerrainDoesNothingWhenAgentExists() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().agentModel().setEntity(coordinate, createPet());

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetTerrain(EtpetsTerrainChoice.ROCK),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        assertSame(TerrainConstant.GROUND, manager.currentGridModel().terrainModel().getEntity(coordinate));
    }

    @Test
    void testApplySetResourcePlantUsesDeterministicFullDefaults() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetResource(EtpetsResourceChoice.PLANT),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        ResourceEntity resourceEntity = manager.currentGridModel().resourceModel().getEntity(coordinate);
        assertAll(
                () -> assertInstanceOf(Plant.class, resourceEntity),
                () -> assertEquals(EtpetsBalance.PLANT_MAX_AMOUNT_MAX, ((Plant) resourceEntity).currentAmount()),
                () -> assertEquals(EtpetsBalance.PLANT_MAX_AMOUNT_MAX, ((Plant) resourceEntity).maxAmount()),
                () -> assertEquals(EtpetsBalance.PLANT_REGENERATION_PER_STEP_BASE, ((Plant) resourceEntity).regenerationPerStep())
        );
    }

    @Test
    void testApplySetResourceInsectUsesDeterministicFullDefaults() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetResource(EtpetsResourceChoice.INSECT),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        ResourceEntity resourceEntity = manager.currentGridModel().resourceModel().getEntity(coordinate);
        assertAll(
                () -> assertInstanceOf(Insect.class, resourceEntity),
                () -> assertEquals(EtpetsBalance.INSECT_MAX_AMOUNT_MAX, ((Insect) resourceEntity).currentAmount()),
                () -> assertEquals(EtpetsBalance.INSECT_MAX_AMOUNT_MAX, ((Insect) resourceEntity).maxAmount()),
                () -> assertEquals(EtpetsBalance.INSECT_REGENERATION_PER_STEP_BASE, ((Insect) resourceEntity).regenerationPerStep())
        );
    }

    @Test
    void testApplySetResourceNoneClearsResourceOnGroundWithoutAgent() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().resourceModel().setEntity(coordinate, new Insect(
                EtpetsBalance.INSECT_MAX_AMOUNT_MAX,
                EtpetsBalance.INSECT_MAX_AMOUNT_MAX,
                EtpetsBalance.INSECT_REGENERATION_PER_STEP_BASE));

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetResource(EtpetsResourceChoice.NONE),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        assertSame(NoResource.NO_RESOURCE, manager.currentGridModel().resourceModel().getEntity(coordinate));
    }

    @Test
    void testApplySetResourceDoesNothingWhenTerrainIsNotGround() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().terrainModel().setEntity(coordinate, TerrainConstant.ROCK);

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetResource(EtpetsResourceChoice.PLANT),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        assertSame(NoResource.NO_RESOURCE, manager.currentGridModel().resourceModel().getEntity(coordinate));
    }

    @Test
    void testApplySetResourceDoesNothingWhenAgentExists() {
        EtpetsSimulationManager manager = new EtpetsSimulationManager(createConfig());
        EtpetsUserAction userAction = new EtpetsUserAction();
        GridCoordinate coordinate = new GridCoordinate(0, 0);
        manager.currentGridModel().agentModel().setEntity(coordinate, createPet());

        userAction.apply(
                manager,
                new EtpetsUserActionContext.SetResource(EtpetsResourceChoice.PLANT),
                EtpetsCell.from(manager.currentGridModel(), coordinate));

        assertSame(NoResource.NO_RESOURCE, manager.currentGridModel().resourceModel().getEntity(coordinate));
    }

}
