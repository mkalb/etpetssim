package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoResource;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Pet;
import de.mkalb.etpetssim.simulations.etpets.model.entity.PetTraits;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant;

final class EtpetsLogicTestSupport {

    private EtpetsLogicTestSupport() {
    }

    static EtpetsGridModel createGridModel() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(8, 8));
        return new EtpetsGridModel(structure,
                new SparseGridModel<>(structure, TerrainConstant.GROUND),
                new SparseGridModel<>(structure, NoResource.NO_RESOURCE),
                new SparseGridModel<>(structure, NoAgent.NO_AGENT));
    }

    static PetTraits createTraits() {
        return new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MAX,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT);
    }

    static Pet createPet(int stepIndexOfBirth, int currentEnergy) {
        return new Pet(
                1,
                null,
                null,
                stepIndexOfBirth,
                currentEnergy,
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_REMAINING_MIN,
                createTraits());
    }

}
