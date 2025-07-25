package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;

public record LabConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength) {
}
