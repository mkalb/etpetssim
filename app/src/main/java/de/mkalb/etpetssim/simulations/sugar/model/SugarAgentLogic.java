package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

import java.util.*;

/**
 * Agent phase of a Sugarscape step: the movement, harvest, metabolism, death, and replacement of the agents.
 */
public final class SugarAgentLogic {

    private static final Comparator<AgentMoveCandidate> AGENT_MOVE_CANDIDATE_COMPARATOR =
            Comparator.comparingInt(AgentMoveCandidate::targetSugarAmount).reversed()
                      .thenComparingInt(AgentMoveCandidate::distanceToTarget)
                      // deterministic tie-breaker using direction of moveTo
                      .thenComparing(candidate -> candidate.moveTo().direction());

    private static final int DISTANCE_ORIGINAL = 0;
    private static final int DISTANCE_DIRECT_NEIGHBOR = 1;
    // Fast-path limit for random spawn sampling; a guaranteed linear scan follows if this is exhausted.
    private static final int MAX_RANDOM_SPAWN_ATTEMPTS = 20;

    private SugarAgentLogic() {
    }

    /**
     * Applies the agent phase of one step to the grid model.
     *
     * <p>The agents act in random order. Each agent looks in its neighbor directions up to its vision range, chooses
     * the free cell with the most sugar (preferring closer cells and its own cell in case of ties), moves one cell
     * toward it if that cell is free, harvests all sugar of its cell, and consumes energy by its metabolism. An agent
     * that reaches its maximum age or runs out of energy dies. After all agents have acted, a new agent spawns on a
     * random free cell for each dead agent.
     *
     * @param gridModel  the grid model whose agents and sugar are updated
     * @param config     the configuration with the agent parameters
     * @param random     the random source for the agent order and the spawn cells
     * @param stepIndex  the index of the current step
     * @param statistics the statistics to update with the changed agent count
     */
    public static void apply(SugarGridModel gridModel,
                             SugarConfig config,
                             Random random,
                             int stepIndex,
                             SugarStatistics statistics) {
        WritableGridModel<AgentEntity> agentModel = gridModel.agentModel();
        WritableGridModel<ResourceEntity> resourceModel = gridModel.resourceModel();
        GridStructure structure = gridModel.structure();

        List<GridCell<AgentEntity>> agentCells = agentModel.nonDefaultCells();
        // Random order is important
        Collections.shuffle(agentCells, random);
        int diedAgents = 0;
        for (GridCell<AgentEntity> agentCell : agentCells) {
            if (agentCell.entity() instanceof Agent agent) {
                GridCoordinate originalCoordinate = agentCell.coordinate();

                List<AgentMoveCandidate> moveCandidates = collectMoveCandidates(originalCoordinate, config, structure,
                        agentModel, resourceModel);
                GridCoordinate newCoordinate = Collections.min(moveCandidates, AGENT_MOVE_CANDIDATE_COMPARATOR)
                                                          .moveTo()
                                                          .mappedNeighborCoordinate();
                GridCoordinate finalCoordinate = attemptMove(originalCoordinate, newCoordinate, agent, agentModel);

                // Harvest all sugar of the current cell
                if (resourceModel.getEntity(finalCoordinate) instanceof Sugar sugar) {
                    int harvestedSugar = sugar.currentAmount();
                    agent.adjustEnergy(harvestedSugar);
                    sugar.adjustAmount(-harvestedSugar);
                }

                agent.adjustEnergy(-config.agentMetabolismRate());
                if ((agent.ageAtStepIndex(stepIndex) >= config.agentMaxAge()) || (agent.currentEnergy() <= 0)) {
                    agentModel.setEntityToDefault(finalCoordinate);
                    statistics.adjustAgentCells(-1);
                    diedAgents++;
                }
            }
        }
        for (int i = 0; i < diedAgents; i++) {
            Optional<GridCoordinate> spawnCoordinate = findRandomFreeCell(structure, random, agentModel);
            if (spawnCoordinate.isPresent()) {
                agentModel.setEntity(spawnCoordinate.get(), new Agent(config.agentInitialEnergy(), stepIndex));
                statistics.adjustAgentCells(1);
            }
        }
    }

    private static List<AgentMoveCandidate> collectMoveCandidates(GridCoordinate originalCoordinate,
                                                                  SugarConfig config,
                                                                  GridStructure structure,
                                                                  ReadableGridModel<AgentEntity> agentModel,
                                                                  ReadableGridModel<ResourceEntity> resourceModel) {
        List<AgentMoveCandidate> moveCandidates = new ArrayList<>();

        // Always include original cell as candidate and use NNW direction as dummy
        CellNeighborWithEdgeBehavior originalCellNeighbor = new CellNeighborWithEdgeBehavior(
                originalCoordinate, CompassDirection.NNW, CellConnectionType.EDGE,
                originalCoordinate, originalCoordinate, EdgeBehaviorAction.VALID);
        moveCandidates.add(new AgentMoveCandidate(originalCellNeighbor, originalCellNeighbor,
                sugarAmountAtCoordinate(originalCoordinate, resourceModel), DISTANCE_ORIGINAL));

        Set<CompassDirection> directions = CellNeighborhoods.cellNeighborDirections(originalCoordinate,
                config.neighborhoodMode(), structure.cellShape());
        for (CompassDirection direction : directions) {
            Optional<CellNeighborWithEdgeBehavior> directNeighbor = findReachableNeighbor(originalCoordinate, direction,
                    config, structure);
            // The agent looks further in the same direction only through a free direct neighbor
            if (directNeighbor.isEmpty() || !agentModel.isDefaultEntity(directNeighbor.get().mappedNeighborCoordinate())) {
                continue;
            }
            CellNeighborWithEdgeBehavior neighbor = directNeighbor.get();
            addMoveCandidateIfFreeWithSugar(moveCandidates, neighbor, neighbor, DISTANCE_DIRECT_NEIGHBOR,
                    agentModel, resourceModel);

            CellNeighborWithEdgeBehavior furtherNeighbor = neighbor;
            for (int distance = DISTANCE_DIRECT_NEIGHBOR + 1; distance <= config.agentVisionRange(); distance++) {
                Optional<CellNeighborWithEdgeBehavior> nextNeighbor = findReachableNeighbor(
                        furtherNeighbor.mappedNeighborCoordinate(), direction, config, structure);
                if (nextNeighbor.isEmpty()) {
                    break;
                }
                furtherNeighbor = nextNeighbor.get();
                addMoveCandidateIfFreeWithSugar(moveCandidates, furtherNeighbor, neighbor, distance,
                        agentModel, resourceModel);
            }
        }
        return moveCandidates;
    }

    private static Optional<CellNeighborWithEdgeBehavior> findReachableNeighbor(GridCoordinate coordinate,
                                                                                CompassDirection direction,
                                                                                SugarConfig config,
                                                                                GridStructure structure) {
        return CellNeighborhoods.cellNeighborWithEdgeBehavior(coordinate, config.neighborhoodMode(), direction, structure)
                                .filter(neighbor -> (neighbor.edgeBehaviorAction() == EdgeBehaviorAction.VALID)
                                        || (neighbor.edgeBehaviorAction() == EdgeBehaviorAction.WRAPPED));
    }

    private static void addMoveCandidateIfFreeWithSugar(List<AgentMoveCandidate> moveCandidates,
                                                        CellNeighborWithEdgeBehavior target,
                                                        CellNeighborWithEdgeBehavior moveTo,
                                                        int distanceToTarget,
                                                        ReadableGridModel<AgentEntity> agentModel,
                                                        ReadableGridModel<ResourceEntity> resourceModel) {
        GridCoordinate targetCoordinate = target.mappedNeighborCoordinate();
        if (agentModel.isDefaultEntity(targetCoordinate)) {
            int sugarAmount = sugarAmountAtCoordinate(targetCoordinate, resourceModel);
            if (sugarAmount > 0) {
                moveCandidates.add(new AgentMoveCandidate(target, moveTo, sugarAmount, distanceToTarget));
            }
        }
    }

    private static int sugarAmountAtCoordinate(GridCoordinate coordinate,
                                               ReadableGridModel<ResourceEntity> resourceModel) {
        if (resourceModel.getEntity(coordinate) instanceof Sugar sugar) {
            return sugar.currentAmount();
        }
        return 0;
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

    private static Optional<GridCoordinate> findRandomFreeCell(GridStructure structure,
                                                               Random random,
                                                               ReadableGridModel<AgentEntity> agentModel) {
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

    private record AgentMoveCandidate(CellNeighborWithEdgeBehavior target,
                                      CellNeighborWithEdgeBehavior moveTo,
                                      int targetSugarAmount,
                                      int distanceToTarget) {
    }

}
