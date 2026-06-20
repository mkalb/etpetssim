package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;
import de.mkalb.etpetssim.simulations.wator.shared.WatorUserActionContext;
import org.jspecify.annotations.Nullable;

public final class WatorUserAction
        implements SimulationUserAction<
        WatorEntity,
        WritableGridModel<WatorEntity>,
        WatorConfig,
        WatorStatistics,
        WatorSimulationManager,
        WatorUserActionContext> {

    public WatorUserAction() {
    }

    @Override
    public void apply(WatorSimulationManager manager,
                      WatorUserActionContext context,
                      @Nullable GridCellView<WatorEntity> selectedCell) {
        if (selectedCell == null) {
            // This user action works only if a cell is selected, so do nothing if no cell is selected.
            return;
        }

        var model = manager.currentModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        var entity = model.getEntity(coordinate);

        switch (context) {
            case ADD_FISH -> {
                if (entity.isWater()) {
                    int stepIndexOfBirth = manager.stepCount() - 1;
                    WatorEntity newEntity = manager.createFish(stepIndexOfBirth);
                    model.setEntity(coordinate, newEntity);
                    statistics.adjustCellCounts(1, 0);
                }
            }
            case ADD_SHARK -> {
                if (entity.isWater()) {
                    int stepIndexOfBirth = manager.stepCount() - 1;
                    WatorEntity newEntity = manager.createShark(stepIndexOfBirth);
                    model.setEntity(coordinate, newEntity);
                    statistics.adjustCellCounts(0, 1);
                }
            }
            case REMOVE_CREATURE -> {
                if (entity.isFish()) {
                    model.setEntityToDefault(coordinate);
                    statistics.adjustCellCounts(-1, 0);
                } else if (entity.isShark()) {
                    model.setEntityToDefault(coordinate);
                    statistics.adjustCellCounts(0, -1);
                }
            }
        }
    }

}
