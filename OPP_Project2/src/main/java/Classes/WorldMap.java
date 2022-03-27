package Classes;

import Classes.IMapObserver;
import Classes.IPositionChanged;
import javafx.scene.layout.Pane;

import java.util.*;

public class WorldMap implements IPositionChanged {
    //STRUCTURES
    private final List<Vector2d> savannaFreePositions = new ArrayList<Vector2d>();
    private final List<Vector2d> jungleFreePositions = new ArrayList<Vector2d>();

    private final Map<Vector2d, Queue<Animal>> animals = new HashMap<>();
    private final Map<Vector2d, Grass> savanna = new HashMap<>();
    private final Map<Vector2d, Grass> jungle = new HashMap<>();
    private final Stack<Animal> deadAnimals = new Stack<Animal>();
    private final Stack<Vector2d> savannaEatenGrassPos = new Stack<Vector2d>();
    private final Stack<Vector2d> jungleEatenGrassPos = new Stack<Vector2d>();

    private final List<IMapObserver> observers = new ArrayList<IMapObserver>();

    //VECTORS
    private final Vector2d savannaLowerLeft;
    private final Vector2d savannaUpperRight;
    private final Vector2d jungleLowerLeft;
    private final Vector2d jungleUpperRight;
    private final Simulation simulation;

    //CONST
    private final double startEnergy;
    private final double moveEnergy;

    public WorldMap(Simulation simulation, Vector2d savannaLowerLeft, Vector2d savannaUpperRight,double jungleRatio, double startEnergy, double moveEnergy) {

        //VECTORS
        this.savannaLowerLeft = savannaLowerLeft;
        this.savannaUpperRight = savannaUpperRight;
        this.jungleLowerLeft = new Vector2d((int) (this.savannaUpperRight.x / 2 - jungleRatio * this.savannaUpperRight.x / 2),
                (int) (this.savannaUpperRight.y / 2 - jungleRatio * this.savannaUpperRight.y / 2));
        this.jungleUpperRight = new Vector2d((int) (this.savannaUpperRight.x / 2 + jungleRatio * this.savannaUpperRight.x / 2),
                (int) (this.savannaUpperRight.y / 2 + jungleRatio * this.savannaUpperRight.y / 2));

        //CONST
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.simulation = simulation;
        this.prepareFreePositions();
    }

    public void spawnFirstAnimals(int numberOfAnimals) {
        Random random = new Random();
        for (int i = 0; i < numberOfAnimals; i++) {
            if (!this.savannaFreePositions.isEmpty() && !this.jungleFreePositions.isEmpty()) {

                int randomAnimalIndex = random.nextInt(this.savannaFreePositions.size() + this.jungleFreePositions.size());

                if (randomAnimalIndex < this.savannaFreePositions.size()) {
                    Vector2d position = this.savannaFreePositions.get(randomAnimalIndex);
                    Animal animal = new Animal(this, position, this.startEnergy, this.moveEnergy);
                    animal.addObserver(this);
                    this.placeAnimal(animal);
                } else {
                    Vector2d position = this.jungleFreePositions.get(randomAnimalIndex % this.jungleFreePositions.size());
                    Animal animal = new Animal(this, position, this.startEnergy, this.moveEnergy);
                    animal.addObserver(this);
                    this.placeAnimal(animal);
                }
            } else {
                throw new IllegalArgumentException("Mapa jest za mała dla takiej ilosci zwierzat : " + numberOfAnimals);
            }
        }


    }

    private void placeAnimal(Animal animal) {
        if (this.animals.containsKey(animal.getPosition())) {
            this.animals.get(animal.getPosition()).add(animal);
        } else {
            this.animals.put(animal.getPosition(), new PriorityQueue<Animal>());
            this.animals.get(animal.getPosition()).add(animal);
            this.removeFreePosition(animal.getPosition());
        }
        this.animalAdded(animal);
    }

    private void prepareFreePositions() {
        for (int i = 0; i < this.savannaUpperRight.x; i++) {
            for (int j = 0; j < this.savannaUpperRight.y; j++) {
                this.makeFreePosition(new Vector2d(i, j));
            }
        }
    }

    public void removeDeadAnimals() {
        while (!deadAnimals.isEmpty()) {
            Animal deadAnimal = this.deadAnimals.pop();
            this.animals.get(deadAnimal.getPosition()).remove(deadAnimal);
            if (this.animals.get(deadAnimal.getPosition()).isEmpty()) {
                this.animals.remove(deadAnimal.getPosition());
                this.makeFreePosition(deadAnimal.getPosition());
            }
            this.animalRemoved(deadAnimal);
        }
    }


    public void checkCollisions() {
        // EAT
        for (Grass grass : savanna.values()) {
            if (this.animals.containsKey(grass.getPosition())) {
                Queue<Animal> pq = this.animals.get(grass.getPosition());
                List<Animal> strongestAnimals = new ArrayList<Animal>();
                Animal strongestAnimal = pq.poll();
                strongestAnimals.add(strongestAnimal);
                while (!pq.isEmpty() && strongestAnimal.getEnergy() == pq.peek().getEnergy()) {
                    strongestAnimals.add(pq.poll());
                }
                this.feedAnimals(strongestAnimals, grass);
                pq.addAll(strongestAnimals);
                this.savannaEatenGrassPos.add(grass.getPosition());
            }
        }

        for (Grass grass : jungle.values()) {
            if (this.animals.containsKey(grass.getPosition())) {
                Queue<Animal> pq = this.animals.get(grass.getPosition());
                List<Animal> strongestAnimals = new ArrayList<Animal>();
                Animal strongestAnimal = pq.poll();
                strongestAnimals.add(strongestAnimal);
                while (!pq.isEmpty() && strongestAnimal.getEnergy() == pq.peek().getEnergy()) {
                    strongestAnimals.add(pq.poll());
                }
                this.feedAnimals(strongestAnimals, grass);
                pq.addAll(strongestAnimals);
                this.jungleEatenGrassPos.add(grass.getPosition());
            }
        }
        for (Vector2d eatenGrassPosition : savannaEatenGrassPos) {
            this.savanna.remove(eatenGrassPosition);
        }
        for (Vector2d eatenGrassPosition : jungleEatenGrassPos) {
            this.jungle.remove(eatenGrassPosition);
        }

        // BORN BABIES
        List<Animal> children = new ArrayList<Animal>();
        for (Map.Entry<Vector2d, Queue<Animal>> mapIterator : this.animals.entrySet()) {
            Queue<Animal> pq = mapIterator.getValue();
            if (pq.size() >= 2) {
                Animal animal = pq.poll();
                Animal other = pq.poll();
                if (animal.canBornBabyWith(other)) {
                    Animal newBaby = animal.bornBaby(other, this.randomPositionToBornBaby(other.getPosition()));
                    newBaby.addObserver(this);
                    children.add(newBaby);
                    if (this.simulation.getAnimalFollowing() != null) {
                        if (animal.equals(this.simulation.getAnimalFollowing())) {
                            this.simulation.getAnimalFollowingChildren().add(newBaby);
                        } else if (other.equals(this.simulation.getAnimalFollowing())) {
                            this.simulation.getAnimalFollowingChildren().add(newBaby);
                        }
                    }
                    animal.addChildren(newBaby);
                    other.addChildren(newBaby);

                }
                pq.add(other);
                pq.add(animal);
            }
        }
        for (Animal newBaby : children) {
            this.placeAnimal(newBaby);
        }
    }


    public void plant(Pane world, double plantEnergy) {
        Random random = new Random();

        //PLANT JUNGLE
        int jungleSize = this.jungleFreePositions.size();
        if (jungleSize > 0) {
            int randomJungleIndex = random.nextInt(this.jungleFreePositions.size());
            Vector2d jungleGrassPosition = this.jungleFreePositions.get(randomJungleIndex);
            this.jungle.put(jungleGrassPosition, new Grass(world, jungleGrassPosition, plantEnergy));
            this.removeFreePosition(jungleGrassPosition);
        }

        //PLANT SAVANNA
        int savannaSize = this.savannaFreePositions.size();
        if (savannaSize > 0) {
            int randomSavannaIndex = random.nextInt(this.savannaFreePositions.size());
            Vector2d savannaGrassPosition = this.savannaFreePositions.get(randomSavannaIndex);
            this.savanna.put(savannaGrassPosition, new Grass(world, savannaGrassPosition, plantEnergy));
            this.removeFreePosition(savannaGrassPosition);
        }

    }


    public boolean stepOutOfBoundaries(Vector2d newPosition) {
        return !(newPosition.precedes(this.savannaUpperRight) && newPosition.follows(this.savannaLowerLeft));
    }

    private void removeFreePosition(Vector2d occupatiedPosition) {
        if (!(occupatiedPosition.precedes(this.jungleUpperRight) && occupatiedPosition.follows(this.jungleLowerLeft))) {
            this.jungleFreePositions.remove(occupatiedPosition);
        } else {
            this.savannaFreePositions.remove(occupatiedPosition);
        }
    }

    private void makeFreePosition(Vector2d freePosition) {
        if (!(freePosition.precedes(this.jungleUpperRight) && freePosition.follows(this.jungleLowerLeft))) {
            this.jungleFreePositions.add(freePosition);
        } else {
            this.savannaFreePositions.add(freePosition);
        }
    }

    private Vector2d randomPositionToBornBaby(Vector2d parentPosition) {
        List<Vector2d> randomFreePositions = new ArrayList<Vector2d>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 && j != 0) {
                    Vector2d potentialFreePosition = new Vector2d(parentPosition.x + i, parentPosition.y + j);
                    if (potentialFreePosition.follows(this.jungleLowerLeft) && potentialFreePosition.precedes(this.jungleUpperRight)) {
                        if (!this.jungleFreePositions.contains(potentialFreePosition)) {
                            randomFreePositions.add(potentialFreePosition);
                        }
                    } else {
                        if (!this.savannaFreePositions.contains(potentialFreePosition)) {
                            randomFreePositions.add(potentialFreePosition);
                        }
                    }
                }
            }
        }
        Random random = new Random();
        if (!randomFreePositions.isEmpty()) {
            int randomIndex = random.nextInt(randomFreePositions.size());
            return randomFreePositions.get(randomIndex);
        } else {
            return new Vector2d(parentPosition.x + random.nextInt(2) - 1, parentPosition.y + random.nextInt(2) - 1);
        }
    }

    //GET
    public Vector2d getSavannaUpperRight() {
        return this.savannaUpperRight;
    }

    public Vector2d getSavannaLowerLeft() {
        return this.savannaLowerLeft;
    }

    public int getPlantsSize() {
        return this.savanna.size() + this.jungle.size();
    }

    public Animal getAnimalAtPosition(Vector2d mousePosition) {
        if (this.animals.containsKey(mousePosition)) {
            return this.animals.get(mousePosition).peek();
        } else {
            throw new IllegalArgumentException("This position " + mousePosition.toString() + " does not contain any animal !");
        }
    }

    //INFORM SIMULATION
    public void addObserver(IMapObserver observer) {
        this.observers.add(observer);
    }

    private void animalAdded(Animal animal) {
        for (IMapObserver observer : observers) {
            observer.animalAdded(animal);
        }
    }

    private void animalRemoved(Animal animal) {
        for (IMapObserver observer : observers) {
            observer.animalRemoved(animal);
        }
    }


    private void feedAnimals(List<Animal> strongestAnimals, Grass grass) {
        for (IMapObserver observer : observers) {
            observer.feedAnimals(strongestAnimals, grass);
        }
    }

    @Override
    public void passedToAnimalHeaven(Animal deadAnimal) {
        this.deadAnimals.add(deadAnimal);

    }

    @Override
    public void animalAtOldPosition(Animal atOldPosition) {
        this.animals.get(atOldPosition.getPosition()).remove(atOldPosition);
        if (this.animals.get(atOldPosition.getPosition()).isEmpty()) {
            this.animals.remove(atOldPosition.getPosition());
            this.makeFreePosition(atOldPosition.getPosition());
        }
    }

    @Override
    public void animalAtNewPosition(Animal atNewPosition) {
        if (!this.animals.containsKey(atNewPosition.getPosition())) {
            this.animals.put(atNewPosition.getPosition(), new PriorityQueue<Animal>());
        }
        this.animals.get(atNewPosition.getPosition()).add(atNewPosition);
    }
}
