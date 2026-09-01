package de.mkalb.etpetssim.simulations.core.view;

import de.mkalb.FxTestSupport;
import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.conway.ConwayFactory;
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
        try {
            Field field = AbstractMainView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to read field: " + fieldName, e);
        }
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
    void testShutdownUnregistersNotificationListener() {
        FxTestSupport.runAndWait(() -> {
            SimulationMainView view = ConwayFactory.createMainView();
            view.buildMainRegion();

            view.shutdownSimulation();

            assertNull(getNullableField(view, "notificationListener"));
        });
    }

}
