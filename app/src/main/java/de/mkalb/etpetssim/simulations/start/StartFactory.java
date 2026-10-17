package de.mkalb.etpetssim.simulations.start;

import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import javafx.stage.Stage;

import java.util.function.*;

/**
 * Factory for the start screen main view.
 */
public final class StartFactory {

    private StartFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView(Stage stage,
                                                    BiConsumer<Stage, SimulationType> stageUpdater) {
        var view = new StartMainView(stage, stageUpdater);

        // Return the main view
        return view;
    }

}
