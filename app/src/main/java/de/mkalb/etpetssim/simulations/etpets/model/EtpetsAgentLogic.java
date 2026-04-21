package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsAgentLogic {

    // ---- V1 balancing constants ----

    public static final int DEFAULT_MAX_ENERGY = 100;
    public static final double DEFAULT_MOVEMENT_COST_MODIFIER = 1.0d;
    public static final int DEFAULT_REPRODUCTION_MIN_ENERGY = 70;
    public static final int DEFAULT_REPRODUCTION_COOLDOWN_MAX = 200;

    private static final int ENERGY_LOSS_PER_STEP = 1;
    private static final int EAT_IF_ADJACENT_ENERGY_THRESHOLD = 80;
    private static final int REPRODUCTION_MIN_AGE = 120;
    private static final int INCUBATION_DURATION = 10;

    // ---- V1 action scoring constants ----
    private static final int SCORE_REPRODUCE_BASE = 80;
    private static final int SCORE_REPRODUCE_PARTNER_BONUS = 5;

    private static final int SCORE_EAT_BASE = 30;
    private static final int SCORE_EAT_HUNGER_BONUS = 6;
    private static final int SCORE_EAT_ENERGY_GAIN_WEIGHT = 2;
    private static final int SCORE_EAT_AMOUNT_WEIGHT = 1;

    private static final int SCORE_MOVE_BASE = 10;
    private static final int SCORE_MOVE_RING2_RESOURCE_BONUS = 8;
    private static final int SCORE_MOVE_RING2_PARTNER_BONUS = 6;
    private static final int SCORE_MOVE_TRAIL_WEAK_BONUS = 2;
    private static final int SCORE_MOVE_COST_PENALTY = 2;

    // ---- Default initial pet trait values (used by EtpetsSimulationManager) ----
    private static final double TRAIL_INCREASE_PER_ENTRY = 1.0d;
    private static final double TRAIL_PREFERENCE_THRESHOLD = 10.0d;
    private static final double TRAIL_MAX = 100.0d;
    private static final double MUTATION_CHANCE_PER_TRAIT = 0.08d;
    private static final double MUTATION_DELTA = 0.05d;
    private static final int DISPLAY_STRING_CAPACITY_HINT = 80;

    private EtpetsAgentLogic() {
    }

    public static void apply(Random random, EtpetsGridModel gridModel, EtpetsIdSequence idSequence, int stepIndex, EtpetsStatistics statistics) {
        int activePetCountChange = 0;
        int eggCountChange = 0;
        int cumulativeDeadPetCountChange = 0;
        GridStructure structure = gridModel.structure();
        WritableGridModel<AgentEntity> agentModel = gridModel.agentModel();

        // Snapshot of all non-default agent cells, sorted by coordinate for determinism.
        List<GridCell<AgentEntity>> agentCells = agentModel.nonDefaultCells()
                                                           .sorted(Comparator.comparing(GridCell::coordinate, EtpetsDeterminism::compareCoordinates))
                                                           .toList();
        // TODO Sort agents by ID (age), shuffle randomly or by position (coordinate)

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
                egg.decreaseIncubation();
                if (egg.incubationRemaining() <= 0) {
                    Pet newPet = hatchEgg(egg, stepIndex, idSequence);
                    agentModel.setEntity(currentCoordinate, newPet);
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
                pet.changeEnergy(-ENERGY_LOSS_PER_STEP);
                pet.decrementReproductionCooldown();

                // Death from energy depletion.
                if (pet.currentEnergy() <= 0) {
                    pet.markDead(stepIndex);
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

        // Coordinate-indexed snapshots allow deterministic position analysis and simple lookups.
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> snapshotCellsByCoordinate = new TreeMap<>();
        for (SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ringCells : neighborhoodCellsByRing.values()) {
            snapshotCellsByCoordinate.putAll(ringCells);
        }

        boolean canSelfReproduce = isReproductionEligible(pet, stepIndex);
        boolean isHungry = pet.currentEnergy() < EAT_IF_ADJACENT_ENERGY_THRESHOLD;

        SortedMap<GridCoordinate, PositionAnalysis> analysesByCoordinate = new TreeMap<>();
        for (RadiusRingCell<EtpetsCell> candidateCell : snapshotCellsByCoordinate.values()) {
            boolean isCurrentCell = candidateCell.ring() == 0;
            boolean isAdjacentWalkableCell = (candidateCell.ring() == 1) && candidateCell.cell().isWalkable();
            if (!isCurrentCell && !isAdjacentWalkableCell) {
                continue;
            }

            analysesByCoordinate.put(
                    candidateCell.coordinate(),
                    analyzePosition(
                            candidateCell,
                            currentCoordinate,
                            snapshotCellsByCoordinate,
                            structure,
                            pet,
                            canSelfReproduce,
                            stepIndex));
        }

        return buildActionCandidates(currentCoordinate, analysesByCoordinate, isHungry);
    }

    private static PositionAnalysis analyzePosition(
            RadiusRingCell<EtpetsCell> targetRingCell,
            GridCoordinate currentCoordinate,
            Map<GridCoordinate, RadiusRingCell<EtpetsCell>> cellSnapshots,
            GridStructure structure,
            Pet pet,
            boolean selfCanReproduce,
            int stepIndex) {
        GridCoordinate targetCoordinate = targetRingCell.coordinate();
        EtpetsCell targetCell = targetRingCell.cell();

        int positionScore = 0;
        if (!targetCoordinate.equals(currentCoordinate)) {
            positionScore -= SCORE_MOVE_COST_PENALTY;
            if ((targetCell.terrainEntity() instanceof Trail trail) && (trail.intensity() > TRAIL_PREFERENCE_THRESHOLD)) {
                positionScore += SCORE_MOVE_TRAIL_WEAK_BONUS;
            }
        }

        List<EtpetsCell> consumables = new ArrayList<>();
        List<ReproductionOption> reproductionOptions = new ArrayList<>();

        // for (GridCoordinate neighborCoord : getValidNeighborCoordinates(targetCoordinate, structure)) {
        //     EtpetsCell neighborCell = cellSnapshots.get(neighborCoord);
        //     if (neighborCell == null) {
        //         continue;
        //     }
        //
        //     if (toConsumableResource(neighborCell.resourceEntity()).isPresent()) {
        //         consumables.add(neighborCell);
        //     }
        //
        //     if (selfCanReproduce && isValidReproductionPartner(pet, neighborCell, stepIndex, cellSnapshots, targetCoordinate, neighborCoord, structure)) {
        //         Optional<GridCoordinate> eggCoord = findEggPlacementCellFromSnapshots(
        //                 targetCoordinate, neighborCoord, structure, cellSnapshots);
        //         eggCoord.ifPresent(coord -> reproductionOptions.add(new ReproductionOption(neighborCoord, coord)));
        //     }
        // }

        // Apply position score adjustments
        if (!consumables.isEmpty()) {
            positionScore += SCORE_MOVE_RING2_RESOURCE_BONUS;
        }
        if (!reproductionOptions.isEmpty()) {
            positionScore += SCORE_MOVE_RING2_PARTNER_BONUS;
        }

        return new PositionAnalysis(positionScore, consumables, reproductionOptions);
    }

    private static boolean isValidReproductionPartner(
            Pet pet,
            EtpetsCell partnerCell,
            int stepIndex,
            Map<GridCoordinate, EtpetsCell> cellSnapshots,
            GridCoordinate targetCoordinate,
            GridCoordinate partnerCoordinate,
            GridStructure structure) {
        AgentEntity agentEntity = partnerCell.agentEntity();
        if (!(agentEntity instanceof Pet partnerPet)) {
            return false;
        }

        if (partnerPet.isDead() || !isReproductionEligible(partnerPet, stepIndex) || areDirectRelatives(pet, partnerPet)) {
            return false;
        }

        return findEggPlacementCellFromSnapshots(targetCoordinate, partnerCoordinate, structure, cellSnapshots).isPresent();
    }

    private static List<ActionCandidate> buildActionCandidates(
            GridCoordinate currentCoordinate,
            Map<GridCoordinate, PositionAnalysis> positionAnalyses,
            boolean hungry) {
        List<ActionCandidate> candidates = new ArrayList<>();
        PositionAnalysis currentAnalysis = positionAnalyses.get(currentCoordinate);
        int currentScore = (currentAnalysis != null) ? currentAnalysis.positionScore() : 0;

        // Reproduction actions
        if (currentAnalysis != null) {
            for (ReproductionOption option : currentAnalysis.reproductionOptions()) {
                int score = currentScore + SCORE_REPRODUCE_BASE + SCORE_REPRODUCE_PARTNER_BONUS;
                candidates.add(new ActionCandidate(
                        ActionType.REPRODUCE, score, currentCoordinate,
                        option.partnerCoordinate(), option.eggCoordinate()));
            }

            // Eat actions
            for (EtpetsCell resourceCell : currentAnalysis.consumables()) {
                Optional<ResourceBase> resourceOpt = toConsumableResource(resourceCell.resourceEntity());
                if (resourceOpt.isEmpty()) {
                    continue;
                }

                ResourceBase resource = resourceOpt.orElseThrow();
                @SuppressWarnings("NumericCastThatLosesPrecision")
                int resourceAmount = Math.min(Integer.MAX_VALUE, (int) resource.currentAmount());
                int hungerBonus = hungry ? SCORE_EAT_HUNGER_BONUS : 0;
                int score = currentScore + SCORE_EAT_BASE + hungerBonus
                        + (resource.energyGainPerAct() * SCORE_EAT_ENERGY_GAIN_WEIGHT)
                        + (resourceAmount * SCORE_EAT_AMOUNT_WEIGHT);

                candidates.add(new ActionCandidate(
                        ActionType.EAT, score, currentCoordinate, resourceCell.coordinate(), null));
            }
        }

        // Wait action
        candidates.add(new ActionCandidate(
                ActionType.WAIT, currentScore, currentCoordinate, currentCoordinate, null));

        // Move actions
        for (Map.Entry<GridCoordinate, PositionAnalysis> entry : positionAnalyses.entrySet()) {
            GridCoordinate targetCoord = entry.getKey();
            if (!targetCoord.equals(currentCoordinate)) {
                int score = entry.getValue().positionScore() + SCORE_MOVE_BASE;
                candidates.add(new ActionCandidate(
                        ActionType.MOVE, score, targetCoord, targetCoord, null));
            }
        }

        return candidates;
    }

    private static ActionCandidate pickBestCandidate(List<ActionCandidate> candidates, Random random) {
        int maxScore = candidates.stream()
                                 .mapToInt(ActionCandidate::score)
                                 .max()
                                 .orElseThrow();
        List<ActionCandidate> topCandidates = candidates.stream()
                                                        .filter(candidate -> candidate.score() == maxScore)
                                                        .toList();

        if (topCandidates.size() == 1) {
            return topCandidates.getFirst();
        }

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
        gridModel.agentModel().setEntityToDefault(from);
        gridModel.agentModel().setEntity(to, pet);

        // Update terrain trail at destination.
        TerrainEntity terrain = gridModel.terrainModel().getEntity(to);
        if (terrain == TerrainConstant.GROUND) {
            gridModel.terrainModel().setEntity(to, new Trail(TRAIL_INCREASE_PER_ENTRY));
        } else if (terrain instanceof Trail trail) {
            trail.increase(TRAIL_INCREASE_PER_ENTRY, TRAIL_MAX);
        }

        return ActionEffect.none();
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
                MUTATION_CHANCE_PER_TRAIT,
                MUTATION_DELTA
        );

        long parentAId = Math.min(pet.petId(), partnerPet.petId());
        long parentBId = Math.max(pet.petId(), partnerPet.petId());

        PetEgg egg = new PetEgg(
                idSequence.next(),
                parentAId,
                parentBId,
                genome,
                stepIndex,
                INCUBATION_DURATION
        );
        gridModel.agentModel().setEntity(candidate.eggTarget(), egg);

        pet.setReproductionCooldownRemaining(pet.traits().reproductionCooldownMax());
        partnerPet.setReproductionCooldownRemaining(partnerPet.traits().reproductionCooldownMax());

        return new ActionEffect(1);
    }

    private static boolean isEggPlacementValid(@Nullable GridCoordinate eggCoord, EtpetsGridModel gridModel) {
        if (eggCoord == null) {
            return false;
        }
        return gridModel.agentModel().getEntity(eggCoord).isEmpty()
                && gridModel.resourceModel().getEntity(eggCoord).isEmpty()
                && (gridModel.terrainModel().getEntity(eggCoord) == TerrainConstant.GROUND);
    }

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

    private static Optional<ResourceBase> toConsumableResource(ResourceEntity entity) {
        if ((entity instanceof ResourceBase resource) && resource.canConsume()) {
            return Optional.of(resource);
        }
        return Optional.empty();
    }

    private static boolean isReproductionEligible(Pet pet, int stepIndex) {
        return !pet.isDead()
                && (pet.ageAtStepIndex(stepIndex) >= REPRODUCTION_MIN_AGE)
                && (pet.currentEnergy() >= pet.traits().reproductionMinEnergy())
                && (pet.reproductionCooldownRemaining() == 0);
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
            Map<GridCoordinate, EtpetsCell> snapshotCellsByCoordinate) {
        List<GridCoordinate> neighborsA = getValidNeighborCoordinates(coordA, structure);
        Set<GridCoordinate> neighborsASet = new HashSet<>(neighborsA);
        List<GridCoordinate> neighborsB = getValidNeighborCoordinates(coordB, structure);

        List<GridCoordinate> candidates = new ArrayList<>();
        for (GridCoordinate coordinate : neighborsB) {
            if (!neighborsASet.contains(coordinate) || coordinate.equals(coordA) || coordinate.equals(coordB)) {
                continue;
            }

            EtpetsCell cellSnapshot = snapshotCellsByCoordinate.get(coordinate);
            if (cellSnapshot == null) {
                continue;
            }
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

    /** Immutable analysis result for a candidate position. */
    private record PositionAnalysis(
            int positionScore,
            List<EtpetsCell> consumables,
            List<ReproductionOption> reproductionOptions) {}

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
