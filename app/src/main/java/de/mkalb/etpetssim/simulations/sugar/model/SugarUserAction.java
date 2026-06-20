package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import de.mkalb.etpetssim.simulations.sugar.shared.*;
import org.jspecify.annotations.Nullable;

public final class SugarUserAction
        implements SimulationUserAction<
        SugarEntity,
        SugarGridModel,
        SugarConfig,
        SugarStatistics,
        SugarSimulationManager,
        SugarUserActionContext> {

    @Override
    public void apply(SugarSimulationManager manager,
                      SugarUserActionContext context,
                      @Nullable GridCellView<SugarEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var model = manager.currentModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();

        switch (context) {
            case SugarUserActionContext.FixedAction fixedAction ->
                    applyFixedAction(model, statistics, coordinate, fixedAction);
            case SugarUserActionContext.AddSugar addSugar ->
                    applyAddSugar(manager, model, statistics, coordinate, addSugar.level());
        }
    }

    private void applyFixedAction(SugarGridModel model,
                                  SugarStatistics statistics,
                                  GridCoordinate coordinate,
                                  SugarUserActionContext.FixedAction fixedAction) {
        switch (fixedAction) {
            case REMOVE_SUGAR -> {
                ResourceEntity resourceEntity = model.resourceModel().getEntity(coordinate);
                if (resourceEntity.isNotEmpty()) {
                    model.resourceModel().setEntity(coordinate, NoResource.NO_RESOURCE);
                    statistics.adjustResourceCells(-1);
                }
            }
        }
    }

    private void applyAddSugar(SugarSimulationManager manager,
                               SugarGridModel model,
                               SugarStatistics statistics,
                               GridCoordinate coordinate,
                               SugarAddSugarLevel addSugarLevel) {
        int maxSugarAmount = manager.config().maxSugarAmount();
        int sugarAmount = addSugarLevel.resolveSugarAmount(maxSugarAmount);
        ResourceEntity currentResourceEntity = model.resourceModel().getEntity(coordinate);
        model.resourceModel().setEntity(coordinate, new Sugar(maxSugarAmount, sugarAmount));
        if (currentResourceEntity.isEmpty()) {
            statistics.adjustResourceCells(1);
        }
    }

}
