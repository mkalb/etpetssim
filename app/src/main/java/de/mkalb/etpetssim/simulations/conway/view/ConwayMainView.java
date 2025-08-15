package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.GridEntityUtils;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.model.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import javafx.scene.paint.Color;

import java.util.*;

public final class ConwayMainView
        extends AbstractMainView<
        ConwayConfig,
        ConwayStatistics,
        DefaultMainViewModel<ConwayEntity, ConwayConfig, ConwayStatistics>,
        ConwayConfigView,
        DefaultControlView,
        ConwayObservationView> {

    public ConwayMainView(DefaultMainViewModel<ConwayEntity, ConwayConfig, ConwayStatistics> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ConwayConfigView configView,
                          DefaultControlView controlView,
                          ConwayObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    @Override
    protected void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(this::initializeSimulationCanvas);
        viewModel.setSimulationStepListener(this::updateSimulationStep);
    }

    private void initializeSimulationCanvas() {
        double cellEdgeLength = viewModel.getCellEdgeLength();
        ReadableGridModel<ConwayEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
        GridStructure structure = viewModel.getStructure();
        int stepCount = viewModel.getStepCount();

        createPainterAndUpdateCanvas(structure, cellEdgeLength);

        updateCanvasBorderPane(structure);

        drawCanvas(currentModel, stepCount);
        observationView.updateObservationLabels();
    }

    private void updateSimulationStep(SimulationStepEvent simulationStepEvent) {
        if (simulationStepEvent.batchModeRunning()) {
            // TODO handle batch mode
            AppLogger.info("Updating view for batch mode step " + simulationStepEvent.stepCount());
        } else {
            AppLogger.info("Drawing canvas for step " + simulationStepEvent.stepCount());

            drawCanvas(viewModel.getCurrentModel(), simulationStepEvent.stepCount());
            observationView.updateObservationLabels();
        }
    }

    private void fillBackground() {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        basePainter.fillCanvasBackground(javafx.scene.paint.Color.BLACK);
    }

    private void drawCanvas(ReadableGridModel<ConwayEntity> currentModel, int stepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        fillBackground();

        // Draw all cells
        currentModel.structure()
                    .coordinatesStream()
                    .forEachOrdered(coordinate ->
                            GridEntityUtils.consumeDescriptorAt(
                                    coordinate,
                                    currentModel,
                                    entityDescriptorRegistry,
                                    descriptor ->
                                            basePainter.drawCell(
                                                    coordinate,
                                                    descriptor.colorAsOptional().orElse(Color.BLACK),
                                                    null,
                                                    0.0d)));
    }

}

