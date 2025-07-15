package de.mkalb.etpetssim.simulations.conwayslife.model;

public record ConwayConfig(
        double cellEdgeLength,
        int gridWidth,
        int gridHeight,
        double alivePercent) {
}
