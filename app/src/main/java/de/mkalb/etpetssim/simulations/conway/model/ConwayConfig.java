package de.mkalb.etpetssim.simulations.conway.model;

public record ConwayConfig(
        double cellEdgeLength,
        int gridWidth,
        int gridHeight,
        double alivePercent) {
}
