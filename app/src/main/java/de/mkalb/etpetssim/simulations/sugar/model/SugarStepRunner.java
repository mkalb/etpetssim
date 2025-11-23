package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.SimulationStepRunner;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

import java.util.*;

public final class SugarStepRunner
        implements SimulationStepRunner<SugarStatistics> {

    private static final Comparator<AgentMoveCandidate> AGENT_MOVE_CANDIDATE_COMPARATOR =
            Comparator.comparingInt(AgentMoveCandidate::targetSugarAmount).reversed()
                      .thenComparingInt(AgentMoveCandidate::distanceToTarget)
                      // deterministic tie-breaker using direction of moveTo
                      .thenComparingInt(c -> c.moveTo.direction().ordinal());

    private final GridStructure structure;
    private final Random random;
    private final SugarConfig config;
    private final SugarGridModel model;

    public SugarStepRunner(SugarConfig config,
                           Random random,
                           SugarGridModel model) {
        this.config = config;
        this.random = random;
        this.model = model;
        structure = model.structure();
    }

    public SugarGridModel model() {
        return model;
    }

    @Override
    public void performStep(int stepIndex, SugarStatistics statistics) {
        performAgentStep(model.agentModel(), model.resourceModel(), statistics);
        performResourceStep(model.resourceModel());
    }

    private void performAgentStep(WritableGridModel<SugarAgentEntity> agentModel,
                                  WritableGridModel<SugarResourceEntity> resourceModel,
                                  SugarStatistics statistics) {
        // 1. Agent actions
        List<GridCell<SugarAgentEntity>> agentCells = new ArrayList<>(agentModel.nonDefaultCells().toList());
        // Random order is important
        Collections.shuffle(agentCells, random);
        for (GridCell<SugarAgentEntity> agentCell : agentCells) {
            // Case to SugarAgent. All non default cells must be agents.
            if (agentCell.entity() instanceof SugarAgent agent) {
                GridCoordinate originalCoordinate = agentCell.coordinate();

                // 1.1 Agent sight (scan visible cells within vision range)
                // include original cell and skip occupied cells
                List<AgentMoveCandidate> moveCandidates = new ArrayList<>();
                CellNeighborWithEdgeBehavior originalCellNeighbor = new CellNeighborWithEdgeBehavior(originalCoordinate, CompassDirection.N, CellConnectionType.EDGE, originalCoordinate, originalCoordinate, EdgeBehaviorAction.VALID);
                moveCandidates.add(new AgentMoveCandidate(originalCellNeighbor, originalCellNeighbor, sugarAmountAtCoordinate(originalCoordinate, resourceModel), 0));
                // TODO fill moveCandidates with other visible cells

                // 1.2 Agent choose target cell (highest sugar, free cell)
                // prefer closer cells or original cell in case of ties
                GridCoordinate newCoordinate = Collections.max(moveCandidates, AGENT_MOVE_CANDIDATE_COMPARATOR)
                                                          .moveTo()
                                                          .mappedNeighborCoordinate();

                // 1.3 Agent movement (move one step toward chosen cell or stay if blocked)
                if (!newCoordinate.equals(originalCoordinate)) {
                    agentModel.setEntityToDefault(originalCoordinate);
                    agentModel.setEntity(newCoordinate, agent);
                }

                // 1.4 Agent harvest (collect all sugar from current cell)
                SugarResourceEntity sugarResourceEntity = resourceModel.getEntity(newCoordinate);
                if (sugarResourceEntity instanceof SugarResourceSugar sugar) {
                    int harvestedSugar = sugar.currentAmount();
                    agent.gainEnergy(harvestedSugar);
                    sugar.reduceEnergy(harvestedSugar);
                }

                // 1.5 Agent metabolism and death (consume sugar, die if energy <= 0)
                agent.reduceEnergy(config.agentMetabolismRate());
                if (agent.currentEnergy() <= 0) {
                    agentModel.setEntityToDefault(newCoordinate);
                    statistics.updateCells(-1);
                }
            }
        }
    }

    private int sugarAmountAtCoordinate(GridCoordinate coordinate,
                                        WritableGridModel<SugarResourceEntity> resourceModel) {
        SugarResourceEntity entity = resourceModel.getEntity(coordinate);
        if (entity instanceof SugarResourceSugar sugar) {
            return sugar.currentAmount();
        }
        return 0;
    }

    private void performResourceStep(WritableGridModel<SugarResourceEntity> resourceModel) {
        // 2. Sugar regeneration
        resourceModel.nonDefaultCells().forEach(cell -> {
            if (cell.entity() instanceof SugarResourceSugar sugar) {
                sugar.gainEnergy(config.sugarRegenerationRate());
            }
        });
    }

    private record AgentMoveCandidate(CellNeighborWithEdgeBehavior target,
                                      CellNeighborWithEdgeBehavior moveTo,
                                      int targetSugarAmount,
                                      int distanceToTarget) {
    }

}
