package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;

final class GridModelTestSupport {

    static final GridStructure SQUARE_STRUCTURE_8X8 = new GridStructure(
            new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
            new GridSize(8, 8));

    private GridModelTestSupport() {
    }

    static GridCoordinate coordinate(int x, int y) {
        return new GridCoordinate(x, y);
    }

    enum TestEntity implements GridEntity {
        EMPTY,
        WALL,
        FOOD;

        @Override
        public String descriptorId() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}

