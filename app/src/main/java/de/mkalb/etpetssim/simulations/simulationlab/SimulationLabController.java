package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.SimulationController;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public final class SimulationLabController implements SimulationController {

    private final Stage stage;
    private final GridStructure structure;
    private final SimulationLabViewBuilder viewBuilder;

    public SimulationLabController(Stage stage) {
        this.stage = stage;
        structure = new GridStructure(new GridTopology(CellShape.TRIANGLE, BoundaryType.BLOCK_X_BLOCK_Y),
                new GridSize(20, 16));
        viewBuilder = new SimulationLabViewBuilder(structure, 100.0d);
    }

    @Override
    public Region buildViewRegion() {
        return viewBuilder.build();
    }

}
