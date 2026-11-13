package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.model.SimulationStatistics;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema;
import de.mkalb.etpetssim.simulations.core.model.StatisticsExtremum;
import de.mkalb.etpetssim.simulations.core.model.StatisticsSample;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@Execution(ExecutionMode.SAME_THREAD)
final class ObservationViewModelTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static ObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, TestStatistics> createViewModel() {
        return new ObservationViewModel<>();
    }

    @Test
    void testGetStatisticsReturnsEmptyInitially() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);

        FXTestSupport.runAndWait(
                () -> assertEquals(Optional.empty(), viewModel.getStatistics()));
    }

    @Test
    void testSetStatistics() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);
        var statistics = new TestStatistics(7);

        FXTestSupport.runAndWait(() -> {
            viewModel.setStatistics(statistics);

            assertEquals(Optional.of(statistics), viewModel.getStatistics());
        });
    }

    @Test
    void testGetStatisticsExtremaReturnsEmptyInitially() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);

        FXTestSupport.runAndWait(
                () -> assertEquals(StatisticsExtrema.empty(), viewModel.getStatisticsExtrema()));
    }

    @Test
    void testSetStatisticsExtrema() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);
        var extremum = new StatisticsExtremum(42.0, 7);
        var extrema = new StatisticsExtrema(Map.of("metric", extremum), Map.of());

        FXTestSupport.runAndWait(() -> {
            viewModel.setStatisticsExtrema(extrema);

            assertEquals(extrema, viewModel.getStatisticsExtrema());
        });
    }

    @Test
    void testSetStatisticsExtremaResetsToEmpty() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);

        FXTestSupport.runAndWait(() -> {
            viewModel.setStatisticsExtrema(new StatisticsExtrema(
                    Map.of("x", new StatisticsExtremum(1.0, 0)), Map.of()));
            viewModel.setStatisticsExtrema(StatisticsExtrema.empty());

            assertEquals(StatisticsExtrema.empty(), viewModel.getStatisticsExtrema());
        });
    }

    @Test
    void testStatisticsHistoryPropertyInitialValueIsEmpty() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);

        FXTestSupport.runAndWait(
                () -> assertEquals(List.of(), viewModel.statisticsHistoryProperty().get()));
    }

    @Test
    void testStatisticsHistoryPropertyNotifiesListener() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);
        var history = List.of(new StatisticsSample(
                1,
                StepTimingStatistics.empty(),
                Map.of("metric", 3.0)));
        var observedValues = new ArrayList<List<StatisticsSample>>();

        FXTestSupport.runAndWait(() -> {
            viewModel.statisticsHistoryProperty().addListener((_, _, value) -> observedValues.add(value));
            viewModel.setStatisticsHistory(history);
        });

        assertEquals(List.of(history), observedValues);
    }

    @Test
    void testSetStatisticsHistory() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);
        var sample = new StatisticsSample(1, StepTimingStatistics.empty(), Map.of("metric", 3.0));
        var history = List.of(sample);

        FXTestSupport.runAndWait(() -> {
            viewModel.setStatisticsHistory(history);

            assertEquals(history, viewModel.statisticsHistoryProperty().get());
        });
    }

    @Test
    void testSetStatisticsHistoryToEmptyResetsProperty() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);

        FXTestSupport.runAndWait(() -> {
            viewModel.setStatisticsHistory(List.of(new StatisticsSample(
                    1, StepTimingStatistics.empty(), Map.of())));
            viewModel.setStatisticsHistory(List.of());

            assertEquals(List.of(), viewModel.statisticsHistoryProperty().get());
        });
    }

    @Test
    void testResetStatisticsClearsStatisticsExtremaAndHistory() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);
        var statistics = new TestStatistics(7);
        var extrema = new StatisticsExtrema(Map.of("metric", new StatisticsExtremum(42.0d, 7)), Map.of());
        var history = List.of(new StatisticsSample(7, StepTimingStatistics.empty(), Map.of("metric", 42.0d)));

        FXTestSupport.runAndWait(() -> {
            viewModel.setStatistics(statistics);
            viewModel.setStatisticsExtrema(extrema);
            viewModel.setStatisticsHistory(history);
            viewModel.resetStatistics();

            assertAll(
                    () -> assertEquals(Optional.empty(), viewModel.getStatistics()),
                    () -> assertEquals(StatisticsExtrema.empty(), viewModel.getStatisticsExtrema()),
                    () -> assertEquals(List.of(), viewModel.statisticsHistoryProperty().get())
            );
        });
    }

    @Test
    void testBindSelectedGridCellPropertyTracksSourceChanges() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);
        var source = new SimpleObjectProperty<GridCell<ConwayEntity>>();
        var selectedCell = new GridCell<>(new GridCoordinate(2, 3), ConwayEntity.ALIVE);

        FXTestSupport.runAndWait(() -> {
            viewModel.bindSelectedGridCellProperty(source);
            source.set(selectedCell);

            assertSame(selectedCell, viewModel.selectedGridCellProperty().get());
        });
    }

    @Test
    void testBindSelectedGridCellPropertyReplacesPreviousBinding() {
        var viewModel = FXTestSupport.supplyAndWaitNonNull(ObservationViewModelTest::createViewModel);
        var firstSource = new SimpleObjectProperty<GridCell<ConwayEntity>>();
        var secondSource = new SimpleObjectProperty<GridCell<ConwayEntity>>();
        var firstCell = new GridCell<>(new GridCoordinate(2, 3), ConwayEntity.ALIVE);
        var secondCell = new GridCell<>(new GridCoordinate(4, 5), ConwayEntity.DEAD);

        FXTestSupport.runAndWait(() -> {
            viewModel.bindSelectedGridCellProperty(firstSource);
            firstSource.set(firstCell);
            viewModel.bindSelectedGridCellProperty(secondSource);
            secondSource.set(secondCell);
            firstSource.set(null);

            assertSame(secondCell, viewModel.selectedGridCellProperty().get());
        });
    }

    private record TestStatistics(int stepCount) implements SimulationStatistics {

        @NonNull
        @Override
        public GridStructure gridStructure() {
            throw new AssertionError("Grid structure is not needed by this test fixture");
        }

    }

}
