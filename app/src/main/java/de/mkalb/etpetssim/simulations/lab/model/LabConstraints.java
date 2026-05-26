package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;

import java.util.*;

/**
 * Shared lab configuration constraints and defaults used by model and view-model.
 */
public final class LabConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.HEXAGON;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CellShape.values());
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.WRAP_XY;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(
            GridEdgeBehavior.values());
    public static final int GRID_WIDTH_DEFAULT = 16;
    public static final int GRID_WIDTH_MIN = GridSize.MIN_SIZE;
    public static final int GRID_WIDTH_MAX = 100;
    public static final int GRID_WIDTH_STEP = 1;
    public static final int GRID_HEIGHT_DEFAULT = 8;
    public static final int GRID_HEIGHT_MIN = GridSize.MIN_SIZE;
    public static final int GRID_HEIGHT_MAX = 100;
    public static final int GRID_HEIGHT_STEP = 1;

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
    public static final LabConfig.ColorMode COLOR_MODE_DEFAULT = LabConfig.ColorMode.COLOR;
    public static final List<LabConfig.ColorMode> COLOR_MODE_VALUES = List.of(
            LabConfig.ColorMode.values());

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Rules - NeighborhoodMode
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_AND_VERTICES;
    public static final List<NeighborhoodMode> NEIGHBORHOOD_MODE_VALUES = List.of(
            NeighborhoodMode.values());

    /**
     * Private constructor to prevent instantiation.
     */
    private LabConstraints() {
    }

}
