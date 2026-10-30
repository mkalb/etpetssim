package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.conway.model.ConwayConfig;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.ConwayUserAction;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.shared.ConwayUserActionContext;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema;
import de.mkalb.etpetssim.simulations.core.shared.SimulationMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStartMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.shared.SimulationStepEvent;
import de.mkalb.etpetssim.simulations.core.shared.SimulationTerminationCheckMode;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class DefaultMainViewModelTest {

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
            BiFunction<ConwayConfig, SimulationInitializationCancellation, ConwaySimulationManager> simulationManagerFactory,
            ExecutorService lifecycleExecutor) {
        ObjectProperty<SimulationState> simulationState =
                new SimpleObjectProperty<>(SimulationState.READY);
        var configViewModel = new ConwayConfigViewModel(simulationState);
        var controlViewModel = new DefaultControlViewModel(simulationState);
        controlViewModel.startModeProperty().setValue(SimulationStartMode.START_PAUSED);
        var observationViewModel =
                new DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics>(simulationState);
        var mainViewModel = new DefaultMainViewModel<>(
                simulationState,
                configViewModel,
                controlViewModel,
                observationViewModel,
                simulationManagerFactory,
                ReadableGridModel::getGridCell,
                new ConwayUserAction(),
                lifecycleExecutor);
        return new Fixture(mainViewModel, configViewModel, controlViewModel, observationViewModel);
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
        fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.BATCH_SINGLE);
        fixture.controlViewModel().stepCountProperty().setValue(10_000);
        fixture.controlViewModel().terminationCheckProperty().setValue(SimulationTerminationCheckMode.UNCHECKED);
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

    private static Object getField(Object target, String fieldName) {
        try {
            Field field = DefaultMainViewModel.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to read field: " + fieldName, e);
        }
    }

    private static Object getStaticField(String fieldName) {
        try {
            Field field = DefaultMainViewModel.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to read static field: " + fieldName, e);
        }
    }

    @Test
    void testHandleCancelButtonCancelsRunningBatch() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
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
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        List<SimulationState> states = new ArrayList<>();
        List<SimulationStepEvent> events = new ArrayList<>();
        CountDownLatch firstStep = new CountDownLatch(1);

        FXTestSupport.runAndWait(() -> {
            recordStates(fixture, states);
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.TIMED);
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
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(1, fixture.observationViewModel().getStatisticsHistory().size()),
                () -> assertEquals(0L, fixture.observationViewModel().getStatisticsHistory().getFirst().stepCount()),
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
            try {
                releaseConstruction.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
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
        FXTestSupport.runAndWait(() -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()));
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
            try {
                releaseConstruction.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.TIMED);
            fixture.controlViewModel().stepDurationProperty().setValue(2_000);
            fixture.mainViewModel().setSimulationInitializedListener(initializationCompleted::countDown);
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.BATCH_CONTINUOUS);
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
            try {
                releaseConstruction.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));

        FXTestSupport.runAndWait(() -> {
            fixture.configViewModel().alivePercentProperty().setValue(0.0d);
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.BATCH_SINGLE);
            fixture.controlViewModel().stepCountProperty().setValue(4);
            fixture.controlViewModel().terminationCheckProperty().setValue(SimulationTerminationCheckMode.UNCHECKED);
            fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> {
                if (newState == SimulationState.PAUSED) {
                    batchCompleted.countDown();
                }
            });
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> {
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.BATCH_CONTINUOUS);
            fixture.controlViewModel().stepCountProperty().setValue(1);
            fixture.controlViewModel().terminationCheckProperty().setValue(SimulationTerminationCheckMode.CHECKED);
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
    void testHandleStartActionTransitionsToErrorWhenListenerFails() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
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
    void testHandlePauseActionPausesRunningBatch() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
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

        SimulationTerminationHandle terminationHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        releaseConstruction.countDown();
        assertTrue(terminationHandle.awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState()),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager()),
                () -> assertFalse(initializationCompleted.get())
        ));
    }

    @Test
    void testSetSimulationResetListenerRunsOnStartAndShutdown() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        AtomicInteger resetCallCount = new AtomicInteger();

        FXTestSupport.runAndWait(() -> fixture.mainViewModel().setSimulationResetListener(resetCallCount::incrementAndGet));
        startAndAwaitInitialization(fixture);
        FXTestSupport.runAndWait(() -> {
            assertEquals(1, resetCallCount.get());
            fixture.mainViewModel().shutdownSimulation();
            assertEquals(2, resetCallCount.get());
        });
    }

    @Test
    void testShutdownSimulationReturnsSameTerminationHandle() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);

        SimulationTerminationHandle firstTerminationHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        SimulationTerminationHandle secondTerminationHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);

        assertAll(
                () -> assertSame(firstTerminationHandle, secondTerminationHandle),
                () -> assertTrue(firstTerminationHandle.awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
        );
    }

    @Test
    void testShutdownSimulationRemainsIdempotentWhenResetListenerFails() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> fixture.mainViewModel().setSimulationResetListener(
                () -> {
                    throw new IllegalStateException("Test reset listener failure");
                }));
        SimulationTerminationHandle firstTerminationHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        SimulationTerminationHandle secondTerminationHandle = FXTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);

        assertAll(
                () -> assertSame(firstTerminationHandle, secondTerminationHandle),
                () -> assertTrue(firstTerminationHandle.awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
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
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        startAndAwaitInitialization(fixture);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().shutdownSimulation();

            assertAll(
                    () -> assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState()),
                    () -> assertEquals(List.of(), fixture.observationViewModel().getStatisticsHistory()),
                    () -> assertEquals(StatisticsExtrema.empty(), fixture.observationViewModel().getStatisticsExtrema())
            );
        });
    }

    @Test
    void testShutdownSimulationKeepsShuttingDownWhenRunningBatchCompletes() throws InterruptedException {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        List<SimulationState> states = new ArrayList<>();
        AtomicReference<SimulationTerminationHandle> terminationHandle = new AtomicReference<>();
        CountDownLatch shutdownRequested = new CountDownLatch(1);

        FXTestSupport.runAndWait(() -> {
            recordStates(fixture, states);
            startBatchAndRunOnFirstBatchStep(fixture, () -> {
                terminationHandle.set(fixture.mainViewModel().shutdownSimulation());
                shutdownRequested.countDown();
            });
        });

        assertTrue(shutdownRequested.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        assertTrue(terminationHandle.get().awaitTermination(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FXTestSupport.runAndWait(() -> assertEquals(
                List.of(SimulationState.RUNNING_BATCH, SimulationState.SHUTTING_DOWN),
                statesFrom(states, SimulationState.RUNNING_BATCH)));
    }

    @Test
    void testShutdownSimulationReleasesViewCallbacks() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(() -> {});
            fixture.mainViewModel().setSimulationStepListener(_ -> {});
            fixture.mainViewModel().setSimulationResetListener(() -> {});
            fixture.mainViewModel().shutdownSimulation();
        });

        assertAll(
                () -> assertSame(
                        getStaticField("NO_OP_SIMULATION_INITIALIZED_LISTENER"),
                        getField(fixture.mainViewModel(), "simulationInitializedListener")),
                () -> assertSame(
                        getStaticField("NO_OP_SIMULATION_STEP_LISTENER"),
                        getField(fixture.mainViewModel(), "simulationStepListener")),
                () -> assertSame(
                        getStaticField("NO_OP_SIMULATION_RESET_LISTENER"),
                        getField(fixture.mainViewModel(), "simulationResetListener"))
        );
    }

    private record Fixture(
            DefaultMainViewModel<
                    ConwayEntity,
                    GridCell<ConwayEntity>,
                    WritableGridModel<ConwayEntity>,
                    ConwayConfig,
                    ConwayStatistics,
                    ConwaySimulationManager,
                    ConwayUserActionContext> mainViewModel,
            ConwayConfigViewModel configViewModel,
            DefaultControlViewModel controlViewModel,
            DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics> observationViewModel) {
    }

}
