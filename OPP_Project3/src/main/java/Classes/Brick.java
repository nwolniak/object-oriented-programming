package Classes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;


public class Brick implements IMapElement {
    private final Vector2d position;
    private final int width;
    private final int height;

    private final Rectangle rectangle;

    private final Vector2d upperLeft;
    private final Vector2d lowerRight;

    private int shield;

    private Bonus bonus;

    private final List<IShieldBrokenObserver> observers = new ArrayList<IShieldBrokenObserver>();

    public Brick(Vector2d position, int width, int height, int shield) {
        this.position = position;
        this.width = width;
        this.height = height;

        this.shield = shield;

        this.upperLeft = new Vector2d(this.position.x, this.position.y);
        this.lowerRight = new Vector2d(this.position.x + this.width, this.position.y + this.height);

        this.rectangle = new Rectangle(this.position.x, this.position.y, this.width, this.height);
        this.rectangle.setFill(Color.FIREBRICK);
    }

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
    }

    public Bonus getBonus() {
        return this.bonus;
    }

    public void hitted() {
        this.shield -= 1;
        if (this.shield == 0) {
            this.shieldBroken();
        }
    }

    public Vector2d getUpperLeft() {
        return this.upperLeft;
    }

    public Vector2d getLowerRight() {
        return this.lowerRight;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public void addObserver(IShieldBrokenObserver observer) {
        this.observers.add(observer);
    }

    private void shieldBroken() {
        for (IShieldBrokenObserver observer : this.observers) {
            observer.shieldBroken(this);
        }
    }

}
