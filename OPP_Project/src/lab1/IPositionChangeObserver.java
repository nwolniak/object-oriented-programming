package lab1;

public interface IPositionChangeObserver {
    // inform observer about position change
    void positionChanged(Vector2d oldPosition, Vector2d newPosition);
}
