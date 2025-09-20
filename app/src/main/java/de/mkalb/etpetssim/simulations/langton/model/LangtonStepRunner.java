package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;

import java.util.*;

@SuppressWarnings("ClassCanBeRecord")
public final class LangtonStepRunner
        implements SimulationStepRunner<LangtonStatistics> {

    private final GridStructure structure;
    private final LangtonConfig config;
    private final WritableGridModel<LangtonGroundEntity> groundModel;
    private final WritableGridModel<LangtonAntEntity> antModel;
    private final CompositeGridModel<LangtonEntity> compositeGridModel;

    public LangtonStepRunner(GridStructure structure,
                             LangtonConfig config,
                             WritableGridModel<LangtonGroundEntity> groundModel,
                             WritableGridModel<LangtonAntEntity> antModel,
                             CompositeGridModel<LangtonEntity> compositeGridModel) {
        this.structure = structure;
        this.config = config;
        this.groundModel = groundModel;
        this.antModel = antModel;
        this.compositeGridModel = compositeGridModel;
    }

    public CompositeGridModel<LangtonEntity> compositeGridModel() {
        return compositeGridModel;
    }

    @Override
    public void performStep(int stepIndex, LangtonStatistics context) {
        List<GridCell<LangtonAntEntity>> orderedAgentCells = antModel.filteredAndSortedCells(LangtonEntity::isAgent, AgentOrderingStrategies.byPosition());
        for (GridCell<LangtonAntEntity> agentCell : orderedAgentCells) {

        }
    }

}
