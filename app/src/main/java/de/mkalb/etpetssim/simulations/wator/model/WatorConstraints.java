package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import java.util.*;

/**
 * Configuration constraints and defaults used across the simulation layers.
 */
public final class WatorConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.SQUARE;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CellShape.values());
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.WRAP_XY;
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
    public static final int CELL_EDGE_LENGTH_DEFAULT = 4;
    public static final int CELL_EDGE_LENGTH_MIN = 1;
    public static final int CELL_EDGE_LENGTH_MAX = 50;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE,
            CellDisplayMode.SHAPE_BORDERED,
            CellDisplayMode.CIRCLE,
            CellDisplayMode.CIRCLE_BORDERED,
            CellDisplayMode.EMOJI);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Initialization
    public static final int RULE_STEP = 1;
    public static final double FISH_PERCENT_DEFAULT = 0.20d;
    public static final double FISH_PERCENT_MIN = 0.0d;
    public static final double FISH_PERCENT_MAX = 1.0d;
    public static final double SHARK_PERCENT_DEFAULT = 0.05d;
    public static final double SHARK_PERCENT_MIN = 0.0d;
    public static final double SHARK_PERCENT_MAX = 1.0d;
    public static final double POPULATION_SHARE_SUM_MAX_INCLUSIVE = 1.0d;

    // Rules - NeighborhoodMode
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_ONLY;

    // Rules
    public static final int FISH_MAX_AGE_DEFAULT = 20;
    public static final int FISH_MAX_AGE_MIN = 1;
    public static final int FISH_MAX_AGE_MAX = 1_000;
    public static final int FISH_MAX_AGE_STEP = RULE_STEP;
    public static final int FISH_MIN_REPRODUCTION_AGE_DEFAULT = 5;
    public static final int FISH_MIN_REPRODUCTION_AGE_MIN = 1;
    public static final int FISH_MIN_REPRODUCTION_AGE_MAX = 1_000;
    public static final int FISH_MIN_REPRODUCTION_AGE_STEP = RULE_STEP;
    public static final int FISH_MIN_REPRODUCTION_INTERVAL_DEFAULT = 3;
    public static final int FISH_MIN_REPRODUCTION_INTERVAL_MIN = 1;
    public static final int FISH_MIN_REPRODUCTION_INTERVAL_MAX = 1_000;
    public static final int FISH_MIN_REPRODUCTION_INTERVAL_STEP = RULE_STEP;
    public static final int SHARK_MAX_AGE_DEFAULT = 40;
    public static final int SHARK_MAX_AGE_MIN = 1;
    public static final int SHARK_MAX_AGE_MAX = 1_000;
    public static final int SHARK_MAX_AGE_STEP = RULE_STEP;
    public static final int SHARK_BIRTH_ENERGY_DEFAULT = 8;
    public static final int SHARK_BIRTH_ENERGY_MIN = 1;
    public static final int SHARK_BIRTH_ENERGY_MAX = 1_000;
    public static final int SHARK_BIRTH_ENERGY_STEP = RULE_STEP;
    public static final int SHARK_ENERGY_LOSS_PER_STEP_DEFAULT = 1;
    public static final int SHARK_ENERGY_LOSS_PER_STEP_MIN = 1;
    public static final int SHARK_ENERGY_LOSS_PER_STEP_MAX = 1_000;
    public static final int SHARK_ENERGY_LOSS_PER_STEP_STEP = RULE_STEP;
    public static final int SHARK_ENERGY_GAIN_PER_FISH_DEFAULT = 2;
    public static final int SHARK_ENERGY_GAIN_PER_FISH_MIN = 1;
    public static final int SHARK_ENERGY_GAIN_PER_FISH_MAX = 1_000;
    public static final int SHARK_ENERGY_GAIN_PER_FISH_STEP = RULE_STEP;
    public static final int SHARK_MIN_REPRODUCTION_AGE_DEFAULT = 15;
    public static final int SHARK_MIN_REPRODUCTION_AGE_MIN = 1;
    public static final int SHARK_MIN_REPRODUCTION_AGE_MAX = 1_000;
    public static final int SHARK_MIN_REPRODUCTION_AGE_STEP = RULE_STEP;
    public static final int SHARK_MIN_REPRODUCTION_ENERGY_DEFAULT = 5;
    public static final int SHARK_MIN_REPRODUCTION_ENERGY_MIN = 1;
    public static final int SHARK_MIN_REPRODUCTION_ENERGY_MAX = 1_000;
    public static final int SHARK_MIN_REPRODUCTION_ENERGY_STEP = RULE_STEP;
    public static final int SHARK_MIN_REPRODUCTION_INTERVAL_DEFAULT = 3;
    public static final int SHARK_MIN_REPRODUCTION_INTERVAL_MIN = 1;
    public static final int SHARK_MIN_REPRODUCTION_INTERVAL_MAX = 1_000;
    public static final int SHARK_MIN_REPRODUCTION_INTERVAL_STEP = RULE_STEP;

    /**
     * Private constructor to prevent instantiation.
     */
    private WatorConstraints() {
    }

}

