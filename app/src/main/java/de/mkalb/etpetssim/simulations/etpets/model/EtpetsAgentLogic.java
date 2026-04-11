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

    private static final int ENERGY_LOSS_PER_STEP = 1;
    private static final int EAT_IF_ADJACENT_ENERGY_THRESHOLD = 80;
    private static final int RESOURCE_SEEKING_ENERGY_THRESHOLD = 60;
    private static final int REPRODUCTION_MIN_AGE = 120;
    private static final int INCUBATION_DURATION = 10;

    // ---- Default initial pet trait values (used by EtpetsSimulationManager) ----
    private static final double TRAIL_INCREASE_PER_ENTRY = 1.0d;
    private static final double TRAIL_PREFERENCE_THRESHOLD = 3.0d;
    private static final double TRAIL_MAX = 100.0d;
    private static final double MUTATION_CHANCE_PER_TRAIT = 0.08d;
    private static final double MUTATION_DELTA = 0.05d;

    private EtpetsAgentLogic() {
    }

    public static void apply(Random random, EtpetsGridModel gridModel, EtpetsIdSequence idSequence, int stepIndex, EtpetsStatistics statistics) {
        int activePetCountChange = 0;
        int eggCountChange = 0;
        int cumulativeDeadPetCountChange = 0;
        GridStructure structure = gridModel.structure();

        // Snapshot of all non-default agent cells, sorted by coordinate for determinism.
        List<GridCell<AgentEntity>> agentCells = gridModel.agentModel()
                                                          .nonDefaultCells()
                                                          .sorted(Comparator.comparing(GridCell::coordinate, EtpetsDeterminism::compareCoordinates))
                                                          .toList();
        // TODO Sort agents by ID (age), shuffle randomly or by position (coordinate)

        for (GridCell<AgentEntity> cell : agentCells) {
            GridCoordinate currentCoordinate = cell.coordinate();
            AgentEntity entity = cell.entity();

            // Check if the entity is still at its original coordinate. It may already have been removed or replaced.
            if (gridModel.agentModel().getEntity(currentCoordinate) != entity) {
                return;
            }

            if (entity instanceof PetEgg egg) {
                egg.decreaseIncubation();
                if (egg.incubationRemaining() <= 0) {
                    Pet newPet = hatchEgg(egg, stepIndex, idSequence);
                    gridModel.agentModel().setEntity(currentCoordinate, newPet);
                    eggCountChange--;
                    activePetCountChange++;
                }
            } else if (entity instanceof Pet pet) {
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
                    cumulativeDeadPetCountChange++;
                    activePetCountChange--;
                    continue; // Stays on grid for exactly 1 visual step.
                }

                var neighborRings = computeNeighborRings(currentCoordinate, structure, gridModel);

                // AppLogger.info(String.format("Processing pet %s at %s with neighbors: firstRing=%s, secondRing=%s",
                //         pet.toDisplayString(), currentCoordinate.toDisplayString(),
                //         neighborRings.firstRing, neighborRings.secondRing
                // ));
                // Step 3 – Eat-if-adjacent (opportunistic eating even when not critically hungry).
                if (pet.currentEnergy() < EAT_IF_ADJACENT_ENERGY_THRESHOLD) {
                    if (tryEat(neighborRings.firstRing().values(), pet, random)) {
                        continue;
                    }
                }

                // Step 4 – Move-to-resource-if-hungry.
                if (pet.currentEnergy() < RESOURCE_SEEKING_ENERGY_THRESHOLD) {
                    if (tryMoveTowardResource(currentCoordinate, neighborRings, pet, gridModel, structure, random)) {
                        continue;
                    }
                }

                // Step 5 – Reproduce-if-possible.
                if (isReproductionEligible(pet, stepIndex)) {
                    if (tryReproduce(currentCoordinate, pet, gridModel, structure, stepIndex, random, idSequence)) {
                        // TODO update eggCountChange if necessary
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

        // Update statistics after processing all agents.
        statistics.updateActivePetCount(activePetCountChange);
        statistics.updateEggCount(eggCountChange);
        statistics.updateCumulativeDeadPetCount(cumulativeDeadPetCountChange);
    }

    // ========== Egg hatching ==========

    private static Pet hatchEgg(PetEgg egg, int stepIndex,
                                EtpetsIdSequence idSequence) {
        PetTraits traits = egg.petGenome().traits();
        return new Pet(
                idSequence.next(),
                egg.parentAId(),
                egg.parentBId(),
                stepIndex,
                traits.maxEnergy(), // TODO calculate birth energy
                0,
                traits);
    }

    // ========== Step 3: Eat-if-adjacent ==========

    private static boolean tryEat(Collection<EtpetsCell> firstRingNeighborCells, Pet pet, Random random) {
        Optional<EtpetsCell> bestConsumableCell = findRandomBestConsumableResourceCell(firstRingNeighborCells, random);

        if (bestConsumableCell.isPresent()
                && (bestConsumableCell.get().resourceEntity() instanceof ResourceBase genericResource)) {
            // AppLogger.info("Consume resource " + genericResource.toDisplayString());
            genericResource.consume();
            int energyGain = genericResource.energyGainPerAct();
            pet.changeEnergy(energyGain);
            return true;
        }

        return false;
    }

    private static Optional<EtpetsCell> findRandomBestConsumableResourceCell(Collection<EtpetsCell> cells,
                                                                             Random random) {
        List<EtpetsCell> bestConsumableCells = collectBestConsumableResourceCells(cells);
        if (!bestConsumableCells.isEmpty()) {
            return Optional.of(bestConsumableCells.get(random.nextInt(bestConsumableCells.size())));
        }

        return Optional.empty();
    }

    private static List<EtpetsCell> collectBestConsumableResourceCells(Collection<EtpetsCell> cells) {
        List<EtpetsCell> bestCells = new ArrayList<>();
        ResourceBase bestResource = null;

        for (EtpetsCell neighborCell : cells) {
            Optional<ResourceBase> resource = toConsumableResource(neighborCell.resourceEntity());
            if (resource.isEmpty()) {
                continue;
            }
            ResourceBase consumableResource = resource.orElseThrow();

            if ((bestResource == null) || isResourceBetter(consumableResource, bestResource)) {
                // New strict best candidate: replace all previous candidates.
                bestCells.clear();
                bestCells.add(neighborCell);
                bestResource = consumableResource;
                continue;
            }

            // Same score as current best: keep all equal candidates for random tie-break.
            if (hasSameResourceScore(consumableResource, bestResource)) {
                bestCells.add(neighborCell);
            }
        }

        return bestCells;
    }

    /**
     * Returns {@code true} if {@code candidate} is a better resource to eat than {@code current}.
     * Ordering: 1) higher energyGainPerAct, 2) higher currentAmount, 3) coordinate order (x asc, y asc).
     */
    private static boolean isResourceBetter(ResourceBase candidate, GridCoordinate candidateCoord,
                                            ResourceBase current, GridCoordinate currentCoord) {
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

    private static boolean isResourceBetter(ResourceBase candidate,
                                            ResourceBase current) {
        int gainCmp = Integer.compare(candidate.energyGainPerAct(), current.energyGainPerAct());
        if (gainCmp != 0) {
            return gainCmp > 0;
        }
        int amtCmp = Double.compare(candidate.currentAmount(), current.currentAmount());
        if (amtCmp != 0) {
            return amtCmp > 0;
        }
        return false;
    }

    private static boolean hasSameResourceScore(ResourceBase first,
                                                ResourceBase second) {
        return (first.energyGainPerAct() == second.energyGainPerAct())
                && (Double.compare(first.currentAmount(), second.currentAmount()) == 0);
    }

    private static Optional<ResourceBase> toConsumableResource(ResourceEntity entity) {
        if ((entity instanceof ResourceBase resource) && resource.canConsume()) {
            return Optional.of(resource);
        }
        return Optional.empty();
    }

    // ========== Step 4: Move-to-resource-if-hungry ==========

    private static boolean tryMoveTowardResource(GridCoordinate coord, NeighborRings neighborRings, Pet pet,
                                                 EtpetsGridModel gridModel, GridStructure structure, Random random) {
        Optional<EtpetsCell> bestConsumableCell = findRandomBestConsumableResourceCell(neighborRings.secondRing().values().stream().map(c -> c.cell).toList(), random);
        if (bestConsumableCell.isPresent()) {
            SecondRingCell secondRingCell = neighborRings.secondRing().get(bestConsumableCell.get().coordinate());
            Set<GridCoordinate> firstRingCells = secondRingCell.reachableViaFirstRing();

            GridCoordinate moveTo = firstRingCells.stream().findFirst().orElseThrow();
            // TODO If multiple reachable first-ring cells, compare trail intensity and otherwise choose randomly.

            movePet(coord, moveTo, pet, gridModel);

            return true;
        }
        return false;
    }

    // ========== Step 5: Reproduce-if-possible ==========

    private static boolean isReproductionEligible(Pet pet, int stepIndex) {
        return !pet.isDead()
                && (pet.ageAtStepIndex(stepIndex) >= REPRODUCTION_MIN_AGE)
                && (pet.currentEnergy() >= pet.traits().reproductionMinEnergy())
                && (pet.reproductionCooldownRemaining() == 0);
    }

    private static boolean tryReproduce(GridCoordinate coordA, Pet petA,
                                        EtpetsGridModel gridModel, GridStructure structure,
                                        int stepIndex, Random random, EtpetsIdSequence idSequence) {
        List<GridCoordinate> adjCoords = getValidNeighborCoordinates(coordA, structure);

        // Collect eligible partner candidates.
        List<GridCoordinate> eligiblePartnerCoords = new ArrayList<>();
        for (GridCoordinate neighborCoord : adjCoords) {
            AgentEntity neighborEntity = gridModel.agentModel().getEntity(neighborCoord);
            if (!(neighborEntity instanceof Pet neighborPet)) {
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
            Pet pA = (Pet) gridModel.agentModel().getEntity(cA);
            Pet pB = (Pet) gridModel.agentModel().getEntity(cB);
            return EtpetsDeterminism.comparePetsForReproduction(pA, cA, pB, cB);
        });

        GridCoordinate partnerCoord = eligiblePartnerCoords.getFirst();
        Pet partnerPet = (Pet) gridModel.agentModel().getEntity(partnerCoord);

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
        PetGenome genome = PetGenome.fromParents(
                new PetGenome(petA.traits()),
                new PetGenome(partnerPet.traits()),
                random,
                MUTATION_CHANCE_PER_TRAIT,
                MUTATION_DELTA
        );

        // Place egg.
        PetEgg egg = new PetEgg(
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
    private static boolean areDirectRelatives(Pet a, Pet b) {
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
            TerrainEntity terrain = gridModel.terrainModel().getEntity(c);
            if (terrain != TerrainConstant.GROUND) {
                continue;
            }
            if (gridModel.resourceModel().getEntity(c).isNotEmpty()) {
                continue;
            }
            if (gridModel.agentModel().getEntity(c).isNotEmpty()) {
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

    private static boolean tryMoveTowardPartner(GridCoordinate coord, Pet pet,
                                                EtpetsGridModel gridModel, GridStructure structure,
                                                int stepIndex) {
        int visionRange = Pet.VISION_RANGE;
        Set<GridCoordinate> visibleCoords = getValidCoordinatesWithinRange(coord, structure, visionRange);

        // Find the nearest eligible, non-relative partner within vision range.
        GridCoordinate targetPartner = null;
        int bestDist = Integer.MAX_VALUE;

        for (GridCoordinate c : visibleCoords) {
            AgentEntity entity = gridModel.agentModel().getEntity(c);
            if (!(entity instanceof Pet candidate)) {
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

    private static void tryExplore(GridCoordinate coord, Pet pet,
                                   EtpetsGridModel gridModel, GridStructure structure) {
        List<GridCoordinate> candidates = getWalkableFreeNeighbors(coord, gridModel, structure);
        if (candidates.isEmpty()) {
            return;
        }

        // Prefer only adjacent trails above threshold (highest intensity first; coord-order as tie-break).
        GridCoordinate bestAdjacentTrail = null;
        double bestAdjacentIntensity = -1.0d;
        for (GridCoordinate c : candidates) {
            TerrainEntity terrain = gridModel.terrainModel().getEntity(c);
            if (terrain instanceof Trail trail) {
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
        int visionRange = Pet.VISION_RANGE;
        Set<GridCoordinate> visibleCoords = getValidCoordinatesWithinRange(coord, structure, visionRange);

        GridCoordinate distantTrail = null;
        double distantTrailIntensity = -1.0d;
        for (GridCoordinate c : visibleCoords) {
            TerrainEntity terrain = gridModel.terrainModel().getEntity(c);
            if (terrain instanceof Trail trail) {
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
                                Pet pet, EtpetsGridModel gridModel) {
        // Movement energy cost (at least 1, scaled by movementCostModifier).
        long roundedMovementCost = Math.round(pet.traits().movementCostModifier());
        int movementCost = Math.max(1, Math.toIntExact(roundedMovementCost));
        pet.changeEnergy(-movementCost);

        // Relocate pet.
        gridModel.agentModel().setEntityToDefault(from);
        gridModel.agentModel().setEntity(to, pet);

        // Update terrain trail at destination.
        TerrainEntity terrain = gridModel.terrainModel().getEntity(to);
        if (terrain == TerrainConstant.GROUND) {
            gridModel.terrainModel().setEntity(to, new Trail(TRAIL_INCREASE_PER_ENTRY));
        } else if (terrain instanceof Trail trail) {
            trail.increase(TRAIL_INCREASE_PER_ENTRY, TRAIL_MAX);
        }
    }

    // ========== Utility helpers ==========

    /**
     * Computes two-ring neighborhood data around a center coordinate.
     *
     * <p><b>First ring:</b> all valid direct neighbors of {@code coordinate}, stored as a
     * {@code coordinate -> cell snapshot} map.
     *
     * <p><b>Second ring:</b> all valid neighbors of walkable first-ring cells
     * (see {@link EtpetsCell#isWalkable()}). Each second-ring entry stores the cell snapshot
     * and the set of first-ring coordinates through which it is reachable.
     * The center coordinate and first-ring coordinates are excluded from the second ring.
     *
     * @param coordinate center coordinate of the expansion.
     * @param structure  grid structure used for neighbor lookup and bounds validation.
     * @param gridModel  source model used to snapshot terrain, resource, and agent entities.
     * @return two-ring neighborhood data with first-ring cell snapshots and second-ring reachability metadata.
     */
    private static NeighborRings computeNeighborRings(GridCoordinate coordinate,
                                                      GridStructure structure,
                                                      EtpetsGridModel gridModel) {
        List<GridCoordinate> firstRingCoordinates = getValidNeighborCoordinates(coordinate, structure);
        Map<GridCoordinate, EtpetsCell> firstRing = new HashMap<>();
        for (GridCoordinate firstRingCoordinate : firstRingCoordinates) {
            EtpetsCell cell = EtpetsCell.of(firstRingCoordinate, gridModel);
            firstRing.put(firstRingCoordinate, cell);
        }

        // Accumulate reachable first-ring sources per second-ring coordinate.
        Map<GridCoordinate, Set<GridCoordinate>> secondRingAccumulator = new HashMap<>();
        for (GridCoordinate firstRingCoordinate : firstRingCoordinates) {
            EtpetsCell cell = firstRing.get(firstRingCoordinate);
            if (cell.isWalkable()) {
                List<GridCoordinate> neighborsOfFirstRing = getValidNeighborCoordinates(firstRingCoordinate, structure);
                for (GridCoordinate secondRingCoordinate : neighborsOfFirstRing) {
                    if (secondRingCoordinate.equals(coordinate) || firstRing.containsKey(secondRingCoordinate)) {
                        continue;
                    }
                    secondRingAccumulator
                            .computeIfAbsent(secondRingCoordinate, k -> new HashSet<>())
                            .add(firstRingCoordinate);
                }
            }
        }

        // Build SecondRingCell records with unmodifiable sets.
        Map<GridCoordinate, SecondRingCell> secondRing = new HashMap<>();
        for (Map.Entry<GridCoordinate, Set<GridCoordinate>> entry : secondRingAccumulator.entrySet()) {
            GridCoordinate secondRingCoordinate = entry.getKey();
            secondRing.put(secondRingCoordinate, new SecondRingCell(
                    EtpetsCell.of(secondRingCoordinate, gridModel),
                    Collections.unmodifiableSet(entry.getValue())
            ));
        }

        return new NeighborRings(Collections.unmodifiableMap(firstRing), Collections.unmodifiableMap(secondRing));
    }

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
        TerrainEntity terrain = gridModel.terrainModel().getEntity(coord);
        return (terrain == TerrainConstant.GROUND) || (terrain instanceof Trail);
    }

    private static boolean isFreeOfAgentAndResource(GridCoordinate coord, EtpetsGridModel gridModel) {
        return gridModel.agentModel().getEntity(coord).isEmpty()
                && gridModel.resourceModel().getEntity(coord).isEmpty();
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

    /**
     * A cell in the second neighborhood ring together with the set of first-ring coordinates
     * through which it is reachable (i.e., via walkable first-ring cells).
     *
     * @param cell                  cell snapshot of the second-ring coordinate.
     * @param reachableViaFirstRing unmodifiable set of first-ring coordinates that are walkable
     *                              and have this cell as a neighbor.
     */
    private record SecondRingCell(EtpetsCell cell,
                                  Set<GridCoordinate> reachableViaFirstRing) {}

    /**
     * Two-ring neighborhood data around a center coordinate.
     *
     * @param firstRing  unmodifiable map from first-ring coordinate to its cell snapshot.
     * @param secondRing unmodifiable map from second-ring coordinate to its {@link SecondRingCell} metadata.
     */
    private record NeighborRings(Map<GridCoordinate, EtpetsCell> firstRing,
                                 Map<GridCoordinate, SecondRingCell> secondRing) {}

}
