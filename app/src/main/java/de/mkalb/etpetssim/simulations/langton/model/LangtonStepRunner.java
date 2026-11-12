package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.SimulationStepRunner;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.engine.support.AgentOrderingStrategies;
import de.mkalb.etpetssim.simulations.langton.model.entity.Ant;
import de.mkalb.etpetssim.simulations.langton.model.entity.AntEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant;

import java.util.List;

/**
 * Step runner of Langton's Ant that moves the ants and changes the color of the cells they enter.
 */
public final class LangtonStepRunner implements SimulationStepRunner<LangtonStatistics> {

    private final LangtonGridModel model;
    private final LangtonConfig config;
    private final GridStructure structure;
    private final List<CompassDirection> directionRing;

    public LangtonStepRunner(LangtonGridModel model, LangtonConfig config) {
        this.model = model;
        this.config = config;
        structure = model.structure();
        // Triangle cells turn on the hexagon ring; LangtonConfig.isValid() rejects the turns they do not support.
        directionRing = (structure.cellShape() == CellShape.SQUARE)
                ? CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING
                : CellNeighborhoods.HEXAGON_DIRECTION_RING;
    }

    public LangtonGridModel model() {
        return model;
    }

    @Override
    public void performStep(int stepIndex, LangtonStatistics statistics) {
        var antModel = model.antModel();
        var terrainModel = model.terrainModel();

        for (GridCell<AntEntity> agentCell : antModel.filteredCellsSortedBy(LangtonEntity::isAgent,
                AgentOrderingStrategies.byPosition())) {
            if (!(agentCell.entity() instanceof Ant ant)) {
                continue;
            }

            var neighborOpt = CellNeighborhoods.cellNeighborWithEdgeBehavior(
                    agentCell.coordinate(),
                    config.neighborhoodMode(),
                    structure,
                    ant.direction());

            if (neighborOpt.isEmpty() || !neighborOpt.get().edgeBehaviorAction().isWithinGrid()) {
                removeAnt(agentCell, antModel, statistics);
                continue;
            }

            var newCoordinate = neighborOpt.get().mappedNeighborCoordinate();
            if (!antModel.isDefaultEntity(newCoordinate)) {
                // Another ant is already on the target cell. Remove the current ant from the simulation.
                removeAnt(agentCell, antModel, statistics);
                continue;
            }
            var terrainEntity = resolveTerrainAndCountVisit(newCoordinate, terrainModel, statistics);
            moveAnt(agentCell, newCoordinate, antModel, ant, terrainEntity);
            switchTerrain(newCoordinate, terrainEntity, terrainModel);
        }
    }

    private void removeAnt(GridCell<AntEntity> agentCell,
                           WritableGridModel<AntEntity> antModel,
                           LangtonStatistics statistics) {
        antModel.setEntityToDefault(agentCell.coordinate());
        statistics.adjustCellCounts(-1, 0);
    }

    private void moveAnt(GridCell<AntEntity> agentCell,
                         GridCoordinate newCoordinate,
                         WritableGridModel<AntEntity> antModel,
                         Ant ant,
                         TerrainConstant terrainEntity) {
        antModel.setEntityToDefault(agentCell.coordinate());
        antModel.setEntity(newCoordinate, agentCell.entity());
        ant.changeDirection(computeNewAntDirection(ant.direction(), terrainEntity.ruleIndex()));
    }

    private void switchTerrain(GridCoordinate coordinate,
                               TerrainConstant terrainEntity,
                               WritableGridModel<TerrainConstant> terrainModel) {
        int newRuleIndex = (terrainEntity.ruleIndex() + 1) % config.movementRules().ruleCount();
        terrainModel.setEntity(coordinate, TerrainConstant.requireByRuleIndex(newRuleIndex));
    }

    private TerrainConstant resolveTerrainAndCountVisit(GridCoordinate newCoordinate,
                                                        WritableGridModel<TerrainConstant> terrainModel,
                                                        LangtonStatistics statistics) {
        TerrainConstant terrainEntity = terrainModel.getEntity(newCoordinate);
        if (terrainEntity.isUnvisited()) {
            terrainEntity = TerrainConstant.COLOR_0;
            statistics.adjustCellCounts(0, 1);
        }
        return terrainEntity;
    }

    private CompassDirection computeNewAntDirection(CompassDirection currentDirection, int ruleIndex) {
        int index = directionRing.indexOf(currentDirection);
        if (index < 0) {
            throw new IllegalStateException("Unable to compute ant direction: currentDirection=" + currentDirection
                    + ", cellShape=" + structure.cellShape());
        }
        int steps = switch (config.movementRules().turnForState(ruleIndex)) {
            case LEFT -> -1;
            case RIGHT -> 1;
            case LEFT2 -> -2;
            case RIGHT2 -> 2;
            case NONE -> 0;
            case U_TURN -> directionRing.size() / 2;
        };
        return directionRing.get(Math.floorMod(index + steps, directionRing.size()));
    }

}
