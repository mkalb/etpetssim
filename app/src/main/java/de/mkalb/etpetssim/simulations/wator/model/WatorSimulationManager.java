package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.model.SimulationManager;

import java.util.*;

public final class WatorSimulationManager implements SimulationManager<WatorEntity, WatorConfig, WatorStatistics> {

    private final WatorConfig config;

    private final GridStructure structure;
    private final WatorStatistics statistics;
    private final SimulationExecutor<WatorEntity> executor;

    public WatorSimulationManager(WatorConfig config) {
        this.config = config;

        structure = new GridStructure(
                new GridTopology(config.cellShape(), config.gridEdgeBehavior()),
                new GridSize(config.gridWidth(), config.gridHeight())
        );
        statistics = new WatorStatistics(structure.cellCount());
        var random = new java.util.Random();
        var model = new ArrayGridModel<WatorEntity>(structure, WatorConstantEntity.WATER);
        var entityFactory = new WatorEntityFactory();

        // Executor with runner and terminationCondition
        var agentLogicFactory = new WatorAgentLogicFactory(config, random, entityFactory);
        var agentStepLogic = agentLogicFactory.createAgentLogic(WatorAgentLogicFactory.WatorLogicType.SIMPLE);
        var runner = new AsynchronousStepRunner<>(model, WatorEntity::isAgent, AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new WatorTerminationCondition();
        executor = new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics);

        // Initialize the grid with fish and sharks
        initializeGrid(model, random, entityFactory);
    }

    private void initializeGrid(GridModel<WatorEntity> model, Random random, WatorEntityFactory entityFactory) {
        GridInitializer<WatorEntity> fishInit = GridInitializers.placeRandomPercent(
                () -> createFish(entityFactory, random), config.fishPercent(), random);
        GridInitializer<WatorEntity> sharkInit = GridInitializers.placeRandomPercent(
                () -> createShark(entityFactory, random), config.sharkPercent(), random);

        fishInit.initialize(model);
        sharkInit.initialize(model);
    }

    private WatorFish createFish(WatorEntityFactory entityFactory, Random random) {
        long randomAge = random.nextInt(config().fishMaxAge());
        long timeOfBirth = -1 - randomAge; // negative age for birth time

        WatorFish watorFish = entityFactory.createFish(timeOfBirth);
        statistics.incrementFishCells();
        return watorFish;
    }

    private WatorShark createShark(WatorEntityFactory entityFactory, Random random) {
        long randomAge = random.nextInt(config.sharkMaxAge());
        long timeOfBirth = -1 - randomAge; // negative age for birth time

        WatorShark watorShark = entityFactory.createShark(timeOfBirth, config.sharkBirthEnergy());
        statistics.incrementSharkCells();
        return watorShark;
    }

    @Override
    public WatorConfig config() {
        return config;
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public WatorStatistics statistics() {
        return statistics;
    }

    void updateStatistics(long currentStep, GridModel<WatorEntity> currentModel) {
        statistics.update(currentStep);
    }

    @Override
    public void executeStep() {
        executor.executeStep();
        updateStatistics(executor.currentStep(), executor.currentModel());
    }

    @Override
    public boolean isRunning() {
        return executor.isRunning();
    }

    @Override
    public long currentStep() {
        return executor.currentStep();
    }

    @Override
    public ReadableGridModel<WatorEntity> currentModel() {
        return executor.currentModel();
    }

}
