package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.*;

import java.util.*;
import java.util.function.*;

public final class WatorSimulationManager {

    private final WatorConfig config;

    private final GridStructure structure;
    private final WatorStatistics statistics;
    private final SimulationExecutor<WatorEntity> executor;
    private final WatorEntityFactory entityFactory;
    private final Random random;

    public WatorSimulationManager(WatorConfig config) {
        this.config = config;

        random = new java.util.Random();

        structure = new GridStructure(
                new GridTopology(config.cellShape(), config.gridEdgeBehavior()),
                new GridSize(config.gridWidth(), config.gridHeight())
        );
        statistics = new WatorStatistics(structure.cellCount());

        GridModel<WatorEntity> model = new ArrayGridModel<>(structure, WatorConstantEntity.WATER);

        entityFactory = new WatorEntityFactory();

        // 1. Create the context builder
        SimulationAgentContextBuilder<WatorEntity, WatorAgentContext> contextBuilder =
                (c, m) -> new WatorAgentContext(c, m, statistics, random);

        // 2. Create the agent logic as a Consumer<WatorAgentContext>
        WatorAgentLogicFactory logicFactory = new WatorAgentLogicFactory(config, entityFactory);
        Consumer<WatorAgentContext> agentLogic = logicFactory.createAgentLogic(WatorAgentLogicFactory.WatorLogicType.SIMPLE);

        // 3. Pass both to the runner
        AsynchronousStepRunner<WatorEntity, WatorAgentContext> runner =
                new AsynchronousStepRunner<>(model, WatorEntity::isAgent, contextBuilder, agentLogic);

        executor = new DefaultSimulationExecutor<>(runner, runner::model, new WatorTerminationCondition(), statistics);

        GridInitializer<WatorEntity> fishInit = GridInitializers.placeRandomPercent(
                this::createFish, config.fishPercent(), random);
        GridInitializer<WatorEntity> sharkInit = GridInitializers.placeRandomPercent(
                this::createShark, config.sharkPercent(), random);

        fishInit.initialize(executor.currentModel());
        sharkInit.initialize(executor.currentModel());

        updateStatistics(executor.currentStep(), executor.currentModel());
    }

    private WatorFish createFish() {
        WatorFish watorFish = entityFactory.createFish(0);
        statistics.incrementFishCells();
        return watorFish;
    }

    private WatorShark createShark() {
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
