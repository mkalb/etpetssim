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

        // Only place a creature on a water cell.
        if (!entity.isWater()) {
            return;
        }

        int stepIndexOfBirth = manager.stepCount() - 1;
        WatorEntity newEntity = switch (context) {
            case ADD_FISH -> manager.createFish(stepIndexOfBirth);
            case ADD_SHARK -> manager.createShark(stepIndexOfBirth);
        };

        model.setEntity(coordinate, newEntity);
        statistics.adjustCellCounts(
                newEntity.isFish() ? 1 : 0,
                newEntity.isShark() ? 1 : 0
        );
    }

}
