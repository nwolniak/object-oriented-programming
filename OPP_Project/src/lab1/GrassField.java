package lab1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GrassField extends AbstractWorldMap {
    private final Map<Vector2d, Grass> grasses = new HashMap<>();
    private final MapBoundary boundaries;

    public GrassField(int numberOfGrasses) {
        this.boundaries = new MapBoundary(this);
        Random random = new Random();
        for (int i = 0; i < numberOfGrasses; i++) {
            Vector2d position = new Vector2d(random.nextInt((int) Math.sqrt(numberOfGrasses * 10) + 1),
                    random.nextInt((int) Math.sqrt(numberOfGrasses * 10) + 1)); // +1 żeby dołączyć prawe ograniczenie
            if (!isOccupied(position)) {
                this.grasses.put(position, new Grass(position));
                this.boundaries.grassAdded(position);
            } else {
                i--;
            }
        }
    }
    @Override
    public boolean place(Animal animal) {
        if (canMoveTo(animal.getPosition())) {
            animal.addObserver(this);
            this.boundaries.animalAdded(animal.getPosition());
            animal.addObserver(this.boundaries);
            this.animals.put(animal.getPosition(), animal);
            return true;
        }
        else{
            throw new IllegalArgumentException(animal.getPosition().toString() + " is not legal position");
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return !(super.objectAt(position) instanceof Animal);
    }

    @Override
    public Object objectAt(Vector2d position) {
        if (super.objectAt(position) != null) {
            return super.objectAt(position);
        }
        return grasses.get(position);
    }

    @Override
    public Vector2d getLowLeft() {
        return this.boundaries.getLowerLeft();
    }

    @Override
    public Vector2d getUpperRight() {
        return this.boundaries.getUpperRight();
    }
}
