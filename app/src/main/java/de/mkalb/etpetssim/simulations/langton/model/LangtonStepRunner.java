package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;

import java.util.*;

@SuppressWarnings("ClassCanBeRecord")
public final class LangtonStepRunner
        implements SimulationStepRunner<LangtonStatistics> {

    private final GridStructure structure;
    private final LangtonConfig config;
    private final WritableGridModel<LangtonGroundEntity> groundModel;
    private final WritableGridModel<LangtonAntEntity> antModel;
    private final LayeredCompositeGridModel<LangtonEntity> compositeGridModel;

    public LangtonStepRunner(GridStructure structure,
                             LangtonConfig config,
                             WritableGridModel<LangtonGroundEntity> groundModel,
                             WritableGridModel<LangtonAntEntity> antModel,
                             LayeredCompositeGridModel<LangtonEntity> compositeGridModel) {
        this.structure = structure;
        this.config = config;
        this.groundModel = groundModel;
        this.antModel = antModel;
        this.compositeGridModel = compositeGridModel;
    }

    public LayeredCompositeGridModel<LangtonEntity> compositeGridModel() {
        return compositeGridModel;
    }

    @Override
    public void performStep(int stepIndex, LangtonStatistics statistics) {
        List<GridCell<LangtonAntEntity>> orderedAgentCells = antModel.filteredAndSortedCells(LangtonEntity::isAgent, AgentOrderingStrategies.byPosition());
        for (GridCell<LangtonAntEntity> agentCell : orderedAgentCells) {
            if (agentCell.entity() instanceof LangtonAnt ant) {
                Optional<CellNeighborWithEdgeBehavior> neighbor = CellNeighborhoods.cellNeighborWithEdgeBehavior(agentCell.coordinate(), config.neighborhoodMode(), ant.direction(), structure);
                if (neighbor.isPresent() && (neighbor.get().edgeBehaviorAction() != EdgeBehaviorAction.ABSORBED) && (neighbor.get().edgeBehaviorAction() != EdgeBehaviorAction.BLOCKED)) {
                    GridCoordinate newCoordinate = neighbor.get().mappedNeighborCoordinate();

                    // determine ground
                    LangtonGroundEntity groundEntity = determineGround(newCoordinate, statistics);

                    // move ant
                    antModel.setEntityToDefault(agentCell.coordinate());
                    antModel.setEntity(newCoordinate, agentCell.entity());
                    ant.changeDirection(computeNewAntDirection(ant.direction(), groundEntity.ruleIndex()));

                    // switch ground
                    int newRuleIndex = (groundEntity.ruleIndex() + 1) % config.langtonMovementRules().getColorCount();
                    LangtonGroundEntity newGroundEntity = LangtonGroundEntity.forRuleIndex(newRuleIndex);
                    groundModel.setEntity(newCoordinate, newGroundEntity);
                } else {
                    // remove ant
                    antModel.setEntityToDefault(agentCell.coordinate());
                    statistics.updateCells(-1, 0);
                }
            }
        }
    }

    LangtonGroundEntity determineGround(GridCoordinate newCoordinate, LangtonStatistics statistics) {
        LangtonGroundEntity groundEntity = groundModel.getEntity(newCoordinate);
        if (groundEntity == LangtonGroundEntity.UNVISITED) {
            groundEntity = LangtonGroundEntity.COLOR_0;
            statistics.updateCells(0, 1);
        }
        return groundEntity;
    }

    CompassDirection computeNewAntDirection(CompassDirection currentDirection, int ruleIndex) {
        LangtonMovementRules.AntTurn turn = config.langtonMovementRules().getTurnForState(ruleIndex);

        CompassDirection newDirection = null;
        switch (structure.cellShape()) {
            case SQUARE -> {
                if (turn == LangtonMovementRules.AntTurn.LEFT) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.W;
                        case CompassDirection.E -> CompassDirection.N;
                        case CompassDirection.S -> CompassDirection.E;
                        case CompassDirection.W -> CompassDirection.S;
                        default -> null;
                    };
                } else if (turn == LangtonMovementRules.AntTurn.RIGHT) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.E;
                        case CompassDirection.E -> CompassDirection.S;
                        case CompassDirection.S -> CompassDirection.W;
                        case CompassDirection.W -> CompassDirection.N;
                        default -> null;
                    };
                }
            }
            case HEXAGON -> {
                if (turn == LangtonMovementRules.AntTurn.LEFT) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.NW;
                        case CompassDirection.NE -> CompassDirection.N;
                        case CompassDirection.SE -> CompassDirection.NE;
                        case CompassDirection.S -> CompassDirection.SE;
                        case CompassDirection.SW -> CompassDirection.S;
                        case CompassDirection.NW -> CompassDirection.SW;
                        default -> null;
                    };
                } else if (turn == LangtonMovementRules.AntTurn.RIGHT) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.NE;
                        case CompassDirection.NE -> CompassDirection.SE;
                        case CompassDirection.SE -> CompassDirection.S;
                        case CompassDirection.S -> CompassDirection.SW;
                        case CompassDirection.SW -> CompassDirection.NW;
                        case CompassDirection.NW -> CompassDirection.N;
                        default -> null;
                    };
                } else if (turn == LangtonMovementRules.AntTurn.LEFT2) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.SW;
                        case CompassDirection.NE -> CompassDirection.NW;
                        case CompassDirection.SE -> CompassDirection.N;
                        case CompassDirection.S -> CompassDirection.NE;
                        case CompassDirection.SW -> CompassDirection.SE;
                        case CompassDirection.NW -> CompassDirection.S;
                        default -> null;
                    };
                } else if (turn == LangtonMovementRules.AntTurn.RIGHT2) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.SE;
                        case CompassDirection.NE -> CompassDirection.S;
                        case CompassDirection.SE -> CompassDirection.SW;
                        case CompassDirection.S -> CompassDirection.NW;
                        case CompassDirection.SW -> CompassDirection.N;
                        case CompassDirection.NW -> CompassDirection.NE;
                        default -> null;
                    };
                } else if (turn == LangtonMovementRules.AntTurn.NONE) {
                    newDirection = currentDirection;
                }
            }
            case TRIANGLE -> {
                if (turn == LangtonMovementRules.AntTurn.LEFT) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.NW;
                        case CompassDirection.NE -> CompassDirection.N;
                        case CompassDirection.SE -> CompassDirection.NE;
                        case CompassDirection.S -> CompassDirection.SE;
                        case CompassDirection.SW -> CompassDirection.S;
                        case CompassDirection.NW -> CompassDirection.SW;
                        default -> null;
                    };
                } else if (turn == LangtonMovementRules.AntTurn.RIGHT) {
                    newDirection = switch (currentDirection) {
                        case CompassDirection.N -> CompassDirection.NE;
                        case CompassDirection.NE -> CompassDirection.SE;
                        case CompassDirection.SE -> CompassDirection.S;
                        case CompassDirection.S -> CompassDirection.SW;
                        case CompassDirection.SW -> CompassDirection.NW;
                        case CompassDirection.NW -> CompassDirection.N;
                        default -> null;
                    };
                }
            }
        }

        if (newDirection == null) {
            throw new IllegalStateException("Could not compute new direction for currentDirection=" + currentDirection + " and turn=" + turn);
        }
        return newDirection;
    }

}
