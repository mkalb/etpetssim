package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.SimulationController;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class SimulationLabController implements SimulationController {

    private final Stage stage;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final SimulationLabViewBuilder viewBuilder;

    private GridStructure structure;
    private GridModel<SimulationLabEntity> model;
    private CellShape cellShape;
    private double cellEdgeLength;

    public SimulationLabController(Stage stage) {
        this.stage = stage;

        entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(SimulationLabEntity.values());

        init();

        viewBuilder = new SimulationLabViewBuilder(model, entityDescriptorRegistry, cellEdgeLength);
    }

    private void init() {
        cellShape = CellShape.TRIANGLE;
        int hexagonEdgeLength = 64;
        cellEdgeLength = (cellShape == CellShape.HEXAGON) ? hexagonEdgeLength : (2 * hexagonEdgeLength);

        structure = new GridStructure(new GridTopology(cellShape, GridEdgeBehavior.WRAP_X_WRAP_Y),
                new GridSize(30, 30));

        model = new SparseGridModel<>(structure, SimulationLabEntity.NORMAL);
        GridInitializers.placeRandomCounted(3, () -> SimulationLabEntity.HIGHLIGHTED, new Random())
                        .initialize(model);

        // Place a symmetric small cross pattern of highlighted entities
        GridEntityUtils.placePatternAt(new GridCoordinate(5, 5), model,
                GridPattern.of(SimulationLabEntity.HIGHLIGHTED, List.of(
                        new GridOffset(0, 1),
                        new GridOffset(1, 0),
                        new GridOffset(2, 1),
                        new GridOffset(1, 2)
                )));
    }

    @Override
    public Region buildViewRegion() {
        return viewBuilder.build();
    }

}
