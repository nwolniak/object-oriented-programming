package Classes;

import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractBallMovingElement implements IMapElement {
    protected WorldMap map;
    protected Vector2d position;
    protected int radius;
    protected int dx;
    protected int dy;
    protected Circle circle;

    protected int speedMultiplier;

    protected final List<IBallHitLowerBoundObserver> lowerBoundObservers = new ArrayList<IBallHitLowerBoundObserver>();

    public Vector2d getLowerRight() {
        return this.position.add(new Vector2d(this.radius, this.radius));
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public int getWidth() {
        return this.radius;
    }

    @Override
    public int getHeight() {
        return this.radius;
    }

    protected Circle getCircle() {
        return this.circle;
    }


    public void setDx(int dx) {
        this.dx = dx;
    }


    protected void hitLowerBound() {
        for (IBallHitLowerBoundObserver observer : this.lowerBoundObservers) {
            observer.hitLowerBound(this);
        }
    }

    protected void draw() {
        this.circle.setCenterX(this.position.x);
        this.circle.setCenterY(this.position.y);
    }

    public void addLowerBoundObserver(IBallHitLowerBoundObserver observer) {
        this.lowerBoundObservers.add(observer);
    }

    public void move() {
        Vector2d newPosition = this.position.add(new Vector2d(this.dx, this.dy));
        if (this.map.canMoveTo(this, newPosition)) {
            this.position = newPosition;
        } else if (this.map.isOnLowerBound(newPosition.add(new Vector2d(this.radius, this.radius)))) {
            this.hitLowerBound();
        }
    }

    public int getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public void setSpeedMultiplier(int speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

}
