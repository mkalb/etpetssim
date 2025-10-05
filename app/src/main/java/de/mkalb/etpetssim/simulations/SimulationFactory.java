package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.conway.ConwayFactory;
import de.mkalb.etpetssim.simulations.lab.LabFactory;
import de.mkalb.etpetssim.simulations.langton.LangtonFactory;
import de.mkalb.etpetssim.simulations.start.StartFactory;
import de.mkalb.etpetssim.simulations.wator.WatorFactory;
import javafx.stage.Stage;

import java.util.function.*;

/**
 * Factory for creating simulation main views and wrapping them in {@link SimulationInstance} objects.
 * <p>
 * The {@link #createInstance(SimulationType, Stage, BiConsumer)} method builds and wires the appropriate
 * view and view models for the requested {@link SimulationType}. If the type is unknown or not implemented,
 * the factory logs an error and returns the start screen as a safe fallback.
 * </p>
 */
public final class SimulationFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SimulationFactory() {
    }

    /**
     * Creates a new instance of a simulation based on the provided type, stage, and stage updater.
     * <p>
     * If the type is not supported, this method logs an error and returns a start screen instance.
     * </p>
     *
     * @param type the type of simulation to create
     * @param stage the primary stage where the simulation will be displayed
     * @param stageUpdater a callback to switch the stage content to a different simulation type
     * @return a new {@link SimulationInstance} for the specified type (or a start screen instance on fallback)
     */
    public static SimulationInstance createInstance(SimulationType type,
                                                    Stage stage,
                                                    BiConsumer<Stage, SimulationType> stageUpdater) {
        return switch (type) {
            case STARTSCREEN -> SimulationInstance.of(type, StartFactory.createMainView(stage, stageUpdater));
            case SIMULATION_LAB -> SimulationInstance.of(type, LabFactory.createMainView());
            case WATOR -> SimulationInstance.of(type, WatorFactory.createMainView());
            case CONWAYS_LIFE -> SimulationInstance.of(type, ConwayFactory.createMainView());
            case LANGTONS_ANT -> SimulationInstance.of(type, LangtonFactory.createMainView());
            // Add other simulation types here after implementing them
            default -> {
                AppLogger.error("Simulation type not implemented: " + type.name());
                // Switch to the start screen as a fallback
                yield SimulationInstance.of(SimulationType.STARTSCREEN, StartFactory.createMainView(stage, stageUpdater));
            }
        };
    }

}
