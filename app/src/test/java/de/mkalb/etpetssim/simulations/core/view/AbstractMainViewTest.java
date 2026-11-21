package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.ConwayFactory;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.ConwayUserAction;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayUserActionContext;
import de.mkalb.etpetssim.simulations.conway.view.ConwayConfigView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayObservationView;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.SimulationShutdownHandle;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.ControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.ObservationViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationExecutionMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationStartMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationState;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationTerminationCheckMode;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionScope;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import de.mkalb.etpetssim.ui.GridGeometry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AbstractMainViewTest {

    private static final GridCoordinate SELECTED_COORDINATE = new GridCoordinate(1, 2);
    private static final String TOOL_ID_GLOBAL = "test.global";

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> createMainView() {
        return assertInstanceOf(AbstractMainView.class, ConwayFactory.createMainView());
    }

    private static RecordingMainView createRecordingMainView() {
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.READY);
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(ConwayEntity.values());
        var configViewModel = new ConwayConfigViewModel(simulationState);
        var controlViewModel = new ControlViewModel(simulationState);
        controlViewModel.startModeProperty().setValue(SimulationStartMode.START_PAUSED);
        var observationViewModel = new ObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics>();
        var viewModel = new MainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                ConwaySimulationManager::new, ReadableGridModel::getGridCell, new ConwayUserAction());
        return new RecordingMainView(viewModel, new ConwayConfigView(configViewModel), controlViewModel,
                new ConwayObservationView(observationViewModel, entityDescriptorRegistry), entityDescriptorRegistry);
    }

    private static RecordingMainView startPausedAndAwaitInitialDraw() throws InterruptedException {
        RecordingMainView view = FXTestSupport.supplyAndWaitNonNull(AbstractMainViewTest::createRecordingMainView);
        CountDownLatch initialDraw = view.expectDraws(1);
        FXTestSupport.runAndWait(() -> {
            view.buildMainRegion();
            view.controlViewModel().requestActionButton();
        });
        assertTrue(initialDraw.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertEquals(SimulationState.PAUSED, view.viewModel.getSimulationState()));
        return view;
    }

    private static VBox simulationRegion(Region mainRegion) {
        SplitPane centerSplitPane = assertInstanceOf(SplitPane.class,
                assertInstanceOf(BorderPane.class, mainRegion).getCenter());
        return assertInstanceOf(VBox.class, centerSplitPane.getItems().getFirst());
    }

    private static HBox editToolBarBox(Region mainRegion) {
        ScrollPane editToolBarScrollPane = assertInstanceOf(ScrollPane.class,
                simulationRegion(mainRegion).getChildren().get(1));
        return assertInstanceOf(HBox.class, editToolBarScrollPane.getContent());
    }

    private static GridEntityDescriptor createDescriptor(Color color) {
        return new GridEntityDescriptor("test", "short", "long", "description", null, color, null);
    }

    @ParameterizedTest
    @CsvSource({
            "3.0, 0.0",
            "4.0, 0.6931471805599453",
            "20.0, 2.302585092994046"
    })
    void testComputeStrokeLineWidth(double edgeLength, double expectedLineWidth) {
        CellDimension cellDimension = GridGeometry.computeCellDimension(edgeLength, CellShape.SQUARE);

        assertEquals(expectedLineWidth, AbstractMainView.computeStrokeLineWidth(cellDimension), 1.0e-12);
    }

    @ParameterizedTest
    @CsvSource({
            "TRIANGLE, 3.0, false",
            "TRIANGLE, 4.0, true",
            "SQUARE, 1.0, false",
            "SQUARE, 2.0, true",
            "HEXAGON, 1.0, false",
            "HEXAGON, 2.0, true"
    })
    void testIsInnerCircleVisible(CellShape cellShape, double edgeLength, boolean expectedVisible) {
        CellDimension cellDimension = GridGeometry.computeCellDimension(edgeLength, cellShape);

        assertEquals(expectedVisible, AbstractMainView.isInnerCircleVisible(cellDimension));
    }

    @Test
    void testBuildMainRegionUsesObservationWidthSplit() {
        FXTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            BorderPane mainRegion = assertInstanceOf(BorderPane.class, view.buildMainRegion());
            SplitPane centerSplitPane = assertInstanceOf(SplitPane.class, mainRegion.getCenter());

            assertAll(
                    () -> assertEquals(Orientation.HORIZONTAL, centerSplitPane.getOrientation()),
                    () -> assertEquals(2, centerSplitPane.getItems().size()),
                    () -> assertArrayEquals(new double[]{0.65d}, centerSplitPane.getDividerPositions(), 0.0d),
                    () -> assertFalse(SplitPane.isResizableWithParent(centerSplitPane.getItems().get(1)))
            );
        });
    }

    @Test
    void testBuildMainRegionReturnsSameRegionWhenCalledRepeatedly() {
        FXTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();

            var firstRegion = view.buildMainRegion();
            var secondRegion = view.buildMainRegion();

            assertSame(firstRegion, secondRegion);
        });
    }

    @Test
    void testShutdownSimulationUnregistersNotificationListener() {
        FXTestSupport.runAndWait(() -> {
            AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> view = createMainView();
            view.buildMainRegion();
            boolean registeredBeforeShutdown = view.hasNotificationListener();

            view.shutdownSimulation();

            assertAll(
                    () -> assertTrue(registeredBeforeShutdown),
                    () -> assertFalse(view.hasNotificationListener())
            );
        });
    }

    @Test
    void testShutdownSimulationUnregistersObservationListener() {
        FXTestSupport.runAndWait(() -> {
            AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> view = createMainView();
            AbstractObservationView<?, ?, ?> observationView = view.observationView;
            boolean registeredBeforeShutdown = observationView.hasSelectedGridCellListener();

            view.shutdownSimulation();

            assertAll(
                    () -> assertTrue(registeredBeforeShutdown),
                    () -> assertFalse(observationView.hasSelectedGridCellListener())
            );
        });
    }

    @Test
    void testShutdownSimulationForwardsViewModelShutdownHandle() {
        FXTestSupport.runAndWait(() -> {
            AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> view = createMainView();
            MainViewModel<?, ?, ?, ?, ?, ?, ?> viewModel = view.viewModel;

            SimulationShutdownHandle shutdownHandle = view.shutdownSimulation();

            assertSame(shutdownHandle, viewModel.shutdownSimulation());
        });
    }

    @Test
    void testShutdownSimulationRemainsIdempotentAfterRepeatedBuild() {
        FXTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            view.buildMainRegion();
            view.buildMainRegion();

            SimulationShutdownHandle firstShutdownHandle = view.shutdownSimulation();
            SimulationShutdownHandle secondShutdownHandle = view.shutdownSimulation();

            assertSame(firstShutdownHandle, secondShutdownHandle);
        });
    }

    @Test
    void testComputeBrightnessVariantsMap() {
        FXTestSupport.runAndWait(() -> {
            AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> view = createMainView();

            assertAll(
                    () -> assertEquals(FXPaintFactory.computeBrightnessVariantsMap(Color.RED, 0, 10, 5, 0.5d),
                            view.computeBrightnessVariantsMap(createDescriptor(Color.RED), 0, 10, 5, 0.5d)),
                    () -> assertNull(view.computeBrightnessVariantsMap(createDescriptor(null), 0, 10, 5, 0.5d))
            );
            view.shutdownSimulation();
        });
    }

    @ParameterizedTest
    @CsvSource({
            "SHAPE, 2.0, SHAPE",
            "SHAPE_BORDERED, 2.0, SHAPE_BORDERED",
            "CIRCLE, 2.0, CIRCLE",
            "CIRCLE_BORDERED, 2.0, CIRCLE_BORDERED",
            "EMOJI, 2.0, CIRCLE",
            "SHAPE, 1.0, SHAPE",
            "SHAPE_BORDERED, 1.0, SHAPE_BORDERED",
            "CIRCLE, 1.0, SHAPE",
            "CIRCLE_BORDERED, 1.0, SHAPE_BORDERED",
            "EMOJI, 1.0, SHAPE"
    })
    void testResolveCellDisplayModeWithoutEmojiFont(CellDisplayMode cellDisplayMode, double edgeLength,
                                                    CellDisplayMode expectedMode) {
        CellDimension cellDimension = GridGeometry.computeCellDimension(edgeLength, CellShape.SQUARE);
        FXTestSupport.runAndWait(() -> {
            AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> view = createMainView();

            assertEquals(expectedMode, view.resolveCellDisplayMode(cellDisplayMode, cellDimension));
            view.shutdownSimulation();
        });
    }

    @Test
    void testResolveCellDisplayModeKeepsEmojiWithEmojiFont() {
        CellDimension cellDimension = GridGeometry.computeCellDimension(1.0d, CellShape.SQUARE);
        FXTestSupport.runAndWait(() -> {
            AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> view = createMainView();
            view.cellEmojiFont = Font.getDefault();

            assertEquals(CellDisplayMode.EMOJI, view.resolveCellDisplayMode(CellDisplayMode.EMOJI, cellDimension));
            view.shutdownSimulation();
        });
    }

    @Test
    void testHandleSelectedGridCellChanged() throws InterruptedException {
        RecordingMainView view = startPausedAndAwaitInitialDraw();

        FXTestSupport.runAndWait(() -> {
            view.viewModel.updateClickedCoordinate(SELECTED_COORDINATE);

            assertEquals(List.of(SELECTED_COORDINATE), view.selectedCoordinates());
            view.shutdownSimulation();
        });
    }

    @Test
    void testApplyGlobalUserActionAndRedraw() throws InterruptedException {
        RecordingMainView view = startPausedAndAwaitInitialDraw();

        FXTestSupport.runAndWait(() -> {
            Button globalButton = assertInstanceOf(Button.class,
                    editToolBarBox(view.buildMainRegion()).getChildren().get(2));

            globalButton.fire();

            assertAll(
                    () -> assertEquals(List.of(new DrawCall(0, Integer.MIN_VALUE), new DrawCall(0, 0)),
                            view.drawCalls()),
                    () -> assertEquals(0L,
                            view.viewModel.getCurrentGridModel().countEntities(ConwayEntity.ALIVE::equals))
            );
            view.shutdownSimulation();
        });
    }

    @Test
    void testHandleSimulationInitialized() throws InterruptedException {
        RecordingMainView view = startPausedAndAwaitInitialDraw();

        FXTestSupport.runAndWait(() -> {
            CanvasPainters painters = view.painters();
            assertNotNull(painters);
            FXGridCanvasPainter basePainter = painters.base();
            Region mainRegion = view.buildMainRegion();
            assertAll(
                    () -> assertEquals(List.of(view.viewModel.getCurrentConfig()), view.initializedConfigs()),
                    () -> assertEquals(List.of(basePainter.cellDimension()), view.initializedCellDimensions()),
                    () -> assertEquals(List.of(new DrawCall(0, Integer.MIN_VALUE)), view.drawCalls()),
                    () -> assertEquals(Math.ceil(basePainter.gridDimension2D().getWidth()),
                            view.overlayCanvas().getWidth(), 0.0d),
                    () -> assertEquals(Math.ceil(basePainter.gridDimension2D().getHeight()),
                            view.overlayCanvas().getHeight(), 0.0d),
                    () -> assertEquals(3, editToolBarBox(mainRegion).getChildren().size()),
                    () -> assertTrue(simulationRegion(mainRegion).getChildren().get(1).isVisible())
            );
            view.shutdownSimulation();
        });
    }

    @Test
    void testHandleSimulationStepDrawsFinalBatchStep() throws InterruptedException {
        RecordingMainView view = startPausedAndAwaitInitialDraw();
        CountDownLatch finalDraw = view.expectDraws(1);

        FXTestSupport.runAndWait(() -> {
            ControlViewModel controlViewModel = view.controlViewModel();
            controlViewModel.simulationExecutionModeProperty().setValue(SimulationExecutionMode.BATCH_SINGLE);
            controlViewModel.stepCountProperty().setValue(3);
            controlViewModel.terminationCheckModeProperty().setValue(SimulationTerminationCheckMode.UNCHECKED);
            controlViewModel.requestActionButton();
        });

        assertTrue(finalDraw.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> {
            assertEquals(List.of(new DrawCall(0, Integer.MIN_VALUE), new DrawCall(3, 0)), view.drawCalls());
            view.shutdownSimulation();
        });
    }

    @Test
    void testRegisterNotificationListener() {
        FXTestSupport.runAndWait(() -> {
            RecordingMainView view = createRecordingMainView();
            Label notificationLabel = assertInstanceOf(Label.class,
                    simulationRegion(view.buildMainRegion()).getChildren().getFirst());
            boolean visibleInitially = notificationLabel.isVisible() || notificationLabel.isManaged();

            view.viewModel.setNotificationType(SimulationNotificationType.TIMEOUT);
            String textWithNotification = notificationLabel.getText();
            boolean visibleWithNotification = notificationLabel.isVisible() && notificationLabel.isManaged();
            view.viewModel.setNotificationType(SimulationNotificationType.NONE);

            assertAll(
                    () -> assertFalse(visibleInitially),
                    () -> assertEquals(AppLocalization.getText(SimulationNotificationType.TIMEOUT.resourceKey()),
                            textWithNotification),
                    () -> assertTrue(visibleWithNotification),
                    () -> assertNull(notificationLabel.getText()),
                    () -> assertFalse(notificationLabel.isVisible()),
                    () -> assertFalse(notificationLabel.isManaged())
            );
            view.shutdownSimulation();
        });
    }

    private record DrawCall(int stepCount, int lastDrawnStepCount) {
    }

    private static final class RecordingMainView extends AbstractMainView<
            ConwayEntity,
            GridCell<ConwayEntity>,
            WritableGridModel<ConwayEntity>,
            ConwayConfig,
            ConwayStatistics,
            ConwaySimulationManager,
            ConwayUserActionContext,
            ConwayObservationView> {

        private final ControlViewModel controlViewModel;
        private final List<ConwayConfig> initializedConfigs = new ArrayList<>();
        private final List<CellDimension> initializedCellDimensions = new ArrayList<>();
        private final List<DrawCall> drawCalls = new ArrayList<>();
        private final List<GridCoordinate> selectedCoordinates = new ArrayList<>();
        private volatile CountDownLatch drawLatch = new CountDownLatch(0);

        RecordingMainView(MainViewModel<ConwayEntity, GridCell<ConwayEntity>, WritableGridModel<ConwayEntity>,
                                  ConwayConfig, ConwayStatistics, ConwaySimulationManager,
                                  ConwayUserActionContext> viewModel,
                          ConwayConfigView configView,
                          ControlViewModel controlViewModel,
                          ConwayObservationView observationView,
                          GridEntityDescriptorRegistry entityDescriptorRegistry) {
            super(viewModel, configView, new ControlView(controlViewModel), observationView,
                    entityDescriptorRegistry);
            this.controlViewModel = controlViewModel;
        }

        ControlViewModel controlViewModel() {
            return controlViewModel;
        }

        CountDownLatch expectDraws(int count) {
            CountDownLatch latch = new CountDownLatch(count);
            drawLatch = latch;
            return latch;
        }

        List<ConwayConfig> initializedConfigs() {
            return initializedConfigs;
        }

        List<CellDimension> initializedCellDimensions() {
            return initializedCellDimensions;
        }

        List<DrawCall> drawCalls() {
            return drawCalls;
        }

        List<GridCoordinate> selectedCoordinates() {
            return selectedCoordinates;
        }

        @NonNull
        @Override
        protected List<SimulationUserActionDescriptor<ConwayUserActionContext>> createUserActionDescriptors() {
            return List.of(new SimulationUserActionDescriptor<>(TOOL_ID_GLOBAL,
                    ConwayUserActionContext.FixedAction.CLEAR_GRID, SimulationUserActionScope.GLOBAL,
                    "conway.toolbar.cleargrid", "conway.toolbar.cleargrid.tooltip"));
        }

        @Override
        protected void handleSelectedGridCellChanged(@NonNull FXGridCanvasPainter painter,
                                                     GridCell<ConwayEntity> oldGridCell,
                                                     GridCell<ConwayEntity> newGridCell) {
            if (newGridCell != null) {
                selectedCoordinates.add(newGridCell.coordinate());
            }
        }

        @Override
        protected void initializeSimulation(@NonNull CanvasPainters painters,
                                            @NonNull ConwayConfig config,
                                            @NonNull CellDimension cellDimension) {
            initializedConfigs.add(config);
            initializedCellDimensions.add(cellDimension);
        }

        @Override
        protected void drawSimulation(@NonNull CanvasPainters painters,
                                      @NonNull WritableGridModel<ConwayEntity> currentGridModel,
                                      int stepCount,
                                      int lastDrawnStepCount) {
            drawCalls.add(new DrawCall(stepCount, lastDrawnStepCount));
            drawLatch.countDown();
        }

    }

}
