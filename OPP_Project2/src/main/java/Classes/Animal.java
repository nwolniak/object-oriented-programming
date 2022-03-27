package Classes;

import Classes.IPositionChanged;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class Animal implements Comparable<Animal> {
    private EnergyState state;
    private Vector2d position;
    private MapDirection direction;
    private final Circle circle;
    private final WorldMap map;
    private final double startEnergy;
    private double energy;
    private final double moveEnergy;
    private final Genotypes genotypes;
    private int age;
    private final List<IPositionChanged> observers = new ArrayList<IPositionChanged>();
    private final List<Animal> children = new ArrayList<Animal>();

    public Animal(WorldMap map, Vector2d position, double startEnergy, double moveEnergy) {
        this(map, position, startEnergy, moveEnergy, new Genotypes());
    }

    public Animal(WorldMap map, Vector2d position, double startEnergy, double moveEnergy, Genotypes genotypes) {
        this.map = map;
        this.age = 0;
        this.state = EnergyState.HEALTHY;
        this.startEnergy = startEnergy;
        this.energy = startEnergy;
        this.position = position;
        this.genotypes = genotypes;
        this.moveEnergy = moveEnergy;
        this.direction = MapDirection.values()[new Random().nextInt(MapDirection.values().length)];
        this.circle = new Circle(0.5, this.state.getColor());

    }


    public void move() {
        this.age++;
        if (this.energy > this.moveEnergy) {
            int rotateNumber = this.genotypes.randomGene();
            for (int i = 0; i < rotateNumber; i++) {
                try {
                    this.direction = this.direction.next();
                } catch (IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }

            }

            Vector2d newPosition = this.position.add(this.direction.toUnitVector());
            if (this.map.stepOutOfBoundaries(newPosition)) {
                int mapWidth = this.map.getSavannaUpperRight().x;
                int mapHeight = this.map.getSavannaUpperRight().y;
                if (newPosition.x < 0 && newPosition.y >= mapHeight) {
                    newPosition = new Vector2d(mapWidth - 1, 0);
                } else if (newPosition.x < 0 && newPosition.y < 0) {
                    newPosition = new Vector2d(mapWidth - 1, mapHeight - 1);
                } else if (newPosition.x < 0) {
                    newPosition = new Vector2d(mapWidth - 1, newPosition.y);
                } else if (newPosition.y < 0) {
                    newPosition = new Vector2d(newPosition.x, mapHeight - 1);
                } else {
                    newPosition = new Vector2d(newPosition.x % mapWidth, newPosition.y % mapHeight);
                }
            }
            this.animalAtOldPosition(this);
            this.position = newPosition;
            this.energy -= this.moveEnergy;
            this.animalAtNewPosition(this);
            this.checkEnergy();
        } else {
            this.energy = 0;
            this.changeState(EnergyState.DEAD);
            this.passedToAnimalHeaven();
        }

    }

    private void checkEnergy() {
        if (this.energy >= this.startEnergy / 2) {
            this.changeState(EnergyState.HEALTHY);
        } else if (this.energy >= this.startEnergy / 4) {
            this.changeState(EnergyState.MEDIUM);
        } else if (this.energy > this.moveEnergy) {
            this.changeState(EnergyState.WEAK);
        }
    }

    private void changeState(EnergyState newState) {
        this.state = newState;
        this.circle.setFill(this.state.getColor());
    }

    public void eat(double partOfGrassEnergy) {
        this.energy += partOfGrassEnergy;
        this.checkEnergy();
    }

    public boolean canBornBabyWith(Animal other) {
        return this.state == EnergyState.HEALTHY && other.state == EnergyState.HEALTHY;
    }

    public Animal bornBaby(Animal other, Vector2d position) {
        double babyEnergy = this.energy / 4 + other.getEnergy() / 4;
        this.energy -= this.energy / 4;
        other.setEnergy(other.getEnergy() - other.getEnergy() / 4);
        Genotypes mixedParentsGenotypes = new Genotypes(this.getGenotypes().connectGenes(other.getGenotypes()));

        return new Animal(this.map, position, babyEnergy, this.moveEnergy, mixedParentsGenotypes);
    }


    public void addObserver(IPositionChanged observer) {
        this.observers.add(observer);
    }

    private void animalAtOldPosition(Animal atOldPosition) {
        for (IPositionChanged observer : this.observers) {
            observer.animalAtOldPosition(atOldPosition);
        }
    }

    private void animalAtNewPosition(Animal atNewPosition) {
        for (IPositionChanged observer : this.observers) {
            observer.animalAtNewPosition(atNewPosition);
        }
    }


    private void passedToAnimalHeaven() {
        for (IPositionChanged observer : this.observers) {
            observer.passedToAnimalHeaven(this);
        }
    }


    public int[] getDominantGenes() {
        int[] genesArray = new int[8];
        for (int i = 0; i < 8; i++) {
            genesArray[i] = 0;
        }
        List<Integer> genes = this.genotypes.getGenotypes();
        for (int i = 0; i < 32; i++) {
            genesArray[genes.get(i)]++;
        }
        return genesArray;
    }

    public int getDominantGene() {
        return this.genotypes.getDominantGene();
    }

    public void addChildren(Animal child) {
        this.children.add(child);
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void changeColorToShowDominance() {
        this.circle.setFill(Color.BLUE);
    }

    public Vector2d getPosition() {
        return this.position;
    }

    public double getEnergy() {
        return this.energy;
    }

    public Genotypes getGenotypes() {
        return this.genotypes;
    }

    public Circle getCircle() {
        return this.circle;
    }

    public List<Animal> getChildren() {
        return this.children;
    }

    public int getAge() {
        return this.age;
    }

    public void draw() {
        this.circle.setRadius(0.5);
        this.circle.setTranslateX(this.position.x + this.circle.getRadius());
        this.circle.setTranslateY(this.position.y + this.circle.getRadius());
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }


    @Override
    public int compareTo(Animal o) {
        return Double.compare(this.energy, o.energy);
    }

}
