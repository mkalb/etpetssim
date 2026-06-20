package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import de.mkalb.etpetssim.simulations.etpets.shared.*;
import org.jspecify.annotations.Nullable;

public final class EtpetsUserAction
        implements SimulationUserAction<
        EtpetsEntity,
        EtpetsGridModel,
        EtpetsConfig,
        EtpetsStatistics,
        EtpetsSimulationManager,
        EtpetsUserActionContext> {

    @Override
    public void apply(EtpetsSimulationManager manager,
                      EtpetsUserActionContext context,
                      @Nullable GridCellView<EtpetsEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var model = manager.currentModel();
        GridCoordinate coordinate = selectedCell.coordinate();

        switch (context) {
            case EtpetsUserActionContext.SetTerrain setTerrain ->
                    applySetTerrain(model, coordinate, setTerrain.terrainChoice());
            case EtpetsUserActionContext.SetResource setResource ->
                    applySetResource(model, coordinate, setResource.resourceChoice());
        }
    }

    private void applySetTerrain(EtpetsGridModel model,
                                 GridCoordinate coordinate,
                                 EtpetsTerrainChoice terrainChoice) {
        if (model.resourceModel().getEntity(coordinate).isNotEmpty()
                || model.agentModel().getEntity(coordinate).isNotEmpty()) {
            return;
        }
        model.terrainModel().setEntity(coordinate, toTerrainEntity(terrainChoice));
    }

    private void applySetResource(EtpetsGridModel model,
                                  GridCoordinate coordinate,
                                  EtpetsResourceChoice resourceChoice) {
        if ((model.terrainModel().getEntity(coordinate) != TerrainConstant.GROUND)
                || model.agentModel().getEntity(coordinate).isNotEmpty()) {
            return;
        }
        model.resourceModel().setEntity(coordinate, toResourceEntity(resourceChoice));
    }

    private TerrainEntity toTerrainEntity(EtpetsTerrainChoice terrainChoice) {
        return switch (terrainChoice) {
            case GROUND -> TerrainConstant.GROUND;
            case ROCK -> TerrainConstant.ROCK;
            case WATER -> TerrainConstant.WATER;
        };
    }

    private ResourceEntity toResourceEntity(EtpetsResourceChoice resourceChoice) {
        return switch (resourceChoice) {
            case NONE -> NoResource.NO_RESOURCE;
            case PLANT -> new Plant(
                    EtpetsBalance.PLANT_MAX_AMOUNT_RANGE_MAX,
                    EtpetsBalance.PLANT_MAX_AMOUNT_RANGE_MAX,
                    EtpetsBalance.PLANT_REGENERATION_PER_STEP_BASE);
            case INSECT -> new Insect(
                    EtpetsBalance.INSECT_MAX_AMOUNT_RANGE_MAX,
                    EtpetsBalance.INSECT_MAX_AMOUNT_RANGE_MAX,
                    EtpetsBalance.INSECT_REGENERATION_PER_STEP_BASE);
        };
    }

}
