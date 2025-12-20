package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarConfig;
import de.mkalb.etpetssim.simulations.sugar.model.SugarGridModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SugarMainView
        extends AbstractDefaultMainView<
        SugarEntity,
        SugarGridModel,
        SugarConfig,
        SugarStatistics,
        SugarConfigView,
        SugarObservationView> {

    private static final Color FALLBACK_COLOR_SUGAR = Color.WHITE;
    private static final Color FALLBACK_COLOR_AGENT = Color.BLUE;
    private static final double SUGAR_MAX_FACTOR_DELTA = 0.4d;
    private static final double AGENT_MAX_FACTOR_DELTA = 0.6d;
    private static final int AGENT_GROUP_COUNT = 7;
    private static final int MAX_COLOR_AGENT_ENERGY_FACTOR = 2;

    private final Paint backgroundPaint;
    private final Map<String, @Nullable Map<Integer, Color>> entityColors;
    private @Nullable CellDrawer<SugarResourceEntity> cellResourceDrawer;
    private @Nullable CellDrawer<SugarAgentEntity> cellAgentDrawer;

    private int maxColorAgentEnergy = 1;

    public SugarMainView(DefaultMainViewModel<SugarEntity, SugarGridModel, SugarConfig, SugarStatistics> viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         SugarConfigView configView,
                         DefaultControlView controlView,
                         SugarObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(SugarEntity.DESCRIPTOR_ID_TERRAIN)
                .colorAsOptional().orElse(Color.BLACK);
        entityColors = HashMap.newHashMap(2);
        entityColors.put(SugarEntity.DESCRIPTOR_ID_RESOURCE_SUGAR, null);
        entityColors.put(SugarEntity.DESCRIPTOR_ID_AGENT, null);
    }

    private int computeMaxColorAgentEnergy(SugarConfig config) {
        return config.agentInitialEnergy() * MAX_COLOR_AGENT_ENERGY_FACTOR;
    }

    @Override
    protected void initSimulation(SugarConfig config, CellDimension cellDimension) {
        maxColorAgentEnergy = computeMaxColorAgentEnergy(config);
        entityColors.put(SugarEntity.DESCRIPTOR_ID_RESOURCE_SUGAR,
                computeBrightnessVariantsMap(entityDescriptorRegistry.getRequiredByDescriptorId(SugarEntity.DESCRIPTOR_ID_RESOURCE_SUGAR),
                        1, config.maxSugarAmount(), config.maxSugarAmount(), SUGAR_MAX_FACTOR_DELTA));
        entityColors.put(SugarEntity.DESCRIPTOR_ID_AGENT,
                computeBrightnessVariantsMap(entityDescriptorRegistry.getRequiredByDescriptorId(SugarEntity.DESCRIPTOR_ID_AGENT),
                        1, maxColorAgentEnergy, AGENT_GROUP_COUNT, AGENT_MAX_FACTOR_DELTA));

        cellResourceDrawer = (descriptor, painter, cell, _) ->
                painter.drawCell(
                        cell.coordinate(),
                        resolveResourceFillColor(descriptor, cell.entity()),
                        null,
                        0.0d);

        cellAgentDrawer = (descriptor, painter, cell, stepCount) -> {
            if ((stepCount > 0)
                    && (cell.entity() instanceof SugarAgent agent)
                    && (agent.stepIndexOfSpawn() == (stepCount - 1))) {
                // draw newly spawned agents with a white border (not for stepCount == 0, as all agents are new then)
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        resolveAgentFillColor(descriptor, cell.entity()),
                        Color.WHITE,
                        1.0d,
                        StrokeType.CENTERED);
            } else {
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        resolveAgentFillColor(descriptor, cell.entity()),
                        null,
                        0.0d,
                        StrokeType.CENTERED);
            }
        };
    }

    private Paint resolveResourceFillColor(GridEntityDescriptor entityDescriptor,
                                           SugarResourceEntity entity) {
        Paint paint = entityDescriptor.color();
        if (paint instanceof Color baseColor) {
            Map<Integer, Color> colorMap = entityColors.get(entityDescriptor.descriptorId());
            if (colorMap != null) {
                Integer value = switch (entity) {
                    case SugarResourceSugar sugar -> sugar.currentAmount();
                    case SugarResourceNone _ -> -1;
                };

                return colorMap.getOrDefault(value, baseColor);
            }
        } else if (paint == null) {
            paint = FALLBACK_COLOR_SUGAR;
        }
        return paint;
    }

    private Paint resolveAgentFillColor(GridEntityDescriptor entityDescriptor,
                                        SugarAgentEntity entity) {
        Paint paint = entityDescriptor.color();
        if (paint instanceof Color baseColor) {
            Map<Integer, Color> colorMap = entityColors.get(entityDescriptor.descriptorId());
            if (colorMap != null) {
                Integer value = switch (entity) {
                    case SugarAgent agent -> Math.min(maxColorAgentEnergy, agent.currentEnergy());
                    case SugarAgentNone _ -> -1;
                };

                return colorMap.getOrDefault(value, baseColor);
            }
        } else if (paint == null) {
            paint = FALLBACK_COLOR_AGENT;
        }
        return paint;
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<SugarEntity> oldGridCell,
                                          @Nullable GridCell<SugarEntity> newGridCell) {
        // Do nothing
    }

    @Override
    protected void drawSimulation(SugarGridModel currentModel, int stepCount, int lastDrawnStepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellResourceDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }
        if (cellAgentDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
        }

        basePainter.fillCanvasBackground(backgroundPaint);

        ReadableGridModel<SugarResourceEntity> resourceModel = currentModel.resourceModel();
        ReadableGridModel<SugarAgentEntity> agentModel = currentModel.agentModel();

        resourceModel.nonDefaultCells()
                     .forEachOrdered(resourceCell -> cellResourceDrawer.draw(
                             entityDescriptorRegistry.getRequiredByDescriptorId(resourceCell.entity().descriptorId()),
                             basePainter, resourceCell, stepCount));

        agentModel.nonDefaultCells()
                  .forEachOrdered(agentCell -> cellAgentDrawer.draw(
                          entityDescriptorRegistry.getRequiredByDescriptorId(agentCell.entity().descriptorId()),
                          basePainter, agentCell, stepCount));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}
