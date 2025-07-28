package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.*;

import java.util.function.*;

public final class WatorSimulationManager {

    private final WatorConfig config;

    private final GridStructure structure;
    private final WatorStatistics statistics;
    private final SimulationExecutor<WatorEntity> executor;
    private final WatorEntityFactory entityFactory;

    public WatorSimulationManager(WatorConfig config) {
        this.config = config;

        structure = new GridStructure(
                new GridTopology(config.cellShape(), config.gridEdgeBehavior()),
                new GridSize(config.gridWidth(), config.gridHeight())
        );
        statistics = new WatorStatistics(structure.cellCount());

        GridModel<WatorEntity> model = new ArrayGridModel<>(structure, WatorConstantEntity.WATER);

        entityFactory = new WatorEntityFactory();

        // 1. Create the context builder
        SimulationAgentContextBuilder<WatorEntity, WatorAgentContext> contextBuilder =
                (c, m) -> new WatorAgentContext(c, m, statistics);

        // 2. Create the agent logic as a Consumer<WatorAgentContext>
        WatorAgentLogicFactory logicFactory = new WatorAgentLogicFactory(config, entityFactory);
        Consumer<WatorAgentContext> agentLogic = logicFactory.createAgentLogic(WatorAgentLogicFactory.WatorLogicType.SIMPLE);

        // 3. Pass both to the runner
        AsynchronousStepRunner<WatorEntity, WatorAgentContext> runner =
                new AsynchronousStepRunner<>(model, WatorEntity::isAgent, contextBuilder, agentLogic);

        // TODO Change WatorTerminationCondition to use WatorStatistics
        executor = new DefaultSimulationExecutor<>(runner, runner::model, new WatorTerminationCondition(), statistics);

        GridInitializer<WatorEntity> fishInit = GridInitializers.placeRandomPercent(
                () -> entityFactory.createFish(0), config.fishPercent(), new java.util.Random()
        );
        GridInitializer<WatorEntity> sharkInit = GridInitializers.placeRandomPercent(
                () -> entityFactory.createShark(0, config.sharkBirthEnergy()), config.sharkPercent(), new java.util.Random()
        );

        fishInit.initialize(executor.currentModel());
        sharkInit.initialize(executor.currentModel());

        updateStatistics(executor.currentStep(), executor.currentModel());
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
