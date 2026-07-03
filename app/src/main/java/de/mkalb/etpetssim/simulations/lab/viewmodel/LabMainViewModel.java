package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.lab.model.*;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.shared.LabNeighborhoodHighlights;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabMainViewModel
        extends AbstractMainViewModel<LabEntity, WritableGridModel<LabEntity>, LabConfig, LabStatistics> {

    private static final String LOG_COMPONENT = "LabMainViewModel";

    private static final int NEIGHBORHOOD_RING_MAX_RADIUS = 5;
    private static final int NEIGHBORHOOD_COORDINATE_RADIUS = 2;

    private final LabConfigViewModel labConfigViewModel;
    private final LabControlViewModel labControlViewModel;
    private final DefaultObservationViewModel<LabEntity, GridCell<LabEntity>, LabStatistics> labObservationViewModel;

    private final ChangeListener<Boolean> configChangedRequestedListener;
    private final ChangeListener<Boolean> drawRequestedChangeListener;
    private final ChangeListener<Boolean> drawModelRequestedChangeListener;
    private final ChangeListener<Boolean> drawTestRequestedChangeListener;

    private @Nullable LabSimulationManager simulationManager;

    private Runnable configChangedListener = () -> {};
    private Runnable drawRequestedListener = () -> {};
    private Runnable drawModelRequestedListener = () -> {};
    private Runnable drawTestRequestedListener = () -> {};

    public LabMainViewModel(ObjectProperty<SimulationState> simulationState,
                            LabConfigViewModel configViewModel,
                            LabControlViewModel controlViewModel,
                            DefaultObservationViewModel<LabEntity, GridCell<LabEntity>, LabStatistics> observationViewModel) {
        super(simulationState, configViewModel, observationViewModel);
        // Keep references to the view models to add/remove listeners and access properties.
        labObservationViewModel = observationViewModel;
        labConfigViewModel = configViewModel;
        labControlViewModel = controlViewModel;

        configChangedRequestedListener = (_, _, newVal) -> {
            if (newVal) {
                handleConfigChanged();
                labConfigViewModel.configChangedRequestedProperty().set(false); // reset
            }
        };
        labConfigViewModel.configChangedRequestedProperty().addListener(configChangedRequestedListener);

        drawRequestedChangeListener = (_, _, newVal) -> {
            if (newVal) {
                handleDrawRequested();
                labControlViewModel.drawRequestedProperty().set(false); // reset
            }
        };
        labControlViewModel.drawRequestedProperty().addListener(drawRequestedChangeListener);

        drawModelRequestedChangeListener = (_, _, newVal) -> {
            if (newVal) {
                handleDrawModelRequested();
                labControlViewModel.drawModelRequestedProperty().set(false); // reset
            }
        };
        labControlViewModel.drawModelRequestedProperty().addListener(drawModelRequestedChangeListener);

        drawTestRequestedChangeListener = (_, _, newVal) -> {
            if (newVal) {
                handleDrawTestRequested();
                labControlViewModel.drawTestRequestedProperty().set(false); // reset
            }
        };
        labControlViewModel.drawTestRequestedProperty().addListener(drawTestRequestedChangeListener);
    }

    @Override
    public void shutdownSimulation() {
        AppLogger.infof("%s: Shutting down simulation", LOG_COMPONENT);
        setSimulationState(SimulationState.SHUTTING_DOWN);

        labConfigViewModel.configChangedRequestedProperty().removeListener(configChangedRequestedListener);
        labControlViewModel.drawRequestedProperty().removeListener(drawRequestedChangeListener);
        labControlViewModel.drawModelRequestedProperty().removeListener(drawModelRequestedChangeListener);
        labControlViewModel.drawTestRequestedProperty().removeListener(drawTestRequestedChangeListener);

        unbindObservationBindings();

        reset();
        configChangedListener = () -> {};
        drawRequestedListener = () -> {};
        drawModelRequestedListener = () -> {};
        drawTestRequestedListener = () -> {};
    }

    @Override
    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.structure();
    }

    @Override
    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config().cellEdgeLength();
    }

    @Override
    public LabConfig getCurrentConfig() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config();
    }

    @Override
    public WritableGridModel<LabEntity> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    @Override
    public boolean hasSimulationManager() {
        return simulationManager != null;
    }

    public void setConfigChangedListener(Runnable listener) {
        configChangedListener = listener;
    }

    public void setDrawRequestedListener(Runnable listener) {
        drawRequestedListener = listener;
    }

    public void setDrawModelRequestedListener(Runnable listener) {
        drawModelRequestedListener = listener;
    }

    public void setDrawTestRequestedListener(Runnable listener) {
        drawTestRequestedListener = listener;
    }

    public void handleConfigChanged() {
        setSimulationState(SimulationState.INITIAL);
        reset();

        configChangedListener.run();
    }

    public void handleDrawRequested() {
        // Reset notification type.
        setNotificationType(SimulationNotificationType.NONE);

        setSimulationState(SimulationState.RUNNING_TIMED);

        reset();

        LabConfig config = configViewModel.getConfig();
        if (!config.isValid()) {
            setSimulationState(SimulationState.ERROR);
            AppLogger.warnf("%s: Cannot start simulation because configuration is invalid.", LOG_COMPONENT);
            setNotificationType(SimulationNotificationType.INVALID_CONFIG);
            return;
        }

        simulationManager = new LabSimulationManager(config);
        labObservationViewModel.setStatistics(simulationManager.statistics());

        drawRequestedListener.run();

        setSimulationState(SimulationState.PAUSED);
    }

    public void handleDrawModelRequested() {
        drawModelRequestedListener.run();
    }

    public void handleDrawTestRequested() {
        drawTestRequestedListener.run();
    }

    public Optional<LabNeighborhoodHighlights> computeNeighborhoodHighlights(GridCoordinate center, GridStructure gridStructure) {
        if (simulationManager == null) {
            return Optional.empty();
        }

        NeighborhoodMode neighborhoodMode = simulationManager.config().neighborhoodMode();
        WritableGridModel<LabEntity> model = simulationManager.currentModel();

        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadius =
                CellNeighborhoods.cellsByRadiusRings(center,
                        neighborhoodMode,
                        gridStructure,
                        NEIGHBORHOOD_RING_MAX_RADIUS,
                        coordinate -> new GridCell<>(coordinate, model.getEntity(coordinate)));

        List<GridCoordinate> validNeighborCoordinates = CellNeighborhoods.coordinatesOfNeighbors(
                                                                                 center,
                                                                                 neighborhoodMode,
                                                                                 gridStructure.cellShape(),
                                                                                 NEIGHBORHOOD_COORDINATE_RADIUS)
                                                                         .stream()
                                                                         .filter(gridStructure::isCoordinateValid)
                                                                         .toList();

        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> validNeighborsWithEdgeBehavior = new LinkedHashMap<>();
        CellNeighborhoods.cellNeighborsWithEdgeBehavior(center, neighborhoodMode, gridStructure)
                         .forEach((neighborCoordinate, neighborCells) -> {
                             if (gridStructure.isCoordinateValid(neighborCoordinate)) {
                                 validNeighborsWithEdgeBehavior.put(neighborCoordinate, neighborCells);
                             }
                         });

        List<EdgeBehaviorResult> neighborEdgeResults = List.copyOf(
                CellNeighborhoods.neighborEdgeResults(center, neighborhoodMode, gridStructure));

        return Optional.of(new LabNeighborhoodHighlights(
                ringCellsByRadius,
                validNeighborCoordinates,
                validNeighborsWithEdgeBehavior,
                neighborEdgeResults));
    }

    public void updateSelectedGridCell(@Nullable GridCoordinate coordinate) {
        if ((simulationManager == null) || (coordinate == null)) {
            labObservationViewModel.selectedGridCellProperty().set(null);
            return;
        }
        LabEntity entity = simulationManager.currentModel().getEntity(coordinate);
        labObservationViewModel.selectedGridCellProperty().set(new GridCell<>(coordinate, entity));
    }

    public void resetSelectedGridCell() {
        labObservationViewModel.selectedGridCellProperty().set(null);
    }

    private void reset() {
        // Reset the simulation manager if it exists
        simulationManager = null;
        // Clear last clicked coordinate
        resetClickedCoordinateProperties();
        resetSelectedGridCell();
    }

}
