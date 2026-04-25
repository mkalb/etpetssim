package de.mkalb.etpetssim.simulations.etpets.model;

public final class EtpetsBalance {

    // TRAIL
    public static final int TRAIL_INTENSITY_MIN = 1;
    public static final int TRAIL_INTENSITY_MAX = 10_000;
    public static final int TRAIL_INTENSITY_INITIAL = 50;
    public static final int TRAIL_INTENSITY_INCREASE_PER_ENTRY = 100;
    public static final int TRAIL_INTENSITY_DECAY_PER_STEP = 1;

    // PLANT
    public static final int PLANT_MAX_AMOUNT_MIN = 5;
    public static final int PLANT_MAX_AMOUNT_MAX = 15;
    public static final int PLANT_CURRENT_AMOUNT_MIN = 0;
    public static final int PLANT_CURRENT_AMOUNT_MAX = PLANT_MAX_AMOUNT_MAX;
    public static final double PLANT_BASE_REGEN_RATE = 0.2d;
    public static final double PLANT_REGEN_RATE_VARIANCE = 0.02d;
    public static final double PLANT_REGEN_VARIANCE_SPREAD_FACTOR = 2.0d;
    public static final int PLANT_CONSUMPTION_PER_ACT = 2;
    public static final int PLANT_ENERGY_GAIN_PER_ACT = 4;

    // INSECT
    public static final int INSECT_MAX_AMOUNT_MIN = 10;
    public static final int INSECT_MAX_AMOUNT_MAX = 30;
    public static final int INSECT_CURRENT_AMOUNT_MIN = 0;
    public static final int INSECT_CURRENT_AMOUNT_MAX = INSECT_MAX_AMOUNT_MAX;
    public static final double INSECT_BASE_REGEN_RATE = 0.05d;
    public static final double INSECT_REGEN_RATE_VARIANCE = 0.02d;
    public static final double INSECT_REGEN_VARIANCE_SPREAD_FACTOR = 2.0d;
    public static final int INSECT_CONSUMPTION_PER_ACT = 4;
    public static final int INSECT_ENERGY_GAIN_PER_ACT = 12;

    // PET_EGG
    public static final int PET_EGG_INCUBATION_REMAINING_MIN = 1;
    public static final int PET_EGG_INCUBATION_REMAINING_MAX = 20;

    // PET defaults and limits
    public static final int PET_MAX_ENERGY_MIN = 75;
    public static final int PET_MAX_ENERGY_MAX = 150;
    public static final int PET_CURRENT_ENERGY_MIN = 1;
    public static final int PET_CURRENT_ENERGY_MAX = PET_MAX_ENERGY_MAX;
    public static final double PET_MOVEMENT_COST_MODIFIER_DEFAULT = 1.0d;
    public static final double PET_MOVEMENT_COST_MODIFIER_MIN = 0.5d;
    public static final double PET_MOVEMENT_COST_MODIFIER_MAX = 1.5d;
    public static final int PET_REPRODUCTION_MIN_ENERGY_DEFAULT = 60;
    public static final int PET_REPRODUCTION_MIN_ENERGY_MIN = 30;
    public static final int PET_REPRODUCTION_MIN_ENERGY_MAX = 80;
    public static final int PET_REPRODUCTION_COOLDOWN_DEFAULT = 200;
    public static final int PET_REPRODUCTION_COOLDOWN_MIN = 100;
    public static final int PET_REPRODUCTION_COOLDOWN_MAX = 300;

    // PET behavior
    public static final int PET_ENERGY_LOSS_PER_STEP = 1;
    public static final int PET_EAT_IF_ADJACENT_ENERGY_THRESHOLD = 70;
    public static final int PET_REPRODUCTION_MIN_AGE = 100;
    public static final int PET_TRAIL_PREFERENCE_THRESHOLD = 500;
    public static final double PET_MUTATION_CHANCE_PER_TRAIT = 0.08d;
    public static final double PET_MUTATION_DELTA = 0.05d;

    // PET action scoring
    public static final int PET_SCORE_REPRODUCE_BASE = 80;
    public static final int PET_SCORE_REPRODUCE_PARTNER_BONUS = 5;
    public static final int PET_SCORE_EAT_BASE = 30;
    public static final int PET_SCORE_EAT_HUNGER_BONUS = 6;
    public static final int PET_SCORE_EAT_ENERGY_GAIN_WEIGHT = 2;
    public static final int PET_SCORE_EAT_AMOUNT_WEIGHT = 1;
    public static final int PET_SCORE_MOVE_BASE = 10;
    public static final int PET_SCORE_MOVE_RING2_RESOURCE_BONUS = 8;
    public static final int PET_SCORE_MOVE_RING2_PARTNER_BONUS = 6;
    public static final int PET_SCORE_MOVE_TRAIL_WEAK_BONUS = 2;
    public static final int PET_SCORE_MOVE_COST_PENALTY = 2;
    public static final int PET_SCORE_MOVE_PREVIOUS_COORDINATE_PENALTY = 6;
    public static final int PET_SCORE_MOVE_PREVIOUS_PREVIOUS_COORDINATE_PENALTY = 3;

    /**
     * Private constructor to prevent instantiation.
     */
    private EtpetsBalance() {
    }

}

