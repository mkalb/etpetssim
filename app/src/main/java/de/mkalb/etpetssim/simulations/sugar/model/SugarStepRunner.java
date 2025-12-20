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

    private static final int DISTANCE_ORIGINAL = 0;
    private static final int DISTANCE_DIRECT_NEIGHBOR = 1;
    private static final int MAX_RANDOM_ATTEMPTS = 100;

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
        performAgentStep(model.agentModel(), model.resourceModel(), stepIndex, statistics);
        performResourceStep(model.resourceModel());
    }

    private void performAgentStep(WritableGridModel<SugarAgentEntity> agentModel,
                                  WritableGridModel<SugarResourceEntity> resourceModel,
                                  int stepIndex,
                                  SugarStatistics statistics) {
        // 1. Agent actions
        List<GridCell<SugarAgentEntity>> agentCells = new ArrayList<>(agentModel.nonDefaultCells().toList());
        // Random order is important
        Collections.shuffle(agentCells, random);
        int diedAgents = 0;
        for (GridCell<SugarAgentEntity> agentCell : agentCells) {
            // Case to SugarAgent. All non default cells must be agents.
            if (agentCell.entity() instanceof SugarAgent agent) {
                GridCoordinate originalCoordinate = agentCell.coordinate();

                // 1.1 Agent sight (scan visible cells within vision range)
                List<AgentMoveCandidate> moveCandidates = collectMoveCandidates(originalCoordinate, agentModel, resourceModel);

                // 1.2 Agent choose target cell (highest sugar, free cell)
                // prefer closer cells or original cell in case of ties
                GridCoordinate newCoordinate = Collections.min(moveCandidates, AGENT_MOVE_CANDIDATE_COMPARATOR)
                                                          .moveTo()
                                                          .mappedNeighborCoordinate();

                // 1.3 Agent movement (move one step toward chosen cell or stay if blocked)
                GridCoordinate finalCoordinate = attemptMove(originalCoordinate, newCoordinate, agent, agentModel);

                // 1.4 Agent harvest (collect all sugar from current cell)
                SugarResourceEntity sugarResourceEntity = resourceModel.getEntity(finalCoordinate);
                if (sugarResourceEntity instanceof SugarResourceSugar sugar) {
                    int harvestedSugar = sugar.currentAmount();
                    agent.gainEnergy(harvestedSugar);
                    sugar.reduceEnergy(harvestedSugar);
                }

                // 1.5 Agent metabolism and death (consume sugar, die if energy <= 0 or age > maxAge)
                agent.reduceEnergy(config.agentMetabolismRate());
                if ((agent.ageAtStepIndex(stepIndex) >= config.agentMaxAge()) || (agent.currentEnergy() <= 0)) {
                    agentModel.setEntityToDefault(finalCoordinate);
                    statistics.updateCells(-1);
                    diedAgents++;
                }
            }
        }
        for (int i = 0; i < diedAgents; i++) {
            // Find free random coordinate to spawn new agent
            Optional<GridCoordinate> spawnCoordinate = findRandomFreeCell(agentModel);
            if (spawnCoordinate.isPresent()) {
                // Spawn new agent
                SugarAgent newAgent = new SugarAgent(config.agentInitialEnergy(), stepIndex);
                agentModel.setEntity(spawnCoordinate.get(), newAgent);
                statistics.updateCells(1);
            }
        }
    }

    private Optional<GridCoordinate> findRandomFreeCell(WritableGridModel<SugarAgentEntity> agentModel) {
        GridCoordinate freeCoordinate = null;

        int attempts = 0;
        do {
            int x = random.nextInt(structure.size().width());
            int y = random.nextInt(structure.size().height());
            GridCoordinate coordinate = new GridCoordinate(x, y);
            if (agentModel.isDefaultEntity(coordinate)) {
                freeCoordinate = coordinate;
            }

            attempts++;
        } while ((freeCoordinate == null) && (attempts < MAX_RANDOM_ATTEMPTS));

        return Optional.ofNullable(freeCoordinate);
    }

    private void performResourceStep(WritableGridModel<SugarResourceEntity> resourceModel) {
        // 2. Sugar regeneration
        resourceModel.nonDefaultCells().forEach(cell -> {
            if (cell.entity() instanceof SugarResourceSugar sugar) {
                sugar.gainEnergy(config.sugarRegenerationRate());
            }
        });
    }

    private List<AgentMoveCandidate> collectMoveCandidates(GridCoordinate originalCoordinate,
                                                           WritableGridModel<SugarAgentEntity> agentModel,
                                                           WritableGridModel<SugarResourceEntity> resourceModel) {
        List<AgentMoveCandidate> moveCandidates = new ArrayList<>();

        // Always include original cell as candidate and use NNW direction as dummy
        CellNeighborWithEdgeBehavior originalCellNeighbor = new CellNeighborWithEdgeBehavior(
                originalCoordinate, CompassDirection.NNW, CellConnectionType.EDGE,
                originalCoordinate, originalCoordinate, EdgeBehaviorAction.VALID);
        moveCandidates.add(new AgentMoveCandidate(originalCellNeighbor, originalCellNeighbor,
                sugarAmountAtCoordinate(originalCoordinate, resourceModel), DISTANCE_ORIGINAL));

        // Find sight directions and iterate over them
        Set<CompassDirection> directions = CellNeighborhoods.cellNeighborDirections(originalCoordinate, config.neighborhoodMode(), structure.cellShape());
        for (CompassDirection direction : directions) {
            Optional<CellNeighborWithEdgeBehavior> neighborOpt = CellNeighborhoods.cellNeighborWithEdgeBehavior(originalCoordinate, config.neighborhoodMode(), direction, structure);

            if (neighborOpt.isPresent()
                    && ((neighborOpt.get().edgeBehaviorAction() == EdgeBehaviorAction.VALID)
                    || (neighborOpt.get().edgeBehaviorAction() == EdgeBehaviorAction.WRAPPED))) {
                CellNeighborWithEdgeBehavior neighbor = neighborOpt.get();
                GridCoordinate neighborCoordinate = neighbor.mappedNeighborCoordinate();
                // Check if the cell is free (not occupied by another agent)
                if (agentModel.isDefaultEntity(neighborCoordinate)) {
                    int sugarAmount = sugarAmountAtCoordinate(neighborCoordinate, resourceModel);
                    if (sugarAmount > 0) {
                        moveCandidates.add(new AgentMoveCandidate(neighbor, neighbor, sugarAmount, DISTANCE_DIRECT_NEIGHBOR));
                    }

                    // If direct neighbor is free and vision range > 1, look further in the same direction
                    if (config.agentVisionRange() > DISTANCE_DIRECT_NEIGHBOR) {
                        CellNeighborWithEdgeBehavior furtherNeighbor = neighbor;
                        for (int d = DISTANCE_DIRECT_NEIGHBOR + 1; d <= config.agentVisionRange(); d++) {
                            Optional<CellNeighborWithEdgeBehavior> furtherNeighborOpt = CellNeighborhoods.cellNeighborWithEdgeBehavior(
                                    furtherNeighbor.mappedNeighborCoordinate(),
                                    config.neighborhoodMode(),
                                    direction,
                                    structure);
                            if (furtherNeighborOpt.isPresent()
                                    && ((furtherNeighborOpt.get().edgeBehaviorAction() == EdgeBehaviorAction.VALID)
                                    || (furtherNeighborOpt.get().edgeBehaviorAction() == EdgeBehaviorAction.WRAPPED))) {
                                furtherNeighbor = furtherNeighborOpt.get();
                                GridCoordinate furtherNeighborCoordinate = furtherNeighbor.mappedNeighborCoordinate();
                                // Check if the cell is free (not occupied by another agent)
                                if (agentModel.isDefaultEntity(furtherNeighborCoordinate)) {
                                    int furtherSugarAmount = sugarAmountAtCoordinate(furtherNeighborCoordinate, resourceModel);
                                    if (furtherSugarAmount > 0) {
                                        moveCandidates.add(new AgentMoveCandidate(furtherNeighbor, neighbor, furtherSugarAmount, d));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return moveCandidates;
    }

    private GridCoordinate attemptMove(GridCoordinate originalCoordinate,
                                       GridCoordinate newCoordinate,
                                       SugarAgent agent,
                                       WritableGridModel<SugarAgentEntity> agentModel) {
        GridCoordinate finalCoordinate = originalCoordinate;
        if (!newCoordinate.equals(originalCoordinate) && agentModel.isDefaultEntity(newCoordinate)) {
            agentModel.setEntityToDefault(originalCoordinate);
            agentModel.setEntity(newCoordinate, agent);
            finalCoordinate = newCoordinate;
        }
        return finalCoordinate;
    }

    private int sugarAmountAtCoordinate(GridCoordinate coordinate,
                                        WritableGridModel<SugarResourceEntity> resourceModel) {
        SugarResourceEntity entity = resourceModel.getEntity(coordinate);
        if (entity instanceof SugarResourceSugar sugar) {
            return sugar.currentAmount();
        }
        return 0;
    }

    private record AgentMoveCandidate(CellNeighborWithEdgeBehavior target,
                                      CellNeighborWithEdgeBehavior moveTo,
                                      int targetSugarAmount,
                                      int distanceToTarget) {
    }

}
