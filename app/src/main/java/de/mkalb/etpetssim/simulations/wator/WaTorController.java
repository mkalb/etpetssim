package de.mkalb.etpetssim.simulations.wator;

import de.mkalb.etpetssim.simulations.view.SimulationMainView;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WaTorController implements SimulationMainView {

    private static final int MAX_SIMULATION_TIME = 100;
    private static final double MAX_PROPORTION_OF_SIM_TIME_TO_SPEED = 0.5d;

    private final WaTorConfigModel waTorConfigModel;
    private final WaTorSimulationModel waTorSimulationModel;
    private final WaTorViewBuilder waTorViewBuilder;
    private @Nullable WaTorSimulation waTorSimulation;

    public WaTorController() {
        waTorConfigModel = new WaTorConfigModel();
        waTorSimulationModel = new WaTorSimulationModel();
        waTorViewBuilder = new WaTorViewBuilder(waTorConfigModel, waTorSimulationModel,
                this::startSimulation,
                this::updateSimulation,
                this::creatureAt);
    }

    @Override
    public Region buildMainRegion() {
        return waTorViewBuilder.build();
    }

    public Optional<WaTorCreature> creatureAt(WaTorCoordinate coordinate) {
        return waTorSimulation.creatureAt(coordinate);
    }

    public boolean startSimulation() {
        waTorSimulationModel.reset();
        waTorSimulation = new WaTorSimulation(waTorConfigModel, waTorSimulationModel);
        WaTorSimulation.SimulationStatus status = waTorSimulation.startSimulation();
        return status != WaTorSimulation.SimulationStatus.STARTED;
    }

    public boolean updateSimulation() {
        // Update simulation and measure the time taken
        long startMillis = System.currentTimeMillis();
        WaTorSimulation.SimulationStatus status = waTorSimulation.updateSimulation();
        long simulationTime = System.currentTimeMillis() - startMillis;

        // Check if the simulation update took too long
        if (status == WaTorSimulation.SimulationStatus.STARTED) {
            if ((simulationTime > MAX_SIMULATION_TIME)
                    || (simulationTime > (waTorConfigModel.speed() * MAX_PROPORTION_OF_SIM_TIME_TO_SPEED))) {
                System.err.println("Error: Simulation update took too long: " + simulationTime + " ms");
                return true;
            } else {
                System.out.println("Simulation update took: " + simulationTime + " ms");
            }
        }

        return status != WaTorSimulation.SimulationStatus.STARTED;
    }

}
