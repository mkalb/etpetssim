package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.AgentOrderingStrategies;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsAgentLogic {

    // Debug/log formatting
    private static final int DISPLAY_STRING_CAPACITY_HINT = 80;

    private EtpetsAgentLogic() {
    }

    public static void apply(Random random, EtpetsGridModel gridModel, EtpetsIdSequence idSequence, int stepIndex, EtpetsStatistics statistics) {
        int activePetCountChange = 0;
        int eggCountChange = 0;
        int cumulativeDeadPetCountChange = 0;
        GridStructure structure = gridModel.structure();
        WritableGridModel<AgentEntity> agentModel = gridModel.agentModel();

        // Snapshot of all non-default agent cells, sorted by position to ensure deterministic processing order.
        List<GridCell<AgentEntity>> agentCells = agentModel.nonDefaultCells()
                                                           .sorted(AgentOrderingStrategies.byPosition())
                                                           .toList();

        for (GridCell<AgentEntity> cell : agentCells) {
            GridCoordinate currentCoordinate = cell.coordinate();
            AgentEntity entity = cell.entity();

            // Check if the entity is still at its original coordinate. It may already have been removed or replaced.
            if (agentModel.getEntity(currentCoordinate) != entity) {
                AppLogger.warnf("Entity at %s changed during processing. Expected: %s, actual: %s. Skipping.",
                        currentCoordinate.toDisplayString(), entity.toDisplayString(),
                        agentModel.getEntity(currentCoordinate).toDisplayString());
                continue;
            }

            if (entity instanceof PetEgg egg) {
                egg.decrementIncubationRemaining();
                if (egg.incubationRemaining() < EtpetsBalance.PET_EGG_INCUBATION_REMAINING_RANGE_MIN) {
                    Pet newPet = hatchEgg(egg, stepIndex, idSequence);
                    agentModel.setEntity(currentCoordinate, newPet);
                    updateTrailAtCoordinate(currentCoordinate, gridModel);
                    eggCountChange--;
                    activePetCountChange++;
                    AppLogger.infof("Egg %s hatched into Pet %s at %s",
                            egg.toDisplayString(),
                            newPet.toDisplayString(),
                            currentCoordinate.toDisplayString());
                }
            } else if (entity instanceof Pet pet) {
                // Death-check: remove pets already marked dead from the previous step.
                if (pet.isDead()) {
                    agentModel.setEntityToDefault(currentCoordinate);
                    continue;
                }

                // Passive energy loss and reproduction cooldown decrement.
                pet.changeEnergy(-EtpetsBalance.PET_ENERGY_LOSS_PER_STEP);
                pet.decrementReproductionCooldownRemaining();

                // Death from energy depletion.
                if (pet.currentEnergy() < EtpetsBalance.PET_CURRENT_ENERGY_RANGE_MIN) {
                    pet.die();
                    cumulativeDeadPetCountChange++;
                    activePetCountChange--;
                    AppLogger.infof("Pet %s died at %s from energy depletion.",
                            pet.toDisplayString(),
                            currentCoordinate.toDisplayString());
                    continue; // Stays on grid for exactly 1 visual step.
                }

                List<ActionCandidate> actionCandidates = collectActionCandidates(
                        currentCoordinate,
                        pet,
                        gridModel,
                        structure,
                        stepIndex);

                ActionCandidate selectedAction = pickBestCandidate(actionCandidates, random);

                AppLogger.infof("Pet %s at %s has %d action candidates: %s. Selected: %s",
                        pet.toDisplayString(),
                        currentCoordinate.toDisplayString(),
                        actionCandidates.size(),
                        toDisplayString(actionCandidates),
                        toDisplayString(selectedAction));

                ActionEffect effect = executeActionCandidate(
                        selectedAction,
                        currentCoordinate,
                        pet,
                        random,
                        gridModel,
                        stepIndex,
                        idSequence);
                eggCountChange += effect.eggCountDelta();
            }
        }

        // Update statistics after processing all agents.
        statistics.updateActivePetCount(activePetCountChange);
        statistics.updateEggCount(eggCountChange);
        statistics.updateCumulativeDeadPetCount(cumulativeDeadPetCountChange);
    }

    private static List<ActionCandidate> collectActionCandidates(GridCoordinate currentCoordinate,
                                                                 Pet pet,
                                                                 EtpetsGridModel gridModel,
                                                                 GridStructure structure,
                                                                 int stepIndex) {
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>>> neighborhoodCellsByRing =
                CellNeighborhoods.cellsByRadiusRings(currentCoordinate,
                        NeighborhoodMode.EDGES_ONLY,
                        structure,
                        2,
                        c -> EtpetsCell.of(c, gridModel));

        // Flat map used by egg-placement search (needs all rings merged).
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> snapshotCellsByCoordinate = new TreeMap<>();
        for (SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ringCells : neighborhoodCellsByRing.values()) {
            snapshotCellsByCoordinate.putAll(ringCells);
        }

        boolean canSelfReproduce = isReproductionEligible(pet, stepIndex);
        boolean isHungry = pet.currentEnergy() < EtpetsBalance.PET_EAT_IF_ADJACENT_ENERGY_THRESHOLD;

        // Pass 1: Ring 2 → determine which ring-1 cells gain look-ahead score bonuses.
        Set<GridCoordinate> ring1HasResourceBonus = new HashSet<>();
        Set<GridCoordinate> ring1HasPartnerBonus = new HashSet<>();
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ring2Cells =
                neighborhoodCellsByRing.getOrDefault(2, Collections.emptySortedMap());
        for (RadiusRingCell<EtpetsCell> ring2Cell : ring2Cells.values()) {
            EtpetsCell cell = ring2Cell.cell();
            if (toConsumableResource(cell.resourceEntity()).isPresent()) {
                ring1HasResourceBonus.addAll(ring2Cell.reachedFromPreviousRing());
            }
            if (canSelfReproduce && isValidReproductionPartner(pet, cell, stepIndex)) {
                ring1HasPartnerBonus.addAll(ring2Cell.reachedFromPreviousRing());
            }
        }

        List<ActionCandidate> candidates = new ArrayList<>();

        // Pass 2: Ring 1 → score and create MOVE candidates;
        //         collect EAT/REPRODUCE data for ring 0.
        List<EtpetsCell> ring0Consumables = new ArrayList<>();
        List<ReproductionOption> ring0ReproductionOptions = new ArrayList<>();
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ring1Cells =
                neighborhoodCellsByRing.getOrDefault(1, Collections.emptySortedMap());
        for (RadiusRingCell<EtpetsCell> ring1Cell : ring1Cells.values()) {
            EtpetsCell cell = ring1Cell.cell();
            GridCoordinate coord = ring1Cell.coordinate();

            // EAT candidates adjacent to the current position (ring 0).
            if (toConsumableResource(cell.resourceEntity()).isPresent()) {
                ring0Consumables.add(cell);
            }

            // REPRODUCE candidates adjacent to the current position (ring 0).
            if (canSelfReproduce && isValidReproductionPartner(pet, cell, stepIndex)) {
                findEggPlacementCellFromSnapshots(currentCoordinate, coord, structure, snapshotCellsByCoordinate)
                        .ifPresent(eggCoord -> ring0ReproductionOptions.add(new ReproductionOption(coord, eggCoord)));
            }

            // MOVE candidates: only walkable ring-1 cells.
            if (cell.isWalkable()) {
                int moveScore = EtpetsBalance.SCORE_MOVE_BASE - EtpetsBalance.SCORE_MOVE_COST_PENALTY;
                if ((cell.terrainEntity() instanceof Trail trail) && (trail.intensity() > EtpetsBalance.PET_TRAIL_PREFERENCE_THRESHOLD)) {
                    moveScore += EtpetsBalance.SCORE_MOVE_TRAIL_WEAK_BONUS;
                }
                if (ring1HasResourceBonus.contains(coord)) {
                    moveScore += EtpetsBalance.SCORE_MOVE_RING2_RESOURCE_BONUS;
                }
                if (ring1HasPartnerBonus.contains(coord)) {
                    moveScore += EtpetsBalance.SCORE_MOVE_RING2_PARTNER_BONUS;
                }
                if (coord.equals(pet.previousCoordinate())) {
                    moveScore -= EtpetsBalance.SCORE_MOVE_PREVIOUS_COORDINATE_PENALTY;
                } else if (coord.equals(pet.previousPreviousCoordinate())) {
                    moveScore -= EtpetsBalance.SCORE_MOVE_PREVIOUS_PREVIOUS_COORDINATE_PENALTY;
                }
                candidates.add(new ActionCandidate(ActionType.MOVE, moveScore, coord, coord, null));
            }
        }

        // Pass 3: Ring 0 (current position) → score and create WAIT / EAT / REPRODUCE candidates.

        // WAIT
        candidates.add(new ActionCandidate(ActionType.WAIT, 0, currentCoordinate, currentCoordinate, null));

        // REPRODUCE
        for (ReproductionOption option : ring0ReproductionOptions) {
            int reproduceScore = EtpetsBalance.SCORE_REPRODUCE_BASE + EtpetsBalance.SCORE_REPRODUCE_PARTNER_BONUS;
            candidates.add(new ActionCandidate(ActionType.REPRODUCE, reproduceScore, currentCoordinate,
                    option.partnerCoordinate(), option.eggCoordinate()));
        }

        // EAT
        for (EtpetsCell resourceCell : ring0Consumables) {
            ResourceEntity resourceEntity = resourceCell.resourceEntity();
            if (!(resourceEntity instanceof ResourceBase resource) || !resource.canConsume()) {
                continue;
            }
            @SuppressWarnings("NumericCastThatLosesPrecision")
            int resourceAmount = Math.min(Integer.MAX_VALUE, (int) resource.currentAmount());
            int hungerBonus = isHungry ? EtpetsBalance.SCORE_EAT_HUNGER_BONUS : 0;
            int eatScore = EtpetsBalance.SCORE_EAT_BASE + hungerBonus
                    + (resource.energyGainPerAct() * EtpetsBalance.SCORE_EAT_ENERGY_GAIN_WEIGHT)
                    + (resourceAmount * EtpetsBalance.SCORE_EAT_AMOUNT_WEIGHT);
            candidates.add(new ActionCandidate(ActionType.EAT, eatScore, currentCoordinate,
                    resourceCell.coordinate(), null));
        }

        return candidates;
    }

    /**
     * Returns {@code true} if {@code partnerCell} contains a pet that is eligible
     * to reproduce with {@code pet}: alive, reproduction-eligible, and not a direct relative.
     * <p>
     * Egg-placement availability is intentionally <em>not</em> checked here so that
     * this method can be used as a lightweight look-ahead filter (ring-2 partner bonus)
     * as well as a full ring-1 partner check (egg placement is verified separately).
     */
    private static boolean isValidReproductionPartner(Pet pet, EtpetsCell partnerCell, int stepIndex) {
        if (!(partnerCell.agentEntity() instanceof Pet partnerPet)) {
            return false;
        }
        return !partnerPet.isDead()
                && isReproductionEligible(partnerPet, stepIndex)
                && !areDirectRelatives(pet, partnerPet);
    }

    private static ActionCandidate pickBestCandidate(Collection<ActionCandidate> candidates, Random random) {
        int maxScore = candidates.stream()
                                 .mapToInt(ActionCandidate::score)
                                 .max()
                                 .orElseThrow();
        List<ActionCandidate> topCandidates = candidates.stream()
                                                        .filter(candidate -> candidate.score() == maxScore)
                                                        .toList();

        if (topCandidates.size() == 1) {
            // Use the only top candidate
            return topCandidates.getFirst();
        }

        // Select randomly among tied top candidates.
        return topCandidates.get(random.nextInt(topCandidates.size()));
    }

    private static ActionEffect executeActionCandidate(
            ActionCandidate candidate,
            GridCoordinate currentCoordinate,
            Pet pet,
            Random random,
            EtpetsGridModel gridModel,
            int stepIndex,
            EtpetsIdSequence idSequence) {
        return switch (candidate.type()) {
            case WAIT -> ActionEffect.none();
            case MOVE -> executeMoveAction(currentCoordinate, candidate.moveTarget(), pet, gridModel);
            case EAT -> executeEatAction(candidate, gridModel, pet);
            case REPRODUCE -> executeReproduceAction(candidate, pet, random, gridModel, stepIndex, idSequence);
        };
    }

    private static ActionEffect executeMoveAction(GridCoordinate from, GridCoordinate to,
                                                  Pet pet, EtpetsGridModel gridModel) {
        // Movement energy cost (at least 1, scaled by movementCostModifier).
        long roundedMovementCost = Math.round(pet.traits().movementCostModifier());
        int movementCost = Math.max(1, Math.toIntExact(roundedMovementCost));
        pet.changeEnergy(-movementCost);

        // Relocate pet.
        pet.recordMoveFrom(from);
        gridModel.agentModel().setEntityToDefault(from);
        gridModel.agentModel().setEntity(to, pet);

        updateTrailAtCoordinate(to, gridModel);

        return ActionEffect.none();
    }

    private static void updateTrailAtCoordinate(GridCoordinate coordinate, EtpetsGridModel gridModel) {
        // Add a fresh trail on ground, otherwise reinforce existing trail intensity.
        TerrainEntity terrain = gridModel.terrainModel().getEntity(coordinate);
        if (terrain == TerrainConstant.GROUND) {
            gridModel.terrainModel().setEntity(coordinate, new Trail(EtpetsBalance.TRAIL_INTENSITY_DEFAULT));
        } else if (terrain instanceof Trail trail) {
            trail.incrementIntensity(EtpetsBalance.TRAIL_INTENSITY_INCREASE_PER_ENTRY);
        }
    }

    private static ActionEffect executeEatAction(ActionCandidate candidate, EtpetsGridModel gridModel, Pet pet) {
        ResourceEntity resourceEntity = gridModel.resourceModel().getEntity(candidate.interactionTarget());
        if ((resourceEntity instanceof ResourceBase resource) && resource.canConsume()) {
            // Consume the resource and gain energy.
            resource.consume();
            pet.changeEnergy(resource.energyGainPerAct());
        } else {
            AppLogger.warnf("Failed to execute EAT action due to failed preconditions: %s. Actual resource entity: %s",
                    candidate, resourceEntity.toDisplayString());
        }

        return ActionEffect.none();
    }

    private static ActionEffect executeReproduceAction(
            ActionCandidate candidate,
            Pet pet,
            Random random,
            EtpetsGridModel gridModel,
            int stepIndex,
            EtpetsIdSequence idSequence) {
        AgentEntity partnerEntity = gridModel.agentModel().getEntity(candidate.interactionTarget());
        if (!(partnerEntity instanceof Pet partnerPet) || !canReproduce(pet, partnerPet, stepIndex)) {
            AppLogger.warn("Failed to execute REPRODUCE action due to failed preconditions: " + candidate);
            return ActionEffect.none();
        }

        if (!isEggPlacementValid(candidate.eggTarget(), gridModel)) {
            AppLogger.warn("Failed to execute REPRODUCE action: invalid egg placement at " + candidate.eggTarget());
            return ActionEffect.none();
        }

        PetGenome genome = PetGenome.fromParents(
                new PetGenome(pet.traits()),
                new PetGenome(partnerPet.traits()),
                random,
                EtpetsBalance.PET_MUTATION_CHANCE_PER_TRAIT,
                EtpetsBalance.PET_MUTATION_DELTA
        );

        long parentAId = Math.min(pet.petId(), partnerPet.petId());
        long parentBId = Math.max(pet.petId(), partnerPet.petId());

        PetEgg egg = new PetEgg(
                idSequence.next(),
                parentAId,
                parentBId,
                genome,
                stepIndex,
                EtpetsBalance.PET_EGG_INCUBATION_REMAINING_DEFAULT
        );
        gridModel.agentModel().setEntity(candidate.eggTarget(), egg);

        pet.resetReproductionCooldown();
        partnerPet.resetReproductionCooldown();

        return new ActionEffect(1);
    }

    private static boolean isEggPlacementValid(@Nullable GridCoordinate eggCoordinate, EtpetsGridModel gridModel) {
        if (eggCoordinate == null) {
            return false;
        }
        return EtpetsCell.of(eggCoordinate, gridModel).isWalkable();
    }

    private static Pet hatchEgg(PetEgg egg, int stepIndex,
                                EtpetsIdSequence idSequence) {
        PetTraits traits = egg.petGenome().traits();
        int birthEnergy = Math.toIntExact(Math.round(traits.maxEnergy() * EtpetsBalance.PET_BIRTH_ENERGY_FACTOR));
        return new Pet(
                idSequence.next(),
                egg.parentAId(),
                egg.parentBId(),
                stepIndex,
                birthEnergy,
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_REMAINING_RANGE_MIN, // No cooldown at start
                traits);
    }

    private static Optional<ResourceBase> toConsumableResource(ResourceEntity entity) {
        if ((entity instanceof ResourceBase resource) && resource.canConsume()) {
            return Optional.of(resource);
        }
        return Optional.empty();
    }

    private static boolean isReproductionEligible(Pet pet, int stepIndex) {
        return (pet.ageAtStepIndex(stepIndex) >= EtpetsBalance.PET_REPRODUCTION_MIN_AGE)
                && pet.isReproductionEligibleByState();
    }

    /**
     * Returns {@code true} if both pets can reproduce together:
     * - Both must be eligible to reproduce
     * - They must not be direct relatives</     */
    private static boolean canReproduce(Pet petA, Pet petB, int stepIndex) {
        return isReproductionEligible(petA, stepIndex)
                && isReproductionEligible(petB, stepIndex)
                && !areDirectRelatives(petA, petB);
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

    private static Optional<GridCoordinate> findEggPlacementCellFromSnapshots(
            GridCoordinate coordA,
            GridCoordinate coordB,
            GridStructure structure,
            Map<GridCoordinate, RadiusRingCell<EtpetsCell>> snapshotCellsByCoordinate) {
        List<GridCoordinate> neighborsA = getValidNeighborCoordinates(coordA, structure);
        Set<GridCoordinate> neighborsASet = new HashSet<>(neighborsA);
        List<GridCoordinate> neighborsB = getValidNeighborCoordinates(coordB, structure);

        List<GridCoordinate> candidates = new ArrayList<>();
        for (GridCoordinate coordinate : neighborsB) {
            if (!neighborsASet.contains(coordinate) || coordinate.equals(coordA) || coordinate.equals(coordB)) {
                continue;
            }

            RadiusRingCell<EtpetsCell> ringCellSnapshot = snapshotCellsByCoordinate.get(coordinate);
            if (ringCellSnapshot == null) {
                continue;
            }
            EtpetsCell cellSnapshot = ringCellSnapshot.cell();
            if (cellSnapshot.terrainEntity() != TerrainConstant.GROUND) {
                continue;
            }
            if (cellSnapshot.resourceEntity().isNotEmpty()) {
                continue;
            }
            if (cellSnapshot.agentEntity().isNotEmpty()) {
                continue;
            }
            candidates.add(coordinate);
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        candidates.sort(EtpetsDeterminism::compareCoordinates);
        return Optional.of(candidates.getFirst());
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

    private static String toDisplayString(List<ActionCandidate> candidates) {
        StringBuilder sb = new StringBuilder(candidates.size() * DISPLAY_STRING_CAPACITY_HINT);
        for (int i = 0; i < candidates.size(); i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(toDisplayString(candidates.get(i)));
        }
        return sb.toString();
    }

    private static String toDisplayString(ActionCandidate candidate) {
        String egg = (candidate.eggTarget() != null) ? candidate.eggTarget().toDisplayString() : "-";
        return String.format(Locale.ROOT,
                "%s(score=%d, move=%s, target=%s, egg=%s)",
                candidate.type(),
                candidate.score(),
                candidate.moveTarget().toDisplayString(),
                candidate.interactionTarget().toDisplayString(),
                egg);
    }

    private enum ActionType {
        EAT,
        REPRODUCE,
        MOVE,
        WAIT
    }

    private record ActionCandidate(ActionType type,
                                   int score,
                                   GridCoordinate moveTarget,
                                   GridCoordinate interactionTarget,
                                   @Nullable GridCoordinate eggTarget) {}

    private record ReproductionOption(GridCoordinate partnerCoordinate,
                                      GridCoordinate eggCoordinate) {}

    private record ActionEffect(int eggCountDelta) {

        private static ActionEffect none() {
            return new ActionEffect(0);
        }

    }

}
