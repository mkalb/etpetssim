package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.model.GridEntityUtils;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.GridArrangement;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.SimulationView;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.HPos;
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
    private static final double INITIAL_CANVAS_SIZE = 100.0d;

    private final LabViewModel viewModel;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Canvas baseCanvas;
    private final Canvas overlayCanvas;
    private final BorderPane canvasBorderPane;

    private @Nullable FXGridCanvasPainter basePainter;
    private @Nullable FXGridCanvasPainter overlayPainter;
    private @Nullable Font font;
    private @Nullable Font smallFont;

    public LabView(LabViewModel viewModel,
                   GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        overlayCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        overlayCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        canvasBorderPane = new BorderPane();
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
        borderPane.getStyleClass().add(FXStyleClasses.VIEW_BORDERPANE);

        return borderPane;
    }

    private TitledPane createConfigPane(String title, FXComponentBuilder.LabeledControl... content) {
        VBox box = new VBox();
        for (FXComponentBuilder.LabeledControl labeledControl : content) {
            box.getChildren().addAll(labeledControl.label(), labeledControl.control());
        }
        box.getStyleClass().add(FXStyleClasses.CONFIG_VBOX);

        TitledPane pane = new TitledPane(title, box);
        pane.setCollapsible(content.length > 0);
        pane.setExpanded(content.length > 0);
        pane.setDisable(content.length == 0);
        pane.getStyleClass().add(FXStyleClasses.CONFIG_TITLEDPANE);
        return pane;
    }

    private Region createConfigRegion() {
        // --- Structure Group ---
        var cellShapeControl = FXComponentBuilder.createLabeledEnumComboBox(
                viewModel.cellShapeProperty(),
                viewModel.cellShapeProperty().displayNameProvider(),
                AppLocalization.getText(CellShape.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridEdgeBehaviorControl = FXComponentBuilder.createLabeledEnumComboBox(
                viewModel.gridEdgeBehaviorProperty(),
                viewModel.gridEdgeBehaviorProperty().displayNameProvider(),
                AppLocalization.getText(GridEdgeBehavior.labelResourceKey()),
                "TODO Tooltip", // TODO Add Tooltip to AppLocalizationKeys
                FXStyleClasses.CONFIG_COMBOBOX
        );

        var gridWidthControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridWidthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_WIDTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP, viewModel.gridWidthProperty().min(), viewModel.gridWidthProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var gridHeightControl = FXComponentBuilder.createLabeledIntSpinner(
                viewModel.gridHeightProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_GRID_HEIGHT),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_GRID_HEIGHT_TOOLTIP, viewModel.gridHeightProperty().min(), viewModel.gridHeightProperty().max()),
                FXStyleClasses.CONFIG_SPINNER
        );

        var cellEdgeLengthControl = FXComponentBuilder.createLabeledIntSlider(
                viewModel.cellEdgeLengthProperty(),
                AppLocalization.getText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH),
                AppLocalization.getFormattedText(AppLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, viewModel.cellEdgeLengthProperty().min(), viewModel.cellEdgeLengthProperty().max()),
                FXStyleClasses.CONFIG_SLIDER
        );

        TitledPane structurePane = createConfigPane(
                AppLocalization.getText(AppLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                cellShapeControl,
                gridEdgeBehaviorControl,
                gridWidthControl,
                gridHeightControl,
                cellEdgeLengthControl
        );

        // --- Layout Group ---
        TitledPane layoutPane = createConfigLayoutTitledPane();

        // --- Main Layout as Columns ---
        HBox mainBox = new HBox(structurePane, layoutPane);
        mainBox.getStyleClass().add(FXStyleClasses.CONFIG_HBOX);

        setupConfigListeners();

        return mainBox;
    }

    private TitledPane createConfigLayoutTitledPane() {
        VBox box = new VBox();
        box.getStyleClass().add(FXStyleClasses.CONFIG_VBOX);

        {
            RadioButton colorButton = new RadioButton("Color");
            RadioButton bwButton = new RadioButton("Black & White");
            ToggleGroup colorToggle = new ToggleGroup();
            colorButton.setToggleGroup(colorToggle);
            bwButton.setToggleGroup(colorToggle);
            colorButton.setSelected(true);

            colorToggle.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle == colorButton) {
                    viewModel.colorModeProperty().setValue(LabViewModel.ColorMode.COLOR);
                } else if (newToggle == bwButton) {
                    viewModel.colorModeProperty().setValue(LabViewModel.ColorMode.BLACK_WHITE);
                }
            });

            box.getChildren().addAll(new Label("Color Mode:"), colorButton, bwButton);
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
                    viewModel.renderingModeProperty().setValue(LabViewModel.RenderingMode.SHAPE);
                } else if (newToggle == circleButton) {
                    viewModel.renderingModeProperty().setValue(LabViewModel.RenderingMode.CIRCLE);
                }
            });
            box.getChildren().addAll(new Label("Rendering Mode:"), shapeButton, circleButton);
        }
        {
            CheckBox strokeCheckBox = new CheckBox("Stroke");
            strokeCheckBox.setSelected(viewModel.strokeModeProperty().isValue(LabViewModel.StrokeMode.CENTERED));
            strokeCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    viewModel.strokeModeProperty().setValue(LabViewModel.StrokeMode.CENTERED);
                } else {
                    viewModel.strokeModeProperty().setValue(LabViewModel.StrokeMode.NONE);
                }
            });
            box.getChildren().addAll(new Label("Stroke:"), strokeCheckBox);
        }

        TitledPane layoutPane = new TitledPane("Layout", box);
        layoutPane.getStyleClass().add(FXStyleClasses.CONFIG_TITLEDPANE);
        return layoutPane;
    }

    private void setupConfigListeners() {
        viewModel.cellShapeProperty().property().addListener((_, _, _) -> disableCanvas());
        viewModel.gridEdgeBehaviorProperty().property().addListener((_, _, _) -> disableCanvas());
        viewModel.gridWidthProperty().property().addListener((_, _, _) -> disableCanvas());
        viewModel.gridHeightProperty().property().addListener((_, _, _) -> disableCanvas());
        viewModel.cellEdgeLengthProperty().property().addListener((_, _, _) -> disableCanvas());
        viewModel.colorModeProperty().property().addListener((_, _, _) -> disableCanvas());
        viewModel.renderingModeProperty().property().addListener((_, _, _) -> disableCanvas());
        viewModel.strokeModeProperty().property().addListener((_, _, _) -> disableCanvas());
    }

    private Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        canvasBorderPane.setCenter(stackPane);
        canvasBorderPane.getStyleClass().add(FXStyleClasses.SIMULATION_CENTER_BORDERPANE);

        ScrollPane scrollPane = new ScrollPane(canvasBorderPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        return scrollPane;
    }

    private Region createControlRegion() {
        Button drawButton = new Button("draw");
        drawButton.setOnAction(_ -> {
            viewModel.onDrawButtonClicked();
            drawBaseCanvas();
        });
        drawButton.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);

        Button drawButtonModel = new Button("draw model");
        drawButtonModel.setOnAction(_ -> drawModel());
        drawButtonModel.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);

        Button drawButtonTest = new Button("draw test");
        drawButtonTest.setOnAction(_ -> drawTest());
        drawButtonTest.getStyleClass().add(FXStyleClasses.CONTROL_BUTTON);

        HBox hbox = new HBox();
        hbox.getChildren().addAll(drawButton, drawButtonModel, drawButtonTest);
        hbox.getStyleClass().add(FXStyleClasses.CONTROL_HBOX);

        return hbox;
    }

    private Region createObservationRegion() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add(FXStyleClasses.OBSERVATION_GRID);

        Label[] nameLabels = {
                new Label("Coordinate:")
        };

        String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);

        Label coordinateLabel = new Label(valueUnknown);
        StringBinding coordinateDisplayBinding = Bindings.createStringBinding(
                () -> {
                    GridCoordinate coord = viewModel.getLastClickedCoordinate();
                    return (coord != null) ? coord.toDisplayString() : valueUnknown;
                },
                viewModel.lastClickedCoordinateProperty()
        );
        coordinateLabel.textProperty().bind(coordinateDisplayBinding);

        Label[] valueLabels = {
                coordinateLabel
        };

        for (int i = 0; i < nameLabels.length; i++) {
            nameLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_NAME_LABEL);
            valueLabels[i].getStyleClass().add(FXStyleClasses.OBSERVATION_VALUE_LABEL);

            grid.add(nameLabels[i], 0, i);
            grid.add(valueLabels[i], 1, i);

            GridPane.setHalignment(nameLabels[i], HPos.LEFT);
            GridPane.setHalignment(valueLabels[i], HPos.RIGHT);
        }

        return grid;
    }

    private void updateCanvasBorderPane(GridStructure structure) {
        canvasBorderPane.setLeft((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_LEFT_BORDERPANE));
        canvasBorderPane.setRight((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_RIGHT_BORDERPANE));
        canvasBorderPane.setTop((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_TOP_BORDERPANE));
        canvasBorderPane.setBottom((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_BOTTOM_BORDERPANE));
    }

    private Region createBorderRegion(String styleClass) {
        Region border = new Region();
        border.getStyleClass().add(styleClass);
        return border;
    }

    private void registerEvents() {
        overlayCanvas.setOnMouseClicked(event -> {
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(new Point2D(event.getX(), event.getY()), basePainter.cellDimension(), overlayPainter.gridDimension2D(), viewModel.getStructure());
            overlayPainter.clearCanvasBackground();
            if (overlayPainter.isOutsideGrid(coordinate)) {
                viewModel.setLastClickedCoordinate(null);
            } else {
                if (!coordinate.equals(viewModel.getLastClickedCoordinate())) {
                    viewModel.setLastClickedCoordinate(coordinate);
                    overlayPainter.drawCellOuterCircle(coordinate, TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeAdjustment.OUTSIDE);

                    GridArrangement.cellNeighborsWithEdgeBehavior(coordinate, NeighborhoodMode.EDGES_AND_VERTICES,
                                           viewModel.getStructure())
                                   .forEach((neighborCoordinate, neighborCells) -> {
                                       if (viewModel.getStructure().isCoordinateValid(neighborCoordinate)) {
                                           overlayPainter.drawCell(neighborCoordinate, Color.YELLOW, null, 0.0d);
                                           if (font != null) {
                                               StringBuilder b = new StringBuilder(4);
                                               for (CellNeighborWithEdgeBehavior cellNeighbor : neighborCells) {
                                                   if (!b.isEmpty()) {
                                                       b.append(" : ");
                                                   }
                                                   b.append(cellNeighbor.direction().arrow());
                                               }
                                               overlayPainter.drawCenteredTextInCell(neighborCoordinate, b.toString(), Color.BLACK, font);
                                           }
                                       }
                                   });

                    GridArrangement.cellNeighborsWithEdgeBehavior(coordinate, NeighborhoodMode.EDGES_ONLY,
                                           viewModel.getStructure())
                                   .forEach((neighborCoordinate, _) -> {
                                       if (viewModel.getStructure().isCoordinateValid(neighborCoordinate)) {
                                           overlayPainter.drawCell(neighborCoordinate, null, Color.DARKORANGE, 2.5d);
                                       }
                                   });
/*
                    GridArrangement.validNeighborCoordinatesStream(coordinate,
                                           NeighborhoodMode.EDGES_AND_VERTICES, viewModel.getStructure(), 3)
                                   .forEach(neighborCoordinate -> {
                                       overlayPainter.drawCell(neighborCoordinate, null, Color.ORANGE, 2.5d);
                                   });

 */
                } else {
                    viewModel.setLastClickedCoordinate(null);
                }
            }
        });

        overlayCanvas.setOnMouseExited(event -> overlayPainter.clearCanvasBackground());
        overlayCanvas.setOnMouseMoved(event -> {
            overlayPainter.clearCanvasBackground();
            Point2D mousePoint = new Point2D(event.getX(), event.getY());
            GridCoordinate estimatedCoordinate = GridGeometry.estimateGridCoordinate(mousePoint, basePainter.cellDimension(), overlayPainter.gridDimension2D(), viewModel.getStructure());
            if (!estimatedCoordinate.isIllegal()) {
                overlayPainter.drawCell(estimatedCoordinate, null, Color.RED, 1.0d);
            }
            overlayPainter.drawCircle(mousePoint, 2.0d, Color.GREEN, null, 1.0d, StrokeAdjustment.CENTERED);
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(mousePoint, basePainter.cellDimension(), overlayPainter.gridDimension2D(), viewModel.getStructure());
            if (!coordinate.isIllegal() && !overlayPainter.isOutsideGrid(coordinate)) {
                overlayPainter.drawCellBoundingBox(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeAdjustment.OUTSIDE);

                if (overlayPainter.cellDimension().edgeLength() >= 8.0d) {
                    overlayPainter.drawCellInnerCircle(coordinate, Color.WHITE, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeAdjustment.INSIDE);
                    // overlayPainter.drawHexagonMatchingCellWidth(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
                    // overlayPainter.drawTriangleMatchingCellWidth(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
                    if ((font != null) && !coordinate.equals(viewModel.getLastClickedCoordinate())) {
                        GridEntityUtils.consumeDescriptorAt(coordinate, viewModel.getCurrentModel(), entityDescriptorRegistry,
                                descriptor -> overlayPainter.drawCenteredTextInCell(coordinate, descriptor.shortName(), Color.RED, font));
                    }
                }
            }
            if (viewModel.getLastClickedCoordinate() != null) {
                overlayPainter.drawCellOuterCircle(viewModel.getLastClickedCoordinate(), TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeAdjustment.OUTSIDE);
            }
        });
    }

    private void disableCanvas() {
        overlayCanvas.setOnMouseClicked(null);
        overlayCanvas.setOnMouseExited(null);
        overlayCanvas.setOnMouseMoved(null);

        viewModel.setLastClickedCoordinate(null);

        if (basePainter != null) {
            basePainter.clearCanvasBackground();
        }
        basePainter = null;
        overlayPainter = null;
    }

    private void resetCanvasAndPainter() {
        viewModel.setLastClickedCoordinate(null);

        double cellEdgeLength = viewModel.getCellEdgeLength();
        GridStructure structure = viewModel.getStructure();

        AppLogger.info("Initialize canvas and painter with structure " + structure.toDisplayString() +
                " and cell edge length " + cellEdgeLength);

        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(6_000.0d, basePainter.gridDimension2D().getWidth()));
        baseCanvas.setHeight(Math.min(4_000.0d, basePainter.gridDimension2D().getHeight()));

        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(Math.min(5_000.0d, overlayPainter.gridDimension2D().getWidth()));
        overlayCanvas.setHeight(Math.min(3_000.0d, overlayPainter.gridDimension2D().getHeight()));

        // Log information
        AppLogger.info("GridDimension2D: " + overlayPainter.gridDimension2D());
        AppLogger.info("CellDimension:   " + overlayPainter.cellDimension());

        // Font
        double fontHeightFactor = (structure.cellShape() == CellShape.TRIANGLE) ? 0.14d : 0.18d;
        double fontSize = Math.round(basePainter.cellDimension().height() * fontHeightFactor);
        if (fontSize > 6) {
            if (Font.getFamilies().contains("Verdana")) {
                font = Font.font("Verdana", fontSize);
                smallFont = Font.font("Verdana", Math.min(8, fontSize));
            } else {
                font = Font.font("System", fontSize);
                smallFont = Font.font("System", Math.min(8, fontSize));
            }
            AppLogger.info("Font for canvas: " + font);
        } else {
            font = null;
            smallFont = null;
            AppLogger.info("Font size too small: " + fontSize);
        }

        updateCanvasBorderPane(structure);

        registerEvents();
    }

    private void drawBaseCanvas() {
        if (!viewModel.hasSimulationManager()) {
            AppLogger.warn("Simulation manager is not initialized, cannot draw base canvas.");
            disableCanvas();
            return;
        }

        resetCanvasAndPainter();

        boolean colorModeBW = viewModel.colorModeProperty().isValue(LabViewModel.ColorMode.BLACK_WHITE);
        boolean renderingModeCircle =
                viewModel.renderingModeProperty().isValue(LabViewModel.RenderingMode.CIRCLE);
        boolean strokeModeNone = viewModel.strokeModeProperty().isValue(LabViewModel.StrokeMode.NONE);
        double strokeLineWidth = 0.5d;
        Color textColor = colorModeBW ? Color.BLACK : TEXT_COLOR;
        Color strokeColor = strokeModeNone ? null : Color.BLACK;

        drawBaseCanvasBackground(colorModeBW);

        viewModel.getStructure()
                 .coordinatesStream()
                 .forEachOrdered(coordinate ->
                         drawCoordinateAtBaseCanvas(coordinate, colorModeBW, renderingModeCircle, strokeColor, strokeLineWidth, textColor));
    }

    private void drawBaseCanvasBackground(boolean colorModeBW) {
        basePainter.fillCanvasBackground(CANVAS_COLOR);
        if (colorModeBW) {
            basePainter.fillGridBackground(Color.WHITE);
        } else {
            basePainter.fillGridBackground(FXPaintBuilder.createHorizontalGradient(GRID_BACKGROUND_COLOR.darker(), GRID_BACKGROUND_COLOR.brighter()));
        }
    }

    private void drawCoordinateAtBaseCanvas(GridCoordinate coordinate, boolean colorModeBW, boolean renderingModeCircle, Color strokeColor, double strokeLineWidth, Color textColor) {
        Color color = colorModeBW ? calculateColumnBlackWhiteColor(coordinate) : calculateColumnSimilarityColor(coordinate);
        if (renderingModeCircle) {
            basePainter.drawCellInnerCircle(coordinate, color, strokeColor, strokeLineWidth, StrokeAdjustment.CENTERED);
        } else {
            basePainter.drawCell(coordinate, color, strokeColor, strokeLineWidth);
        }
        if (font != null) {
            basePainter.drawCenteredTextInCell(coordinate, coordinate.toDisplayString(), textColor, font);
        }
    }

    private void drawTest() {
        if ((basePainter == null) || (overlayPainter == null)) {
            return;
        }

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
    }

    private void drawModel() {
        if ((basePainter == null) || (overlayPainter == null)) {
            return;
        }

        Color fillColor = FXPaintBuilder.createColorWithAlpha(Color.RED, 0.5d);
        viewModel.getCurrentModel().nonDefaultCells()
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

}
