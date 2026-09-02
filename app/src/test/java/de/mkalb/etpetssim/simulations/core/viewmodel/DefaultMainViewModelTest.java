package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.SimulationTermination;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.core.shared.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class DefaultMainViewModelTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FxTestSupport.ensureStarted();
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
        FxTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(initializationCompleted::countDown);
            fixture.controlViewModel().requestActionButton();
            assertEquals(SimulationState.INITIALIZING, fixture.mainViewModel().getSimulationState());
        });
        assertTrue(initializationCompleted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
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
    void testStartForwardsInitialHistoryAndExtrema() throws InterruptedException {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        startAndAwaitInitialization(fixture);

        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(1, fixture.observationViewModel().getStatisticsHistory().size()),
                () -> assertEquals(0L, fixture.observationViewModel().getStatisticsHistory().getFirst().stepCount()),
                () -> assertFalse(fixture.observationViewModel().getStatisticsExtrema().minimumValues().isEmpty()),
                () -> assertFalse(fixture.observationViewModel().getStatisticsExtrema().maximumValues().isEmpty())
        ));

        FxTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testStartInitializesOffJavaFxThread() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        AtomicBoolean constructedOnFxThread = new AtomicBoolean();
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
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

        FxTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(initializationCompleted::countDown);
            fixture.controlViewModel().requestActionButton();
            assertAll(
                    () -> assertEquals(SimulationState.INITIALIZING, fixture.mainViewModel().getSimulationState()),
                    () -> assertFalse(fixture.mainViewModel().hasSimulationManager())
            );
        });

        assertTrue(constructionStarted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        assertFalse(constructedOnFxThread.get());
        releaseConstruction.countDown();
        assertTrue(initializationCompleted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FxTestSupport.runAndWait(() -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()));
        FxTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testTimedStartRetainsCapturedModeAndDurationDuringInitialization() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        CountDownLatch initializationCompleted = new CountDownLatch(1);
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            constructionStarted.countDown();
            try {
                releaseConstruction.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));

        FxTestSupport.runAndWait(() -> {
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.TIMED);
            fixture.controlViewModel().stepDurationProperty().setValue(2_000);
            fixture.mainViewModel().setSimulationInitializedListener(initializationCompleted::countDown);
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FxTestSupport.runAndWait(() -> {
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.BATCH_CONTINUOUS);
            fixture.controlViewModel().stepDurationProperty().setValue(50);
        });
        releaseConstruction.countDown();
        assertTrue(initializationCompleted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.RUNNING_TIMED, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(600L, fixture.mainViewModel().getThrottleDrawMillis())
        ));
        FxTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testBatchStartRetainsCapturedExecutionSettingsDuringInitialization() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        CountDownLatch batchCompleted = new CountDownLatch(1);
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
            constructionStarted.countDown();
            try {
                releaseConstruction.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            return new ConwaySimulationManager(config, cancellation);
        }, lifecycleExecutor));

        FxTestSupport.runAndWait(() -> {
            fixture.configViewModel().alivePercentProperty().setValue(0.0d);
            fixture.controlViewModel().startModeProperty().setValue(SimulationStartMode.START_IMMEDIATELY);
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.BATCH_SINGLE);
            fixture.controlViewModel().stepCountProperty().setValue(4);
            fixture.controlViewModel().terminationCheckProperty().setValue(SimulationTerminationCheck.UNCHECKED);
            fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> {
                if (newState == SimulationState.PAUSED) {
                    batchCompleted.countDown();
                }
            });
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FxTestSupport.runAndWait(() -> {
            fixture.controlViewModel().simulationModeProperty().setValue(SimulationMode.BATCH_CONTINUOUS);
            fixture.controlViewModel().stepCountProperty().setValue(1);
            fixture.controlViewModel().terminationCheckProperty().setValue(SimulationTerminationCheck.CHECKED);
        });
        releaseConstruction.countDown();
        assertTrue(batchCompleted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(4, fixture.mainViewModel().getStepCount())
        ));
        FxTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testInitializationFailureTransitionsToError() throws InterruptedException {
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(() -> createFixture((_, _) -> {
            throw new IllegalStateException("Test initialization failure");
        }, lifecycleExecutor));
        CountDownLatch failureHandled = new CountDownLatch(1);

        FxTestSupport.runAndWait(() -> {
            fixture.mainViewModel().simulationStateProperty().addListener((_, _, newState) -> {
                if (newState == SimulationState.ERROR) {
                    failureHandled.countDown();
                }
            });
            fixture.controlViewModel().requestActionButton();
        });

        assertTrue(failureHandled.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.ERROR, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(SimulationNotificationType.EXCEPTION,
                        fixture.mainViewModel().notificationTypeProperty().get()),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager())
        ));
        FxTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @Test
    void testInitializationListenerFailureTransitionsToError() throws InterruptedException {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        CountDownLatch failureHandled = new CountDownLatch(1);

        FxTestSupport.runAndWait(() -> {
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

        assertTrue(failureHandled.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.ERROR, fixture.mainViewModel().getSimulationState()),
                () -> assertEquals(SimulationNotificationType.EXCEPTION,
                        fixture.mainViewModel().notificationTypeProperty().get()),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager())
        ));
        FxTestSupport.runAndWait(fixture.mainViewModel()::shutdownSimulation);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testShutdownSuppressesStaleInitializationCompletion() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        AtomicBoolean initializationCompleted = new AtomicBoolean();
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(() -> createFixture((config, _) -> {
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

        FxTestSupport.runAndWait(() -> {
            fixture.mainViewModel().setSimulationInitializedListener(() -> initializationCompleted.set(true));
            fixture.controlViewModel().requestActionButton();
        });
        assertTrue(constructionStarted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        SimulationTermination termination = FxTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        releaseConstruction.countDown();
        assertTrue(termination.awaitTermination(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState()),
                () -> assertFalse(fixture.mainViewModel().hasSimulationManager()),
                () -> assertFalse(initializationCompleted.get())
        ));
    }

    @Test
    void testSimulationResetListenerRunsOnStartAndShutdown() throws InterruptedException {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        AtomicInteger resetCallCount = new AtomicInteger();

        FxTestSupport.runAndWait(() -> fixture.mainViewModel().setSimulationResetListener(resetCallCount::incrementAndGet));
        startAndAwaitInitialization(fixture);
        FxTestSupport.runAndWait(() -> {
            assertEquals(1, resetCallCount.get());
            fixture.mainViewModel().shutdownSimulation();
            assertEquals(2, resetCallCount.get());
        });
    }

    @Test
    void testShutdownReturnsSameTerminationHandle() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);

        SimulationTermination firstTermination = FxTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        SimulationTermination secondTermination = FxTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);

        assertAll(
                () -> assertSame(firstTermination, secondTermination),
                () -> assertTrue(firstTermination.awaitTermination(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
        );
    }

    @Test
    void testShutdownRemainsIdempotentWhenResetListenerFails() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> fixture.mainViewModel().setSimulationResetListener(
                () -> {
                    throw new IllegalStateException("Test reset listener failure");
                }));
        SimulationTermination firstTermination = FxTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);
        SimulationTermination secondTermination = FxTestSupport.supplyAndWaitNonNull(
                fixture.mainViewModel()::shutdownSimulation);

        assertAll(
                () -> assertSame(firstTermination, secondTermination),
                () -> assertTrue(firstTermination.awaitTermination(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
        );
    }

    @Test
    void testShutdownDoesNotWaitForInitializationWorker() throws InterruptedException {
        CountDownLatch constructionStarted = new CountDownLatch(1);
        CountDownLatch releaseConstruction = new CountDownLatch(1);
        ExecutorService lifecycleExecutor = Executors.newSingleThreadExecutor();
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(() -> createFixture((config, cancellation) -> {
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

        FxTestSupport.runAndWait(fixture.controlViewModel()::requestActionButton);
        assertTrue(constructionStarted.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        FxTestSupport.runAndWait(() -> {
            fixture.mainViewModel().shutdownSimulation();
            fixture.mainViewModel().shutdownSimulation();
            assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState());
        });

        assertTrue(lifecycleExecutor.isShutdown());
        assertFalse(lifecycleExecutor.isTerminated());
        releaseConstruction.countDown();
        assertTrue(lifecycleExecutor.awaitTermination(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    @Test
    void testShutdownClearsHistoryAndExtrema() throws InterruptedException {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);
        startAndAwaitInitialization(fixture);

        FxTestSupport.runAndWait(() -> {
            fixture.mainViewModel().shutdownSimulation();

            assertAll(
                    () -> assertEquals(SimulationState.SHUTTING_DOWN, fixture.mainViewModel().getSimulationState()),
                    () -> assertEquals(List.of(), fixture.observationViewModel().getStatisticsHistory()),
                    () -> assertEquals(StatisticExtrema.empty(), fixture.observationViewModel().getStatisticsExtrema())
            );
        });
    }

    @Test
    void testShutdownReleasesViewCallbacks() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultMainViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> {
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
