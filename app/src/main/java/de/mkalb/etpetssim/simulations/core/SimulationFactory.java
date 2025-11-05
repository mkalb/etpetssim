package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.SimulationType;
import de.mkalb.etpetssim.simulations.conway.ConwayFactory;
import de.mkalb.etpetssim.simulations.forest.ForestFactory;
import de.mkalb.etpetssim.simulations.lab.LabFactory;
import de.mkalb.etpetssim.simulations.langton.LangtonFactory;
import de.mkalb.etpetssim.simulations.start.StartFactory;
import de.mkalb.etpetssim.simulations.wator.WatorFactory;
import javafx.stage.Stage;

import java.util.function.*;

/**
 * Factory for creating simulation main views and wrapping them in {@link SimulationInstance} objects.
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
     * If the provided simulation type is not supported, this method throws an {@link IllegalArgumentException}.
     * </p>
     *
     * @param type the type of simulation to create
     * @param stage the primary stage where the simulation will be displayed
     * @param stageUpdater a callback to switch the stage content to a different simulation type
     * @return a new {@link SimulationInstance} for the specified type
     * @throws IllegalArgumentException if the simulation type is not supported
     */
    public static SimulationInstance createInstance(SimulationType type,
                                                    Stage stage,
                                                    BiConsumer<Stage, SimulationType> stageUpdater) {
        return SimulationInstance.of(type, switch (type) {
            case STARTSCREEN -> StartFactory.createMainView(stage, stageUpdater);
            case SIMULATION_LAB -> LabFactory.createMainView();
            case WATOR -> WatorFactory.createMainView();
            case CONWAYS_LIFE -> ConwayFactory.createMainView();
            case LANGTONS_ANT -> LangtonFactory.createMainView();
            case FOREST_FIRE -> ForestFactory.createMainView();
            // Add other simulation types here after implementing them
            default -> throw new IllegalArgumentException("Unsupported simulation type: " + type);
        });
    }

}
