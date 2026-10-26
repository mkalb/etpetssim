package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

final class SugarLogicTestSupport {

    static final int INITIAL_ENERGY = 10;
    static final int METABOLISM_RATE = 2;
    static final int MAX_AGE = 50;

    private SugarLogicTestSupport() {
    }

    static SugarConfig createConfig(int agentVisionRange, int sugarRegenerationRate) {
        return new SugarConfig(
                CellShape.SQUARE,
                GridEdgeBehavior.BLOCK_XY,
                8,
                8,
                SugarConstraints.CELL_EDGE_LENGTH_DEFAULT,
                SugarConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                0.0d,
                1,
                0,
                SugarConstraints.MIN_SUGAR_AMOUNT_DEFAULT,
                SugarConstraints.MAX_SUGAR_AMOUNT_DEFAULT,
                INITIAL_ENERGY,
                NeighborhoodMode.EDGES_ONLY,
                sugarRegenerationRate,
                METABOLISM_RATE,
                agentVisionRange,
                MAX_AGE
        );
    }

    static SugarGridModel createGridModel(SugarConfig config) {
        GridStructure structure = config.createGridStructure();
        return new SugarGridModel(structure,
                new SparseGridModel<>(structure, NoResource.NO_RESOURCE),
                new SparseGridModel<>(structure, NoAgent.NO_AGENT));
    }

}
