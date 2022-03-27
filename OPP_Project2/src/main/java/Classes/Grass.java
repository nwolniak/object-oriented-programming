package Classes;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Grass extends Rectangle {
    private double energy;
    private final Vector2d position;
    private final Rectangle rectangle;


    public Grass(Pane world, Vector2d position, double grassEnergy) {
        this.energy = grassEnergy;
        this.position = position;
        this.rectangle = new Rectangle(this.position.x, this.position.y, 1, 1);
        this.rectangle.setFill(Color.GREENYELLOW);
        world.getChildren().add(rectangle);
    }

    public void wasEaten() {
        this.energy = 0;

    }

    public Vector2d getPosition() {
        return this.position;
    }


    public double getEnergy() {
        return this.energy;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }
}
