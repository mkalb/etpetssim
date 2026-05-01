package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.model.SimulationConfig;

/**
 * Immutable configuration for the Wa-Tor simulation.
 *
 * @param cellShape the configured cell shape
 * @param gridEdgeBehavior the configured grid edge behavior
 * @param gridWidth the grid width in cells
 * @param gridHeight the grid height in cells
 * @param cellEdgeLength the rendered cell edge length in pixels
 * @param cellDisplayMode the cell display mode used by the UI
 * @param seed the random seed used for initialization
 * @param fishPercent the initial fish population share
 * @param sharkPercent the initial shark population share
 * @param fishMaxAge the maximum fish age
 * @param fishMinReproductionAge the minimum fish age for reproduction
 * @param fishMinReproductionInterval the minimum number of steps between fish reproductions
 * @param sharkMaxAge the maximum shark age
 * @param sharkBirthEnergy the initial shark energy
 * @param sharkEnergyLossPerStep the shark energy loss per step
 * @param sharkEnergyGainPerFish the shark energy gained by eating one fish
 * @param sharkMinReproductionAge the minimum shark age for reproduction
 * @param sharkMinReproductionEnergy the minimum shark energy for reproduction
 * @param sharkMinReproductionInterval the minimum number of steps between shark reproductions
 * @param neighborhoodMode the neighborhood mode used for movement and interaction
 */
public record WatorConfig(
        CellShape cellShape,
        GridEdgeBehavior gridEdgeBehavior,
        int gridWidth,
        int gridHeight,
        double cellEdgeLength,
        CellDisplayMode cellDisplayMode,
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

    /**
     * Validates the common simulation settings and ensures that fish and shark shares fit into the grid.
     *
     * @return {@code true} if this configuration is valid, otherwise {@code false}
     */
    @Override
    public boolean isValid() {
        boolean baseValid = SimulationConfig.super.isValid();
        boolean watorValid = (fishPercent + sharkPercent) < 1.0d;
        return baseValid && watorValid;
    }

}

