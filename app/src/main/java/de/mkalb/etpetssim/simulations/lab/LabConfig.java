package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.engine.CellShape;

public record LabConfig(
        CellShape shape,
        double cellEdgeLength,
        int gridWidth,
        int gridHeight) {
}
