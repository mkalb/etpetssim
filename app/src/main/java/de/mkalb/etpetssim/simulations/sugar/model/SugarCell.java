package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

/**
 * Snapshot of all Sugar layers for a single grid coordinate.
 * <p>
 * The record stores both layer entities (resource, agent) and
 * exposes an effective top-most entity via {@link #entity()}.
 *
 * @param coordinate     coordinate of this snapshot
 * @param resourceEntity resource layer entity
 * @param agentEntity    agent layer entity
 */
public record SugarCell(GridCoordinate coordinate,
                        ResourceEntity resourceEntity,
                        AgentEntity agentEntity)
        implements GridCellView<SugarEntity> {

    /**
     * Creates a layered Sugar cell snapshot from the given grid model.
     *
     * @param model      the Sugar grid model providing all layers
     * @param coordinate the coordinate to read
     * @return the composed cell snapshot for the coordinate
     */
    public static SugarCell of(SugarGridModel model, GridCoordinate coordinate) {
        return new SugarCell(coordinate,
                model.resourceModel().getEntity(coordinate),
                model.agentModel().getEntity(coordinate));
    }

    /**
     * Returns the effective entity visible for this cell.
     * <p>
     * Priority order is: agent, then resource.
     *
     * @return the top-most non-empty Sugar entity
     */
    @Override
    public SugarEntity entity() {
        if (agentEntity instanceof Agent) {
            return agentEntity;
        }
        return resourceEntity;
    }

    /**
     * Returns a display string containing the coordinate and both layer
     * entities (resource, agent).
     * <p>
     * Format:
     * {@code <coordinate> <resource-display> <agent-display>}
     *
     * @return a layer-complete display string for this Sugar cell
     */
    @Override
    public String toDisplayString() {
        return String.format("%s %s %s",
                coordinate.toDisplayString(),
                resourceEntity.toDisplayString(),
                agentEntity.toDisplayString());
    }

}

