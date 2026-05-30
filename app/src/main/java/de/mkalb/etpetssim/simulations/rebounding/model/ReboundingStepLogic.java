package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.AgentStepLogic;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.TerrainConstant;

public final class ReboundingStepLogic implements AgentStepLogic<ReboundingEntity, ReboundingStatistics> {

    private final GridStructure structure;
    private final ReboundingConfig config;

    public ReboundingStepLogic(GridStructure structure, ReboundingConfig config) {
        this.structure = structure;
        this.config = config;
    }

    @Override
    public void performAgentStep(GridCell<ReboundingEntity> agentCell,
                                 WritableGridModel<ReboundingEntity> model,
                                 int stepIndex,
                                 ReboundingStatistics statistics) {
        if (!(agentCell.entity() instanceof Rebounder movingEntity)) {
            throw new IllegalArgumentException("Provided cell does not contain a Rebounder entity. Cell: " + agentCell);
        }
        GridCoordinate currentCoordinate = agentCell.coordinate();

        // Check if the entity is still at its original coordinate. It may already have been removed.
        if (model.getEntity(currentCoordinate) != movingEntity) {
            return;
        }

        // Find neighbor cell in the direction of movement
        var neighbor = CellNeighborhoods.cellNeighborWithEdgeBehavior(
                currentCoordinate,
                config.neighborhoodMode(),
                movingEntity.getDirection(),
                structure
        ).orElseThrow(() -> new IllegalStateException("Unexpected empty neighbor. This should not happen. Cell: " + agentCell));

        // Check edge behavior and neighbor cell entity to determine the action for the moving entity
        if ((neighbor.edgeBehaviorAction() == EdgeBehaviorAction.BLOCKED)) {
            // Change direction at blocked grid boundary
            movingEntity.setDirection(computeBounceDirection(
                    movingEntity.getDirection(),
                    neighbor.mappedNeighborCoordinate()));
        } else { // VALID
            var neighborEntity = model.getEntity(neighbor.mappedNeighborCoordinate());
            if (neighborEntity == TerrainConstant.GROUND) {
                // Move the entity to the new empty cell
                model.setEntity(neighbor.mappedNeighborCoordinate(), movingEntity);
                model.setEntityToDefault(currentCoordinate);
            } else if (neighborEntity == TerrainConstant.WALL) {
                // Change direction at WALL and remove WALL (set to GROUND)
                movingEntity.setDirection(computeBounceDirection(
                        movingEntity.getDirection(),
                        neighbor.mappedNeighborCoordinate()));
                model.setEntityToDefault(neighbor.mappedNeighborCoordinate());

                statistics.decreaseWallCells();
            } else if (neighborEntity.isRebounder()) {
                // Move into the neighbor cell, destroying the other rebounder
                model.setEntity(neighbor.mappedNeighborCoordinate(), movingEntity);
                model.setEntityToDefault(currentCoordinate);

                statistics.decreaseMovingEntityCells();
            }
        }
    }

    private CompassDirection computeBounceDirection(CompassDirection currentDirection, GridCoordinate blockedCoordinate) {
        if (currentDirection.level() == 0) {
            return currentDirection.opposite();
        } else if (currentDirection.level() == 1) {
            if (blockedCoordinate.isXWithinAndYOutOfOriginBounds(structure.maxCoordinateExclusive())) {
                return switch (currentDirection) {
                    case NE -> CompassDirection.SE;
                    case SE -> CompassDirection.NE;
                    case SW -> CompassDirection.NW;
                    case NW -> CompassDirection.SW;
                    default -> throw new IllegalStateException("Unexpected direction: " + currentDirection);
                };
            } else if (blockedCoordinate.isYWithinAndXOutOfOriginBounds(structure.maxCoordinateExclusive())) {
                return switch (currentDirection) {
                    case NE -> CompassDirection.NW;
                    case SE -> CompassDirection.SW;
                    case SW -> CompassDirection.SE;
                    case NW -> CompassDirection.NE;
                    default -> throw new IllegalStateException("Unexpected direction: " + currentDirection);
                };
            } else {
                return switch (currentDirection) {
                    case NE -> CompassDirection.SW;
                    case SE -> CompassDirection.NW;
                    case SW -> CompassDirection.NE;
                    case NW -> CompassDirection.SE;
                    default -> throw new IllegalStateException("Unexpected direction: " + currentDirection);
                };
            }
        }

        throw new IllegalStateException("Unhandled direction level: " + currentDirection.level() + " for direction: " + currentDirection);
    }

}
