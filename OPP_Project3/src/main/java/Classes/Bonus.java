package Classes;

import javafx.scene.shape.Circle;

public class Bonus extends AbstractBallMovingElement {

    private final BonusType bonusType;

    public Bonus(WorldMap map, Vector2d position, int radius, BonusType bonusType) {
        this.map = map;
        this.position = position;
        this.radius = radius;
        this.dx = 0;
        this.dy = 1;
        this.bonusType = bonusType;
        this.circle = new Circle(this.position.x, this.position.y, this.radius);
        this.circle.setFill(this.bonusType.getColor());
    }

    public double getBonusValue() {
        return this.bonusType.getBonusValue();
    }

    public BonusType getBonusType() {
        return this.bonusType;
    }


}
