package lab1;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver {
    protected final Map<Vector2d, Animal> animals = new HashMap<>();
    private final MapVisualizer mapVisualized = new MapVisualizer(this);


    @Override
    public boolean place(Animal animal) {
        if (canMoveTo(animal.getPosition())) {
            animal.addObserver(this);
            this.animals.put(animal.getPosition(), animal);
            return true;
        }
        else{
            throw new IllegalArgumentException(animal.getPosition().toString() + " is not legal position");
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    @Override
    public Object objectAt(Vector2d position) {
        return animals.get(position);
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        Animal animal = this.animals.get(oldPosition);
        this.animals.remove(oldPosition);
        this.animals.put(newPosition, animal);

    }

    protected abstract Vector2d getLowLeft();
    protected abstract Vector2d getUpperRight();

    public String toString() {
        return mapVisualized.draw(this.getLowLeft(), this.getUpperRight());
    }
}
