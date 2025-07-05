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
        int hexagonEdgeLength = 64;

        double cellEdgeLength = (cellShape == CellShape.HEXAGON) ? hexagonEdgeLength : (2 * hexagonEdgeLength);
        structure = new GridStructure(new GridTopology(cellShape, GridEdgeBehavior.WRAP_X_BLOCK_Y),
                GridSize.SMALL_SQUARE);
        viewBuilder = new SimulationLabViewBuilder(structure, cellEdgeLength);
    }

    @Override
    public Region buildViewRegion() {
        return viewBuilder.build();
    }

}
