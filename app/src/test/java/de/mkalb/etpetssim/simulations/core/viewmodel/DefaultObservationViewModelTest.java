package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.executor.StepTimingStatistics;
import de.mkalb.etpetssim.simulations.core.model.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class DefaultObservationViewModelTest {

    private static <STA extends SimulationStatistics> DefaultObservationViewModel<?, ?, STA> createViewModel() {
        var stateWrapper = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        return new DefaultObservationViewModel<>(stateWrapper.getReadOnlyProperty());
    }

    @Test
    void testStatisticsExtremaPropertyInitialValueIsEmpty() {
        var viewModel = createViewModel();

        assertAll(
                () -> assertEquals(StatisticExtrema.empty(), viewModel.statisticsExtremaProperty().get()),
                () -> assertEquals(StatisticExtrema.empty(), viewModel.getStatisticsExtrema())
        );
    }

    @Test
    void testStatisticsHistoryPropertyInitialValueIsEmpty() {
        var viewModel = createViewModel();

        assertAll(
                () -> assertEquals(List.of(), viewModel.statisticsHistoryProperty().get()),
                () -> assertEquals(List.of(), viewModel.getStatisticsHistory())
        );
    }

    @Test
    void testSetStatisticsExtremaUpdatesPropertyAndGetter() {
        var viewModel = createViewModel();
        var extremum = new StatisticExtremum(42.0, 7L);
        var extrema = new StatisticExtrema(Map.of("metric", extremum), Map.of());

        viewModel.setStatisticsExtrema(extrema);

        assertAll(
                () -> assertEquals(extrema, viewModel.statisticsExtremaProperty().get()),
                () -> assertEquals(extrema, viewModel.getStatisticsExtrema())
        );
    }

    @Test
    void testSetStatisticsHistoryUpdatesPropertyAndGetter() {
        var viewModel = createViewModel();
        var sample = new StatisticSample(1, StepTimingStatistics.empty(), Map.of("metric", 3.0));
        var history = List.of(sample);

        viewModel.setStatisticsHistory(history);

        assertAll(
                () -> assertEquals(history, viewModel.statisticsHistoryProperty().get()),
                () -> assertEquals(history, viewModel.getStatisticsHistory())
        );
    }

    @Test
    void testStatisticsExtremaPropertyNotifiesListener() {
        var viewModel = createViewModel();
        var extrema = new StatisticExtrema(
                Map.of("metric", new StatisticExtremum(42.0, 7L)),
                Map.of());
        var observedValues = new ArrayList<StatisticExtrema>();
        viewModel.statisticsExtremaProperty().addListener((_, _, value) -> observedValues.add(value));

        viewModel.setStatisticsExtrema(extrema);

        assertEquals(List.of(extrema), observedValues);
    }

    @Test
    void testStatisticsHistoryPropertyNotifiesListener() {
        var viewModel = createViewModel();
        var history = List.of(new StatisticSample(
                1,
                StepTimingStatistics.empty(),
                Map.of("metric", 3.0)));
        var observedValues = new ArrayList<List<StatisticSample>>();
        viewModel.statisticsHistoryProperty().addListener((_, _, value) -> observedValues.add(value));

        viewModel.setStatisticsHistory(history);

        assertEquals(List.of(history), observedValues);
    }

    @Test
    void testSetStatisticsExtremaToEmptyResetsProperty() {
        var viewModel = createViewModel();
        viewModel.setStatisticsExtrema(new StatisticExtrema(Map.of("x", new StatisticExtremum(1.0, 0L)), Map.of()));

        viewModel.setStatisticsExtrema(StatisticExtrema.empty());

        assertEquals(StatisticExtrema.empty(), viewModel.statisticsExtremaProperty().get());
    }

    @Test
    void testSetStatisticsHistoryToEmptyResetsProperty() {
        var viewModel = createViewModel();
        viewModel.setStatisticsHistory(List.of(new StatisticSample(1, StepTimingStatistics.empty(), Map.of())));

        viewModel.setStatisticsHistory(List.of());

        assertEquals(List.of(), viewModel.statisticsHistoryProperty().get());
    }

}
