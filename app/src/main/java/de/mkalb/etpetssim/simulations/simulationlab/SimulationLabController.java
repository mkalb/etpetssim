package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.SimulationController;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

@SuppressWarnings("MagicNumber")
public final class SimulationLabController implements SimulationController {

    private final Stage stage;
    private final GridStructure structure;
    private final SimulationLabViewBuilder viewBuilder;

    public SimulationLabController(Stage stage) {
        this.stage = stage;
        CellShape cellShape = CellShape.TRIANGLE;
        double cellSideLength = 100.0d;
        structure = new GridStructure(new GridTopology(cellShape, BoundaryType.BLOCK_X_BLOCK_Y),
                new GridSize(20, 16));
        viewBuilder = new SimulationLabViewBuilder(structure, cellSideLength);
    }

    @Override
    public Region buildViewRegion() {
        return viewBuilder.build();
    }

}
