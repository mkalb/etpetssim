package de.mkalb.etpetssim.simulations.start;

import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import javafx.stage.Stage;

import java.util.function.*;

public final class StartFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private StartFactory() {
    }

    public static SimulationMainView createMainView(Stage stage,
                                                    BiConsumer<Stage, SimulationType> stageUpdater) {
        return new StartMainView(stage, stageUpdater);
    }

}
