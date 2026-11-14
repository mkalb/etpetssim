package de.mkalb.etpetssim.simulations.core.viewmodel;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class AbstractConfigViewModelTest {

    @Test
    void testCommonConfigConstraintsRecord() {
        List<CellShape> cellShapes = new ArrayList<>(List.of(CellShape.SQUARE, CellShape.HEXAGON));
        List<GridEdgeBehavior> gridEdgeBehaviors = new ArrayList<>(List.of(GridEdgeBehavior.values()));
        List<CellDisplayMode> cellDisplayModes = new ArrayList<>(List.of(CellDisplayMode.values()));

        AbstractConfigViewModel.CommonConfigConstraints constraints =
                new AbstractConfigViewModel.CommonConfigConstraints(
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
                () -> assertEquals(CellShape.SQUARE, constraints.cellShapeDefault()),
                () -> assertEquals(List.of(CellShape.SQUARE, CellShape.HEXAGON), constraints.cellShapeValues()),
                () -> assertEquals(GridEdgeBehavior.values()[0], constraints.gridEdgeBehaviorDefault()),
                () -> assertEquals(List.of(GridEdgeBehavior.values()), constraints.gridEdgeBehaviorValues()),
                () -> assertEquals(40, constraints.gridWidthDefault()),
                () -> assertEquals(10, constraints.gridWidthMin()),
                () -> assertEquals(100, constraints.gridWidthMax()),
                () -> assertEquals(2, constraints.gridWidthStep()),
                () -> assertEquals(30, constraints.gridHeightDefault()),
                () -> assertEquals(10, constraints.gridHeightMin()),
                () -> assertEquals(80, constraints.gridHeightMax()),
                () -> assertEquals(5, constraints.gridHeightStep()),
                () -> assertEquals(12, constraints.cellEdgeLengthDefault()),
                () -> assertEquals(4, constraints.cellEdgeLengthMin()),
                () -> assertEquals(20, constraints.cellEdgeLengthMax()),
                () -> assertEquals(CellDisplayMode.values()[0], constraints.cellDisplayModeDefault()),
                () -> assertEquals(List.of(CellDisplayMode.values()), constraints.cellDisplayModeValues()),
                () -> assertEquals("seed", constraints.seedDefault()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> constraints.cellShapeValues().add(CellShape.TRIANGLE)),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> constraints.gridEdgeBehaviorValues().clear()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> constraints.cellDisplayModeValues().clear())
        );
    }

}
