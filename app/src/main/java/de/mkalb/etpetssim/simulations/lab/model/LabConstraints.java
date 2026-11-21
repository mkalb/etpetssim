package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabBaseLayer;
import de.mkalb.etpetssim.simulations.lab.shared.LabCellText;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.simulations.lab.shared.LabDistribution;
import de.mkalb.etpetssim.simulations.lab.shared.LabGridModelType;
import de.mkalb.etpetssim.simulations.lab.shared.LabPattern;

import java.util.List;

/**
 * Defaults, limits, and allowed values of the Simulation Lab configuration.
 */
public final class LabConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.HEXAGON;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(CellShape.values());
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.WRAP_XY;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(GridEdgeBehavior.values());
    public static final int GRID_WIDTH_DEFAULT = 16;
    public static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    public static final int GRID_WIDTH_MAX = GridSize.MAX_SIZE;
    public static final int GRID_WIDTH_STEP = 1;
    public static final int GRID_HEIGHT_DEFAULT = 8;
    public static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    public static final int GRID_HEIGHT_MAX = GridSize.MAX_SIZE;
    public static final int GRID_HEIGHT_STEP = 1;
    public static final LabGridModelType GRID_MODEL_TYPE_DEFAULT = LabGridModelType.SPARSE;
    public static final List<LabGridModelType> GRID_MODEL_TYPE_VALUES = List.of(LabGridModelType.values());

    // Layout
    public static final int CELL_EDGE_LENGTH_DEFAULT = 40;
    public static final int CELL_EDGE_LENGTH_MIN = 1;
    public static final int CELL_EDGE_LENGTH_MAX = 1_000;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE_BORDERED;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE,
            CellDisplayMode.SHAPE_BORDERED,
            CellDisplayMode.CIRCLE,
            CellDisplayMode.CIRCLE_BORDERED);
    public static final LabColorMode COLOR_MODE_DEFAULT = LabColorMode.COLOR;
    public static final List<LabColorMode> COLOR_MODE_VALUES = List.of(LabColorMode.values());
    public static final LabBaseLayer BASE_LAYER_DEFAULT = LabBaseLayer.CHECKERBOARD;
    public static final List<LabBaseLayer> BASE_LAYER_VALUES = List.of(LabBaseLayer.values());
    public static final LabCellText CELL_TEXT_DEFAULT = LabCellText.SHOWN;
    public static final List<LabCellText> CELL_TEXT_VALUES = List.of(LabCellText.values());

    // Initialization
    public static final String SEED_DEFAULT = "";
    public static final LabDistribution DISTRIBUTION_DEFAULT = LabDistribution.RANDOM_PERCENT;
    public static final List<LabDistribution> DISTRIBUTION_VALUES = List.of(LabDistribution.values());
    public static final double HIGHLIGHTED_PERCENT_DEFAULT = 0.1d;
    public static final double HIGHLIGHTED_PERCENT_MIN = 0.0d;
    public static final double HIGHLIGHTED_PERCENT_MAX = 1.0d;
    public static final LabPattern PATTERN_DEFAULT = LabPattern.CROSS;
    public static final List<LabPattern> PATTERN_VALUES = List.of(LabPattern.values());

    // Rules
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_AND_VERTICES;

    private LabConstraints() {
    }

}
