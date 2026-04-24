package de.mkalb.etpetssim.simulations.etpets.model;

public final class EtpetsBalance {

    // Agent defaults
    public static final int PET_MAX_ENERGY_DEFAULT = 100;
    public static final double PET_MOVEMENT_COST_MODIFIER_DEFAULT = 1.0d;
    public static final int PET_REPRODUCTION_MIN_ENERGY_DEFAULT = 70;
    public static final int PET_REPRODUCTION_COOLDOWN_MAX_DEFAULT = 200;

    // Agent trait ranges (genome bounds)
    public static final int PET_TRAIT_MAX_ENERGY_MIN = 60;
    public static final int PET_TRAIT_MAX_ENERGY_MAX = 140;
    public static final double PET_TRAIT_MOVEMENT_COST_MODIFIER_MIN = 0.5d;
    public static final double PET_TRAIT_MOVEMENT_COST_MODIFIER_MAX = 1.5d;
    public static final int PET_TRAIT_REPRODUCTION_MIN_ENERGY_MIN = 50;
    public static final int PET_TRAIT_REPRODUCTION_MIN_ENERGY_MAX = 90;
    public static final int PET_TRAIT_REPRODUCTION_COOLDOWN_MIN = 120;
    public static final int PET_TRAIT_REPRODUCTION_COOLDOWN_MAX = 320;

    // Agent behavior
    public static final int PET_ENERGY_LOSS_PER_STEP = 1;
    public static final int PET_EAT_IF_ADJACENT_ENERGY_THRESHOLD = 80;
    public static final int PET_REPRODUCTION_MIN_AGE = 120;
    public static final int PET_EGG_INCUBATION_DURATION = 10;
    public static final double PET_TRAIL_MAX = 100.0d;
    public static final double PET_TRAIL_INCREASE_PER_ENTRY = 1.0d;
    public static final double PET_TRAIL_DECAY_PER_STEP = 0.02d;
    public static final double PET_TRAIL_PREFERENCE_THRESHOLD = 10.0d;
    public static final double PET_MUTATION_CHANCE_PER_TRAIT = 0.08d;
    public static final double PET_MUTATION_DELTA = 0.05d;

    // Agent action scoring
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

    // Resource initialization ranges and regeneration
    public static final int PLANT_MAX_AMOUNT_MIN = 5;
    public static final int PLANT_MAX_AMOUNT_MAX = 15;
    public static final double PLANT_BASE_REGEN_RATE = 0.2d;
    public static final int INSECT_MAX_AMOUNT_MIN = 10;
    public static final int INSECT_MAX_AMOUNT_MAX = 25;
    public static final double INSECT_BASE_REGEN_RATE = 0.05d;
    public static final double RESOURCE_REGEN_RATE_VARIANCE = 0.02d;
    public static final double RESOURCE_REGEN_VARIANCE_SPREAD_FACTOR = 2.0d;

    // Resource consumption and energy gain per act
    public static final int PLANT_CONSUMPTION_PER_ACT = 2;
    public static final int PLANT_ENERGY_GAIN_PER_ACT = 3;
    public static final int INSECT_CONSUMPTION_PER_ACT = 3;
    public static final int INSECT_ENERGY_GAIN_PER_ACT = 9;

    /**
     * Private constructor to prevent instantiation.
     */
    private EtpetsBalance() {
    }

}

