package de.mkalb.etpetssim.wator;

import java.util.*;
import java.util.function.*;

public final class WaTorSimulation {

    public enum SimulationStatus {NEW, STARTED, FINISHED}

    private final WaTorModel waTorModel;
    private final WaTorTerritory territory;
    private final Random random;
    private long sequence;
    private SimulationStatus simulationStatus;
    private int currentFishNumber;
    private int currentSharkNumber;
    private final SortedMap<Long, WaTorSeaCreature> creatures;

    public WaTorSimulation(WaTorModel waTorModel) {
        this.waTorModel = waTorModel;
        territory = new WaTorTerritory(waTorModel.xSize(), waTorModel.ySize());
        simulationStatus = SimulationStatus.NEW;
        random = new Random();
        currentFishNumber = 0;
        currentSharkNumber = 0;
        creatures = new TreeMap<>();
    }

    WaTorFish newFish(WaTorCoordinate coordinate) {
        WaTorFish waTorFish = new WaTorFish(sequence, waTorModel.timeCounter(), coordinate);
        currentFishNumber++;
        sequence++;
        return waTorFish;
    }

    WaTorShark newShark(WaTorCoordinate coordinate) {
        WaTorShark waTorShark = new WaTorShark(sequence, waTorModel.timeCounter(), coordinate, 10);
        currentSharkNumber++;
        sequence++;
        return waTorShark;
    }

    WaTorCoordinate randomCoordinate() {
        return new WaTorCoordinate(random.nextInt(waTorModel.xSize()), random.nextInt(waTorModel.ySize()));
    }

    void fillContainerWithCreatures(Function<WaTorCoordinate, WaTorSeaCreature> creatureSupplier,
                                    IntSupplier numberSupplier) {
        int currentNumber = 0;
        int totalNumber = numberSupplier.getAsInt();
        while (currentNumber < totalNumber) {
            WaTorCoordinate coordinate = randomCoordinate();
            if (territory.isEmpty(coordinate)) {
                WaTorSeaCreature creature = creatureSupplier.apply(coordinate);
                creatures.put(creature.sequenceId(), creature);
                territory.placeIdAt(creature.sequenceId(), coordinate);
                currentNumber++;
            }
        }
    }

    public Optional<WaTorSeaCreature> creatureAt(WaTorCoordinate coordinate) {
        return territory.findIdAt(coordinate).map(creatures::get);
    }

    public SimulationStatus startSimulation() {
        if (simulationStatus != SimulationStatus.NEW) {
            throw new IllegalStateException();
        }
        waTorModel.resetTimeCounter();
        System.out.println("Start simulation: " + waTorModel.timeCounter());

        fillContainerWithCreatures(this::newFish, waTorModel::fishNumber);
        fillContainerWithCreatures(this::newShark, waTorModel::sharkNumber);

        simulationStatus = SimulationStatus.STARTED;

        if ((currentFishNumber == 0) && (currentSharkNumber == 0)) {
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

    private void simulateCreature(WaTorSeaCreature creature) {
        WaTorCoordinate coordinate = creature.currentPlace();
        List<WaTorCoordinate> neighbors = coordinate.neighbors(waTorModel.xSize(), waTorModel.ySize());
        List<WaTorCoordinate> emptyNeighbors = neighbors.stream().filter(territory::isEmpty).toList();

        switch (creature) {
            case WaTorFish fish -> {
                // Move
                if (!emptyNeighbors.isEmpty()) {
                    WaTorCoordinate newCoordinate = selectRandomCoordinate(emptyNeighbors);
                    territory.moveId(fish.sequenceId(), coordinate, newCoordinate);
                    fish.moveTo(newCoordinate);
                    // Reproduce
                    if (fish.age(waTorModel.timeCounter()) >= 5) {
                        if (fish.numberOfReproductions() < 20) {
                            if (fish.timeOfLastReproduction().isEmpty() ||
                                    ((waTorModel.timeCounter() - fish.timeOfLastReproduction().getAsLong()) >= 3)) {
                                WaTorFish childFish = newFish(coordinate);
                                creatures.put(childFish.sequenceId(), childFish);
                                territory.placeIdAt(childFish.sequenceId(), coordinate);
                                fish.reproduce(childFish);
                            }
                        }
                    }
                }
                if (fish.age(waTorModel.timeCounter()) >= 20) {
                    // Fish dies
                    territory.removeIdAt(fish.currentPlace());
                    creatures.remove(fish.sequenceId());
                    currentFishNumber--;
                }
            }
            case WaTorShark shark -> {
                // Reduce energy
                shark.reduceEnergy();

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
                    shark.gainEnergy(fish);

                    //  System.out.println("creatures.remove: " + fish.sequenceId());
                    creatures.remove(fish.sequenceId());

                    currentFishNumber--;

                    newCoordinate = fish.currentPlace();
                } else if (!emptyNeighbors.isEmpty()) {
                    newCoordinate = selectRandomCoordinate(emptyNeighbors);
                }

                // Move shark to new coordinate if possible
                if (newCoordinate != null) {
                    territory.moveId(shark.sequenceId(), coordinate, newCoordinate);
                    shark.moveTo(newCoordinate);

                    // Reproduce shark if possible
                    if ((shark.age(waTorModel.timeCounter()) >= 15) && (shark.currentEnergy() > 5)) {
                        if (shark.numberOfReproductions() < 20) {
                            if (shark.timeOfLastReproduction().isEmpty() ||
                                    ((waTorModel.timeCounter() - shark.timeOfLastReproduction().getAsLong()) >= 3)) {
                                WaTorShark childShark = newShark(coordinate);
                                creatures.put(childShark.sequenceId(), childShark);
                                territory.placeIdAt(childShark.sequenceId(), coordinate);
                                shark.reproduce(childShark);
                            }
                        }
                    }
                }

                if ((shark.currentEnergy() <= 0) || (shark.age(waTorModel.timeCounter()) >= 50)) {
                    // Shark dies
                    territory.removeIdAt(shark.currentPlace());
                    creatures.remove(shark.sequenceId());
                    currentSharkNumber--;
                }

            }
        }
    }

    public SimulationStatus updateSimulation() {
        if (simulationStatus != SimulationStatus.STARTED) {
            throw new IllegalStateException();
        }
        waTorModel.incrementTimeCounter();
        // System.out.println("Update simulation: " + waTorModel.timeCounter());

        List<Long> currentCreatureIds = new ArrayList<>(creatures.keySet());
        currentCreatureIds.forEach(id -> {
            WaTorSeaCreature creature = creatures.get(id);
            if (creature != null) { // A fish can be eaten by a shark
                try {
                    simulateCreature(creature);
                } catch (RuntimeException e) {
                    System.err.println("Error simulating creature: " + creature + " : " + e.getMessage());
                    throw e;
                }
            }
        });

        if ((currentFishNumber <= 0) && (currentSharkNumber <= 0)) {
            simulationStatus = SimulationStatus.FINISHED;
        }

        return simulationStatus;
    }

}
