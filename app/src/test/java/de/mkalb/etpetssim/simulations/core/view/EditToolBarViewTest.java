package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionScope;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class EditToolBarViewTest {

    private static final String TOOL_ID_CELL = "test.cell";
    private static final String TOOL_ID_GLOBAL = "test.global";
    private static final String TOOL_ID_UNKNOWN = "test.unknown";
    private static final String LABEL_KEY_CELL = "conway.toolbar.togglecell";
    private static final String TOOLTIP_KEY_CELL = "conway.toolbar.togglecell.tooltip";
    private static final String LABEL_KEY_GLOBAL = "conway.toolbar.cleargrid";
    private static final String TOOLTIP_KEY_GLOBAL = "conway.toolbar.cleargrid.tooltip";

    private static final SimulationUserActionDescriptor<NoUserActionContext> CELL_DESCRIPTOR =
            new SimulationUserActionDescriptor<>(TOOL_ID_CELL, NoUserActionContext.NO_CONTEXT,
                    SimulationUserActionScope.SELECTED_CELL, LABEL_KEY_CELL, TOOLTIP_KEY_CELL);
    private static final SimulationUserActionDescriptor<NoUserActionContext> GLOBAL_DESCRIPTOR =
            new SimulationUserActionDescriptor<>(TOOL_ID_GLOBAL, NoUserActionContext.NO_CONTEXT,
                    SimulationUserActionScope.GLOBAL, LABEL_KEY_GLOBAL, TOOLTIP_KEY_GLOBAL);

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static HBox toolbarBox(EditToolBarView<NoUserActionContext> view) {
        return assertInstanceOf(HBox.class, assertInstanceOf(ScrollPane.class, view.getNode()).getContent());
    }

    private static Fixture createRebuiltFixture(Node optionPanel) {
        var fixture = new Fixture(new EditToolBarView<>(), new SimpleBooleanProperty(false),
                new SimpleObjectProperty<>(SimulationUserActionDescriptor.SELECT_TOOL_ID),
                new SimpleObjectProperty<>(SimulationState.PAUSED), new ArrayList<>());
        fixture.rebuild(List.of(CELL_DESCRIPTOR, GLOBAL_DESCRIPTOR), optionPanel);
        return fixture;
    }

    private static void assertTooltip(String expectedTooltipKey, ButtonBase button) {
        assertEquals(AppLocalization.getText(expectedTooltipKey), button.getTooltip().getText());
    }

    @Test
    void testConstructor() {
        FXTestSupport.runAndWait(() -> {
            var view = new EditToolBarView<NoUserActionContext>();

            assertAll(
                    () -> assertFalse(view.getNode().isVisible()),
                    () -> assertFalse(view.getNode().isManaged()),
                    () -> assertTrue(view.getNode().isDisabled()),
                    () -> assertTrue(toolbarBox(view).getChildren().isEmpty())
            );
        });
    }

    @Test
    void testRegisterCleanup() {
        FXTestSupport.runAndWait(() -> {
            var view = new EditToolBarView<NoUserActionContext>();
            AtomicInteger cleanupCount = new AtomicInteger();
            view.registerCleanup(cleanupCount::incrementAndGet);

            view.clear();
            view.clear();

            assertEquals(1, cleanupCount.get());
        });
    }

    @Test
    void testRegisterCleanupContinuesAfterFailingAction() {
        FXTestSupport.runAndWait(() -> {
            var view = new EditToolBarView<NoUserActionContext>();
            AtomicInteger cleanupCount = new AtomicInteger();
            view.registerCleanup(() -> {
                throw new IllegalStateException("Test cleanup failure");
            });
            view.registerCleanup(cleanupCount::incrementAndGet);

            view.clear();

            assertEquals(1, cleanupCount.get());
        });
    }

    @Test
    void testClear() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());
            ToggleButton cellButton = fixture.toggleButton(2);

            fixture.view().clear();
            fixture.selectedToolId().set(TOOL_ID_CELL);

            assertAll(
                    () -> assertFalse(fixture.view().getNode().isVisible()),
                    () -> assertFalse(fixture.view().getNode().isManaged()),
                    () -> assertTrue(fixture.view().getNode().isDisabled()),
                    () -> assertTrue(toolbarBox(fixture.view()).getChildren().isEmpty()),
                    () -> assertFalse(cellButton.isSelected(), "listener of the selected tool id must be removed")
            );
        });
    }

    @Test
    void testRebuild() {
        FXTestSupport.runAndWait(() -> {
            Label optionPanel = new Label();
            Fixture fixture = createRebuiltFixture(optionPanel);
            List<Node> nodes = toolbarBox(fixture.view()).getChildren();

            assertAll(
                    () -> assertEquals(5, nodes.size()),
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.SIMULATION_TOOLBAR_EDIT),
                            fixture.button(0).getText()),
                    () -> assertTooltip(SimulationLocalizationKeys.SIMULATION_TOOLBAR_EDIT_TOOLTIP,
                            fixture.button(0)),
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.SIMULATION_TOOLBAR_SELECT),
                            fixture.toggleButton(1).getText()),
                    () -> assertTooltip(SimulationLocalizationKeys.SIMULATION_TOOLBAR_SELECT_TOOLTIP,
                            fixture.toggleButton(1)),
                    () -> assertEquals(AppLocalization.getText(LABEL_KEY_CELL), fixture.toggleButton(2).getText()),
                    () -> assertTooltip(TOOLTIP_KEY_CELL, fixture.toggleButton(2)),
                    () -> assertEquals(AppLocalization.getText(LABEL_KEY_GLOBAL), fixture.button(3).getText()),
                    () -> assertTooltip(TOOLTIP_KEY_GLOBAL, fixture.button(3)),
                    () -> assertSame(optionPanel, nodes.get(4)),
                    () -> assertSame(fixture.toggleButton(1).getToggleGroup(), fixture.toggleButton(2).getToggleGroup()),
                    () -> assertTrue(fixture.view().getNode().isVisible()),
                    () -> assertTrue(fixture.view().getNode().isManaged())
            );
        });
    }

    @Test
    void testRebuildIgnoresEmptyDescriptors() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());

            fixture.rebuild(List.of(), new Label());

            assertAll(
                    () -> assertFalse(fixture.view().getNode().isVisible()),
                    () -> assertTrue(toolbarBox(fixture.view()).getChildren().isEmpty())
            );
        });
    }

    @Test
    void testRebuildOmitsMissingOptionPanel() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());

            fixture.rebuild(List.of(CELL_DESCRIPTOR, GLOBAL_DESCRIPTOR), null);

            assertEquals(4, toolbarBox(fixture.view()).getChildren().size());
        });
    }

    @Test
    void testRebuildRunsCleanupOfPreviousToolbar() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());
            AtomicInteger cleanupCount = new AtomicInteger();
            fixture.view().registerCleanup(cleanupCount::incrementAndGet);

            fixture.rebuild(List.of(CELL_DESCRIPTOR), new Label());

            assertAll(
                    () -> assertEquals(1, cleanupCount.get()),
                    () -> assertEquals(4, toolbarBox(fixture.view()).getChildren().size())
            );
        });
    }

    @Test
    void testRebuildBindsDisabledToPausedState() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());
            boolean disabledWhenPaused = fixture.view().getNode().isDisabled();

            fixture.simulationState().set(SimulationState.RUNNING_TIMED);

            assertAll(
                    () -> assertFalse(disabledWhenPaused),
                    () -> assertTrue(fixture.view().getNode().isDisabled())
            );
        });
    }

    @Test
    void testRebuildBindsVisibilityToEditMode() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());
            List<Node> nodes = toolbarBox(fixture.view()).getChildren();
            List<Boolean> visibleBeforeEdit = nodes.stream().map(Node::isVisible).toList();

            fixture.button(0).fire();

            assertAll(
                    () -> assertEquals(List.of(true, false, false, false, false), visibleBeforeEdit),
                    () -> assertTrue(fixture.editMode().get()),
                    () -> assertEquals(List.of(false, true, true, true, true),
                            nodes.stream().map(Node::isVisible).toList()),
                    () -> assertEquals(List.of(false, true, true, true, true),
                            nodes.stream().map(Node::isManaged).toList())
            );
        });
    }

    @Test
    void testRebuildSelectsToggleOfSelectedToolId() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());
            boolean selectSelectedInitially = fixture.toggleButton(1).isSelected();

            fixture.selectedToolId().set(TOOL_ID_CELL);
            boolean cellSelectedForCellTool = fixture.toggleButton(2).isSelected();
            fixture.selectedToolId().set(TOOL_ID_UNKNOWN);

            assertAll(
                    () -> assertTrue(selectSelectedInitially),
                    () -> assertTrue(cellSelectedForCellTool),
                    () -> assertTrue(fixture.toggleButton(1).isSelected()),
                    () -> assertEquals(SimulationUserActionDescriptor.SELECT_TOOL_ID, fixture.selectedToolId().get())
            );
        });
    }

    @Test
    void testRebuildUpdatesSelectedToolIdFromToggle() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());

            fixture.toggleButton(2).fire();
            String toolIdAfterCellToggle = fixture.selectedToolId().get();
            fixture.toggleButton(1).fire();

            assertAll(
                    () -> assertEquals(TOOL_ID_CELL, toolIdAfterCellToggle),
                    () -> assertEquals(SimulationUserActionDescriptor.SELECT_TOOL_ID, fixture.selectedToolId().get())
            );
        });
    }

    @Test
    void testRebuildForwardsGlobalAction() {
        FXTestSupport.runAndWait(() -> {
            Fixture fixture = createRebuiltFixture(new Label());

            fixture.button(3).fire();

            assertEquals(List.of(GLOBAL_DESCRIPTOR), fixture.globalActions());
        });
    }

    private record Fixture(
            EditToolBarView<NoUserActionContext> view,
            BooleanProperty editMode,
            ObjectProperty<String> selectedToolId,
            ObjectProperty<SimulationState> simulationState,
            List<SimulationUserActionDescriptor<NoUserActionContext>> globalActions) {

        void rebuild(List<SimulationUserActionDescriptor<NoUserActionContext>> descriptors, Node optionPanel) {
            view.rebuild(descriptors, editMode, selectedToolId, simulationState, _ -> optionPanel,
                    globalActions::add);
        }

        Button button(int index) {
            return assertInstanceOf(Button.class, toolbarBox(view).getChildren().get(index));
        }

        ToggleButton toggleButton(int index) {
            return assertInstanceOf(ToggleButton.class, toolbarBox(view).getChildren().get(index));
        }

    }

}
