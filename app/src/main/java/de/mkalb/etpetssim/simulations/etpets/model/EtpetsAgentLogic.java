package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;

import java.util.*;

/**
 * V1 agent logic for ET Pets simulation.
 * <p>
 * Processes all agent entities each step in deterministic coordinate order.
 * Each living pet executes the following priority chain:
 * <ol>
 *   <li>Death-check (remove pets marked dead last step)</li>
 *   <li>Passive energy loss + cooldown decrement</li>
 *   <li>Death from energy depletion</li>
 *   <li>Eat-if-adjacent (if energy &lt; {@link #EAT_IF_ADJACENT_ENERGY_THRESHOLD})</li>
 *   <li>Move-to-resource-if-hungry (if energy &lt; {@link #RESOURCE_SEEKING_ENERGY_THRESHOLD})</li>
 *   <li>Reproduce-if-possible</li>
 *   <li>Move-to-enable-reproduction</li>
 *   <li>Explore/Trail</li>
 * </ol>
 * Eggs are incubated each step and hatch when {@code incubationRemaining} reaches zero.
 */
public final class EtpetsAgentLogic {

    // ---- V1 balancing constants ----

    public static final int DEFAULT_MAX_ENERGY = 100;
    public static final double DEFAULT_MOVEMENT_COST_MODIFIER = 1.0d;
    public static final int DEFAULT_REPRODUCTION_MIN_ENERGY = 70;
    public static final int DEFAULT_REPRODUCTION_COOLDOWN_MAX = 200;
    static final int ENERGY_LOSS_PER_STEP = 1;
    static final int EAT_IF_ADJACENT_ENERGY_THRESHOLD = 80;
    static final int RESOURCE_SEEKING_ENERGY_THRESHOLD = 60;
    static final int REPRODUCTION_MIN_AGE = 120;
    static final int INCUBATION_DURATION = 10;

    // ---- Default initial pet trait values (used by EtpetsSimulationManager) ----
    static final double TRAIL_INCREASE_PER_ENTRY = 1.0d;
    static final double TRAIL_PREFERENCE_THRESHOLD = 3.0d;
    static final double TRAIL_MAX = 100.0d;
    static final double MUTATION_CHANCE_PER_TRAIT = 0.08d;
    static final double MUTATION_DELTA = 0.05d;

    private EtpetsAgentLogic() {
    }

    public static void apply(Random random, EtpetsGridModel gridModel, EtpetsIdSequence idSequence, int stepIndex, EtpetsStatistics statistics) {
        int newDeadCount = 0;
        GridStructure structure = gridModel.structure();

        // Snapshot of all non-default agent cells, sorted by coordinate for determinism.
        List<GridCell<EtpetsAgentEntity>> agentCells = gridModel.agentModel()
                                                                .nonDefaultCells()
                                                                .sorted(Comparator.comparing(GridCell::coordinate, EtpetsDeterminism::compareCoordinates))
                                                                .toList();
        // TODO Sort agents by ID (age), shuffle randomly or by position (coordinate)

        for (GridCell<EtpetsAgentEntity> cell : agentCells) {
            GridCoordinate currentCoordinate = cell.coordinate();
            EtpetsAgentEntity entity = cell.entity();

            // Check if the entity is still at its original coordinate. It may already have been removed.
            if (gridModel.agentModel().getEntity(currentCoordinate) != entity) {
                return;
            }

            if (entity instanceof EtpetsPetEgg egg) {
                egg.decreaseIncubation();
                if (egg.incubationRemaining() <= 0) {
                    hatchEgg(currentCoordinate, egg, stepIndex, gridModel, idSequence);
                }
            } else if (entity instanceof EtpetsPet pet) {
                // Step 1 – Death-check: remove pets already marked dead from the previous step.
                if (pet.isDead()) {
                    gridModel.agentModel().setEntityToDefault(currentCoordinate);
                    continue;
                }

                // Passive energy loss and reproduction cooldown decrement.
                pet.changeEnergy(-ENERGY_LOSS_PER_STEP);
                pet.decrementReproductionCooldown();

                // Step 2 – Death from energy depletion.
                if (pet.currentEnergy() <= 0) {
                    pet.markDead(stepIndex);
                    newDeadCount++;
                    continue; // Stays on grid for exactly 1 visual step.
                }

                // Step 3 – Eat-if-adjacent (opportunistic eating even when not critically hungry).
                if (pet.currentEnergy() < EAT_IF_ADJACENT_ENERGY_THRESHOLD) {
                    if (tryEat(currentCoordinate, pet, gridModel, structure)) {
                        continue;
                    }
                }

                // Step 4 – Move-to-resource-if-hungry.
                if (pet.currentEnergy() < RESOURCE_SEEKING_ENERGY_THRESHOLD) {
                    if (tryMoveTowardResource(currentCoordinate, pet, gridModel, structure)) {
                        continue;
                    }
                }

                // Step 5 – Reproduce-if-possible.
                if (isReproductionEligible(pet, stepIndex)) {
                    if (tryReproduce(currentCoordinate, pet, gridModel, structure, stepIndex, random, idSequence)) {
                        continue;
                    }
                    // Step 6 – Move-to-enable-reproduction (eligible but no valid partner adjacent).
                    if (tryMoveTowardPartner(currentCoordinate, pet, gridModel, structure, stepIndex)) {
                        continue;
                    }
                }

                // Step 7 – Explore / follow trail.
                tryExplore(currentCoordinate, pet, gridModel, structure);
            }
        }

        // TODO update statistics
    }

    // ========== Egg hatching ==========

    private static void hatchEgg(GridCoordinate currentCoordinate, EtpetsPetEgg egg, int stepIndex,
                                 EtpetsGridModel gridModel, EtpetsIdSequence idSequence) {
        EtpetsPetTraits traits = egg.petGenome().traits();
        EtpetsPet newPet = new EtpetsPet(
                idSequence.next(),
                egg.parentAId(),
                egg.parentBId(),
                stepIndex,
                traits.maxEnergy(),
                0,
                traits
        );
        gridModel.agentModel().setEntity(currentCoordinate, newPet);
    }

    // ========== Step 3: Eat-if-adjacent ==========

    private static boolean tryEat(GridCoordinate coord, EtpetsPet pet,
                                  EtpetsGridModel gridModel, GridStructure structure) {
        List<GridCoordinate> validNeighbors = getValidNeighborCoordinates(coord, structure);

        GridCoordinate bestCoord = null;
        EtpetsResourceGeneric bestResource = null;

        for (GridCoordinate neighborCoord : validNeighbors) {
            Optional<EtpetsResourceGeneric> resource = asConsumableResource(gridModel.resourceModel().getEntity(neighborCoord));
            if (resource.isEmpty()) {
                continue;
            }
            EtpetsResourceGeneric consumableResource = resource.orElseThrow();
            if ((bestCoord == null) || isResourceBetter(consumableResource, neighborCoord, bestResource, bestCoord)) {
                bestCoord = neighborCoord;
                bestResource = consumableResource;
            }
        }

        if (bestCoord == null) {
            return false;
        }

        bestResource.consume();
        int energyGain = bestResource.energyGainPerAct();
        pet.changeEnergy(energyGain);
        return true;
    }

    /**
     * Returns {@code true} if {@code candidate} is a better resource to eat than {@code current}.
     * Ordering: 1) higher energyGainPerAct, 2) higher currentAmount, 3) coordinate order (x asc, y asc).
     */
    private static boolean isResourceBetter(EtpetsResourceGeneric candidate, GridCoordinate candidateCoord,
                                            EtpetsResourceGeneric current, GridCoordinate currentCoord) {
        int gainCmp = Integer.compare(candidate.energyGainPerAct(), current.energyGainPerAct());
        if (gainCmp != 0) {
            return gainCmp > 0;
        }
        int amtCmp = Double.compare(candidate.currentAmount(), current.currentAmount());
        if (amtCmp != 0) {
            return amtCmp > 0;
        }
        return EtpetsDeterminism.compareCoordinates(candidateCoord, currentCoord) < 0;
    }

    private static Optional<EtpetsResourceGeneric> asConsumableResource(EtpetsResourceEntity entity) {
        if ((entity instanceof EtpetsResourceGeneric resource) && resource.canConsume()) {
            return Optional.of(resource);
        }
        return Optional.empty();
    }

    // ========== Step 4: Move-to-resource-if-hungry ==========

    private static boolean tryMoveTowardResource(GridCoordinate coord, EtpetsPet pet,
                                                 EtpetsGridModel gridModel, GridStructure structure) {
        int visionRange = EtpetsPet.VISION_RANGE;
        Set<GridCoordinate> visibleCoords = getValidCoordinatesWithinRange(coord, structure, visionRange);

        // Find the best consumable resource within vision range.
        GridCoordinate targetResource = null;
        EtpetsResourceGeneric targetResourceEntity = null;
        for (GridCoordinate c : visibleCoords) {
            Optional<EtpetsResourceGeneric> resource = asConsumableResource(gridModel.resourceModel().getEntity(c));
            if (resource.isEmpty()) {
                continue;
            }
            EtpetsResourceGeneric consumableResource = resource.orElseThrow();
            if ((targetResource == null) || isResourceBetter(consumableResource, c, targetResourceEntity, targetResource)) {
                targetResource = c;
                targetResourceEntity = consumableResource;
            }
        }
        if (targetResource == null) {
            return false;
        }

        List<GridCoordinate> candidates = getWalkableFreeNeighbors(coord, gridModel, structure);
        if (candidates.isEmpty()) {
            return false;
        }

        GridCoordinate finalTarget = targetResource;

        // Prefer candidates directly adjacent to the target resource.
        List<GridCoordinate> adjacentToTarget = new ArrayList<>();
        for (GridCoordinate c : candidates) {
            if (isAdjacent(c, finalTarget, structure)) {
                adjacentToTarget.add(c);
            }
        }

        GridCoordinate moveTo;
        if (!adjacentToTarget.isEmpty()) {
            adjacentToTarget.sort(EtpetsDeterminism::compareCoordinates);
            moveTo = adjacentToTarget.getFirst();
        } else {
            // Move toward target: pick the candidate with the shortest BFS distance.
            candidates.sort((a, b) -> {
                int distA = bfsDistance(a, finalTarget, structure, visionRange + 2);
                int distB = bfsDistance(b, finalTarget, structure, visionRange + 2);
                int cmp = Integer.compare(distA, distB);
                if (cmp != 0) {
                    return cmp;
                }
                return EtpetsDeterminism.compareCoordinates(a, b);
            });
            moveTo = candidates.getFirst();
        }

        movePet(coord, moveTo, pet, gridModel);
        return true;
    }

    // ========== Step 5: Reproduce-if-possible ==========

    private static boolean isReproductionEligible(EtpetsPet pet, int stepIndex) {
        return !pet.isDead()
                && (pet.ageAtStepIndex(stepIndex) >= REPRODUCTION_MIN_AGE)
                && (pet.currentEnergy() >= pet.traits().reproductionMinEnergy())
                && (pet.reproductionCooldownRemaining() == 0);
    }

    private static boolean tryReproduce(GridCoordinate coordA, EtpetsPet petA,
                                        EtpetsGridModel gridModel, GridStructure structure,
                                        int stepIndex, Random random, EtpetsIdSequence idSequence) {
        List<GridCoordinate> adjCoords = getValidNeighborCoordinates(coordA, structure);

        // Collect eligible partner candidates.
        List<GridCoordinate> eligiblePartnerCoords = new ArrayList<>();
        for (GridCoordinate neighborCoord : adjCoords) {
            EtpetsAgentEntity neighborEntity = gridModel.agentModel().getEntity(neighborCoord);
            if (!(neighborEntity instanceof EtpetsPet neighborPet)) {
                continue;
            }
            if (neighborPet.isDead()) {
                continue;
            }
            if (!isReproductionEligible(neighborPet, stepIndex)) {
                continue;
            }
            if (areDirectRelatives(petA, neighborPet)) {
                continue;
            }
            if (findEggPlacementCell(coordA, neighborCoord, gridModel, structure).isEmpty()) {
                continue;
            }
            eligiblePartnerCoords.add(neighborCoord);
        }

        if (eligiblePartnerCoords.isEmpty()) {
            return false;
        }

        // Select the best partner using deterministic scoring.
        eligiblePartnerCoords.sort((cA, cB) -> {
            EtpetsPet pA = (EtpetsPet) gridModel.agentModel().getEntity(cA);
            EtpetsPet pB = (EtpetsPet) gridModel.agentModel().getEntity(cB);
            return EtpetsDeterminism.comparePetsForReproduction(pA, cA, pB, cB);
        });

        GridCoordinate partnerCoord = eligiblePartnerCoords.getFirst();
        EtpetsPet partnerPet = (EtpetsPet) gridModel.agentModel().getEntity(partnerCoord);

        Optional<GridCoordinate> eggCoord = findEggPlacementCell(coordA, partnerCoord, gridModel, structure);
        if (eggCoord.isEmpty()) {
            return false;
        }

        // Normalize parent IDs: parentAId < parentBId.
        long idA = petA.petId();
        long idB = partnerPet.petId();
        long parentAId = Math.min(idA, idB);
        long parentBId = Math.max(idA, idB);

        // Inherit genome via trait averaging plus mutation.
        EtpetsPetGenome genome = EtpetsPetGenome.fromParents(
                new EtpetsPetGenome(petA.traits()),
                new EtpetsPetGenome(partnerPet.traits()),
                random,
                MUTATION_CHANCE_PER_TRAIT,
                MUTATION_DELTA
        );

        // Place egg.
        EtpetsPetEgg egg = new EtpetsPetEgg(
                idSequence.next(),
                parentAId,
                parentBId,
                genome,
                stepIndex,
                INCUBATION_DURATION
        );
        gridModel.agentModel().setEntity(eggCoord.orElseThrow(), egg);

        // Reset reproduction cooldown for both parents.
        petA.setReproductionCooldownRemaining(petA.traits().reproductionCooldownMax());
        partnerPet.setReproductionCooldownRemaining(partnerPet.traits().reproductionCooldownMax());

        return true;
    }

    /**
     * Returns {@code true} if pets {@code a} and {@code b} share any non-null ID
     * among their own IDs and their parent IDs (direct relatives check).
     */
    private static boolean areDirectRelatives(EtpetsPet a, EtpetsPet b) {
        Set<Long> idsA = new HashSet<>();
        idsA.add(a.petId());
        if (a.parentAId() != null) {
            idsA.add(a.parentAId());
        }
        if (a.parentBId() != null) {
            idsA.add(a.parentBId());
        }
        if (idsA.contains(b.petId())) {
            return true;
        }
        if ((b.parentAId() != null) && idsA.contains(b.parentAId())) {
            return true;
        }
        return (b.parentBId() != null) && idsA.contains(b.parentBId());
    }

    /**
     * Finds the best valid egg placement cell shared by both parent coordinates.
     * Requirements: terrain = GROUND (not Trail), no resource, no agent.
     * Returns the coordinate with the lowest (x, y) sort order, or {@code null} if none found.
     */
    private static Optional<GridCoordinate> findEggPlacementCell(GridCoordinate coordA, GridCoordinate coordB,
                                                                 EtpetsGridModel gridModel, GridStructure structure) {
        List<GridCoordinate> neighborsA = getValidNeighborCoordinates(coordA, structure);
        Set<GridCoordinate> neighborsASet = new HashSet<>(neighborsA);
        List<GridCoordinate> neighborsB = getValidNeighborCoordinates(coordB, structure);

        List<GridCoordinate> candidates = new ArrayList<>();
        for (GridCoordinate c : neighborsB) {
            if (!neighborsASet.contains(c)) {
                continue;
            }
            if (c.equals(coordA) || c.equals(coordB)) {
                continue;
            }
            // Spec: egg placement cell MUST contain Ground, not Trail.
            EtpetsTerrainEntity terrain = gridModel.terrainModel().getEntity(c);
            if (terrain != EtpetsTerrainConstant.GROUND) {
                continue;
            }
            if (!gridModel.resourceModel().getEntity(c).isNone()) {
                continue;
            }
            if (!gridModel.agentModel().getEntity(c).isNone()) {
                continue;
            }
            candidates.add(c);
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        candidates.sort(EtpetsDeterminism::compareCoordinates);
        return Optional.of(candidates.getFirst());
    }

    // ========== Step 6: Move-to-enable-reproduction ==========

    private static boolean tryMoveTowardPartner(GridCoordinate coord, EtpetsPet pet,
                                                EtpetsGridModel gridModel, GridStructure structure,
                                                int stepIndex) {
        int visionRange = EtpetsPet.VISION_RANGE;
        Set<GridCoordinate> visibleCoords = getValidCoordinatesWithinRange(coord, structure, visionRange);

        // Find the nearest eligible, non-relative partner within vision range.
        GridCoordinate targetPartner = null;
        int bestDist = Integer.MAX_VALUE;

        for (GridCoordinate c : visibleCoords) {
            EtpetsAgentEntity entity = gridModel.agentModel().getEntity(c);
            if (!(entity instanceof EtpetsPet candidate)) {
                continue;
            }
            if (candidate.isDead()) {
                continue;
            }
            if (!isReproductionEligible(candidate, stepIndex)) {
                continue;
            }
            if (areDirectRelatives(pet, candidate)) {
                continue;
            }
            int dist = bfsDistance(coord, c, structure, visionRange + 1);
            if ((dist < bestDist)
                    || ((dist == bestDist) && ((targetPartner == null)
                    || (EtpetsDeterminism.compareCoordinates(c, targetPartner) < 0)))) {
                bestDist = dist;
                targetPartner = c;
            }
        }

        if (targetPartner == null) {
            return false;
        }

        List<GridCoordinate> candidates = getWalkableFreeNeighbors(coord, gridModel, structure);
        if (candidates.isEmpty()) {
            return false;
        }

        GridCoordinate finalTarget = targetPartner;
        candidates.sort((a, b) -> {
            int distA = bfsDistance(a, finalTarget, structure, visionRange + 2);
            int distB = bfsDistance(b, finalTarget, structure, visionRange + 2);
            int cmp = Integer.compare(distA, distB);
            if (cmp != 0) {
                return cmp;
            }
            return EtpetsDeterminism.compareCoordinates(a, b);
        });

        movePet(coord, candidates.getFirst(), pet, gridModel);
        return true;
    }

    // ========== Step 7: Explore / Trail ==========

    private static void tryExplore(GridCoordinate coord, EtpetsPet pet,
                                   EtpetsGridModel gridModel, GridStructure structure) {
        List<GridCoordinate> candidates = getWalkableFreeNeighbors(coord, gridModel, structure);
        if (candidates.isEmpty()) {
            return;
        }

        // Prefer only adjacent trails above threshold (highest intensity first; coord-order as tie-break).
        GridCoordinate bestAdjacentTrail = null;
        double bestAdjacentIntensity = -1.0d;
        for (GridCoordinate c : candidates) {
            EtpetsTerrainEntity terrain = gridModel.terrainModel().getEntity(c);
            if (terrain instanceof EtpetsTerrainTrail trail) {
                double intensity = trail.intensity();
                if (intensity <= TRAIL_PREFERENCE_THRESHOLD) {
                    continue;
                }
                if ((intensity > bestAdjacentIntensity)
                        || ((Double.compare(intensity, bestAdjacentIntensity) == 0)
                        && (EtpetsDeterminism.compareCoordinates(c, bestAdjacentTrail) < 0))) {
                    bestAdjacentIntensity = intensity;
                    bestAdjacentTrail = c;
                }
            }
        }

        if (bestAdjacentTrail != null) {
            movePet(coord, bestAdjacentTrail, pet, gridModel);
            return;
        }

        // No adjacent trail above threshold - look within vision range for a preferred trail.
        int visionRange = EtpetsPet.VISION_RANGE;
        Set<GridCoordinate> visibleCoords = getValidCoordinatesWithinRange(coord, structure, visionRange);

        GridCoordinate distantTrail = null;
        double distantTrailIntensity = -1.0d;
        for (GridCoordinate c : visibleCoords) {
            EtpetsTerrainEntity terrain = gridModel.terrainModel().getEntity(c);
            if (terrain instanceof EtpetsTerrainTrail trail) {
                double intensity = trail.intensity();
                if (intensity <= TRAIL_PREFERENCE_THRESHOLD) {
                    continue;
                }
                if ((intensity > distantTrailIntensity)
                        || ((Double.compare(intensity, distantTrailIntensity) == 0)
                        && (EtpetsDeterminism.compareCoordinates(c, distantTrail) < 0))) {
                    distantTrailIntensity = intensity;
                    distantTrail = c;
                }
            }
        }

        GridCoordinate moveTo;
        if (distantTrail != null) {
            GridCoordinate finalTrail = distantTrail;
            candidates.sort((a, b) -> {
                int distA = bfsDistance(a, finalTrail, structure, visionRange + 2);
                int distB = bfsDistance(b, finalTrail, structure, visionRange + 2);
                int cmp = Integer.compare(distA, distB);
                if (cmp != 0) {
                    return cmp;
                }
                return EtpetsDeterminism.compareCoordinates(a, b);
            });
            moveTo = candidates.getFirst();
        } else {
            // No trail anywhere: pick any walkable free cell in stable coordinate order.
            candidates.sort(EtpetsDeterminism::compareCoordinates);
            moveTo = candidates.getFirst();
        }

        movePet(coord, moveTo, pet, gridModel);
    }

    // ========== Movement ==========

    /**
     * Moves {@code pet} from {@code from} to {@code to}, applying movement energy cost
     * and updating the terrain trail at the destination.
     */
    private static void movePet(GridCoordinate from, GridCoordinate to,
                                EtpetsPet pet, EtpetsGridModel gridModel) {
        // Movement energy cost (at least 1, scaled by movementCostModifier).
        long roundedMovementCost = Math.round(pet.traits().movementCostModifier());
        int movementCost = Math.max(1, Math.toIntExact(roundedMovementCost));
        pet.changeEnergy(-movementCost);

        // Relocate pet.
        gridModel.agentModel().setEntityToDefault(from);
        gridModel.agentModel().setEntity(to, pet);

        // Update terrain trail at destination.
        EtpetsTerrainEntity terrain = gridModel.terrainModel().getEntity(to);
        if (terrain == EtpetsTerrainConstant.GROUND) {
            gridModel.terrainModel().setEntity(to, new EtpetsTerrainTrail(TRAIL_INCREASE_PER_ENTRY));
        } else if (terrain instanceof EtpetsTerrainTrail trail) {
            trail.increase(TRAIL_INCREASE_PER_ENTRY, TRAIL_MAX);
        }
    }

    // ========== Utility helpers ==========

    /** Returns all valid (in-bounds) neighbor coordinates of {@code coord}. */
    private static List<GridCoordinate> getValidNeighborCoordinates(GridCoordinate coord,
                                                                    GridStructure structure) {
        Collection<EdgeBehaviorResult> results =
                CellNeighborhoods.neighborEdgeResults(coord, NeighborhoodMode.EDGES_ONLY, structure);
        List<GridCoordinate> valid = new ArrayList<>(results.size());
        for (EdgeBehaviorResult r : results) {
            if (r.action() == EdgeBehaviorAction.VALID) {
                valid.add(r.mapped());
            }
        }
        return valid;
    }

    /** Returns all walkable (Ground or Trail), resource-free, agent-free neighbor coordinates. */
    private static List<GridCoordinate> getWalkableFreeNeighbors(GridCoordinate coord,
                                                                 EtpetsGridModel gridModel,
                                                                 GridStructure structure) {
        List<GridCoordinate> neighbors = getValidNeighborCoordinates(coord, structure);
        List<GridCoordinate> result = new ArrayList<>();
        for (GridCoordinate c : neighbors) {
            if (isWalkable(c, gridModel) && isFreeOfAgentAndResource(c, gridModel)) {
                result.add(c);
            }
        }
        return result;
    }

    private static boolean isWalkable(GridCoordinate coord, EtpetsGridModel gridModel) {
        EtpetsTerrainEntity terrain = gridModel.terrainModel().getEntity(coord);
        return (terrain == EtpetsTerrainConstant.GROUND) || (terrain instanceof EtpetsTerrainTrail);
    }

    private static boolean isFreeOfAgentAndResource(GridCoordinate coord, EtpetsGridModel gridModel) {
        return gridModel.agentModel().getEntity(coord).isNone()
                && gridModel.resourceModel().getEntity(coord).isNone();
    }

    /** Returns {@code true} if coordinate {@code a} has {@code b} as a valid direct neighbor. */
    private static boolean isAdjacent(GridCoordinate a, GridCoordinate b, GridStructure structure) {
        Collection<EdgeBehaviorResult> results =
                CellNeighborhoods.neighborEdgeResults(a, NeighborhoodMode.EDGES_ONLY, structure);
        for (EdgeBehaviorResult r : results) {
            if ((r.action() == EdgeBehaviorAction.VALID) && r.mapped().equals(b)) {
                return true;
            }
        }
        return false;
    }

    /** Returns all valid (in-bounds) grid coordinates within {@code range} steps of {@code coord}. */
    private static Set<GridCoordinate> getValidCoordinatesWithinRange(GridCoordinate coord,
                                                                      GridStructure structure,
                                                                      int range) {
        Set<GridCoordinate> all = CellNeighborhoods.coordinatesOfNeighbors(
                coord, NeighborhoodMode.EDGES_ONLY, structure.cellShape(), range);
        Set<GridCoordinate> valid = new HashSet<>();
        for (GridCoordinate c : all) {
            if (structure.isCoordinateValid(c)) {
                valid.add(c);
            }
        }
        return valid;
    }

    /**
     * BFS distance from {@code from} to {@code to} within the grid (ignoring terrain/agent obstacles).
     * Returns {@code maxDistance + 1} if {@code to} is not reachable within {@code maxDistance} steps.
     */
    private static int bfsDistance(GridCoordinate from, GridCoordinate to,
                                   GridStructure structure, int maxDistance) {
        if (from.equals(to)) {
            return 0;
        }
        if (maxDistance <= 0) {
            return maxDistance + 1;
        }
        Set<GridCoordinate> visited = new HashSet<>();
        Queue<GridCoordinate> queue = new ArrayDeque<>();
        visited.add(from);
        queue.add(from);
        int dist = 0;
        while (!queue.isEmpty() && (dist < maxDistance)) {
            int levelSize = queue.size();
            dist++;
            for (int i = 0; i < levelSize; i++) {
                GridCoordinate current = queue.remove();
                for (EdgeBehaviorResult r : CellNeighborhoods.neighborEdgeResults(
                        current, NeighborhoodMode.EDGES_ONLY, structure)) {
                    if (r.action() != EdgeBehaviorAction.VALID) {
                        continue;
                    }
                    GridCoordinate next = r.mapped();
                    if (next.equals(to)) {
                        return dist;
                    }
                    if (visited.add(next)) {
                        queue.add(next);
                    }
                }
            }
        }
        return maxDistance + 1;
    }

}
