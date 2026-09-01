package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.core.shared.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

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
        return new Fixture(mainViewModel, controlViewModel, observationViewModel);
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

    @Test
    void testStartForwardsInitialHistoryAndExtrema() throws InterruptedException {
        Fixture fixture = FxTestSupport.supplyAndWait(DefaultMainViewModelTest::createFixture);
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
        Fixture fixture = FxTestSupport.supplyAndWait(() -> createFixture((config, cancellation) -> {
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
    void testShutdownClearsHistoryAndExtrema() throws InterruptedException {
        Fixture fixture = FxTestSupport.supplyAndWait(DefaultMainViewModelTest::createFixture);
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

    private record Fixture(
            DefaultMainViewModel<
                    ConwayEntity,
                    GridCell<ConwayEntity>,
                    WritableGridModel<ConwayEntity>,
                    ConwayConfig,
                    ConwayStatistics,
                    ConwaySimulationManager,
                    ConwayUserActionContext> mainViewModel,
            DefaultControlViewModel controlViewModel,
            DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics> observationViewModel) {
    }

}
