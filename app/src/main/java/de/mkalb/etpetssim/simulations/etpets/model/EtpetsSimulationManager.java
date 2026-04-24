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
        // for (int i = 0; i <= 100; i++) {
        //     model.terrainModel().setEntity(new GridCoordinate(i, 1), new Trail(i));
        // }

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
        // for (int i = 0; i <= EtpetsBalance.PLANT_MAX_AMOUNT_MAX; i++) {
        //     model.resourceModel().setEntity(new GridCoordinate(i, 3), new Plant(i, i, 10));
        // }
        // for (int i = 0; i <= EtpetsBalance.INSECT_MAX_AMOUNT_MAX; i++) {
        //     model.resourceModel().setEntity(new GridCoordinate(i, 5), new Insect(i, i, 10));
        // }

        List<GridCoordinate> available = traversableCoordinates(model, random);
        int requestedResources = plantCount + insectCount;
        if (requestedResources > available.size()) {
            throw new IllegalArgumentException("Invalid ET Pets resource initialization: requested "
                    + requestedResources + " resource cells, but only " + available.size()
                    + " traversable cells are available.");
        }

        int plantRange = (EtpetsBalance.PLANT_MAX_AMOUNT_MAX - EtpetsBalance.PLANT_MAX_AMOUNT_MIN) + 1;
        int insectRange = (EtpetsBalance.INSECT_MAX_AMOUNT_MAX - EtpetsBalance.INSECT_MAX_AMOUNT_MIN) + 1;

        int offset = 0;
        for (int i = 0; (i < plantCount) && ((offset + i) < available.size()); i++) {
            double maxAmount = EtpetsBalance.PLANT_MAX_AMOUNT_MIN + random.nextInt(plantRange);
            double regenRate = EtpetsBalance.PLANT_BASE_REGEN_RATE
                    + (((random.nextDouble() * EtpetsBalance.RESOURCE_REGEN_VARIANCE_SPREAD_FACTOR) - 1.0d) * EtpetsBalance.RESOURCE_REGEN_RATE_VARIANCE);
            model.resourceModel().setEntity(available.get(offset + i),
                    new Plant(maxAmount, maxAmount, regenRate));
        }
        offset += plantCount;
        for (int i = 0; (i < insectCount) && ((offset + i) < available.size()); i++) {
            double maxAmount = EtpetsBalance.INSECT_MAX_AMOUNT_MIN + random.nextInt(insectRange);
            double regenRate = EtpetsBalance.INSECT_BASE_REGEN_RATE
                    + (((random.nextDouble() * EtpetsBalance.RESOURCE_REGEN_VARIANCE_SPREAD_FACTOR) - 1.0d) * EtpetsBalance.RESOURCE_REGEN_RATE_VARIANCE);
            model.resourceModel().setEntity(available.get(offset + i),
                    new Insect(maxAmount, maxAmount, regenRate));
        }
    }

    private void initializePets(EtpetsGridModel model, Random random, EtpetsIdSequence idSequence) {
        List<GridCoordinate> available = traversableCoordinates(model, random);
        if (config().petCount() > available.size()) {
            throw new IllegalArgumentException("Invalid ET Pets pet initialization: requested "
                    + config().petCount() + " pets, but only " + available.size()
                    + " traversable cells are available.");
        }

        PetTraits defaultTraits = new PetTraits(
                EtpetsBalance.PET_MAX_ENERGY_DEFAULT,
                EtpetsBalance.PET_MOVEMENT_COST_MODIFIER_DEFAULT,
                EtpetsBalance.PET_REPRODUCTION_MIN_ENERGY_DEFAULT,
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_MAX_DEFAULT
        );

        for (int i = 0; i < config().petCount(); i++) {
            model.agentModel().setEntity(available.get(i), new Pet(
                    idSequence.next(),
                    null,
                    null,
                    -1,
                    EtpetsBalance.PET_MAX_ENERGY_DEFAULT,
                    0,
                    defaultTraits
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

