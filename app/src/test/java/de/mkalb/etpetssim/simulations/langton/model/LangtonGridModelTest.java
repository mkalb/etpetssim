package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.langton.model.entity.Ant;
import de.mkalb.etpetssim.simulations.langton.model.entity.AntEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("SameParameterValue")
final class LangtonGridModelTest {

    private static GridStructure createStructure(int width, int height) {
        return new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(width, height));
    }

    private static WritableGridModel<TerrainConstant> createTerrainModel(GridStructure structure) {
        return new ArrayGridModel<>(structure, TerrainConstant.UNVISITED);
    }

    private static WritableGridModel<AntEntity> createAntModel(GridStructure structure) {
        return new SparseGridModel<>(structure, NoAgent.NO_AGENT);
    }

    private static LangtonGridModel createModel() {
        GridStructure structure = createStructure(8, 8);
        return new LangtonGridModel(structure, createTerrainModel(structure), createAntModel(structure));
    }

    @Test
    void testRecord() {
        GridStructure structure = createStructure(8, 8);
        WritableGridModel<TerrainConstant> terrainModel = createTerrainModel(structure);
        WritableGridModel<AntEntity> antModel = createAntModel(structure);

        LangtonGridModel model = new LangtonGridModel(structure, terrainModel, antModel);

        assertAll(
                () -> assertSame(structure, model.structure()),
                () -> assertSame(terrainModel, model.terrainModel()),
                () -> assertSame(antModel, model.antModel())
        );
    }

    @Test
    void testConstructorRejectsDifferentStructure() {
        GridStructure structure = createStructure(8, 8);
        GridStructure otherStructure = createStructure(8, 10);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new LangtonGridModel(structure,
                        createTerrainModel(otherStructure), createAntModel(structure))),
                () -> assertThrows(IllegalArgumentException.class, () -> new LangtonGridModel(structure,
                        createTerrainModel(structure), createAntModel(otherStructure)))
        );
    }

    @Test
    void testGetEntities() {
        LangtonGridModel model = createModel();
        GridCoordinate coordinate = new GridCoordinate(1, 2);
        Ant ant = new Ant(CompassDirection.N);
        model.terrainModel().setEntity(coordinate, TerrainConstant.COLOR_1);
        model.antModel().setEntity(coordinate, ant);

        assertEquals(List.of(TerrainConstant.COLOR_1, ant), model.getEntities(coordinate));
    }

    @Test
    void testLayerCount() {
        LangtonGridModel model = createModel();

        assertEquals(model.getEntities(new GridCoordinate(0, 0)).size(), model.layerCount());
    }

}
