package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

import java.util.*;

public final class SugarAgentLogic {

    private static final Comparator<AgentMoveCandidate> AGENT_MOVE_CANDIDATE_COMPARATOR =
            Comparator.comparingInt(AgentMoveCandidate::targetSugarAmount).reversed()
                      .thenComparingInt(AgentMoveCandidate::distanceToTarget)
                      // deterministic tie-breaker using direction of moveTo
                      .thenComparingInt(c -> c.moveTo().direction().ordinal());

    private static final int DISTANCE_ORIGINAL = 0;
    private static final int DISTANCE_DIRECT_NEIGHBOR = 1;
    // Fast-path limit for random spawn sampling; a guaranteed linear scan follows if this is exhausted.
    private static final int MAX_RANDOM_SPAWN_ATTEMPTS = 20;

    private SugarAgentLogic() {
    }

    public static void apply(SugarConfig config,
                             Random random,
                             SugarGridModel gridModel,
                             int stepIndex,
                             SugarStatistics statistics) {
        WritableGridModel<AgentEntity> agentModel = gridModel.agentModel();
        WritableGridModel<ResourceEntity> resourceModel = gridModel.resourceModel();
        GridStructure structure = gridModel.structure();

        // 1. Agent actions
        List<GridCell<AgentEntity>> agentCells = agentModel.nonDefaultCells();
        // Random order is important
        Collections.shuffle(agentCells, random);
        int diedAgents = 0;
        for (GridCell<AgentEntity> agentCell : agentCells) {
            // Cast to Agent. All non-default cells must be agents.
            if (agentCell.entity() instanceof Agent agent) {
                GridCoordinate originalCoordinate = agentCell.coordinate();

                // 1.1 Agent sight (scan visible cells within vision range)
                List<AgentMoveCandidate> moveCandidates = collectMoveCandidates(originalCoordinate, config, structure, agentModel, resourceModel);

                // 1.2 Agent choose target cell (highest sugar, free cell)
                // prefer closer cells or original cell in case of ties
                GridCoordinate newCoordinate = Collections.min(moveCandidates, AGENT_MOVE_CANDIDATE_COMPARATOR)
                                                          .moveTo()
                                                          .mappedNeighborCoordinate();

                // 1.3 Agent movement (move one step toward chosen cell or stay if blocked)
                GridCoordinate finalCoordinate = attemptMove(originalCoordinate, newCoordinate, agent, agentModel);

                // 1.4 Agent harvest (collect all sugar from current cell)
                ResourceEntity sugarResourceEntity = resourceModel.getEntity(finalCoordinate);
                if (sugarResourceEntity instanceof Sugar sugar) {
                    int harvestedSugar = sugar.currentAmount();
                    agent.gainEnergy(harvestedSugar);
                    sugar.reduceAmount(harvestedSugar);
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
            Optional<GridCoordinate> spawnCoordinate = findRandomFreeCell(structure, random, agentModel);
            if (spawnCoordinate.isPresent()) {
                // Spawn new agent
                Agent newAgent = new Agent(config.agentInitialEnergy(), stepIndex);
                agentModel.setEntity(spawnCoordinate.get(), newAgent);
                statistics.updateCells(1);
            }
        }
    }

    private static Optional<GridCoordinate> findRandomFreeCell(GridStructure structure,
                                                               Random random,
                                                               WritableGridModel<AgentEntity> agentModel) {
        int width = structure.size().width();
        int height = structure.size().height();

        // Fast path: random sampling works well when the grid is sparsely populated.
        for (int i = 0; i < MAX_RANDOM_SPAWN_ATTEMPTS; i++) {
            GridCoordinate candidate = new GridCoordinate(random.nextInt(width), random.nextInt(height));
            if (agentModel.isDefaultEntity(candidate)) {
                return Optional.of(candidate);
            }
        }

        // Fallback: linear scan from a random offset guarantees a free cell is found if one exists.
        // This handles dense grids where random sampling would likely miss the few remaining free cells.
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                GridCoordinate candidate = new GridCoordinate((startX + dx) % width, (startY + dy) % height);
                if (agentModel.isDefaultEntity(candidate)) {
                    return Optional.of(candidate);
                }
            }
        }

        return Optional.empty(); // Grid is completely full.
    }

    private static List<AgentMoveCandidate> collectMoveCandidates(GridCoordinate originalCoordinate,
                                                                  SugarConfig config,
                                                                  GridStructure structure,
                                                                  WritableGridModel<AgentEntity> agentModel,
                                                                  WritableGridModel<ResourceEntity> resourceModel) {
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

    private static GridCoordinate attemptMove(GridCoordinate originalCoordinate,
                                              GridCoordinate newCoordinate,
                                              Agent agent,
                                              WritableGridModel<AgentEntity> agentModel) {
        GridCoordinate finalCoordinate = originalCoordinate;
        if (!newCoordinate.equals(originalCoordinate) && agentModel.isDefaultEntity(newCoordinate)) {
            agentModel.setEntityToDefault(originalCoordinate);
            agentModel.setEntity(newCoordinate, agent);
            finalCoordinate = newCoordinate;
        }
        return finalCoordinate;
    }

    private static int sugarAmountAtCoordinate(GridCoordinate coordinate,
                                               WritableGridModel<ResourceEntity> resourceModel) {
        ResourceEntity entity = resourceModel.getEntity(coordinate);
        if (entity instanceof Sugar sugar) {
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
