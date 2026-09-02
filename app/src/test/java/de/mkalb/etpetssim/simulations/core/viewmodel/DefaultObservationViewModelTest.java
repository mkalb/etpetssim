package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class DefaultObservationViewModelTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
    }

    private static Fixture createFixture() {
        var state = new SimpleObjectProperty<>(SimulationState.READY);
        return new Fixture(state, new DefaultObservationViewModel<>(state));
    }

    @Test
    void testStatisticsExtremaPropertyInitialValueIsEmpty() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(StatisticExtrema.empty(), fixture.viewModel().statisticsExtremaProperty().get()),
                () -> assertEquals(StatisticExtrema.empty(), fixture.viewModel().getStatisticsExtrema())
        ));
    }

    @Test
    void testStatisticsHistoryPropertyInitialValueIsEmpty() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(List.of(), fixture.viewModel().statisticsHistoryProperty().get()),
                () -> assertEquals(List.of(), fixture.viewModel().getStatisticsHistory())
        ));
    }

    @Test
    void testStatisticsIsInitiallyEmpty() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> assertAll(
                () -> assertNull(fixture.viewModel().statisticsProperty().get()),
                () -> assertEquals(Optional.empty(), fixture.viewModel().getStatistics())
        ));
    }

    @Test
    void testSetStatisticsUpdatesPropertyAndOptionalGetter() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var statistics = new TestStatistics(7);

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatistics(statistics);

            assertAll(
                    () -> assertSame(statistics, fixture.viewModel().statisticsProperty().get()),
                    () -> assertEquals(Optional.of(statistics), fixture.viewModel().getStatistics())
            );
        });
    }

    @Test
    void testSetStatisticsExtremaUpdatesPropertyAndGetter() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var extremum = new StatisticExtremum(42.0, 7L);
        var extrema = new StatisticExtrema(Map.of("metric", extremum), Map.of());

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsExtrema(extrema);

            assertAll(
                    () -> assertEquals(extrema, fixture.viewModel().statisticsExtremaProperty().get()),
                    () -> assertEquals(extrema, fixture.viewModel().getStatisticsExtrema())
            );
        });
    }

    @Test
    void testSetStatisticsHistoryUpdatesPropertyAndGetter() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var sample = new StatisticSample(1, StepTimingStatistics.empty(), Map.of("metric", 3.0));
        var history = List.of(sample);

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsHistory(history);

            assertAll(
                    () -> assertEquals(history, fixture.viewModel().statisticsHistoryProperty().get()),
                    () -> assertEquals(history, fixture.viewModel().getStatisticsHistory())
            );
        });
    }

    @Test
    void testStatisticsExtremaPropertyNotifiesListener() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var extrema = new StatisticExtrema(
                Map.of("metric", new StatisticExtremum(42.0, 7L)),
                Map.of());
        var observedValues = new ArrayList<StatisticExtrema>();

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().statisticsExtremaProperty().addListener((_, _, value) -> observedValues.add(value));
            fixture.viewModel().setStatisticsExtrema(extrema);
        });

        assertEquals(List.of(extrema), observedValues);
    }

    @Test
    void testStatisticsHistoryPropertyNotifiesListener() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var history = List.of(new StatisticSample(
                1,
                StepTimingStatistics.empty(),
                Map.of("metric", 3.0)));
        var observedValues = new ArrayList<List<StatisticSample>>();

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().statisticsHistoryProperty().addListener((_, _, value) -> observedValues.add(value));
            fixture.viewModel().setStatisticsHistory(history);
        });

        assertEquals(List.of(history), observedValues);
    }

    @Test
    void testSetStatisticsExtremaToEmptyResetsProperty() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsExtrema(new StatisticExtrema(
                    Map.of("x", new StatisticExtremum(1.0, 0L)), Map.of()));
            fixture.viewModel().setStatisticsExtrema(StatisticExtrema.empty());

            assertEquals(StatisticExtrema.empty(), fixture.viewModel().statisticsExtremaProperty().get());
        });
    }

    @Test
    void testSetStatisticsHistoryToEmptyResetsProperty() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsHistory(List.of(new StatisticSample(
                    1, StepTimingStatistics.empty(), Map.of())));
            fixture.viewModel().setStatisticsHistory(List.of());

            assertEquals(List.of(), fixture.viewModel().statisticsHistoryProperty().get());
        });
    }

    @Test
    void testResetStatisticsClearsStatisticsExtremaAndHistory() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var statistics = new TestStatistics(7);
        var extrema = new StatisticExtrema(Map.of("metric", new StatisticExtremum(42.0d, 7L)), Map.of());
        var history = List.of(new StatisticSample(7, StepTimingStatistics.empty(), Map.of("metric", 42.0d)));

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatistics(statistics);
            fixture.viewModel().setStatisticsExtrema(extrema);
            fixture.viewModel().setStatisticsHistory(history);
            fixture.viewModel().resetStatistics();

            assertAll(
                    () -> assertEquals(Optional.empty(), fixture.viewModel().getStatistics()),
                    () -> assertEquals(StatisticExtrema.empty(), fixture.viewModel().getStatisticsExtrema()),
                    () -> assertEquals(List.of(), fixture.viewModel().getStatisticsHistory())
            );
        });
    }

    @Test
    void testBindSelectedGridCellPropertyTracksSourceChanges() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var source = new SimpleObjectProperty<@Nullable GridCell<ConwayEntity>>();
        var selectedCell = new GridCell<>(new GridCoordinate(2, 3), ConwayEntity.ALIVE);

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().bindSelectedGridCellProperty(source);
            source.set(selectedCell);

            assertSame(selectedCell, fixture.viewModel().selectedGridCellProperty().get());
        });
    }

    @Test
    void testBindSelectedGridCellPropertyReplacesPreviousBinding() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var firstSource = new SimpleObjectProperty<@Nullable GridCell<ConwayEntity>>();
        var secondSource = new SimpleObjectProperty<@Nullable GridCell<ConwayEntity>>();
        var firstCell = new GridCell<>(new GridCoordinate(2, 3), ConwayEntity.ALIVE);
        var secondCell = new GridCell<>(new GridCoordinate(4, 5), ConwayEntity.DEAD);

        FxTestSupport.runAndWait(() -> {
            fixture.viewModel().bindSelectedGridCellProperty(firstSource);
            firstSource.set(firstCell);
            fixture.viewModel().bindSelectedGridCellProperty(secondSource);
            secondSource.set(secondCell);
            firstSource.set(null);

            assertSame(secondCell, fixture.viewModel().selectedGridCellProperty().get());
        });
    }

    @Test
    void testSimulationStatePropertyIsForwarded() {
        Fixture fixture = FxTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FxTestSupport.runAndWait(() -> {
            assertSame(fixture.state(), fixture.viewModel().simulationStateProperty());
            fixture.state().set(SimulationState.PAUSED);

            assertEquals(SimulationState.PAUSED, fixture.viewModel().getSimulationState());
        });
    }

    private record Fixture(
            SimpleObjectProperty<SimulationState> state,
            DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, TestStatistics> viewModel) {
    }

    @SuppressWarnings("SameParameterValue")
    private record TestStatistics(int stepCount) implements SimulationStatistics {

        @Override
        public int getStepCount() {
            return stepCount;
        }

        @Override
        public GridStructure getGridStructure() {
            throw new AssertionError("Grid structure is not needed by this test fixture.");
        }

    }

}
