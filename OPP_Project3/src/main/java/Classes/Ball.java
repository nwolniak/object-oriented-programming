package Classes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class Ball extends AbstractBallMovingElement {

    private final List<IPositionChangedObserver> positionObservers = new ArrayList<IPositionChangedObserver>();
    private MovementState movementState;
    private boolean isSpeedBonusActive = false;
    private int speedBonus = 1;


    private final Vector2d lowerRight;


    public Ball(WorldMap map, Vector2d position, int radius) {
        this.map = map;
        this.position = position;
        this.radius = radius;
        this.circle = new Circle(radius, Color.CYAN);
        this.dx = 0;
        this.dy = -1;
        this.movementState = MovementState.GOING_UP;

        this.lowerRight = new Vector2d(this.position.x + this.radius, this.position.y + this.radius);

    }

    @Override
    public void move() {
        Vector2d positionChange = new Vector2d(this.dx, this.dy);
        Vector2d newPosition = this.position.add(positionChange);
        if (this.map.isOnLeftBound(newPosition)) {
            this.changeDirection(true, false);
            if (this.getMovementState() == MovementState.GOING_LEFT_UP) {
                this.setMovementState(MovementState.GOING_RIGHT_UP);
            } else {
                this.setMovementState(MovementState.GOING_RIGHT_DOWN);
            }
        } else if (this.map.isOnRightBound(newPosition)) {
            this.changeDirection(true, false);
            if (this.getMovementState() == MovementState.GOING_RIGHT_UP) {
                this.setMovementState(MovementState.GOING_LEFT_UP);
            } else {
                this.setMovementState(MovementState.GOING_LEFT_DOWN);
            }
        } else if (this.map.isOnUpperBound(newPosition)) {
            this.changeDirection(false, true);
            if (this.getMovementState() == MovementState.GOING_LEFT_UP) {
                this.setMovementState(MovementState.GOING_LEFT_DOWN);
            } else if (this.getMovementState() == MovementState.GOING_RIGHT_UP) {
                this.setMovementState(MovementState.GOING_RIGHT_DOWN);
            } else {
                this.setMovementState(MovementState.GOING_DOWN);
            }
        } else if (this.map.isOnLowerBound(newPosition.add(new Vector2d(this.getWidth(), this.getHeight())))) {
            this.hitLowerBound();
        }

        this.position = this.position.add(new Vector2d(this.dx, this.dy));
    }

    public void setSpeedBonus(int speedMultiplier) {
        if (!this.isSpeedBonusActive) {
            this.speedBonus = speedMultiplier;
            this.speedMultiplier += speedBonus;
            this.isSpeedBonusActive = true;
        }
    }

    public void removeSpeedBonus() {
        if (this.isSpeedBonusActive) {
            this.speedMultiplier -= this.speedBonus;
        }
    }

    public void changeDirection(boolean dx, boolean dy) {
        if (dx) {
            this.dx *= -1;
        }
        if (dy) {
            this.dy *= -1;
        }
    }

    public void setMovementState(MovementState movementState) {
        this.movementState = movementState;
    }

    public MovementState getMovementState() {
        return this.movementState;
    }


    public Vector2d getLowerRight() {
        return this.lowerRight;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Ball)) return false;
        return (this.position.equals(((Ball) other).getPosition()));
    }
}
