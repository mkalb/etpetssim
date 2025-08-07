package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;

public interface SimulationConfig {

    CellShape cellShape();

    GridEdgeBehavior gridEdgeBehavior();

    int gridWidth();

    int gridHeight();

    double cellEdgeLength();

}
