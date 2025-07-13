package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.ConwayViewModel;
import de.mkalb.etpetssim.simulations.simulationlab.SimulationLabController;
import de.mkalb.etpetssim.simulations.startscreen.StartScreenController;
import de.mkalb.etpetssim.simulations.wator.WaTorController;
import javafx.stage.Stage;

import java.util.function.*;

/**
 * Factory class for creating instances of simulations.
 * It provides a method to create a simulation instance based on the type of simulation requested.
 */
public final class SimulationFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SimulationFactory() {
    }

    /**
     * Creates a new instance of a simulation based on the provided type, stage and stage updater.
     *
     * @param type the type of simulation to create
     * @param stage the stage where the simulation will be displayed
     * @param stageUpdater a consumer that updates the stage with the new simulation type
     * @return a new SimulationInstance for the specified type
     */
    public static SimulationInstance createInstance(SimulationType type,
                                                    Stage stage,
                                                    BiConsumer<Stage, SimulationType> stageUpdater) {
        return switch (type) {
            case STARTSCREEN -> SimulationInstance.of(type, new StartScreenController(stage, stageUpdater));
            case SIMULATION_LAB -> SimulationInstance.of(type, new SimulationLabController(stage));
            case WATOR -> SimulationInstance.of(type, new WaTorController());
            case CONWAYS_LIFE -> SimulationInstance.of(type, new ConwayViewModel());
            // add other simulation types here later after implementing them
            default -> {
                AppLogger.error("Simulation type not implemented: " + type.name());
                // Switch to the start screen as a fallback
                yield SimulationInstance.of(SimulationType.STARTSCREEN, new StartScreenController(stage, stageUpdater));
            }
        };
    }

}
