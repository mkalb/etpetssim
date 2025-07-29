package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;

public record WatorConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        double fishPercent,
        double sharkPercent,
        int sharkBirthEnergy,
        NeighborhoodMode neighborhoodMode) {}

