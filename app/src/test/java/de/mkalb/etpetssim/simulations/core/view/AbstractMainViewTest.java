package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.conway.ConwayFactory;
import de.mkalb.etpetssim.simulations.core.SimulationTermination;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationMainViewModel;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
@Execution(ExecutionMode.SAME_THREAD)
final class AbstractMainViewTest {

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FxTestSupport.ensureStarted();
    }

    private static @Nullable Object getNullableField(Object target, String fieldName) {
        return getNullableField(target, AbstractMainView.class, fieldName);
    }

    private static @Nullable Object getNullableField(Object target, Class<?> declaringClass, String fieldName) {
        try {
            Field field = declaringClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to read field: " + fieldName, e);
        }
    }

    private static Object getNonNullField(Object target, String fieldName) {
        return Objects.requireNonNull(getNullableField(target, fieldName),
                "Field '" + fieldName + "' must not be null");
    }

    @Test
    void testBuildMainRegionUsesObservationWidthSplit() {
        FxTestSupport.runAndWait(() -> {
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
        FxTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();

            var firstRegion = view.buildMainRegion();
            var secondRegion = view.buildMainRegion();

            assertSame(firstRegion, secondRegion);
        });
    }

    @Test
    void testShutdownUnregistersNotificationListener() {
        FxTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            view.buildMainRegion();

            view.shutdownSimulation();

            assertNull(getNullableField(view, "notificationListener"));
        });
    }

    @Test
    void testShutdownUnregistersObservationListener() {
        FxTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            AbstractObservationView<?, ?, ?, ?> observationView = assertInstanceOf(AbstractObservationView.class,
                    getNonNullField(view, "observationView"));

            view.shutdownSimulation();

            assertNull(getNullableField(observationView, AbstractObservationView.class, "selectedGridCellListener"));
        });
    }

    @Test
    void testShutdownForwardsViewModelTerminationHandle() {
        FxTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            SimulationMainViewModel viewModel = assertInstanceOf(SimulationMainViewModel.class,
                    getNonNullField(view, "viewModel"));

            SimulationTermination termination = view.shutdownSimulation();

            assertSame(termination, viewModel.shutdownSimulation());
        });
    }

    @Test
    void testShutdownRemainsIdempotentAfterRepeatedBuild() {
        FxTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            view.buildMainRegion();
            view.buildMainRegion();

            SimulationTermination firstTermination = view.shutdownSimulation();
            SimulationTermination secondTermination = view.shutdownSimulation();

            assertSame(firstTermination, secondTermination);
        });
    }

}
