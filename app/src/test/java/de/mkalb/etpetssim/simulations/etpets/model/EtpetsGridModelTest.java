package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.etpets.model.entity.AgentEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoResource;
import de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class EtpetsGridModelTest {

    private static GridStructure createStructure(int width, int height) {
        return new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY), new GridSize(width, height));
    }

    private static WritableGridModel<TerrainEntity> createTerrainModel(GridStructure structure) {
        return new SparseGridModel<>(structure, TerrainConstant.GROUND);
    }

    private static WritableGridModel<ResourceEntity> createResourceModel(GridStructure structure) {
        return new SparseGridModel<>(structure, NoResource.NO_RESOURCE);
    }

    private static WritableGridModel<AgentEntity> createAgentModel(GridStructure structure) {
        return new SparseGridModel<>(structure, NoAgent.NO_AGENT);
    }

    private static EtpetsGridModel createModel() {
        GridStructure structure = createStructure(8, 8);
        return new EtpetsGridModel(structure,
                createTerrainModel(structure),
                createResourceModel(structure),
                createAgentModel(structure));
    }

    @Test
    void testRecord() {
        GridStructure structure = createStructure(8, 8);
        WritableGridModel<TerrainEntity> terrainModel = createTerrainModel(structure);
        WritableGridModel<ResourceEntity> resourceModel = createResourceModel(structure);
        WritableGridModel<AgentEntity> agentModel = createAgentModel(structure);

        EtpetsGridModel model = new EtpetsGridModel(structure, terrainModel, resourceModel, agentModel);

        assertAll(
                () -> assertSame(structure, model.structure()),
                () -> assertSame(terrainModel, model.terrainModel()),
                () -> assertSame(resourceModel, model.resourceModel()),
                () -> assertSame(agentModel, model.agentModel())
        );
    }

    @Test
    void testConstructorRejectsDifferentStructure() {
        GridStructure structure = createStructure(8, 8);
        GridStructure otherStructure = createStructure(8, 10);

        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new EtpetsGridModel(structure,
                        createTerrainModel(otherStructure), createResourceModel(structure), createAgentModel(structure))),
                () -> assertThrows(IllegalArgumentException.class, () -> new EtpetsGridModel(structure,
                        createTerrainModel(structure), createResourceModel(otherStructure), createAgentModel(structure))),
                () -> assertThrows(IllegalArgumentException.class, () -> new EtpetsGridModel(structure,
                        createTerrainModel(structure), createResourceModel(structure), createAgentModel(otherStructure)))
        );
    }

    @Test
    void testGetEntities() {
        EtpetsGridModel model = createModel();
        GridCoordinate coordinate = new GridCoordinate(1, 2);
        model.terrainModel().setEntity(coordinate, TerrainConstant.ROCK);

        assertEquals(List.of(TerrainConstant.ROCK, NoResource.NO_RESOURCE, NoAgent.NO_AGENT),
                model.getEntities(coordinate));
    }

    @Test
    void testLayerCount() {
        EtpetsGridModel model = createModel();

        assertEquals(model.getEntities(new GridCoordinate(0, 0)).size(), model.layerCount());
    }

}
