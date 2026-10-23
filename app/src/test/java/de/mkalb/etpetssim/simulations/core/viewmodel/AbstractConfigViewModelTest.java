package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class AbstractConfigViewModelTest {

    @Test
    void testCommonConfigSettingsRecord() {
        List<CellShape> cellShapes = new ArrayList<>(List.of(CellShape.SQUARE, CellShape.HEXAGON));
        List<GridEdgeBehavior> gridEdgeBehaviors = new ArrayList<>(List.of(GridEdgeBehavior.values()));
        List<CellDisplayMode> cellDisplayModes = new ArrayList<>(List.of(CellDisplayMode.values()));

        AbstractConfigViewModel.CommonConfigSettings settings = new AbstractConfigViewModel.CommonConfigSettings(
                CellShape.SQUARE, cellShapes,
                GridEdgeBehavior.values()[0], gridEdgeBehaviors,
                40, 10, 100, 2,
                30, 10, 80, 5,
                12, 4, 20,
                CellDisplayMode.values()[0], cellDisplayModes,
                "seed");
        cellShapes.clear();
        gridEdgeBehaviors.clear();
        cellDisplayModes.clear();

        assertAll(
                () -> assertEquals(CellShape.SQUARE, settings.cellShapeInitial()),
                () -> assertEquals(List.of(CellShape.SQUARE, CellShape.HEXAGON), settings.cellShapeValues()),
                () -> assertEquals(GridEdgeBehavior.values()[0], settings.gridEdgeBehaviorInitial()),
                () -> assertEquals(List.of(GridEdgeBehavior.values()), settings.gridEdgeBehaviorValues()),
                () -> assertEquals(40, settings.gridWidthInitial()),
                () -> assertEquals(10, settings.gridWidthMin()),
                () -> assertEquals(100, settings.gridWidthMax()),
                () -> assertEquals(2, settings.gridWidthStep()),
                () -> assertEquals(30, settings.gridHeightInitial()),
                () -> assertEquals(10, settings.gridHeightMin()),
                () -> assertEquals(80, settings.gridHeightMax()),
                () -> assertEquals(5, settings.gridHeightStep()),
                () -> assertEquals(12, settings.cellEdgeLengthInitial()),
                () -> assertEquals(4, settings.cellEdgeLengthMin()),
                () -> assertEquals(20, settings.cellEdgeLengthMax()),
                () -> assertEquals(CellDisplayMode.values()[0], settings.cellDisplayModeInitial()),
                () -> assertEquals(List.of(CellDisplayMode.values()), settings.cellDisplayModeValues()),
                () -> assertEquals("seed", settings.seedInitial()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> settings.cellShapeValues().add(CellShape.TRIANGLE)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> settings.gridEdgeBehaviorValues().clear()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> settings.cellDisplayModeValues().clear())
        );
    }

}
