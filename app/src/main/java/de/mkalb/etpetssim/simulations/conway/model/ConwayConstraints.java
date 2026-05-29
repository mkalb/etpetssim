package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import java.util.*;

/**
 * Configuration constraints and defaults used across the simulation layers.
 */
public final class ConwayConstraints {

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
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE_BORDERED;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE,
            CellDisplayMode.SHAPE_BORDERED,
            CellDisplayMode.CIRCLE,
            CellDisplayMode.CIRCLE_BORDERED);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Initialization
    public static final double ALIVE_PERCENT_DEFAULT = 0.1d;
    public static final double ALIVE_PERCENT_MIN = 0.0d;
    public static final double ALIVE_PERCENT_MAX = 1.0d;

    // Rules - NeighborhoodMode
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_AND_VERTICES;

    // Rules
    public static final ConwayTransitionRules TRANSITION_RULES_DEFAULT = ConwayTransitionRules.of(Set.of(2, 3), Set.of(3));
    public static final int BIRTH_NEIGHBOR_COUNT_MIN = 1;

    /**
     * Private constructor to prevent instantiation.
     */
    private ConwayConstraints() {
    }

}
