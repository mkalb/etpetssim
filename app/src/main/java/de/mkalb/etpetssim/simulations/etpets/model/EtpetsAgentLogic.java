package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.engine.support.AgentOrderingStrategies;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsAgentLogic {

    // Debug/log formatting
    private static final int DISPLAY_STRING_CAPACITY_HINT = 80;
    private static final String LOG_COMPONENT = "EtpetsAgentLogic";

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

            // Check if the entity is still at its original coordinate.
            if (agentModel.getEntity(currentCoordinate) != entity) {
                throw new IllegalStateException("EtpetsAgentLogic: Snapshot entity mismatch at "
                        + currentCoordinate.toDisplayString()
                        + " (expected=" + entity.toDisplayString()
                        + ", actual=" + agentModel.getEntity(currentCoordinate).toDisplayString() + ").");
            }

            if (entity instanceof PetEgg egg) {
                egg.decrementIncubationRemaining();
                if (egg.incubationRemaining() < EtpetsBalance.PET_EGG_INCUBATION_REMAINING_RANGE_MIN) {
                    Pet newPet = hatchEgg(egg, stepIndex, idSequence);
                    agentModel.setEntity(currentCoordinate, newPet);
                    updateTrailAtCoordinate(currentCoordinate, gridModel);
                    eggCountChange--;
                    activePetCountChange++;
                    logEggHatched(egg, newPet, currentCoordinate);
                }
            } else if (entity instanceof Pet pet) {
                // Death-check: remove pets already marked dead from the previous step.
                if (pet.isDead()) {
                    agentModel.setEntityToDefault(currentCoordinate);
                    continue;
                }

                // Passive energy loss and reproduction cooldown decrement.
                pet.changeEnergy(-EtpetsBalance.PET_STEP_ENERGY_LOSS);
                pet.tickReproductionCooldown();

                // Check death conditions.
                boolean petDied = false;
                if (pet.currentEnergy() < EtpetsBalance.PET_CURRENT_ENERGY_RANGE_MIN) {
                    logPetDiedFromEnergyDepletion(pet, currentCoordinate);
                    petDied = true;
                } else {
                    double ageMortalityChance = computeAgeMortalityChance(pet, stepIndex);
                    if ((ageMortalityChance > 0.0d) && (random.nextDouble() < ageMortalityChance)) {
                        logPetDiedFromAgeMortality(pet, currentCoordinate, ageMortalityChance);
                        petDied = true;
                    }
                }
                if (petDied) {
                    pet.die();
                    cumulativeDeadPetCountChange++;
                    activePetCountChange--;
                    continue; // Stays on grid for exactly 1 visual step.
                }

                ActionEffect effect = selectAndExecuteAction(
                        currentCoordinate,
                        pet,
                        gridModel,
                        structure,
                        random,
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

    private static ActionEffect selectAndExecuteAction(GridCoordinate currentCoordinate,
                                                       Pet pet,
                                                       EtpetsGridModel gridModel,
                                                       GridStructure structure,
                                                       Random random,
                                                       int stepIndex,
                                                       EtpetsIdSequence idSequence) {
        List<ActionCandidate> actionCandidates = collectActionCandidates(
                currentCoordinate,
                pet,
                gridModel,
                structure,
                random,
                stepIndex);

        ActionCandidate selectedAction = pickBestCandidate(actionCandidates, random);

        // AppLogger.infof("Pet %s at %s has %d action candidates: %s. Selected: %s",
        //         pet.toDisplayString(),
        //         currentCoordinate.toDisplayString(),
        //         actionCandidates.size(),
        //         toDisplayString(actionCandidates),
        //         toDisplayString(selectedAction));

        return executeActionCandidate(
                selectedAction,
                currentCoordinate,
                pet,
                random,
                gridModel,
                stepIndex,
                idSequence);
    }

    private static List<ActionCandidate> collectActionCandidates(GridCoordinate currentCoordinate,
                                                                 Pet pet,
                                                                 EtpetsGridModel gridModel,
                                                                 GridStructure structure,
                                                                 Random random,
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

        boolean canSelfReproduce = pet.isReproductionEligibleByState(stepIndex);

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
        Set<GridCoordinate> ring1HasLowMobilityPenalty = new HashSet<>();
        Set<GridCoordinate> ring1HasCrowdingPenalty = new HashSet<>();
        SortedMap<GridCoordinate, RadiusRingCell<EtpetsCell>> ring1Cells =
                neighborhoodCellsByRing.getOrDefault(1, Collections.emptySortedMap());
        for (RadiusRingCell<EtpetsCell> ring1Cell : ring1Cells.values()) {
            EtpetsCell cell = ring1Cell.cell();
            GridCoordinate coordinate = ring1Cell.coordinate();

            // EAT candidates adjacent to the current position (ring 0).
            if (toConsumableResource(cell.resourceEntity()).isPresent()) {
                ring0Consumables.add(cell);
            }

            // REPRODUCE candidates adjacent to the current position (ring 0).
            if (canSelfReproduce && isValidReproductionPartner(pet, cell, stepIndex)) {
                computeEggPlacementCoordinate(currentCoordinate, coordinate, structure, snapshotCellsByCoordinate)
                        .ifPresent(eggCoord -> ring0ReproductionOptions.add(new ReproductionOption(cell, eggCoord)));
            }

            // MOVE candidates: only walkable ring-1 cells.
            if (cell.isWalkable()) {
                List<GridCoordinate> neighborCoordinates = computeValidNeighborCoordinates(coordinate, structure);
                if (hasLowMobilityPenalty(neighborCoordinates, gridModel)) {
                    ring1HasLowMobilityPenalty.add(coordinate);
                }
                if (hasCrowdingPenalty(neighborCoordinates, gridModel)) {
                    ring1HasCrowdingPenalty.add(coordinate);
                }

                int moveScore = computeMoveScore(
                        pet,
                        cell,
                        coordinate,
                        ring1HasResourceBonus,
                        ring1HasPartnerBonus,
                        ring1HasLowMobilityPenalty,
                        ring1HasCrowdingPenalty,
                        random);
                candidates.add(new ActionCandidate(PetActionType.MOVE, moveScore,
                        coordinate, coordinate, null));
            }
        }

        // Pass 3: Ring 0 (current position) → score and create WAIT / EAT / REPRODUCE candidates.

        // WAIT (always available as fallback)
        int waitScore = computeWaitScore(pet, random, stepIndex);
        candidates.add(new ActionCandidate(PetActionType.WAIT, waitScore,
                currentCoordinate, currentCoordinate, null));

        // REPRODUCE
        if (canSelfReproduce && !ring0ReproductionOptions.isEmpty()) {
            for (ReproductionOption option : ring0ReproductionOptions) {
                int reproduceScore = computeReproduceScore(
                        pet,
                        option.partnerCell);
                candidates.add(new ActionCandidate(PetActionType.REPRODUCE, reproduceScore,
                        currentCoordinate, option.partnerCell.coordinate(), option.eggCoordinate()));
            }
        }

        // EAT
        if (pet.canEat() && !ring0Consumables.isEmpty()) {
            for (EtpetsCell resourceCell : ring0Consumables) {
                ResourceEntity resourceEntity = resourceCell.resourceEntity();
                if (!(resourceEntity instanceof ResourceBase resource) || !resource.canConsume()) {
                    continue;
                }

                int eatScore = computeEatScore(
                        pet,
                        resource,
                        stepIndex);
                candidates.add(new ActionCandidate(PetActionType.EAT, eatScore,
                        currentCoordinate, resourceCell.coordinate(), null));
            }
        }

        return candidates;
    }

    private static ActionCandidate pickBestCandidate(Collection<ActionCandidate> actionCandidates, Random random) {
        int maxScore = actionCandidates.stream()
                                       .mapToInt(ActionCandidate::score)
                                       .max()
                                       .orElseThrow();
        List<ActionCandidate> topCandidates = actionCandidates.stream()
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
            ActionCandidate actionCandidate,
            GridCoordinate currentCoordinate,
            Pet pet,
            Random random,
            EtpetsGridModel gridModel,
            int stepIndex,
            EtpetsIdSequence idSequence) {
        pet.recordLastAction(actionCandidate.type(), actionCandidate.score());
        return switch (actionCandidate.type()) {
            case WAIT -> executeWaitAction(currentCoordinate, gridModel);
            case MOVE -> executeMoveAction(currentCoordinate, actionCandidate.moveTarget(), pet, gridModel);
            case EAT ->
                    executeEatAction(currentCoordinate, actionCandidate.interactionTarget(), actionCandidate, pet, gridModel);
            case REPRODUCE -> executeReproduceAction(actionCandidate, pet, random, gridModel, stepIndex, idSequence);
        };
    }

    private static ActionEffect executeWaitAction(GridCoordinate petCoordinate,
                                                  EtpetsGridModel gridModel) {
        updateTrailAtCoordinate(petCoordinate, gridModel);

        return ActionEffect.none();
    }

    private static ActionEffect executeMoveAction(GridCoordinate fromCoordinate,
                                                  GridCoordinate toCoordinate,
                                                  Pet pet,
                                                  EtpetsGridModel gridModel) {
        // Movement energy cost (at least 1, scaled by movementCostModifier).
        long roundedMovementCost = Math.round(pet.traits().movementCostModifier());
        int movementCost = Math.max(1, Math.toIntExact(roundedMovementCost));
        pet.changeEnergy(-movementCost);

        // Relocate pet.
        pet.recordMoveFrom(fromCoordinate);
        gridModel.agentModel().setEntityToDefault(fromCoordinate);
        gridModel.agentModel().setEntity(toCoordinate, pet);

        updateTrailAtCoordinate(toCoordinate, gridModel);

        return ActionEffect.none();
    }

    private static ActionEffect executeEatAction(GridCoordinate petCoordinate,
                                                 GridCoordinate resourceCoordinate,
                                                 ActionCandidate actionCandidate,
                                                 Pet pet,
                                                 EtpetsGridModel gridModel) {
        if (!(gridModel.resourceModel().getEntity(resourceCoordinate) instanceof ResourceBase resource)
                || !resource.canConsume()) {
            throw new IllegalStateException("EtpetsAgentLogic: EAT precondition failed for action " + actionCandidate + ".");
        }

        // Consume the resource, gain energy and update trail.
        resource.consume();
        pet.changeEnergy(resource.energyGainPerAct());
        updateTrailAtCoordinate(petCoordinate, gridModel);

        return ActionEffect.none();
    }

    private static ActionEffect executeReproduceAction(ActionCandidate actionCandidate,
                                                       Pet pet,
                                                       Random random,
                                                       EtpetsGridModel gridModel,
                                                       int stepIndex,
                                                       EtpetsIdSequence idSequence) {
        if (!(gridModel.agentModel().getEntity(actionCandidate.interactionTarget()) instanceof Pet partnerPet)
                || !canReproduce(pet, partnerPet, stepIndex)) {
            throw new IllegalStateException("EtpetsAgentLogic: REPRODUCE precondition failed for action " + actionCandidate + ".");
        }

        if (!(actionCandidate.eggTarget() instanceof GridCoordinate eggCoordinate)
                || !EtpetsCell.of(eggCoordinate, gridModel).isWalkable()) {
            throw new IllegalStateException("EtpetsAgentLogic: REPRODUCE egg placement invalid for action " + actionCandidate + ".");
        }

        PetGenome genome = PetGenome.fromParents(
                pet.traitsGenome(),
                partnerPet.traitsGenome(),
                random,
                EtpetsBalance.PET_GENOME_MUTATION_CHANCE_PER_TRAIT,
                EtpetsBalance.PET_GENOME_MUTATION_DELTA
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
        gridModel.agentModel().setEntity(eggCoordinate, egg);

        pet.resetReproductionCooldown();
        partnerPet.resetReproductionCooldown();

        return new ActionEffect(1);
    }

    private static int computeMoveScore(Pet pet,
                                        EtpetsCell cell,
                                        GridCoordinate coordinate,
                                        Set<GridCoordinate> ring1HasResourceBonus,
                                        Set<GridCoordinate> ring1HasPartnerBonus,
                                        Set<GridCoordinate> ring1HasLowMobilityPenalty,
                                        Set<GridCoordinate> ring1HasCrowdingPenalty,
                                        Random random) {
        double energyRatio = clampToUnitRange((double) pet.currentEnergy() / pet.traits().maxEnergy());
        boolean isGroundWithoutTrail = false;
        int trailIntensity = 0;
        TerrainEntity terrain = cell.terrainEntity();
        if (terrain instanceof Trail trail) {
            trailIntensity = trail.intensity();
        } else if (terrain == TerrainConstant.GROUND) {
            isGroundWithoutTrail = true;
        }
        boolean hasOscillationHistoryMatch = pet.hasCoordinateInMovementHistory(coordinate);

        double rawScore = EtpetsScoreMath.computeRawMoveScore(
                energyRatio,
                pet.traits().movementCostModifier(),
                ring1HasResourceBonus.contains(coordinate),
                ring1HasPartnerBonus.contains(coordinate),
                ring1HasLowMobilityPenalty.contains(coordinate),
                ring1HasCrowdingPenalty.contains(coordinate),
                isGroundWithoutTrail,
                trailIntensity,
                hasOscillationHistoryMatch);

        // Rare exploration spike on fresh ground to help break local movement loops.
        if (isGroundWithoutTrail
                && (rawScore > EtpetsBalance.PET_MOVE_SCORE_BASE)
                && (random.nextDouble() < EtpetsBalance.PET_MOVE_EXPLORATION_SPIKE_CHANCE)) {
            rawScore *= EtpetsBalance.PET_MOVE_EXPLORATION_SPIKE_MULTIPLIER;
        }

        int roundedScore = Math.toIntExact(Math.round(rawScore));
        return Math.clamp(roundedScore,
                EtpetsBalance.PET_MOVE_SCORE_RANGE_MIN,
                EtpetsBalance.PET_MOVE_SCORE_RANGE_MAX);
    }

    private static int computeWaitScore(Pet pet,
                                        Random random,
                                        int stepIndex) {
        if (!pet.hasReachedAgeingEffectsAge(stepIndex)) {
            return 0;
        }

        int ageingSteps = pet.ageingStepsAtStepIndex(stepIndex);
        double rawWaitChance = EtpetsBalance.PET_AGEING_WAIT_CHANCE_BASE
                + (ageingSteps * EtpetsBalance.PET_AGEING_WAIT_CHANCE_INCREASE_PER_STEP);
        double waitChance = Math.clamp(rawWaitChance, 0.0d, EtpetsBalance.PET_AGEING_WAIT_CHANCE_MAX);
        if (random.nextDouble() >= waitChance) {
            return 0;
        }

        int rawWaitScore = 1 + (ageingSteps / EtpetsBalance.PET_AGEING_WAIT_SCORE_INCREASE_STEP_SPAN);
        return Math.clamp(rawWaitScore, 1, EtpetsBalance.PET_AGEING_WAIT_SCORE_MAX);
    }

    private static int computeReproduceScore(Pet pet,
                                             EtpetsCell partnerCell) {
        if (!(partnerCell.agentEntity() instanceof Pet partnerPet)) {
            throw new IllegalStateException("EtpetsAgentLogic: Invalid reproduction partner cell: expected agent Pet, actual="
                    + partnerCell.agentEntity().toDisplayString() + ".");
        }

        double petQualityScore = pet.traits().genomeQualityScore();
        double partnerQualityScore = partnerPet.traits().genomeQualityScore();

        double rawScore = EtpetsScoreMath.computeRawReproduceScore(
                petQualityScore,
                partnerQualityScore);
        int roundedScore = Math.toIntExact(Math.round(rawScore));
        return Math.clamp(roundedScore,
                EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MIN,
                EtpetsBalance.PET_REPRODUCTION_SCORE_RANGE_MAX);
    }

    private static int computeEatScore(Pet pet,
                                       ResourceBase resource,
                                       int stepIndex) {
        int currentEnergy = pet.currentEnergy();
        int maxEnergy = pet.traits().maxEnergy();
        int resourceGain = resource.energyGainPerAct();
        int age = pet.ageAtStepIndex(stepIndex);

        double rawScore = EtpetsScoreMath.computeRawEatScore(
                currentEnergy,
                maxEnergy,
                resourceGain,
                age);
        int roundedScore = Math.toIntExact(Math.round(rawScore));
        return Math.clamp(roundedScore,
                EtpetsBalance.PET_EAT_SCORE_RANGE_MIN,
                EtpetsBalance.PET_EAT_SCORE_RANGE_MAX);
    }

    private static double computeAgeMortalityChance(Pet pet, int stepIndex) {
        if (!pet.hasReachedAgeingEffectsAge(stepIndex)) {
            return 0.0d;
        }

        int ageingSteps = pet.ageingStepsAtStepIndex(stepIndex);
        double rawChance = EtpetsBalance.PET_AGEING_MORTALITY_CHANCE_BASE
                + (ageingSteps * EtpetsBalance.PET_AGEING_MORTALITY_CHANCE_INCREASE_PER_STEP);
        return Math.clamp(rawChance, 0.0d, EtpetsBalance.PET_AGEING_MORTALITY_CHANCE_MAX);
    }

    private static boolean isValidReproductionPartner(Pet pet, EtpetsCell partnerCell, int stepIndex) {
        if (!(partnerCell.agentEntity() instanceof Pet partnerPet)) {
            return false;
        }
        return canReproduce(pet, partnerPet, stepIndex);
    }

    private static boolean canReproduce(Pet firstPet, Pet secondPet, int stepIndex) {
        return firstPet.isReproductionEligibleByState(stepIndex)
                && secondPet.isReproductionEligibleByState(stepIndex)
                && !areDirectRelatives(firstPet, secondPet);
    }

    private static boolean areDirectRelatives(Pet firstPet, Pet secondPet) {
        Set<Long> idsA = new HashSet<>();
        idsA.add(firstPet.petId());
        if (firstPet.parentAId() != null) {
            idsA.add(firstPet.parentAId());
        }
        if (firstPet.parentBId() != null) {
            idsA.add(firstPet.parentBId());
        }
        if (idsA.contains(secondPet.petId())) {
            return true;
        }
        if ((secondPet.parentAId() != null) && idsA.contains(secondPet.parentAId())) {
            return true;
        }
        return (secondPet.parentBId() != null) && idsA.contains(secondPet.parentBId());
    }

    private static Optional<GridCoordinate> computeEggPlacementCoordinate(
            GridCoordinate sourceCoordinate,
            GridCoordinate partnerCoordinate,
            GridStructure gridStructure,
            Map<GridCoordinate, RadiusRingCell<EtpetsCell>> snapshotCellsByCoordinate) {
        // TODO Optimize methode and parameter
        List<GridCoordinate> sourceNeighbors = computeValidNeighborCoordinates(sourceCoordinate, gridStructure);
        Set<GridCoordinate> sourceNeighborSet = new HashSet<>(sourceNeighbors);
        List<GridCoordinate> partnerNeighbors = computeValidNeighborCoordinates(partnerCoordinate, gridStructure);

        List<GridCoordinate> candidates = new ArrayList<>();
        for (GridCoordinate coordinate : partnerNeighbors) {
            if (!sourceNeighborSet.contains(coordinate) || coordinate.equals(sourceCoordinate) || coordinate.equals(partnerCoordinate)) {
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
        candidates.sort(EtpetsDeterminism::compareCoordinates); // TODO replace compareCoordinates with random or better solution
        return Optional.of(candidates.getFirst());
    }

    private static List<GridCoordinate> computeValidNeighborCoordinates(GridCoordinate originCoordinate,
                                                                        GridStructure gridStructure) {
        Collection<EdgeBehaviorResult> results =
                CellNeighborhoods.neighborEdgeResults(originCoordinate, NeighborhoodMode.EDGES_ONLY, gridStructure);
        List<GridCoordinate> valid = new ArrayList<>(results.size());
        for (EdgeBehaviorResult r : results) {
            if (r.action() == EdgeBehaviorAction.VALID) {
                valid.add(r.mapped());
            }
        }
        return valid;
    }

    private static boolean hasLowMobilityPenalty(List<GridCoordinate> neighborCoordinates,
                                                 EtpetsGridModel gridModel) {
        int walkableCount = 0;
        for (GridCoordinate neighbor : neighborCoordinates) {
            if (EtpetsCell.of(neighbor, gridModel).isWalkable()) {
                walkableCount++;
            }
        }
        return walkableCount < EtpetsBalance.PET_MOVE_LOW_MOBILITY_THRESHOLD;
    }

    private static boolean hasCrowdingPenalty(List<GridCoordinate> neighborCoordinates,
                                              EtpetsGridModel gridModel) {
        int petCount = 0;
        for (GridCoordinate neighbor : neighborCoordinates) {
            AgentEntity agent = gridModel.agentModel().getEntity(neighbor);
            if ((agent instanceof Pet pet) && !pet.isDead()) {
                petCount++;
            }
        }
        return petCount >= EtpetsBalance.PET_MOVE_CROWDING_THRESHOLD;
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

    private static Pet hatchEgg(PetEgg egg, int stepIndex,
                                EtpetsIdSequence idSequence) {
        PetTraits traits = egg.petGenome().traits();
        int birthEnergy = Math.toIntExact(Math.round(traits.maxEnergy() * EtpetsBalance.PET_CURRENT_ENERGY_BIRTH_FACTOR));
        return new Pet(
                idSequence.next(),
                egg.parentAId(),
                egg.parentBId(),
                stepIndex,
                birthEnergy,
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_REMAINING_RANGE_MIN, // No cooldown at start
                traits);
    }

    private static Optional<ResourceBase> toConsumableResource(ResourceEntity resourceEntity) {
        if ((resourceEntity instanceof ResourceBase resource) && resource.canConsume()) {
            return Optional.of(resource);
        }
        return Optional.empty();
    }

    private static void logEggHatched(PetEgg egg, Pet newPet, GridCoordinate coordinate) {
        AppLogger.infof("%s: Egg %s hatched into Pet %s at %s.",
                LOG_COMPONENT,
                egg.toDisplayString(),
                newPet.toDisplayString(),
                coordinate.toDisplayString());
    }

    private static void logPetDiedFromEnergyDepletion(Pet pet, GridCoordinate coordinate) {
        AppLogger.infof("%s: Pet %s died at %s (reason=energy-depletion).",
                LOG_COMPONENT,
                pet.toDisplayString(),
                coordinate.toDisplayString());
    }

    private static void logPetDiedFromAgeMortality(Pet pet, GridCoordinate coordinate, double mortalityChance) {
        AppLogger.infof("%s: Pet %s died at %s (reason=age-mortality, chance=%.6f).",
                LOG_COMPONENT,
                pet.toDisplayString(),
                coordinate.toDisplayString(),
                mortalityChance);
    }

    private static double clampToUnitRange(double value) {
        return Math.clamp(value, 0.0d, 1.0d);
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

    private record ActionCandidate(PetActionType type,
                                   int score,
                                   GridCoordinate moveTarget,
                                   GridCoordinate interactionTarget,
                                   @Nullable GridCoordinate eggTarget) {

        private ActionCandidate {
            if (score < 0) {
                throw new IllegalArgumentException("EtpetsAgentLogic: ActionCandidate score must be >= 0 (actual=" + score + ").");
            }
            switch (type) {
                case WAIT, MOVE, EAT -> {
                    if (eggTarget != null) {
                        throw new IllegalArgumentException("EtpetsAgentLogic: ActionCandidate eggTarget must be null for type " + type + ".");
                    }
                }
                case REPRODUCE -> {
                    if (eggTarget == null) {
                        throw new IllegalArgumentException("EtpetsAgentLogic: ActionCandidate eggTarget is required for type REPRODUCE.");
                    }
                }
            }
        }

    }

    private record ReproductionOption(EtpetsCell partnerCell, GridCoordinate eggCoordinate) {

        private ReproductionOption {
            if (!(partnerCell.agentEntity() instanceof Pet)) {
                throw new IllegalArgumentException("EtpetsAgentLogic: ReproductionOption partnerCell must contain a Pet agent.");
            }
        }

    }

    private record ActionEffect(int eggCountDelta) {

        private ActionEffect {
            if (eggCountDelta < 0) {
                throw new IllegalArgumentException("EtpetsAgentLogic: ActionEffect eggCountDelta must be >= 0 (actual=" + eggCountDelta + ").");
            }
        }

        private static ActionEffect none() {
            return new ActionEffect(0);
        }

    }

}
