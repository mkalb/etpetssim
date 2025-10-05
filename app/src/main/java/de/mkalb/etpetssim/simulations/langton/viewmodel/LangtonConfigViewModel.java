package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.model.LangtonRuleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.*;

public final class LangtonConfigViewModel
        extends AbstractConfigViewModel<LangtonConfig> {

    private static final CommonConfigSettings COMMON_SETTINGS = new CommonConfigSettings(
            CellShape.SQUARE,
            GridEdgeBehavior.WRAP_XY,
            List.of(GridEdgeBehavior.WRAP_XY, GridEdgeBehavior.ABSORB_XY),
            100,
            50,
            1_000,
            GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE,
            100,
            50,
            1_000,
            GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE,
            8,
            1,
            32,
            CellDisplayMode.SHAPE,
            List.of(CellDisplayMode.SHAPE, CellDisplayMode.SHAPE_BORDERED),
            ""
    );
    private static final NeighborhoodMode NEIGHBORHOOD_MODE_INITIAL = NeighborhoodMode.EDGES_ONLY;

    private final LangtonRuleProperty rule = new LangtonRuleProperty();

    public LangtonConfigViewModel(ReadOnlyObjectProperty<SimulationState> simulationState) {
        super(simulationState, COMMON_SETTINGS);
    }

    @Override
    public LangtonConfig getConfig() {
        return new LangtonConfig(
                cellShapeProperty().property().getValue(),
                gridEdgeBehaviorProperty().property().getValue(),
                gridWidthProperty().property().getValue(),
                gridHeightProperty().property().getValue(),
                cellEdgeLengthProperty().property().getValue(),
                cellDisplayModeProperty().property().getValue(),
                seedProperty().computeSeedAndUpdateLabel(),
                NEIGHBORHOOD_MODE_INITIAL,
                ruleProperty().computeRuleAndUpdateProperties(cellShapeProperty().property().getValue())
        );
    }

    public LangtonRuleProperty ruleProperty() {
        return rule;
    }

}
