package de.mkalb.etpetssim.simulations.langton.viewmodel;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class LangtonEditToolBarViewModelTest {

    private static LangtonConfig createConfig(CellShape cellShape) {
        return new LangtonConfig(
                cellShape,
                LangtonConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LangtonConstraints.GRID_WIDTH_DEFAULT,
                LangtonConstraints.GRID_HEIGHT_DEFAULT,
                LangtonConstraints.CELL_EDGE_LENGTH_DEFAULT,
                LangtonConstraints.CELL_DISPLAY_MODE_DEFAULT,
                1L,
                LangtonConstraints.NEIGHBORHOOD_MODE_DEFAULT,
                LangtonMovementRules.fromString((cellShape == CellShape.TRIANGLE) ? "URR" : LangtonConstraints.RULE_DEFAULT)
        );
    }

    @Test
    void testUpdateAvailableDirectionsForTriangleClearsDirections() {
        LangtonEditToolBarViewModel viewModel = new LangtonEditToolBarViewModel();

        viewModel.updateAvailableDirections(createConfig(CellShape.TRIANGLE));

        assertAll(
                () -> assertTrue(viewModel.availableDirections().isEmpty()),
                () -> assertNull(viewModel.getSelectedDirection())
        );
    }

    @Test
    void testResolveSelectedAddAntContextForTriangleCellPointingDownUsesNorth() {
        LangtonEditToolBarViewModel viewModel = new LangtonEditToolBarViewModel();
        LangtonConfig config = createConfig(CellShape.TRIANGLE);

        var context = viewModel.resolveSelectedAddAntContext(config, new GridCoordinate(0, 0)).orElseThrow();

        assertEquals(CompassDirection.N, context.direction());
    }

    @Test
    void testResolveSelectedAddAntContextForTriangleCellPointingUpUsesSouth() {
        LangtonEditToolBarViewModel viewModel = new LangtonEditToolBarViewModel();
        LangtonConfig config = createConfig(CellShape.TRIANGLE);

        var context = viewModel.resolveSelectedAddAntContext(config, new GridCoordinate(1, 0)).orElseThrow();

        assertEquals(CompassDirection.S, context.direction());
    }

    @Test
    void testResolveSelectedAddAntContextForTriangleWithoutSelectedCellIsEmpty() {
        LangtonEditToolBarViewModel viewModel = new LangtonEditToolBarViewModel();
        LangtonConfig config = createConfig(CellShape.TRIANGLE);

        assertTrue(viewModel.resolveSelectedAddAntContext(config, null).isEmpty());
    }

}
