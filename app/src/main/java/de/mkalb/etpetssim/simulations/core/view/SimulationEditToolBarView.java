package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

final class SimulationEditToolBarView<CTX extends SimulationUserActionContext> {

    private static final String LOG_COMPONENT = "SimulationEditToolBarView";

    private final ScrollPane editAffordanceScrollPane;
    private final HBox editAffordanceBox;
    private final List<Runnable> actionToolBarCleanupActions = new ArrayList<>();
    private @Nullable Button editModeButton;
    private @Nullable ObjectProperty<String> selectedToolIdProperty;
    private @Nullable ChangeListener<String> selectedToolIdListener;

    SimulationEditToolBarView() {
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
        clear();
    }

    private static double clampZeroToOne(double value) {
        return Math.clamp(value, 0.0d, 1.0d);
    }

    Region getNode() {
        return editAffordanceScrollPane;
    }

    void registerCleanup(Runnable cleanupAction) {
        actionToolBarCleanupActions.add(cleanupAction);
    }

    void clear() {
        clearEditAffordanceToolBar();
        clearActionToolBar();
    }

    void rebuild(
            List<SimulationUserActionDescriptor<CTX>> descriptors,
            @Nullable BooleanProperty editModeProperty,
            @Nullable ObjectProperty<String> currentSelectedToolIdProperty,
            ReadOnlyObjectProperty<SimulationState> simulationStateProperty,
            Function<@Nullable ObjectProperty<String>, @Nullable Node> optionPanelFactory,
            Consumer<SimulationUserActionDescriptor<CTX>> globalActionHandler) {

        clear();
        if (descriptors.isEmpty() || (editModeProperty == null)) {
            return;
        }

        List<Node> nodes = createEditToolBarNodes(descriptors,
                editModeProperty,
                currentSelectedToolIdProperty,
                optionPanelFactory,
                globalActionHandler);
        if (nodes.isEmpty()) {
            return;
        }

        Button editButton = new Button();
        configureEditToolBarButton(editButton);
        editButton.setText(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_EDIT));
        editButton.setTooltip(new Tooltip(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_EDIT_TOOLTIP)));
        editButton.setOnAction(_ -> editModeProperty.set(!editModeProperty.get()));
        editButton.visibleProperty().bind(editModeProperty.not());
        editButton.managedProperty().bind(editModeProperty.not());

        editModeButton = editButton;

        List<Node> toolbarNodes = new ArrayList<>();
        toolbarNodes.add(editButton);
        toolbarNodes.addAll(nodes);
        editAffordanceBox.getChildren().setAll(toolbarNodes);
        editAffordanceScrollPane.disableProperty().bind(simulationStateProperty.isNotEqualTo(SimulationState.PAUSED));
        editAffordanceScrollPane.setVisible(true);
        editAffordanceScrollPane.setManaged(true);
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

    private List<Node> createEditToolBarNodes(
            List<SimulationUserActionDescriptor<CTX>> descriptors,
            BooleanProperty editModeProperty,
            @Nullable ObjectProperty<String> currentSelectedToolIdProperty,
            Function<@Nullable ObjectProperty<String>, @Nullable Node> optionPanelFactory,
            Consumer<SimulationUserActionDescriptor<CTX>> globalActionHandler) {

        ToggleGroup cellActionToggleGroup = new ToggleGroup();
        ToggleButton selectButton = null;

        List<Node> nodes = new ArrayList<>();

        Map<Toggle, SimulationUserActionDescriptor<CTX>> descriptorByToggle = new HashMap<>();

        if (currentSelectedToolIdProperty != null) {
            selectButton = new ToggleButton(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_SELECT));
            configureEditToolBarButton(selectButton);
            selectButton.setTooltip(new Tooltip(AppLocalization.getText(AppLocalizationKeys.SIMULATION_TOOLBAR_SELECT_TOOLTIP)));
            selectButton.setToggleGroup(cellActionToggleGroup);
            selectButton.visibleProperty().bind(editModeProperty);
            selectButton.managedProperty().bind(editModeProperty);
            nodes.add(selectButton);
        }

        for (var descriptor : descriptors) {
            if ((descriptor.scope() == SimulationUserActionScope.CELL_SELECTED)
                    && (currentSelectedToolIdProperty != null)) {
                ToggleButton actionButton = new ToggleButton(AppLocalization.getText(descriptor.labelKey()));
                configureEditToolBarButton(actionButton);
                actionButton.setTooltip(new Tooltip(AppLocalization.getText(descriptor.tooltipKey())));
                actionButton.setToggleGroup(cellActionToggleGroup);
                actionButton.visibleProperty().bind(editModeProperty);
                actionButton.managedProperty().bind(editModeProperty);
                nodes.add(actionButton);
                descriptorByToggle.put(actionButton, descriptor);
            } else if (descriptor.scope() == SimulationUserActionScope.GLOBAL) {
                Button actionButton = new Button(AppLocalization.getText(descriptor.labelKey()));
                configureEditToolBarButton(actionButton);
                actionButton.setTooltip(new Tooltip(AppLocalization.getText(descriptor.tooltipKey())));
                actionButton.setOnAction(_ -> globalActionHandler.accept(descriptor));
                actionButton.visibleProperty().bind(editModeProperty);
                actionButton.managedProperty().bind(editModeProperty);
                nodes.add(actionButton);
            }
        }

        Node optionPanelNode = optionPanelFactory.apply(currentSelectedToolIdProperty);
        if (optionPanelNode != null) {
            optionPanelNode.visibleProperty().bind(editModeProperty);
            optionPanelNode.managedProperty().bind(editModeProperty);
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

    private void configureEditToolBarButton(ButtonBase button) {
        button.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_BUTTON);
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

}


