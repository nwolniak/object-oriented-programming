package lab1;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine implements IEngine {
    private final MoveDirection[] directions;
    private final List<Animal> animals = new ArrayList<>();

    SimulationEngine(MoveDirection[] directions, IWorldMap map, Vector2d[] startingPositions) {
        this.directions = directions;
        for (Vector2d position : startingPositions) {
            Animal animal = new Animal(map,position);
            map.place(animal);
            animals.add(animal);
        }
    }

    @Override
    public void run() {

        for (int i = 0; i < this.directions.length; i++) {
            Animal animal = this.animals.get(i % this.animals.size());
            animal.move(this.directions[i]);
            this.animals.set(i % this.animals.size(), animal);
        }
    }
}