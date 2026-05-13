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

    // Pet lifecycle (EtpetsAgentLogic)
    public static final int PET_STEP_ENERGY_LOSS = 1;
    public static final int PET_REPRODUCTION_MIN_AGE = 400;

    // Pet move score model
    public static final int PET_MOVE_SCORE_RANGE_MIN = 1;
    public static final int PET_MOVE_SCORE_RANGE_MAX = 40;
    public static final double PET_MOVE_SCORE_BASE = 4.0d;
    public static final double PET_MOVE_SURVIVAL_PRESSURE_EXPONENT = 2.0d;
    public static final double PET_MOVE_RESOURCE_WEIGHT_BASE = 4.0d;
    public static final double PET_MOVE_RESOURCE_WEIGHT_SURVIVAL = 12.0d;
    public static final double PET_MOVE_PARTNER_WEIGHT = 7.5d;
    public static final int PET_MOVE_TRAIL_BONUS_START_INTENSITY = 120;
    public static final double PET_MOVE_TRAIL_BONUS_INTENSITY_SCALE = 2_200.0d;
    public static final double PET_MOVE_TRAIL_BONUS_MAX = 12.0d;
    public static final double PET_MOVE_TRAIL_BONUS_CURVE_K = 3.5d;
    public static final double PET_MOVE_EXPLORATION_WEIGHT = 8.0d;
    public static final double PET_MOVE_EXPLORATION_ENERGY_EXPONENT = 1.4d;
    public static final double PET_MOVE_EXPLORATION_COST_EXPONENT = 1.3d;
    public static final double PET_MOVE_OSCILLATION_PREVIOUS_PENALTY = 8.0d;
    public static final double PET_MOVE_OSCILLATION_PREVIOUS_PREVIOUS_PENALTY = 4.0d;
    public static final int PET_MOVE_LOW_MOBILITY_THRESHOLD = 3;
    public static final double PET_MOVE_LOW_MOBILITY_PENALTY = 5.0d;
    public static final double PET_MOVE_SURVIVAL_PRESSURE_HIGH_THRESHOLD = 0.7d;
    public static final int PET_MOVE_CROWDING_THRESHOLD = 3;
    public static final double PET_MOVE_CROWDING_PENALTY = 3.0d;

    // Pet reproduction score model
    public static final int PET_REPRODUCTION_SCORE_RANGE_MIN = 40;
    public static final int PET_REPRODUCTION_SCORE_RANGE_MAX = 100;
    public static final double PET_REPRODUCTION_SCORE_WEIGHT_AVG_QUALITY = 0.65d;
    public static final double PET_REPRODUCTION_SCORE_WEIGHT_MIN_QUALITY = 0.35d;

    // Pet eat score model
    public static final int PET_EAT_SCORE_RANGE_MIN = 1;
    public static final int PET_EAT_SCORE_RANGE_MAX = 100;
    public static final double PET_EAT_SCORE_HUNGER_WEIGHT = 35.0d;
    public static final double PET_EAT_SCORE_HUNGER_EXPONENT = 1.6d;
    public static final double PET_EAT_SCORE_PANIC_WEIGHT = 40.0d;
    public static final double PET_EAT_SCORE_PANIC_THRESHOLD = 50.0d;
    public static final double PET_EAT_SCORE_PANIC_EXPONENT = 2.4d;
    public static final double PET_EAT_SCORE_GAIN_WEIGHT = 18.0d;
    public static final double PET_EAT_SCORE_GAIN_EXPONENT = 1.0d;
    public static final double PET_EAT_SCORE_WASTE_WEIGHT = 70.0d;
    public static final double PET_EAT_SCORE_WASTE_EXPONENT = 3.0d;
    public static final double PET_EAT_SCORE_AGE_WEIGHT = 6.0d;
    public static final double PET_EAT_SCORE_AGE_DECAY = 120.0d;

    /**
     * Private constructor to prevent instantiation.
     */
    private EtpetsBalance() {
    }

}
