package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class DefaultObservationViewModelTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static Fixture createFixture() {
        var state = new SimpleObjectProperty<>(SimulationState.READY);
        return new Fixture(state, new DefaultObservationViewModel<>(state));
    }

    @Test
    void testStatisticsExtremaPropertyInitialValueIsEmpty() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(StatisticsExtrema.empty(), fixture.viewModel().statisticsExtremaProperty().get()),
                () -> assertEquals(StatisticsExtrema.empty(), fixture.viewModel().getStatisticsExtrema())
        ));
    }

    @Test
    void testStatisticsHistoryPropertyInitialValueIsEmpty() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertEquals(List.of(), fixture.viewModel().statisticsHistoryProperty().get()),
                () -> assertEquals(List.of(), fixture.viewModel().getStatisticsHistory())
        ));
    }

    @Test
    void testGetStatisticsReturnsEmptyInitially() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> assertAll(
                () -> assertNull(fixture.viewModel().statisticsProperty().get()),
                () -> assertEquals(Optional.empty(), fixture.viewModel().getStatistics())
        ));
    }

    @Test
    void testSetStatisticsUpdatesPropertyAndOptionalGetter() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var statistics = new TestStatistics(7);

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatistics(statistics);

            assertAll(
                    () -> assertSame(statistics, fixture.viewModel().statisticsProperty().get()),
                    () -> assertEquals(Optional.of(statistics), fixture.viewModel().getStatistics())
            );
        });
    }

    @Test
    void testSetStatisticsExtremaUpdatesPropertyAndGetter() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var extremum = new StatisticsExtremum(42.0, 7L);
        var extrema = new StatisticsExtrema(Map.of("metric", extremum), Map.of());

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsExtrema(extrema);

            assertAll(
                    () -> assertEquals(extrema, fixture.viewModel().statisticsExtremaProperty().get()),
                    () -> assertEquals(extrema, fixture.viewModel().getStatisticsExtrema())
            );
        });
    }

    @Test
    void testSetStatisticsHistoryUpdatesPropertyAndGetter() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var sample = new StatisticsSample(1, StepTimingStatistics.empty(), Map.of("metric", 3.0));
        var history = List.of(sample);

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsHistory(history);

            assertAll(
                    () -> assertEquals(history, fixture.viewModel().statisticsHistoryProperty().get()),
                    () -> assertEquals(history, fixture.viewModel().getStatisticsHistory())
            );
        });
    }

    @Test
    void testStatisticsExtremaPropertyNotifiesListener() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var extrema = new StatisticsExtrema(
                Map.of("metric", new StatisticsExtremum(42.0, 7L)),
                Map.of());
        var observedValues = new ArrayList<StatisticsExtrema>();

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().statisticsExtremaProperty().addListener((_, _, value) -> observedValues.add(value));
            fixture.viewModel().setStatisticsExtrema(extrema);
        });

        assertEquals(List.of(extrema), observedValues);
    }

    @Test
    void testStatisticsHistoryPropertyNotifiesListener() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var history = List.of(new StatisticsSample(
                1,
                StepTimingStatistics.empty(),
                Map.of("metric", 3.0)));
        var observedValues = new ArrayList<List<StatisticsSample>>();

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().statisticsHistoryProperty().addListener((_, _, value) -> observedValues.add(value));
            fixture.viewModel().setStatisticsHistory(history);
        });

        assertEquals(List.of(history), observedValues);
    }

    @Test
    void testSetStatisticsExtremaToEmptyResetsProperty() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsExtrema(new StatisticsExtrema(
                    Map.of("x", new StatisticsExtremum(1.0, 0L)), Map.of()));
            fixture.viewModel().setStatisticsExtrema(StatisticsExtrema.empty());

            assertEquals(StatisticsExtrema.empty(), fixture.viewModel().statisticsExtremaProperty().get());
        });
    }

    @Test
    void testSetStatisticsHistoryToEmptyResetsProperty() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatisticsHistory(List.of(new StatisticsSample(
                    1, StepTimingStatistics.empty(), Map.of())));
            fixture.viewModel().setStatisticsHistory(List.of());

            assertEquals(List.of(), fixture.viewModel().statisticsHistoryProperty().get());
        });
    }

    @Test
    void testResetStatisticsClearsStatisticsExtremaAndHistory() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var statistics = new TestStatistics(7);
        var extrema = new StatisticsExtrema(Map.of("metric", new StatisticsExtremum(42.0d, 7L)), Map.of());
        var history = List.of(new StatisticsSample(7, StepTimingStatistics.empty(), Map.of("metric", 42.0d)));

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().setStatistics(statistics);
            fixture.viewModel().setStatisticsExtrema(extrema);
            fixture.viewModel().setStatisticsHistory(history);
            fixture.viewModel().resetStatistics();

            assertAll(
                    () -> assertEquals(Optional.empty(), fixture.viewModel().getStatistics()),
                    () -> assertEquals(StatisticsExtrema.empty(), fixture.viewModel().getStatisticsExtrema()),
                    () -> assertEquals(List.of(), fixture.viewModel().getStatisticsHistory())
            );
        });
    }

    @Test
    void testBindSelectedGridCellPropertyTracksSourceChanges() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var source = new SimpleObjectProperty<GridCell<ConwayEntity>>();
        var selectedCell = new GridCell<>(new GridCoordinate(2, 3), ConwayEntity.ALIVE);

        FXTestSupport.runAndWait(() -> {
            fixture.viewModel().bindSelectedGridCellProperty(source);
            source.set(selectedCell);

            assertSame(selectedCell, fixture.viewModel().selectedGridCellProperty().get());
        });
    }

    @Test
    void testBindSelectedGridCellPropertyReplacesPreviousBinding() {
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);
        var firstSource = new SimpleObjectProperty<GridCell<ConwayEntity>>();
        var secondSource = new SimpleObjectProperty<GridCell<ConwayEntity>>();
        var firstCell = new GridCell<>(new GridCoordinate(2, 3), ConwayEntity.ALIVE);
        var secondCell = new GridCell<>(new GridCoordinate(4, 5), ConwayEntity.DEAD);

        FXTestSupport.runAndWait(() -> {
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
        Fixture fixture = FXTestSupport.supplyAndWaitNonNull(DefaultObservationViewModelTest::createFixture);

        FXTestSupport.runAndWait(() -> {
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

        @NonNull
        @Override
        public GridStructure gridStructure() {
            throw new AssertionError("Grid structure is not needed by this test fixture.");
        }

    }

}
