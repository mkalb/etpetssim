package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayMainView
        extends AbstractDefaultMainView<
        ConwayEntity,
        WritableGridModel<ConwayEntity>,
        ConwayConfig,
        ConwayStatistics,
        ConwayConfigView,
        ConwayObservationView> {

    private static final double ALIVE_BORDER_WIDTH = 1.0d;

    private final Paint backgroundPaint;
    private final Paint alivePaint;
    private final Color aliveBorderColor;

    public ConwayMainView(DefaultMainViewModel<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
                                  ConwayStatistics> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ConwayConfigView configView,
                          DefaultControlView controlView,
                          ConwayObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(ConwayEntity.DEAD.descriptorId())
                .colorAsOptional().orElse(Color.BLACK);
        alivePaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(ConwayEntity.ALIVE.descriptorId())
                .colorAsOptional().orElse(Color.WHITE);
        aliveBorderColor = entityDescriptorRegistry
                .getRequiredByDescriptorId(ConwayEntity.ALIVE.descriptorId())
                .borderColorAsOptional().orElse(Color.GRAY);
    }

    @Override
    protected void initSimulation(ConwayConfig config, CellDimension cellDimension) {
        // Do nothing
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ConwayEntity> oldGridCell,
                                          @Nullable GridCell<ConwayEntity> newGridCell) {
        // Do nothing
    }

    @Override
    protected void drawSimulation(WritableGridModel<ConwayEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundPaint);

        currentModel.nonDefaultCoordinates()
                    .forEach(coordinate ->
                            basePainter.drawCell(
                                    coordinate,
                                    alivePaint,
                                    aliveBorderColor,
                                    ALIVE_BORDER_WIDTH));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}

