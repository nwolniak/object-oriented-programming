package lab1;

public class RectangularMap extends AbstractWorldMap {
    private final Vector2d lowLeft;
    private final Vector2d upperRight;

    RectangularMap(int width, int height) {
        this.lowLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height-1 ); // ponieważ lowLeft = (0,0)
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.precedes(upperRight) && position.follows(lowLeft) && !super.isOccupied(position);
    }

    @Override
    public Vector2d getLowLeft() {
        return this.lowLeft;
    }

    @Override
    public Vector2d getUpperRight() {
        return this.upperRight;
    }
}
