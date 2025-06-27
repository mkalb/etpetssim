package de.mkalb.etpetssim.wator;

import java.util.*;
import java.util.function.*;

public final class WaTorSimulation {

    private final WaTorConfigModel waTorConfigModel;
    private final WaTorSimulationModel waTorSimulationModel;
    private final WaTorTerritory territory;
    private final Random random;
    private final SortedMap<Long, WaTorCreature> creatures;
    private long sequence;
    private SimulationStatus simulationStatus;
    public WaTorSimulation(WaTorConfigModel waTorConfigModel, WaTorSimulationModel waTorSimulationModel) {
        this.waTorConfigModel = waTorConfigModel;
        this.waTorSimulationModel = waTorSimulationModel;
        territory = new WaTorTerritory(waTorConfigModel.xSize(), waTorConfigModel.ySize());
        simulationStatus = SimulationStatus.NEW;
        random = new Random();
        creatures = new TreeMap<>();
    }

    WaTorFish newFish(WaTorCoordinate coordinate) {
        WaTorFish waTorFish = new WaTorFish(sequence, waTorSimulationModel.timeCounter(), coordinate);
        waTorSimulationModel.incrementFishNumber();
        sequence++;
        return waTorFish;
    }

    WaTorShark newShark(WaTorCoordinate coordinate) {
        WaTorShark waTorShark = new WaTorShark(sequence, waTorSimulationModel.timeCounter(), coordinate, 10);
        waTorSimulationModel.incrementSharkNumber();
        sequence++;
        return waTorShark;
    }

    WaTorCoordinate randomCoordinate() {
        return new WaTorCoordinate(random.nextInt(waTorConfigModel.xSize()), random.nextInt(waTorConfigModel.ySize()));
    }

    void fillContainerWithCreatures(Function<WaTorCoordinate, WaTorCreature> creatureSupplier,
                                    IntSupplier numberSupplier) {
        int currentNumber = 0;
        int totalNumber = numberSupplier.getAsInt();
        while (currentNumber < totalNumber) {
            WaTorCoordinate coordinate = randomCoordinate();
            if (territory.isEmpty(coordinate)) {
                WaTorCreature creature = creatureSupplier.apply(coordinate);
                creatures.put(creature.sequenceId(), creature);
                territory.placeIdAt(creature.sequenceId(), coordinate);
                currentNumber++;
            }
        }
    }

    public Optional<WaTorCreature> creatureAt(WaTorCoordinate coordinate) {
        return territory.findIdAt(coordinate).map(creatures::get);
    }

    public SimulationStatus startSimulation() {
        if (simulationStatus != SimulationStatus.NEW) {
            throw new IllegalStateException();
        }

        fillContainerWithCreatures(this::newFish, waTorConfigModel::fishNumber);
        fillContainerWithCreatures(this::newShark, waTorConfigModel::sharkNumber);

        simulationStatus = SimulationStatus.STARTED;

        if (waTorSimulationModel.combinedNumberOfCreatures() <= 0) {
            simulationStatus = SimulationStatus.FINISHED;
        }

        return simulationStatus;
    }

    private WaTorCoordinate selectRandomCoordinate(List<WaTorCoordinate> coordinates) {
        return coordinates.get(random.nextInt(coordinates.size()));
    }

    private WaTorFish selectRandomFish(List<WaTorFish> fishs) {
        return fishs.get(random.nextInt(fishs.size()));
    }

    private void simulateCreature(WaTorCreature creature) {
        WaTorCoordinate coordinate = creature.currentPlace();
        List<WaTorCoordinate> neighbors = coordinate.neighbors(waTorConfigModel.xSize(), waTorConfigModel.ySize());
        List<WaTorCoordinate> emptyNeighbors = neighbors.stream().filter(territory::isEmpty).toList();

        switch (creature) {
            case WaTorFish fish -> {
                // Move
                if (!emptyNeighbors.isEmpty()) {
                    WaTorCoordinate newCoordinate = selectRandomCoordinate(emptyNeighbors);
                    territory.moveId(fish.sequenceId(), coordinate, newCoordinate);
                    fish.moveTo(newCoordinate);
                    // Reproduce
                    if (fish.age(waTorSimulationModel.timeCounter()) >= 5) {
                        if (fish.numberOfReproductions() < 20) {
                            if (fish.timeOfLastReproduction().isEmpty() ||
                                    ((waTorSimulationModel.timeCounter() - fish.timeOfLastReproduction().getAsLong()) >= 3)) {
                                WaTorFish childFish = newFish(coordinate);
                                creatures.put(childFish.sequenceId(), childFish);
                                territory.placeIdAt(childFish.sequenceId(), coordinate);
                                fish.reproduce(childFish);
                            }
                        }
                    }
                }
                if (fish.age(waTorSimulationModel.timeCounter()) >= 20) {
                    // Fish dies
                    territory.removeIdAt(fish.currentPlace());
                    creatures.remove(fish.sequenceId());
                    waTorSimulationModel.decrementFishNumber();
                }
            }
            case WaTorShark shark -> {
                // Reduce energy
                shark.reduceEnergy(1);

                List<WaTorFish> fishNeighbors = neighbors.stream()
                                                         .map(territory::findIdAt)
                                                         .flatMap(Optional::stream)
                                                         .map(creatures::get)
                                                         .filter(WaTorFish.class::isInstance)
                                                         .map(WaTorFish.class::cast)
                                                         .toList();

                WaTorCoordinate newCoordinate = null;

                if (!fishNeighbors.isEmpty()) {
                    // Move shark to fish neighbor if possible and eat fish and increase energy
                    WaTorFish fish = selectRandomFish(fishNeighbors);
                    // System.out.println("Fish: " + fish + " eaten by shark: " + shark);

                    // System.out.println("territory.removeIdAt: " + fish.currentPlace());
                    territory.removeIdAt(fish.currentPlace());

                    // System.out.println("shark.gainEnergy: " + fish);
                    shark.gainEnergy(2);

                    //  System.out.println("creatures.remove: " + fish.sequenceId());
                    creatures.remove(fish.sequenceId());

                    waTorSimulationModel.decrementFishNumber();

                    newCoordinate = fish.currentPlace();
                } else if (!emptyNeighbors.isEmpty()) {
                    newCoordinate = selectRandomCoordinate(emptyNeighbors);
                }

                // Move shark to new coordinate if possible
                if (newCoordinate != null) {
                    territory.moveId(shark.sequenceId(), coordinate, newCoordinate);
                    shark.moveTo(newCoordinate);

                    // Reproduce shark if possible
                    if ((shark.age(waTorSimulationModel.timeCounter()) >= 15) && (shark.currentEnergy() > 5)) {
                        if (shark.numberOfReproductions() < 20) {
                            if (shark.timeOfLastReproduction().isEmpty() ||
                                    ((waTorSimulationModel.timeCounter() - shark.timeOfLastReproduction().getAsLong()) >= 3)) {
                                WaTorShark childShark = newShark(coordinate);
                                creatures.put(childShark.sequenceId(), childShark);
                                territory.placeIdAt(childShark.sequenceId(), coordinate);
                                shark.reproduce(childShark);
                            }
                        }
                    }
                }

                if ((shark.currentEnergy() <= 0) || (shark.age(waTorSimulationModel.timeCounter()) >= 50)) {
                    // Shark dies
                    territory.removeIdAt(shark.currentPlace());
                    creatures.remove(shark.sequenceId());
                    waTorSimulationModel.decrementSharkNumber();
                }

            }
        }
    }

    public SimulationStatus updateSimulation() {
        if (simulationStatus != SimulationStatus.STARTED) {
            throw new IllegalStateException();
        }
        waTorSimulationModel.incrementTimeCounter();
        // System.out.println("Update simulation: " + waTorConfigModel.timeCounter());

        List<Long> currentCreatureIds = new ArrayList<>(creatures.keySet());
        currentCreatureIds.forEach(id -> {
            WaTorCreature creature = creatures.get(id);
            if (creature != null) { // A fish can be eaten by a shark
                try {
                    simulateCreature(creature);
                } catch (RuntimeException e) {
                    System.err.println("Error simulating creature: " + creature + " : " + e.getMessage());
                    throw e;
                }
            }
        });

        if (waTorSimulationModel.combinedNumberOfCreatures() <= 0) {
            simulationStatus = SimulationStatus.FINISHED;
        }

        return simulationStatus;
    }

    public enum SimulationStatus {NEW, STARTED, FINISHED}

}
