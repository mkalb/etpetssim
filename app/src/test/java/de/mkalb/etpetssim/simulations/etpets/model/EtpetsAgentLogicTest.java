package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Insect;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Pet;
import de.mkalb.etpetssim.simulations.etpets.model.entity.PetEgg;
import de.mkalb.etpetssim.simulations.etpets.model.entity.PetGenome;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Trail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Random;

import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsLogicTestSupport.createGridModel;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsLogicTestSupport.createPet;
import static de.mkalb.etpetssim.simulations.etpets.model.EtpetsLogicTestSupport.createTraits;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class EtpetsAgentLogicTest {

    private static final GridCoordinate PET_COORDINATE = new GridCoordinate(3, 3);
    private static final int STEP_INDEX = 10;
    private static final int NEXT_ID = 100;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static EtpetsStatistics apply(EtpetsGridModel model, int activePetCells, int eggCells) {
        EtpetsStatistics statistics = new EtpetsStatistics(model.structure());
        statistics.adjustCellCounts(activePetCells, eggCells, 0);
        EtpetsAgentLogic.apply(model, new Random(1L), new EtpetsIdSequence(NEXT_ID), STEP_INDEX, statistics);
        return statistics;
    }

    @Test
    void testApplyHatchesEggWhenIncubationEnds() {
        EtpetsGridModel model = createGridModel();
        model.agentModel().setEntity(PET_COORDINATE, new PetEgg(1, 2, 3, new PetGenome(createTraits()), 0,
                EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MIN));

        EtpetsStatistics statistics = apply(model, 0, 1);

        assertAll(
                () -> assertInstanceOf(Pet.class, model.agentModel().getEntity(PET_COORDINATE)),
                () -> assertEquals(NEXT_ID, model.agentModel().getEntity(PET_COORDINATE).id()),
                () -> assertInstanceOf(Trail.class, model.terrainModel().getEntity(PET_COORDINATE)),
                () -> assertEquals(1, statistics.activePetCells()),
                () -> assertEquals(0, statistics.eggCells())
        );
    }

    @Test
    void testApplyKeepsIncubatingEgg() {
        EtpetsGridModel model = createGridModel();
        PetEgg egg = new PetEgg(1, 2, 3, new PetGenome(createTraits()), 0,
                EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MIN + 2);
        model.agentModel().setEntity(PET_COORDINATE, egg);

        EtpetsStatistics statistics = apply(model, 0, 1);

        assertAll(
                () -> assertSame(egg, model.agentModel().getEntity(PET_COORDINATE)),
                () -> assertEquals(EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MIN + 1, egg.incubationRemaining()),
                () -> assertEquals(1, statistics.eggCells())
        );
    }

    @Test
    void testApplyMarksPetWithoutEnergyAsDead() {
        EtpetsGridModel model = createGridModel();
        Pet pet = createPet(STEP_INDEX - 1, EtpetsBalance.PET_CURRENT_ENERGY_MIN);
        model.agentModel().setEntity(PET_COORDINATE, pet);

        EtpetsStatistics statistics = apply(model, 1, 0);

        assertAll(
                () -> assertTrue(pet.isDead()),
                () -> assertSame(pet, model.agentModel().getEntity(PET_COORDINATE)),
                () -> assertEquals(0, statistics.activePetCells()),
                () -> assertEquals(1, statistics.cumulativePetDeathCount())
        );
    }

    @Test
    void testApplyRemovesPetThatDiedInPreviousStep() {
        EtpetsGridModel model = createGridModel();
        Pet pet = createPet(STEP_INDEX - 1, EtpetsBalance.PET_CURRENT_ENERGY_MAX);
        pet.die();
        model.agentModel().setEntity(PET_COORDINATE, pet);

        EtpetsStatistics statistics = apply(model, 0, 0);

        assertAll(
                () -> assertSame(NoAgent.NO_AGENT, model.agentModel().getEntity(PET_COORDINATE)),
                () -> assertEquals(0, statistics.activePetCells()),
                () -> assertEquals(0, statistics.cumulativePetDeathCount())
        );
    }

    @Test
    void testApplyLetsPetEatAdjacentResource() {
        EtpetsGridModel model = createGridModel();
        int currentEnergy = 20;
        Pet pet = createPet(STEP_INDEX - 1, currentEnergy);
        model.agentModel().setEntity(PET_COORDINATE, pet);
        GridCoordinate resourceCoordinate = new GridCoordinate(4, 3);
        Insect insect = new Insect(
                EtpetsBalance.INSECT_MAX_AMOUNT_MAX,
                EtpetsBalance.INSECT_MAX_AMOUNT_MAX,
                EtpetsBalance.INSECT_REGENERATION_PER_STEP_BASE);
        model.resourceModel().setEntity(resourceCoordinate, insect);
        for (GridCoordinate rockCoordinate : List.of(new GridCoordinate(3, 2), new GridCoordinate(3, 4),
                new GridCoordinate(2, 3))) {
            model.terrainModel().setEntity(rockCoordinate, TerrainConstant.ROCK);
        }

        apply(model, 1, 0);

        assertAll(
                () -> assertSame(pet, model.agentModel().getEntity(PET_COORDINATE)),
                () -> assertEquals((currentEnergy - EtpetsBalance.PET_STEP_ENERGY_LOSS)
                        + EtpetsBalance.INSECT_ENERGY_GAIN_PER_ACT, pet.currentEnergy()),
                () -> assertTrue(insect.currentAmount() < EtpetsBalance.INSECT_MAX_AMOUNT_MAX),
                () -> assertInstanceOf(Trail.class, model.terrainModel().getEntity(PET_COORDINATE))
        );
    }

    @Test
    void testApplyLetsPetMoveToOnlyFreeNeighbor() {
        EtpetsGridModel model = createGridModel();
        Pet pet = createPet(STEP_INDEX - 1, EtpetsBalance.PET_CURRENT_ENERGY_MAX);
        model.agentModel().setEntity(PET_COORDINATE, pet);
        GridCoordinate freeCoordinate = new GridCoordinate(4, 3);
        for (GridCoordinate rockCoordinate : List.of(new GridCoordinate(3, 2), new GridCoordinate(3, 4),
                new GridCoordinate(2, 3))) {
            model.terrainModel().setEntity(rockCoordinate, TerrainConstant.ROCK);
        }

        apply(model, 1, 0);

        assertAll(
                () -> assertSame(NoAgent.NO_AGENT, model.agentModel().getEntity(PET_COORDINATE)),
                () -> assertSame(pet, model.agentModel().getEntity(freeCoordinate)),
                () -> assertInstanceOf(Trail.class, model.terrainModel().getEntity(freeCoordinate))
        );
    }

}
