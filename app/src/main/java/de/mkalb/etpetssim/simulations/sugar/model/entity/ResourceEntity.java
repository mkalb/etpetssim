package de.mkalb.etpetssim.simulations.sugar.model.entity;

/**
 * Contract for resource entities in the Sugarscape simulation.
 */
public sealed interface ResourceEntity extends SugarEntity
        permits Sugar, NoResource {
}
