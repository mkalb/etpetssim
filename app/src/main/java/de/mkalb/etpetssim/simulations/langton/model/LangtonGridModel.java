package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAntEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonGroundEntity;

import java.util.*;

public record LangtonGridModel(
        GridStructure structure,
        WritableGridModel<LangtonGroundEntity> groundModel,
        WritableGridModel<LangtonAntEntity> antModel)
        implements CompositeGridModel<LangtonEntity> {

    public LangtonGridModel {
        if (!structure.equals(groundModel.structure()) || !structure.equals(antModel.structure())) {
            throw new IllegalArgumentException("All structures must be the same");
        }
    }

    @Override
    public List<LangtonEntity> getEntities(GridCoordinate coordinate) {
        return List.of(groundModel.getEntity(coordinate), antModel.getEntity(coordinate));
    }

    @Override
    public int subModelCount() {
        return 2;
    }

}
