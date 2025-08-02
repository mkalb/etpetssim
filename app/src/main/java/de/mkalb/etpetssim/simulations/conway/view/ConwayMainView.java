package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.GridEntityUtils;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.SimulationView;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayMainViewModel;
import de.mkalb.etpetssim.simulations.view.AbstractMainView;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class ConwayMainView extends AbstractMainView<ConwayMainViewModel> implements SimulationView {

    private final ConwayConfigView configView;
    private final ConwayControlView controlView;
    private final ConwayObservationView observationView;

    public ConwayMainView(ConwayMainViewModel viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ConwayConfigView configView,
                          ConwayControlView controlView,
                          ConwayObservationView observationView) {
        super(viewModel, entityDescriptorRegistry);
        this.configView = configView;
        this.observationView = observationView;
        this.controlView = controlView;
    }

    @Override
    public Region buildRegion() {
        Region configRegion = configView.buildRegion();
        Region simulationRegion = createSimulationRegion();
        Region controlRegion = controlView.buildRegion();
        Region observationRegion = observationView.buildRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(configRegion);
        borderPane.setCenter(simulationRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);
        borderPane.getStyleClass().add(FXStyleClasses.VIEW_BORDERPANE);

        registerViewModelListeners();

        return borderPane;
    }

    private void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(() -> {
            double cellEdgeLength = viewModel.getCellEdgeLength();
            ReadableGridModel<ConwayEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
            GridStructure structure = viewModel.getStructure();
            long currentStep = viewModel.getCurrentStep();

            AppLogger.info("Initialize canvas and painter with structure " + structure.toDisplayString() +
                    " and cell edge length " + cellEdgeLength);

            createPainterAndUpdateCanvas(structure, cellEdgeLength);

            updateCanvasBorderPane(structure);

            drawCanvas(currentModel, currentStep);
            observationView.updateObservationLabels();
        });
        viewModel.setSimulationStepListener(() -> {
            ReadableGridModel<ConwayEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
            long currentStep = viewModel.getCurrentStep();
            AppLogger.info("Drawing canvas for step " + currentStep);

            drawCanvas(currentModel, currentStep);
            observationView.updateObservationLabels();
        });
    }

    private void drawCanvas(ReadableGridModel<ConwayEntity> currentModel, long currentStep) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        // Fill background
        basePainter.fillCanvasBackground(javafx.scene.paint.Color.BLACK);
        basePainter.fillGridBackground(javafx.scene.paint.Color.WHITE);

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

