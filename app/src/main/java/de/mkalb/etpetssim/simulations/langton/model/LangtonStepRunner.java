package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;

public final class LangtonStepRunner
        implements SimulationStepRunner<LangtonStatistics> {

    private final GridStructure structure;
    private final LangtonConfig config;
    private final LangtonGridModel model;

    public LangtonStepRunner(LangtonConfig config,
                             LangtonGridModel model) {
        this.config = config;
        this.model = model;
        structure = model.structure();
    }

    public LangtonGridModel model() {
        return model;
    }

    @Override
    public void performStep(int stepIndex, LangtonStatistics statistics) {
        var antModel = model.antModel();
        var groundModel = model.groundModel();

        for (GridCell<LangtonAntEntity> agentCell : antModel.filteredAndSortedCells(LangtonEntity::isAgent, AgentOrderingStrategies.byPosition())) {
            if (!(agentCell.entity() instanceof LangtonAnt ant)) {
                continue;
            }

            var neighborOpt = CellNeighborhoods.cellNeighborWithEdgeBehavior(
                    agentCell.coordinate(),
                    config.neighborhoodMode(),
                    ant.direction(),
                    structure);

            if (neighborOpt.isEmpty() || isEdgeAbsorbedOrBlocked(neighborOpt.get())) {
                removeAnt(agentCell, antModel, statistics);
                continue;
            }

            var newCoordinate = neighborOpt.get().mappedNeighborCoordinate();
            var groundEntity = determineGround(newCoordinate, statistics);
            moveAnt(agentCell, newCoordinate, antModel, ant, groundEntity);
            switchGround(newCoordinate, groundEntity, groundModel);
        }
    }

    boolean isEdgeAbsorbedOrBlocked(CellNeighborWithEdgeBehavior neighbor) {
        return (neighbor.edgeBehaviorAction() == EdgeBehaviorAction.ABSORBED)
                || (neighbor.edgeBehaviorAction() == EdgeBehaviorAction.BLOCKED);
    }

    void removeAnt(GridCell<LangtonAntEntity> agentCell, WritableGridModel<LangtonAntEntity> antModel, LangtonStatistics statistics) {
        antModel.setEntityToDefault(agentCell.coordinate());
        statistics.updateCells(-1, 0);
    }

    void moveAnt(GridCell<LangtonAntEntity> agentCell, GridCoordinate newCoordinate, WritableGridModel<LangtonAntEntity> antModel, LangtonAnt ant, LangtonGroundEntity groundEntity) {
        antModel.setEntityToDefault(agentCell.coordinate());
        antModel.setEntity(newCoordinate, agentCell.entity());
        ant.changeDirection(computeNewAntDirection(ant.direction(), groundEntity.ruleIndex()));
    }

    void switchGround(GridCoordinate coordinate, LangtonGroundEntity groundEntity, WritableGridModel<LangtonGroundEntity> groundModel) {
        int newRuleIndex = (groundEntity.ruleIndex() + 1) % config.langtonMovementRules().getRuleCount();
        groundModel.setEntity(coordinate, LangtonGroundEntity.byRuleIndex(newRuleIndex));
    }

    LangtonGroundEntity determineGround(GridCoordinate newCoordinate, LangtonStatistics statistics) {
        var groundModel = model.groundModel();
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
            case SQUARE -> newDirection = switch (turn) {
                case LEFT -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.W;
                    case CompassDirection.E -> CompassDirection.N;
                    case CompassDirection.S -> CompassDirection.E;
                    case CompassDirection.W -> CompassDirection.S;
                    default -> null;
                };
                case RIGHT -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.E;
                    case CompassDirection.E -> CompassDirection.S;
                    case CompassDirection.S -> CompassDirection.W;
                    case CompassDirection.W -> CompassDirection.N;
                    default -> null;
                };
                case LEFT2, RIGHT2, U_TURN -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.S;
                    case CompassDirection.E -> CompassDirection.W;
                    case CompassDirection.S -> CompassDirection.N;
                    case CompassDirection.W -> CompassDirection.E;
                    default -> null;
                };
                case NONE -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.N;
                    case CompassDirection.E -> CompassDirection.E;
                    case CompassDirection.S -> CompassDirection.S;
                    case CompassDirection.W -> CompassDirection.W;
                    default -> null;
                };
            };
            case HEXAGON -> newDirection = switch (turn) {
                case LEFT -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.NW;
                    case CompassDirection.NE -> CompassDirection.N;
                    case CompassDirection.SE -> CompassDirection.NE;
                    case CompassDirection.S -> CompassDirection.SE;
                    case CompassDirection.SW -> CompassDirection.S;
                    case CompassDirection.NW -> CompassDirection.SW;
                    default -> null;
                };
                case RIGHT -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.NE;
                    case CompassDirection.NE -> CompassDirection.SE;
                    case CompassDirection.SE -> CompassDirection.S;
                    case CompassDirection.S -> CompassDirection.SW;
                    case CompassDirection.SW -> CompassDirection.NW;
                    case CompassDirection.NW -> CompassDirection.N;
                    default -> null;
                };
                case LEFT2 -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.SW;
                    case CompassDirection.NE -> CompassDirection.NW;
                    case CompassDirection.SE -> CompassDirection.N;
                    case CompassDirection.S -> CompassDirection.NE;
                    case CompassDirection.SW -> CompassDirection.SE;
                    case CompassDirection.NW -> CompassDirection.S;
                    default -> null;
                };
                case RIGHT2 -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.SE;
                    case CompassDirection.NE -> CompassDirection.S;
                    case CompassDirection.SE -> CompassDirection.SW;
                    case CompassDirection.S -> CompassDirection.NW;
                    case CompassDirection.SW -> CompassDirection.N;
                    case CompassDirection.NW -> CompassDirection.NE;
                    default -> null;
                };
                case NONE -> currentDirection;
                case U_TURN -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.S;
                    case CompassDirection.NE -> CompassDirection.SW;
                    case CompassDirection.SE -> CompassDirection.NW;
                    case CompassDirection.S -> CompassDirection.N;
                    case CompassDirection.SW -> CompassDirection.NE;
                    case CompassDirection.NW -> CompassDirection.SE;
                    default -> null;
                };
            };
            case TRIANGLE -> newDirection = switch (turn) {
                case LEFT -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.NW;
                    case CompassDirection.NE -> CompassDirection.N;
                    case CompassDirection.SE -> CompassDirection.NE;
                    case CompassDirection.S -> CompassDirection.SE;
                    case CompassDirection.SW -> CompassDirection.S;
                    case CompassDirection.NW -> CompassDirection.SW;
                    default -> null;
                };
                case RIGHT -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.NE;
                    case CompassDirection.NE -> CompassDirection.SE;
                    case CompassDirection.SE -> CompassDirection.S;
                    case CompassDirection.S -> CompassDirection.SW;
                    case CompassDirection.SW -> CompassDirection.NW;
                    case CompassDirection.NW -> CompassDirection.N;
                    default -> null;
                };
                case U_TURN -> switch (currentDirection) {
                    case CompassDirection.N -> CompassDirection.S;
                    case CompassDirection.NE -> CompassDirection.SW;
                    case CompassDirection.SE -> CompassDirection.NW;
                    case CompassDirection.S -> CompassDirection.N;
                    case CompassDirection.SW -> CompassDirection.NE;
                    case CompassDirection.NW -> CompassDirection.SE;
                    default -> null;
                };
                default -> null;
            };
        }

        if (newDirection == null) {
            throw new IllegalStateException("Could not compute new direction for currentDirection=" + currentDirection + " and turn=" + turn);
        }
        return newDirection;
    }

}
