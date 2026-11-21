package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.core.AppLocalization;
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
import de.mkalb.etpetssim.simulations.core.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.core.view.CanvasPainters;
import de.mkalb.etpetssim.simulations.core.view.ControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.lab.model.LabConfig;
import de.mkalb.etpetssim.simulations.lab.model.LabSimulationManager;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import de.mkalb.etpetssim.simulations.lab.shared.LabBaseLayer;
import de.mkalb.etpetssim.simulations.lab.shared.LabColorMode;
import de.mkalb.etpetssim.simulations.lab.viewmodel.LabNeighborhoodHighlights;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.CellShapeSide;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import de.mkalb.etpetssim.ui.GridGeometry;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.SortedMap;

/**
 * Main view of the Simulation Lab.
 *
 * <p>The base layer shows the configured {@link LabBaseLayer}; the dynamic layer shows the highlighted cells and the
 * neighborhood highlights of the selected cell; the overlay shows the selected cell, the cell under the mouse pointer,
 * and its distance from the selected cell.
 */
public final class LabMainView
        extends AbstractMainView<
        LabEntity,
        GridCell<LabEntity>,
        WritableGridModel<LabEntity>,
        LabConfig,
        LabStatistics,
        LabSimulationManager,
        NoUserActionContext,
        LabObservationView> {

    // Edit toolbar
    private static final String LAB_TOOL_ID_SWITCH_ENTITY = "lab.switchEntity";
    private static final String LAB_TOOLBAR_SWITCH_ENTITY = "lab.toolbar.switchentity";
    private static final String LAB_TOOLBAR_SWITCH_ENTITY_TOOLTIP = "lab.toolbar.switchentity.tooltip";

    // Base layer: checkerboard with the coordinates
    private static final Color CANVAS_COLOR = Color.BLACK;
    private static final Color GRID_BACKGROUND_COLOR = Color.DIMGRAY;
    private static final Color GRID_BACKGROUND_COLOR_GRAYSCALE = Color.WHITE;
    private static final Color CHECKER_STROKE_COLOR = Color.BLACK;
    private static final double CHECKER_LINE_WIDTH = 0.5d;
    private static final int CHECKER_GROUP_SIZE = 2;
    private static final int CHECKER_GROUP_SHIFT = 1;
    private static final Color COORDINATE_TEXT_COLOR = Color.DARKSLATEGRAY;
    private static final Color COORDINATE_TEXT_COLOR_GRAYSCALE = Color.BLACK;

    // Base layer: painter test with numbered drawing variants
    private static final Color PAINTER_TEST_FILL_COLOR = Color.LIGHTSKYBLUE;
    private static final Color PAINTER_TEST_FILL_COLOR_GRAYSCALE = Color.LIGHTGRAY;
    private static final Color PAINTER_TEST_STROKE_COLOR = Color.DARKBLUE;
    private static final Color PAINTER_TEST_STROKE_COLOR_GRAYSCALE = Color.BLACK;
    private static final Color PAINTER_TEST_ACCENT_COLOR = Color.ORANGERED;
    private static final Color PAINTER_TEST_ACCENT_COLOR_GRAYSCALE = Color.DIMGRAY;
    private static final Color PAINTER_TEST_OUTLINE_COLOR = Color.GRAY;
    private static final double PAINTER_TEST_OUTLINE_LINE_WIDTH = 0.5d;
    private static final double PAINTER_TEST_LINE_WIDTH = 3.0d;
    private static final double PAINTER_TEST_SCALE = 0.6d;
    private static final List<StrokeType> PAINTER_TEST_STROKE_TYPES = List.of(StrokeType.INSIDE,
            StrokeType.OUTSIDE, StrokeType.CENTERED);
    private static final String PAINTER_TEST_TEXT = "Aa";
    private static final String PAINTER_TEST_POINT_TEXT = "+";
    private static final double PAINTER_TEST_NUMBER_OFFSET_FACTOR = 0.6d;

    // Dynamic layer: highlighted cells and neighborhood highlights of the selected cell
    private static final Color HIGHLIGHTED_CELL_COLOR = FXPaintFactory.adjustColorAlpha(Color.RED, 0.5);
    private static final int OUTER_RING_RADIUS = LabNeighborhoodHighlights.RING_MAX_RADIUS;
    private static final Color OUTER_RING_COLOR = Color.GOLD;
    private static final int INNER_RING_RADIUS = 3;
    private static final Color INNER_RING_COLOR = Color.TOMATO;
    private static final int RING_LINE_WIDTH_SCALE = 2;
    private static final Color NEIGHBOR_COORDINATE_COLOR = Color.ORANGE;
    private static final double NEIGHBOR_COORDINATE_LINE_WIDTH = 2.0d;
    private static final Color NEIGHBOR_EDGE_FILL_COLOR = Color.YELLOW;
    private static final Color NEIGHBOR_VERTEX_FILL_COLOR = Color.GOLDENROD;
    private static final double NEIGHBOR_VERTEX_SCALE = 0.6d;
    private static final Color NEIGHBOR_DIRECTION_TEXT_COLOR = Color.BLACK;
    private static final int NEIGHBOR_DIRECTION_TEXT_CAPACITY = 4;
    private static final Color EDGE_RESULT_VALID_COLOR = Color.DARKORANGE;
    private static final Color EDGE_RESULT_WRAPPED_COLOR = Color.DARKRED;
    private static final Color EDGE_RESULT_BLOCKED_COLOR = Color.DARKRED;
    private static final double EDGE_RESULT_LINE_WIDTH = 3.0d;

    // Overlay: selected cell and hover
    private static final Color SELECTED_CELL_COLOR = Color.ROSYBROWN;
    private static final Color SELECTED_CELL_FILL_COLOR = FXPaintFactory.adjustColorAlpha(Color.WHITE, 0.2);
    private static final double SELECTED_CELL_LINE_WIDTH = 4.0d;
    private static final Color HOVER_ESTIMATED_CELL_COLOR = Color.RED;
    private static final double HOVER_ESTIMATED_CELL_LINE_WIDTH = 1.0d;
    private static final Color HOVER_CELL_COLOR = Color.DARKSLATEBLUE;
    private static final Color HOVER_CELL_FILL_COLOR = Color.WHITE;
    private static final double HOVER_CELL_LINE_WIDTH = 2.0d;
    private static final double HOVER_CELL_MIN_EDGE_LENGTH = 6.0d;
    private static final Color HOVER_TEXT_COLOR = Color.RED;
    private static final Color HOVER_POINTER_COLOR = Color.DARKGREEN;
    private static final double HOVER_POINTER_RADIUS = 2.0d;
    private static final String LAB_HOVER_DISTANCE = "lab.hover.distance";
    private static final String HOVER_RING_BEYOND_MAX_PREFIX = "> ";
    private static final Color HOVER_DISTANCE_TEXT_COLOR = Color.WHITE;
    private static final Color HOVER_DISTANCE_BACKGROUND_COLOR = FXPaintFactory.adjustColorAlpha(Color.BLACK, 0.7);
    private static final double HOVER_DISTANCE_FONT_SIZE = 12.0d;
    private static final double HOVER_DISTANCE_PADDING = 6.0d;
    private static final double HOVER_DISTANCE_OFFSET_Y = 16.0d;

    public LabMainView(MainViewModel<LabEntity, GridCell<LabEntity>, WritableGridModel<LabEntity>,
                               LabConfig, LabStatistics, LabSimulationManager, NoUserActionContext> viewModel,
                       GridEntityDescriptorRegistry entityDescriptorRegistry,
                       LabConfigView configView,
                       ControlView controlView,
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

    private static Rectangle2D computeCellBounds(FXGridCanvasPainter painter, GridCoordinate coordinate) {
        return GridGeometry.computeCellBounds(coordinate, painter.cellDimension(),
                painter.gridStructure().cellShape());
    }

    private static void drawPainterTestTextInCell(FXGridCanvasPainter painter,
                                                  GridCoordinate coordinate,
                                                  Color textColor,
                                                  @Nullable Font font) {
        if (font != null) {
            painter.drawCenteredTextInCell(coordinate, PAINTER_TEST_TEXT, textColor, font);
        }
    }

    private static void drawPainterTestTextAtPoint(FXGridCanvasPainter painter,
                                                   GridCoordinate coordinate,
                                                   Color textColor,
                                                   @Nullable Font font) {
        if (font != null) {
            Rectangle2D bounds = computeCellBounds(painter, coordinate);
            Point2D point = new Point2D(bounds.getMinX() + (bounds.getWidth() / 4.0d),
                    bounds.getMinY() + (bounds.getHeight() / 4.0d));
            painter.drawCenteredTextAt(point, PAINTER_TEST_POINT_TEXT, textColor, font);
        }
    }

    private static void drawPainterTestPolygon(FXGridCanvasPainter painter,
                                               GridCoordinate coordinate,
                                               Color fill,
                                               Color stroke) {
        Rectangle2D bounds = computeCellBounds(painter, coordinate);
        double midX = bounds.getMinX() + (bounds.getWidth() / 2.0d);
        double midY = bounds.getMinY() + (bounds.getHeight() / 2.0d);
        painter.drawPolygon(
                new double[]{midX, bounds.getMaxX(), midX, bounds.getMinX()},
                new double[]{bounds.getMinY(), midY, bounds.getMaxY(), midY},
                fill, stroke, PAINTER_TEST_LINE_WIDTH);
    }

    private static void drawPainterTestPolyline(FXGridCanvasPainter painter, GridCoordinate coordinate, Color stroke) {
        Rectangle2D bounds = computeCellBounds(painter, coordinate);
        double quarterWidth = bounds.getWidth() / 4.0d;
        painter.drawPolyline(
                new double[]{bounds.getMinX(), bounds.getMinX() + quarterWidth, bounds.getMinX() + (2 * quarterWidth),
                        bounds.getMinX() + (3 * quarterWidth), bounds.getMaxX()},
                new double[]{bounds.getMaxY(), bounds.getMinY(), bounds.getMaxY(), bounds.getMinY(),
                        bounds.getMaxY()},
                stroke, PAINTER_TEST_LINE_WIDTH);
    }

    /**
     * Draws one pixel per step along a diagonal of the cell bounds: from the top left to the bottom right with
     * {@code drawPixelDirect}, or from the top right to the bottom left with {@code drawPixelRect}.
     *
     * @param painter      the painter of the base layer
     * @param coordinate   the coordinate of the cell
     * @param color        the pixel color
     * @param asRectangles {@code true} to draw the pixels as rectangles, {@code false} to write them directly
     */
    private static void drawPainterTestPixels(FXGridCanvasPainter painter,
                                              GridCoordinate coordinate,
                                              Color color,
                                              boolean asRectangles) {
        Rectangle2D bounds = computeCellBounds(painter, coordinate);
        int left = Math.toIntExact(Math.round(bounds.getMinX()));
        int top = Math.toIntExact(Math.round(bounds.getMinY()));
        int width = Math.toIntExact(Math.round(bounds.getWidth()));
        int height = Math.toIntExact(Math.round(bounds.getHeight()));
        int steps = Math.min(width, height);
        for (int step = 0; step < steps; step++) {
            int dx = (step * width) / steps;
            int y = top + ((step * height) / steps);
            if (asRectangles) {
                painter.drawPixelRect((left + width) - 1 - dx, y, color);
            } else {
                painter.drawPixelDirect(left + dx, y, color);
            }
        }
    }

    @Override
    protected void initializeSimulation(CanvasPainters painters, LabConfig config, CellDimension cellDimension) {
        FXGridCanvasPainter basePainter = painters.base();

        boolean colorModeGrayscale = (config.colorMode() == LabColorMode.GRAYSCALE);
        Color textColor = colorModeGrayscale ? COORDINATE_TEXT_COLOR_GRAYSCALE : COORDINATE_TEXT_COLOR;

        drawBaseCanvasBackground(basePainter, colorModeGrayscale);

        switch (config.baseLayer()) {
            case CHECKERBOARD -> drawCheckerboard(basePainter, config, colorModeGrayscale, textColor);
            case PAINTER_TEST -> drawPainterTest(basePainter, colorModeGrayscale, textColor);
            case NONE -> {
                // nothing to render
            }
        }
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
                SimulationUserActionScope.SELECTED_CELL,
                LAB_TOOLBAR_SWITCH_ENTITY,
                LAB_TOOLBAR_SWITCH_ENTITY_TOOLTIP));
    }

    private void drawBaseCanvasBackground(FXGridCanvasPainter basePainter, boolean colorModeGrayscale) {
        basePainter.fillCanvasBackground(CANVAS_COLOR);
        if (colorModeGrayscale) {
            basePainter.fillGridBackground(GRID_BACKGROUND_COLOR_GRAYSCALE);
        } else {
            basePainter.fillGridBackground(FXPaintFactory.createHorizontalGradient(GRID_BACKGROUND_COLOR.darker(),
                    GRID_BACKGROUND_COLOR.brighter()));
        }
    }

    private void drawCheckerboard(FXGridCanvasPainter basePainter,
                                  LabConfig config,
                                  boolean colorModeGrayscale,
                                  Color textColor) {
        boolean renderingModeCircle = (config.cellDisplayMode() == CellDisplayMode.CIRCLE)
                || (config.cellDisplayMode() == CellDisplayMode.CIRCLE_BORDERED);
        Color strokeColor = config.cellDisplayMode().hasBorder() ? CHECKER_STROKE_COLOR : null;

        basePainter.gridStructure()
                .coordinatesStream()
                .forEachOrdered(coordinate ->
                        drawBaseCanvasCoordinate(basePainter, coordinate, colorModeGrayscale, renderingModeCircle,
                                strokeColor, textColor));
    }

    private void drawBaseCanvasCoordinate(FXGridCanvasPainter basePainter,
                                          GridCoordinate coordinate,
                                          boolean colorModeGrayscale,
                                          boolean renderingModeCircle,
                                          @Nullable Color strokeColor,
                                          Color textColor) {
        Color color = colorModeGrayscale ? computeCheckerGrayscaleColor(coordinate) : computeCheckerColor(coordinate);
        if (renderingModeCircle) {
            basePainter.drawCellInnerCircle(coordinate, color, strokeColor, CHECKER_LINE_WIDTH, StrokeType.CENTERED);
        } else {
            basePainter.drawCell(coordinate, color, strokeColor, CHECKER_LINE_WIDTH);
        }
        if (cellFont != null) {
            basePainter.drawCenteredTextInCell(coordinate, coordinate.toDisplayString(), textColor, cellFont);
        }
    }

    /**
     * Draws the painter test: cell <i>i</i> in row-major order shows the drawing variant <i>i</i> modulo the number of
     * variants on top of a thin cell outline, with the number of the variant below its center.
     *
     * @param basePainter        the painter of the base layer
     * @param colorModeGrayscale whether the colors are drawn in grayscale
     * @param textColor          the color of the variant numbers
     */
    private void drawPainterTest(FXGridCanvasPainter basePainter, boolean colorModeGrayscale, Color textColor) {
        List<PainterTestVariant> variants = createPainterTestVariants(
                colorModeGrayscale ? PAINTER_TEST_FILL_COLOR_GRAYSCALE : PAINTER_TEST_FILL_COLOR,
                colorModeGrayscale ? PAINTER_TEST_STROKE_COLOR_GRAYSCALE : PAINTER_TEST_STROKE_COLOR,
                colorModeGrayscale ? PAINTER_TEST_ACCENT_COLOR_GRAYSCALE : PAINTER_TEST_ACCENT_COLOR);
        int gridWidth = basePainter.gridStructure().size().width();
        basePainter.gridStructure()
                .coordinatesStream()
                .forEachOrdered(coordinate -> drawPainterTestCell(basePainter, coordinate,
                        ((coordinate.y() * gridWidth) + coordinate.x()) % variants.size(), variants, textColor));
    }

    private void drawPainterTestCell(FXGridCanvasPainter basePainter,
                                     GridCoordinate coordinate,
                                     int variantNumber,
                                     List<PainterTestVariant> variants,
                                     Color textColor) {
        basePainter.drawCell(coordinate, null, PAINTER_TEST_OUTLINE_COLOR, PAINTER_TEST_OUTLINE_LINE_WIDTH);
        variants.get(variantNumber).draw(basePainter, coordinate);
        if (cellFont != null) {
            CellDimension cellDimension = basePainter.cellDimension();
            Point2D center = GridGeometry.computeCellCenter(coordinate, cellDimension,
                    basePainter.gridStructure().cellShape());
            basePainter.drawCenteredTextAt(
                    center.add(0.0d, cellDimension.innerRadius() * PAINTER_TEST_NUMBER_OFFSET_FACTOR),
                    String.valueOf(variantNumber), textColor, cellFont);
        }
    }

    /**
     * Creates the numbered drawing variants of the painter test; the list index is the number of a variant.
     *
     * <p>Order: the bounding box, the inner circle, and the outer circle, each with the stroke types {@code INSIDE},
     * {@code OUTSIDE}, and {@code CENTERED} without and with fill; a scaled cell; a triangle and a hexagon matching the
     * cell width; the frame segment of every {@link CellShapeSide}; text in the cell and at a point; a polygon, a
     * polyline, and pixels drawn directly and as rectangles within the cell bounds; the horizontal, vertical, and
     * radial gradients of {@link FXPaintFactory}.
     *
     * @param fill   the fill color
     * @param stroke the stroke and text color
     * @param accent the second color of the gradients and the pixel color
     * @return the drawing variants in their fixed order
     */
    private List<PainterTestVariant> createPainterTestVariants(Color fill, Color stroke, Color accent) {
        Font font = cellFont;
        double width = PAINTER_TEST_LINE_WIDTH;
        List<PainterTestVariant> variants = new ArrayList<>();
        for (StrokeType strokeType : PAINTER_TEST_STROKE_TYPES) {
            variants.add((p, c) -> p.drawCellBoundingBox(c, null, stroke, width, strokeType));
            variants.add((p, c) -> p.drawCellBoundingBox(c, fill, stroke, width, strokeType));
        }
        for (StrokeType strokeType : PAINTER_TEST_STROKE_TYPES) {
            variants.add((p, c) -> p.drawCellInnerCircle(c, null, stroke, width, strokeType));
            variants.add((p, c) -> p.drawCellInnerCircle(c, fill, stroke, width, strokeType));
        }
        for (StrokeType strokeType : PAINTER_TEST_STROKE_TYPES) {
            variants.add((p, c) -> p.drawCellOuterCircle(c, null, stroke, width, strokeType));
            variants.add((p, c) -> p.drawCellOuterCircle(c, fill, stroke, width, strokeType));
        }
        variants.add((p, c) -> p.drawScaledCell(c, fill, stroke, width, PAINTER_TEST_SCALE));
        variants.add((p, c) -> p.drawTriangleMatchingCellWidth(c, fill, stroke, width));
        variants.add((p, c) -> p.drawHexagonMatchingCellWidth(c, fill, stroke, width));
        for (CellShapeSide side : CellShapeSide.values()) {
            variants.add((p, c) -> p.drawCellFrameSegment(c, stroke, width, side));
        }
        variants.add((p, c) -> drawPainterTestTextInCell(p, c, stroke, font));
        variants.add((p, c) -> drawPainterTestTextAtPoint(p, c, stroke, font));
        variants.add((p, c) -> drawPainterTestPolygon(p, c, fill, stroke));
        variants.add((p, c) -> drawPainterTestPolyline(p, c, stroke));
        variants.add((p, c) -> drawPainterTestPixels(p, c, accent, false));
        variants.add((p, c) -> drawPainterTestPixels(p, c, accent, true));
        variants.add((p, c) -> p.drawCell(c, FXPaintFactory.createHorizontalGradient(fill, accent), null,
                NO_STROKE_LINE_WIDTH));
        variants.add((p, c) -> p.drawCell(c, FXPaintFactory.createVerticalGradient(fill, accent), null,
                NO_STROKE_LINE_WIDTH));
        variants.add((p, c) -> p.drawCell(c, FXPaintFactory.createRadialGradient(fill, accent), null,
                NO_STROKE_LINE_WIDTH));
        return List.copyOf(variants);
    }

    private void drawDynamicLayer(FXGridCanvasPainter dynamicPainter,
                                  ReadableGridModel<LabEntity> currentGridModel,
                                  @Nullable GridCell<LabEntity> selectedGridCell) {
        dynamicPainter.clearCanvasBackground();

        currentGridModel.nonDefaultCells()
                .forEach(cell -> dynamicPainter.drawCell(cell.coordinate(), HIGHLIGHTED_CELL_COLOR, null,
                        NO_STROKE_LINE_WIDTH));

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
        drawRing(highlights.ringCellsByRadius(), OUTER_RING_RADIUS, OUTER_RING_COLOR, painter);
        drawRing(highlights.ringCellsByRadius(), INNER_RING_RADIUS, INNER_RING_COLOR, painter);
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
                        RING_LINE_WIDTH_SCALE * ringCell.reachedFromPreviousRing().size(),
                        StrokeType.INSIDE));
    }

    private void drawNeighborCoordinates(Collection<GridCoordinate> neighborCoordinates,
                                         FXGridCanvasPainter painter) {
        neighborCoordinates.forEach(neighborCoordinate -> painter.drawCell(neighborCoordinate, null,
                NEIGHBOR_COORDINATE_COLOR, NEIGHBOR_COORDINATE_LINE_WIDTH));
    }

    private void drawNeighborsWithEdgeBehavior(Map<GridCoordinate,
                                                       List<CellNeighborWithEdgeBehavior>> neighborsWithEdgeBehavior,
                                               FXGridCanvasPainter painter) {
        neighborsWithEdgeBehavior.forEach((neighborCoordinate, neighborCells) ->
                drawNeighborWithEdgeBehavior(neighborCoordinate, neighborCells, painter));
    }

    /**
     * Draws a direct neighbor of the selected cell by its connection type: a neighbor connected by an edge is filled
     * completely, a neighbor connected by a vertex in a smaller, darker shape; a neighbor that is connected in both
     * ways (possible with wrapping on small grids) shows both.
     *
     * @param neighborCoordinate the coordinate of the neighbor within the grid
     * @param neighborCells      the neighbor relations of the selected cell to this coordinate
     * @param painter            the painter of the dynamic layer
     */
    private void drawNeighborWithEdgeBehavior(GridCoordinate neighborCoordinate,
                                              Collection<CellNeighborWithEdgeBehavior> neighborCells,
                                              FXGridCanvasPainter painter) {
        if (neighborCells.stream().anyMatch(CellNeighborWithEdgeBehavior::isEdgeConnection)) {
            painter.drawCell(neighborCoordinate, NEIGHBOR_EDGE_FILL_COLOR, null, NO_STROKE_LINE_WIDTH);
        }
        if (neighborCells.stream().anyMatch(CellNeighborWithEdgeBehavior::isVertexConnection)) {
            painter.drawScaledCell(neighborCoordinate, NEIGHBOR_VERTEX_FILL_COLOR, null, NO_STROKE_LINE_WIDTH,
                    NEIGHBOR_VERTEX_SCALE);
        }
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
        painter.drawCenteredTextInCell(coordinate, builder.toString(), NEIGHBOR_DIRECTION_TEXT_COLOR, cellFont);
    }

    private void drawNeighborEdgeBehaviorResults(Collection<EdgeBehaviorResult> edgeBehaviorResults,
                                                 FXGridCanvasPainter painter) {
        edgeBehaviorResults.forEach(edgeBehaviorResult -> {
            switch (edgeBehaviorResult.action()) {
                case VALID -> painter.drawCell(edgeBehaviorResult.mapped(), null, EDGE_RESULT_VALID_COLOR,
                        EDGE_RESULT_LINE_WIDTH);
                case WRAPPED -> painter.drawCell(edgeBehaviorResult.mapped(), null, EDGE_RESULT_WRAPPED_COLOR,
                        EDGE_RESULT_LINE_WIDTH);
                case BLOCKED -> painter.drawCell(edgeBehaviorResult.mapped(), EDGE_RESULT_BLOCKED_COLOR, null,
                        NO_STROKE_LINE_WIDTH);
                case ABSORBED -> {
                    // nothing to render
                }
            }
        });
    }

    private void drawSelectedCellMarker(FXGridCanvasPainter overlayPainter,
                                        @Nullable GridCell<LabEntity> selectedGridCell) {
        if (selectedGridCell != null) {
            overlayPainter.drawCellOuterCircle(selectedGridCell.coordinate(), SELECTED_CELL_FILL_COLOR,
                    SELECTED_CELL_COLOR, SELECTED_CELL_LINE_WIDTH, StrokeType.OUTSIDE);
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
        overlayPainter.drawCircle(mousePoint, HOVER_POINTER_RADIUS, HOVER_POINTER_COLOR, null, NO_STROKE_LINE_WIDTH,
                StrokeType.CENTERED);
        drawHoverDistance(mousePoint, overlayPainter);
    }

    private void drawEstimatedHoverCoordinate(Point2D mousePoint, FXGridCanvasPainter overlayPainter) {
        GridCoordinate estimatedCoordinate = GridGeometry.estimateGridCoordinate(
                mousePoint,
                overlayPainter.cellDimension(),
                overlayPainter.gridDimension2D(),
                overlayPainter.gridStructure());
        if (!estimatedCoordinate.isIllegal()) {
            overlayPainter.drawCell(estimatedCoordinate, null, HOVER_ESTIMATED_CELL_COLOR,
                    HOVER_ESTIMATED_CELL_LINE_WIDTH);
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

        overlayPainter.drawCellBoundingBox(coordinate, null, HOVER_CELL_COLOR, HOVER_CELL_LINE_WIDTH,
                StrokeType.OUTSIDE);
        if (overlayPainter.cellDimension().edgeLength() >= HOVER_CELL_MIN_EDGE_LENGTH) {
            overlayPainter.drawCellInnerCircle(coordinate, HOVER_CELL_FILL_COLOR, HOVER_CELL_COLOR,
                    HOVER_CELL_LINE_WIDTH, StrokeType.INSIDE);
            drawHoverDescriptor(coordinate, overlayPainter);
        }
    }

    /**
     * Draws the ring distance and the coordinate difference from the selected cell to the cell under the mouse
     * pointer, above the pointer; nothing is drawn without a selected cell or outside the grid.
     *
     * @param mousePoint     the position of the mouse pointer on the canvas
     * @param overlayPainter the painter of the overlay
     */
    private void drawHoverDistance(Point2D mousePoint, FXGridCanvasPainter overlayPainter) {
        GridCell<LabEntity> selectedGridCell = viewModel.selectedGridCellProperty().get();
        if (selectedGridCell == null) {
            return;
        }
        GridCoordinate hoverCoordinate = GridGeometry.fromCanvasPosition(
                mousePoint,
                overlayPainter.cellDimension(),
                overlayPainter.gridDimension2D(),
                overlayPainter.gridStructure());
        if (hoverCoordinate.isIllegal() || !overlayPainter.isInsideGrid(hoverCoordinate)) {
            return;
        }

        OptionalInt ringRadius = LabNeighborhoodHighlights.from(
                        selectedGridCell.coordinate(),
                        viewModel.getCurrentConfig().neighborhoodMode(),
                        overlayPainter.gridStructure(),
                        viewModel.getCurrentGridModel())
                .findRingRadius(hoverCoordinate);
        String ringText = ringRadius.isPresent() ? String.valueOf(ringRadius.getAsInt())
                : (HOVER_RING_BEYOND_MAX_PREFIX + LabNeighborhoodHighlights.RING_MAX_RADIUS);
        String text = AppLocalization.getFormattedText(LAB_HOVER_DISTANCE, ringText,
                selectedGridCell.coordinate().offsetTo(hoverCoordinate).toDisplayString());
        overlayPainter.drawCenteredTextWithBackgroundAt(mousePoint.subtract(0.0d, HOVER_DISTANCE_OFFSET_Y), text,
                HOVER_DISTANCE_TEXT_COLOR, Font.font(HOVER_DISTANCE_FONT_SIZE), HOVER_DISTANCE_BACKGROUND_COLOR,
                HOVER_DISTANCE_PADDING);
    }

    private void drawHoverDescriptor(GridCoordinate coordinate, FXGridCanvasPainter overlayPainter) {
        if (cellFont == null) {
            return;
        }
        LabEntity entity = viewModel.getCurrentGridModel().getEntity(coordinate);
        entityDescriptorRegistry.findByDescriptorId(entity.descriptorId())
                .ifPresent(descriptor -> overlayPainter.drawCenteredTextInCell(
                        coordinate, descriptor.shortName(), HOVER_TEXT_COLOR, cellFont));
    }

    @FunctionalInterface
    private interface PainterTestVariant {

        void draw(FXGridCanvasPainter painter, GridCoordinate coordinate);

    }

}
