package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.simulations.conway.ConwayFactory;
import de.mkalb.etpetssim.simulations.etpets.EtpetsFactory;
import de.mkalb.etpetssim.simulations.forest.ForestFactory;
import de.mkalb.etpetssim.simulations.lab.LabFactory;
import de.mkalb.etpetssim.simulations.langton.LangtonFactory;
import de.mkalb.etpetssim.simulations.rebounding.ReboundingFactory;
import de.mkalb.etpetssim.simulations.snake.SnakeFactory;
import de.mkalb.etpetssim.simulations.sugar.SugarFactory;
import de.mkalb.etpetssim.simulations.wator.WatorFactory;

/**
 * Factory for creating simulation main views and wrapping them in {@link SimulationScreen} objects.
 */
public final class SimulationFactory {

    private SimulationFactory() {
    }

    /**
     * Creates a new screen for a simulation of the provided type.
     *
     * @param simulationType the type of simulation to create
     * @return a new {@link SimulationScreen} for the specified type
     */
    public static SimulationScreen createScreen(SimulationType simulationType) {
        return SimulationScreen.of(simulationType, switch (simulationType) {
            case ET_PETS -> EtpetsFactory.createMainView();
            case WATOR -> WatorFactory.createMainView();
            case CONWAYS_LIFE -> ConwayFactory.createMainView();
            case LANGTONS_ANT -> LangtonFactory.createMainView();
            case FOREST_FIRE -> ForestFactory.createMainView();
            case SUGARSCAPE -> SugarFactory.createMainView();
            case SNAKE -> SnakeFactory.createMainView();
            case REBOUNDING_ENTITIES -> ReboundingFactory.createMainView();
            case SIMULATION_LAB -> LabFactory.createMainView();
        });
    }

}
