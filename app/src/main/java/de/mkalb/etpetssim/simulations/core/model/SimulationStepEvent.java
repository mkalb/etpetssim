package de.mkalb.etpetssim.simulations.core.model;

public record SimulationStepEvent(boolean batchModeRunning, int stepCount, boolean finalStep) {}
