package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.model.StatisticExtrema;
import de.mkalb.etpetssim.simulations.core.shared.*;
import javafx.beans.property.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

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
        ObjectProperty<SimulationState> simulationState =
                new SimpleObjectProperty<>(SimulationState.INITIAL);
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
                ConwaySimulationManager::new,
                ReadableGridModel::getGridCell,
                new ConwayUserAction());
        return new Fixture(mainViewModel, controlViewModel, observationViewModel);
    }

    @Test
    void testStartForwardsInitialHistoryAndExtrema() {
        FxTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();

            fixture.controlViewModel().requestActionButton();

            assertAll(
                    () -> assertEquals(SimulationState.PAUSED, fixture.mainViewModel().getSimulationState()),
                    () -> assertEquals(1, fixture.observationViewModel().getStatisticsHistory().size()),
                    () -> assertEquals(0L, fixture.observationViewModel().getStatisticsHistory().getFirst().stepCount()),
                    () -> assertFalse(fixture.observationViewModel().getStatisticsExtrema().minimumValues().isEmpty()),
                    () -> assertFalse(fixture.observationViewModel().getStatisticsExtrema().maximumValues().isEmpty())
            );

            fixture.mainViewModel().shutdownSimulation();
        });
    }

    @Test
    void testShutdownClearsHistoryAndExtrema() {
        FxTestSupport.runAndWait(() -> {
            Fixture fixture = createFixture();
            fixture.controlViewModel().requestActionButton();

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
