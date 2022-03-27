package Classes;


import Classes.IMapObserver;
import javafx.scene.layout.Pane;

import java.util.*;

public class Simulation implements IMapObserver {
    private final List<Animal> animals = new ArrayList<Animal>();


    private final Pane world;
    private final double plantEnergy;
    private final WorldMap map;
    public int sumDeadLife;
    public int deadAnimalsCounter;
    private Animal animalFollowing;
    private List<Animal> animalFollowingChildren;

    private int allAnimalsNumber;
    private int allGrassesNumber;
    private final int[] allDominantGenotypes;
    private double allAvgEnergy;
    private double allAvgLifeTime;
    private int allAnimalsChildrenNumber;

    public Simulation(Pane world, int numberOfAnimals, double startEnergy, double moveEnergy, double plantEnergy, double jungleRatio) {
        this.world = world;
        this.plantEnergy = plantEnergy;
        this.sumDeadLife = 0;
        this.deadAnimalsCounter = 0;
        this.map = new WorldMap(this, new Vector2d(0, 0), new Vector2d((int) this.world.getWidth(), (int) this.world.getHeight()),jungleRatio, startEnergy, moveEnergy);
        this.map.addObserver(this);

        try {
            this.map.spawnFirstAnimals(numberOfAnimals);
        } catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }

        //STATISTICS
        this.allAnimalsNumber = 0;
        this.allGrassesNumber = 0;
        this.allDominantGenotypes = new int[8];
        this.allAvgEnergy = 0;
        this.allAvgLifeTime = 0;
        this.allAnimalsChildrenNumber = 0;

        this.draw();
    }

    public void removeDeadAnimals() {
        this.map.removeDeadAnimals();
    }

    public void move() {
        for (Animal animal : this.animals) {
            animal.move();
        }
    }

    public void checkCollisions() {
        this.map.checkCollisions();
    }

    @Override
    public void feedAnimals(List<Animal> strongestAnimals, Grass grass) {
        double partOfGrassEnergy = grass.getEnergy() / strongestAnimals.size();
        for (Animal animal : strongestAnimals) {
            animal.eat(partOfGrassEnergy);
        }
        grass.wasEaten();
        this.world.getChildren().remove(grass.getRectangle());
    }

    public void plant() {
        this.map.plant(this.world, this.plantEnergy);
    }

    public void draw() {
        for (Animal animal : this.animals) {
            animal.draw();
        }
    }

    public void uploadFileStatistics() {
        this.allAnimalsNumber += this.getAnimalsSize();
        this.allGrassesNumber += this.getPlantsSize();
        this.allAvgEnergy += this.getAvgEnergy();
        this.allAvgLifeTime += this.getAvgAnimalsLife();
        this.allAnimalsChildrenNumber += this.getAvgChildren();
        int[] dominantGenotypes = this.getDomimantGenes();
        for (int i = 0; i < 8; i++) {
            this.allDominantGenotypes[i] += dominantGenotypes[i];
        }
    }


    //STATISTICS

    public int getAnimalsSize() {
        return this.animals.size();
    }

    public int getPlantsSize() {
        return this.map.getPlantsSize();
    }

    public double getAvgAnimalsLife() {
        if (this.sumDeadLife == 0 || this.deadAnimalsCounter == 0) {
            return 0;
        }
        return (double) this.sumDeadLife / this.deadAnimalsCounter;
    }

    public int[] getDomimantGenes() {
        int[] genesArray = new int[8];
        for (int i = 0; i < 8; i++) {
            genesArray[i] = 0;
        }
        for (Animal animal : this.animals) {
            int[] animalGenes = animal.getDominantGenes();
            for (int i = 0; i < 8; i++) {
                genesArray[i] += animalGenes[i];
            }
        }
        return genesArray;
    }

    public double getAvgEnergy() {
        double sumEnergy = 0;
        if (this.animals.size() == 0) {
            return 0;
        }
        for (Animal animal : this.animals) {
            sumEnergy += animal.getEnergy();
        }
        return sumEnergy / this.animals.size();
    }

    public int getAvgChildren() {
        TreeSet<Animal> uniqueChildren = new TreeSet<Animal>();
        if (this.animals.size() == 0) {
            return 0;
        }
        for (Animal animal : this.animals) {
            uniqueChildren.addAll(animal.getChildren());
        }
        return uniqueChildren.size();
    }

    public Vector2d animalClickedPosition(Vector2d mousePosition) {
        return this.map.getAnimalAtPosition(mousePosition).getPosition();
    }

    public String animalClickedGenotypesToString(Vector2d mousePosition) {
        return this.map.getAnimalAtPosition(mousePosition).getGenotypes().toString();
    }


    public void addAnimalToFollow(Vector2d mousePosition) {
        this.animalFollowing = this.map.getAnimalAtPosition(mousePosition);
        this.animalFollowingChildren = new ArrayList<Animal>();
    }

    public boolean isAnimalToFollowChosen() {
        if (this.animalFollowing != null) {
            return true;
        } else {
            throw new NullPointerException("Animal has not been yet chosen");
        }

    }

    public Animal getAnimalFollowing() {
        return this.animalFollowing;
    }

    public List<Animal> getAnimalFollowingChildren() {
        return this.animalFollowingChildren;
    }

    public int[] getAnimalFollowingStatistics() {
        int[] statisticsArray = new int[3];
        statisticsArray[0] = this.getAnimalFollowingChildren().size();
        TreeSet<Animal> uniqueDescendants = new TreeSet<Animal>();
        uniqueDescendants.addAll(this.getDescendats(uniqueDescendants, this.animalFollowingChildren));
        statisticsArray[1] = uniqueDescendants.size();
        if (this.animalFollowing.getEnergy() == 0) {
            statisticsArray[2] = this.animalFollowing.getAge();
        } else {
            statisticsArray[2] = -1;
        }
        return statisticsArray;
    }

    private TreeSet<Animal> getDescendats(TreeSet<Animal> uniqueDescendants, List<Animal> animalChildren) {
        if (animalChildren.size() == 0) {
            return new TreeSet<>();
        }
        uniqueDescendants.addAll(animalChildren);
        for (Animal animal : animalChildren) {
            uniqueDescendants.addAll(getDescendats(uniqueDescendants, animal.getChildren()));
        }
        return uniqueDescendants;
    }

    public void showAnimalsWithDominantGenes() {
        int[] dominantGenes = this.getDomimantGenes();
        int dominantGene = 0;
        int dominantGenotype = dominantGenes[0];
        for (int i = 1; i < 8; i++) {
            if (dominantGenes[i] > dominantGenotype) {
                dominantGenotype = dominantGenes[i];
                dominantGene = i;
            }
        }
        List<Animal> animalsWithDominantGenes = new ArrayList<>();
        for (Animal animal : this.animals) {
            if (animal.getDominantGene() == dominantGene) {
                animalsWithDominantGenes.add(animal);
            }
        }
        for (Animal animal : animalsWithDominantGenes) {
            animal.changeColorToShowDominance();
        }
        this.draw();
    }

    public int getAllAnimalsNumber() {
        return this.allAnimalsNumber;
    }

    public int getAllGrassesNumber() {
        return this.allGrassesNumber;
    }

    public int getAllAnimalsChildrenNumber() {
        return this.allAnimalsChildrenNumber;
    }

    public double getAllAvgEnergy() {
        return this.allAvgEnergy;
    }

    public double getAllAvgLifeTime() {
        return this.allAvgLifeTime;
    }

    public int getAllDominantGenotype() {
        int dominantGene = 0;
        int dominantGeneNumber = this.allDominantGenotypes[0];
        for (int i = 1; i < 7; i++) {
            if (this.allDominantGenotypes[i] > dominantGeneNumber) {
                dominantGene = i;
                dominantGeneNumber = this.allDominantGenotypes[i];
            }
        }
        return dominantGene;
    }

    @Override
    public void animalAdded(Animal animal) {
        this.world.getChildren().add(animal.getCircle());
        this.animals.add(animal);
    }

    @Override
    public void animalRemoved(Animal animal) {
        this.world.getChildren().remove(animal.getCircle());
        this.animals.remove(animal);
        this.deadAnimalsCounter++;
        this.sumDeadLife += animal.getAge();
    }
}
