package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import java.util.*;

/**
 * Shared ET Pets configuration constraints and defaults used by model and view-model.
 */
public final class EtpetsConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.HEXAGON;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CELL_SHAPE_DEFAULT);
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.BLOCK_XY;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(
            GRID_EDGE_BEHAVIOR_DEFAULT);
    public static final int GRID_WIDTH_DEFAULT = 50;
    public static final int GRID_WIDTH_MIN = 20;
    public static final int GRID_WIDTH_MAX = 200;
    public static final int GRID_WIDTH_STEP = GridTopology.HEXAGON_MAX_REQUIRED_WIDTH_MULTIPLE;
    public static final int GRID_HEIGHT_DEFAULT = 20;
    public static final int GRID_HEIGHT_MIN = 20;
    public static final int GRID_HEIGHT_MAX = 200;
    public static final int GRID_HEIGHT_STEP = GridTopology.HEXAGON_MAX_REQUIRED_HEIGHT_MULTIPLE;

    // Layout
    public static final int CELL_EDGE_LENGTH_DEFAULT = 10;
    public static final int CELL_EDGE_LENGTH_MIN = 5;
    public static final int CELL_EDGE_LENGTH_MAX = 50;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CELL_DISPLAY_MODE_DEFAULT);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Initialization
    public static final double ROCK_PERCENT_DEFAULT = 0.01d;
    public static final double WATER_PERCENT_DEFAULT = 0.02d;
    public static final double PLANT_PERCENT_DEFAULT = 0.05d;
    public static final double INSECT_PERCENT_DEFAULT = 0.01d;
    public static final double PERCENT_MIN = 0.0d;
    public static final double PERCENT_MAX = 0.5d;
    public static final double TOTAL_PERCENT_MAX = 0.5d;
    public static final int PET_COUNT_DEFAULT = 10;
    public static final int PET_COUNT_MIN = 0;
    public static final int PET_COUNT_MAX = 100;
    public static final int PET_COUNT_STEP = 1;

    // Rules - NeighborhoodMode
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_ONLY;

    /**
     * Private constructor to prevent instantiation.
     */
    private EtpetsConstraints() {
    }

}

