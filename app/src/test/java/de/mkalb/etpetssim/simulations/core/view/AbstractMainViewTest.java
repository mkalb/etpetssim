package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.conway.ConwayFactory;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AbstractMainViewTest {

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> createMainView() {
        return assertInstanceOf(AbstractMainView.class, ConwayFactory.createMainView());
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
    void testShutdownSimulationForwardsViewModelTerminationHandle() {
        FXTestSupport.runAndWait(() -> {
            AbstractMainView<?, ?, ?, ?, ?, ?, ?, ?> view = createMainView();
            MainViewModel<?, ?, ?, ?, ?, ?, ?> viewModel = view.viewModel;

            SimulationTerminationHandle terminationHandle = view.shutdownSimulation();

            assertSame(terminationHandle, viewModel.shutdownSimulation());
        });
    }

    @Test
    void testShutdownSimulationRemainsIdempotentAfterRepeatedBuild() {
        FXTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            view.buildMainRegion();
            view.buildMainRegion();

            SimulationTerminationHandle firstTerminationHandle = view.shutdownSimulation();
            SimulationTerminationHandle secondTerminationHandle = view.shutdownSimulation();

            assertSame(firstTerminationHandle, secondTerminationHandle);
        });
    }

}
