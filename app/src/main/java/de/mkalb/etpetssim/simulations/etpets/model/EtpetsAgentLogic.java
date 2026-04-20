package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
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
                continue;
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

                List<ActionCandidate> actionCandidates = collectActionCandidates(
                        currentCoordinate,
                        pet,
                        gridModel,
                        structure,
                        stepIndex
                );
                AppLogger.info(() -> String.format(Locale.ROOT,
                        "PET #%d @%s candidates: %s",
                        pet.petId(),
                        currentCoordinate.toDisplayString(),
                        toDisplayString(actionCandidates)));

                ActionCandidate selectedAction = pickBestCandidate(actionCandidates, random);
                AppLogger.info(() -> String.format(Locale.ROOT,
                        "PET #%d selected action: %s",
                        pet.petId(),
                        toDisplayString(selectedAction)));

                ActionEffect effect = executeActionCandidate(
                        selectedAction,
                        currentCoordinate,
                        pet,
                        random,
                        gridModel,
                        stepIndex,
                        idSequence
                );
                eggCountChange += effect.eggCountDelta();
            }
        }

        // Update statistics after processing all agents.
        statistics.updateActivePetCount(activePetCountChange);
        statistics.updateEggCount(eggCountChange);
        statistics.updateCumulativeDeadPetCount(cumulativeDeadPetCountChange);
    }

    private static List<ActionCandidate> collectActionCandidates(
            GridCoordinate currentCoordinate,
            Pet pet,
            EtpetsGridModel gridModel,
            GridStructure structure,
            int stepIndex) {
        SortedMap<Integer, SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>>> ringCells = CellNeighborhoods.cellsByRadiusRings(currentCoordinate,
                NeighborhoodMode.EDGES_ONLY,
                structure,
                2,
                c -> EtpetsCell.of(c, gridModel));
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ring0 = ringCells.getOrDefault(0, new TreeMap<>());
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ring1 = ringCells.getOrDefault(1, new TreeMap<>());
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ring2 = ringCells.getOrDefault(2, new TreeMap<>());

        Map<GridCoordinate, EtpetsCell> snapshotCellsByCoordinate = new HashMap<>();
        ring0.forEach((coordinate, ringCell) -> snapshotCellsByCoordinate.put(coordinate, ringCell.cell()));
        ring1.forEach((coordinate, ringCell) -> snapshotCellsByCoordinate.put(coordinate, ringCell.cell()));
        ring2.forEach((coordinate, ringCell) -> snapshotCellsByCoordinate.put(coordinate, ringCell.cell()));

        List<GridCoordinate> candidateTargets = new ArrayList<>();
        candidateTargets.add(currentCoordinate);
        for (Map.Entry<GridCoordinate, RadiusRingCell<EtpetsCell>> ring1Entry : ring1.entrySet()) {
            if (ring1Entry.getValue().cell().isWalkable()) {
                candidateTargets.add(ring1Entry.getKey());
            }
        }

        boolean selfReproductionEligible = isReproductionEligible(pet, stepIndex);
        boolean hungry = pet.currentEnergy() < EAT_IF_ADJACENT_ENERGY_THRESHOLD;

        Map<GridCoordinate, Integer> positionScores = new HashMap<>();
        Map<GridCoordinate, List<EtpetsCell>> consumablesByTarget = new HashMap<>();
        Map<GridCoordinate, List<ReproductionOption>> reproductionByTarget = new HashMap<>();

        for (GridCoordinate targetCoordinate : candidateTargets) {
            int positionScore = 0;

            EtpetsCell targetCellSnapshot = snapshotCellsByCoordinate.get(targetCoordinate);
            if (targetCellSnapshot == null) {
                AppLogger.warn("Missing cell snapshot for coordinate " + targetCoordinate);
                continue;
            }

            if (!targetCoordinate.equals(currentCoordinate)) {
                positionScore -= SCORE_MOVE_COST_PENALTY;

                if ((targetCellSnapshot.terrainEntity() instanceof Trail trail) && (trail.intensity() > TRAIL_PREFERENCE_THRESHOLD)) {
                    positionScore += SCORE_MOVE_TRAIL_WEAK_BONUS;
                }
            }

            List<EtpetsCell> consumableCells = new ArrayList<>();
            List<ReproductionOption> reproductionOptions = new ArrayList<>();

            for (GridCoordinate neighborCoordinate : getValidNeighborCoordinates(targetCoordinate, structure)) {
                EtpetsCell neighborCellSnapshot = snapshotCellsByCoordinate.get(neighborCoordinate);
                if (neighborCellSnapshot == null) {
                    continue;
                }

                if (toConsumableResource(neighborCellSnapshot.resourceEntity()).isPresent()) {
                    consumableCells.add(neighborCellSnapshot);
                }

                if (!selfReproductionEligible) {
                    continue;
                }

                AgentEntity neighborEntity = neighborCellSnapshot.agentEntity();
                if (!(neighborEntity instanceof Pet partnerPet)
                        || partnerPet.isDead()
                        || !isReproductionEligible(partnerPet, stepIndex)
                        || areDirectRelatives(pet, partnerPet)) {
                    continue;
                }

                Optional<GridCoordinate> eggCoordinate = findEggPlacementCellFromSnapshots(
                        targetCoordinate,
                        neighborCoordinate,
                        structure,
                        snapshotCellsByCoordinate);
                eggCoordinate.ifPresent(gridCoordinate ->
                        reproductionOptions.add(new ReproductionOption(neighborCoordinate, gridCoordinate)));
            }

            if (!consumableCells.isEmpty()) {
                positionScore += SCORE_MOVE_RING2_RESOURCE_BONUS;
                if (hungry) {
                    positionScore += SCORE_EAT_HUNGER_BONUS;
                }
            }

            if (!reproductionOptions.isEmpty()) {
                positionScore += SCORE_MOVE_RING2_PARTNER_BONUS;
            }

            positionScores.put(targetCoordinate, positionScore);
            consumablesByTarget.put(targetCoordinate, consumableCells);
            reproductionByTarget.put(targetCoordinate, reproductionOptions);
        }

        List<ActionCandidate> candidates = new ArrayList<>();

        int currentPositionScore = positionScores.getOrDefault(currentCoordinate, 0);
        for (ReproductionOption reproductionOption : reproductionByTarget.getOrDefault(currentCoordinate, Collections.emptyList())) {
            int score = currentPositionScore + SCORE_REPRODUCE_BASE + SCORE_REPRODUCE_PARTNER_BONUS;
            candidates.add(new ActionCandidate(
                    ActionType.REPRODUCE,
                    score,
                    currentCoordinate,
                    reproductionOption.partnerCoordinate(),
                    reproductionOption.eggCoordinate()));
        }

        for (EtpetsCell resourceCell : consumablesByTarget.getOrDefault(currentCoordinate, Collections.emptyList())) {
            Optional<ResourceBase> resourceOptional = toConsumableResource(resourceCell.resourceEntity());
            if (resourceOptional.isEmpty()) {
                continue;
            }
            ResourceBase resource = resourceOptional.orElseThrow();
            int score = currentPositionScore
                    + SCORE_EAT_BASE
                    + (resource.energyGainPerAct() * SCORE_EAT_ENERGY_GAIN_WEIGHT)
                    + ((int) Math.floor(resource.currentAmount()) * SCORE_EAT_AMOUNT_WEIGHT);
            candidates.add(new ActionCandidate(ActionType.EAT, score, currentCoordinate, resourceCell.coordinate(), null));
        }

        // WAIT is valid at current coordinate and is dominated by EAT/REPRODUCE via action weights.
        candidates.add(new ActionCandidate(ActionType.WAIT, currentPositionScore, currentCoordinate, currentCoordinate, null));

        for (GridCoordinate targetCoordinate : candidateTargets) {
            if (targetCoordinate.equals(currentCoordinate)) {
                continue;
            }
            int score = positionScores.getOrDefault(targetCoordinate, 0) + SCORE_MOVE_BASE;
            candidates.add(new ActionCandidate(ActionType.MOVE, score, targetCoordinate, targetCoordinate, null));
        }

        return candidates;
    }

    private static ActionCandidate pickBestCandidate(List<ActionCandidate> candidates, Random random) {
        int maxScore = candidates.stream().mapToInt(ActionCandidate::score).max().orElseThrow();
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
            case MOVE -> {
                movePet(currentCoordinate, candidate.moveTarget(), pet, gridModel);
                yield ActionEffect.none();
            }
            case EAT -> {
                ResourceEntity resourceEntity = gridModel.resourceModel().getEntity(candidate.interactionTarget());
                if ((resourceEntity instanceof ResourceBase resource) && resource.canConsume()) {
                    resource.consume();
                    pet.changeEnergy(resource.energyGainPerAct());
                } else {
                    AppLogger.warn("Failed to execute EAT action due to failed preconditions: " + candidate);
                }
                yield ActionEffect.none();
            }
            case REPRODUCE -> {
                AgentEntity partnerEntity = gridModel.agentModel().getEntity(candidate.interactionTarget());
                if (!(partnerEntity instanceof Pet partnerPet)
                        || partnerPet.isDead()
                        || !isReproductionEligible(partnerPet, stepIndex)
                        || areDirectRelatives(pet, partnerPet)
                        || (candidate.eggTarget() == null)
                        || gridModel.agentModel().getEntity(candidate.eggTarget()).isNotEmpty()
                        || gridModel.resourceModel().getEntity(candidate.eggTarget()).isNotEmpty()
                        || (gridModel.terrainModel().getEntity(candidate.eggTarget()) != TerrainConstant.GROUND)) {
                    AppLogger.warn("Failed to execute REPRODUCE action due to failed preconditions: " + candidate);
                    yield ActionEffect.none();
                }

                long parentAId = Math.min(pet.petId(), partnerPet.petId());
                long parentBId = Math.max(pet.petId(), partnerPet.petId());

                PetGenome genome = PetGenome.fromParents(
                        new PetGenome(pet.traits()),
                        new PetGenome(partnerPet.traits()),
                        random,
                        MUTATION_CHANCE_PER_TRAIT,
                        MUTATION_DELTA
                );

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

                yield new ActionEffect(1);
            }
        };
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
        StringBuilder sb = new StringBuilder();
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
