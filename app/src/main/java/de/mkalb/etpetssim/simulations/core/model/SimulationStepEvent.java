package de.mkalb.etpetssim.simulations.core.model;

/**
 * Represents a UI-facing event emitted after simulation step processing.
 *
 * @param batchModeRunning whether the simulation is currently running in batch mode
 * @param stepCount the total step count after processing
 * @param finalStep whether this event represents the final step before stopping
 */
public record SimulationStepEvent(boolean batchModeRunning, int stepCount, boolean finalStep) {}
