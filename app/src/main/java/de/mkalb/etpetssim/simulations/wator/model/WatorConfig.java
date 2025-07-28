package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;

public record WatorConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        double fishPercent,
        double sharkPercent,
        int sharkBirthEnergy) {}

