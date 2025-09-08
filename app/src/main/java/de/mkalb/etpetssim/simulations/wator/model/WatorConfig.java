package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.model.SimulationConfig;

public record WatorConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        long seed,
        // Initialization
        double fishPercent,
        double sharkPercent,
        // Rules
        int fishMaxAge,
        int fishMinReproductionAge,
        int fishMinReproductionInterval,
        int sharkMaxAge,
        int sharkBirthEnergy,
        int sharkEnergyLossPerStep,
        int sharkEnergyGainPerFish,
        int sharkMinReproductionAge,
        int sharkMinReproductionEnergy,
        int sharkMinReproductionInterval,
        NeighborhoodMode neighborhoodMode)
        implements SimulationConfig {

    @Override
    public boolean isValid() {
        boolean baseValid = SimulationConfig.super.isValid();
        boolean watorValid = (fishPercent + sharkPercent) < 1.0d;
        return baseValid && watorValid;
    }

}

