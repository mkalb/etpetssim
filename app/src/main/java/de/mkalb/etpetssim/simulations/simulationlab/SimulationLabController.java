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

        CellShape cellShape = CellShape.SQUARE;
        int hexagonLength = 32;

        double cellSideLength = (cellShape == CellShape.HEXAGON) ? hexagonLength : (2 * hexagonLength);
        structure = new GridStructure(new GridTopology(cellShape, GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                GridSize.SMALL_RECTANGLE);
        viewBuilder = new SimulationLabViewBuilder(structure, cellSideLength);
    }

    @Override
    public Region buildViewRegion() {
        return viewBuilder.build();
    }

}
