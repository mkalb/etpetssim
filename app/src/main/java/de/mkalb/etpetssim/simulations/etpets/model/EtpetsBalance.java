package de.mkalb.etpetssim.simulations.etpets.model;

public final class EtpetsBalance {

    // Trail
    public static final int TRAIL_INTENSITY_RANGE_MIN = 1;
    public static final int TRAIL_INTENSITY_RANGE_MAX = 10_000;
    public static final int TRAIL_INTENSITY_DEFAULT = 20; // First entry (lower than TRAIL_INTENSITY_INCREASE_PER_ENTRY)
    public static final int TRAIL_INTENSITY_INCREASE_PER_ENTRY = 40;
    public static final int TRAIL_INTENSITY_DECAY_PER_STEP = 1;

    // Plant
    public static final int PLANT_MAX_AMOUNT_RANGE_MIN = 12;
    public static final int PLANT_MAX_AMOUNT_RANGE_MAX = 30;
    public static final int PLANT_CURRENT_AMOUNT_RANGE_MIN = 0;
    public static final int PLANT_CURRENT_AMOUNT_RANGE_MAX = PLANT_MAX_AMOUNT_RANGE_MAX;
    public static final double PLANT_REGENERATION_PER_STEP_BASE = 0.30d;
    public static final double PLANT_REGENERATION_PER_STEP_DELTA = 0.08d; // Must not be 0.0d
    public static final int PLANT_CONSUMPTION_PER_ACT = 1;
    public static final int PLANT_ENERGY_GAIN_PER_ACT = 4;

    // Insect
    public static final int INSECT_MAX_AMOUNT_RANGE_MIN = 4;
    public static final int INSECT_MAX_AMOUNT_RANGE_MAX = 10;
    public static final int INSECT_CURRENT_AMOUNT_RANGE_MIN = 0;
    public static final int INSECT_CURRENT_AMOUNT_RANGE_MAX = INSECT_MAX_AMOUNT_RANGE_MAX;
    public static final double INSECT_REGENERATION_PER_STEP_BASE = 0.04d;
    public static final double INSECT_REGENERATION_PER_STEP_DELTA = 0.02d; // Must not be 0.0d
    public static final int INSECT_CONSUMPTION_PER_ACT = 3;
    public static final int INSECT_ENERGY_GAIN_PER_ACT = 20;

    // PetEgg
    public static final int PET_EGG_INCUBATION_REMAINING_RANGE_MIN = 1;
    public static final int PET_EGG_INCUBATION_REMAINING_RANGE_MAX = 20;
    public static final int PET_EGG_INCUBATION_REMAINING_DEFAULT = PET_EGG_INCUBATION_REMAINING_RANGE_MAX;

    // PetTraits
    public static final int PET_TRAITS_MAX_ENERGY_RANGE_MIN = 75;
    public static final int PET_TRAITS_MAX_ENERGY_RANGE_MAX = 150;
    public static final double PET_TRAITS_MOVEMENT_COST_MODIFIER_DEFAULT = 1.0d;
    public static final double PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MIN = 0.5d;
    public static final double PET_TRAITS_MOVEMENT_COST_MODIFIER_RANGE_MAX = 1.5d;
    public static final int PET_TRAITS_REPRODUCTION_MIN_ENERGY_DEFAULT = 60;
    public static final int PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MIN = 30;
    public static final int PET_TRAITS_REPRODUCTION_MIN_ENERGY_RANGE_MAX = 80;
    public static final int PET_TRAITS_REPRODUCTION_COOLDOWN_DEFAULT = 200;
    public static final int PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MIN = 100;
    public static final int PET_TRAITS_REPRODUCTION_COOLDOWN_RANGE_MAX = 300;

    // PetGenome
    public static final double PET_GENOME_MUTATION_CHANCE_PER_TRAIT = 0.08d;
    public static final double PET_GENOME_MUTATION_DELTA = 0.05d;

    // Pet
    public static final int PET_CURRENT_ENERGY_RANGE_MIN = 1;
    public static final int PET_CURRENT_ENERGY_RANGE_MAX = PET_TRAITS_MAX_ENERGY_RANGE_MAX;
    public static final double PET_CURRENT_ENERGY_BIRTH_FACTOR = 0.2d;
    public static final int PET_REPRODUCTION_COOLDOWN_REMAINING_RANGE_MIN = 0;

    // Pet behavior (EtpetsAgentLogic)
    public static final int PET_BEHAVIOR_ENERGY_LOSS_PER_STEP = 1;
    public static final int PET_BEHAVIOR_ENERGY_HUNGRY_THRESHOLD = 70;
    public static final int PET_BEHAVIOR_REPRODUCTION_MIN_AGE = 400;
    public static final int PET_BEHAVIOR_TRAIL_INTENSITY_THRESHOLD = 100;

    // Pet score (EtpetsAgentLogic)
    public static final int SCORE_REPRODUCE_BASE = 80;
    public static final int SCORE_REPRODUCE_PARTNER_BONUS = 5;
    public static final int SCORE_EAT_BASE = 30;
    public static final int SCORE_EAT_HUNGER_BONUS = 6;
    public static final int SCORE_EAT_ENERGY_GAIN_WEIGHT = 2;
    public static final int SCORE_EAT_AMOUNT_WEIGHT = 1;
    public static final int SCORE_MOVE_BASE = 10;
    public static final int SCORE_MOVE_RING2_RESOURCE_BONUS = 8;
    public static final int SCORE_MOVE_RING2_PARTNER_BONUS = 6;
    public static final int SCORE_MOVE_TRAIL_WEAK_BONUS = 2;
    public static final int SCORE_MOVE_COST_PENALTY = 2;
    public static final int SCORE_MOVE_PREVIOUS_COORDINATE_PENALTY = 6;
    public static final int SCORE_MOVE_PREVIOUS_PREVIOUS_COORDINATE_PENALTY = 3;

    /**
     * Private constructor to prevent instantiation.
     */
    private EtpetsBalance() {
    }

}

