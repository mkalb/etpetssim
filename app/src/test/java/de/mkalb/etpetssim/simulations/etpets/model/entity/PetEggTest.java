package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class PetEggTest {

    private static PetGenome createGenome() {
        return new PetGenome(new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN));
    }

    @Test
    void testToDisplayString() {
        PetEgg egg = new PetEgg(7, 1, 2, createGenome(), 3, 4);

        assertEquals("[PET_EGG #7 *3 I=4]", egg.toDisplayString());
    }

}
