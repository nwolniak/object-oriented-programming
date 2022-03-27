package Classes;


import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Platform implements IMapElement {
    private final WorldMap map;
    private Vector2d position;
    private int width;
    private final int height;
    private MovementState movementState;

    private final Rectangle rectangle;

    private int speedMultiplier = 5;
    private double widthMultiplier = 1;
    private int speedBonus;
    private boolean isWidthMultiplier = false;
    private int dx;

    public Platform(WorldMap map, Vector2d position, int width, int height) {
        this.map = map;
        this.position = position;
        this.width = width;
        this.height = height;
        this.rectangle = new Rectangle(this.position.x, this.position.y, this.width, this.height);
        this.rectangle.setFill(Color.BLACK);
        this.movementState = MovementState.MOTIONLESS;
        this.dx = 0;
        this.speedBonus = 1;
    }


    public void move() {
        Vector2d newPosition = this.position.add(new Vector2d(this.dx, 0));
        if (this.map.canMoveTo(this, newPosition)) {
            this.position = newPosition;
        }
    }

    public int getSpeedMultiplier() {
        return this.speedMultiplier * this.speedBonus;
    }

    public void setSpeedBonus(int speedBonus) {
        this.speedBonus = speedBonus;
    }

    public void removeSpeedBonus() {
        this.speedBonus = 1;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setMovementState(MovementState movementState) {
        this.movementState = movementState;
    }

    public void setWidthMultiplier(double widthMultiplier) {
        if (!this.isWidthMultiplier) {
            this.widthMultiplier = widthMultiplier;
            this.width *= this.widthMultiplier;
            if (!this.map.canPlaceAt(this.position.add(new Vector2d(width, this.height)))) {
                this.position = this.position.subtract(new Vector2d(this.position.x + this.width - this.map.getLowerRight().x, 0));
            }
            this.rectangle.setWidth(this.width);
            this.isWidthMultiplier = true;
        }
    }

    public void removeWidthMiltiplier() {
        if (this.isWidthMultiplier) {
            this.width /= this.widthMultiplier;
            this.rectangle.setWidth(this.width);
            this.widthMultiplier = 1;
            this.isWidthMultiplier = false;
        }
    }

    public MovementState getMovementState() {
        return this.movementState;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }


    public Vector2d getUpperLeft() {
        return this.position;
    }

    public Vector2d getLowerRight() {
        return this.position.add(new Vector2d(this.width, this.height));
    }


    public void draw() {

        this.rectangle.setX(this.position.x);
        this.rectangle.setY(this.position.y);
    }
}
