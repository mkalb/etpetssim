package de.mkalb.etpetssim.simulations.model;

public record SimulationStepEvent(boolean batchModeRunning, int stepCount, boolean finalStep) {}
