package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.engine.neighborhood.RadiusRingCell;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.model.LabSimulationManager;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;

/**
 * Main ViewModel of the Simulation Lab.
 */
public final class LabMainViewModel
        extends AbstractMainViewModel<LabEntity, WritableGridModel<LabEntity>, LabConfig, LabStatistics> {

    private static final String LOG_COMPONENT = "LabMainViewModel";

    private static final int NEIGHBORHOOD_RING_MAX_RADIUS = 5;
    private static final int NEIGHBORHOOD_COORDINATE_RADIUS = 2;

    private final LabConfigViewModel labConfigViewModel;
    private final LabControlViewModel labControlViewModel;
    private final DefaultObservationViewModel<LabEntity, GridCell<LabEntity>, LabStatistics> labObservationViewModel;

    private final ChangeListener<Boolean> configChangedRequestListener;
    private final ChangeListener<Boolean> drawRequestListener;
    private final ChangeListener<Boolean> drawModelRequestListener;
    private final ChangeListener<Boolean> drawTestRequestListener;

    private @Nullable LabSimulationManager simulationManager;

    private Runnable configChangedListener = () -> {};
    private Runnable drawRequestedListener = () -> {};
    private Runnable drawModelRequestedListener = () -> {};
    private Runnable drawTestRequestedListener = () -> {};

    public LabMainViewModel(ObjectProperty<SimulationState> simulationState,
                            LabConfigViewModel configViewModel,
                            LabControlViewModel controlViewModel,
                            DefaultObservationViewModel<LabEntity, GridCell<LabEntity>,
                                    LabStatistics> observationViewModel) {
        super(simulationState, configViewModel, observationViewModel);
        labObservationViewModel = observationViewModel;
        labConfigViewModel = configViewModel;
        labControlViewModel = controlViewModel;

        configChangedRequestListener = createRequestListener(labConfigViewModel.configChangedRequestedProperty(),
                this::handleConfigChanged);
        labConfigViewModel.configChangedRequestedProperty().addListener(configChangedRequestListener);

        drawRequestListener = createRequestListener(labControlViewModel.drawRequestedProperty(),
                this::handleDrawRequested);
        labControlViewModel.drawRequestedProperty().addListener(drawRequestListener);

        drawModelRequestListener = createRequestListener(labControlViewModel.drawModelRequestedProperty(),
                this::handleDrawModelRequested);
        labControlViewModel.drawModelRequestedProperty().addListener(drawModelRequestListener);

        drawTestRequestListener = createRequestListener(labControlViewModel.drawTestRequestedProperty(),
                this::handleDrawTestRequested);
        labControlViewModel.drawTestRequestedProperty().addListener(drawTestRequestListener);
    }

    private static ChangeListener<Boolean> createRequestListener(BooleanProperty request, Runnable handler) {
        return (_, _, requested) -> {
            if (requested) {
                handler.run();
                request.set(false);
            }
        };
    }

    @Override
    public SimulationTerminationHandle shutdownSimulation() {
        AppLogger.infof("%s: Shutting down simulation", LOG_COMPONENT);
        setSimulationState(SimulationState.SHUTTING_DOWN);

        labConfigViewModel.configChangedRequestedProperty().removeListener(configChangedRequestListener);
        labControlViewModel.drawRequestedProperty().removeListener(drawRequestListener);
        labControlViewModel.drawModelRequestedProperty().removeListener(drawModelRequestListener);
        labControlViewModel.drawTestRequestedProperty().removeListener(drawTestRequestListener);

        unbindObservationBindings();

        reset();
        configChangedListener = () -> {};
        drawRequestedListener = () -> {};
        drawModelRequestedListener = () -> {};
        drawTestRequestedListener = () -> {};
        return SimulationTerminationHandle.completed();
    }

    @Override
    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.structure();
    }

    @Override
    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.config().cellEdgeLength();
    }

    @Override
    public LabConfig getCurrentConfig() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.config();
    }

    @Override
    public WritableGridModel<LabEntity> getCurrentGridModel() {
        Objects.requireNonNull(simulationManager, "Unable to access simulationManager: not initialized");
        return simulationManager.currentGridModel();
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

    public Optional<LabNeighborhoodHighlights> computeNeighborhoodHighlights(GridCoordinate center,
                                                                             GridStructure gridStructure) {
        if (simulationManager == null) {
            return Optional.empty();
        }

        NeighborhoodMode neighborhoodMode = simulationManager.config().neighborhoodMode();
        WritableGridModel<LabEntity> model = simulationManager.currentGridModel();

        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCellsByRadius =
                CellNeighborhoods.cellsByRadiusRings(center,
                        neighborhoodMode,
                        gridStructure,
                        NEIGHBORHOOD_RING_MAX_RADIUS,
                        coordinate -> new GridCell<>(coordinate, model.getEntity(coordinate)));

        List<GridCoordinate> validNeighborCoordinates = CellNeighborhoods.neighborCoordinatesWithinRadius(
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

        List<EdgeBehaviorResult> neighborEdgeBehaviorResults = List.copyOf(
                CellNeighborhoods.neighborEdgeBehaviorResults(center, neighborhoodMode, gridStructure));

        return Optional.of(new LabNeighborhoodHighlights(
                ringCellsByRadius,
                validNeighborCoordinates,
                validNeighborsWithEdgeBehavior,
                neighborEdgeBehaviorResults));
    }

    public void updateSelectedGridCell(GridCoordinate coordinate) {
        if (simulationManager == null) {
            labObservationViewModel.selectedGridCellProperty().set(null);
            return;
        }
        LabEntity entity = simulationManager.currentGridModel().getEntity(coordinate);
        labObservationViewModel.selectedGridCellProperty().set(new GridCell<>(coordinate, entity));
    }

    public void resetSelectedGridCell() {
        labObservationViewModel.selectedGridCellProperty().set(null);
    }

    private void handleConfigChanged() {
        setSimulationState(SimulationState.READY);
        reset();

        configChangedListener.run();
    }

    private void handleDrawRequested() {
        setNotificationType(SimulationNotificationType.NONE);

        setSimulationState(SimulationState.RUNNING_TIMED);

        reset();

        LabConfig config = configViewModel.createConfig();
        if (!config.isValid()) {
            setSimulationState(SimulationState.ERROR);
            AppLogger.warnf("%s: Ignoring start request: configuration is invalid", LOG_COMPONENT);
            setNotificationType(SimulationNotificationType.INVALID_CONFIG);
            return;
        }

        simulationManager = new LabSimulationManager(config);
        labObservationViewModel.setStatistics(simulationManager.statistics());

        drawRequestedListener.run();

        setSimulationState(SimulationState.PAUSED);
    }

    private void handleDrawModelRequested() {
        drawModelRequestedListener.run();
    }

    private void handleDrawTestRequested() {
        drawTestRequestedListener.run();
    }

    private void reset() {
        simulationManager = null;
        resetClickedCoordinateProperties();
        resetSelectedGridCell();
    }

}
