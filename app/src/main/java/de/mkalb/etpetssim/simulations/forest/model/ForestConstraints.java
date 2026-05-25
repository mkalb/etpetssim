package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;

import java.util.*;

/**
 * Shared forest configuration constraints and defaults used by model and view-model.
 */
public final class ForestConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.HEXAGON;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CellShape.values());
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.BLOCK_XY;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(
            GridEdgeBehavior.BLOCK_XY,
            GridEdgeBehavior.WRAP_XY);
    public static final int GRID_WIDTH_DEFAULT = 200;
    public static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    public static final int GRID_WIDTH_MAX = 1_000;
    public static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    public static final int GRID_HEIGHT_DEFAULT = 100;
    public static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    public static final int GRID_HEIGHT_MAX = 1_000;
    public static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;

    // Layout
    public static final int CELL_EDGE_LENGTH_DEFAULT = 2;
    public static final int CELL_EDGE_LENGTH_MIN = 1;
    public static final int CELL_EDGE_LENGTH_MAX = 50;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.CIRCLE_BORDERED;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE,
            CellDisplayMode.SHAPE_BORDERED,
            CellDisplayMode.CIRCLE,
            CellDisplayMode.CIRCLE_BORDERED,
            CellDisplayMode.EMOJI);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Initialization
    public static final double TREE_DENSITY_DEFAULT = 0.2d;
    public static final double TREE_DENSITY_MIN = 0.0d;
    public static final double TREE_DENSITY_MAX = 1.0d;

    // Rules
    public static final double TREE_GROWTH_PROBABILITY_DEFAULT = 0.002d;
    public static final double TREE_GROWTH_PROBABILITY_MIN = 0.00d;
    public static final double TREE_GROWTH_PROBABILITY_MAX = 0.20d;
    public static final double LIGHTNING_IGNITION_PROBABILITY_DEFAULT = 0.001d;
    public static final double LIGHTNING_IGNITION_PROBABILITY_MIN = 0.00d;
    public static final double LIGHTNING_IGNITION_PROBABILITY_MAX = 0.02d;
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_ONLY;
    public static final List<NeighborhoodMode> NEIGHBORHOOD_MODE_VALUES = List.of(
            NeighborhoodMode.values());

    /**
     * Private constructor to prevent instantiation.
     */
    private ForestConstraints() {
    }

}
