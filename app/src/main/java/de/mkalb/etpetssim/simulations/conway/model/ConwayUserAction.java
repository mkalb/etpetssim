package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import org.jspecify.annotations.Nullable;

public final class ConwayUserAction
        implements SimulationUserAction<
        ConwayEntity,
        WritableGridModel<ConwayEntity>,
        ConwayConfig,
        ConwayStatistics,
        ConwaySimulationManager,
        NoUserActionContext> {

    public ConwayUserAction() {
    }

    @Override
    public void apply(ConwaySimulationManager manager,
                      NoUserActionContext context,
                      @Nullable GridCellView<ConwayEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        WritableGridModel<ConwayEntity> model = manager.currentModel();
        ConwayStatistics statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        ConwayEntity entity = model.getEntity(coordinate);

        ConwayEntity newEntity = switch (entity) {
            case DEAD -> ConwayEntity.ALIVE;
            case ALIVE -> ConwayEntity.DEAD;
        };

        model.setEntity(coordinate, newEntity);

        statistics.adjustCellCounts(entity.isDead() ? 1 : -1, 1);
    }

}

