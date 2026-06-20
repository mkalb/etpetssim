package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * Generic base view that composes config, control and observation regions.
 */
public abstract class AbstractMainView<
        VM extends SimulationMainViewModel,
        CTX extends SimulationUserActionContext,
        CFV extends SimulationConfigView,
        CLV extends SimulationControlView,
        OV extends SimulationObservationView>
        implements SimulationMainView {

    private static final double INITIAL_CANVAS_WIDTH = 640.0d;
    private static final double INITIAL_CANVAS_HEIGHT = 480.0d;
    private static final double MAX_CANVAS_WIDTH = 6_400.0d;
    private static final double MAX_CANVAS_HEIGHT = 4_800.0d;
    private static final String MAIN_FONT_FAMILY = "Verdana";
    private static final String FALLBACK_FONT_FAMILY = "System";
    private static final double MIN_CELL_FONT_SIZE = 7.0d;
    private static final double MIN_EMOJI_FONT_SIZE = 3.0d;
    private static final double CENTER_SPLIT_PANE_DIVIDER_POSITION = 0.75d;
    private static final String LOG_COMPONENT = "MainView";

    protected final VM viewModel;
    protected final CFV configView;
    protected final CLV controlView;
    protected final OV observationView;
    protected final GridEntityDescriptorRegistry entityDescriptorRegistry;
    protected final Canvas baseCanvas;
    protected final Canvas dynamicCanvas;
    protected final Canvas overlayCanvas;
    private final ScrollPane canvasScrollPane;
    private final BorderPane canvasBorderPane;
    private final Label notificationLabel;
    private final ScrollPane editAffordanceScrollPane;
    private final HBox editAffordanceBox;
    private final Map<Double, Font> fontCache;
    private final List<Runnable> actionToolBarCleanupActions = new ArrayList<>();
    protected @Nullable FXGridCanvasPainter basePainter;
    protected @Nullable FXGridCanvasPainter dynamicPainter;
    protected @Nullable FXGridCanvasPainter overlayPainter;
    protected @Nullable Font cellFont;
    protected @Nullable Font cellEmojiFont;
    private @Nullable Button editModeButton;
    private @Nullable ObjectProperty<String> selectedToolIdProperty;
    private @Nullable ChangeListener<String> selectedToolIdListener;

    protected AbstractMainView(VM viewModel,
                               CFV configView, CLV controlView, OV observationView,
                               GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.configView = configView;
        this.controlView = controlView;
        this.observationView = observationView;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        dynamicCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        overlayCanvas = new Canvas(INITIAL_CANVAS_WIDTH, INITIAL_CANVAS_HEIGHT);
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        dynamicCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        overlayCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);

        canvasBorderPane = new BorderPane();
        canvasBorderPane.getStyleClass().add(FXStyleClasses.SIMULATION_CENTER_BORDERPANE);

        canvasScrollPane = new ScrollPane();
        canvasScrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        fontCache = new HashMap<>();

        notificationLabel = new Label();
        notificationLabel.getStyleClass().add(FXStyleClasses.SIMULATION_NOTIFICATION_LABEL);
        clearNotification();

        editAffordanceBox = new HBox();
        editAffordanceBox.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR);

        editAffordanceScrollPane = new ScrollPane();
        editAffordanceScrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_SCROLLPANE);
        editAffordanceScrollPane.setContent(editAffordanceBox);
        editAffordanceScrollPane.setFitToHeight(true);
        editAffordanceScrollPane.setFitToWidth(false);
        editAffordanceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        editAffordanceScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        editAffordanceScrollPane.setPannable(false);
        registerEditAffordanceWheelScroll();
        clearEditAffordanceToolBar();
    }

    private static double clampZeroToOne(double value) {
        return Math.clamp(value, 0.0d, 1.0d);
    }

    @SuppressWarnings("MagicNumber")
    protected static double computeCellFontSize(CellDimension cellDimension, CellShape cellShape) {
        return Math.round(cellDimension.height() *
                switch (cellShape) {
                    case TRIANGLE -> 0.135d;
                    case SQUARE -> 0.18d;
                    case HEXAGON -> 0.175d;
                });
    }

    @SuppressWarnings("MagicNumber")
    protected static double computeCellEmojiFontSize(CellDimension cellDimension, CellShape cellShape) {
        return Math.round(cellDimension.height() *
                switch (cellShape) {
                    case TRIANGLE -> 0.6d;
                    case SQUARE -> 0.9d;
                    case HEXAGON -> 0.8d;
                });
    }

    private void registerEditAffordanceWheelScroll() {
        editAffordanceScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            double delta = (Math.abs(event.getDeltaX()) > 0.0d)
                    ? event.getDeltaX()
                    : -event.getDeltaY();
            if (delta == 0.0d) {
                return;
            }

            Node content = editAffordanceScrollPane.getContent();
            if (content == null) {
                return;
            }
            double viewportWidth = editAffordanceScrollPane.getViewportBounds().getWidth();
            double maxX = content.getLayoutBounds().getWidth() - viewportWidth;
            if (maxX <= 0.0d) {
                return;
            }

            double nextHValue = clampZeroToOne(editAffordanceScrollPane.getHvalue() + (delta / maxX));
            editAffordanceScrollPane.setHvalue(nextHValue);
            event.consume();
        });
    }

    @Override
    public final Region buildMainRegion() {
        Region configRegion = configView.buildConfigRegion();
        Region controlRegion = controlView.buildControlRegion();
        Region observationRegion = observationView.buildObservationRegion();
        Region simulationRegion = createSimulationRegion();

        SplitPane centerSplitPane = new SplitPane();
        centerSplitPane.setOrientation(Orientation.HORIZONTAL);
        centerSplitPane.getStyleClass().add(FXStyleClasses.CENTER_SPLITPANE);
        centerSplitPane.getItems().addAll(simulationRegion, observationRegion);
        centerSplitPane.setDividerPositions(CENTER_SPLIT_PANE_DIVIDER_POSITION);
        SplitPane.setResizableWithParent(observationRegion, false);

        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add(FXStyleClasses.MAIN_BORDERPANE);

        borderPane.setTop(configRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setCenter(centerSplitPane);

        registerViewModelListeners();
        registerNotificationListener();
        registerOverlayPrimaryClickHandler();

        return borderPane;
    }

    @Override
    public void shutdownSimulation() {
        viewModel.shutdownSimulation();
        basePainter = null;
        dynamicPainter = null;
        overlayPainter = null;
        cellFont = null;
        cellEmojiFont = null;
        clearNotification();
        clearEditAffordanceToolBar();
        clearActionToolBar();
    }

    protected abstract void registerViewModelListeners();

    private void registerOverlayPrimaryClickHandler() {
        overlayCanvas.setOnMouseReleased(event -> {
            FXGridCanvasPainter painter = overlayPainter;
            if ((painter != null)
                    && (event.getButton() == MouseButton.PRIMARY)
                    && event.isStillSincePress()
                    && (event.getClickCount() == 1)) {
                Point2D mousePoint = new Point2D(event.getX(), event.getY());
                GridCoordinate mouseCoordinate = GridGeometry.fromCanvasPosition(mousePoint,
                        painter.cellDimension(), painter.gridDimension2D(), painter.gridStructure());
                if (!mouseCoordinate.isIllegal() && painter.isInsideGrid(mouseCoordinate)) {
                    handleMouseClickedCoordinate(mousePoint, mouseCoordinate, painter);
                }
            }
        });
    }

    protected abstract void handleMouseClickedCoordinate(Point2D mousePoint, GridCoordinate mouseCoordinate, FXGridCanvasPainter painter);

    private void registerNotificationListener() {
        viewModel.notificationTypeProperty().addListener((_, _, newVal) -> {
            if (newVal == SimulationNotificationType.NONE) {
                clearNotification();
            } else {
                updateNotification(AppLocalization.getText(newVal.resourceKey()));
            }
        });
    }

    private void updateNotification(String notification) {
        notificationLabel.setText(notification);
        notificationLabel.setVisible(true);
        notificationLabel.setManaged(true);
    }

    private void clearNotification() {
        notificationLabel.setText(null);
        notificationLabel.setVisible(false);
        notificationLabel.setManaged(false);
    }

    private void clearActionToolBar() {
        if ((selectedToolIdProperty != null) && (selectedToolIdListener != null)) {
            selectedToolIdProperty.removeListener(selectedToolIdListener);
        }
        selectedToolIdProperty = null;
        selectedToolIdListener = null;
        for (var cleanupAction : actionToolBarCleanupActions) {
            try {
                cleanupAction.run();
            } catch (RuntimeException e) {
                AppLogger.errorf(e, "%s: Failed to clean up edit toolbar resources.", LOG_COMPONENT);
            }
        }
        actionToolBarCleanupActions.clear();
    }

    private void clearEditAffordanceToolBar() {
        if (editModeButton != null) {
            editModeButton.setOnAction(null);
        }
        editModeButton = null;
        for (Node item : editAffordanceBox.getChildren()) {
            item.visibleProperty().unbind();
            item.managedProperty().unbind();
        }
        editAffordanceBox.getChildren().clear();
        editAffordanceScrollPane.disableProperty().unbind();
        editAffordanceScrollPane.visibleProperty().unbind();
        editAffordanceScrollPane.managedProperty().unbind();
        editAffordanceScrollPane.setDisable(true);
        editAffordanceScrollPane.setVisible(false);
        editAffordanceScrollPane.setManaged(false);
    }

    protected final void rebuildActionToolBar() {
        clearEditAffordanceToolBar();
        clearActionToolBar();
        List<SimulationUserActionDescriptor<CTX>> descriptors = createUserActionDescriptors();
        if (descriptors.isEmpty()) {
            return;
        }

        var currentEditModeProperty = editModeActiveProperty();
        if (currentEditModeProperty == null) {
            return;
        }

        var currentSelectedToolIdProperty = selectedUserActionToolIdProperty();
        List<Node> nodes = createEditToolBarNodes(descriptors, currentEditModeProperty, currentSelectedToolIdProperty);
        if (nodes.isEmpty()) {
            return;
        }

        Button editButton = new Button();
        configureEditToolBarButton(editButton);
        editButton.setText(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_EDIT));
        editButton.setTooltip(new Tooltip(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_EDIT_TOOLTIP)));
        editButton.setOnAction(_ -> currentEditModeProperty.set(!currentEditModeProperty.get()));
        editButton.visibleProperty().bind(currentEditModeProperty.not());
        editButton.managedProperty().bind(currentEditModeProperty.not());

        editModeButton = editButton;

        List<Node> toolbarNodes = new ArrayList<>();
        toolbarNodes.add(editButton);
        toolbarNodes.addAll(nodes);
        editAffordanceBox.getChildren().setAll(toolbarNodes);
        editAffordanceScrollPane.disableProperty().bind(viewModel.simulationStateProperty().isNotEqualTo(SimulationState.PAUSED));
        editAffordanceScrollPane.setVisible(true);
        editAffordanceScrollPane.setManaged(true);
    }

    /**
     * Returns user-action descriptors used to build edit controls above the canvas.
     *
     * @return descriptor list; empty when the simulation has no user-edit actions
     */
    protected List<SimulationUserActionDescriptor<CTX>> createUserActionDescriptors() {
        return List.of();
    }

    private List<Node> createEditToolBarNodes(
            List<SimulationUserActionDescriptor<CTX>> descriptors,
            BooleanProperty currentEditModeProperty,
            @Nullable ObjectProperty<String> currentSelectedToolIdProperty) {

        ToggleGroup cellActionToggleGroup = new ToggleGroup();
        ToggleButton selectButton = null;

        List<Node> nodes = new ArrayList<>();

        Map<Toggle, SimulationUserActionDescriptor<CTX>> descriptorByToggle = new HashMap<>();

        if (currentSelectedToolIdProperty != null) {
            selectButton = new ToggleButton(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_SELECT));
            configureEditToolBarButton(selectButton);
            selectButton.setTooltip(new Tooltip(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_SELECT_TOOLTIP)));
            selectButton.setToggleGroup(cellActionToggleGroup);
            selectButton.visibleProperty().bind(currentEditModeProperty);
            selectButton.managedProperty().bind(currentEditModeProperty);
            nodes.add(selectButton);
        }

        for (var descriptor : descriptors) {
            if ((descriptor.scope() == SimulationUserActionScope.CELL_SELECTED)
                    && (currentSelectedToolIdProperty != null)) {
                ToggleButton actionButton = new ToggleButton(AppLocalization.getText(descriptor.labelKey()));
                configureEditToolBarButton(actionButton);
                actionButton.setTooltip(new Tooltip(AppLocalization.getText(descriptor.tooltipKey())));
                actionButton.setToggleGroup(cellActionToggleGroup);
                actionButton.visibleProperty().bind(currentEditModeProperty);
                actionButton.managedProperty().bind(currentEditModeProperty);
                nodes.add(actionButton);
                descriptorByToggle.put(actionButton, descriptor);
            } else if (descriptor.scope() == SimulationUserActionScope.GLOBAL) {
                Button actionButton = new Button(AppLocalization.getText(descriptor.labelKey()));
                configureEditToolBarButton(actionButton);
                actionButton.setTooltip(new Tooltip(AppLocalization.getText(descriptor.tooltipKey())));
                actionButton.setOnAction(_ -> applyGlobalUserActionAndRedraw(descriptor));
                actionButton.visibleProperty().bind(currentEditModeProperty);
                actionButton.managedProperty().bind(currentEditModeProperty);
                nodes.add(actionButton);
            }
        }

        Node optionPanelNode = createEditToolBarOptionPanel(currentSelectedToolIdProperty);
        if (optionPanelNode != null) {
            optionPanelNode.visibleProperty().bind(currentEditModeProperty);
            optionPanelNode.managedProperty().bind(currentEditModeProperty);
            nodes.add(optionPanelNode);
        }

        if (currentSelectedToolIdProperty != null) {
            selectedToolIdProperty = currentSelectedToolIdProperty;
            var finalSelectButton = selectButton;

            selectedToolIdListener = (_, _, toolId) -> {
                if (SimulationUserActionDescriptor.SELECT_TOOL_ID.equals(toolId)) {
                    cellActionToggleGroup.selectToggle(finalSelectButton);
                    return;
                }
                for (var entry : descriptorByToggle.entrySet()) {
                    if (toolId.equals(entry.getValue().toolId())) {
                        cellActionToggleGroup.selectToggle(entry.getKey());
                        return;
                    }
                }
                cellActionToggleGroup.selectToggle(finalSelectButton);
            };
            currentSelectedToolIdProperty.addListener(selectedToolIdListener);

            String currentToolId = currentSelectedToolIdProperty.get();
            selectedToolIdListener.changed(currentSelectedToolIdProperty, currentToolId, currentToolId);
        }

        cellActionToggleGroup.selectedToggleProperty().addListener((_, _, selectedToggle) -> {
            if (currentSelectedToolIdProperty == null) {
                return;
            }
            var descriptor = descriptorByToggle.get(selectedToggle);
            currentSelectedToolIdProperty.set(
                    (descriptor != null)
                            ? descriptor.toolId()
                            : SimulationUserActionDescriptor.SELECT_TOOL_ID);
        });

        return nodes;
    }

    /**
     * Creates simulation-specific edit option controls shown in expanded edit mode.
     *
     * <p>The returned node is managed by the common edit-toolbar lifecycle and will be made visible/managed only while
     * edit mode is active. Implementations should keep descriptor metadata and JavaFX node creation separate.
     *
     * @param selectedToolId selected stable tool id used for enablement bindings
     * @return option-panel node, or {@code null} when the simulation has no option controls
     */
    @SuppressWarnings("unused")
    protected @Nullable Node createEditToolBarOptionPanel(@Nullable ObjectProperty<String> selectedToolId) {
        return null;
    }

    /**
     * Registers a cleanup action invoked when the edit toolbar is rebuilt or the simulation shuts down.
     *
     * @param cleanupAction cleanup action for listeners or bindings owned by simulation-specific option controls
     */
    protected final void registerActionToolBarCleanup(Runnable cleanupAction) {
        actionToolBarCleanupActions.add(cleanupAction);
    }

    private void configureEditToolBarButton(ButtonBase button) {
        button.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_BUTTON);
    }

    /**
     * Exposes edit-mode state used by the compact Edit affordance.
     *
     * @return editable state property, or {@code null} when edit mode is not supported
     */
    protected @Nullable BooleanProperty editModeActiveProperty() {
        return null;
    }

    /**
     * Exposes the selected stable tool id used by toolbar toggle tools.
     *
     * @return selected tool-id property, or {@code null} when tool selection is not supported
     */
    protected @Nullable ObjectProperty<String> selectedUserActionToolIdProperty() {
        return null;
    }

    /**
     * Applies a global-scoped user action descriptor and refreshes the simulation view as needed.
     *
     * @param descriptor descriptor to apply
     */
    protected abstract void applyGlobalUserActionAndRedraw(SimulationUserActionDescriptor<CTX> descriptor);

    protected final Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, dynamicCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(dynamicCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        canvasBorderPane.setCenter(stackPane);

        canvasScrollPane.setContent(canvasBorderPane);
        canvasScrollPane.setFitToHeight(false);
        canvasScrollPane.setFitToWidth(false);
        canvasScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        canvasScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        canvasScrollPane.setPannable(true);

        VBox vBox = new VBox();
        vBox.getChildren().add(notificationLabel);
        vBox.getChildren().add(editAffordanceScrollPane);
        vBox.getChildren().add(canvasScrollPane);
        VBox.setVgrow(canvasScrollPane, Priority.ALWAYS);

        return vBox;
    }

    protected final CellDimension createPainterAndUpdateCanvas(GridStructure structure, double cellEdgeLength) {
        if ((basePainter != null) && (dynamicPainter != null) && (overlayPainter != null)) {
            basePainter.clearCanvasBackground();
            dynamicPainter.clearCanvasBackground();
            overlayPainter.clearCanvasBackground();
            basePainter = null;
            dynamicPainter = null;
            overlayPainter = null;
        }

        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(MAX_CANVAS_WIDTH, Math.ceil(basePainter.gridDimension2D().getWidth())));
        baseCanvas.setHeight(Math.min(MAX_CANVAS_HEIGHT, Math.ceil(basePainter.gridDimension2D().getHeight())));

        dynamicPainter = new FXGridCanvasPainter(dynamicCanvas, structure, cellEdgeLength);
        dynamicCanvas.setWidth(baseCanvas.getWidth());
        dynamicCanvas.setHeight(baseCanvas.getHeight());

        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(baseCanvas.getWidth());
        overlayCanvas.setHeight(baseCanvas.getHeight());

        CellDimension cellDimension = basePainter.cellDimension();

        AppLogger.infof("%s: Canvas painter created. painter=%s", LOG_COMPONENT, basePainter);
        if ((baseCanvas.getWidth() < basePainter.gridDimension2D().getWidth()) ||
                (baseCanvas.getHeight() < basePainter.gridDimension2D().getHeight())) {
            AppLogger.warnf("%s: Canvas size is smaller than the grid dimension.", LOG_COMPONENT);
            viewModel.setNotificationType(SimulationNotificationType.CANVAS_SIZE_LIMIT);
        }

        // Font
        double fontSize = computeCellFontSize(cellDimension, structure.cellShape());
        if (fontSize >= MIN_CELL_FONT_SIZE) {
            cellFont = getPreferredFont(fontSize);
            AppLogger.infof("%s: Cell font created. font=%s", LOG_COMPONENT, cellFont);
        } else {
            cellFont = null;
            AppLogger.infof("%s: Cell font not created because font size is too small. fontSize=%.1f", LOG_COMPONENT, fontSize);
        }
        double fontSizeEmoji = computeCellEmojiFontSize(cellDimension, structure.cellShape());
        if (fontSizeEmoji >= MIN_EMOJI_FONT_SIZE) {
            cellEmojiFont = getPreferredFont(fontSizeEmoji);
            AppLogger.infof("%s: Cell emoji font created. font=%s", LOG_COMPONENT, cellEmojiFont);
        } else {
            cellEmojiFont = null;
            AppLogger.infof("%s: Cell emoji font not created because font size is too small. fontSize=%.1f", LOG_COMPONENT, fontSizeEmoji);
        }

        return cellDimension;
    }

    protected final void updateCanvasBorderPane(GridStructure structure) {
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

    @SuppressWarnings("MagicNumber")
    protected final Point2D computeVisibleCanvasCenter(Canvas canvas) {
        double viewportWidth = canvasScrollPane.getViewportBounds().getWidth();
        double viewportHeight = canvasScrollPane.getViewportBounds().getHeight();

        double hValue = canvasScrollPane.getHvalue();
        double vValue = canvasScrollPane.getVvalue();

        double maxX = canvas.getWidth() - viewportWidth;
        double maxY = canvas.getHeight() - viewportHeight;
        double x0 = (maxX > 0) ? (hValue * maxX) : 0;
        double y0 = (maxY > 0) ? (vValue * maxY) : 0;

        double x = x0 + (viewportWidth / 2.0d);
        double y = y0 + (viewportHeight / 2.0d);

        return new Point2D(x, y);
    }

    protected final Font getPreferredFont(double size) {
        return fontCache.computeIfAbsent(size, s -> {
            if (Font.getFamilies().contains(MAIN_FONT_FAMILY)) {
                return Font.font(MAIN_FONT_FAMILY, s);
            } else {
                return Font.font(FALLBACK_FONT_FAMILY, s);
            }
        });
    }

}
