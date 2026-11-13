package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult;
import de.mkalb.etpetssim.engine.neighborhood.RadiusRingCell;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CanvasPainters;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.model.LabSimulationManager;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabNeighborhoodHighlights;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import de.mkalb.etpetssim.ui.GridGeometry;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

/**
 * Main view of the Simulation Lab.
 *
 * <p>The dynamic layer shows the highlighted cells and the neighborhood highlights of the selected cell; the overlay
 * shows the selected cell and the cell under the mouse pointer.
 */
public final class LabMainView
        extends AbstractDefaultMainView<
        LabEntity,
        GridCell<LabEntity>,
        WritableGridModel<LabEntity>,
        LabConfig,
        LabStatistics,
        LabSimulationManager,
        NoUserActionContext,
        LabConfigView,
        LabObservationView> {

    private static final String LAB_TOOL_ID_SWITCH_ENTITY = "lab.switchEntity";
    private static final String LAB_TOOLBAR_SWITCH_ENTITY = "lab.toolbar.switchentity";
    private static final String LAB_TOOLBAR_SWITCH_ENTITY_TOOLTIP = "lab.toolbar.switchentity.tooltip";

    private static final Color SELECTED_CELL_COLOR = Color.ROSYBROWN;
    private static final Color MOUSE_HOVER_COLOR = Color.DARKSLATEBLUE;
    private static final Color TEXT_COLOR = Color.DARKSLATEGRAY;
    private static final Color TEXT_COLOR_GRAYSCALE = Color.BLACK;
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Color CANVAS_COLOR = Color.BLACK;
    private static final Color GRID_BACKGROUND_COLOR = Color.DIMGRAY;
    private static final Color TRANSLUCENT_WHITE = FXPaintFactory.adjustColorAlpha(Color.WHITE, 0.2);
    private static final double SELECTED_CELL_LINE_WIDTH = 4.0d;
    private static final double MOUSE_HOVER_LINE_WIDTH = 2.0d;
    private static final double SHAPE_LINE_WIDTH = 0.5d;
    private static final double HOVER_MIN_EDGE_LENGTH = 6.0d;
    private static final double MODEL_FILL_ALPHA = 0.5d;
    private static final int OUTER_HIGHLIGHT_RING_RADIUS = LabNeighborhoodHighlights.RING_MAX_RADIUS;
    private static final int INNER_HIGHLIGHT_RING_RADIUS = 3;
    private static final int HIGHLIGHT_RING_STROKE_SCALE = 2;
    private static final int NEIGHBOR_DIRECTION_TEXT_CAPACITY = 4;
    private static final int CHECKER_GROUP_SIZE = 2;
    private static final int CHECKER_GROUP_SHIFT = 1;

    private static final double ESTIMATED_CELL_STROKE_WIDTH = 1.0d;
    private static final double NEIGHBOR_COORDINATE_STROKE_WIDTH = 2.0d;
    private static final double EDGE_RESULT_STROKE_WIDTH = 3.0d;
    private static final double HOVER_POINTER_RADIUS = 2.0d;

    public LabMainView(DefaultMainViewModel<LabEntity, GridCell<LabEntity>, WritableGridModel<LabEntity>,
                               LabConfig, LabStatistics, LabSimulationManager, NoUserActionContext> viewModel,
                       GridEntityDescriptorRegistry entityDescriptorRegistry,
                       LabConfigView configView,
                       DefaultControlView controlView,
                       LabObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        registerOverlayCanvasEvents();
    }

    private static Color computeCheckerColor(GridCoordinate coordinate) {
        int checkerGroup = computeCheckerGroup(coordinate);
        return switch (checkerGroup) {
            case 0 -> Color.LIGHTSKYBLUE;       // Column 0, Row 0
            case 1 -> Color.LIGHTSTEELBLUE;     // Column 0, Row 1
            case 2 -> Color.PALEGREEN;          // Column 1, Row 0
            case 3 -> Color.MEDIUMAQUAMARINE;   // Column 1, Row 1
            default -> throw new IllegalStateException("checkerGroup must be in [0, 3], but was: " + checkerGroup);
        };
    }

    private static Color computeCheckerGrayscaleColor(GridCoordinate coordinate) {
        int checkerGroup = computeCheckerGroup(coordinate);
        return switch (checkerGroup) {
            case 0 -> Color.WHITE;              // Column 0, Row 0
            case 1 -> Color.LIGHTGRAY;          // Column 0, Row 1
            case 2 -> Color.DARKGRAY;           // Column 1, Row 0
            case 3 -> Color.GRAY;               // Column 1, Row 1
            default -> throw new IllegalStateException("checkerGroup must be in [0, 3], but was: " + checkerGroup);
        };
    }

    private static int computeCheckerGroup(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % CHECKER_GROUP_SIZE;
        int rowGroup = coordinate.y() % CHECKER_GROUP_SIZE;
        return (columnGroup << CHECKER_GROUP_SHIFT) | rowGroup;
    }

    private static boolean isHoverState(SimulationState simulationState) {
        return switch (simulationState) {
            case RUNNING_TIMED, PAUSED, CANCELED, FINISHED -> true;
            case READY, INITIALIZING, RUNNING_BATCH, PAUSING_BATCH, CANCELLING_BATCH, ERROR, SHUTTING_DOWN -> false;
        };
    }

    @Override
    protected void initializeSimulation(CanvasPainters painters, LabConfig config, CellDimension cellDimension) {
        FXGridCanvasPainter basePainter = painters.base();

        boolean colorModeGrayscale = (config.colorMode() == LabColorMode.GRAYSCALE);
        boolean renderingModeCircle = (config.cellDisplayMode() == CellDisplayMode.CIRCLE)
                || (config.cellDisplayMode() == CellDisplayMode.CIRCLE_BORDERED);
        Color textColor = colorModeGrayscale ? TEXT_COLOR_GRAYSCALE : TEXT_COLOR;
        Color strokeColor = config.cellDisplayMode().hasBorder() ? STROKE_COLOR : null;

        drawBaseCanvasBackground(basePainter, colorModeGrayscale);

        basePainter.gridStructure()
                .coordinatesStream()
                .forEachOrdered(coordinate ->
                        drawBaseCanvasCoordinate(basePainter, coordinate, colorModeGrayscale, renderingModeCircle,
                                strokeColor, textColor));
    }

    @Override
    protected void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
                                                 @Nullable GridCell<LabEntity> oldGridCell,
                                                 @Nullable GridCell<LabEntity> newGridCell) {
        painter.clearCanvasBackground();
        drawSelectedCellMarker(painter, newGridCell);

        CanvasPainters currentPainters = painters();
        if ((currentPainters != null) && viewModel.hasSimulationManager()) {
            drawDynamicLayer(currentPainters.dynamic(), viewModel.getCurrentGridModel(), newGridCell);
        }
    }

    @Override
    protected void drawSimulation(CanvasPainters painters,
                                  WritableGridModel<LabEntity> currentGridModel,
                                  int stepCount,
                                  int lastDrawnStepCount) {
        drawDynamicLayer(painters.dynamic(), currentGridModel, viewModel.selectedGridCellProperty().get());
    }

    @Override
    protected List<SimulationUserActionDescriptor<NoUserActionContext>> createUserActionDescriptors() {
        return List.of(new SimulationUserActionDescriptor<>(
                LAB_TOOL_ID_SWITCH_ENTITY,
                NoUserActionContext.NO_CONTEXT,
                SimulationUserActionScope.CELL_SELECTED,
                LAB_TOOLBAR_SWITCH_ENTITY,
                LAB_TOOLBAR_SWITCH_ENTITY_TOOLTIP));
    }

    private void drawBaseCanvasBackground(FXGridCanvasPainter basePainter, boolean colorModeGrayscale) {
        basePainter.fillCanvasBackground(CANVAS_COLOR);
        if (colorModeGrayscale) {
            basePainter.fillGridBackground(Color.WHITE);
        } else {
            basePainter.fillGridBackground(FXPaintFactory.createHorizontalGradient(GRID_BACKGROUND_COLOR.darker(),
                    GRID_BACKGROUND_COLOR.brighter()));
        }
    }

    private void drawBaseCanvasCoordinate(FXGridCanvasPainter basePainter,
                                          GridCoordinate coordinate,
                                          boolean colorModeGrayscale,
                                          boolean renderingModeCircle,
                                          @Nullable Color strokeColor,
                                          Color textColor) {
        Color color = colorModeGrayscale ? computeCheckerGrayscaleColor(coordinate) : computeCheckerColor(coordinate);
        if (renderingModeCircle) {
            basePainter.drawCellInnerCircle(coordinate, color, strokeColor, SHAPE_LINE_WIDTH, StrokeType.CENTERED);
        } else {
            basePainter.drawCell(coordinate, color, strokeColor, SHAPE_LINE_WIDTH);
        }
        if (cellFont != null) {
            basePainter.drawCenteredTextInCell(coordinate, coordinate.toDisplayString(), textColor, cellFont);
        }
    }

    private void drawDynamicLayer(FXGridCanvasPainter dynamicPainter,
                                  ReadableGridModel<LabEntity> currentGridModel,
                                  @Nullable GridCell<LabEntity> selectedGridCell) {
        dynamicPainter.clearCanvasBackground();

        Color fillColor = FXPaintFactory.adjustColorAlpha(Color.RED, MODEL_FILL_ALPHA);
        currentGridModel.nonDefaultCells()
                .forEach(cell -> dynamicPainter.drawCell(cell.coordinate(), fillColor, null, NO_STROKE_LINE_WIDTH));

        if (selectedGridCell != null) {
            drawNeighborhoodHighlights(LabNeighborhoodHighlights.from(
                            selectedGridCell.coordinate(),
                            viewModel.getCurrentConfig().neighborhoodMode(),
                            dynamicPainter.gridStructure(),
                            currentGridModel),
                    dynamicPainter);
        }
    }

    private void drawNeighborhoodHighlights(LabNeighborhoodHighlights highlights,
                                            FXGridCanvasPainter painter) {
        drawRing(highlights.ringCellsByRadius(), OUTER_HIGHLIGHT_RING_RADIUS, Color.GOLD, painter);
        drawRing(highlights.ringCellsByRadius(), INNER_HIGHLIGHT_RING_RADIUS, Color.TOMATO, painter);
        drawNeighborCoordinates(highlights.validNeighborCoordinates(), painter);
        drawNeighborsWithEdgeBehavior(highlights.validNeighborsWithEdgeBehavior(), painter);
        drawNeighborEdgeBehaviorResults(highlights.neighborEdgeBehaviorResults(), painter);
    }

    private void drawRing(SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<GridCell<LabEntity>>>> ringCells,
                          int ringRadius,
                          Color strokeColor,
                          FXGridCanvasPainter painter) {
        ringCells.getOrDefault(ringRadius, Collections.emptySortedMap())
                .values()
                .forEach(ringCell -> painter.drawCellInnerCircle(ringCell.coordinate(),
                        null,
                        strokeColor,
                        HIGHLIGHT_RING_STROKE_SCALE * ringCell.reachedFromPreviousRing().size(),
                        StrokeType.INSIDE));
    }

    private void drawNeighborCoordinates(Collection<GridCoordinate> neighborCoordinates,
                                         FXGridCanvasPainter painter) {
        neighborCoordinates.forEach(neighborCoordinate -> painter.drawCell(neighborCoordinate, null, Color.ORANGE,
                NEIGHBOR_COORDINATE_STROKE_WIDTH));
    }

    private void drawNeighborsWithEdgeBehavior(Map<GridCoordinate,
                                                       List<CellNeighborWithEdgeBehavior>> neighborsWithEdgeBehavior,
                                               FXGridCanvasPainter painter) {
        neighborsWithEdgeBehavior.forEach((neighborCoordinate, neighborCells) ->
                drawNeighborWithEdgeBehavior(neighborCoordinate, neighborCells, painter));
    }

    private void drawNeighborWithEdgeBehavior(GridCoordinate neighborCoordinate,
                                              Collection<CellNeighborWithEdgeBehavior> neighborCells,
                                              FXGridCanvasPainter painter) {
        painter.drawCell(neighborCoordinate, Color.YELLOW, null, NO_STROKE_LINE_WIDTH);
        drawNeighborDirections(neighborCoordinate, neighborCells, painter);
    }

    private void drawNeighborDirections(GridCoordinate coordinate,
                                        Collection<CellNeighborWithEdgeBehavior> neighborCells,
                                        FXGridCanvasPainter painter) {
        if (cellFont == null) {
            return;
        }

        StringBuilder builder = new StringBuilder(NEIGHBOR_DIRECTION_TEXT_CAPACITY);
        for (CellNeighborWithEdgeBehavior neighborCell : neighborCells) {
            if (!builder.isEmpty()) {
                builder.append(" : ");
            }
            builder.append(neighborCell.direction().arrow());
        }
        painter.drawCenteredTextInCell(coordinate, builder.toString(), Color.BLACK, cellFont);
    }

    private void drawNeighborEdgeBehaviorResults(Collection<EdgeBehaviorResult> edgeBehaviorResults,
                                                 FXGridCanvasPainter painter) {
        edgeBehaviorResults.forEach(edgeBehaviorResult -> {
            switch (edgeBehaviorResult.action()) {
                case VALID ->
                        painter.drawCell(edgeBehaviorResult.mapped(), null, Color.DARKORANGE, EDGE_RESULT_STROKE_WIDTH);
                case WRAPPED ->
                        painter.drawCell(edgeBehaviorResult.mapped(), null, Color.DARKRED, EDGE_RESULT_STROKE_WIDTH);
                case BLOCKED ->
                        painter.drawCell(edgeBehaviorResult.mapped(), Color.DARKRED, null, NO_STROKE_LINE_WIDTH);
                case ABSORBED -> {
                    // nothing to render
                }
            }
        });
    }

    private void drawSelectedCellMarker(FXGridCanvasPainter overlayPainter,
                                        @Nullable GridCell<LabEntity> selectedGridCell) {
        if (selectedGridCell != null) {
            overlayPainter.drawCellOuterCircle(selectedGridCell.coordinate(), TRANSLUCENT_WHITE, SELECTED_CELL_COLOR,
                    SELECTED_CELL_LINE_WIDTH, StrokeType.OUTSIDE);
        }
    }

    private void registerOverlayCanvasEvents() {
        overlayCanvas().setOnMouseExited(_ -> findHoverOverlayPainter().ifPresent(this::handleMouseExited));
        overlayCanvas().setOnMouseMoved(event -> findHoverOverlayPainter().ifPresent(overlayPainter ->
                handleMouseMoved(new Point2D(event.getX(), event.getY()), overlayPainter)));
    }

    private Optional<FXGridCanvasPainter> findHoverOverlayPainter() {
        CanvasPainters currentPainters = painters();
        if ((currentPainters == null)
                || !viewModel.hasSimulationManager()
                || !isHoverState(viewModel.getSimulationState())) {
            return Optional.empty();
        }
        return Optional.of(currentPainters.overlay());
    }

    private void handleMouseExited(FXGridCanvasPainter overlayPainter) {
        overlayPainter.clearCanvasBackground();
        drawSelectedCellMarker(overlayPainter, viewModel.selectedGridCellProperty().get());
    }

    private void handleMouseMoved(Point2D mousePoint, FXGridCanvasPainter overlayPainter) {
        overlayPainter.clearCanvasBackground();
        drawEstimatedHoverCoordinate(mousePoint, overlayPainter);
        drawPreciseHoverCoordinate(mousePoint, overlayPainter);
        drawSelectedCellMarker(overlayPainter, viewModel.selectedGridCellProperty().get());
        overlayPainter.drawCircle(mousePoint, HOVER_POINTER_RADIUS, Color.DARKGREEN, null, NO_STROKE_LINE_WIDTH,
                StrokeType.CENTERED);
    }

    private void drawEstimatedHoverCoordinate(Point2D mousePoint, FXGridCanvasPainter overlayPainter) {
        GridCoordinate estimatedCoordinate = GridGeometry.estimateGridCoordinate(
                mousePoint,
                overlayPainter.cellDimension(),
                overlayPainter.gridDimension2D(),
                overlayPainter.gridStructure());
        if (!estimatedCoordinate.isIllegal()) {
            overlayPainter.drawCell(estimatedCoordinate, null, Color.RED, ESTIMATED_CELL_STROKE_WIDTH);
        }
    }

    private void drawPreciseHoverCoordinate(Point2D mousePoint, FXGridCanvasPainter overlayPainter) {
        GridCoordinate coordinate = GridGeometry.fromCanvasPosition(
                mousePoint,
                overlayPainter.cellDimension(),
                overlayPainter.gridDimension2D(),
                overlayPainter.gridStructure());
        if (coordinate.isIllegal() || !overlayPainter.isInsideGrid(coordinate)) {
            return;
        }

        overlayPainter.drawCellBoundingBox(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH,
                StrokeType.OUTSIDE);
        if (overlayPainter.cellDimension().edgeLength() >= HOVER_MIN_EDGE_LENGTH) {
            overlayPainter.drawCellInnerCircle(coordinate, Color.WHITE, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH,
                    StrokeType.INSIDE);
            drawHoverDescriptor(coordinate, overlayPainter);
        }
    }

    private void drawHoverDescriptor(GridCoordinate coordinate, FXGridCanvasPainter overlayPainter) {
        if (cellFont == null) {
            return;
        }
        LabEntity entity = viewModel.getCurrentGridModel().getEntity(coordinate);
        entityDescriptorRegistry.findByDescriptorId(entity.descriptorId())
                .ifPresent(descriptor -> overlayPainter.drawCenteredTextInCell(
                        coordinate, descriptor.shortName(), Color.RED, cellFont));
    }

}
