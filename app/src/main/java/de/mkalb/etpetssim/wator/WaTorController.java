package de.mkalb.etpetssim.wator;

import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WaTorController {

    private final WaTorConfigModel waTorConfigModel;
    private final WaTorViewBuilder waTorViewBuilder;
    private @Nullable WaTorSimulation waTorSimulation;

    public WaTorController() {
        waTorConfigModel = new WaTorConfigModel();
        waTorViewBuilder = new WaTorViewBuilder(waTorConfigModel,
                this::startSimulation,
                this::updateSimulation,
                this::creatureAt);
    }

    public Region buildViewRegion() {
        return waTorViewBuilder.build();
    }

    public Optional<WaTorCreature> creatureAt(WaTorCoordinate coordinate) {
        Objects.requireNonNull(waTorSimulation);

        return waTorSimulation.creatureAt(coordinate);
    }

    public boolean startSimulation() {
        waTorSimulation = new WaTorSimulation(waTorConfigModel);
        return waTorSimulation.startSimulation() != WaTorSimulation.SimulationStatus.STARTED;
    }

    public boolean updateSimulation() {
        Objects.requireNonNull(waTorSimulation);
        return waTorSimulation.updateSimulation() != WaTorSimulation.SimulationStatus.STARTED;
    }

}
