package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationState;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import de.mkalb.etpetssim.ui.InputDoubleIntRangeProperty;
import de.mkalb.etpetssim.ui.InputDoubleProperty;
import de.mkalb.etpetssim.ui.InputIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AbstractConfigViewTest {

    private static final String VALIDATION_MESSAGE = "invalid";
    private static final String TITLE = "title";

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static TestConfigView createView(boolean multipleChoices) {
        return new TestConfigView(new TestConfigViewModel(new SimpleObjectProperty<>(SimulationState.READY),
                multipleChoices));
    }

    private static FXComponentFactory.LabeledControl<TextField> createLabeledTextField() {
        return new FXComponentFactory.LabeledControl<>(new Label(), new TextField());
    }

    private static String tooltipText(FXComponentFactory.LabeledControl<? extends Region> labeledControl) {
        return labeledControl.label().getTooltip().getText();
    }

    private static List<Node> paneContent(TitledPane pane) {
        return assertInstanceOf(VBox.class, pane.getContent()).getChildren();
    }

    @Test
    void testCreateConfigMainBox() {
        FXTestSupport.runAndWait(() -> {
            TestConfigView view = createView(true);
            TitledPane pane = view.createConfigTitledPane(TITLE);

            Region mainBox = view.createConfigMainBox(pane);

            VBox contentBox = assertInstanceOf(VBox.class, assertInstanceOf(ScrollPane.class, mainBox).getContent());
            HBox paneBox = assertInstanceOf(HBox.class, contentBox.getChildren().get(0));
            Label validationLabel = assertInstanceOf(Label.class, contentBox.getChildren().get(1));
            boolean visibleWithoutIssues = validationLabel.isVisible() || validationLabel.isManaged();
            view.viewModel.registerRule(new SimpleBooleanProperty(true), VALIDATION_MESSAGE);

            assertAll(
                    () -> assertEquals(List.of(pane), paneBox.getChildren()),
                    () -> assertFalse(visibleWithoutIssues),
                    () -> assertTrue(validationLabel.isVisible()),
                    () -> assertTrue(validationLabel.isManaged()),
                    () -> assertEquals(VALIDATION_MESSAGE, validationLabel.getText())
            );
        });
    }

    @Test
    void testCreateConfigTitledPane() {
        FXTestSupport.runAndWait(() -> {
            TestConfigView view = createView(true);
            var first = createLabeledTextField();
            var second = createLabeledTextField();

            TitledPane pane = view.createConfigTitledPane(TITLE, first, second);

            assertAll(
                    () -> assertEquals(TITLE, pane.getText()),
                    () -> assertEquals(List.of(first.label(), first.controlRegion(), second.label(),
                            second.controlRegion()), paneContent(pane)),
                    () -> assertTrue(pane.isCollapsible()),
                    () -> assertTrue(pane.isExpanded()),
                    () -> assertFalse(pane.isDisabled())
            );
        });
    }

    @Test
    void testCreateConfigTitledPaneDisablesControlsWhenNotStartable() {
        FXTestSupport.runAndWait(() -> {
            TestConfigView view = createView(true);
            var labeledControl = createLabeledTextField();
            view.createConfigTitledPane(TITLE, labeledControl);
            boolean disabledWhenReady = labeledControl.controlRegion().isDisabled();

            view.viewModel.simulationState().set(SimulationState.PAUSED);

            assertAll(
                    () -> assertFalse(disabledWhenReady),
                    () -> assertTrue(labeledControl.controlRegion().isDisabled()),
                    () -> assertFalse(labeledControl.label().isDisabled())
            );
        });
    }

    @Test
    void testCreateConfigTitledPaneDisablesEmptyPane() {
        FXTestSupport.runAndWait(() -> {
            TitledPane pane = createView(true).createConfigTitledPane(TITLE);

            assertAll(
                    () -> assertTrue(paneContent(pane).isEmpty()),
                    () -> assertFalse(pane.isCollapsible()),
                    () -> assertFalse(pane.isExpanded()),
                    () -> assertTrue(pane.isDisabled())
            );
        });
    }

    @Test
    void testBindVisibilityToCellShape() {
        FXTestSupport.runAndWait(() -> {
            TestConfigView view = createView(true);
            Label label = new Label();
            Region controlRegion = new Region();
            view.bindVisibilityToCellShape(label, controlRegion, CellShape.HEXAGON);
            boolean visibleForSquare = label.isVisible() || label.isManaged()
                    || controlRegion.isVisible() || controlRegion.isManaged();

            view.viewModel.cellShapeProperty().setValue(CellShape.HEXAGON);

            assertAll(
                    () -> assertFalse(visibleForSquare),
                    () -> assertTrue(label.isVisible()),
                    () -> assertTrue(label.isManaged()),
                    () -> assertTrue(controlRegion.isVisible()),
                    () -> assertTrue(controlRegion.isManaged())
            );
        });
    }

    @Test
    void testCreateStructurePane() {
        FXTestSupport.runAndWait(() -> {
            TestConfigView view = createView(true);

            TitledPane pane = view.createStructurePane();

            Label gridWidthLabel = assertInstanceOf(Label.class, paneContent(pane).get(4));
            assertAll(
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_STRUCTURE),
                            pane.getText()),
                    () -> assertEquals(8, paneContent(pane).size()),
                    () -> assertEquals(AppLocalization.getFormattedText(
                                    SimulationLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP, 10, 1_000),
                            gridWidthLabel.getTooltip().getText())
            );
        });
    }

    @Test
    void testCreateStructurePaneOmitsSingleChoiceControls() {
        FXTestSupport.runAndWait(() -> {
            TitledPane pane = createView(false).createStructurePane();

            assertEquals(4, paneContent(pane).size());
        });
    }

    @Test
    void testCreateLayoutPane() {
        FXTestSupport.runAndWait(() -> {
            TestConfigView view = createView(true);
            var additional = createLabeledTextField();

            TitledPane pane = view.createLayoutPane(additional);

            Label cellEdgeLengthLabel = assertInstanceOf(Label.class, paneContent(pane).getFirst());
            assertAll(
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.CONFIG_TITLE_LAYOUT),
                            pane.getText()),
                    () -> assertEquals(6, paneContent(pane).size()),
                    () -> assertSame(additional.controlRegion(), paneContent(pane).getLast()),
                    () -> assertEquals(AppLocalization.getFormattedText(
                                    SimulationLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, 4, 20),
                            cellEdgeLengthLabel.getTooltip().getText())
            );
        });
    }

    @Test
    void testCreateLayoutPaneOmitsSingleChoiceControls() {
        FXTestSupport.runAndWait(() -> {
            TitledPane pane = createView(false).createLayoutPane();

            assertEquals(2, paneContent(pane).size());
        });
    }

    @Test
    void testCreateSeedControl() {
        FXTestSupport.runAndWait(() -> {
            var seedControl = createView(true).createSeedControl();

            assertEquals(AppLocalization.getText(SimulationLocalizationKeys.CONFIG_SEED_TOOLTIP),
                    tooltipText(seedControl));
        });
    }

    @Test
    void testCreateNeighborhoodModeControl() {
        FXTestSupport.runAndWait(() -> {
            InputChoiceProperty<NeighborhoodMode> neighborhoodMode = InputChoiceProperty.ofList(
                    NeighborhoodMode.EDGES_ONLY, List.of(NeighborhoodMode.values()), NeighborhoodMode::name);

            var neighborhoodModeControl = createView(true).createNeighborhoodModeControl(neighborhoodMode);

            assertEquals(AppLocalization.getText(SimulationLocalizationKeys.CONFIG_NEIGHBORHOOD_MODE_TOOLTIP),
                    tooltipText(neighborhoodModeControl));
        });
    }

    @Test
    void testFormatPercentRangeTooltip() {
        String tooltip = createView(true).formatPercentRangeTooltip("conway.config.alivepercent.tooltip",
                InputDoubleProperty.of(0.1d, 0.0d, 0.5d));

        assertEquals("Set the initial percentage of alive cells (0% - 50%)", tooltip);
    }

    @Test
    void testFormatDoubleRangeTooltip() {
        String tooltip = createView(true).formatDoubleRangeTooltip("forest.config.treegrowthprobability.tooltip",
                InputDoubleProperty.of(0.01d, 0.001d, 0.25d));

        assertEquals("Set the probability that a new tree grows in an empty cell (0.001 - 0.250)", tooltip);
    }

    @Test
    void testFormatIntRangeTooltip() {
        String tooltip = createView(true).formatIntRangeTooltip(SimulationLocalizationKeys.CONFIG_GRID_WIDTH_TOOLTIP,
                InputIntegerProperty.of(40, 10, 1_000, 2));

        assertEquals("Set the width of the grid in cells (10 - 1,000 cells)", tooltip);
    }

    @Test
    void testFormatIntRangeTooltipWithDoubleIntRangeProperty() {
        String tooltip = createView(true).formatIntRangeTooltip(
                SimulationLocalizationKeys.CONFIG_CELL_EDGE_LENGTH_TOOLTIP, InputDoubleIntRangeProperty.of(12, 4, 20));

        assertEquals("Adjust the edge length of each cell in pixels (4 - 20 pixels)", tooltip);
    }

    private static final class TestConfigViewModel extends AbstractConfigViewModel<ConwayConfig> {

        private final ObjectProperty<SimulationState> simulationState;

        TestConfigViewModel(ObjectProperty<SimulationState> simulationState, boolean multipleChoices) {
            super(simulationState, new CommonConfigConstraints(
                    CellShape.SQUARE,
                    multipleChoices ? List.of(CellShape.SQUARE, CellShape.HEXAGON) : List.of(CellShape.SQUARE),
                    GridEdgeBehavior.BLOCK_XY,
                    multipleChoices ? List.of(GridEdgeBehavior.BLOCK_XY, GridEdgeBehavior.WRAP_XY)
                            : List.of(GridEdgeBehavior.BLOCK_XY),
                    40, 10, 1_000, 2,
                    30, 10, 800, 2,
                    12, 4, 20,
                    CellDisplayMode.SHAPE,
                    multipleChoices ? List.of(CellDisplayMode.values()) : List.of(CellDisplayMode.SHAPE),
                    ""));
            this.simulationState = simulationState;
        }

        ObjectProperty<SimulationState> simulationState() {
            return simulationState;
        }

        void registerRule(ObservableBooleanValue violatedCondition, String message) {
            registerConfigValidationRule(violatedCondition, () -> message);
        }

        @NonNull
        @Override
        public ConwayConfig createConfig() {
            throw new UnsupportedOperationException();
        }

    }

    private static final class TestConfigView extends AbstractConfigView<ConwayConfig, TestConfigViewModel> {

        TestConfigView(TestConfigViewModel viewModel) {
            super(viewModel);
        }

        @NonNull
        @Override
        public Region buildConfigRegion() {
            return createConfigMainBox(createStructurePane(), createLayoutPane());
        }

    }

}
