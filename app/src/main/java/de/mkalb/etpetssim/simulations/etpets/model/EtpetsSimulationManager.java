package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.TimedSimulationExecutor;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;

import java.util.*;

public final class EtpetsSimulationManager
        extends AbstractTimedSimulationManager<EtpetsEntity, EtpetsGridModel, EtpetsConfig, EtpetsStatistics> {

    private final GridStructure structure;
    private final EtpetsStatistics statistics;
    private final TimedSimulationExecutor<EtpetsEntity, EtpetsGridModel> executor;

    public EtpetsSimulationManager(EtpetsConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new EtpetsStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new EtpetsGridModel(
                structure,
                new SparseGridModel<>(structure, TerrainConstant.GROUND),
                new SparseGridModel<>(structure, NoResource.NO_RESOURCE),
                new SparseGridModel<>(structure, NoAgent.NO_AGENT)
        );

        var idSequence = new EtpetsIdSequence(1L);

        var runner = new EtpetsStepRunner(config, random, model, idSequence);
        var terminationCondition = new EtpetsTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeTerrain(model, random);
        initializeResources(model, random);
        initializePets(model, random, idSequence);

        updateInitialStatistics(model);
    }

    private static int computePercentCount(int totalCells, int percent) {
        return Math.max(0, (totalCells * percent) / 100);
    }

    private void initializeTerrain(EtpetsGridModel model, Random random) {
        int totalCells = structure.cellCount();
        int rockCount = computePercentCount(totalCells, config().rockPercent());
        int waterCount = computePercentCount(totalCells, config().waterPercent());

        // Only for testing / debugging
        // model.terrainModel().setEntity(new GridCoordinate(0, 1), new Trail(EtpetsBalance.TRAIL_INTENSITY_RANGE_MIN));
        // model.terrainModel().setEntity(new GridCoordinate(1, 1), new Trail(1_000));
        // model.terrainModel().setEntity(new GridCoordinate(2, 1), new Trail(2_000));
        // model.terrainModel().setEntity(new GridCoordinate(3, 1), new Trail(3_000));
        // model.terrainModel().setEntity(new GridCoordinate(4, 1), new Trail(4_000));
        // model.terrainModel().setEntity(new GridCoordinate(5, 1), new Trail(5_000));
        // model.terrainModel().setEntity(new GridCoordinate(6, 1), new Trail(6_000));
        // model.terrainModel().setEntity(new GridCoordinate(7, 1), new Trail(7_000));
        // model.terrainModel().setEntity(new GridCoordinate(8, 1), new Trail(8_000));
        // model.terrainModel().setEntity(new GridCoordinate(9, 1), new Trail(9_000));
        // model.terrainModel().setEntity(new GridCoordinate(10, 1), new Trail(EtpetsBalance.TRAIL_INTENSITY_RANGE_MAX));

        List<GridCoordinate> coordinates = new ArrayList<>(structure.coordinatesList());
        Collections.shuffle(coordinates, random);

        int offset = 0;
        for (int i = 0; (i < rockCount) && ((offset + i) < coordinates.size()); i++) {
            model.terrainModel().setEntity(coordinates.get(offset + i), TerrainConstant.ROCK);
        }
        offset += rockCount;
        for (int i = 0; (i < waterCount) && ((offset + i) < coordinates.size()); i++) {
            model.terrainModel().setEntity(coordinates.get(offset + i), TerrainConstant.WATER);
        }
    }

    private void initializeResources(EtpetsGridModel model, Random random) {
        int totalCells = structure.cellCount();
        int plantCount = computePercentCount(totalCells, config().plantPercent());
        int insectCount = computePercentCount(totalCells, config().insectPercent());

        // Only for testing / debugging
        // for (int i = EtpetsBalance.PLANT_CURRENT_AMOUNT_RANGE_MIN; i <= EtpetsBalance.PLANT_CURRENT_AMOUNT_RANGE_MAX; i++) {
        //     model.resourceModel().setEntity(new GridCoordinate(i, 3), new Plant(i, i, 100));
        // }
        // for (int i = EtpetsBalance.INSECT_CURRENT_AMOUNT_RANGE_MIN; i <= EtpetsBalance.INSECT_CURRENT_AMOUNT_RANGE_MAX; i++) {
        //     model.resourceModel().setEntity(new GridCoordinate(i, 5), new Insect(i, i, 100));
        // }

        List<GridCoordinate> available = traversableCoordinates(model, random);
        int requestedResources = plantCount + insectCount;
        if (requestedResources > available.size()) {
            throw new IllegalArgumentException("Invalid ET Pets resource initialization: requested "
                    + requestedResources + " resource cells, but only " + available.size()
                    + " traversable cells are available.");
        }

        int plantRange = (EtpetsBalance.PLANT_MAX_AMOUNT_RANGE_MAX - EtpetsBalance.PLANT_MAX_AMOUNT_RANGE_MIN) + 1;
        int insectRange = (EtpetsBalance.INSECT_MAX_AMOUNT_RANGE_MAX - EtpetsBalance.INSECT_MAX_AMOUNT_RANGE_MIN) + 1;

        int offset = 0;
        for (int i = 0; (i < plantCount) && ((offset + i) < available.size()); i++) {
            double maxAmount = EtpetsBalance.PLANT_MAX_AMOUNT_RANGE_MIN + random.nextInt(plantRange);
            double regenerationPerStep = EtpetsBalance.PLANT_REGENERATION_PER_STEP_BASE
                    + random.nextDouble(-EtpetsBalance.PLANT_REGENERATION_PER_STEP_DELTA, EtpetsBalance.PLANT_REGENERATION_PER_STEP_DELTA);
            model.resourceModel().setEntity(available.get(offset + i),
                    new Plant(maxAmount, maxAmount, regenerationPerStep));
        }
        offset += plantCount;
        for (int i = 0; (i < insectCount) && ((offset + i) < available.size()); i++) {
            double maxAmount = EtpetsBalance.INSECT_MAX_AMOUNT_RANGE_MIN + random.nextInt(insectRange);
            double regenerationPerStep = EtpetsBalance.INSECT_REGENERATION_PER_STEP_BASE
                    + random.nextDouble(-EtpetsBalance.INSECT_REGENERATION_PER_STEP_DELTA, EtpetsBalance.INSECT_REGENERATION_PER_STEP_DELTA);
            model.resourceModel().setEntity(available.get(offset + i),
                    new Insect(maxAmount, maxAmount, regenerationPerStep));
        }
    }

    private void initializePets(EtpetsGridModel model, Random random, EtpetsIdSequence idSequence) {
        // Only for testing / debugging
        // for (int i = EtpetsBalance.PET_EGG_INCUBATION_REMAINING_RANGE_MIN; i <= EtpetsBalance.PET_EGG_INCUBATION_REMAINING_RANGE_MAX; i++) {
        //     model.agentModel().setEntity(new GridCoordinate(i, 7), new PetEgg(
        //             idSequence.next(),
        //             -1,
        //             -2,
        //             new PetGenome(new PetTraits(
        //                     EtpetsBalance.PET_MAX_ENERGY_RANGE_MAX,
        //                     EtpetsBalance.PET_MOVEMENT_COST_MODIFIER_DEFAULT,
        //                     EtpetsBalance.PET_REPRODUCTION_MIN_ENERGY_DEFAULT,
        //                     EtpetsBalance.PET_REPRODUCTION_COOLDOWN_DEFAULT)),
        //             -1,
        //             i
        //     ));
        // }

        List<GridCoordinate> available = traversableCoordinates(model, random);
        if (config().petCount() > available.size()) {
            throw new IllegalArgumentException("Invalid ET Pets pet initialization: requested "
                    + config().petCount() + " pets, but only " + available.size()
                    + " traversable cells are available.");
        }

        int maxEnergyRange = (EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MAX - EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN) + 1;

        for (int i = 0; i < config().petCount(); i++) {
            int maxEnergy = EtpetsBalance.PET_TRAITS_MAX_ENERGY_RANGE_MIN + random.nextInt(maxEnergyRange);

            PetTraits trait = new PetTraits(
                    maxEnergy,
                    EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                    EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                    EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT
            );

            model.agentModel().setEntity(available.get(i), new Pet(
                    idSequence.next(),
                    null, // Initial pets have no parents
                    null,// Initial pets have no parents
                    -1,
                    maxEnergy, // Use maxEnergy as initial energy (currentEnergy) for better starting conditions
                    EtpetsBalance.PET_REPRODUCTION_COOLDOWN_REMAINING_RANGE_MIN, // No cooldown at start
                    trait
            ));
        }
    }

    private List<GridCoordinate> traversableCoordinates(EtpetsGridModel model, Random random) {
        List<GridCoordinate> available = new ArrayList<>();
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            if (model.terrainModel().isDefaultEntity(coordinate)
                    && model.resourceModel().isDefaultEntity(coordinate)
                    && model.agentModel().isDefaultEntity(coordinate)) {
                available.add(coordinate);
            }
        }
        Collections.shuffle(available, random);
        return available;
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public EtpetsStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    private void updateInitialStatistics(EtpetsGridModel model) {
        int activePetCountInitial = Math.toIntExact(model.agentModel()
                                                         .countEntities(entity -> (entity instanceof Pet pet) && !pet.isDead()));
        int eggCountInitial = Math.toIntExact(model.agentModel()
                                                   .countEntities(entity -> entity instanceof PetEgg));
        int cumulativeDeadPetCountInitial = Math.toIntExact(model.agentModel()
                                                                 .countEntities(entity -> (entity instanceof Pet pet) && pet.isDead()));
        statistics.updateInitialCells(activePetCountInitial, eggCountInitial, cumulativeDeadPetCountInitial);
    }

    @Override
    protected TimedSimulationExecutor<EtpetsEntity, EtpetsGridModel> executor() {
        return executor;
    }

}

