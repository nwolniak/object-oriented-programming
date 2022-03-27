package lab1;

import java.util.ArrayList;
import java.util.List;

public class Animal extends AbstractWorldMapElement {
    private MapDirection orientation = MapDirection.NORTH;
    private final IWorldMap map;
    private final List<IPositionChangeObserver> observers = new ArrayList<>();


    public Animal(IWorldMap map) {
        this(map,new Vector2d(2,2));
    }

    public Animal(IWorldMap map, Vector2d initialPosition) {
        this.map = map;
        this.position = initialPosition;

    }
    @Override
    public String toString() {
        switch (this.orientation) {
            case NORTH:
                return "^";
            case EAST:
                return ">";
            case WEST:
                return "<";
            case SOUTH:
                return "v";
            default:
                return null;
        }
    }

    public void move(MoveDirection direction) {
        if (direction == MoveDirection.FORWARD) {
            Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
            if (this.map.canMoveTo(newPosition)) {
                Vector2d oldPosition = this.position;
                this.position = newPosition;
                this.positionChanged(oldPosition, this.position);
            }
        } else if (direction == MoveDirection.BACKWARD) {
            Vector2d newPosition = this.position.subtract(this.orientation.toUnitVector());
            if (this.map.canMoveTo(newPosition)) {
                Vector2d oldPosition = this.position;
                this.position = newPosition;
                this.positionChanged(oldPosition, this.position);
            }
        } else if (direction == MoveDirection.LEFT) {
            this.orientation = this.orientation.previous();
        } else if (direction == MoveDirection.RIGHT) {
            this.orientation = this.orientation.next();
        }
    }
    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }
    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }
    private void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for (IPositionChangeObserver observer : this.observers){
            observer.positionChanged(oldPosition,newPosition);
        }
    }

}


