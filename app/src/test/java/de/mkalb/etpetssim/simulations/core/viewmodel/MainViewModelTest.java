package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.SynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStepLogic;
import de.mkalb.etpetssim.simulations.conway.model.ConwayTerminationCondition;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayUserActionContext;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.SimulationShutdownHandle;
import de.mkalb.etpetssim.simulations.core.model.AbstractSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class MainViewModelTest {

    private static final GridCoordinate SELECTED_COORDINATE = new GridCoordinate(1, 2);
    private static final GridCoordinate COORDINATE_OUTSIDE_GRID = new GridCoordinate(100_000, 100_000);
    private static final String TOOL_ID_CELL = "test.cell";
    private static final String TOOL_ID_GLOBAL = "test.global";

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static Fixture createFixture() {
        return createFixture(ConwaySimulationManager::new, Executors.newSingleThreadExecutor());
    }

    private static Fixture createFixture(
            BiFunction<ConwayConfig, SimulationInitializationCancellation,
                    AbstractSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
                            ConwayStatistics>> simulationManagerFactory,
            ExecutorService lifecycleExecutor) {
        ObjectProperty<SimulationState> simulationState =
                new SimpleObjectProperty<>(SimulationState.READY);
        var configViewModel = new ConwayConfigViewModel(simulationState);
        var controlViewModel = new ControlViewModel(simulationState);
        controlViewModel.startModeProperty().setValue(SimulationStartMode.START_PAUSED);
        var observationViewModel = new ObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics>();
        var userAction = new RecordingUserAction();
        return new Fixture(
                new MainViewModel<>(
                        simulationState,
                        configViewModel,
                        controlViewModel,
                        observationViewModel,
                        simulationManagerFactory,
                        ReadableGridModel::getGridCell,
                        userAction,
                        lifecycleExecutor),
                configViewModel, controlViewModel, observationViewModel, userAction);
    }

    private static Fixture createFixtureCapturingManager(
            AtomicReference<ConwaySimulationManager> manager) {
        return FXTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            var createdManager = new ConwaySimulationManager(config, cancellation);
            manager.set(createdManager);
            return createdManager;
        }, Executors.newSingleThreadExecutor()));
    }

    private static Fixture createPausedFixture() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        startAndAwaitInitialization(fixture);
        return fixture;
    }

    private static SimulationUserActionDescriptor<ConwayUserActionContext> createDescriptor(
            String toolId, SimulationUserActionScope scope, Optional<ConwayUserActionContext> context) {
        return new SimulationUserActionDescriptor<>(toolId, scope, "test.toolbar.tool", "test.toolbar.tool.tooltip",
                () -> context);
    }

    private static List<SimulationUserActionDescriptor<ConwayUserActionContext>> createDescriptors() {
        Optional<ConwayUserActionContext> context = Optional.of(ConwayUserActionContext.FixedAction.CLEAR_GRID);
        return List.of(
                createDescriptor(TOOL_ID_GLOBAL, SimulationUserActionScope.GLOBAL, context),
                createDescriptor(TOOL_ID_CELL, SimulationUserActionScope.SELECTED_CELL, context));
    }

    private static Fixture createFixtureWithFailingStep(Runnable stepFailure) {
        return FXTestSupport.supplyAndWaitNonNull(() -> createFixture(
                (config, _) -> new StubSimulationManager(config, stepFailure), Executors.newSingleThreadExecutor()));
    }

    private static void startAndAwaitError(Fixture fixture, List<SimulationState> states) throws InterruptedException {
        AtomicReference<CountDownLatch> errorReached = new AtomicReference<>();
        FXTestSupport.runAndWait(() -> {
            recordStates(fixture, states);
            errorReached.set(createStateLatch(fixture, SimulationState.ERROR));
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(errorReached.get().await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.ERROR, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(SimulationNotificationType.EXCEPTION,
                        fixture.mainViewModel().notificationTypeProperty().get())
        ));
    }

    private static void configureBatch(Fixture fixture) {
        fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
        fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.BATCH_SINGLE);
        fixture.controlViewModel().stepCountProperty().setValue(10);
        fixture.controlViewModel().terminationCheckModeProperty().setValue(SimulationTerminationCheckMode.UNCHECKED);
    }

    private static void startAndAwaitInitialization(Fixture fixture) throws InterruptedException {
        CountDownLatch initializationCompleted = new CountDownLatch(1);
        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(initializationCompleted::countDown);
            fixture.controlViewModel().requestActionButton();
            assertEquals(SimulationState.INITIALIZING, fixture.mainViewModel().getSimulationState());
        });
        assertTrue(initializationCompleted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    private static void recordStates(Fixture fixture, List<SimulationState> states) {
        fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> states.add(newState));
    }

    private static CountDownLatch createStateLatch(Fixture fixture, SimulationState awaitedState) {
        CountDownLatch awaitedStateReached = new CountDownLatch(1);
        fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> {
            if (newState == awaitedState) {
                awaitedStateReached.countDown();
            }
        });
        return awaitedStateReached;
    }

    private static void startBatchAndRunOnFirstBatchStep(Fixture fixture, Runnable action) {
        AtomicBoolean actionRun = new AtomicBoolean();
        fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
        fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.BATCH_SINGLE);
        fixture.controlViewModel().stepCountProperty().setValue(10_000);
        fixture.controlViewModel().terminationCheckModeProperty().setValue(SimulationTerminationCheckMode.UNCHECKED);
        fixture.mainViewModel().setSimulationStepListener(event -> {
            if (event.batchModeRunning()
                    && (fixture.mainViewModel().getSimulationState() == SimulationState.RUNNING_BATCH)
                    && !actionRun.getAndSet(true)) {
                action.run();
            }
        });
        fixture.controlViewModel().requestActionButton();
    }

    private static List<SimulationState> statesFrom(List<SimulationState> states, SimulationState firstState) {
        return List.copyOf(states.subList(states.indexOf(firstState), states.size()));
    }

    private static void awaitRelease(CountDownLatch releaseConstruction) {
        try {
            releaseConstruction.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to await the release of the construction", e);
        }
    }

    @Test
    void testSetSimulationResetListenerRunsOnStartAndShutdown() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        AtomicInteger resetCallCount = new AtomicInteger();

        FXTestSupport
                .runAndWait(() -> fixture.mainViewModel().setSimulationResetListener(resetCallCount::incrementAndGet));
        startAndAwaitInitialization(fixture);
        FXTestSupport.runAndWait(() -> {
            assertEquals(1, resetCallCount.get());
            fixture.mainViewModel().shutdownSimulation();
            assertEquals(2, resetCallCount.get());
        });
    }

    @Test
    void testSetNotificationType() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setNotificationType(SimulationNotificationType.TIMEOUT);

            assertEquals(SimulationNotificationType.TIMEOUT, fixture.mainViewModel().notificationTypeProperty().get());
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testGetStructure() throws InterruptedException {
        AtomicReference<ConwaySimulationManager> manager = new AtomicReference<>();
        Fixture fixture = createFixtureCapturingManager(manager);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> {
            assertSame(manager.get().structure(), fixture.mainViewModel().getStructure());
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testGetCellEdgeLength() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        FXTestSupport.runAndWait(() -> fixture.configViewModel().cellEdgeLengthProperty().setValue(17.0d));
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> {
            assertEquals(17.0d, fixture.mainViewModel().getCellEdgeLength(), 0.0d);
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testShutdownSimulationSuppressesStaleInitializationCompletion() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        AtomicBoolean initializationCompleted = new AtomicBoolean();
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((config, _) -> {
            constructionStarted.countDown();
            boolean interrupted = false;
            while (releaseConstruction.getCount() > 0L) {
                try {
                    releaseConstruction.await();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.interrupted();
            }
            return new ConwaySimulationManager(config);
        }, lifecycleExecutor));

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(() -> initializationCompleted.set(true));
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        SimulationShutdownHandle shutdownHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        releaseConstruction.countDown();
        assertTrue(shutdownHandle.awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState()),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager()),
                () -> assertFalse(initializationCompleted.get())
        ));
    }

    @Test
    void testShutdownSimulationReturnsSameShutdownHandle() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);

        SimulationShutdownHandle firstShutdownHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        SimulationShutdownHandle secondShutdownHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);

        assertAll(
                () -> assertSame(firstShutdownHandle, secondShutdownHandle),
                () -> assertTrue(firstShutdownHandle.awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS,
                        TimeUnit.SECONDS))
        );
    }

    @Test
    void testShutdownSimulationRemainsIdempotentWhenResetListenerFails() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> fixture.mainViewModel().setSimulationResetListener(
                () -> {
                    throw new IllegalStateException("Test reset listener failure");
                }));
        SimulationShutdownHandle firstShutdownHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        SimulationShutdownHandle secondShutdownHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);

        assertAll(
                () -> assertSame(firstShutdownHandle, secondShutdownHandle),
                () -> assertTrue(firstShutdownHandle.awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS,
                        TimeUnit.SECONDS))
        );
    }

    @Test
    void testShutdownSimulationDoesNotWaitForInitializationWorker() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            constructionStarted.countDown();
            boolean interrupted = false;
            while (releaseConstruction.getCount() > 0L) {
                try {
                    releaseConstruction.await();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));

        FXTestSupport.runAndWait(fixture.controlViewModel()::requestActionButton);
        assertTrue(constructionStarted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().shutdownSimulation();
            fixture.mainViewModel().shutdownSimulation();
            assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState());
        });

        assertTrue(lifecycleExecutor.isShutdown());
        assertFalse(lifecycleExecutor.isTerminated());
        releaseConstruction.countDown();
        assertTrue(lifecycleExecutor.awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testShutdownSimulationClearsHistoryAndExtrema() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().shutdownSimulation();

            assertAll(
                    () -> assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState()),
                    () -> assertEquals(List.of(), fixture.observationViewModel().statisticsHistoryProperty().get()),
                    () -> assertEquals(StatisticsExtrema.empty(), fixture.observationViewModel().getStatisticsExtrema())
            );
        });
    }

    @Test
    void testShutdownSimulationKeepsShuttingDownWhenRunningBatchCompletes() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        List<SimulationState> states = new ArrayList<>();
        AtomicReference<SimulationShutdownHandle> shutdownHandle = new AtomicReference<>();
        CountDownLatch shutdownRequested = new CountDownLatch(1);

        FXTestSupport.runAndWait(() -> {
            recordStates(fixture, states);
            startBatchAndRunOnFirstBatchStep(fixture, () -> {
                shutdownHandle.set(fixture.mainViewModel().shutdownSimulation());
                shutdownRequested.countDown();
            });
        });

        assertTrue(shutdownRequested.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        assertTrue(shutdownHandle.get().awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertEquals(
                List.of(SimulationState.RUNNING_BATCH, SimulationState.SHUTTING_DOWN),
                statesFrom(states, SimulationState.RUNNING_BATCH)));
    }

    @Test
    void testShutdownSimulationReleasesViewCallbacks() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(() -> {});
            fixture.mainViewModel().setSimulationStepListener(_ -> {});
            fixture.mainViewModel().setSimulationResetListener(() -> {});
            assertTrue(fixture.mainViewModel().hasViewListeners());

            fixture.mainViewModel().shutdownSimulation();

            assertFalse(fixture.mainViewModel().hasViewListeners());
        });
    }

    @Test
    void testGetCurrentConfig() throws InterruptedException {
        AtomicReference<ConwaySimulationManager> manager = new AtomicReference<>();
        Fixture fixture = createFixtureCapturingManager(manager);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> {
            assertSame(manager.get().config(), fixture.mainViewModel().getCurrentConfig());
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testHasSimulationManager() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        boolean managerBeforeStart = FXTestSupport.supplyAndWaitNonNull(fixture.mainViewModel()::hasSimulationManager);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> {
            assertAll(
                    () -> assertFalse(managerBeforeStart),
                    () -> assertTrue(fixture.mainViewModel().hasSimulationManager())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testGetCurrentGridModel() throws InterruptedException {
        AtomicReference<ConwaySimulationManager> manager = new AtomicReference<>();
        Fixture fixture = createFixtureCapturingManager(manager);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> {
            assertSame(manager.get().currentGridModel(), fixture.mainViewModel().getCurrentGridModel());
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testGetStepCount() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            assertEquals(0, fixture.mainViewModel().getStepCount());
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testUpdateClickedCoordinate() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().updateClickedCoordinate(SELECTED_COORDINATE);

            var expectedEntity = fixture.mainViewModel().getCurrentGridModel().getEntity(SELECTED_COORDINATE);
            var selectedCell = fixture.mainViewModel().selectedGridCellProperty().get();
            assertAll(
                    () -> assertEquals(new GridCell<>(SELECTED_COORDINATE, expectedEntity), selectedCell),
                    () -> assertEquals(expectedEntity, fixture.mainViewModel().lastSelectedEntityProperty().get()),
                    () -> assertEquals(selectedCell, fixture.observationViewModel().selectedGridCellProperty().get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testUpdateClickedCoordinateIgnoresClickWithoutSimulationManager() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().updateClickedCoordinate(SELECTED_COORDINATE);

            assertAll(
                    () -> assertNull(fixture.mainViewModel().selectedGridCellProperty().get()),
                    () -> assertNull(fixture.mainViewModel().lastSelectedEntityProperty().get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testUpdateClickedCoordinateKeepsLastSelectedEntityWhenSelectionClears() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().updateClickedCoordinate(SELECTED_COORDINATE);
            var expectedEntity = fixture.mainViewModel().getCurrentGridModel().getEntity(SELECTED_COORDINATE);

            fixture.controlViewModel().requestCancelButton();

            assertAll(
                    () -> assertEquals(SimulationState.CANCELED, fixture.mainViewModel().getSimulationState()),
                    () -> assertNull(fixture.mainViewModel().selectedGridCellProperty().get()),
                    () -> assertEquals(expectedEntity, fixture.mainViewModel().lastSelectedEntityProperty().get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testUpdateClickedCoordinateClearsSelectionWhenCellLookupFails() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().updateClickedCoordinate(SELECTED_COORDINATE);

            fixture.mainViewModel().updateClickedCoordinate(COORDINATE_OUTSIDE_GRID);

            assertNull(fixture.mainViewModel().selectedGridCellProperty().get());
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplyUserAction() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().getCurrentGridModel().setEntity(SELECTED_COORDINATE, ConwayEntity.DEAD);
            fixture.mainViewModel().updateClickedCoordinate(SELECTED_COORDINATE);

            boolean applied = fixture.mainViewModel().applyUserAction(ConwayUserActionContext.FixedAction.CLEAR_GRID);

            assertAll(
                    () -> assertTrue(applied),
                    () -> assertEquals(List.of(ConwayUserActionContext.FixedAction.CLEAR_GRID),
                            fixture.userAction().contexts()),
                    () -> assertEquals(List.of(new GridCell<>(SELECTED_COORDINATE, ConwayEntity.DEAD)),
                            fixture.userAction().selectedCells()),
                    () -> assertEquals(new GridCell<>(SELECTED_COORDINATE, ConwayEntity.ALIVE),
                            fixture.mainViewModel().selectedGridCellProperty().get()),
                    () -> assertEquals(ConwayEntity.ALIVE, fixture.mainViewModel().lastSelectedEntityProperty().get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplyUserActionPassesNoCellWithoutSelection() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            boolean applied = fixture.mainViewModel().applyUserAction(ConwayUserActionContext.FixedAction.CLEAR_GRID);

            assertAll(
                    () -> assertTrue(applied),
                    () -> assertEquals(1, fixture.userAction().callCount()),
                    () -> assertEquals(List.of(), fixture.userAction().selectedCells()),
                    () -> assertNull(fixture.mainViewModel().selectedGridCellProperty().get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplyUserActionReturnsFalseWithoutSimulationManager() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
            boolean applied = fixture.mainViewModel().applyUserAction(ConwayUserActionContext.FixedAction.CLEAR_GRID);

            assertAll(
                    () -> assertFalse(applied),
                    () -> assertEquals(0, fixture.userAction().callCount())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplyUserActionReturnsFalseWhenNotPaused() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().requestCancelButton();

            boolean applied = fixture.mainViewModel().applyUserAction(ConwayUserActionContext.FixedAction.CLEAR_GRID);

            assertAll(
                    () -> assertEquals(SimulationState.CANCELED, fixture.mainViewModel().getSimulationState()),
                    () -> assertFalse(applied),
                    () -> assertEquals(0, fixture.userAction().callCount())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplyUserActionWithDescriptor() throws InterruptedException {
        Fixture fixture = createPausedFixture();
        var descriptor = createDescriptor(TOOL_ID_GLOBAL, SimulationUserActionScope.GLOBAL,
                Optional.of(ConwayUserActionContext.FixedAction.CLEAR_GRID));

        FXTestSupport.runAndWait(() -> {
            boolean applied = fixture.mainViewModel().applyUserAction(descriptor);

            assertAll(
                    () -> assertTrue(applied),
                    () -> assertEquals(List.of(ConwayUserActionContext.FixedAction.CLEAR_GRID),
                            fixture.userAction().contexts())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplyUserActionWithDescriptorReturnsFalseForEmptyContext() throws InterruptedException {
        Fixture fixture = createPausedFixture();
        var descriptor = createDescriptor(TOOL_ID_GLOBAL, SimulationUserActionScope.GLOBAL, Optional.empty());

        FXTestSupport.runAndWait(() -> {
            boolean applied = fixture.mainViewModel().applyUserAction(descriptor);

            assertAll(
                    () -> assertFalse(applied),
                    () -> assertEquals(0, fixture.userAction().callCount())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplySelectedCellUserAction() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().editModeActiveProperty().set(true);
            fixture.mainViewModel().selectedUserActionToolIdProperty().set(TOOL_ID_CELL);
            fixture.mainViewModel().updateClickedCoordinate(SELECTED_COORDINATE);

            boolean applied = fixture.mainViewModel().applySelectedCellUserAction(createDescriptors());

            assertAll(
                    () -> assertTrue(applied),
                    () -> assertEquals(1, fixture.userAction().callCount()),
                    () -> assertEquals(SELECTED_COORDINATE,
                            fixture.userAction().selectedCells().getFirst().coordinate())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testApplySelectedCellUserActionReturnsFalseWithoutPrecondition() throws InterruptedException {
        Fixture fixture = createPausedFixture();
        var descriptors = createDescriptors();

        FXTestSupport.runAndWait(() -> {
            var mainViewModel = fixture.mainViewModel();
            mainViewModel.selectedUserActionToolIdProperty().set(TOOL_ID_CELL);
            mainViewModel.updateClickedCoordinate(SELECTED_COORDINATE);
            boolean appliedWithoutEditMode = mainViewModel.applySelectedCellUserAction(descriptors);

            mainViewModel.editModeActiveProperty().set(true);
            mainViewModel.selectedUserActionToolIdProperty().set(TOOL_ID_GLOBAL);
            boolean appliedWithGlobalTool = mainViewModel.applySelectedCellUserAction(descriptors);

            mainViewModel.selectedUserActionToolIdProperty().set(TOOL_ID_CELL);
            mainViewModel.selectedGridCellProperty().set(null);
            boolean appliedWithoutSelectedCell = mainViewModel.applySelectedCellUserAction(descriptors);

            assertAll(
                    () -> assertFalse(appliedWithoutEditMode),
                    () -> assertFalse(appliedWithGlobalTool),
                    () -> assertFalse(appliedWithoutSelectedCell),
                    () -> assertEquals(0, fixture.userAction().callCount())
            );
            mainViewModel.shutdownSimulation();
        });
    }

    @Test
    void testHandleCancelButtonCancelsRunningBatch() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        List<SimulationState> states = new ArrayList<>();
        AtomicReference<CountDownLatch> canceled = new AtomicReference<>();

        FXTestSupport.runAndWait(() -> {
            recordStates(fixture, states);
            canceled.set(createStateLatch(fixture, SimulationState.CANCELED));
            startBatchAndRunOnFirstBatchStep(fixture, fixture.controlViewModel()::requestCancelButton);
        });

        assertTrue(canceled.get().await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertEquals(
                List.of(SimulationState.RUNNING_BATCH, SimulationState.CANCELLING_BATCH, SimulationState.CANCELED),
                statesFrom(states, SimulationState.RUNNING_BATCH)));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleCancelButtonCancelsRunningTimerWithFinalStep() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        List<SimulationState> states = new ArrayList<>();
        List<SimulationStepEvent> events = new ArrayList<>();
        CountDownLatch firstStep = new CountDownLatch(1);

        FXTestSupport.runAndWait(() -> {
            recordStates(fixture, states);
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.TIMED);
            fixture.controlViewModel().stepDurationProperty().setValue(50);
            fixture.mainViewModel().setSimulationStepListener(event -> {
                events.add(event);
                firstStep.countDown();
            });
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(firstStep.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().requestCancelButton();
            assertAll(
                    () -> assertEquals(
                            List.of(SimulationState.RUNNING_TIMED, SimulationState.CANCELED),
                            statesFrom(states, SimulationState.RUNNING_TIMED)),
                    () -> assertTrue(events.getLast().finalStep()),
                    () -> assertFalse(events.getLast().batchModeRunning())
            );
        });
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionForwardsInitialHistoryAndExtrema() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(1, fixture.observationViewModel().statisticsHistoryProperty().get().size()),
                () -> assertEquals(0L, fixture.observationViewModel().statisticsHistoryProperty().get().getFirst()
                        .stepCount()),
                () -> assertFalse(fixture.observationViewModel().getStatisticsExtrema().minimumValues().isEmpty()),
                () -> assertFalse(fixture.observationViewModel().getStatisticsExtrema().maximumValues().isEmpty())
        ));

        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionInitializesOffJavaFxThread() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        AtomicBoolean constructedOnFxThread = new AtomicBoolean();
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            constructedOnFxThread.set(Platform.isFxApplicationThread());
            constructionStarted.countDown();
            awaitRelease(releaseConstruction);
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));
        CountDownLatch initializationCompleted = new CountDownLatch(1);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(initializationCompleted::countDown);
            fixture.controlViewModel().requestActionButton();
            assertAll(
                    () -> assertEquals(SimulationState.INITIALIZING, fixture.mainViewModel().getSimulationState()),
                    () -> assertFalse(fixture.mainViewModel().hasSimulationManager())
            );
        });

        assertTrue(constructionStarted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        assertFalse(constructedOnFxThread.get());
        releaseConstruction.countDown();
        assertTrue(initializationCompleted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertEquals(SimulationState.PAUSED,
                fixture.mainViewModel().getSimulationState()));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionRetainsCapturedTimedSettings() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        CountDownLatch initializationCompleted = new CountDownLatch(1);
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            constructionStarted.countDown();
            awaitRelease(releaseConstruction);
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.TIMED);
            fixture.controlViewModel().stepDurationProperty().setValue(2_000);
            fixture.mainViewModel().setSimulationInitializedListener(initializationCompleted::countDown);
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().simulationExecutionModeProperty()
                    .setValue(SimulationExecutionMode.BATCH_CONTINUOUS);
            fixture.controlViewModel().stepDurationProperty().setValue(50);
        });
        releaseConstruction.countDown();
        assertTrue(initializationCompleted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.RUNNING_TIMED, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(600L, fixture.mainViewModel().getThrottleDrawMillis())
        ));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionRetainsCapturedBatchSettings() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        CountDownLatch batchCompleted = new CountDownLatch(1);
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            constructionStarted.countDown();
            awaitRelease(releaseConstruction);
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));

        FXTestSupport.runAndWait(() -> {
            fixture.configViewModel().alivePercentProperty().setValue(0.0d);
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.BATCH_SINGLE);
            fixture.controlViewModel().stepCountProperty().setValue(4);
            fixture.controlViewModel().terminationCheckModeProperty()
                    .setValue(SimulationTerminationCheckMode.UNCHECKED);
            fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> {
                if (newState == SimulationState.PAUSED) {
                    batchCompleted.countDown();
                }
            });
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().simulationExecutionModeProperty()
                    .setValue(SimulationExecutionMode.BATCH_CONTINUOUS);
            fixture.controlViewModel().stepCountProperty().setValue(1);
            fixture.controlViewModel().terminationCheckModeProperty().setValue(SimulationTerminationCheckMode.CHECKED);
        });
        releaseConstruction.countDown();
        assertTrue(batchCompleted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(4, fixture.mainViewModel().getStepCount())
        ));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionTransitionsToErrorWhenManagerFails() throws InterruptedException {
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((_, _) -> {
            throw new IllegalStateException("Test initialization failure");
        }, lifecycleExecutor));
        CountDownLatch failureHandled = new CountDownLatch(1);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> {
                if (newState == SimulationState.ERROR) {
                    failureHandled.countDown();
                }
            });
            fixture.controlViewModel().requestActionButton();
        });

        assertTrue(failureHandled.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.ERROR, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(SimulationNotificationType.EXCEPTION,
                        fixture.mainViewModel().notificationTypeProperty().get()),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager())
        ));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionTransitionsToErrorWhenManagerThrowsError() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((_, _) -> {
            throw new OutOfMemoryError("Test initialization error");
        }, Executors.newSingleThreadExecutor()));
        List<SimulationState> states = new ArrayList<>();

        startAndAwaitError(fixture, states);
        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(List.of(SimulationState.INITIALIZING, SimulationState.ERROR), states),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager())
        ));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionTransitionsToErrorWhenListenerFails() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        CountDownLatch failureHandled = new CountDownLatch(1);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(() -> {
                throw new IllegalStateException("Test initialized listener failure");
            });
            fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> {
                if (newState == SimulationState.ERROR) {
                    failureHandled.countDown();
                }
            });
            fixture.controlViewModel().requestActionButton();
        });

        assertTrue(failureHandled.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.ERROR, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(SimulationNotificationType.EXCEPTION,
                        fixture.mainViewModel().notificationTypeProperty().get()),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager())
        ));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleStartActionTransitionsToErrorForInvalidConfig() {
        AtomicInteger factoryCallCount = new AtomicInteger();
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            factoryCallCount.incrementAndGet();
            return new ConwaySimulationManager(config, cancellation);
        }, Executors.newSingleThreadExecutor()));

        FXTestSupport.runAndWait(() -> {
            fixture.configViewModel().gridWidthProperty().setValue(0);

            fixture.controlViewModel().requestActionButton();

            assertAll(
                    () -> assertEquals(SimulationState.ERROR, fixture.mainViewModel().getSimulationState()),
                    () -> assertEquals(SimulationNotificationType.INVALID_CONFIG,
                            fixture.mainViewModel().notificationTypeProperty().get()),
                    () -> assertFalse(fixture.mainViewModel().hasSimulationManager()),
                    () -> assertEquals(0, factoryCallCount.get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testHandlePauseActionPausesRunningBatch() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        List<SimulationState> states = new ArrayList<>();
        AtomicReference<CountDownLatch> paused = new AtomicReference<>();

        FXTestSupport.runAndWait(() -> {
            recordStates(fixture, states);
            paused.set(createStateLatch(fixture, SimulationState.PAUSED));
            startBatchAndRunOnFirstBatchStep(fixture, fixture.controlViewModel()::requestActionButton);
        });

        assertTrue(paused.get().await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertEquals(
                List.of(SimulationState.RUNNING_BATCH, SimulationState.PAUSING_BATCH, SimulationState.PAUSED),
                statesFrom(states, SimulationState.RUNNING_BATCH)));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testHandleResumeActionStartsTimer() throws InterruptedException {
        Fixture fixture = createPausedFixture();

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setNotificationType(SimulationNotificationType.TIMEOUT);
            fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.TIMED);
            fixture.controlViewModel().stepDurationProperty().setValue(2_000);

            fixture.controlViewModel().requestActionButton();

            assertAll(
                    () -> assertEquals(SimulationState.RUNNING_TIMED, fixture.mainViewModel().getSimulationState()),
                    () -> assertTrue(fixture.mainViewModel().isTimerRunning()),
                    () -> assertEquals(600L, fixture.mainViewModel().getThrottleDrawMillis()),
                    () -> assertEquals(SimulationNotificationType.NONE,
                            fixture.mainViewModel().notificationTypeProperty().get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testHandleResumeActionExecutesBatch() throws InterruptedException {
        Fixture fixture = createPausedFixture();
        List<SimulationState> states = new ArrayList<>();
        AtomicReference<CountDownLatch> paused = new AtomicReference<>();

        FXTestSupport.runAndWait(() -> {
            configureBatch(fixture);
            fixture.controlViewModel().stepCountProperty().setValue(4);
            recordStates(fixture, states);
            paused.set(createStateLatch(fixture, SimulationState.PAUSED));

            fixture.controlViewModel().requestActionButton();
        });

        assertTrue(paused.get().await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> {
            assertAll(
                    () -> assertEquals(List.of(SimulationState.RUNNING_BATCH, SimulationState.PAUSED), states),
                    () -> assertEquals(4, fixture.mainViewModel().getStepCount())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testExecuteTimerStepPausesWhenFinishedWithTerminationCheck() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(MainViewModelTest::createFixture);
        List<SimulationState> states = new ArrayList<>();
        List<SimulationStepEvent> events = new ArrayList<>();
        AtomicReference<CountDownLatch> paused = new AtomicReference<>();

        FXTestSupport.runAndWait(() -> {
            fixture.configViewModel().alivePercentProperty().setValue(0.0d);
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.TIMED);
            fixture.controlViewModel().stepDurationProperty().setValue(50);
            fixture.controlViewModel().terminationCheckModeProperty().setValue(SimulationTerminationCheckMode.CHECKED);
            fixture.mainViewModel().setSimulationStepListener(events::add);
            recordStates(fixture, states);
            paused.set(createStateLatch(fixture, SimulationState.PAUSED));

            fixture.controlViewModel().requestActionButton();
        });

        assertTrue(paused.get().await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> {
            assertAll(
                    () -> assertEquals(List.of(SimulationState.RUNNING_TIMED, SimulationState.PAUSED),
                            statesFrom(states, SimulationState.RUNNING_TIMED)),
                    () -> assertEquals(List.of(new SimulationStepEvent(false, 1, false),
                            new SimulationStepEvent(false, 1, true)), events),
                    () -> assertFalse(fixture.mainViewModel().isTimerRunning()),
                    () -> assertEquals(SimulationNotificationType.NONE,
                            fixture.mainViewModel().notificationTypeProperty().get())
            );
            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testExecuteTimerStepTransitionsToErrorWhenStepFails() throws InterruptedException {
        Fixture fixture = createFixtureWithFailingStep(() -> {
            throw new ArithmeticException("Test timer step failure");
        });
        List<SimulationState> states = new ArrayList<>();
        List<SimulationStepEvent> events = new ArrayList<>();

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationExecutionModeProperty().setValue(SimulationExecutionMode.TIMED);
            fixture.controlViewModel().stepDurationProperty().setValue(50);
            fixture.mainViewModel().setSimulationStepListener(events::add);
        });
        startAndAwaitError(fixture, states);
        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(
                        List.of(SimulationState.RUNNING_TIMED, SimulationState.ERROR),
                        statesFrom(states, SimulationState.RUNNING_TIMED)),
                () -> assertFalse(fixture.mainViewModel().isTimerRunning()),
                () -> assertTrue(events.getLast().finalStep())
        ));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testExecuteBatchStepsTransitionsToErrorWhenStepFails() throws InterruptedException {
        Fixture fixture = createFixtureWithFailingStep(() -> {
            throw new ArithmeticException("Test batch step failure");
        });
        List<SimulationState> states = new ArrayList<>();

        FXTestSupport.runAndWait(() -> configureBatch(fixture));
        startAndAwaitError(fixture, states);
        FXTestSupport.runAndWait(() -> assertEquals(
                List.of(SimulationState.RUNNING_BATCH, SimulationState.ERROR),
                statesFrom(states, SimulationState.RUNNING_BATCH)));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testExecuteBatchStepsTransitionsToErrorWhenStepThrowsError() throws InterruptedException {
        Fixture fixture = createFixtureWithFailingStep(() -> {
            throw new StackOverflowError("Test batch step error");
        });
        List<SimulationState> states = new ArrayList<>();

        FXTestSupport.runAndWait(() -> configureBatch(fixture));
        startAndAwaitError(fixture, states);
        FXTestSupport.runAndWait(() -> assertEquals(
                List.of(SimulationState.RUNNING_BATCH, SimulationState.ERROR),
                statesFrom(states, SimulationState.RUNNING_BATCH)));
        FXTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    private record Fixture(
            MainViewModel<
                    ConwayEntity,
                    GridCell<ConwayEntity>,
                    WritableGridModel<ConwayEntity>,
                    ConwayConfig,
                    ConwayStatistics,
                    AbstractSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
                            ConwayStatistics>,
                    ConwayUserActionContext> mainViewModel,
            ConwayConfigViewModel configViewModel,
            ControlViewModel controlViewModel,
            ObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics> observationViewModel,
            RecordingUserAction userAction) {
    }

    private static final class RecordingUserAction implements SimulationUserAction<ConwayEntity,
            WritableGridModel<ConwayEntity>, ConwayConfig, ConwayStatistics,
            AbstractSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig, ConwayStatistics>,
            ConwayUserActionContext> {

        private final List<ConwayUserActionContext> contexts = new ArrayList<>();
        private final List<GridCellView<ConwayEntity>> selectedCells = new ArrayList<>();
        private int callCount;

        @Override
        public void apply(
                @NonNull AbstractSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
                        ConwayStatistics> manager,
                @NonNull ConwayUserActionContext context,
                GridCellView<ConwayEntity> selectedCell) {
            callCount++;
            contexts.add(context);
            if (selectedCell != null) {
                selectedCells.add(selectedCell);
                manager.currentGridModel().setEntity(selectedCell.coordinate(), ConwayEntity.ALIVE);
            }
        }

        int callCount() {
            return callCount;
        }

        List<ConwayUserActionContext> contexts() {
            return contexts;
        }

        List<GridCellView<ConwayEntity>> selectedCells() {
            return selectedCells;
        }

    }

    private static final class StubSimulationManager
            extends AbstractSimulationManager<ConwayEntity, WritableGridModel<ConwayEntity>, ConwayConfig,
            ConwayStatistics> {

        private final GridStructure structure;
        private final ConwayStatistics statistics;
        private final TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor;
        private final Runnable stepFailure;

        StubSimulationManager(ConwayConfig config, Runnable stepFailure) {
            super(config, List.of());
            structure = config.createGridStructure();
            statistics = new ConwayStatistics(structure);
            var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);
            var runner = new SynchronousStepRunner<>(model, new ConwayStepLogic(structure, config));
            executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner,
                    runner::currentGridModel, new ConwayTerminationCondition(), statistics));
            this.stepFailure = stepFailure;
        }

        @NonNull
        @Override
        public GridStructure structure() {
            return structure;
        }

        @NonNull
        @Override
        public ConwayStatistics statistics() {
            return statistics;
        }

        @Override
        protected void updateStatistics() {
            if (executor.stepCount() > 0) {
                stepFailure.run();
            }
        }

        @NonNull
        @Override
        protected TimedSimulationExecutor<ConwayEntity, WritableGridModel<ConwayEntity>> executor() {
            return executor;
        }

    }

}
