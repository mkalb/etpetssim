package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremum;
import de.mkalb.etpetssim.simulations.core.model.StatisticsMetric;
import de.mkalb.etpetssim.simulations.core.viewmodel.ObservationViewModel;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AbstractObservationViewTest {

    private static final GridCoordinate SELECTED_COORDINATE = new GridCoordinate(3, 4);
    private static final String KEY_NOT_FINITE = "notFinite";
    private static final int GRID_WIDTH = 40;
    private static final int GRID_HEIGHT = 30;

    private static GridEntityDescriptorRegistry entityDescriptorRegistry;

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        entityDescriptorRegistry = GridEntityDescriptorRegistry.of(ConwayEntity.values());
        FXTestSupport.ensureStarted();
    }

    private static TestObservationView createView() {
        return new TestObservationView(new ObservationViewModel<>(), entityDescriptorRegistry);
    }

    private static ConwayStatistics createStatistics(int stepCount) {
        var structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GRID_WIDTH, GRID_HEIGHT));
        var statistics = new ConwayStatistics(structure);
        statistics.setStepCount(stepCount);
        return statistics;
    }

    private static String unknown() {
        return AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
    }

    private static String nameText(String key) {
        return AppLocalization.getText(key) + ":";
    }

    private static Label titleLabel(VBox section) {
        return assertInstanceOf(Label.class, section.getChildren().getFirst());
    }

    private static GridPane grid(VBox section) {
        return assertInstanceOf(GridPane.class, section.getChildren().get(1));
    }

    private static Optional<Node> findCell(VBox section, int column, int row) {
        return grid(section).getChildren().stream()
                .filter(node -> Objects.equals(column, GridPane.getColumnIndex(node))
                        && Objects.equals(row, GridPane.getRowIndex(node)))
                .findFirst();
    }

    private static Label cell(VBox section, int column, int row) {
        return assertInstanceOf(Label.class, findCell(section, column, row).orElseThrow());
    }

    @Test
    void testInitializeObservationLabels() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            VBox gridSection = view.createGridSection();
            VBox statusSection = view.createStatusSection();
            view.initializeObservationLabels();
            String totalCellsWithoutStatistics = cell(gridSection, 1, 0).getText();

            view.viewModel.setStatistics(createStatistics(1_234));
            view.initializeObservationLabels();

            assertAll(
                    () -> assertEquals(unknown(), totalCellsWithoutStatistics),
                    () -> assertEquals("1,200", cell(gridSection, 1, 0).getText()),
                    () -> assertEquals("1,234", cell(statusSection, 1, 0).getText())
            );
        });
    }

    @Test
    void testShutdownObservation() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            view.registerSelectedCellListener(view.viewModel.selectedGridCellProperty());
            boolean registeredBeforeShutdown = view.hasSelectedGridCellListener();

            view.shutdownObservation();

            assertAll(
                    () -> assertTrue(registeredBeforeShutdown),
                    () -> assertFalse(view.hasSelectedGridCellListener())
            );
        });
    }

    @Test
    void testCreateObservationScrollPane() {
        FXTestSupport.runAndWait(() -> {
            Region first = new Region();
            Region second = new Region();

            ScrollPane scrollPane = createView().createObservationScrollPane(first, null, second);

            VBox content = assertInstanceOf(VBox.class, scrollPane.getContent());
            assertAll(
                    () -> assertEquals(List.of(first, second), content.getChildren()),
                    () -> assertTrue(scrollPane.isFitToWidth())
            );
        });
    }

    @Test
    void testCreateGridSection() {
        FXTestSupport.runAndWait(() -> {
            VBox section = createView().createGridSection();

            assertAll(
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_SECTION_GRID),
                            titleLabel(section).getText()),
                    () -> assertEquals(nameText(SimulationLocalizationKeys.OBSERVATION_GRID_TOTAL_CELLS),
                            cell(section, 0, 0).getText()),
                    () -> assertEquals(unknown(), cell(section, 1, 0).getText())
            );
        });
    }

    @Test
    void testCreateStatusSection() {
        FXTestSupport.runAndWait(() -> {
            VBox section = createView().createStatusSection();

            assertAll(
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_SECTION_STATUS),
                            titleLabel(section).getText()),
                    () -> assertEquals(nameText(SimulationLocalizationKeys.OBSERVATION_STEP),
                            cell(section, 0, 0).getText()),
                    () -> assertEquals(unknown(), cell(section, 1, 0).getText())
            );
        });
    }

    @Test
    void testSetFormattedIntegerValue() {
        FXTestSupport.runAndWait(() -> {
            Label label = new Label();

            createView().setFormattedIntegerValue(label, 1_234_567);

            assertEquals("1,234,567", label.getText());
        });
    }

    @Test
    void testSetUnknownValues() {
        FXTestSupport.runAndWait(() -> {
            Label first = new Label("1");
            Label second = new Label("2");

            createView().setUnknownValues(first, second);

            assertAll(
                    () -> assertEquals(unknown(), first.getText()),
                    () -> assertEquals(unknown(), second.getText())
            );
        });
    }

    @Test
    void testRegisterSelectedCellListener() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            view.registerSelectedCellListener(view.viewModel.selectedGridCellProperty());
            VBox section = view.createSelectedCellSection();
            boolean visibleWithoutSelection = section.isVisible() || section.isManaged();

            view.viewModel.selectedGridCellProperty().set(new GridCell<>(SELECTED_COORDINATE, ConwayEntity.ALIVE));
            boolean visibleWithSelection = section.isVisible() && section.isManaged();
            String coordinateText = cell(section, 1, 0).getText();
            String cellTypeText = cell(section, 1, 1).getText();
            view.viewModel.selectedGridCellProperty().set(null);

            assertAll(
                    () -> assertFalse(visibleWithoutSelection),
                    () -> assertTrue(visibleWithSelection),
                    () -> assertEquals(SELECTED_COORDINATE.toDisplayString(), coordinateText),
                    () -> assertEquals(entityDescriptorRegistry
                            .requireByDescriptorId(ConwayEntity.ALIVE.descriptorId()).shortName(), cellTypeText),
                    () -> assertFalse(section.isVisible()),
                    () -> assertEquals(unknown(), cell(section, 1, 0).getText()),
                    () -> assertEquals(unknown(), cell(section, 1, 1).getText())
            );
        });
    }

    @Test
    void testCreateSelectedCellSection() {
        FXTestSupport.runAndWait(() -> {
            VBox section = createView().createSelectedCellSection();

            assertAll(
                    () -> assertEquals(
                            AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL),
                            titleLabel(section).getText()),
                    () -> assertEquals(nameText(SimulationLocalizationKeys.OBSERVATION_COORDINATE),
                            cell(section, 0, 0).getText()),
                    () -> assertEquals(nameText(SimulationLocalizationKeys.OBSERVATION_CELL_TYPE),
                            cell(section, 0, 1).getText()),
                    () -> assertEquals(4, grid(section).getChildren().size()),
                    () -> assertFalse(section.isVisible())
            );
        });
    }

    @Test
    void testCreateExtendedSelectedCellSection() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            view.viewModel.selectedGridCellProperty().set(new GridCell<>(SELECTED_COORDINATE, ConwayEntity.DEAD));
            view.registerSelectedCellListener(view.viewModel.selectedGridCellProperty());
            Label extraLabel = new Label();

            VBox section = view.createExtendedSelectedCellSection(
                    new String[]{SimulationLocalizationKeys.OBSERVATION_STEP}, new Label[]{extraLabel});

            assertAll(
                    () -> assertEquals(nameText(SimulationLocalizationKeys.OBSERVATION_STEP),
                            cell(section, 0, 2).getText()),
                    () -> assertSame(extraLabel, cell(section, 1, 2)),
                    () -> assertTrue(section.isVisible()),
                    () -> assertEquals(SELECTED_COORDINATE.toDisplayString(), cell(section, 1, 0).getText())
            );
        });
    }

    @Test
    void testCreateExtendedSelectedCellSectionRejectsDifferentLengths() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();

            assertThrows(IllegalArgumentException.class, () -> view.createExtendedSelectedCellSection(
                    new String[]{SimulationLocalizationKeys.OBSERVATION_STEP}, new Label[0]));
        });
    }

    @Test
    void testCreateGenericMetricSection() {
        FXTestSupport.runAndWait(() -> {
            VBox section = createView().createGenericMetricSection(ConwayStatistics.metrics());

            assertAll(
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_SECTION_METRICS),
                            titleLabel(section).getText()),
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_EXTREMUM_MIN),
                            cell(section, 2, 0).getText()),
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_EXTREMUM_MAX),
                            cell(section, 3, 0).getText()),
                    () -> assertEquals(nameText(ConwayStatistics.metrics().getFirst().labelKey()),
                            cell(section, 0, 1).getText()),
                    () -> assertEquals(unknown(), cell(section, 1, 1).getText()),
                    () -> assertEquals(unknown(), cell(section, 2, 1).getText()),
                    () -> assertTrue(findCell(section, 2, 3).isEmpty(), "changed cells track no minimum"),
                    () -> assertEquals(unknown(), cell(section, 3, 3).getText())
            );
        });
    }

    @Test
    void testCreateGenericMetricSectionPlacesMaximumInMinimumColumnWithoutMinimum() {
        FXTestSupport.runAndWait(() -> {
            var metric = new StatisticsMetric<>(ConwayStatistics.KEY_CHANGED_CELLS,
                    "conway.observation.cells.changed", ConwayStatistics::changedCells, StatisticsExtremaMode.MAX);

            VBox section = createView().createGenericMetricSection(List.of(metric));

            assertAll(
                    () -> assertEquals(AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_EXTREMUM_MAX),
                            cell(section, 2, 0).getText()),
                    () -> assertEquals(unknown(), cell(section, 2, 1).getText()),
                    () -> assertEquals(4, grid(section).getChildren().size())
            );
        });
    }

    @Test
    void testCreateGenericMetricSectionOmitsHeaderWithoutExtrema() {
        FXTestSupport.runAndWait(() -> {
            var metric = new StatisticsMetric<>(ConwayStatistics.KEY_CHANGED_CELLS,
                    "conway.observation.cells.changed", ConwayStatistics::changedCells, StatisticsExtremaMode.NONE);

            VBox section = createView().createGenericMetricSection(List.of(metric));

            assertAll(
                    () -> assertEquals(2, grid(section).getChildren().size()),
                    () -> assertEquals(nameText("conway.observation.cells.changed"), cell(section, 0, 0).getText())
            );
        });
    }

    @Test
    void testCreateChartSection() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            TitledPane chartSectionWithoutMetrics = view.createChartSection();
            view.createGenericMetricSection(ConwayStatistics.metrics());

            TitledPane chartSection = view.createChartSection();

            assertAll(
                    () -> assertNull(chartSectionWithoutMetrics),
                    () -> assertNotNull(chartSection)
            );
            view.shutdownObservation();
        });
    }

    @Test
    void testCreateChartSectionReturnsNullWithoutChartedMetric() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            view.createGenericMetricSection(List.of(new StatisticsMetric<>(ConwayStatistics.KEY_CHANGED_CELLS,
                    "conway.observation.cells.changed", ConwayStatistics::changedCells, StatisticsExtremaMode.MAX)));

            assertNull(view.createChartSection());
        });
    }

    @Test
    void testUpdateObservationLabels() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            VBox statusSection = view.createStatusSection();
            VBox metricSection = view.createGenericMetricSection(ConwayStatistics.metrics());
            ConwayStatistics statistics = createStatistics(7);
            statistics.adjustCellCounts(1_100, 25);
            view.viewModel.setStatistics(statistics);
            view.viewModel.setStatisticsExtrema(new StatisticsExtrema(
                    Map.of(ConwayStatistics.KEY_ALIVE_CELLS, new StatisticsExtremum(900.0d, 2)),
                    Map.of(ConwayStatistics.KEY_ALIVE_CELLS, new StatisticsExtremum(1_150.0d, 1_005))));

            view.updateObservationLabels();

            Label minimumLabel = cell(metricSection, 2, 1);
            Label maximumLabel = cell(metricSection, 3, 1);
            Label missingMaximumLabel = cell(metricSection, 3, 3);
            String atStep = AppLocalization.getText(SimulationLocalizationKeys.OBSERVATION_EXTREMUM_AT_STEP);
            assertAll(
                    () -> assertEquals("7", cell(statusSection, 1, 0).getText()),
                    () -> assertEquals("1,100", cell(metricSection, 1, 1).getText()),
                    () -> assertEquals("100", cell(metricSection, 1, 2).getText()),
                    () -> assertEquals("25", cell(metricSection, 1, 3).getText()),
                    () -> assertEquals("900", minimumLabel.getText()),
                    () -> assertEquals(atStep + " 2", minimumLabel.getTooltip().getText()),
                    () -> assertEquals("1,150", maximumLabel.getText()),
                    () -> assertEquals(atStep + " 1,005", maximumLabel.getTooltip().getText()),
                    () -> assertEquals(unknown(), missingMaximumLabel.getText()),
                    () -> assertNull(missingMaximumLabel.getTooltip())
            );
        });
    }

    @Test
    void testUpdateObservationLabelsShowsUnknownForNonFiniteValue() {
        FXTestSupport.runAndWait(() -> {
            TestObservationView view = createView();
            VBox metricSection = view.createGenericMetricSection(List.of(new StatisticsMetric<>(KEY_NOT_FINITE,
                    "conway.observation.cells.changed", _ -> Double.NaN, StatisticsExtremaMode.NONE)));
            view.viewModel.setStatistics(createStatistics(1));

            view.updateObservationLabels();

            assertEquals(unknown(), cell(metricSection, 1, 0).getText());
        });
    }

    private static final class TestObservationView
            extends AbstractObservationView<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics> {

        TestObservationView(ObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics> viewModel,
                            GridEntityDescriptorRegistry entityDescriptorRegistry) {
            super(viewModel, entityDescriptorRegistry);
        }

        @NonNull
        @Override
        public Region buildObservationRegion() {
            return createObservationScrollPane(createStatusSection(), createGridSection(),
                    createGenericMetricSection(ConwayStatistics.metrics()), createSelectedCellSection(),
                    createChartSection());
        }

    }

}
