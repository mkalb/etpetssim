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

    // Resource initialization ranges (spec V1 defaults).
    private static final int PLANT_MAX_AMOUNT_MIN = 3;
    private static final int PLANT_MAX_AMOUNT_MAX = 6;
    private static final double PLANT_BASE_REGEN_RATE = 0.2d;
    private static final int INSECT_MAX_AMOUNT_MIN = 6;
    private static final int INSECT_MAX_AMOUNT_MAX = 12;
    private static final double INSECT_BASE_REGEN_RATE = 0.05d;
    private static final double REGEN_RATE_VARIANCE = 0.02d;
    private static final double VARIANCE_SPREAD_FACTOR = 2.0d;

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
                new SparseGridModel<>(structure, EtpetsTerrainConstant.GROUND),
                new SparseGridModel<>(structure, EtpetsResourceNone.NONE),
                new SparseGridModel<>(structure, EtpetsAgentNone.NONE)
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

        List<GridCoordinate> coordinates = new ArrayList<>(structure.coordinatesList());
        Collections.shuffle(coordinates, random);

        int offset = 0;
        for (int i = 0; (i < rockCount) && ((offset + i) < coordinates.size()); i++) {
            model.terrainModel().setEntity(coordinates.get(offset + i), EtpetsTerrainConstant.ROCK);
        }
        offset += rockCount;
        for (int i = 0; (i < waterCount) && ((offset + i) < coordinates.size()); i++) {
            model.terrainModel().setEntity(coordinates.get(offset + i), EtpetsTerrainConstant.WATER);
        }
    }

    private void initializeResources(EtpetsGridModel model, Random random) {
        int totalCells = structure.cellCount();
        int plantCount = computePercentCount(totalCells, config().plantPercent());
        int insectCount = computePercentCount(totalCells, config().insectPercent());

        List<GridCoordinate> available = traversableCoordinates(model, random);
        int requestedResources = plantCount + insectCount;
        if (requestedResources > available.size()) {
            throw new IllegalArgumentException("Invalid ET Pets resource initialization: requested "
                    + requestedResources + " resource cells, but only " + available.size()
                    + " traversable cells are available.");
        }

        int plantRange = (PLANT_MAX_AMOUNT_MAX - PLANT_MAX_AMOUNT_MIN) + 1;
        int insectRange = (INSECT_MAX_AMOUNT_MAX - INSECT_MAX_AMOUNT_MIN) + 1;

        int offset = 0;
        for (int i = 0; (i < plantCount) && ((offset + i) < available.size()); i++) {
            double maxAmount = PLANT_MAX_AMOUNT_MIN + random.nextInt(plantRange);
            double regenRate = PLANT_BASE_REGEN_RATE
                    + (((random.nextDouble() * VARIANCE_SPREAD_FACTOR) - 1.0d) * REGEN_RATE_VARIANCE);
            model.resourceModel().setEntity(available.get(offset + i),
                    new EtpetsResourcePlant(maxAmount, maxAmount, regenRate));
        }
        offset += plantCount;
        for (int i = 0; (i < insectCount) && ((offset + i) < available.size()); i++) {
            double maxAmount = INSECT_MAX_AMOUNT_MIN + random.nextInt(insectRange);
            double regenRate = INSECT_BASE_REGEN_RATE
                    + (((random.nextDouble() * VARIANCE_SPREAD_FACTOR) - 1.0d) * REGEN_RATE_VARIANCE);
            model.resourceModel().setEntity(available.get(offset + i),
                    new EtpetsResourceInsect(maxAmount, maxAmount, regenRate));
        }
    }

    private void initializePets(EtpetsGridModel model, Random random, EtpetsIdSequence idSequence) {
        List<GridCoordinate> available = traversableCoordinates(model, random);
        if (config().petCount() > available.size()) {
            throw new IllegalArgumentException("Invalid ET Pets pet initialization: requested "
                    + config().petCount() + " pets, but only " + available.size()
                    + " traversable cells are available.");
        }

        EtpetsPetTraits defaultTraits = new EtpetsPetTraits(
                EtpetsAgentLogic.DEFAULT_MAX_ENERGY,
                EtpetsAgentLogic.DEFAULT_MOVEMENT_COST_MODIFIER,
                EtpetsAgentLogic.DEFAULT_REPRODUCTION_MIN_ENERGY,
                EtpetsAgentLogic.DEFAULT_REPRODUCTION_COOLDOWN_MAX
        );

        int placed = 0;
        for (GridCoordinate coordinate : available) {
            if (placed >= config().petCount()) {
                break;
            }
            if (model.agentModel().isDefaultEntity(coordinate)) {
                model.agentModel().setEntity(coordinate, new EtpetsPet(
                        idSequence.next(),
                        null,
                        null,
                        -1,
                        EtpetsAgentLogic.DEFAULT_MAX_ENERGY,
                        0,
                        defaultTraits
                ));
                placed++;
            }
        }
    }

    private List<GridCoordinate> traversableCoordinates(EtpetsGridModel model, Random random) {
        List<GridCoordinate> available = new ArrayList<>();
        for (GridCoordinate coordinate : structure.coordinatesList()) {
            EtpetsTerrainEntity terrainEntity = model.terrainModel().getEntity(coordinate);
            if (terrainEntity == EtpetsTerrainConstant.GROUND) {
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
                                                         .countEntities(entity -> (entity instanceof EtpetsPet pet) && !pet.isDead()));
        int eggCountInitial = Math.toIntExact(model.agentModel()
                                                   .countEntities(entity -> entity instanceof EtpetsPetEgg));
        int cumulativeDeadPetCountInitial = Math.toIntExact(model.agentModel()
                                                                 .countEntities(entity -> (entity instanceof EtpetsPet pet) && pet.isDead()));
        statistics.updateInitialCells(activePetCountInitial, eggCountInitial, cumulativeDeadPetCountInitial);
    }

    @Override
    protected TimedSimulationExecutor<EtpetsEntity, EtpetsGridModel> executor() {
        return executor;
    }

}

