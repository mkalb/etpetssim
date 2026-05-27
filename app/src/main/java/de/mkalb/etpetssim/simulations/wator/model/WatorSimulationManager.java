package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.AsynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.support.AgentOrderingStrategies;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.wator.model.entity.*;

import java.util.*;

public final class WatorSimulationManager
        extends AbstractTimedSimulationManager<WatorEntity, WritableGridModel<WatorEntity>, WatorConfig,
        WatorStatistics> {

    private final GridStructure structure;
    private final WatorStatistics statistics;
    private final TimedSimulationExecutor<WatorEntity, WritableGridModel<WatorEntity>> executor;

    public WatorSimulationManager(WatorConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new WatorStatistics(structure);
        var random = new Random(config.seed());
        var model = new ArrayGridModel<WatorEntity>(structure, TerrainConstant.WATER);
        var entityFactory = new CreatureFactory();

        // Executor with runner and terminationCondition
        var agentLogicFactory = new WatorAgentLogicFactory(config, random, entityFactory);
        var agentStepLogic = agentLogicFactory.createAgentLogic();
        var runner = new AsynchronousStepRunner<>(model, WatorEntity::isAgent, AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new WatorTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        // Initialize the grid with fish and sharks
        initializeGrid(model, random, entityFactory);

        statistics.updateCells();
    }

    private void initializeGrid(WritableGridModel<WatorEntity> model, Random random, CreatureFactory entityFactory) {
        var fishCount = Math.clamp(
                Math.toIntExact(Math.round(config().fishPercent() * structure.cellCount())),
                0, structure.cellCount());
        var fish = new ArrayList<WatorEntity>(fishCount);
        for (int i = 0; i < fishCount; i++) {
            fish.add(createFish(entityFactory, random));
        }

        var sharkCount = Math.clamp(
                Math.toIntExact(Math.round(config().sharkPercent() * structure.cellCount())),
                0, structure.cellCount() - fishCount);
        var sharks = new ArrayList<WatorEntity>(sharkCount);
        for (int i = 0; i < sharkCount; i++) {
            sharks.add(createShark(entityFactory, random));
        }

        GridInitializers.placeAllAtRandomPositions(fish, WatorEntity::isWater, random).initialize(model);
        GridInitializers.placeAllAtRandomPositions(sharks, WatorEntity::isWater, random).initialize(model);
    }

    private Fish createFish(CreatureFactory entityFactory, Random random) {
        int stepIndexOfBirth = -1 - random.nextInt(config().fishMaxAge()); // negative age for birth time

        Fish fish = entityFactory.createFish(stepIndexOfBirth);
        statistics.incrementFishCells();
        return fish;
    }

    private Shark createShark(CreatureFactory entityFactory, Random random) {
        int stepIndexOfBirth = -1 - random.nextInt(config().sharkMaxAge()); // negative age for birth time

        Shark shark = entityFactory.createShark(stepIndexOfBirth, config().sharkBirthEnergy());
        statistics.incrementSharkCells();
        return shark;
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
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @Override
    protected TimedSimulationExecutor<WatorEntity, WritableGridModel<WatorEntity>> executor() {
        return executor;
    }

}
