package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;

import java.util.*;

/**
 * Shared Sugarscape configuration constraints and defaults used by model and view-model.
 */
public final class SugarConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.SQUARE;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CellShape.SQUARE,
            CellShape.HEXAGON);
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.WRAP_XY;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(
            GridEdgeBehavior.BLOCK_XY,
            GridEdgeBehavior.WRAP_XY,
            GridEdgeBehavior.BLOCK_X_WRAP_Y,
            GridEdgeBehavior.WRAP_X_BLOCK_Y);
    public static final int GRID_WIDTH_DEFAULT = 50;
    public static final int GRID_WIDTH_MIN = 10;
    public static final int GRID_WIDTH_MAX = 500;
    public static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    public static final int GRID_HEIGHT_DEFAULT = 50;
    public static final int GRID_HEIGHT_MIN = 10;
    public static final int GRID_HEIGHT_MAX = 500;
    public static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;

    // Layout
    public static final int CELL_EDGE_LENGTH_DEFAULT = 8;
    public static final int CELL_EDGE_LENGTH_MIN = 1;
    public static final int CELL_EDGE_LENGTH_MAX = 50;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Initialization
    public static final int RULE_STEP = 1;
    public static final double AGENT_PERCENT_DEFAULT = 0.2d;
    public static final double AGENT_PERCENT_MIN = 0.0d;
    public static final double AGENT_PERCENT_MAX = 1.0d;
    public static final int SUGAR_PEAKS_DEFAULT = 4;
    public static final int SUGAR_PEAKS_MIN = 1;
    public static final int SUGAR_PEAKS_MAX = 5;
    public static final int SUGAR_PEAKS_STEP = RULE_STEP;
    public static final int SUGAR_RADIUS_LIMIT_DEFAULT = 14;
    public static final int SUGAR_RADIUS_LIMIT_MIN = 0;
    public static final int SUGAR_RADIUS_LIMIT_MAX = 100;
    public static final int SUGAR_RADIUS_LIMIT_STEP = RULE_STEP;
    public static final int MIN_SUGAR_AMOUNT_DEFAULT = 1;
    public static final int MAX_SUGAR_AMOUNT_DEFAULT = 8;
    public static final int MAX_SUGAR_AMOUNT_MIN = 1;
    public static final int MAX_SUGAR_AMOUNT_MAX = 20;
    public static final int MAX_SUGAR_AMOUNT_STEP = RULE_STEP;
    public static final int AGENT_INITIAL_ENERGY_DEFAULT = 12;
    public static final int AGENT_INITIAL_ENERGY_MIN = 1;
    public static final int AGENT_INITIAL_ENERGY_MAX = 20;
    public static final int AGENT_INITIAL_ENERGY_STEP = RULE_STEP;

    // Rules
    public static final int SUGAR_REGENERATION_RATE_DEFAULT = 1;
    public static final int SUGAR_REGENERATION_RATE_MIN = 1;
    public static final int SUGAR_REGENERATION_RATE_MAX = 10;
    public static final int SUGAR_REGENERATION_RATE_STEP = RULE_STEP;
    public static final int AGENT_METABOLISM_RATE_DEFAULT = 2;
    public static final int AGENT_METABOLISM_RATE_MIN = 1;
    public static final int AGENT_METABOLISM_RATE_MAX = 10;
    public static final int AGENT_METABOLISM_RATE_STEP = RULE_STEP;
    public static final int AGENT_VISION_RANGE_DEFAULT = 8;
    public static final int AGENT_VISION_RANGE_MIN = 1;
    public static final int AGENT_VISION_RANGE_MAX = 10;
    public static final int AGENT_VISION_RANGE_STEP = RULE_STEP;
    public static final int AGENT_MAX_AGE_DEFAULT = 100;
    public static final int AGENT_MAX_AGE_MIN = 1;
    public static final int AGENT_MAX_AGE_MAX = 1_000;
    public static final int AGENT_MAX_AGE_STEP = RULE_STEP;
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_ONLY;

    /**
     * Private constructor to prevent instantiation.
     */
    private SugarConstraints() {
    }

}
