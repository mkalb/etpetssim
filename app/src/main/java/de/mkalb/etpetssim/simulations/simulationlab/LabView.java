package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.SimulationView;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.jspecify.annotations.Nullable;

public final class LabView implements SimulationView {

    private static final Color MOUSE_CLICK_COLOR = Color.ROSYBROWN;
    private static final Color MOUSE_HOVER_COLOR = Color.DARKSLATEBLUE;
    private static final Color TEXT_COLOR = Color.DARKSLATEGRAY;
    private static final Color CANVAS_COLOR = Color.BLACK;
    private static final Color GRID_BACKGROUND_COLOR = Color.DIMGRAY;
    private static final Color TRANSLUCENT_WHITE = FXPaintBuilder.createColorWithAlpha(Color.WHITE, 0.2); // for lightening effect
    private static final Color TRANSLUCENT_BLACK = FXPaintBuilder.createColorWithAlpha(Color.BLACK, 0.2); // for darkening effect
    private static final double MOUSE_CLICK_LINE_WIDTH = 8.0d;
    private static final double MOUSE_HOVER_LINE_WIDTH = 2.0d;

    private final LabViewModel viewModel;
    private final Canvas baseCanvas;
    private final Canvas overlayCanvas;

    private final ReadableGridModel<LabEntity> model;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final GridStructure structure;
    private final FXGridCanvasPainter basePainter;
    private final FXGridCanvasPainter overlayPainter;
    private final @Nullable Font font;
    private final @Nullable Font smallFont;

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);
    private final ObjectProperty<RenderingMode> renderingMode = new SimpleObjectProperty<>(RenderingMode.SHAPE);
    private final ObjectProperty<ColorMode> colorMode = new SimpleObjectProperty<>(ColorMode.COLOR);
    private final ObjectProperty<StrokeMode> strokeMode = new SimpleObjectProperty<>(StrokeMode.CENTERED);

    public LabView(LabViewModel viewModel,
                   LabSimulationManager manager,
                   GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;
        structure = manager.structure();
        model = manager.currentModel();
        double cellEdgeLength = manager.config().cellEdgeLength();

        // Canvas and FXGridCanvasPainter
        baseCanvas = new Canvas(cellEdgeLength, cellEdgeLength);
        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        double additionalBorder = 0.0d; // only for testing grid dimension
        baseCanvas.setWidth(Math.min(6_000.0d, basePainter.gridDimension2D().getWidth() + additionalBorder));
        baseCanvas.setHeight(Math.min(4_000.0d, basePainter.gridDimension2D().getHeight() + additionalBorder));
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);

        overlayCanvas = new Canvas(cellEdgeLength, cellEdgeLength);
        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(Math.min(5_000.0d, overlayPainter.gridDimension2D().getWidth()));
        overlayCanvas.setHeight(Math.min(3_000.0d, overlayPainter.gridDimension2D().getHeight()));

        // Log information
        AppLogger.info("Structure:       " + structure.toDisplayString());
        AppLogger.info("GridDimension2D: " + overlayPainter.gridDimension2D());
        AppLogger.info("Cell count:      " + structure.cellCount());
        AppLogger.info("CellDimension:   " + overlayPainter.cellDimension());
        AppLogger.info("NonDefaultCells: " + model.nonDefaultCells().count());

        // Font
        double fontHeightFactor = (structure.cellShape() == CellShape.TRIANGLE) ? 0.14d : 0.18d;
        double fontSize = Math.round(basePainter.cellDimension().height() * fontHeightFactor);
        if (fontSize > 6) {
            if (Font.getFamilies().contains("Verdana")) {
                font = Font.font("Verdana", fontSize);
                smallFont = Font.font("Verdana", 8);
            } else {
                font = Font.font("System", fontSize);
                smallFont = Font.font("System", 8);
            }
            AppLogger.info("Font for canvas: " + font);
        } else {
            font = null;
            smallFont = null;
            AppLogger.info("Font size too small: " + fontSize);
        }
    }

    @Override
    public Region buildViewRegion() {
        Region configRegion = createConfigRegion();
        Region simulationRegion = createSimulationRegion();
        Region controlRegion = createControlRegion();
        Region observationRegion = createObservationRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(configRegion);
        borderPane.setCenter(simulationRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);
        borderPane.getStyleClass().add(FXStyleClasses.SIMULATION_BORDERPANE);

        registerEvents();

        drawCanvas();

        return borderPane;
    }

    private Region createConfigRegion() {
        VBox configBox = new VBox();
        configBox.setMinWidth(200);
        configBox.setMinHeight(50);

        {
            RadioButton colorButton = new RadioButton("Color");
            RadioButton bwButton = new RadioButton("Black & White");
            ToggleGroup colorToggle = new ToggleGroup();
            colorButton.setToggleGroup(colorToggle);
            bwButton.setToggleGroup(colorToggle);
            colorButton.setSelected(true);

            colorToggle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == colorButton) {
                    colorMode.set(ColorMode.COLOR);
                } else if (newToggle == bwButton) {
                    colorMode.set(ColorMode.BLACK_WHITE);
                }
            });

            configBox.getChildren().addAll(new Label("Color Mode:"), colorButton, bwButton);
        }
        {
            RadioButton shapeButton = new RadioButton("Shape");
            RadioButton circleButton = new RadioButton("Circle");
            ToggleGroup renderToggle = new ToggleGroup();
            shapeButton.setToggleGroup(renderToggle);
            circleButton.setToggleGroup(renderToggle);
            shapeButton.setSelected(true);
            renderToggle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == shapeButton) {
                    renderingMode.set(RenderingMode.SHAPE);
                } else if (newToggle == circleButton) {
                    renderingMode.set(RenderingMode.CIRCLE);
                }
            });
            configBox.getChildren().addAll(new Label("Rendering Mode:"), shapeButton, circleButton);
        }
        {
            RadioButton strokeButtonNone = new RadioButton("None");
            RadioButton strokeButtonCentered = new RadioButton("Centered");
            ToggleGroup strokeToogle = new ToggleGroup();
            strokeButtonNone.setToggleGroup(strokeToogle);
            strokeButtonCentered.setToggleGroup(strokeToogle);
            strokeButtonCentered.setSelected(true);
            strokeToogle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == strokeButtonNone) {
                    strokeMode.set(StrokeMode.NONE);
                } else if (newToggle == strokeButtonCentered) {
                    strokeMode.set(StrokeMode.CENTERED);
                }
            });
            configBox.getChildren().addAll(new Label("Stroke:"), strokeButtonNone, strokeButtonCentered);
        }

        return configBox;
    }

    private Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        ScrollPane scrollPane = new ScrollPane(addVisibleCanvasBorder(stackPane));
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        return scrollPane;
    }

    private Region createControlRegion() {
        HBox controlBox = new HBox();

        Button drawButton = new Button("draw");
        drawButton.setOnAction(event -> {
            resetCanvas();
            drawCanvas();
        });
        controlBox.getChildren().add(drawButton);

        Button drawButtonModel = new Button("draw model");
        drawButtonModel.setOnAction(event -> drawModel());
        controlBox.getChildren().add(drawButtonModel);

        Button drawButtonTest = new Button("draw test");
        drawButtonTest.setOnAction(event -> drawTest());
        controlBox.getChildren().add(drawButtonTest);

        return controlBox;
    }

    private Region createObservationRegion() {
        VBox statBox = new VBox();
        statBox.setMinWidth(200);
        statBox.setMinHeight(50);

        statBox.getChildren().add(new Label("Structure: " + structure.toDisplayString()));
        statBox.getChildren().add(new Label("Edge length: " + basePainter.cellDimension().edgeLength()));
        statBox.getChildren().add(new Label("Cell dimension: " + basePainter.cellDimension().toDisplayString()));
        statBox.getChildren().add(new Label("Grid dimension: " + basePainter.gridDimension2D()));
        statBox.getChildren().add(new Label("Font: " + font));
        Label coordinateLabel = new Label();
        StringBinding coordinateDisplayBinding = Bindings.createStringBinding(
                () -> {
                    GridCoordinate coord = lastClickedCoordinate.get();
                    return "Coordinate: " + (coord != null ? coord.toDisplayString() : "");
                },
                lastClickedCoordinate
        );
        coordinateLabel.textProperty().bind(coordinateDisplayBinding);
        statBox.getChildren().add(coordinateLabel);

        return statBox;
    }

    private void resetCanvas() {
        lastClickedCoordinate.set(null);
        overlayPainter.clearCanvasBackground();
        basePainter.clearGridBackground();
    }

    private Pane addVisibleCanvasBorder(Pane pane) {
        if ((structure.edgeBehaviorX() == EdgeBehavior.WRAP) && (structure.edgeBehaviorY() == EdgeBehavior.WRAP)) {
            return pane;
        }

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(pane);

        if (structure.edgeBehaviorX() != EdgeBehavior.WRAP) {
            Region leftBorder = new Region();
            leftBorder.getStyleClass().add("canvas-left-border-pane");
            borderPane.setLeft(leftBorder);

            Region rightBorder = new Region();
            rightBorder.getStyleClass().add("canvas-right-border-pane");
            borderPane.setRight(rightBorder);
        }

        if (structure.edgeBehaviorY() != EdgeBehavior.WRAP) {
            Region topBorder = new Region();
            topBorder.getStyleClass().add("canvas-top-border-pane");
            borderPane.setTop(topBorder);

            Region bottomBorder = new Region();
            bottomBorder.getStyleClass().add("canvas-bottom-border-pane");
            borderPane.setBottom(bottomBorder);
        }

        return borderPane;
    }

    private void registerEvents() {
        Canvas overlayCanvas = overlayPainter.canvas();
        overlayCanvas.setOnMouseClicked(event -> {
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(new Point2D(event.getX(), event.getY()), basePainter.cellDimension(), overlayPainter.gridDimension2D(), structure);
            overlayPainter.clearCanvasBackground();
            if (overlayPainter.isOutsideGrid(coordinate)) {
                lastClickedCoordinate.set(null);
            } else {
                if (!coordinate.equals(lastClickedCoordinate.get())) {
                    lastClickedCoordinate.set(coordinate);
                    overlayPainter.drawCellOuterCircle(coordinate, TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeAdjustment.OUTSIDE);

                    GridArrangement.validCellNeighborsStream(coordinate, NeighborhoodMode.EDGES_AND_VERTICES,
                                           structure)
                                   .forEach(cellNeighbor -> {
                                       overlayPainter.drawCell(cellNeighbor.neighborCoordinate(), Color.YELLOW, null, 0.0d);
                                       if (smallFont != null) {
                                           overlayPainter.drawCenteredTextInCell(cellNeighbor.neighborCoordinate(), cellNeighbor.toDisplayString(), Color.BLACK, smallFont);
                                       }
                                   });

                    GridArrangement.validNeighborCoordinatesStream(coordinate,
                                           NeighborhoodMode.EDGES_AND_VERTICES, structure, 3)
                                   .forEach(neighborCoordinate -> {
                                       overlayPainter.drawCell(neighborCoordinate, null, Color.ORANGE, 2.5d);
                                   });
                } else {
                    lastClickedCoordinate.set(null);
                }
            }
        });

        overlayCanvas.setOnMouseExited(event -> overlayPainter.clearCanvasBackground());
        overlayCanvas.setOnMouseMoved(event -> {
            overlayPainter.clearCanvasBackground();
            Point2D mousePoint = new Point2D(event.getX(), event.getY());
            GridCoordinate estimatedCoordinate = GridGeometry.estimateGridCoordinate(mousePoint, basePainter.cellDimension(), overlayPainter.gridDimension2D(), structure);
            if (!estimatedCoordinate.isIllegal()) {
                overlayPainter.drawCell(estimatedCoordinate, null, Color.RED, 1.0d);
            }
            overlayPainter.drawCircle(mousePoint, 2.0d, Color.GREEN, null, 1.0d, StrokeAdjustment.CENTERED);
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(mousePoint, basePainter.cellDimension(), overlayPainter.gridDimension2D(), structure);
            if (!coordinate.isIllegal() && !overlayPainter.isOutsideGrid(coordinate)) {
                overlayPainter.drawCellBoundingBox(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeAdjustment.OUTSIDE);

                if (overlayPainter.cellDimension().edgeLength() >= 8.0d) {
                    overlayPainter.drawCellInnerCircle(coordinate, Color.WHITE, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeAdjustment.INSIDE);
                    // overlayPainter.drawHexagonMatchingCellWidth(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
                    // overlayPainter.drawTriangleMatchingCellWidth(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
                    if ((font != null) && !coordinate.equals(lastClickedCoordinate.get())) {
                        GridEntityUtils.consumeDescriptorAt(coordinate, model, entityDescriptorRegistry,
                                descriptor -> overlayPainter.drawCenteredTextInCell(coordinate, descriptor.shortName(), Color.RED, font));
                    }
                }
            }
            if (lastClickedCoordinate.get() != null) {
                overlayPainter.drawCellOuterCircle(lastClickedCoordinate.get(), TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeAdjustment.OUTSIDE);
            }
        });
    }

    private void drawCanvas() {
        boolean useColorBlackWhite = colorMode.get() == ColorMode.BLACK_WHITE;
        boolean drawCellAsInnerCircle = renderingMode.get() == RenderingMode.CIRCLE;
        // Background
        basePainter.fillCanvasBackground(CANVAS_COLOR);
        if (useColorBlackWhite) {
            basePainter.fillGridBackground(Color.WHITE);
        } else {
            basePainter.fillGridBackground(FXPaintBuilder.createHorizontalGradient(GRID_BACKGROUND_COLOR.darker(), GRID_BACKGROUND_COLOR.brighter()));
        }

        // Cells at all coordinates
        structure.coordinatesStream().forEachOrdered(coordinate -> {
            Color color = useColorBlackWhite ? calculateColumnBlackWhiteColor(coordinate) : calculateColumnSimilarityColor(coordinate);
            Color textColor = useColorBlackWhite ? Color.BLACK : TEXT_COLOR;
            if (drawCellAsInnerCircle) {
                switch (strokeMode.get()) {
                    case StrokeMode.NONE ->
                            basePainter.drawCellInnerCircle(coordinate, color, null, 0.0d, StrokeAdjustment.CENTERED);
                    case StrokeMode.CENTERED ->
                            basePainter.drawCellInnerCircle(coordinate, color, Color.BLACK, 0.5d, StrokeAdjustment.CENTERED);
                }
            } else {
                if (strokeMode.get() == StrokeMode.NONE) {
                    basePainter.drawCell(coordinate, color, null, 0.0d);
                } else {
                    basePainter.drawCell(coordinate, color, Color.BLACK, 0.5d);
                }
            }
            if (font != null) {
                basePainter.drawCenteredTextInCell(coordinate, coordinate.toDisplayString(), textColor, font);
            }
        });
    }

    private void drawTest() {
        if (overlayPainter.cellDimension().edgeLength() < 16.0d) {
            AppLogger.warn("edge length is too small for drawing test elements, skipping test drawing.");
            return;
        }

        Color t1 = FXPaintBuilder.createColorWithAlpha(Color.RED, 0.5);
        Color t2 = FXPaintBuilder.createColorWithAlpha(Color.YELLOW, 0.8);

        basePainter.drawCellBoundingBox(new GridCoordinate(2, 4), t1, t2, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(2, 6), t2, t1, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(2, 8), t1, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(2, 10), t2, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        basePainter.drawCellBoundingBox(new GridCoordinate(4, 4), null, t2, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(4, 6), null, t1, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(4, 8), null, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        basePainter.drawCellBoundingBox(new GridCoordinate(4, 10), null, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        basePainter.drawCellInnerCircle(new GridCoordinate(6, 4), t1, t2, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(6, 6), t2, t1, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(6, 8), t1, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(6, 10), t2, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        basePainter.drawCellInnerCircle(new GridCoordinate(8, 4), null, t2, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(8, 6), null, t1, 8.0d, StrokeAdjustment.INSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(8, 8), null, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        basePainter.drawCellInnerCircle(new GridCoordinate(8, 10), null, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        for (int x = 50; x < 100; x++) {
            basePainter.drawPixelDirect(x * 4, 100, Color.MAGENTA);
            basePainter.drawPixelRect(x * 4, 120, Color.RED);
        }

        basePainter.drawTriangle(new GridCoordinate(11, 4),
                GridGeometry.convertEdgeLengthToMatchWidth(basePainter.cellDimension().edgeLength(), basePainter.gridStructure().cellShape(), CellShape.TRIANGLE),
                Color.WHITE, Color.BLACK, 4.0d);
        basePainter.drawHexagon(new GridCoordinate(9, 3),
                GridGeometry.convertEdgeLengthToMatchWidth(basePainter.cellDimension().edgeLength(), basePainter.gridStructure().cellShape(), CellShape.HEXAGON),
                Color.WHITE, Color.BLACK, 4.0d);

        basePainter.drawCellFrameSegment(new GridCoordinate(0, 1), Color.DARKGREEN, 5.0, PolygonViewDirection.LEFT);
        basePainter.drawCellFrameSegment(new GridCoordinate(1, 2), Color.DARKBLUE, 5.0, PolygonViewDirection.LEFT);
      /*
        boolean leftEdge = true;
        painter.drawColumnEdgeLine(0, 0, 3, leftEdge, Color.RED, 2.0d);
        painter.drawColumnEdgeLine(2, 1, 3, leftEdge, Color.RED, 2.0d);
        painter.drawColumnEdgeLine(4, 1, 4, leftEdge, Color.RED, 2.0d);
        painter.drawColumnEdgeLine(6, 0, 15, leftEdge, Color.RED, 2.0d);

        painter.drawColumnEdgeLine(7, 0, 3, leftEdge, Color.RED, 2.0d);
        painter.drawColumnEdgeLine(9, 1, 3, leftEdge, Color.RED, 2.0d);
        painter.drawColumnEdgeLine(11, 1, 4, leftEdge, Color.RED, 2.0d);
        painter.drawColumnEdgeLine(13, 0, 15, leftEdge, Color.RED, 2.0d);
*/
    }

    private void drawModel() {
        Color fillColor = FXPaintBuilder.createColorWithAlpha(Color.RED, 0.5d);
        model.nonDefaultCells()
             .forEach((GridCell<LabEntity> cell) -> basePainter.drawCell(cell.coordinate(), fillColor, null, 0.0d));
    }

    private Color calculateColumnSimilarityColor(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % 2;
        int rowGroup = coordinate.y() % 2;

        return switch ((columnGroup << 1) | rowGroup) {
            case 0 -> Color.LIGHTSKYBLUE;       // Column 0, Row 0
            case 1 -> Color.LIGHTSTEELBLUE;     // Column 0, Row 1
            case 2 -> Color.PALEGREEN;          // Column 1, Row 0
            case 3 -> Color.MEDIUMAQUAMARINE;   // Column 1, Row 1
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

    private Color calculateColumnBlackWhiteColor(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % 2;
        int rowGroup = coordinate.y() % 2;

        return switch ((columnGroup << 1) | rowGroup) {
            case 0 -> Color.WHITE;       // Column 0, Row 0
            case 1 -> Color.LIGHTGRAY;     // Column 0, Row 1
            case 2 -> Color.DARKGRAY;          // Column 1, Row 0
            case 3 -> Color.GRAY;   // Column 1, Row 1
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

    public enum RenderingMode {
        SHAPE, CIRCLE
    }

    public enum ColorMode {
        COLOR, BLACK_WHITE
    }

    public enum StrokeMode {
        NONE, CENTERED
    }

}
