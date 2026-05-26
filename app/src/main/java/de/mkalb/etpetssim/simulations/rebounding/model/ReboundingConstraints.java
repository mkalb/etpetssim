package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;

import java.util.*;

/**
 * Shared rebounding configuration constraints and defaults used by model and view-model.
 */
public final class ReboundingConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.HEXAGON;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CellShape.SQUARE,
            CellShape.HEXAGON);
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.BLOCK_XY;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(
            GridEdgeBehavior.BLOCK_XY);
    public static final int GRID_WIDTH_DEFAULT = 70;
    public static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    public static final int GRID_WIDTH_MAX = 1_000;
    public static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    public static final int GRID_HEIGHT_DEFAULT = 30;
    public static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    public static final int GRID_HEIGHT_MAX = 1_000;
    public static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;

    // Layout
    public static final int CELL_EDGE_LENGTH_DEFAULT = 8;
    public static final int CELL_EDGE_LENGTH_MIN = 1;
    public static final int CELL_EDGE_LENGTH_MAX = 50;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE,
            CellDisplayMode.SHAPE_BORDERED);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Initialization
    public static final int VERTICAL_WALLS_DEFAULT = 2;
    public static final int VERTICAL_WALLS_MIN = 0;
    public static final int VERTICAL_WALLS_MAX = 100;
    public static final int VERTICAL_WALLS_STEP = 1;
    public static final double MOVING_ENTITY_PERCENT_DEFAULT = 0.02d;
    public static final double MOVING_ENTITY_PERCENT_MIN = 0.0d;
    public static final double MOVING_ENTITY_PERCENT_MAX = 0.1d;

    // Rules - NeighborhoodMode
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_AND_VERTICES;
    public static final List<NeighborhoodMode> NEIGHBORHOOD_MODE_VALUES = List.of(
            NeighborhoodMode.values());

    /**
     * Private constructor to prevent instantiation.
     */
    private ReboundingConstraints() {
    }

}
