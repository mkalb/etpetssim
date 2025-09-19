package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.CompositeGridModel;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LangtonMainView
        extends AbstractDefaultMainView<
        LangtonEntity,
        CompositeGridModel<LangtonEntity>,
        LangtonConfig,
        LangtonStatistics,
        LangtonConfigView,
        LangtonObservationView> {

    private final Paint backgroundPaint;

    public LangtonMainView(DefaultMainViewModel<LangtonEntity, CompositeGridModel<LangtonEntity>, LangtonConfig, LangtonStatistics> viewModel,
                           GridEntityDescriptorRegistry entityDescriptorRegistry,
                           LangtonConfigView configView,
                           DefaultControlView controlView,
                           LangtonObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(LangtonGroundEntity.UNVISITED.descriptorId())
                .colorAsOptional().orElse(Color.WHITE);
    }

    @Override
    protected void initSimulation(LangtonConfig config, CellDimension cellDimension) {
        // Do nothing
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<LangtonEntity> oldGridCell,
                                          @Nullable GridCell<LangtonEntity> newGridCell) {
        // Do nothing
    }

    @Override
    protected void drawSimulation(CompositeGridModel<LangtonEntity> currentModel, int stepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        // TODO draw background only at beginning?
        basePainter.fillCanvasBackground(backgroundPaint);

        // TODO implement drawSimulation
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}

