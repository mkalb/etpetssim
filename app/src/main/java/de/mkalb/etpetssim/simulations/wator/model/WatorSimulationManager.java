package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.*;

import java.util.*;

public final class WatorSimulationManager {

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
        var runner = new AsynchronousStepRunner<>(model, WatorEntity::isAgent, agentStepLogic);
        var terminationCondition = new WatorTerminationCondition();
        executor = new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics);

        // Initialize the grid with fish and sharks
        initializeGrid(model, random, entityFactory);
    }

    private void initializeGrid(GridModel<WatorEntity> model, Random random, WatorEntityFactory entityFactory) {
        GridInitializer<WatorEntity> fishInit = GridInitializers.placeRandomPercent(
                () -> createFish(entityFactory), config.fishPercent(), random);
        GridInitializer<WatorEntity> sharkInit = GridInitializers.placeRandomPercent(
                () -> createShark(entityFactory), config.sharkPercent(), random);

        fishInit.initialize(model);
        sharkInit.initialize(model);
    }

    private WatorFish createFish(WatorEntityFactory entityFactory) {
        WatorFish watorFish = entityFactory.createFish(0);
        statistics.incrementFishCells();
        return watorFish;
    }

    private WatorShark createShark(WatorEntityFactory entityFactory) {
        WatorShark watorShark = entityFactory.createShark(0, config.sharkBirthEnergy());
        statistics.incrementSharkCells();
        return watorShark;
    }

    public void executeStep() {
        executor.executeStep();
        updateStatistics(executor.currentStep(), executor.currentModel());
    }

    void updateStatistics(long currentStep, GridModel<WatorEntity> currentModel) {
        statistics.update(currentStep);
    }

    public boolean isRunning() {
        return executor.isRunning();
    }

    public ReadableGridModel<WatorEntity> currentModel() {
        return executor.currentModel();
    }

    public long currentStep() {
        return executor.currentStep();
    }

    public WatorStatistics statistics() {
        return statistics;
    }

    public GridStructure structure() {
        return structure;
    }

    public WatorConfig config() {
        return config;
    }

}
