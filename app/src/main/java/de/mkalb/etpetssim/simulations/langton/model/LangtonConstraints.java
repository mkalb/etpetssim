package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;

import java.util.*;

/**
 * Configuration constraints and defaults used across the simulation layers.
 */
public final class LangtonConstraints {

    // Structure
    public static final CellShape CELL_SHAPE_DEFAULT = CellShape.SQUARE;
    public static final List<CellShape> CELL_SHAPE_VALUES = List.of(
            CellShape.values());
    public static final GridEdgeBehavior GRID_EDGE_BEHAVIOR_DEFAULT = GridEdgeBehavior.WRAP_XY;
    public static final List<GridEdgeBehavior> GRID_EDGE_BEHAVIOR_VALUES = List.of(
            GridEdgeBehavior.WRAP_XY,
            GridEdgeBehavior.ABSORB_XY);
    public static final int GRID_WIDTH_DEFAULT = 200;
    public static final int GRID_WIDTH_MIN = 100;
    public static final int GRID_WIDTH_MAX = 2_000;
    public static final int GRID_WIDTH_STEP = GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE;
    public static final int GRID_HEIGHT_DEFAULT = 200;
    public static final int GRID_HEIGHT_MIN = 100;
    public static final int GRID_HEIGHT_MAX = 2_000;
    public static final int GRID_HEIGHT_STEP = GridTopology.MAX_REQUIRED_HEIGHT_MULTIPLE;

    // Layout
    public static final int CELL_EDGE_LENGTH_DEFAULT = 4;
    public static final int CELL_EDGE_LENGTH_MIN = 1;
    public static final int CELL_EDGE_LENGTH_MAX = 50;
    public static final CellDisplayMode CELL_DISPLAY_MODE_DEFAULT = CellDisplayMode.SHAPE;
    public static final List<CellDisplayMode> CELL_DISPLAY_MODE_VALUES = List.of(
            CellDisplayMode.SHAPE,
            CellDisplayMode.SHAPE_BORDERED);

    // Initialization - Seed
    public static final String SEED_INITIAL = "";

    // Rules - NeighborhoodMode
    public static final NeighborhoodMode NEIGHBORHOOD_MODE_DEFAULT = NeighborhoodMode.EDGES_ONLY;

    /**
     * Private constructor to prevent instantiation.
     */
    private LangtonConstraints() {
    }

}
