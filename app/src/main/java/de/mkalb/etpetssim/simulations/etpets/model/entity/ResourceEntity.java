package de.mkalb.etpetssim.simulations.etpets.model.entity;

/**
 * Contract for resource entities in the ET Pets simulation.
 */
public sealed interface ResourceEntity extends EtpetsEntity
        permits NoResource, ResourceBase {
}
