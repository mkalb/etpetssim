package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.AsynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.support.AgentOrderingStrategies;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.simulations.core.model.AbstractSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.wator.model.entity.CreatureFactory;
import de.mkalb.etpetssim.simulations.wator.model.entity.Fish;
import de.mkalb.etpetssim.simulations.wator.model.entity.Shark;
import de.mkalb.etpetssim.simulations.wator.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;

import java.util.ArrayList;
import java.util.Random;

import static de.mkalb.etpetssim.engine.support.WorkCheckpoints.isCancellationCheckpoint;

/**
 * Simulation manager of Wa-Tor.
 */
public final class WatorSimulationManager
        extends AbstractSimulationManager<WatorEntity, WritableGridModel<WatorEntity>, WatorConfig, WatorStatistics> {

    private final GridStructure structure;
    private final WatorStatistics statistics;
    private final TimedSimulationExecutor<WatorEntity, WritableGridModel<WatorEntity>> executor;
    private final CreatureFactory creatureFactory;

    public WatorSimulationManager(WatorConfig config) {
        this(config, SimulationInitializationCancellation.none());
    }

    public WatorSimulationManager(WatorConfig config, SimulationInitializationCancellation cancellation) {
        super(config, WatorStatistics.metrics());
        cancellation.checkCanceled();

        structure = config.createGridStructure();
        statistics = new WatorStatistics(structure);
        var random = new Random(config.seed());
        var model = new ArrayGridModel<WatorEntity>(structure, TerrainConstant.WATER);

        creatureFactory = new CreatureFactory();
        var agentStepLogic = new WatorStepLogic(config, random, creatureFactory);
        var runner = new AsynchronousStepRunner<>(model, WatorEntity::isAgent, AgentOrderingStrategies.byPosition(),
                agentStepLogic);
        var terminationCondition = new WatorTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model,
                terminationCondition, statistics));

        initializeGrid(model, random, cancellation);
        cancellation.checkCanceled();
        initializeStatistics(model);
        recordInitialStatisticsSample();
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public WatorStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.setStepCount(executor.stepCount());
    }

    @Override
    protected TimedSimulationExecutor<WatorEntity, WritableGridModel<WatorEntity>> executor() {
        return executor;
    }

    Fish createFish(int stepIndexOfBirth) {
        return creatureFactory.createFish(stepIndexOfBirth);
    }

    Shark createShark(int stepIndexOfBirth) {
        return creatureFactory.createShark(stepIndexOfBirth, config().sharkBirthEnergy());
    }

    private void initializeGrid(WritableGridModel<WatorEntity> model,
                                Random random,
                                SimulationInitializationCancellation cancellation) {
        var fishCount = Math.clamp(
                Math.toIntExact(Math.round(config().fishPercent() * structure.cellCount())),
                0, structure.cellCount());
        var fish = new ArrayList<WatorEntity>(fishCount);
        for (int i = 0; i < fishCount; i++) {
            if (isCancellationCheckpoint(i)) {
                cancellation.checkCanceled();
            }
            fish.add(createInitialFish(random));
        }

        var sharkCount = Math.clamp(
                Math.toIntExact(Math.round(config().sharkPercent() * structure.cellCount())),
                0, structure.cellCount() - fishCount);
        var sharks = new ArrayList<WatorEntity>(sharkCount);
        for (int i = 0; i < sharkCount; i++) {
            if (isCancellationCheckpoint(i)) {
                cancellation.checkCanceled();
            }
            sharks.add(createInitialShark(random));
        }

        GridInitializers.placeAllAtRandomPositions(fish, WatorEntity::isWater, random, cancellation::checkCanceled)
                .initialize(model);
        GridInitializers.placeAllAtRandomPositions(sharks, WatorEntity::isWater, random, cancellation::checkCanceled)
                .initialize(model);
    }

    private Fish createInitialFish(Random random) {
        // The initial creatures are born before the first step, so that they start with a random age.
        int stepIndexOfBirth = -1 - random.nextInt(config().fishMaxAge());
        return createFish(stepIndexOfBirth);
    }

    private Shark createInitialShark(Random random) {
        int stepIndexOfBirth = -1 - random.nextInt(config().sharkMaxAge());
        return createShark(stepIndexOfBirth);
    }

    private void initializeStatistics(ReadableGridModel<WatorEntity> model) {
        int fishCellsInitial = Math.toIntExact(model.countEntities(WatorEntity::isFish));
        int sharkCellsInitial = Math.toIntExact(model.countEntities(WatorEntity::isShark));
        statistics.initializeStartupCellCounts(fishCellsInitial, sharkCellsInitial);
    }

}
