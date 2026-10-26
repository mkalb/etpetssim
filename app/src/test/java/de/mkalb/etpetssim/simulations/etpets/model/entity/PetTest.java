package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
final class PetTest {

    private static Pet createPet() {
        PetTraits traits = new PetTraits(
                EtpetsBalance.PET_TRAITS_MAX_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_MOVEMENT_COST_MODIFIER_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN,
                EtpetsBalance.PET_TRAITS_REPRODUCTION_COOLDOWN_MIN);
        return new Pet(7, null, null, 3, 20, 5, traits);
    }

    @Test
    void testToDisplayString() {
        Pet newPet = createPet();
        Pet activePet = createPet();
        activePet.recordLastAction(PetActionType.EAT, 12);
        Pet deadPet = createPet();
        deadPet.recordLastAction(PetActionType.MOVE, -4);
        deadPet.die();

        assertAll(
                () -> assertEquals("[PET #7 *3 E=20 C=5 A=-]", newPet.toDisplayString()),
                () -> assertEquals("[PET #7 *3 E=20 C=5 A=eat@12]", activePet.toDisplayString()),
                () -> assertEquals("[PET #7 *3 E=20 C=5 A=move@-4 †]", deadPet.toDisplayString())
        );
    }

}
