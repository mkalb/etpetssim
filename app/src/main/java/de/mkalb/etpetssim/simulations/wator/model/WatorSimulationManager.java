package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationManager;

import java.util.*;

public final class WatorSimulationManager
        extends AbstractTimedSimulationManager<WatorEntity, WatorConfig, WatorStatistics> {

    private final GridStructure structure;
    private final WatorStatistics statistics;
    private final TimedSimulationExecutor<WatorEntity> executor;

    public WatorSimulationManager(WatorConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new WatorStatistics(structure.cellCount());
        var random = new java.util.Random();
        var model = new ArrayGridModel<WatorEntity>(structure, WatorConstantEntity.WATER);
        var entityFactory = new WatorEntityFactory();

        // Executor with runner and terminationCondition
        var agentLogicFactory = new WatorAgentLogicFactory(config, random, entityFactory);
        var agentStepLogic = agentLogicFactory.createAgentLogic(WatorAgentLogicFactory.WatorLogicType.SIMPLE);
        var runner = new AsynchronousStepRunner<>(model, WatorEntity::isAgent, AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new WatorTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        // Initialize the grid with fish and sharks
        initializeGrid(model, random, entityFactory);
    }

    private void initializeGrid(GridModel<WatorEntity> model, Random random, WatorEntityFactory entityFactory) {
        GridInitializer<WatorEntity> fishInit = GridInitializers.placeRandomPercent(
                () -> createFish(entityFactory, random), config().fishPercent(), random);
        GridInitializer<WatorEntity> sharkInit = GridInitializers.placeRandomPercent(
                () -> createShark(entityFactory, random), config().sharkPercent(), random);

        fishInit.initialize(model);
        sharkInit.initialize(model);
    }

    private WatorFish createFish(WatorEntityFactory entityFactory, Random random) {
        int randomAge = random.nextInt(config().fishMaxAge());
        int timeOfBirth = -1 - randomAge; // negative age for birth time

        WatorFish watorFish = entityFactory.createFish(timeOfBirth);
        statistics.incrementFishCells();
        return watorFish;
    }

    private WatorShark createShark(WatorEntityFactory entityFactory, Random random) {
        int randomAge = random.nextInt(config().sharkMaxAge());
        int timeOfBirth = -1 - randomAge; // negative age for birth time

        WatorShark watorShark = entityFactory.createShark(timeOfBirth, config().sharkBirthEnergy());
        statistics.incrementSharkCells();
        return watorShark;
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
    protected TimedSimulationExecutor<WatorEntity> executor() {
        return executor;
    }

}
