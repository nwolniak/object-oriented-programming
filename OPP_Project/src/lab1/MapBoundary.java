package lab1;

import java.util.TreeSet;

public class MapBoundary implements IPositionChangeObserver {
    private final TreeSet<IMapElement> xSortedSet = new TreeSet<IMapElement>(new xMapBoundaryComparator());
    private final TreeSet<IMapElement> ySortedSet = new TreeSet<IMapElement>(new yMapBoundaryComparator());
    private final IWorldMap map;

    public MapBoundary(IWorldMap map) {
        this.map = map;
    }

    public void grassAdded(Vector2d position) {
        Grass grass = new Grass(position);
        this.xSortedSet.add(grass);
        this.ySortedSet.add(grass);
    }

    public void animalAdded(Vector2d position) {
        Animal animal = new Animal(this.map, position);
        this.xSortedSet.add(animal);
        this.ySortedSet.add(animal);
    }

    @Override
    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {

        Animal animalToBeDeleted = new Animal(this.map, oldPosition);
        Animal animalToBeAdded = new Animal(this.map, newPosition);

        this.xSortedSet.remove(animalToBeDeleted);
        this.ySortedSet.remove(animalToBeDeleted);

        this.xSortedSet.add(animalToBeAdded);
        this.ySortedSet.add(animalToBeAdded);

    }

    public Vector2d getLowerLeft() {
        return new Vector2d(this.xSortedSet.first().getPosition().x, this.ySortedSet.first().getPosition().y);
    }

    public Vector2d getUpperRight() {
        return new Vector2d(this.xSortedSet.last().getPosition().x, this.ySortedSet.last().getPosition().y);
    }

}
