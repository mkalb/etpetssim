package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgentEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceEntity;

import java.util.*;

public record SugarGridModel(
        GridStructure structure,
        WritableGridModel<SugarResourceEntity> resourceModel,
        WritableGridModel<SugarAgentEntity> agentModel)
        implements CompositeGridModel<SugarEntity> {

    public SugarGridModel {
        if (!structure.equals(resourceModel.structure()) || !structure.equals(agentModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    @Override
    public List<SugarEntity> getEntities(GridCoordinate coordinate) {
        return List.of(resourceModel.getEntity(coordinate), agentModel.getEntity(coordinate));
    }

    @Override
    public int subModelCount() {
        return 2;
    }

}
