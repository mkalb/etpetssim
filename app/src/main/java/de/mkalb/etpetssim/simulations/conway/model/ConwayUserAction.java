package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import org.jspecify.annotations.Nullable;

public final class ConwayUserAction
        implements SimulationUserAction<
        ConwayEntity,
        WritableGridModel<ConwayEntity>,
        ConwayConfig,
        ConwayStatistics,
        ConwaySimulationManager,
        ConwayUserActionContext> {

    public ConwayUserAction() {
    }

    @Override
    public void apply(ConwaySimulationManager manager,
                      ConwayUserActionContext context,
                      @Nullable GridCellView<ConwayEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var model = manager.currentModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        var entity = model.getEntity(coordinate);

        switch (context) {
            case ConwayUserActionContext.FixedAction fixedAction ->
                    applyFixedAction(model, statistics, coordinate, entity, fixedAction);
            case ConwayUserActionContext.PlacePattern placePattern -> {
                if (!placePattern.patternChoice().availableFor(manager.config())) {
                    return;
                }
                applyPattern(model, statistics, coordinate, placePattern.patternChoice().pattern());
            }
        }
    }

    private void applyFixedAction(WritableGridModel<ConwayEntity> model,
                                  ConwayStatistics statistics,
                                  GridCoordinate coordinate,
                                  ConwayEntity entity,
                                  ConwayUserActionContext.FixedAction fixedAction) {
        if (fixedAction != ConwayUserActionContext.FixedAction.TOGGLE_CELL) {
            return;
        }

        ConwayEntity newEntity = entity.isDead() ? ConwayEntity.ALIVE : ConwayEntity.DEAD;
        model.setEntity(coordinate, newEntity);
        statistics.adjustCellCounts(entity.isDead() ? 1 : -1, 1);
    }

    private void applyPattern(WritableGridModel<ConwayEntity> model,
                              ConwayStatistics statistics,
                              GridCoordinate anchorCoordinate,
                              GridPattern<ConwayEntity> pattern) {
        var offsetMap = pattern.offsetMap();
        if (!canPlacePatternAt(model, anchorCoordinate, pattern)) {
            return;
        }

        int aliveDelta = 0;
        int changedCellsDelta = 0;
        for (var entry : offsetMap.entrySet()) {
            GridCoordinate targetCoordinate = anchorCoordinate.offset(entry.getKey());
            ConwayEntity oldEntity = model.getEntity(targetCoordinate);
            ConwayEntity newEntity = entry.getValue();
            if (oldEntity != newEntity) {
                aliveDelta += newEntity.isAlive() ? 1 : -1;
                changedCellsDelta += 1;
            }
        }

        for (var entry : offsetMap.entrySet()) {
            model.setEntity(anchorCoordinate.offset(entry.getKey()), entry.getValue());
        }

        if ((aliveDelta != 0) || (changedCellsDelta != 0)) {
            statistics.adjustCellCounts(aliveDelta, changedCellsDelta);
        }
    }

    private boolean canPlacePatternAt(WritableGridModel<ConwayEntity> model,
                                      GridCoordinate anchorCoordinate,
                                      GridPattern<ConwayEntity> pattern) {
        for (var offset : pattern.offsetMap().keySet()) {
            if (!model.isCoordinateValid(anchorCoordinate.offset(offset))) {
                return false;
            }
        }
        return true;
    }

}

