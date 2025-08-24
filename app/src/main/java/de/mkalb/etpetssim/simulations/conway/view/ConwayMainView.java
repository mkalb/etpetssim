package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public final class ConwayMainView
        extends AbstractDefaultMainView<
        ConwayEntity,
        ConwayConfig,
        ConwayStatistics,
        ConwayConfigView,
        ConwayObservationView> {

    private final Paint backgroundPaint;
    private final Paint alivePaint;

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
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(ConwayEntity.DEAD.descriptorId())
                .colorAsOptional().orElse(Color.BLACK);
        alivePaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(ConwayEntity.ALIVE.descriptorId())
                .colorAsOptional().orElse(Color.WHITE);
    }

    @Override
    protected void initSimulation(ConwayConfig config) {
        // Do nothing
    }

    @Override
    protected void drawSimulation(ReadableGridModel<ConwayEntity> currentModel, int stepCount) {
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
                                    null,
                                    0.0d));
    }

}

